package com.jarslab.ts;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.assertj.core.api.Assertions.assertThat;

public class TSGIteratorTest
{
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldFailForNullCreationParameters()
    {
        //expect
        expectedException.expect(NullPointerException.class);
        //when
        new TSGIterator(null);
    }

    @Test
    public void shouldHandleGracefullyEmptyInBit()
    {
        //given
        final TSGIterator tsgIterator = new TSGIterator(new InBitSet(new byte[]{}));
        //when
        final boolean hasNext = tsgIterator.hasNext();
        //then
        assertThat(hasNext).isFalse();
    }

    @Test
    public void shouldActAsValidIterator()
    {
        //given
        final TSG tsg = new TSG(TSGTest.START_TIME, new OutBitSet());
        tsg.put(TSGTest.START_TIME, 4.2);
        final TSGIterator tsgIterator = tsg.toIterator();
        //when
        final boolean firstNext = tsgIterator.hasNext();
        final DataPoint dataPoint = tsgIterator.next();
        final boolean closedNext = tsgIterator.hasNext();
        final boolean closedNext_2 = tsgIterator.hasNext();
        //then
        assertThat(firstNext).isTrue();
        assertThat(closedNext).isFalse();
        assertThat(closedNext_2).isFalse();
        assertThat(dataPoint).isEqualTo(new DataPoint(TSGTest.START_TIME, 4.2));
    }
}