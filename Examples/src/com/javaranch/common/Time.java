package com.javaranch.common ;


/** Contains a collection of static methods related to Time. <p>


    - - - - - - - - - - - - - - - - - <p>

    Copyright (c) 1998-2004 Paul Wheaton <p>

    You are welcome to do whatever you want to with this source file provided
    that you maintain this comment fragment (between the dashed lines).
    Modify it, change the package name, change the class name ... personal or business
    use ...  sell it, share it ... add a copyright for the portions you add ... <p>

    My goal in giving this away and maintaining the copyright is to hopefully direct
    developers back to JavaRanch. <p>

    The original source can be found at <a href=http://www.javaranch.com>JavaRanch</a> <p>

    - - - - - - - - - - - - - - - - - <p>

    @author Paul Wheaton
*/

public class Time
{

    private Time(){} // just to make sure someone doesn't try to instantiate this.

    /** Wait for a bit. <p>

        @param millisecs - How long to wait in milliseconds. <p>

    */
    public static void delay( int millisecs )
    {
        try
        {
            Thread.currentThread().sleep( millisecs );
        }
        catch( InterruptedException e ){}
    }


    /** send test to System.out.println and then delay for half a sec.
    */
    public static void debug( String text )
    {
        System.out.println( text );
        delay( 500 );
    }

}
