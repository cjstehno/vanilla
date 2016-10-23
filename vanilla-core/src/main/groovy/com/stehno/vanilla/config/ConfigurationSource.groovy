package com.stehno.vanilla.config

import com.stehno.vanilla.mapper.ObjectMapper
import com.stehno.vanilla.util.TimeSpan

import java.nio.file.Path

/**
 * A generic abstraction of a configuration source.
 */
interface ConfigurationSource {

    /**
     * Used to retrieve the specified property as a String.
     *
     * @param key the property key
     * @return the property value
     */
    String getString(String key)

    /**
     * Used to retrieve the specified property as a String, with the designated default value.
     *
     * @param key the property key
     * @return the property value
     */
    String getString(String key, String defaultValue)

    /**
     * Used to retrieve the specified property as an Integer.
     *
     * @param key the property key
     * @return the property value
     */
    Integer getInt(String key)

    /**
     * Used to retrieve the specified property as an Integer, with the designated default value.
     *
     * @param key the property key
     * @return the property value
     */
    Integer getInt(String key, Integer defaultValue)

    /**
     * Used to retrieve the specified property as a Long.
     *
     * @param key the property key
     * @return the property value
     */
    Long getLong(String key)

    /**
     * Used to retrieve the specified property as a Long, with the designated default value.
     *
     * @param key the property key
     * @return the property value
     */
    Long getLong(String key, Long defaultValue)

    /**
     * Used to retrieve the specified property as a Float.
     *
     * @param key the property key
     * @return the property value
     */
    Float getFloat(String key)

    /**
     * Used to retrieve the specified property as a Float, with the designated default value.
     *
     * @param key the property key
     * @return the property value
     */
    Float getFloat(String key, Float defaultValue)

    /**
     * Used to retrieve the specified property as a Double.
     *
     * @param key the property key
     * @return the property value
     */
    Double getDouble(String key)

    /**
     * Used to retrieve the specified property as a Double, with the designated default value.
     *
     * @param key the property key
     * @return the property value
     */
    Double getDouble(String key, Double defaultValue)

    /**
     * Used to retrieve the specified property as a Path.
     *
     * @param key the property key
     * @return the property value
     */
    Path getPath(String key)

    /**
     * Used to retrieve the specified property as a Path, with the designated default value.
     *
     * @param key the property key
     * @return the property value
     */
    Path getPath(String key, Path defaultValue)

    /**
     * Used to retrieve the specified property as a File.
     *
     * @param key the property key
     * @return the property value
     */
    File getFile(String key)

    /**
     * Used to retrieve the specified property as a File, with the designated default value.
     *
     * @param key the property key
     * @return the property value
     */
    File getFile(String key, File defaultValue)

    /**
     * Used to retrieve the specified property as a TimeSpan.
     *
     * @param key the property key
     * @return the property value
     */
    TimeSpan getTimeSpan(String key)

    /**
     * Used to retrieve the specified property as a TimeSpan, with the designated default value.
     *
     * @param key the property key
     * @return the property value
     */
    TimeSpan getTimeSpan(String key, TimeSpan defaultValue)

    /**
     * Used to retrieve the specified property as a Boolean.
     *
     * @param key the property key
     * @return the property value
     */
    Boolean getBoolean(String key)

    /**
     * Used to retrieve the specified property as a Boolean, with the designated default value.
     *
     * @param key the property key
     * @return the property value
     */
    Boolean getBoolean(String key, Boolean defaultValue)

    /**
     * Used to extract a ConfigurationSource built from the configuration keys below the specified key.
     *
     * @param key the property key
     * @return the property value
     */
    ConfigurationSource getConfiguration(String key)

    Map<String, Object> getMap(String key)

    /**
     * Used to retrieve a Map<String,Object> containing all the values configured under the given key. All keys under the
     * prefix key will be mapped (and flattened) to their values.
     *
     * The map keys will be stripped of the parent key. The value types will be implementation dependent, but generally
     * Strings unless otherwise specified by the implementation.
     *
     * If no values are found for the specified prefix key, the defaultValue will be returned.
     *
     * The returned map should be immutable.
     *
     * @param key the prefix key
     * @param defaultValue the default map value
     * @return the mapped configuration values.
     */
    Map<String, Object> getMap(String key, Map<String, Object> defaultValue)

    public <T> T getObject(String key, Class<? extends T> type, ObjectMapper mapper)

    public <T> T getObject(String key, Class<? extends T> type, ObjectMapper mapper, T defaultValue)
}