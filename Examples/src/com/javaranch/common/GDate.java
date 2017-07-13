package com.javaranch.common;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Gregorian Date, international format (YYYY/MM/DD). <p>
 * <p/>
 * These dates are stored as Year, Month and Day. Not very memory efficient, but good for converting to strings, or
 * quickly extracting the day of the month. <p>
 * <p/>
 * See the complimentary class JDate for memory efficiency or doing date math. <p>
 * <p/>
 * This object uses the international YYYY/MM/DD format. See ADate for MM/DD/YYYY format. <p>
 * <p/>
 * - - - - - - - - - - - - - - - - - <p>
 * <p/>
 * Copyright (c) 1998-2004 Paul Wheaton <p>
 * <p/>
 * You are welcome to do whatever you want to with this source file provided that you maintain this comment fragment
 * (between the dashed lines). Modify it, change the package name, change the class name ... personal or business use
 * ...  sell it, share it ... add a copyright for the portions you add ... <p>
 * <p/>
 * My goal in giving this away and maintaining the copyright is to hopefully direct developers back to JavaRanch. <p>
 * <p/>
 * The original source can be found at <a href=http://www.javaranch.com>JavaRanch</a> <p>
 * <p/>
 * - - - - - - - - - - - - - - - - - <p>
 *
 * @author Paul Wheaton
 */

public class GDate
{

    private int y = 0;
    private int m = 0;
    private int d = 0;

    /**
     * Create a Gregorian Date object with a default of today's date. <p>
     */
    public GDate()
    {
        Calendar c = new GregorianCalendar();
        c.setTime( new Date() );
        y = c.get( GregorianCalendar.YEAR );
        m = c.get( GregorianCalendar.MONTH ) + 1;
        d = c.get( GregorianCalendar.DAY_OF_MONTH );
    }

    /**
     * Create a Gregorian Date object with a default of today's date. <p>
     */
    public GDate( TimeZone timeZone )
    {
        setToToday( timeZone );
    }

    /**
     * Create a Gregorian Date object with a specific date. <p>
     */
    public GDate( int year, int month, int day )
    {
        y = year;
        m = month;
        d = day;
    }

    /**
     * Create a Gregorian Date object with a specific Julian date. <p>
     */
    public GDate( JDate j )
    {
        this( j.getGDate() );
    }

    /**
     * Create a Gregorian Date object from another Gregorian Date object (GDate or ADate). <p>
     */
    public GDate( GDate d )
    {
        this.y = d.y;
        this.m = d.m;
        this.d = d.d;
    }

    /**
     * Create a Gregorian Date object with a loosely formatted text date. <p>
     * <p/>
     * The general format is YYYY/MM/DD. <p>
     * <p/>
     * Any type of delimiter can be used (slash, hyphen, space, etc.).  The month and date does not have to be two
     * digits in length.  The year does not have to be four digits in length, although "98" will be interpretted as
     * 0098, not 1998 or 2098.  <p>
     * <p/>
     * Invalid dates are accepted. <p>
     * <p/>
     * If you leave out the day of the month, the first is assumed. <p>
     * <p/>
     * If you leave out the month, January is assumed. <p>
     */
    public GDate( String textDate )
    {
        set( textDate );
    }

    public GDate( Date date )
    {
        Calendar c = new GregorianCalendar();
        c.setTime( date );
        y = c.get( GregorianCalendar.YEAR );
        m = c.get( GregorianCalendar.MONTH ) + 1;
        d = c.get( GregorianCalendar.DAY_OF_MONTH );
    }

    /**
     * Force the date based on the contents of a string. <p> The general format is YYYY/MM/DD. <p>
     * <p/>
     * Any type of delimiter can be used (slash, hyphen, space, etc.).  The month and date does not have to be two
     * digits in length.  The year does not have to be four digits in length, although "98" will be interpretted as
     * 0098, not 1998 or 2098.  <p>
     * <p/>
     * Invalid dates are accepted. <p>
     * <p/>
     * If you leave out the day of the month, the first is assumed. <p>
     * <p/>
     * If you leave out the month, January is assumed. <p>
     */
    public void set( String textDate )
    {
        Str s = new Str( textDate );
        for ( int i = 0; i < s.length() ; i++ )
        {
            if ( !Numbers.inRange( s.get( i ), '0', '9' ) )
            {
                s.set( i, ' ' );
            }
        }
        s.trim();
        s.removeDoubleSpaces();
        if ( s.length() > 0 )
        {
            y = Str.atoi( s.extractWord().toString() );
            m = 1;
            d = 1;
            if ( s.length() > 0 )
            {
                m = Str.atoi( s.extractWord().toString() );
            }
            if ( s.length() > 0 )
            {
                d = Str.atoi( s.extractWord().toString() );
            }
        }
    }

