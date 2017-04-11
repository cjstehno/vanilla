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
package com.stehno.vanilla.jdbc

import groovy.transform.Canonical

/**
 * Created by cjstehno on 11/26/15.
 */
@Canonical
class DummyObjectC {
    String name
    int age
    float weight

    private byte _somethingElse

    void setSomethingElse(byte value) {
        _somethingElse = value
    }

    byte getSomethingElse() {
        _somethingElse
    }
}
