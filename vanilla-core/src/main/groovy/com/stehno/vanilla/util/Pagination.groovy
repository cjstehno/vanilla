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
package com.stehno.vanilla.util

import groovy.transform.TypeChecked

import static com.stehno.vanilla.Affirmations.affirm
import static groovy.transform.TypeCheckingMode.SKIP
import static java.lang.Math.min

/**
 * Provides utilities for calculating pagination values.
 */
@TypeChecked
class Pagination {

    /**
     * Calculates and retrieves the sub-set of the collection representing the requested page.
     *
     * @param collection the collection of items to be paginated
     * @param pageNumber the number (1-based) of the page to be retrieved
     * @param pageSize the number of items in a single page
     * @return the reference to the sub-set of items in the page (same collection)
     */
    @TypeChecked(SKIP)
    static <E> Collection<E> page(Collection<E> collection, int pageNumber, int pageSize) {
        collection[range(pageNumber, pageSize, collection.size())]
    }

    /**
     * Calculates and retrieves the sub-set of the array representing the requested page.
     *
     * @param collection the array of items to be paginated
     * @param pageNumber the number (1-based) of the page to be retrieved
     * @param pageSize the number of items in a single page
     * @return the reference to the sub-set of items in the page
     */
    @TypeChecked(SKIP)
    static <E> E[] page(E[] collection, int pageNumber, int pageSize) {
        collection[range(pageNumber, pageSize, collection.size())]
    }

    /**
     * Calculates the index range for the requested page.
     *
     * @param pageNumber the 1-based number of the page being calculated
     * @param pageSize the number of items in a page
     * @param totalItems the total number of items being paginated
     * @return the index range of the page
     */
    static IntRange range(int pageNumber, int pageSize, int totalItems) {
        affirm pageNumber > 0, 'The page number must be greater than zero.'
        affirm pageSize > 0, 'The page size must be greater than zero.'

        if (totalItems) {
            int start = (pageNumber - 1) * pageSize
            int end = start + pageSize - 1

            return (start..min(totalItems - 1, end))
        }

        return (0..0)
    }

    /**
     * Counts the available number of pages for the specified number of total items.
     *
     * @param totalItems the count of available items
     * @param pageSize the page size
     * @return the count of pages
     */
    static int countPages(int totalItems, int pageSize) {
        (totalItems / pageSize + (totalItems % pageSize == 0 ? 0 : 1)) as int
    }

    /**
     * Counts the available number of pages for the specified collection.
     *
     * @param collection the collection of items
     * @param pageSize the page size
     * @return the count of pages
     */
    static <E> int countPages(Collection<E> collection, int pageSize) {
        countPages(collection.size(), pageSize)
    }

    /**
     * Counts the available number of pages for the specified array.
     *
     * @param collection the array of items
     * @param pageSize the page size
     * @return the count of pages
     */
    static <E> int countPages(E[] collection, int pageSize) {
        countPages(collection.length, pageSize)
    }
}
