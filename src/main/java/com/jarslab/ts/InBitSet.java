package com.jarslab.ts;

import java.util.BitSet;

import static java.util.Objects.requireNonNull;

public class InBitSet implements InBit
{
    private int position;
    private final BitSet bitSet;

    public InBitSet(final byte[] bytes)
    {
        this(BitSet.valueOf(bytes));
    }

    public InBitSet(final BitSet bitSet)
    {
        this.bitSet = requireNonNull(bitSet);
    }

    @Override
    public boolean read()
    {
        final boolean current = bitSet.get(position);
        ++position;
        return current;
    }

    @Override
    public long read(final int size)
    {
        long value = 0L;
        for (int i = 0; i < size; ++i) {
            value += bitSet.get(position) ? (1L << i) : 0L;
            ++position;
        }
        return value;
    }
}
