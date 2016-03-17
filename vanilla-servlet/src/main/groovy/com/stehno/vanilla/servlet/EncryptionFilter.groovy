package com.stehno.vanilla.servlet


import groovy.transform.TypeChecked
import groovy.util.logging.Slf4j

import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.servlet.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * TODO: document me
 */
@TypeChecked @Slf4j // FIXME: consider compile-static
class EncryptionFilter implements Filter {

    // FIXME: come up with a client-side helper

    static final String DEFAULT_CRYPTO_ALGORITHM = 'AES/CBC/PKCS5PADDING'

    static final String ENCRYPTION_KEY_ID = 'Encryption-Key-ID'
    static final String ENCRYPTOR_SESSION_KEY = 'filter.encryption.encryptor'
    static final String DECRYPTOR_SESSION_KEY = 'filter.encryption.decryptor'

    private static final Collection<String> ENCRYPTION_HEADERS = [
        ENCRYPTION_KEY_ID, DecryptedRequest.ENCRYPTED_CONTENT_LENGTH, DecryptedRequest.ENCRYPTED_CONTENT_TYPE
    ].asImmutable()

    String cryptoAlgorithm = DEFAULT_CRYPTO_ALGORITHM
    EncryptionKeyResolver keyResolver

    @Override
    void init(FilterConfig config) throws ServletException {
        cryptoAlgorithm = config.getInitParameter('encryption-algorithm').trim() ?: DEFAULT_CRYPTO_ALGORITHM

        log.info 'Using encryption-algorithm: {}', cryptoAlgorithm

        // NOTE: if no resolver found in application context, will expect direct injection
        String keyResolverName = config.getInitParameter('key-resolver')
        if (keyResolverName) {
            keyResolver = config.servletContext.getAttribute(keyResolverName) as EncryptionKeyResolver
        }

        if (keyResolver) {
            log.info 'Using Key-Resolver: {}', keyResolver.class.name
        } else {
            throw new IllegalStateException('No EncryptionKeyResolver configured.')
        }
    }

    @Override
    void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = req as HttpServletRequest
        HttpServletResponse response = res as HttpServletResponse

        if (isEncrypted(request)) {
            chain.doFilter(
                new DecryptedRequest(request, findDecryptionCipher(request)),
                new EncryptedResponse(response, findEncryptionCipher(request))
            )

        } else {
            // just pass through as normal

            chain.doFilter(request, response)
        }
    }

    @Override
    void destroy() {
        // TODO: anything?
    }

    private boolean isEncrypted(final HttpServletRequest request) {
        request.headerNames.toList().containsAll(ENCRYPTION_HEADERS)
    }

    private Cipher findEncryptionCipher(final HttpServletRequest request) {
        Cipher cipher = request.session.getAttribute(ENCRYPTOR_SESSION_KEY) as Cipher
        if (!cipher) {
            cipher = createCipher(request.getHeader(ENCRYPTION_KEY_ID), Cipher.ENCRYPT_MODE)
            request.session.setAttribute(ENCRYPTOR_SESSION_KEY, cipher)
        }
        cipher
    }

    private Cipher findDecryptionCipher(final HttpServletRequest request) {
        Cipher cipher = request.session.getAttribute(DECRYPTOR_SESSION_KEY) as Cipher
        if (!cipher) {
            cipher = createCipher(request.getHeader(ENCRYPTION_KEY_ID), Cipher.DECRYPT_MODE)
            request.session.setAttribute(DECRYPTOR_SESSION_KEY, cipher)
        }
        cipher
    }

    // Pull the cipher stuff out into a util (vanilla?)
    private Cipher createCipher(final String keyId, final int mode) {
        // FIXME: is this a standard or is it just salt?
        IvParameterSpec param = new IvParameterSpec('RandomInitVector'.getBytes('UTF-8'))
        Cipher cipher = Cipher.getInstance(cryptoAlgorithm)
        cipher.init(mode, keyResolver.resolve(keyId), param)
        return cipher
    }
}