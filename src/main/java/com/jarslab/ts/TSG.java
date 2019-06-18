package com.jarslab.ts;

import java.nio.ByteBuffer;
import java.util.BitSet;

import static java.util.Objects.requireNonNull;

public class TSG
{
    private final int startTime;
    private final OutBit outBit;
    private int time;
    private double value;
    private int timeDelta;
    private int leading;
    private int trailing;
    private boolean closed;

    public TSG(final int startTime,
               final OutBit outBit)
    {
        this.startTime = startTime;
        this.outBit = requireNonNull(outBit);
        outBit.writeInt(startTime);
    }

    private TSG(final int startTime,
                final OutBit outBit,
                final boolean ignore)
    {
        this.startTime = startTime;
        this.outBit = requireNonNull(outBit);
    }

    public static TSG fromBytes(final byte[] bytes)
    {
        final ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        final int startTime = byteBuffer.getInt();
        final int time = byteBuffer.getInt();
        final double value = byteBuffer.getDouble();
        final int timeDelta = byteBuffer.getInt();
        final int leading = byteBuffer.getInt();
        final int trailing = byteBuffer.getInt();
        final int position = byteBuffer.getInt();
        byte[] bytesArray = new byte[byteBuffer.remaining()];
        byteBuffer.get(bytesArray, 0, bytesArray.length);
        final TSG tsg = new TSG(
                startTime,
                new OutBitSet(BitSet.valueOf(bytesArray), position),
                false);
        tsg.time = time;
        tsg.value = value;
        tsg.timeDelta = timeDelta;
        tsg.leading = leading;
        tsg.trailing = trailing;
        return tsg;
    }

    public synchronized void close()
    {
        if (!closed) {
            outBit.flipBits(36);
            outBit.skipBit();
            closed = true;
        }
    }

    public synchronized void put(final DataPoint dataPoint)
    {
        put(dataPoint.getTime(), dataPoint.getValue());
    }

    public synchronized void put(final int time,
                                 final double value)
    {
        if (closed) {
            throw new IllegalStateException("Block already closed.");
        }
        if (this.time == 0) {
            if (time <= this.startTime) {
                throw new IllegalArgumentException(
                        String.format("Issued time: `%d` is out of block start time: `%d`.", time, startTime));
            }
            putInitialPoint(time, value);
        } else {
            if (time <= this.time) {
                throw new IllegalArgumentException(
                        String.format("Issued time: `%d` is before last inserted: `%d`.", time, this.time));
            }
            putTime(time);
            putValue(value);
        }
    }

    public synchronized boolean isClosed()
    {
        return closed;
    }

    public synchronized byte[] toBytes()
    {
        if (closed) {
            throw new IllegalStateException("Block already closed, dump data instead.");
        }
        return ByteUtils.concat(
                ByteBuffer.allocate(32)
                        .putInt(startTime)
                        .putInt(time)
                        .putDouble(value)
                        .putInt(timeDelta)
                        .putInt(leading)
                        .putInt(trailing)
                        .putInt(outBit.getSize())
                        .array(),
                outBit.toBytes());
    }

    public synchronized byte[] getDataBytes()
    {
        if (!closed) {
            throw new IllegalStateException("Block not sealed yet.");
        }
        return outBit.toBytes();
    }

    public synchronized TSGIterator toIterator()
    {
        if (closed) {
            return new TSGIterator(new InBitSet(outBit.copy().toBytes()));
        } else {
            final OutBit outBitCopy = outBit.copy();
            outBitCopy.flipBits(36);
            outBitCopy.skipBit();
            return new TSGIterator(new InBitSet(outBitCopy.toBytes()));
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
