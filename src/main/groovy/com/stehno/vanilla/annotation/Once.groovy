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
package com.stehno.vanilla.annotation

import com.stehno.vanilla.transform.OnceTransform
import org.codehaus.groovy.transform.GroovyASTTransformationClass

import java.lang.annotation.Documented
import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * When this annotation is applied to a method, it will wrap the contents of the method in a check so that the method body will only be executed
 * once, no matter how many times the method is called.
 *
 * By default, subsequent calls to the method are ignored; however, by setting the "ignoreMultipleCalls" property to "false" you can cause
 * subsequent calls to throw an IllegalStateException.
 *
 * Note: This annotation only applies to methods that return "void".
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
@Documented
@GroovyASTTransformationClass(classes = [OnceTransform])
@interface Once {

    /**
     * Specifies whether or not multiple calls to the annotated method are ignored or throw an IllegalStateException. Defaults to "true",
     * denoting that subsequent calls will be ignored.
     */
    boolean ignoreMultipleCalls() default true

    /**
     * An alias to "fieldName".
     */
    String value() default ''

    /**
     * The name of the status field created to store whether or not the method has been called. The field will be exposed as a read-only
     * property with the specified name. If no value is provided, the field name will be based on the annotated method name as:
     * "boolean is<methodName>Called()".
     */
    String fieldName() default ''
}
