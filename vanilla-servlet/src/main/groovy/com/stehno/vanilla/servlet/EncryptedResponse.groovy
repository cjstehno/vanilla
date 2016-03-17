package com.stehno.vanilla.servlet

import groovy.transform.TypeChecked

import javax.crypto.Cipher
import javax.crypto.CipherOutputStream
import javax.servlet.ServletOutputStream
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpServletResponseWrapper

@TypeChecked
class EncryptedResponse extends HttpServletResponseWrapper {

    static final String ENCRYPTED_CONTENT_TYPE = 'Encrypted-Content-Type'
    private final EncryptedServletOutputStream outs

    EncryptedResponse(HttpServletResponse response, Cipher cipher) {
        super(response)

        this.outs = new EncryptedServletOutputStream(
            new CipherOutputStream(response.outputStream, cipher)
        )
    }

    @Override
    void setContentType(String type) {
        setHeader(ENCRYPTED_CONTENT_TYPE, type)
        super.setContentType('application/octet-stream')
    }

    @Override
    PrintWriter getWriter() throws IOException {
        new PrintWriter(outs)
    }

    @Override
    ServletOutputStream getOutputStream() throws IOException {
        outs
    }
}
