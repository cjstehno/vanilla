package com.stehno.vanilla.servlet

import groovy.transform.Canonical
import groovy.transform.TypeChecked

import java.security.Key

/**
 * TODO: document me
 */
@TypeChecked @Canonical
class StaticEncryptionKeyResolver implements EncryptionKeyResolver {

    Map<String, Key> keys = [:]

    @Override
    Key resolve(String keyId) {
        keys[keyId]
    }
}
