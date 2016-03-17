package com.stehno.vanilla.servlet

import com.mockrunner.mock.web.MockFilterChain
import com.mockrunner.mock.web.MockFilterConfig
import com.mockrunner.mock.web.MockHttpServletRequest
import com.mockrunner.mock.web.MockHttpServletResponse
import spock.lang.Specification

import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.IvParameterSpec
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.security.Key

class EncryptionFilterSpec extends Specification {

    private MockServlet servlet = new MockServlet()
    private MockHttpServletRequest request = new MockHttpServletRequest()
    private MockHttpServletResponse response = new MockHttpServletResponse()
    private MockFilterChain chain = new MockFilterChain(servlet: servlet)
    private EncryptionFilter filter = new EncryptionFilter()
    private MockFilterConfig filterConfig = new MockFilterConfig()
    private final Key key = KeyGenerator.getInstance('AES').generateKey()

    // TODO: test init/destroy
    // TODO: test the individual parts

    def 'doFilter()'() {
        setup:
        filter.keyResolver = Mock(EncryptionKeyResolver) {
            2 * resolve('some.test.id') >> key
        }

        byte[] decryptedContent = null
        servlet.serviceClosure = { ServletRequest req, ServletResponse res ->
            decryptedContent = (req as HttpServletRequest).inputStream.bytes

            HttpServletResponse response = res as HttpServletResponse
            response.contentType = 'text/plain'
            response.outputStream.bytes = decryptedContent
        }

        byte[] content = 'this is my content'.bytes
        byte[] encryptedContent = encrypt(content, key)

        request.bodyContent = encryptedContent
        request.contentType = 'application/octet-stream'
        request.addHeader(EncryptionFilter.ENCRYPTION_KEY_ID, 'some.test.id')
        request.addHeader(DecryptedRequest.ENCRYPTED_CONTENT_TYPE, 'text/plain')
        request.addHeader(DecryptedRequest.ENCRYPTED_CONTENT_LENGTH, content.length as String)

        when:
        filter.doFilter(request, response, chain)

        then:
        decryptedContent == content
        servlet.request instanceof DecryptedRequest
        servlet.request.contentLength == content.length
        servlet.request.contentType == 'text/plain'

        servlet.response instanceof EncryptedResponse
        servlet.response.outputStream
        servlet.response.contentType == 'application/octet-stream'
    }

    private static byte[] encrypt(byte[] content, Key key) {
        // FIXME: is this a standard or is it just salt?
        IvParameterSpec param = new IvParameterSpec('RandomInitVector'.getBytes('UTF-8'))
        Cipher cipher = Cipher.getInstance('AES/CBC/PKCS5PADDING')
        cipher.init(Cipher.ENCRYPT_MODE, key, param)
        cipher.doFinal(content)
    }
}