package com.stehno.vanilla.servlet

import groovy.transform.TypeChecked

import javax.crypto.CipherOutputStream
import javax.servlet.ServletOutputStream
import javax.servlet.WriteListener

@TypeChecked
class EncryptedServletOutputStream extends ServletOutputStream {

    private final CipherOutputStream outstream

    EncryptedServletOutputStream(CipherOutputStream outstream) {
        this.outstream = outstream
    }

    @Override
    boolean isReady() {
        // TODO: what is the side-effect of this?
        true
    }

    @Override
    void setWriteListener(WriteListener writeListener) {
        throw new UnsupportedOperationException('WriteListener not supported')
    }

    @Override
    void write(int b) throws IOException {
        outstream.write(b)
    }
}