package com.stehno.vanilla.servlet

import groovy.transform.TypeChecked

import javax.crypto.CipherInputStream
import javax.servlet.ReadListener
import javax.servlet.ServletInputStream

/**
 * TODO: document me
 */
@TypeChecked
class EncryptedServletInputStream extends ServletInputStream {

    private final CipherInputStream instream
    final int contentLength
    private int contentRead

    EncryptedServletInputStream(CipherInputStream instream, int contentLength) {
        this.instream = instream
        this.contentLength = contentLength
    }

    @Override
    boolean isFinished() {
        contentRead >= contentLength
    }

    @Override
    boolean isReady() {
        instream.available()
    }

    @Override
    void setReadListener(ReadListener readListener) {
        throw new UnsupportedOperationException('ReadListener not supported')
    }

    @Override
    int read() throws IOException {
        contentRead++
        instream.read()
    }
}
