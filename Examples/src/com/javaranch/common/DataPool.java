package com.javaranch.common ;

import java.util.* ;

    // todo - Needs JavaDoc

/**
 *     - - - - - - - - - - - - - - - - - <p>

    Copyright (c) 1999-2004 Paul Wheaton <p>

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
public class DataPool extends Thread
{
    private static final long timeout = 90 * 60 * 1000 ;  // 90 minutes worth of milliseconds
    private static final int waitForCleaning = 5 * 60 * 1000 ;  // 5 minutes worth of milliseconds

    private static final Map hash = new HashMap();
    private static Random rand = new Random();

    private DataPool(){}

    private static class Wrapper
    {
        private Object obj ;
        private long lastTouched = 0 ;

        Wrapper( Object obj )
        {
            this.obj = obj ;
            touch();
        }

        void touch()
        {
            lastTouched = System.currentTimeMillis();
        }

        Object getObject()
        {
            return obj ;
        }

        long getLastTouched()
        {
            return lastTouched ;
        }

    }

    /** Object in, token out - use the token to get the object back. <p>
     */
    public static String add( Object data )
    {
        long part1 = Math.abs( rand.nextInt() % 100000 );
        long part2 = ( System.currentTimeMillis() % 10000000 );
        String token = String.valueOf( ( part1 * 10000000 ) + part2 ) ;
        synchronized( hash )
        {
            hash.put( token , new Wrapper( data ) );
        }
        return token ;
    }

    public static Object get( String token )
    {
        Object returnVal = null ;
        synchronized( hash )
        {
            Wrapper w = (Wrapper)hash.get( token );
            if ( w != null )
            {
                w.touch();
                returnVal = w.getObject();
            }
        }
        return returnVal ;
    }

    public static void delete( String token )
    {
        synchronized( hash )
        {
            hash.remove( token );
        }
    }


    // periodically remove stuff.
    public void run()
    {
        while ( true )
        {
            Time.delay( waitForCleaning );
            long killTime = System.currentTimeMillis() - timeout ;
            synchronized( hash )
            {
                Object[] tokens = hash.keySet().toArray();
                for( int i = 0 ; i < tokens.length ; i++ )
                {
                    Object token = tokens[ i ];
                    Wrapper w = (Wrapper)hash.get( token );
                    if ( w.getLastTouched() < killTime )
                    {
                        hash.remove( token );
                    }
                }
            }
        }
    }

    static
    {
        (new DataPool()).start();
    }

}
