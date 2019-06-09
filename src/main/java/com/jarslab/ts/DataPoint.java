package com.jarslab.ts;

import java.util.Objects;

public class DataPoint
{
    private final int time;
    private final double value;

    public DataPoint(final int time, final double value)
    {
        this.time = time;
        this.value = value;
    }

    public int getTime()
    {
        return time;
    }

    public double getValue()
    {
        return value;
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final DataPoint dataPoint = (DataPoint) o;
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
        return "DataPoint{" +
                "time=" + time +
                ", value=" + value +
                '}';
    }
}
