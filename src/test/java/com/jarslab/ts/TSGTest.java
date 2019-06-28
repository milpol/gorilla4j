package com.jarslab.ts;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.assertj.core.api.Assertions.assertThat;

public class TSGTest
{
    static final long START_TIME = 1546300800;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldFailForNullCreationParameters()
    {
        //expect
        expectedException.expect(NullPointerException.class);
        //when
        new TSG(42, null);
    }

    @Test
    public void shouldFailForInvalidTimeInputCreation()
    {
        //expect
        expectedException.expect(IllegalArgumentException.class);
        //when
        new TSG(-42, new OutBitSet());
    }

    @Test
    public void shouldOpenCloseTSGBlock()
    {
        //given
        final OutBitSet outBit = new OutBitSet();
        final TSG tsg = new TSG(START_TIME, outBit);
        //when
        tsg.close();
        //then
        assertThat(outBit.getSize()).isEqualTo(37);
        assertThat(tsg.isClosed()).isTrue();
    }

    @Test
    public void shouldFailForNullDataPointInput()
    {
        //given
        final TSG tsg = new TSG(START_TIME, new OutBitSet());
        //expect
        expectedException.expect(NullPointerException.class);
        //when
        tsg.put(null);
    }

    @Test
    public void shouldPutSingleDataPoint()
    {
        //given
        final TSG tsg = new TSG(START_TIME, new OutBitSet());
        final DataPoint dataPoint = new DefaultDataPoint(START_TIME + 5, 4.2);
        //when
        tsg.put(dataPoint);
        //then
        assertThat(tsg.toIterator().next()).isEqualTo(dataPoint);
    }

    @Test
    public void shouldPutTwoDataPoints()
    {
        //given
        final TSG tsg = new TSG(START_TIME, new OutBitSet());
        final DataPoint dataPoint_1 = new DefaultDataPoint(START_TIME + 5, 4.2);
        final DataPoint dataPoint_2 = new DefaultDataPoint(START_TIME + 10, 4.3);
        //when
        tsg.put(dataPoint_1);
        tsg.put(dataPoint_2);
        tsg.close();
        //then
        final TSGIterator tsgIterator = tsg.toIterator();
        assertThat(tsgIterator.next()).isEqualTo(dataPoint_1);
        assertThat(tsgIterator.next()).isEqualTo(dataPoint_2);
    }

    @Test
    public void shouldPutSingleDataPointWithPrimitives()
    {
        //given
        final TSG tsg = new TSG(START_TIME, new OutBitSet());
        final DataPoint dataPoint = new DefaultDataPoint(START_TIME + 5, 4.2);
        //when
        tsg.put(START_TIME + 5, 4.2);
        //then
        assertThat(tsg.toIterator().next()).isEqualTo(dataPoint);
    }

    @Test
    public void shouldFailPuttingDataPointOnClosedBlock()
    {
        //given
        final TSG tsg = new TSG(START_TIME, new OutBitSet());
        tsg.close();
        final DataPoint dataPoint = new DefaultDataPoint(START_TIME + 5, 4.2);
        //expect
        expectedException.expect(IllegalStateException.class);
        //when
        tsg.put(dataPoint);
    }

    @Test
    public void shouldDumpDataToBytesAndRecreateBlock()
    {
        //given
        final TSG tsg = new TSG(START_TIME, new OutBitSet());
        final DataPoint dataPoint = new DefaultDataPoint(START_TIME + 5, 4.2);
        tsg.put(dataPoint);
        final byte[] tsgBytes = tsg.toBytes();
        //when
        final TSG recreatedTsg = TSG.fromBytes(tsgBytes);
        //then
        assertThat(tsg.isClosed()).isEqualTo(recreatedTsg.isClosed());
        assertThat(tsg.toIterator().next()).isEqualTo(recreatedTsg.toIterator().next());
    }

    @Test
    public void shouldFailDumpingOnClosedBlock()
    {
        //given
        final TSG tsg = new TSG(START_TIME, new OutBitSet());
        tsg.close();
        //expect
        expectedException.expect(IllegalStateException.class);
        //when
        tsg.toBytes();
    }

    @Test
    public void shouldFailGettingDataBytesOnOpenBlock()
    {
        //given
        final TSG tsg = new TSG(START_TIME, new OutBitSet());
        //expect
        expectedException.expect(IllegalStateException.class);
        //when
        tsg.getDataBytes();
    }

    @Test
    public void shouldGetDataBytesOnClosedBlock()
    {
        //given
        final TSG tsg = new TSG(START_TIME, new OutBitSet());
        tsg.put(START_TIME + 15, 4.2);
        tsg.close();
        //when
        final byte[] dataBytes = tsg.getDataBytes();
        //then
        assertThat(dataBytes).isNotEmpty();
    }

    @Test
    public void shouldFailAddingPointBeforeStartTime()
    {
        //given
        final TSG tsg = new TSG(START_TIME, new OutBitSet());
        //expect
        expectedException.expect(IllegalArgumentException.class);
        //when
        tsg.put(START_TIME - 30, 4.2);
    }

    @Test
    public void shouldFailAddingPointsOutOfOrder()
    {
        //given
        final TSG tsg = new TSG(START_TIME, new OutBitSet());
        tsg.put(START_TIME + 90, 4.2);
        //expect
        expectedException.expect(IllegalArgumentException.class);
        //when
        tsg.put(START_TIME + 30, 42);
    }
}