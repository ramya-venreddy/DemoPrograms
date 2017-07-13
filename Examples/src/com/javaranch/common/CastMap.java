package com.javaranch.common ;

import java.util.* ;

/** This hashtable gives some extra casting help. <p>

    - - - - - - - - - - - - - - - - - <p>

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
public class CastMap extends HashMap
{

    /**
     *
     * @param key - the key whose associated value is to be returned.
     * @return the value,  cast to a String, to which this map maps the specified key,
     *         or the String "null" if the map contains no mapping for this key.
     */
    public String getString( String key )
    {
        return (String)get( key );
    }

    /**
     *
     * @param key - the key whose associated value is to be returned.
     * @return the value,  cast to a String and trimmed of white space, to which this map maps the specified key,
     *         or the String "null" if the map contains no mapping for this key.
     */
    public String getTrimmedString( String key )
    {
        String returnVal = null ;
        String s = (String)get( key );
        if ( s != null )
        {
            returnVal = s.trim();
        }
        return returnVal ;
    }

}
