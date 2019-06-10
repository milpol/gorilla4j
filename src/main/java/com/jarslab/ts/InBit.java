package com.jarslab.ts;

public interface InBit<T>
{
    boolean read();

    default int readInt()
    {
        return readToInt(32);
    }

    default long readLong()
    {
        return read(64);
    }

    default int readToInt(int size)
    {
        return (int) read(size);
    }

    long read(int size);

    T copy();
}
