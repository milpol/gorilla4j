package com.jarslab.ts;

import java.util.Objects;

public class DefaultDataPoint implements DataPoint
{
    private final long time;
    private final double value;

    public DefaultDataPoint(final long time,
                            final double value)
    {
        if (time < 0) {
            throw new IllegalArgumentException(
                    String.format("Invalid time value `%d`.", time));
        }
        this.time = time;
        this.value = value;
    }

    @Override
    public long getTime()
    {
        return time;
    }

    @Override
    public double getValue()
    {
        return value;
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final DefaultDataPoint dataPoint = (DefaultDataPoint) o;
        return time == dataPoint.time &&
                Double.compare(dataPoint.value, value) == 0;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(time, value);
    }

    @Override
    public String toString()
    {
        return "DefaultDataPoint{" +
                "time=" + time +
                ", value=" + value +
                '}';
    }
}
