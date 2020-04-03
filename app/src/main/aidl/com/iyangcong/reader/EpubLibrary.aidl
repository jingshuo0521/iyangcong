// EpubLibrary.aidl
package com.iyangcong.reader;

// Declare any non-default types here with import statements

interface EpubLibrary {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    boolean isFinished();

    void startEpubDecodeAndEncode(long bookId);
}
