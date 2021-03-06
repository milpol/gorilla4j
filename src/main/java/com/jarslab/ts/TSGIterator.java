package com.jarslab.ts;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.stream.DoubleStream;

import static java.util.Objects.requireNonNull;

/**
 * TSG block data viewer in the form of <code>DataPoint</code> iterator.
 */
public class TSGIterator implements Iterator<DataPoint>
{
    private final InBit inBit;
    private long time;
    private double value;
    private int leading;
    private int trailing;
    private int timeDelta;
    private boolean finished;

    /**
     * Create TSG iterator from given InBit TSG bits.
     *
     * @param inBit TSG data dump wrapped in InBit abstraction
     * @throws NullPointerException for null InBit
     */
    public TSGIterator(final InBit inBit)
    {
        this.inBit = requireNonNull(inBit);
        final long timeStart = inBit.readLong();
        this.timeDelta = inBit.readToInt(14);
        this.time = timeStart + timeDelta;
        this.value = Double.longBitsToDouble(inBit.readLong());
        this.finished = DoubleStream.of(timeStart, timeDelta, time, value)
                .allMatch(i -> i == 0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasNext()
    {
        return !finished;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataPoint next()
    {
        if (finished) {
            throw new NoSuchElementException();
        } else {
            final DataPoint dataPoint = new DefaultDataPoint(time, value);
            nextAdvance();
            return dataPoint;
        }
    }

    private void nextAdvance()
    {
        byte deltaSize = 0;
        for (int i = 0; i < 4; ++i) {
            deltaSize <<= 1;
            if (!inBit.read()) {
                break;
            }
            deltaSize |= 1;
        }
        int deltaDelta = 0;
        int deltaBits = 0;
        if (deltaSize == 0x00) {
        } else if (deltaSize == 0x02) {
            deltaBits = 7;
        } else if (deltaSize == 0x06) {
            deltaBits = 9;
        } else if (deltaSize == 0x0e) {
            deltaBits = 12;
        } else if (deltaSize == 0x0f) {
            final int bits = inBit.readInt();
            if (bits == 0xffffffff) {
                finished = true;
                return;
            }
            deltaDelta = bits;
        }
        if (deltaBits != 0) {
            int bits = inBit.readToInt(deltaBits);
            if (bits > (1 << (deltaBits - 1))) {
                bits = bits - (1 << deltaBits);
            }
            deltaDelta = bits;
        }
        timeDelta += deltaDelta;
        time += timeDelta;

        final boolean valueChanged = inBit.read();
        if (valueChanged) {
            final boolean differentZigZag = inBit.read();
            if (differentZigZag) {
                leading = inBit.readToInt(5);
                int valueBits = inBit.readToInt(6);
                if (valueBits == 0) {
                    valueBits = 64;
                }
                trailing = 64 - leading - valueBits;
            }
            final int valueBitsCount = 64 - trailing - leading;
            final long valueBits = inBit.read(valueBitsCount);
            long currentValueBits = Double.doubleToLongBits(value);
            currentValueBits ^= (valueBits << trailing);
            value = Double.longBitsToDouble(currentValueBits);
        }
    }
}
