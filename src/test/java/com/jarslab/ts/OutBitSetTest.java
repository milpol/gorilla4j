package com.jarslab.ts;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.BitSet;

import static org.assertj.core.api.Assertions.assertThat;

public class OutBitSetTest
{
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldFailForNullCreationParameters()
    {
        //expect
        expectedException.expect(NullPointerException.class);
        //when
        new OutBitSet(null, 0);
    }

    @Test
    public void shouldFailForInvalidPositionParameter()
    {
        //expect
        expectedException.expect(IllegalArgumentException.class);
        //when
        new OutBitSet(new BitSet(), -1);
    }

    @Test
    public void shouldSkipBit()
    {
        //given
        final BitSet bitSet = new BitSet();
        final OutBitSet outBitSet = new OutBitSet(bitSet, 0);
        //when
        outBitSet.skipBit();
        //then
        assertThat(outBitSet.getSize()).isEqualTo(1);
        assertThat(bitSet.get(0)).isFalse();
    }

    @Test
    public void shouldFlipBit()
    {
        //given
        final BitSet bitSet = new BitSet();
        final OutBitSet outBitSet = new OutBitSet(bitSet, 0);
        //when
        outBitSet.flipBit();
        //then
        assertThat(outBitSet.getSize()).isEqualTo(1);
        assertThat(bitSet.get(0)).isTrue();
    }

    @Test
    public void shouldFlipNBits()
    {
        //given
        final BitSet bitSet = new BitSet();
        final OutBitSet outBitSet = new OutBitSet(bitSet, 0);
        //when
        outBitSet.flipBits(2);
        //then
        assertThat(outBitSet.getSize()).isEqualTo(2);
        assertThat(bitSet.get(0)).isTrue();
        assertThat(bitSet.get(1)).isTrue();
        assertThat(bitSet.get(2)).isFalse();
    }

    @Test
    public void shouldWriteValue()
    {
        //given
        final BitSet bitSet = new BitSet();
        final OutBitSet outBitSet = new OutBitSet(bitSet, 0);
        //when
        outBitSet.write(42, 32);
        //then
        assertThat(outBitSet.getSize()).isEqualTo(32);
        assertThat(bitSet.toByteArray()[0]).isEqualTo((byte) 42);
    }

    @Test
    public void shouldCopyOutBitSet()
    {
        //given
        final BitSet bitSet = new BitSet();
        final OutBitSet outBitSet = new OutBitSet(bitSet, 0);
        outBitSet.write(42, 32);
        //when
        final OutBitSet outBitCopy = (OutBitSet) outBitSet.copy();
        //then
        assertThat(outBitSet.getSize()).isEqualTo(outBitCopy.getSize());
        assertThat(outBitSet.toBytes()).isEqualTo(outBitCopy.toBytes());
    }
}