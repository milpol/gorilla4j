package com.jarslab.ts;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;

public class TSGIntegrationTests
{
    @Test
    public void shouldBuildMinuteDayBlock()
    {
        //given
        final TSG tsg = new TSG(TSGTest.START_TIME, new OutBitSet());
        //when
        long samplingTime = TSGTest.START_TIME;
        for (int i = 0; i < 1440; i++) {
            samplingTime += 60;
            tsg.put(samplingTime, i / 100);
        }
        tsg.close();
        final TSGIterator tsgIterator = tsg.toIterator();
        //then
        samplingTime = TSGTest.START_TIME;
        for (int i = 0; i < 1440; i++) {
            samplingTime += 60;
            assertThat(tsgIterator.next())
                    .isEqualTo(new DefaultDataPoint(samplingTime, i / 100));
        }
        assertThat(tsgIterator.hasNext()).isFalse();
    }

    @Test
    public void shouldBuildBlockWithIrregularTimes()
    {
        //given
        final List<DataPoint> dataPoints = new ArrayList<>();
        long samplingTime = TSGTest.START_TIME;
        for (int i = 0; i < 50; i++) {
            samplingTime += ThreadLocalRandom.current().nextInt(0, (i + 1) * 10);
            dataPoints.add(new DefaultDataPoint(samplingTime, ThreadLocalRandom.current().nextDouble(100)));
        }
        final TSG tsg = new TSG(TSGTest.START_TIME, new OutBitSet());
        //when
        dataPoints.forEach(tsg::put);
        tsg.close();
        //then
        final TSGIterator tsgIterator = tsg.toIterator();
        dataPoints.forEach(dataPoint -> assertThat(tsgIterator.next()).isEqualTo(dataPoint));
        assertThat(tsgIterator.hasNext()).isFalse();
    }

    @Test
    public void shouldBuildBlockFromTicker()
    {
        //given
        final List<DataPoint> dataPoints = new ArrayList<>();
        long samplingTime = TSGTest.START_TIME;
        for (int i = 0; i < 50; i++) {
            samplingTime++;
            dataPoints.add(new DefaultDataPoint(samplingTime, i));
        }
        final TSG tsg = new TSG(TSGTest.START_TIME, new OutBitSet());
        //when
        dataPoints.forEach(tsg::put);
        tsg.close();
        //then
        final TSGIterator tsgIterator = tsg.toIterator();
        dataPoints.forEach(dataPoint -> assertThat(tsgIterator.next()).isEqualTo(dataPoint));
        assertThat(tsgIterator.hasNext()).isFalse();
    }

    @Test
    public void shouldBuildBlockFromSpreadData()
    {
        //given
        final List<DataPoint> dataPoints = new ArrayList<>();
        long samplingTime = TSGTest.START_TIME;
        for (int i = 0; i < 50; i++) {
            samplingTime += 100 * ThreadLocalRandom.current().nextInt(0, (i + 1) * 100);
            dataPoints.add(new DefaultDataPoint(samplingTime, i % 2 == 0 ? Integer.MAX_VALUE : i));
        }
        final TSG tsg = new TSG(TSGTest.START_TIME, new OutBitSet());
        //when
        dataPoints.forEach(tsg::put);
        tsg.close();
        //then
        final TSGIterator tsgIterator = tsg.toIterator();
        dataPoints.forEach(dataPoint -> assertThat(tsgIterator.next()).isEqualTo(dataPoint));
        assertThat(tsgIterator.hasNext()).isFalse();
    }
}