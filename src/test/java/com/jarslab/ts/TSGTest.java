package com.jarslab.ts;

import org.junit.Test;

import java.util.BitSet;

public class TSGTest
{
    @Test
    public void shouldCreateTimeAlignedTsg()
    {
        //given
        final int startTime = 1560074400;
        final TSG tsg = new TSG(startTime);
        //when
        tsg.put(startTime + 300, 1.1);
        tsg.put(startTime + 300 * 2, 1.1);
        tsg.put(startTime + 300 * 3, 1.2);
        tsg.put(startTime + 300 * 4, 1.3);
        tsg.put(startTime + 300 * 5, 1.4);
        tsg.put(startTime + 300 * 6, 2.1);
        tsg.put(startTime + 300 * 7, 2.2);
        tsg.put(startTime + 300 * 8, 2.3);
        tsg.close();
        //then
        final BitSet bitSet = tsg.getBitSet();
        final TSGIterator tsgIterator = new TSGIterator(bitSet);
        for (int i = 0; i < 8; ++i) {
            System.out.println(tsgIterator.next());
        }
    }

    @Test
    public void shouldCreateIrregularTimeTsg()
    {
        //given
        final int startTime = 1560074400;
        final TSG tsg = new TSG(startTime);
        //when
        tsg.put(startTime + 2000, 1.2);
        tsg.put(startTime + 4050, 1.3);
        tsg.put(startTime + 5012, 1.4);
        tsg.close();
        //then
        final BitSet bitSet = tsg.getBitSet();
        final TSGIterator tsgIterator = new TSGIterator(bitSet);
        for (int i = 0; i < 3; ++i) {
            System.out.println(tsgIterator.next());
        }
    }
}