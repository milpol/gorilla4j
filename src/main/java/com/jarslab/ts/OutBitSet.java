package com.jarslab.ts;

import java.util.BitSet;

import static java.util.Objects.requireNonNull;

public class OutBitSet implements OutBit
{
    private final BitSet bitSet;
    private int position = 0;

    public OutBitSet()
    {
        bitSet = new BitSet();
    }

    OutBitSet(final BitSet bitSet,
              final int position)
    {
        this.bitSet = requireNonNull(bitSet);
        this.position = position;
    }

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
            if (ByteUtils.getBit(value, i)) {
                bitSet.set(position);
            }
            ++position;
        }
    }

    @Override
    public int getSize()
    {
        return position;
    }

    @Override
    public byte[] toBytes()
    {
        return bitSet.toByteArray();
    }
}
