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
import java.nio.file.Paths

import static groovy.transform.TypeCheckingMode.SKIP

/**
 * Map-based implementation of the ConfigurationSource interface. The contained map will be single-level, having no nested Map or Collection
 * structures. The mapped values are expected to be Strings or of the value type being retrieved.
 */
@TypeChecked
class MapConfigurationSource implements ConfigurationSource {

    private final Map<String, Object> source

    // map should be flat keys -> values (not nested)  - maybe make a way to flatten
    MapConfigurationSource(Map<String, Object> source = [:]) {
        this.source = source
    }

    @Override
    String getString(String key, String defaultValue = null) {
        source[key] ?: defaultValue
    }

    @Override
    Integer getInt(String key, Integer defaultValue = null) {
        getConfig key, defaultValue, { String v -> Integer.parseInt(v) }
    }

    @Override
    Long getLong(String key, Long defaultValue = null) {
        getConfig key, defaultValue, { String v -> Long.parseLong(v) }
    }

    @Override
    Float getFloat(String key, Float defaultValue = null) {
        getConfig key, defaultValue, { String v -> Float.parseFloat(v) }
    }

    @Override
    Double getDouble(String key, Double defaultValue = null) {
        getConfig key, defaultValue, { String v -> Double.parseDouble(v) }
    }

    @Override
    Path getPath(String key, Path defaultValue = null) {
        getConfig key, defaultValue, { String v -> Paths.get(v) }
    }

    @Override
    File getFile(String key, File defaultValue = null) {
        getConfig key, defaultValue, { String v -> new File(v) }
    }

    @Override
    TimeSpan getTimeSpan(String key, TimeSpan defaultValue = null) {
        getConfig key, defaultValue, { String v -> TimeSpan.parse(v) }
    }

    @Override
    Boolean getBoolean(String key, Boolean defaultValue = null) {
        getConfig key, defaultValue, { String v -> Boolean.parseBoolean(v) }
    }

    @Override
    ConfigurationSource getConfiguration(String key) {
        new MapConfigurationSource(getMap(key, [:]))
    }

    @Override @TypeChecked(SKIP)
    Map<String, Object> getMap(String key, Map<String, Object> defaultValue = null) {
        String prefix = "${key}."

        Map<String, Object> map = [:]

        source.each { k, v ->
            if (k.startsWith(prefix)) {
                map[k - prefix] = v
            }
        }

        (map ?: defaultValue).asImmutable()
    }

    @Override
    public <T> T getObject(String key, Class<? extends T> type, ObjectMapper mapper, T defaultValue = null) {
        Map<String, Object> map = getMap(key)
        map ? (mapper.create(map, type) as T) : defaultValue
    }

    private <T> T getConfig(final String key, final T defaultValue, final Closure<T> factory) {
        Object value = source[key]
        if (value == null) {
            return defaultValue
        } else if (value instanceof String) {
            return factory(value)
        }
        return value as T
    }
}
