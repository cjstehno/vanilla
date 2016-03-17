package com.stehno.vanilla.servlet

import java.security.Key

/**
 * TODO: document me
 */
interface EncryptionKeyResolver {

    Key resolve(String keyId)
}