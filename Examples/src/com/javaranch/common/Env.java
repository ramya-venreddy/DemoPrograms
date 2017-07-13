package com.javaranch.common ;

import java.util.* ;
import java.io.* ;

/** A class that loads an environment file and makes the data of that file available as key-value pairs. <p>


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

public class Env
{

    private static Properties props = null ;

    private static void init()
    {
        if ( props == null )
        {
            try
            {
                String filename = System.getProperty( "ENV" );
                if ( filename == null )
                {
                    throw new Exception( "" );
                }
                props = new Properties();
                props.load( new FileInputStream( filename ) );
            }
            catch( Exception e )
            {
                throw new RuntimeException( "could not load environment table" );
            }
        }
    }

    public static String get( String key )
    {
        String returnVal = null ;
        if ( key != null )
        {
            if ( key.length() > 0 )
            {
                init();
                returnVal = props.getProperty( key );
            }
        }
        return returnVal ;
    }

    public static Enumeration getKeys()
    {
        init();
        return props.keys();
    }

}



