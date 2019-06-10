package com.jarslab.ts;

import java.util.BitSet;

public class OutBitSet implements OutBit<BitSet>
{
    private final BitSet bitSet = new BitSet();
    private int position = 0;

    @Override
    public void skipBit()
    {
        ++position;
    }

    @Override
    public void flipBit()
    {
        bitSet.flip(position);
        ++position;
    }

    @Override
    public void flipBits(final int n)
    {
        bitSet.flip(position, position + n);
        position += n;
    }

    @Override
    public void write(final long value, final int size)
    {
        for (int i = 0; i < size; ++i) {
            if (getBit(value, i)) {
                bitSet.set(position);
            }
            ++position;
        }
    }

    @Override
    public BitSet copy()
    {
        return (BitSet) bitSet.clone();
    }

    @Override
    public int getSize()
    {
        return position;
    }

    private boolean getBit(final long n, final int k)
    {
        return ((n >> k) & 1) == 1;
    }
}