    /**
     * Set this object to reflect today's date. <p>
     */
    public void setToToday( TimeZone timeZone )
    {
        GregorianCalendar gc = new GregorianCalendar( timeZone );
        y = gc.get( GregorianCalendar.YEAR );
        m = gc.get( GregorianCalendar.MONTH ) + 1;
        d = gc.get( GregorianCalendar.DAY_OF_MONTH );
    }

    public void setYear( int year )
    {
        y = year;
    }

    /**
     * Expects values 1..12 although invalid values are accepted. <p>
     */
    public void setMonth( int month )
    {
        m = month;
    }

    /**
     * Expects values 1..31 although invalid values are accepted. <p>
     */
    public void setDay( int day )
    {
        d = day;
    }

    public int getYear()
    {
        return y;
    }

    public int getMonth()
    {
        return m;
    }

    private static final String[] monthName =
            { null ,
                    "January" ,
                    "February" ,
                    "March" ,
                    "April" ,
                    "May" ,
                    "June" ,
                    "July" ,
                    "August" ,
                    "September" ,
                    "October" ,
                    "November" ,
                    "December" };

    /**
     * Convert a number 1 through 12 to a String "January" through "December". <p>
     * <p/>
     * Passing in values outside of the 1 through 12 range will return an empty string. <p>
     */
    public static String getMonthString( int month )
    {
        String returnVal = "";
        if ( Numbers.inRange( month, 1, 12 ) )
        {
            returnVal = monthName[ month ];
        }
        return returnVal;
    }

    /**
     * Get the full month name. <p>
     * <p/>
     * If the current month is outside of the 1 through 12 range, an empty string will be returned. <p>
     * <p/>
     * Month names will be capitalized properly.  e.g. "January". <p>
     */
    public String getMonthString()
    {
        return getMonthString( m );
    }

    /**
     * Return the day of the month (1..31). <p>
     */
    public int getDay()
    {
        return d;
    }

    /**
     * Such as "05-01-2000". <p>
     *
     * @deprecated
     */
    public String getShortFormat()
    {
        StringBuffer buffy = new StringBuffer( String.valueOf( m ) );
        if ( buffy.length() == 1 )
        {
            buffy.insert( 0, '0' );
        }
        buffy.append( '-' );
        buffy.append( String.valueOf( d ) );
        if ( buffy.length() == 4 )
        {
            buffy.insert( 3, '0' );
        }
        buffy.append( '-' );
        buffy.append( String.valueOf( y ) );
        return buffy.toString();
    }

    /**
     * Such as "2000/05/01". <p>
     */
    public String getFixedFormat()
    {
        StringBuffer buffy = new StringBuffer( String.valueOf( y ) );
        while ( buffy.length() < 4 )
        {
            buffy.insert( 0, '0' );
        }
        buffy.append( '-' );
        buffy.append( String.valueOf( m ) );
        if ( buffy.length() == 6 )
        {
            buffy.insert( 5, '0' );
        }
        buffy.append( '-' );
        buffy.append( String.valueOf( d ) );
        if ( buffy.length() == 9 )
        {
            buffy.insert( 8, '0' );
        }
        return buffy.toString();
    }

    /**
     * Returns a date formatted like "2000/7/24". <p>
     */
    public static String toString( GDate g )
    {
        return g.y + "/" + g.m + '/' + g.d;
    }

    /**
     * Returns a date formatted like "2000/7/24". <p>
     */
    public String toString()
    {
        return toString( this );
    }

    /**
     * Returns a date formatted like "September 22, 1999". <p>
     */
    public String getLongFormat()
    {
        StringBuffer buffy = new StringBuffer( monthName[ m ] );
        buffy.append( ' ' );
        buffy.append( String.valueOf( d ) );
        buffy.append( ", " );
        buffy.append( String.valueOf( y ) );
        return buffy.toString();
    }

    public Timestamp getTimestamp()
    {
        GregorianCalendar gc = new GregorianCalendar( y, m - 1, d );
        return new Timestamp( gc.getTime().getTime() );
    }

    public Timestamp getTimestamp( TimeZone timeZone )
    {
        GregorianCalendar gc = new GregorianCalendar( timeZone );
        gc.set( y, m - 1, d );
        return new Timestamp( gc.getTime().getTime() );
    }

    /**
     * Get a copy of this object. <p>
     */
    public Object clone()
    {
        return new GDate( this );
    }

    private static GDate unitTest_today = null;

    static void setTodayForUnitTesting( GDate today )
    {
        unitTest_today = today;
    }

    public static GDate getToday( TimeZone timeZone )
    {
        GDate returnVal = null;
        if ( unitTest_today == null )
        {
            returnVal = new GDate( timeZone );
        }
        else
        {
            returnVal = unitTest_today;
        }
        return returnVal;
    }

    public boolean equals( Object obj )
    {
        boolean returnVal = false;
        if ( obj != null )
        {
            if ( obj instanceof GDate )
            {
                GDate g = (GDate)obj;
                returnVal = ( ( y == g.y ) && ( m == g.m ) && ( d == g.d ) );
            }
        }
        return returnVal;
    }

}

