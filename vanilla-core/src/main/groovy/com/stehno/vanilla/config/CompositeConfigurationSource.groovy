package com.stehno.vanilla.config

import com.stehno.vanilla.mapper.ObjectMapper
import com.stehno.vanilla.util.TimeSpan
import groovy.transform.TypeChecked

import java.nio.file.Path

import static groovy.transform.TypeCheckingMode.SKIP

/**
 * Created by cjstehno on 10/22/16.
 */
@TypeChecked
class CompositeConfigurationSource implements ConfigurationSource {

    private final List<ConfigurationSource> sources

    CompositeConfigurationSource(final List<ConfigurationSource> sources = []) {
        this.sources = sources
    }

    void addSource(final ConfigurationSource source) {
        sources << source
    }

    void leftShift(final ConfigurationSource source) {
        addSource source
    }

    @Override
    String getString(String key, String defaultValue = null) {
        findValue { ConfigurationSource src -> src.getString(key, defaultValue) }
    }

    @Override
    Integer getInt(String key, Integer defaultValue = null) {
        findValue { ConfigurationSource src -> src.getInt(key, defaultValue) }
    }

    @Override
    Long getLong(String key, Long defaultValue = null) {
        findValue { ConfigurationSource src -> src.getLong(key, defaultValue) }
    }

    @Override
    Float getFloat(String key, Float defaultValue = null) {
        findValue { ConfigurationSource src -> src.getFloat(key, defaultValue) }
    }

    @Override
    Double getDouble(String key, Double defaultValue = null) {
        findValue { ConfigurationSource src -> src.getDouble(key, defaultValue) }
    }

    @Override
    Path getPath(String key, Path defaultValue = null) {
        findValue { ConfigurationSource src -> src.getPath(key, defaultValue) }
    }

    @Override
    File getFile(String key, File defaultValue = null) {
        findValue { ConfigurationSource src -> src.getFile(key, defaultValue) }
    }

    @Override
    TimeSpan getTimeSpan(String key, TimeSpan defaultValue = null) {
        findValue { ConfigurationSource src -> src.getTimeSpan(key, defaultValue) }
    }

    @Override
    Boolean getBoolean(String key, Boolean defaultValue = null) {
        findValue { ConfigurationSource src -> src.getBoolean(key, defaultValue) }
    }

    @Override
    ConfigurationSource getConfiguration(String key) {
        findValue { ConfigurationSource src -> src.getConfiguration(key) }
    }

    @Override
    Map<String, Object> getMap(String key, Map<String, Object> defaultValue = null) {
        findValue { ConfigurationSource src -> src.getMap(key, defaultValue) }
    }

    @Override
    public <T> T getObject(String key, Class<? extends T> type, ObjectMapper mapper, T defaultValue = null) {
        findValue { ConfigurationSource src -> src.getObject(key, type, mapper, defaultValue) }
    }

    @TypeChecked(SKIP)
    private <T> T findValue(final Closure<T> getter) {
        T value = null
        for (final ConfigurationSource source : sources) {
            value = getter(source)
            if (value != null) {
                break
            }
        }
        value
    }
}
