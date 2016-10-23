package com.stehno.vanilla.config

import com.stehno.vanilla.mapper.ObjectMapper
import com.stehno.vanilla.util.TimeSpan
import groovy.transform.TypeChecked

import java.nio.file.Path
import java.nio.file.Paths

import static groovy.transform.TypeCheckingMode.SKIP

/**
 * A ConfigurationSource based on a Properties file.
 */
@TypeChecked
class PropertiesConfigurationSource implements ConfigurationSource {

    private final Properties source

    PropertiesConfigurationSource(final Properties props) {
        source = props
    }

    PropertiesConfigurationSource(final InputStream stream) {
        Properties props = new Properties()
        stream.withStream { s ->
            props.load(s)
        }
        source = props
    }

    PropertiesConfigurationSource(final Path path) {
        this(path.newInputStream())
    }

    @Override
    String getString(final String key, String defaultValue = null) {
        source.getProperty(key, defaultValue)
    }

    @Override
    Integer getInt(String key, Integer defaultValue = null) {
        getConfig key, defaultValue, { String s -> Integer.parseInt(s) }
    }

    @Override
    Long getLong(String key, Long defaultValue = null) {
        getConfig key, defaultValue, { String s -> Long.parseLong(s) }
    }

    @Override
    Float getFloat(String key, Float defaultValue = null) {
        getConfig key, defaultValue, { String s -> Float.parseFloat(s) }
    }

    @Override
    Double getDouble(String key, Double defaultValue = null) {
        getConfig key, defaultValue, { String s -> Double.parseDouble(s) }
    }

    @Override
    Path getPath(String key, Path defaultValue = null) {
        getConfig key, defaultValue, { String s -> Paths.get(s) }
    }

    @Override
    File getFile(String key, File defaultValue = null) {
        getConfig key, defaultValue, { String s -> new File(s) }
    }

    @Override
    TimeSpan getTimeSpan(String key, TimeSpan defaultValue = null) {
        getConfig key, defaultValue, { String s -> TimeSpan.parse(s) }
    }

    @Override
    Boolean getBoolean(String key, Boolean defaultValue = null) {
        getConfig key, defaultValue, { String s -> Boolean.valueOf(s) }
    }

    @Override
    ConfigurationSource getConfiguration(String key) {
        new MapConfigurationSource(getMap(key, [:]))
    }

    @Override @TypeChecked(SKIP)
    Map<String, Object> getMap(String key, Map<String, Object> defaultValue = null) {
        String prefix = "${key}."

        Map<String, Object> map = [:]

        source.keys().findAll { String k -> k.startsWith(prefix) }.each { String k ->
            map[k - prefix] = source.getProperty(k)
        }

        (map ?: defaultValue).asImmutable()
    }

    @Override
    public <T> T getObject(String key, Class<? extends T> type, ObjectMapper mapper, T defaultValue = null) {
        Map<String, Object> map = getMap(key)
        map ? (mapper.create(map, type) as T) : defaultValue
    }

    private <T> T getConfig(final String key, final T defaultValue, final Closure<T> factory) {
        String valueString = source.getProperty(key)
        !valueString ? defaultValue : factory(valueString)
    }
}
