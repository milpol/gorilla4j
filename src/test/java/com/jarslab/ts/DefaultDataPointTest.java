package com.jarslab.ts;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

public class DefaultDataPointTest
{
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldFailForInvalidTime()
    {
        //expect
        expectedException.expect(IllegalArgumentException.class);
        //when
        new DefaultDataPoint(-1, 4.2);
    }

    @Test
    public void shouldProvideEqualityContract()
    {
        //given
        final long time = Instant.now().getEpochSecond();
        final double value = 4.2;
        //when
        final DataPoint dataPoint_1 = new DefaultDataPoint(time, value);
        final DataPoint dataPoint_2 = new DefaultDataPoint(time, value);
        //then
        assertThat(dataPoint_1).isEqualTo(dataPoint_2);
    }
}