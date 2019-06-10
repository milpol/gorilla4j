package com.jarslab.ts;

import static java.util.Objects.requireNonNull;

public class TSG
{
    private final int startTime;
    private final OutBit<?> outBit;
    private int time;
    private double value;
    private int timeDelta;
    private int leading;
    private int trailing;
    private boolean finished;

    public TSG(final int startTime,
               final OutBit<?> outBit)
    {
        this.startTime = startTime;
        this.outBit = requireNonNull(outBit);
        outBit.writeInt(startTime);
    }

    public synchronized void close()
    {
        if (!finished) {
            outBit.flipBits(36);
            outBit.skipBit();
            finished = true;
        }
    }

    public synchronized void put(final DataPoint dataPoint)
    {
        put(dataPoint.getTime(), dataPoint.getValue());
    }

    public synchronized void put(final int time,
                                 final double value)
    {
        if (finished) {
            throw new IllegalStateException("Block already closed.");
        }
        if (this.time == 0) {
            putInitialPoint(time, value);
        } else {
            putTime(time);
            putValue(value);
        }
    }

    private void putInitialPoint(final int time,
                                 final double value)
    {
        this.time = time;
        this.value = value;
        timeDelta = time - startTime;
        outBit.write(timeDelta, 14);
        outBit.writeLong(Double.doubleToLongBits(value));
    }

    private void putTime(final int time)
    {
        int timeDelta = time - this.time;
        int timeDeltaDelta = timeDelta - this.timeDelta;
        if (timeDeltaDelta == 0) {
            outBit.skipBit();
        } else if (-63 <= timeDeltaDelta && timeDeltaDelta <= 64) {
            outBit.flipBit();
            outBit.skipBit();
            outBit.write(timeDeltaDelta, 7);
        } else if (-255 <= timeDeltaDelta && timeDeltaDelta <= 256) {
            outBit.flipBits(2);
            outBit.skipBit();
            outBit.write(timeDeltaDelta, 9);
        } else if (-2047 <= timeDeltaDelta && timeDeltaDelta <= 2048) {
            outBit.flipBits(3);
            outBit.skipBit();
            outBit.write(timeDeltaDelta, 12);
        } else {
            outBit.flipBits(4);
            outBit.write(timeDeltaDelta, 32);
        }
        this.time = time;
    }

    private void putValue(final double value)
    {
        final long valueDelta = Double.doubleToLongBits(value) ^ Double.doubleToLongBits(this.value);
        if (valueDelta == 0) {
            outBit.skipBit();
        } else {
            outBit.flipBit();
            final int valueLeadingZeros = Math.min(Long.numberOfLeadingZeros(valueDelta), 31);
            final int valueTrailingZeros = Long.numberOfTrailingZeros(valueDelta);
            if (this.leading != 31 &&
                    valueLeadingZeros >= this.leading &&
                    valueTrailingZeros >= this.trailing) {
                outBit.skipBit();
                outBit.write(valueDelta >> this.trailing,
                        64 - this.leading - this.trailing);
            } else {
                leading = valueLeadingZeros;
                trailing = valueTrailingZeros;
                outBit.flipBit();
                outBit.write(leading, 5);
                int significantBits = 64 - leading - trailing;
                outBit.write(significantBits, 6);
                outBit.write(valueDelta >> trailing, significantBits);
            }
        }
        this.value = value;
    }
}
