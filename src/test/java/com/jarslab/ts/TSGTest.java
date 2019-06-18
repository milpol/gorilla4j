package com.jarslab.ts;

import org.junit.Test;

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
        final TSGIterator tsgIterator = new TSGIterator(new InBitSet(tsg.getDataBytes()));
        while (tsgIterator.hasNext()) {
            System.out.println(tsgIterator.next());
        }
    }

    @Test
    public void shouldSerializeDeserializeTsg()
    {
        //given
        final int startTime = 1560074400;
        final OutBitSet outBitsSet = new OutBitSet();
        final TSG tsg = new TSG(startTime, outBitsSet);
        tsg.put(startTime + 300, 1.1);
        tsg.put(startTime + 300 * 2, 1.1);
        tsg.put(startTime + 300 * 3, 1.2);
        tsg.put(startTime + 300 * 4, 1.3);
        tsg.put(startTime + 300 * 5, 1.4);
        tsg.put(startTime + 300 * 6, 2.1);
        tsg.put(startTime + 300 * 7, 2.2);
        tsg.put(startTime + 300 * 8, 2.3);
        //when
        final TSG tsgFromDump = TSG.fromBytes(tsg.toBytes());
        tsg.close();
        tsgFromDump.put(startTime + 300 * 9, 3.1);
        tsgFromDump.put(startTime + 300 * 10, 3.2);
        tsgFromDump.close();
        final TSGIterator tsgIterator = tsgFromDump.toIterator();
        //then
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
        final TSGIterator tsgIterator = new TSGIterator(new InBitSet(tsg.getDataBytes()));
        while (tsgIterator.hasNext()) {
            System.out.println(tsgIterator.next());
        }
    }
}