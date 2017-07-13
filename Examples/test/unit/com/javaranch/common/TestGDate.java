package com.javaranch.common ;

import junit.framework.TestCase ;

public class TestGDate extends TestCase
{
    public void testConstructors()
    {
        GDate thisDay = new GDate();
        JDate testDay = new JDate( thisDay );
        GDate day = new GDate( testDay );
        assertEquals( day, thisDay );

        GDate anotherDay = new GDate( 1991, 9, 23 );
        assertEquals( anotherDay.getYear(), 1991 );
        assertEquals( anotherDay.getMonth(), 9 );
        assertEquals( anotherDay.getDay(), 23 );

        GDate looseDay = new GDate( "1992 3 16" );
        assertEquals( looseDay.getYear(), 1992 );
        assertEquals( looseDay.getMonth(), 3 );
        assertEquals( looseDay.getDay(), 16 );
        looseDay = new GDate( "1991-9-23" );
        assertEquals( looseDay, anotherDay );
        looseDay = new GDate( "1991*09*23" );
        assertEquals( looseDay, anotherDay );
        looseDay = new GDate( "55" );
        assertEquals( looseDay, new GDate( 55, 1, 1 ) );

        GDate copiedDay = new GDate( looseDay );
        assertEquals( copiedDay, looseDay );
    }

}
