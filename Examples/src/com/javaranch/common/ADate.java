package com.javaranch.common;


import java.sql.*;
import java.util.*;


/** Gregorian Date, american format (MM/DD/YYYY). <p>

 These dates are stored as Year, Month and Day. Not very memory efficient, but
 good for converting to strings, or quickly extracting the day of the month. <p>

 See the complimentary class JDate for memory efficiency or doing date math. <p>

 This object uses the american MM/DD/YYYY format. See GDate for YYYY/MM/DD format. <p>

 @author Paul Wheaton
 */
public class ADate extends GDate
{

    /** Create a Gregorian Date object with a default of today's date. <p>
     */
    public ADate( TimeZone timeZone )
    {
        super( timeZone );
    }

    /** Create a Gregorian Date object with a default of today's date. <p>
     */
    public ADate()
    {
    }

    /** Create a Gregorian Date object with a specific date. <p>
     */
    public ADate( int year , int month , int day )
    {
        super( year , month , day );
    }

    /** Create a Gregorian Date object with a specific Julian date. <p>
     */
    public ADate( JDate j )
    {
        this( j.getGDate() );
    }

    /** Create a Gregorian Date object from another Gregorian Date object (GDate or ADate). <p>
     */
    public ADate( GDate d )
    {
        super( d );
    }

    /** Create a Gregorian Date object based on today's date.
     */
    public ADate( Timestamp t )
    {
        super( t );
    }

    /** Create a Gregorian Date object with a loosely formatted text date. <p>

     The general format is MM/DD/YYYY. <p>

     Any type of delimiter can be used (slash, hyphen, space, etc.).  The month and date
     does not have to be two digits in length.  The year does not have to be four digits in
     length, although "98" will be interpretted as 0098, not 1998 or 2098.  <p>

     Invalid dates are accepted. <p>

     */
    public ADate( String textDate )
    {
        super( textDate ); // will call our set() eventually
    }

    /** Force the date based on the contents of a string. <p>
     The general format is DD/MM/YYYY. <p>

     Any type of delimiter can be used (slash, hyphen, space, etc.).  The month and date
     does not have to be two digits in length.  The year does not have to be four digits in
     length, although "98" will be interpretted as 0098, not 1998 or 2098.  <p>

     Invalid dates are accepted. <p>

     If you leave out the day of the month, the first is assumed. <p>

     If you leave out the month, January is assumed. <p>

     */
    public void set( String textDate )
    {
        Str s = new Str( textDate );
        for ( int i = 0 ; i < s.length() ; i++ )
        {
            if ( ! Numbers.inRange( s.get( i ) , '0' , '9' ) )
            {
                s.set( i , ' ' );
            }
        }
        s.trim();
        s.removeDoubleSpaces();
        if ( s.length() > 0 )
        {
            setMonth( Str.atoi( s.extractWord().toString() ) );

            int d = 1;
            if ( s.length() > 0 )
            {
                d = Str.atoi( s.extractWord().toString() );
            }
            setDay( d );

            int y = 0;
            if ( s.length() > 0 )
            {
                y = Str.atoi( s.extractWord().toString() );
            }
            setYear( y );
        }
    }

    /**  Such as "05/01/2000". <p>
     */
    public String getFixedFormat()
    {
        StringBuffer buffy = new StringBuffer( String.valueOf( getMonth() ) );
        if ( buffy.length() == 1 )
        {
            buffy.insert( 0 , '0' );
        }
        buffy.append( '-' );
        buffy.append( String.valueOf( getDay() ) );
        if ( buffy.length() == 4 )
        {
            buffy.insert( 3 , '0' );
        }
        buffy.append( '-' );
        buffy.append( String.valueOf( getYear() ) );
        return buffy.toString();
    }

    /** Returns a date formatted like "7/24/2000". <p>
     */
    public static String toString( GDate g )
    {
        return g.getMonth() + "/" + g.getDay() + '/' + g.getYear();
    }

    /** Returns a date formatted like "7/24/2000". <p>
     */
    public String toString()
    {
        return toString( this );
    }

    /** Get a copy of this object. <p>
     */
    public Object clone()
    {
        return new ADate( this );
    }

    public static Timestamp getTimestamp( String textDate )
    {
        return new ADate( textDate ).getTimestamp();
    }

}

