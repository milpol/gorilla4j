package com.jarslab.ts;

public interface OutBit<T>
{
    void skipBit();

    void flipBit();

    void flipBits(int n);

    void write(long value, int size);

    default void writeInt(int value)
    {
        write(value, 32);
    }

    default void writeLong(long value)
    {
        write(value, 64);
    }

    T copy();

    int getSize();
}
