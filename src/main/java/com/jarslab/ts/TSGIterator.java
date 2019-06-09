package com.jarslab.ts;

import java.util.BitSet;
import java.util.Iterator;

import static java.util.Objects.requireNonNull;

public class TSGIterator implements Iterator<DataPoint>
{
    private final int timeStart;
    private final BitSet bitSet;
    private int time;
    private double value;
    private int leading;
    private int trailing;
    private boolean finished;
    private int timeDelta;

    private int currentIndex = 0;

    public TSGIterator(final BitSet bitSet)
    {
        this.bitSet = requireNonNull(bitSet);
        this.timeStart = readInt();
    }

    @Override
    public boolean hasNext()
    {
        return !finished;
    }

    @Override
    public DataPoint next()
    {
        if (time == 0) {
            timeDelta = readToInt(14);
            time = timeStart + timeDelta;
            value = Double.longBitsToDouble(readLong());
        } else {
            byte d = 0;
            for (int i = 0; i < 4; ++i) {
                d <<= 1;
                if (!readBit()) {
                    break;
                }
                d |= 1;
            }
            int dod = 0;
            int sz = 0;
            if (d == 0x00) {
            } else if (d == 0x02) {
                sz = 7;
            } else if (d == 0x06) {
                sz = 9;
            } else if (d == 0x0e) {
                sz = 12;
            } else if (d == 0x0f) {
                final int bits = readInt();
                if (bits == 0xffffffff) {
                    finished = true;
                    throw new IllegalStateException("End of stream");
                }
                dod = bits;
            }
            if (sz != 0) {
                int bits = readToInt(sz);
                if (bits > (1 << (sz - 1))) {
                    bits = bits - (1 << sz);
                }
                dod = bits;
            }
            timeDelta += dod;
            time += timeDelta;

            final boolean valueChanged = readBit();
            if (valueChanged) {
                final boolean differentZeros = readBit();
                if (differentZeros) {
                    leading = readToInt(5);
                    int mbits = readToInt(6);
                    if (mbits == 0) {
                        mbits = 64;
                    }
                    trailing = 64 - leading - mbits;
                }
                final int valueBitsCount = 64 - trailing - leading;
                final long valueBits = read(valueBitsCount);
                long currentValueBits = Double.doubleToLongBits(value);
                currentValueBits ^= (valueBits << trailing);
                value = Double.longBitsToDouble(currentValueBits);
            }
        }
        return new DataPoint(time, value);
    }

    private int readInt()
    {
        return (int) read(32);
    }

    private long readLong()
    {
        return read(64);
    }

    private int readToInt(final int limit)
    {
        return (int) read(limit);
    }

    private boolean readBit()
    {
        final boolean current = bitSet.get(currentIndex);
        ++currentIndex;
        return current;
    }

    private long read(final int limit)
    {
        long value = 0L;
        for (int i = 0; i < limit; ++i) {
            value += bitSet.get(currentIndex) ? (1L << i) : 0L;
            ++currentIndex;
        }
        return value;
    }
}
