package com.javaranch.common;

import java.util.*;

/** Date is tracked as an integer. <p>

 Technically, a Julian calendar is the calendar designed by
 Julius Caesar in 46 B.C.  It has months and leap years much like
 the gregorian calendar we use now (designed by Pope Gregory the
 13th in 1582).  But for several years, computer geeks have counted
 the number of days since the beginning of a year or another point
 in time and referred to this as a "Julian Date".  From here on
 out, any reference to a "julian date" means "the number of days
 since January 1, 0000".  This class will behave as if the
 Gregorian calendar leap year system has been in place since
 January 1, 0000.<p>

 This class will behave as both a Julian date and a Gregorian date
 although the actual date will be stored as a single integer.<p>


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

public class JDate implements java.io.Serializable
{

    private int j; //  number of days since Jan 1, 0000

    /** Create a new JDate object with a default of zero. <p>
     */
    public JDate()
    {
        j = 0;
    }

    /** Create a new JDate object specified as the number of days since January 1, 0000. <p>
     */
    public JDate( int julianDate )
    {
        j = julianDate;
    }

    /** Create a new JDate object equal to another JDate object. <p>
     */
    public JDate( JDate julianDate )
    {
        j = julianDate.j;
    }

    /** Create a new JDate object based on a year, month and day. <p>
     */
    public JDate( int year , int month , int day )
    {
        j = convertGregorianToJulian( year , month , day );
    }

    /** Create a new JDate object equal to a GDate object. <p>
     */
    public JDate( GDate g )
    {
        j = convertGregorianToJulian( g.getYear() , g.getMonth() , g.getDay() );
    }

    private static boolean isLeapYear( int year )
    {
        boolean returnVal = false;
        if ( year % 400 == 0 )
        {
            // this is definitely a leap year
            returnVal = true;
        }
        else
            if ( year % 4 == 0 )
            {
                // this is probably a leap year
                if ( year % 100 == 0 )
                {
                    // this is definitely not a leap year
                    // do nothing
                }
                else
                {
                    // this is definitely a leap year
                    returnVal = true;
                }
            }
        return returnVal;
    }

    private static final int daysInFourYears = 1461;   // number of days in four years
    private static final int daysInCentury = 36524;  // number of days in a century
    private static final int daysInFourCenturies = 146097; // number of days in four centuries

    private static final int[] daysInMonth =
            {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    /** Report if the given date is valid or not. <p>
     */
    public static boolean isValid( GDate date )
    {
        boolean returnVal = false;
        int year = date.getYear();
        if ( Numbers.inRange( year , 1200 , 2200 ) )
        {
            int month = date.getMonth();
            if ( Numbers.inRange( month , 1 , 12 ) )
            {
                int day = date.getDay();
                if ( day >= 1 )
                {
                    if ( day <= daysInMonth[ month - 1 ] )
                    {
                        returnVal = true;
                    }
                    else
                        if ( ( month == 2 ) && ( day == 29 ) && isLeapYear( year ) )
                        {
                            returnVal = true;
                        }
                }
            }
        }
        return returnVal;
    }

    private static GDate convertJulianToGregorian( int j )
    {
        // j is a disposable variable I'll chip at to find the gregorian values
        int numFC = j / daysInFourCenturies;  // number of "four century" chunks
        int year = numFC * 400;
        j -= ( daysInFourCenturies * numFC );
        int numCenturies = 0;
        if ( j > ( daysInCentury + 1 ) )
        {
            j -= ( daysInCentury + 1 );
            year += 100;
            numCenturies = j / daysInCentury;
            year += ( numCenturies * 100 );
            j -= ( numCenturies * daysInCentury );
            numCenturies++;
        }
        if ( ( numCenturies > 0 ) && ( j > ( daysInFourYears - 1 ) ) )
        {
            j -= ( daysInFourYears - 1 );
            year += 4;
        }
        int numFY = j / daysInFourYears; // number of "four year" chunks
        year += ( numFY * 4 );
        j -= ( numFY * daysInFourYears );
        int month = 0;
        int day = 0;
        if ( isLeapYear( year ) )
        {
            if ( j >= 366 )
            {
                j -= 366;
                year++;
            }
            else
                if ( j == 59 ) // Jan 1, would have j = 0, Feb 29 would have j=59
                {
                    month = 2;
                    day = 29;
                }
                else
                    if ( j > 59 )
                    {
                        j--;
                    }
        }
        if ( month == 0 ) // otherwise, g was set as a leap day
        {
            while ( j >= 365 )
            {
                j -= 365;
                year++;
            }
            while ( daysInMonth[ month ] <= j )
            {
                j -= daysInMonth[ month ];
                month++;
            }
            month++;
            day = j + 1;
        }
        return new GDate( year , month , day );
    }

    private static int convertGregorianToJulian( int year , int month , int day )
    {
        boolean leap = isLeapYear( year - ( year % 4 ) );
        int numFC = year / 400; // the number of "four century" chunks
        year -= numFC * 400;
        int j = numFC * daysInFourCenturies; // the value to be returned
        int numCenturies = year / 100;
        if ( numCenturies > 0 )
        {
            year -= numCenturies * 100;
            j += ( numCenturies * daysInCentury ) + 1;  // the first century has one more day
        }
        int numFY = year / 4; // the number of "four year" chunks
        year -= numFY * 4;
        if ( ( numCenturies != 0 ) && ( numFY > 0 ) )
        {
            // generally, the first four year chunk doesn't have a leap year
            j += 1460;
            numFY--;
        }
        j += numFY * daysInFourYears;
        if ( leap )
        {
            if ( year > 0 )
            {
                j += 366;
                year--;
            }
            else
                if ( month > 2 )
                {
                    j++;
                }
        }
        while ( year > 0 )
        {
            j += 365;
            year--;
        }
        if ( month > 12 )
        {
            month = 12;
        }
        for ( int i = 0 ; i < month - 1 ; i++ )
        {
            j += daysInMonth[ i ];
        }
        j += ( day - 1 );
        return j;
    }

    private static int convertGregorianToJulian( GDate g )
    {
        return convertGregorianToJulian( g.getYear() , g.getMonth() , g.getDay() );
    }

    /** Set this object to be equal to an integer. <p>
     */
    public void set( int newJulianDate )
    {
        j = newJulianDate;
    }

    /** Set this object to be equal to another JDate object. <p>
     */
    public void set( JDate newJulianDate )
    {
        j = newJulianDate.j;
    }

    /** Set this object to be equal to a GDate object. <p>
     */
    public void set( GDate g )
    {
        j = convertGregorianToJulian( g );
    }

    /** Change just the year - the month and day values will be unmodified. <p>
     */
    public void setYear( int newYear )
    {
        GDate g = convertJulianToGregorian( j );
        g.setYear( newYear );
        j = convertGregorianToJulian( g );
    }

    /** Change just the month - the year and day values will be unmodified. <p>
     */
    public void setMonth( int newMonth )
    {
        GDate g = convertJulianToGregorian( j );
        g.setMonth( newMonth );
        j = convertGregorianToJulian( g );
    }

    /** Change just the day - the year and month values will be unmodified. <p>
     */
    public void setDay( int newDay )
    {
        GDate g = convertJulianToGregorian( j );
        g.setDay( newDay );
        j = convertGregorianToJulian( g );
    }

    /** Get the integer representation. <p>
     */
    public int get()
    {
        return j;
    }

    /** Extract the year. <p>
     */
    public int getYear()
    {
        GDate g = convertJulianToGregorian( j );
        return g.getYear();
    }

    /** Extract the month. <p>
     */
    public int getMonth()
    {
        GDate g = convertJulianToGregorian( j );
        return g.getMonth();
    }

    /** Get the full month name:  "January", "February", "March", etc. <p>
     */
    public String getMonthString()
    {
        return GDate.getMonthString( getMonth() );
    }

    /** Get the day of the month (not the day of the week). <p>
     */
    public int getDay()
    {
        GDate g = convertJulianToGregorian( j );
        return g.getDay();
    }

    /** Get the day of the week (not the day of the month). <p>

     Zero is monday. <p>
     */
    public int getDOW()
    {
        return ( ( j - 2 ) % 7 );
    }

    private static final String[] dowName =
            {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

    /** Get the full day of the week name:  "Monday", "Tuesday", "Wednesday", etc. <p>
     */
    public String getDOWString()
    {
        return dowName[ getDOW() ];
    }

    /** Convert to a GDate object. <p>
     */
    public GDate getGDate()
    {
        return convertJulianToGregorian( j );
    }

    private static final long millisecondsPerDay = 24 * 60 * 60 * 1000;
    // 24 hours in a day, 60 minutes per hour, 60 seconds per minute, 1000 milliseconds per second

    private static final int dateEpoch = convertGregorianToJulian( 1970 , 1 , 1 );

    /** Convert to a Date object. <p>

     Since a Date object has a time element, the time is set to noon. <p>
     */
    public Date getDate()
    {
        long date = ( j - dateEpoch ) * millisecondsPerDay;
        long noon = millisecondsPerDay / 2;
        return new Date( date + noon );
    }

    /** Get something like "05-01-1999" for May 5th, 1999. <p>
     @deprecated
     */
    public String getShortFormat()
    {
        GDate g = convertJulianToGregorian( j );
        return g.getFixedFormat();
    }

    /** Get something like "Wednesday, September 22, 1999". <p>
     */
    public String getLongFormat()
    {
        StringBuffer buffy = new StringBuffer( getDOWString() );
        buffy.append( ", " );
        GDate g = convertJulianToGregorian( j );
        buffy.append( g.getLongFormat() );
        return buffy.toString();
    }

    /** Compare this object to an integer. <p>
     */
    public boolean equals( int julianDate )
    {
        return ( julianDate == j );
    }

    public int hashCode()
    {
        return j;
    }

    public boolean equals( Object obj )
    {
        boolean returnVal = false;
        if ( obj != null )
        {
            if ( obj instanceof JDate )
            {
                JDate jDate = (JDate) obj;
                returnVal = ( j == jDate.j );
            }
            else
                if ( obj instanceof GDate )
                {
                    GDate gDate = (GDate) obj;
                    returnVal = ( j == convertGregorianToJulian( gDate ) );
                }
        }
        return returnVal;
    }

    /** Increment the date by one. <p>
     */
    public void inc()
    {
        j++;
    }

    /** Increment the date by the given value. <p>

     Negative values will decrement. <p>
     */
    public void inc( int adjust )
    {
        j += adjust;
    }

    /** Decrement the date by one. <p>
     */
    public void dec()
    {
        j--;
    }

    /** Adjust the date by the given value. <p>

     Positive values will increment. <p>

     Negative values will decrement. <p>
     */
    public void adjust( int adj )
    {
        j += adj;
    }

    /** Returns the same thing as getLongFormat(). <p>
     */
    public String toString()
    {
        return getLongFormat();
    }

}



