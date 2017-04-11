/*
 * Copyright (C) 2017 Christopher J. Stehno <chris@stehno.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stehno.vanilla.config

import com.stehno.vanilla.mapper.ObjectMapper
import com.stehno.vanilla.util.TimeSpan
import groovy.transform.TypeChecked

import java.nio.file.Path

import static groovy.transform.TypeCheckingMode.SKIP

/**
 * Implementation of the ConfigurationSource interface that allows multiple ConfigurationSources to be layered such that the first source containing a value for a property will be used - this allows
 * properties to be retrieved from multiple sources where each source does not necessarily contain all properties, or when there is a priority override system desired for property resolution.
 */
@TypeChecked
class CompositeConfigurationSource implements ConfigurationSource {

    private final List<ConfigurationSource> sources

    /**
     * Creates a composite ConfigurationSource from a list of other configuration sources. The sources are searched in the provided order such that those defined earlier in the list will be checked
     * first, if no property is found, the next source will be searched.
     *
     * @param sources the ordered list of sources to be used
     */
    CompositeConfigurationSource(final List<ConfigurationSource> sources = []) {
        this.sources = sources
    }

    /**
     * Inserts the given ConfigurationSource at the specified index in the list.
     *
     * @param source the source to be added
     * @param index the index
     */
    void insertSource(final ConfigurationSource source, final int index) {
        sources.add index, source
    }

    /**
     * Adds the given source to the end of the composite search list.
     *
     * @param source the source to be added
     */
    void addSource(final ConfigurationSource source) {
        sources << source
    }

    /**
     * An alias to "addSource(ConfigurationSource)".
     *
     * @param source the source to be added
     */
    void leftShift(final ConfigurationSource source) {
        addSource source
    }

    @Override
    String getString(String key, String defaultValue = null) {
        findValue(defaultValue) { ConfigurationSource src -> src.getString(key) }
    }

    @Override
    Integer getInt(String key, Integer defaultValue = null) {
        findValue(defaultValue) { ConfigurationSource src -> src.getInt(key) }
    }

    @Override
    Long getLong(String key, Long defaultValue = null) {
        findValue(defaultValue) { ConfigurationSource src -> src.getLong(key) }
    }

    @Override
    Float getFloat(String key, Float defaultValue = null) {
        findValue(defaultValue) { ConfigurationSource src -> src.getFloat(key) }
    }

    @Override
    Double getDouble(String key, Double defaultValue = null) {
        findValue(defaultValue) { ConfigurationSource src -> src.getDouble(key) }
    }

    @Override
    Path getPath(String key, Path defaultValue = null) {
        findValue(defaultValue) { ConfigurationSource src -> src.getPath(key) }
    }

    @Override
    File getFile(String key, File defaultValue = null) {
        findValue(defaultValue) { ConfigurationSource src -> src.getFile(key) }
    }

    @Override
    TimeSpan getTimeSpan(String key, TimeSpan defaultValue = null) {
        findValue(defaultValue) { ConfigurationSource src -> src.getTimeSpan(key) }
    }

    @Override
    Boolean getBoolean(String key, Boolean defaultValue = null) {
        findValue(defaultValue) { ConfigurationSource src -> src.getBoolean(key) }
    }

    @Override
    ConfigurationSource getConfiguration(String key) {
        findValue { ConfigurationSource src -> src.getConfiguration(key) }
    }

    @Override
    Map<String, Object> getMap(String key, Map<String, Object> defaultValue = null) {
        findValue(defaultValue) { ConfigurationSource src -> src.getMap(key) }
    }

    @Override
    public <T> T getObject(String key, Class<? extends T> type, ObjectMapper mapper, T defaultValue = null) {
        findValue(defaultValue) { ConfigurationSource src -> src.getObject(key, type, mapper) }
    }

    @TypeChecked(SKIP)
    private <T> T findValue(final T defaultValue = null, final Closure<T> getter) {
        T value = null
        for (final ConfigurationSource source : sources) {
            value = getter(source)
            if (value != null) {
                break
            }
        }
        value != null ? value : defaultValue
    }
}
