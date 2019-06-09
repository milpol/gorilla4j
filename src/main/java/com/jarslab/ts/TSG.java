package com.jarslab.ts;

import java.util.BitSet;

public class TSG
{
    private final int startTime;
    private int time;
    private double value;
    private int timeDelta;
    private int leading;
    private int trailing;
    private BitSet bitSet = new BitSet();
    private int currentIndex = 0;
    private boolean finished;

    public TSG(final int startTime)
    {
        this.startTime = startTime;
        writeInt(startTime);
    }

    public synchronized void close()
    {
        if (!finished) {
            bitSet.set(currentIndex, currentIndex + 36);
            currentIndex += 37;
            skipBit();
            finished = true;
        }
    }

    public synchronized void put(final int time, final double value)
    {
        if (this.time == 0) {
            this.time = time;
            this.value = value;
            timeDelta = time - startTime;
            write(timeDelta, 14);
            writeLong(Double.doubleToLongBits(value));
        } else {
            int timeDelta = time - this.time;
            int dod = timeDelta - this.timeDelta;
            if (dod == 0) {
                skipBit();
            } else if (-63 <= dod && dod <= 64) {
//                write(0x02, 2); //TODO: Find smarter way to drop-in unsigned flags
                flipBit();
                skipBit();
                write(dod, 7);
            } else if (-255 <= dod && dod <= 256) {
//                write(0x06, 3);
                flipBit();
                flipBit();
                skipBit();
                write(dod, 9);
            } else if (-2047 <= dod && dod <= 2048) {
//                write(0x0e, 4);
                flipBit();
                flipBit();
                flipBit();
                skipBit();
                write(dod, 12);
            } else {
//                write(0x0f, 4);
                flipBit();
                flipBit();
                flipBit();
                flipBit();
                write(dod, 32);
            }

            long valueDelta = Double.doubleToLongBits(value) ^ Double.doubleToLongBits(this.value);
            if (valueDelta == 0) {
                skipBit();
            } else {
                flipBit();
                final int valueLeadingZeros = Math.min(Long.numberOfLeadingZeros(valueDelta), 31);
                final int valueTrailingZeros = Long.numberOfTrailingZeros(valueDelta);
                if (this.leading != 31 && valueLeadingZeros >= this.leading && valueTrailingZeros >= this.trailing) {
                    skipBit();
                    write(valueDelta >> this.trailing, 64 - this.leading - this.trailing);
                } else {
                    leading = valueLeadingZeros;
                    trailing = valueTrailingZeros;
                    flipBit();
                    write(leading, 5);
                    int significantBits = 64 - leading - trailing;
                    write(significantBits, 6);
                    write(valueDelta >> trailing, significantBits);
                }
            }
            this.time = time;
            this.value = value;
        }
    }

    BitSet getBitSet()
    {
        return bitSet;
    }

    private void skipBit()
    {
        ++currentIndex;
    }

    private void flipBit()
    {
        bitSet.set(currentIndex);
        ++currentIndex;
    }

    private void writeInt(final int n)
    {
        write(n, 32);
    }

    private void writeLong(final long n)
    {
        write(n, 64);
    }

    private void write(final long n, final int size)
    {
        for (int i = 0; i < size; ++i) {
            if (getBit(n, i)) {
                bitSet.set(currentIndex);
            }
            ++currentIndex;
        }
    }

    private boolean getBit(final long n, final int k)
    {
        return ((n >> k) & 1) == 1;
    }
}
