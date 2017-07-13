package com.javaranch.common;

import junit.framework.TestCase;

public class TestNumbers extends TestCase
{

    public void test_endian() throws Exception
    {
        assertEquals( (short)0x0100 , Numbers.endian( (short)0x0001 ) );
        assertEquals( (short)0x0102 , Numbers.endian( (short)0x0201 ) );
        assertEquals( (short)0x1122 , Numbers.endian( (short)0x2211 ) );
        assertEquals( (short)0xff00 , Numbers.endian( (short)0x00ff ) );
        assertEquals( (short)0x00ff , Numbers.endian( (short)0xff00 ) );
        assertEquals( (short)0xaabb , Numbers.endian( (short)0xbbaa ) );
    }

}
