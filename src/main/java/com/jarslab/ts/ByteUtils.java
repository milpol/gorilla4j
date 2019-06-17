package com.jarslab.ts;

import static java.util.Objects.requireNonNull;

final class ByteUtils
{
    private ByteUtils()
    {
    }

    static boolean getBit(final long n, final int k)
    {
        return ((n >> k) & 1) == 1;
    }

    static byte[] concat(final byte[] bytes1,
                         final byte[] bytes2)
    {
        requireNonNull(bytes1);
        requireNonNull(bytes2);
        final int bytes1Length = bytes1.length;
        final int bytes2Length = bytes2.length;
        final byte[] output = new byte[bytes1Length + bytes2Length];
        System.arraycopy(bytes1, 0, output, 0, bytes1Length);
        System.arraycopy(bytes2, 0, output, bytes1Length, bytes2Length);
        return output;
    }
}
