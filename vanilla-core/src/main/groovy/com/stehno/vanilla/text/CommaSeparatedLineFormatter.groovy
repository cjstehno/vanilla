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
package com.stehno.vanilla.text

import groovy.transform.TypeChecked

/**
 * Simple line formatter implementation which works for collections and arrays. It will generate a comma-separated text file with '#' used to
 * prefix comments. The line formatting will be simple string conversion of the objects.
 */
@TypeChecked
class CommaSeparatedLineFormatter implements LineFormatter {

    public static final String COMMA = ','
    public static final String COMMENT_PREFIX = '# '

    @Override
    String formatComment(String text) {
        COMMENT_PREFIX.concat(text)
    }

    @Override
    String formatLine(Object object) {
        (object as Collection).join(COMMA)
    }
}
