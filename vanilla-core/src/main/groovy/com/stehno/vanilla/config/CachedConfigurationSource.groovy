/*
 * Copyright (C) 2016 Christopher J. Stehno <chris@stehno.com>
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
import groovy.transform.Memoized
import groovy.transform.TupleConstructor

import java.nio.file.Path

/**
 * A cached decorator for ConfigurationSource implementations. Each of the interface methods is decorated with a Groovy @Memoized annotation to cause
 * the calls to be cached.
 */
@TupleConstructor
class CachedConfigurationSource implements ConfigurationSource {

    /**
     * The decorated ConfigurationSource.
     */
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
