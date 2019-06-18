package com.jarslab.ts;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.assertj.core.api.Assertions.assertThat;

public class ByteUtilsTest
{
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldFailForOutOfLongRangeParameter()
    {
        //expect
        expectedException.expect(IllegalArgumentException.class);
        //when
        ByteUtils.getBit(42, 65);
    }

    @Test
    public void shouldIndicateAllLowBits()
    {
        for (int i = 0; i < 64; i++) {
            assertThat(ByteUtils.getBit(0, i)).isFalse();
        }
    }

    @Test
    public void shouldIndicateAllHighBits()
    {
        for (int i = 0; i < 63; i++) {
            assertThat(ByteUtils.getBit(Long.MAX_VALUE, i)).isTrue();
        }
    }

    @Test
    public void shouldIndicateFirstHighBits()
    {
        for (int i = 0; i < 64; i++) {
            assertThat(ByteUtils.getBit(15, i)).isEqualTo(i < 4);
        }
    }

    @Test
    public void shouldFailForNullByteArrays()
    {
        //expect
        expectedException.expect(NullPointerException.class);
        //when
        ByteUtils.concat(null, null);
    }

    @Test
    public void shouldNotFailMergingEmptyArrays()
    {
        //given
        //when
        final byte[] result = ByteUtils.concat(new byte[]{}, new byte[]{});
        //then
        assertThat(result).isEmpty();
    }

    @Test
    public void shouldMergeToEmptyArray()
    {
        //given
        //when
        final byte[] result = ByteUtils.concat(new byte[]{(byte) 1}, new byte[]{});
        //then
        assertThat(result).containsOnly((byte) 1);
    }

    @Test
    public void shouldMergeByteArrays()
    {
        //given
        //when
        final byte[] result = ByteUtils.concat(new byte[]{(byte) 1}, new byte[]{(byte) 2});
        //then
        assertThat(result).containsExactly((byte) 1, (byte) 2);
    }
}