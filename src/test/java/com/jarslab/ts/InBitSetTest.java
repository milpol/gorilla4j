package com.jarslab.ts;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.BitSet;

import static org.assertj.core.api.Assertions.assertThat;

public class InBitSetTest
{
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldFailForNullBytes()
    {
        //expect
        expectedException.expect(NullPointerException.class);
        //when
        new InBitSet((byte[]) null);
    }

    @Test
    public void shouldFailForNullBitSet()
    {
        //expect
        expectedException.expect(NullPointerException.class);
        //when
        new InBitSet((BitSet) null);
    }

    @Test
    public void shouldReadSingleBits()
    {
        //given
        final BitSet bitSet = new BitSet(2);
        bitSet.flip(1);
        final InBitSet inBitSet = new InBitSet(bitSet);
        //when
        final boolean b1 = inBitSet.read();
        final boolean b2 = inBitSet.read();
        //then
        assertThat(b1).isFalse();
        assertThat(b2).isTrue();
    }

    @Test
    public void shouldReadToLimit()
    {
        //given
        final BitSet bitSet = new BitSet(2);
        bitSet.flip(1);
        bitSet.flip(3);
        final InBitSet inBitSet = new InBitSet(bitSet);
        //when
        final long result = inBitSet.read(4);
        //then
        assertThat(result).isEqualTo(10);
    }

    @Test
    public void shouldReadInt()
    {
        //given
        final InBitSet inBitSet = new InBitSet(new byte[]{42, 0, 0, 0});
        //when
        final int value = inBitSet.readInt();
        //then
        assertThat(value).isEqualTo(42);
    }

    @Test
    public void shouldReadLong()
    {
        //given
        final InBitSet inBitSet = new InBitSet(new byte[]{0, 1, 0, 0, 0, 0, 0, 0});
        //when
        final long value = inBitSet.readLong();
        //then
        assertThat(value).isEqualTo(256);
    }

    @Test
    public void shouldFailForLongOverflowRead()
    {
        //given
        final InBitSet inBitSet = new InBitSet(new BitSet());
        //expect
        expectedException.expect(IllegalArgumentException.class);
        //when
        inBitSet.read(100);
    }
}