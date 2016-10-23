package com.stehno.vanilla.config

import com.stehno.vanilla.mapper.ObjectMapper
import com.stehno.vanilla.util.TimeSpan
import groovy.transform.Memoized
import groovy.transform.TupleConstructor

import java.nio.file.Path

/**
 * Created by cjstehno on 10/22/16.
 */
@TupleConstructor
class CachedConfigurationSource implements ConfigurationSource {

    final ConfigurationSource source

    @Memoized
    String getString(String key, String defaultValue = null) {
        source.getString(key, defaultValue)
    }

    @Memoized
    Integer getInt(String key, Integer defaultValue = null) {
        source.getInt(key, defaultValue)
    }

    @Memoized
    Long getLong(String key, Long defaultValue = null) {
        source.getLong(key, defaultValue)
    }

    @Memoized
    Float getFloat(String key, Float defaultValue = null) {
        source.getFloat(key, defaultValue)
    }

    @Memoized
    Double getDouble(String key, Double defaultValue = null) {
        source.getDouble(key, defaultValue)
    }

    @Memoized
    Path getPath(String key, Path defaultValue = null) {
        source.getPath(key, defaultValue)
    }

    @Memoized
    File getFile(String key, File defaultValue = null) {
        source.getFile(key, defaultValue)
    }

    @Memoized
    TimeSpan getTimeSpan(String key, TimeSpan defaultValue = null) {
        source.getTimeSpan(key, defaultValue)
    }

    @Memoized
    Boolean getBoolean(String key, Boolean defaultValue = null) {
        source.getBoolean(key, defaultValue)
    }

    @Memoized
    ConfigurationSource getConfiguration(String key) {
        source.getConfiguration(key)
    }

    @Memoized
    Map<String, Object> getMap(String key, Map<String, Object> defaultValue = null) {
        source.getMap(key, defaultValue)
    }

    @Memoized
    public <T> T getObject(String key, Class<? extends T> type, ObjectMapper mapper, T defaultValue = null) {
        source.getObject(key, type, mapper, defaultValue)
    }
}
