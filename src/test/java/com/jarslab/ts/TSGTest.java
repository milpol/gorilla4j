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
        final OutBitSet outBitsSet = new OutBitSet();
        final TSG tsg = new TSG(startTime, outBitsSet);
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
        final BitSet bitSet = outBitsSet.copy();
        final TSGIterator tsgIterator = new TSGIterator(new InBitSet(bitSet));
        while (tsgIterator.hasNext()) {
            System.out.println(tsgIterator.next());
        }
    }

    @Test
    public void shouldCreateIrregularTimeTsg()
    {
        //given
        final int startTime = 1560074400;
        final OutBitSet outBitsSet = new OutBitSet();
        final TSG tsg = new TSG(startTime, outBitsSet);
        //when
        tsg.put(startTime + 2000, 1.293);
        tsg.put(startTime + 4050, 1.375);
        tsg.put(startTime + 5012, 1.411);
        tsg.close();
        //then
        final BitSet bitSet = outBitsSet.copy();
        final TSGIterator tsgIterator = new TSGIterator(new InBitSet(bitSet));
        while (tsgIterator.hasNext()) {
            System.out.println(tsgIterator.next());
        }
    }
}