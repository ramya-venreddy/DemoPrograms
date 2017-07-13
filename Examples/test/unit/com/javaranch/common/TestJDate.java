package com.javaranch.common;

import junit.framework.TestCase;

public class TestJDate extends TestCase
{

    // Convert a julian date to a gregorian date and then back to a julian date.
    // The two julian dates should be the same.  Do this test for 10,000 consecutive dates.
    public void testConvertions()
    {
        GDate today = new GDate();
        JDate j = new JDate( today );
        j.adjust( -10000 );
        for ( int i = 0; i < 10000 ; i++ )
        {
            int orig = j.get();
            GDate g = j.getGDate();
            j.set( g );
            assertEquals( j.get(), orig );
            j.set( orig + 1 );
        }
    }

}
