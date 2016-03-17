package com.stehno.vanilla.servlet

import groovy.transform.TypeChecked

import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.servlet.ServletInputStream
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletRequestWrapper
/**
 * TODO: document me
 */
@TypeChecked
class DecryptedRequest extends HttpServletRequestWrapper {

    static final String ENCRYPTED_CONTENT_LENGTH = 'Encrypted-Content-Length'
    static final String ENCRYPTED_CONTENT_TYPE = 'Encrypted-Content-Type'

    private final EncryptedServletInputStream inputStream
    String contentType

    DecryptedRequest(HttpServletRequest request, Cipher cipher) {
        super(request)

        contentType = request.getHeader(ENCRYPTED_CONTENT_TYPE)

        this.inputStream = new EncryptedServletInputStream(
            new CipherInputStream(request.inputStream, cipher),
            request.getIntHeader(ENCRYPTED_CONTENT_LENGTH)
        )
    }

    @Override
    int getContentLength() {
        inputStream.contentLength
    }

    @Override
    long getContentLengthLong() {
        inputStream.contentLength as long
    }

    @Override
    boolean isSecure() { true }

    @Override
    ServletInputStream getInputStream() throws IOException {
        inputStream
    }

    @Override
    BufferedReader getReader() throws IOException {
        new BufferedReader(new InputStreamReader(inputStream))
    }
}

