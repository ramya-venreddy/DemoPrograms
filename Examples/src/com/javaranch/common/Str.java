package com.javaranch.common;

import java.text.*;
import java.util.Date;
import java.sql.*;

/** A robust string processing class. <p>

 The Java String and StringBuffer classes are final.  So it is not
 possible to inherit all of the functionality of one of those classes and enhance it
 with a few more methods. <p>

 This class attempts to provide all of the functionality found in both String and StringBuffer,
 plus provide a wide collection of additional functionality. <p>

 This class internally functions more like StringBuffer than String.  If a Str object
 is appended and needs to grow beyond its current size, it will do so without programmer
 intervention.  It also keeps a little extra buffer of size to accomodate small amounts
 of growth without needing memory re-allocation. <p>


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
public class Str
{
    private char[] buffy;   // the string characters
    private int len = 0;    // current length of the string
    private int extra = 50; // how much extra to grow when growth is needed

    /** Set the object to have an initial capacity. <p>

     @param initialSize How many characters the object can initially hold.<p>
     */
    public Str( int initialSize )
    {
        buffy = new char[ initialSize ];
    }

    /** Create an object with a specific initial capacity and to be able to grow by a specific increment. <p>

     @param initialSize How many characters the object can initially hold. <p>
     @param initialExtra How many characters the object will grow by when more memory is needed. <p>
     */
    public Str( int initialSize , int initialExtra )
    {
        buffy = new char[ initialSize ];
        extra = initialExtra;
    }

    /** The object has memory allocated to store an average size string. <p>
     */
    public Str()
    {
        buffy = new char[ extra ];
    }

    /** The object is inititialized to have an exact copy of the character array.  Enough memory is allocated for some growth. <p>

     @param c the character array to copy. <p>
     */
    public Str( char[] c )
    {
        len = c.length;
        buffy = new char[ len + extra ];
        System.arraycopy( c, 0, buffy, 0, len );
    }

    /** A new object is created that is an exact copy of the provided Str object. <p>

     Enough memory is allocated for some growth. <p>

     @param s The Str object to copy. <p>
     */
    public Str( Str s )
    {
        set( s );
    }

    /** Force the contents of this object to be the same as another Str object. <p>

     @param s The Str object to copy. <p>
     */
    public void set( Str s )
    {
        len = s.len;
        extra = s.extra;
        buffy = new char[ s.buffy.length ];
        System.arraycopy( s.buffy, 0, buffy, 0, len );
    }

    /** A new object is created that contains a copy of the provided String object.  Enough memory is allocated for some growth. <p>

     @param s The String object to copy. <p>
     */
    public Str( String s )
    {
        if ( s == null )
        {
            buffy = new char[ extra ];
        }
        else
        {
            set( s );
        }
    }

    /** Force the contents of this object to be the same as a String object. <p>

     @param s The String object to copy. <p>
     */
    public void set( String s )
    {
        len = s.length();
        buffy = new char[ len + extra ];
        for ( int i = 0; i < len ; i++ )
        {
            buffy[ i ] = s.charAt( i );
        }
    }

    /** Get the current length of your string - not the same as the amount of memory allocated. <p>

     Works just like the length() methods in String and StringBuffer. <p>

     @return The current string length. <p>
     */
    public int length()
    {
        return len;
    }

    /** Force the length of your string. <p>

     If you want to empty your string, use setLength(0); <p>

     If you specify a number that is longer than the current string length, the new characters will be null characters. <p>

     @param newLen what you want the new length of your string to be. <p>
     */
    public void setLength( int newLen )
    {
        if ( newLen > buffy.length )
        {
            setCapacity( newLen + extra );
        }
        if ( newLen > len )
        {
            // fill new chars with zero
            while ( len < newLen )
            {
                buffy[ len ] = (char)0;
                len++;
            }
        }
        else
        {
            len = newLen;
        }
    }

    /** A string comparison method that returns a numeric result. <p>

     A lot like the ANSI strcmp() function. <p>

     @param s The Str object to compare to. <p>
     @return 0 If this is equal to s, a negative value if this is less than s, a positive value if this is greater than s. <p>
     */
    public int compareTo( Str s )
    {
        int returnVal = 0;
        int minComp = Math.min( len , s.len );
        int i = 0;
        while ( ( i < minComp ) && ( buffy[ i ] == s.buffy[ i ] ) )
        {
            i++;
        }
        if ( i == minComp )
        {
            // both strings are equal up to the length of the shortest string.
            // therefore, the longer string is greater
            returnVal = len - s.len;
            // if the two strings have equal length, zero is returned
        }
        else
        {
            // the character being examined at i is the first different character
            returnVal = buffy[ i ] - s.buffy[ i ];
        }
        return returnVal;
    }

    /** A string comparison method that returns a numeric result. <p>

     A lot like the ANSI strcmp() function. <p>

     @param s the String object to compare to. <p>
     @return 0 if this is equal to s, a negative value if this is less than s, a positive value if this is greater than s. <p>
     */
    public int compareTo( String s )
    {
        int returnVal = 0;
        int minComp = Math.min( len , s.length() );
        int i = 0;
        while ( ( i < minComp ) && ( buffy[ i ] == s.charAt( i ) ) )
        {
            i++;
        }
        if ( i == minComp )
        {
            // both strings are equal up to the length of the shortest string.
            // therefore, the longer string is greater
            returnVal = len - s.length();
            // if the two strings have equal length, zero is returned
        }
        else
        {
            // the character being examined at i is the first different character
            returnVal = buffy[ i ] - s.charAt( i );
        }
        return returnVal;
    }

    /** Test for equality between your string and the string in s. <p>

     This method is case sensitive. <p>

     @param s The Str object to compare to. <p>
     @return True if s is identical. <p>
     */
    public boolean eq( Str s )
    {
        return ( compareTo( s ) == 0 );
    }

    /** Test for equality between your string and the string in s. <p>

     This method is case sensitive. <p>

     @param s The String object to compare to. <p>
     @return True if s is identical. <p>
     */
    public boolean eq( String s )
    {
        return ( compareTo( s ) == 0 );
    }

    /** Test for "not equal" between your string and the string in s. <p>

     This method is case sensitive. <p>

     @param s The Str object to compare to. <p>
     @return False if s is identical. <p>
     */
    public boolean ne( Str s )
    {
        return !( compareTo( s ) == 0 );
    }

    /** Test for "not equal" between your string and the string in s. <p>

     This method is case sensitive. <p>

     @param s The String object to compare to. <p>
     @return False if s is identical. <p>
     */
    public boolean ne( String s )
    {
        return !( compareTo( s ) == 0 );
    }

    /** Test for "less than" between your string and the string in s. <p>

     This method is case sensitive. <p>

     @param s The Str object to compare to. <p>
     @return True if your string is "less than" s. <p>
     */
    public boolean lt( Str s )
    {
        return ( compareTo( s ) < 0 );
    }

    /** Test for "less than" between your string and the string in s. <p>

     This method is case sensitive. <p>

     @param s The String object to compare to. <p>
     @return True if your string is "less than" s. <p>
     */
    public boolean lt( String s )
    {
        return ( compareTo( s ) < 0 );
    }

    /** Test for "less than or equal to" between your string and the string in s. <p>

     This method is case sensitive. <p>

     @param s The Str object to compare to. <p>
     @return True if your string is "less than or equal to" s. <p>
     */
    public boolean le( Str s )
    {
        return ( compareTo( s ) <= 0 );
    }

    /** Test for "less than or equal to" between your string and the string in s. <p>

     This method is case sensitive. <p>

     @param s The String object to compare to. <p>
     @return True if your string is "less than or equal to" s. <p>
     */
    public boolean le( String s )
    {
        return ( compareTo( s ) <= 0 );
    }

    /** Test for "greater than or equal to" between your string and the string in s. <p>

     This method is case sensitive. <p>

     @param s The Str object to compare to. <p>
     @return True if your string is "greater than or equal to" s. <p>
     */
    public boolean ge( Str s )
    {
        return ( compareTo( s ) >= 0 );
    }

    /** Test for "greater than or equal to" between your string and the string in s. <p>

     This method is case sensitive. <p>

     @param s The String object to compare to. <p>
     @return True if your string is "greater than or equal to" s. <p>
     */
    public boolean ge( String s )
    {
        return ( compareTo( s ) >= 0 );
    }

    /** Test for "greater than" between your string and the string in s. <p>

     This method is case sensitive. <p>

     @param s The Str object to compare to. <p>
     @return True if your string is "greater than" s. <p>
     */
    public boolean gt( Str s )
    {
        return ( compareTo( s ) > 0 );
    }

    /** Test for "greater than" between your string and the string in s. <p>

     This method is case sensitive. <p>

     @param s The String object to compare to. <p>
     @return True if your string is "greater than" s. <p>
     */
    public boolean gt( String s )
    {
        return ( compareTo( s ) > 0 );
    }

    /** Set how much extra to grow when growth is needed. <p>

     Smaller values will usually save memory, but frequent reallocation may take a lot of time. <p>

     @param howMuch The number of extra characters to allocate when memory reallocation is required.  Values must be greater than zero. <p>
     */
    public void setExtra( int howMuch )
    {
        if ( howMuch > 0 )
        {
            extra = howMuch;
        }
    }

    /** Get the number of characters this object can hold without doing a reallocation. <p>

     @return The number of characters this object can currently hold without reallocation. <p>
     */
    public int getCapacity()
    {
        return buffy.length;
    }

    /** Force this object to be able to hold a specific number of characters without reallocation. <p>

     Any attempt to make the string grow bigger than this size will succeed. <p>

     Setting the capacity to be smaller than the current string size will result in trailing characters being clipped. <p>

     @param howMuch How many characters will be the new capacity. <p>
     */
    public void setCapacity( int howMuch )
    {
        if ( howMuch < len )
        {
            len = howMuch;
        }
        char[] newBuffy = new char[ howMuch ];
        System.arraycopy( buffy, 0, newBuffy, 0, len );
        buffy = newBuffy;
    }

    /** Retrieve a copy of one character. <p>

     @param index Which character (0 is the first character). <p>
     @return The retrieved character.  Returns a null character if index is outside of 0..length. <p>
     */
    public char get( int index )
    {
        char returnValue = (char)0;
        if ( Numbers.inRange( index , 0 , len - 1 ) )
        {
            returnValue = buffy[ index ];
        }
        return returnValue;
    }

    /** Retrieve a copy of the last character. <p>

     @return The retrieved character.  Returns a null character if the string is empty. <p>
     */
    public char getLast()
    {
        return get( len - 1 );
    }

    /** Used for compatibility with String and StringBuffer. <p>

     see get(). <p>
     */
    public char charAt( int index )
    {
        return get( index );
    }

    /** Retrieve a copy of a substring. <p>

     @param index The character of the substring. <p>
     @param length The length of the substring.  Specifying a substring beyond the end of the string will return as much of the string as possible. <p>
     @return A Str object containing the substring. <p>
     */
    public Str get( int index , int length )
    {
        Str s = new Str( length + extra );
        if ( index < 0 )
        {
            index = 0;
        }
        if ( index < len )
        {
            if ( index + length > len )
            {
                length = len - index;
            }
            for ( int i = 0; i < length ; i++ )
            {
                s.buffy[ i ] = get( index + i );
            }
            s.len = length;
        }
        return s;
    }

    /** Provided for compatibility with String and StringBuffer and the case mixing many people expect. <p>
     */
    public Str subString( int index , int length )
    {
        return get( index , length );
    }

    /** Provided for compatibility with String and StringBuffer and the case mixing Sun provides. <p>
     */
    public Str substring( int index , int length )
    {
        return get( index , length );
    }

    /** Get all of the substring that occurs before a particular character. <p>

     @param index The character that marks the end of the substring.  This character is not included in the substring. <p>
     @return A Str object containing the substring. <p>
     */
    public Str before( int index )
    {
        return get( 0 , index );
    }

    /** Get all of the substring that occurs through a particular character. <p>

     @param index The character that marks the end of the substring.  This character is included in the substring. <p>
     @return A Str object containing the substring. <p>
     */
    public Str through( int index )
    {
        return get( 0 , index );
    }

    /** Get all of the substring that occurs after a particular character. <p>

     @param index The character that marks the beginning of the substring.  This character is not included in the substring. <p>
     @return A Str object containing the substring. <p>
     */
    public Str after( int index )
    {
        return get( index + 1 , ( len - index ) - 1 );
    }

    /** Get all of the substring that occurs from a particular character to the end of the string. <p>

     @param index The character that marks the beginning of the substring.  This character is included in the substring. <p>
     @return A Str object containing the substring. <p>
     */
    public Str from( int index )
    {
        return get( index , len - index );
    }

    /** Generate a character array that contains a copy of the string. <p>

     @return A new char array. <p>
     */
    public char[] toCharArray()
    {
        char[] b = new char[ len ];
        System.arraycopy( buffy, 0, b, 0, len );
        return b;
    }

    /** Set one character in the string. <p>

     @param index Which character to set.  If index references a character beyond the last character of the string, the string will be lengthened and the last character will be set appropriately (undefined characters will be set to a null character). <p>
     @param c The character to be placed at index. <p>
     */
    public void set( int index , char c )
    {
        if ( index >= 0 )
        {
            if ( index >= len )
            {
                setLength( index + 1 );
            }
            buffy[ index ] = c;
        }
    }

    /** Provided for compatibility with StringBuffer. <p>
     */
    public void setCharAt( int index , char c )
    {
        set( index , c );
    }


    /** Add one character to the end of the string. <p>

     @param c The character to append to the end of the string. <p>
     */
    public void append( char c )
    {
        testBuffyLen( len + 1 );
        buffy[ len ] = c;
        len++;
    }

    /** Append a string on to the end of your string. <p>

     @param s The Str object to be appended. <p>
     */
    public void append( Str s )
    {
        int newLen = len + s.len;
        testBuffyLen( newLen );
        for ( int i = 0; i < s.len ; i++ )
        {
            buffy[ len + i ] = s.buffy[ i ];
        }
        len = newLen;
    }

    /** Append a string on to the end of your string. <p>

     @param s The String object to be appended. <p>
     */
    public void append( String s )
    {
        if ( s != null )
        {
            int newLen = len + s.length();
            testBuffyLen( newLen );
            for ( int i = 0; i < s.length() ; i++ )
            {
                buffy[ len + i ] = s.charAt( i );
            }
            len = newLen;
        }
    }

    /** Insert a character immediately before a particular character. <p>

     @param c The character to be inserted. <p>
     @param index Where to insert c.  The character currently at index will me moved to the right. <p>
     */
    public void insert( char c , int index )
    {
        if ( index > len )
        {
            index = len;
        }
        testBuffyLen( len + 1 );

        // move chars from index on one to the right
        for ( int i = len - 1; i >= index ; i-- )
        {
            buffy[ i + 1 ] = buffy[ i ];
        }

        buffy[ index ] = c;
        len++;
    }

    // used by several other methods to make room for an insertion
    private void insertJunk( int howMany , int index )
    {
        int newLen = len + howMany;
        testBuffyLen( newLen );

        // move chars from index on to the right
        for ( int i = len - 1; i >= index ; i-- )
        {
            buffy[ i + howMany ] = buffy[ i ];
        }

        len = newLen;
    }

    /** Insert a Str object immediately before a particular character. <p>

     @param s The Str object to be inserted. <p>
     @param index Where to insert s.  The character currently at index will me moved to the right. <p>
     */
    public void insert( Str s , int index )
    {
        insertJunk( s.len , index );

        for ( int i = 0; i < s.len ; i++ )
        {
            buffy[ index + i ] = s.buffy[ i ];
        }
    }

    /** Insert a String object immediately before a particular character. <p>

     @param s The String object to be inserted. <p>
     @param index Where to insert s.  The character currently at index will me moved to the right. <p>
     */
    public void insert( String s , int index )
    {
        if ( index > len )
        {
            index = len;
        }
        insertJunk( s.length() , index );

        for ( int i = 0; i < s.length() ; i++ )
        {
            buffy[ index + i ] = s.charAt( i );
        }
    }

    /** Remove characters from your string. <p>

     @param index Referencing the first character to be removed. <p>
     @param howMany The number of characters to remove. <p>
     */
    public void delete( int index , int howMany )
    {
        if ( len > 0 )
        {
            if ( index < len )
            {
                if ( index + howMany > len )
                {
                    howMany = len - index;
                }
                int newLen = len - howMany;
                for ( int i = index; i < newLen ; i++ )
                {
                    buffy[ i ] = buffy[ i + howMany ];
                }
                len = newLen;
            }
        }
    }

    /** Remove characters from the beginning of your string. <p>

     @param howMany The number of characters to remove. <p>
     */
    public void deleteFirst( int howMany )
    {
        delete( 0 , howMany );
    }

    /** Remove first character from the beginning of your string. <p>
     */
    public void deleteFirst()
    {
        delete( 0 , 1 );
    }

    /** Remove characters from the end of your string. <p>

     @param howMany The number of characters to remove. <p>
     */
    public void deleteLast( int howMany )
    {
        delete( len - howMany , howMany );
    }

    /** Remove the last character from the end of your string. <p>
     */
    public void deleteLast()
    {
        deleteLast( 1 );
    }

    /** If you wish to treat your string like a stack, you can push and pop characters. <p>

     @param c The character that will be appended to the end of your string. <p>
     */
    public void push( char c )
    {
        append( c );
    }

    /** If you wish to treat your string like a stack, you can push and pop characters. <p>

     Remember the old assembly programming pearl:  May all your pushes be popped. <p>

     @return The last character in your string.  If there are no more characters in the string, a null character is returned. <p>
     */
    public char pop()
    {
        char returnVal = (char)0;
        if ( len > 0 )
        {
            len--;
            returnVal = buffy[ len ];
        }
        return returnVal;
    }

    /** Test to see if your string begins with a specific sequence of characters. <p>

     This test is much faster than the equivalent in the regular expression library.  And is also much faster than doing
     a substring comparison. <p>

     This test is case sensitive. <p>

     @param s A String object representing the first few characters being tested for. <p>
     @return True if s matches the beginning of your string exactly. <p>
     */
    public boolean startsWith( String s )
    {
        boolean returnValue = false;
        if ( len > s.length() )
        {
            boolean match = true;
            for ( int i = 0; i < s.length() ; i++ )
            {
                if ( buffy[ i ] != s.charAt( i ) )
                {
                    match = false;
                }
            }
            returnValue = match;
        }
        return returnValue;
    }

    /** Find the position within your string of a particular character. <p>

     This method is case sensitive. <p>

     @param c The character to search for. <p>
     @param startPos The character position in your string to start looking for c. <p>
     @return The position of the character that matches c where 0 is the first character of the string.  -1 is returned if a matching character could not be found. <p>
     */
    public int indexOf( char c , int startPos )
    {
        boolean done = false;
        while ( !done )
        {
            if ( startPos >= len )
            {
                startPos = -1;
                done = true;
            }
            else
            {
                if ( buffy[ startPos ] == c )
                {
                    done = true;
                }
                else
                {
                    startPos++;
                }
            }
        }
        return startPos;
    }

    /** Find the position within your string of a particular character, ignoring case. <p>

     This method is not case sensitive. <p>

     @param c The character to search for. <p>
     @param startPos The character position in your string to start looking for c. <p>
     @return The position of the character that matches c where 0 is the first character of the string.  -1 is returned if a matching character could not be found. <p>
     */
    public int indexOfIgnoreCase( char c , int startPos )
    {
        char lowerC = Character.toLowerCase( c );
        boolean done = false;
        while ( !done )
        {
            if ( startPos >= len )
            {
                startPos = -1;
                done = true;
            }
            else
            {
                if ( Character.toLowerCase( buffy[ startPos ] ) == lowerC )
                {
                    done = true;
                }
                else
                {
                    startPos++;
                }
            }
        }
        return startPos;
    }

    /** Find the position within your string of a particular sequence of characters. <p>

     This method is case sensitive. <p>

     @param s A String object representing the character sequence to search for. <p>
     @param startPos The character position in your string to start looking for s. <p>
     @return -1 is returned if a match could not be found.  Otherwise the position of the first character of the match is returned. <p>
     */
    public int indexOf( String s , int startPos )
    {
        if ( s.length() > 0 )
        {
            boolean done = false;
            while ( !done )
            {
                startPos = indexOf( s.charAt( 0 ) , startPos );
                if ( startPos == -1 )
                {
                    done = true;
                }
                else
                {
                    if ( startPos + s.length() > len )
                    {
                        startPos = -1;
                        done = true;
                    }
                    else
                    {
                        boolean match = true;
                        for ( int i = 1; i < s.length() ; i++ )
                        {
                            if ( s.charAt( i ) != buffy[ startPos + i ] )
                            {
                                match = false;
                            }
                        }
                        if ( match )
                        {
                            done = true;
                        }
                        else
                        {
                            startPos++;
                        }
                    }
                }
            }
        }
        else
        {
            startPos = -1;
        }
        return startPos;
    }

    /** Find the position within your string of a particular sequence of characters, ignoring the case. <p>

     This method is not case sensitive. <p>

     @param s A String object representing the character sequence to search for. <p>
     @param startPos The character position in your string to start looking for s. <p>
     @return -1 is returned if a match could not be found.  Otherwise the position of the first character of the match is returned. <p>
     */
    public int indexOfIgnoreCase( String s , int startPos )
    {
        String lowerS = s.toLowerCase();
        if ( lowerS.length() > 0 )
        {
            boolean done = false;
            while ( !done )
            {
                startPos = indexOfIgnoreCase( lowerS.charAt( 0 ) , startPos );
                if ( startPos == -1 )
                {
                    done = true;
                }
                else
                {
                    if ( startPos + lowerS.length() > len )
                    {
                        startPos = -1;
                        done = true;
                    }
                    else
                    {
                        boolean match = true;
                        for ( int i = 1; i < lowerS.length() ; i++ )
                        {
                            if ( lowerS.charAt( i ) != Character.toLowerCase( buffy[ startPos + i ] ) )
                            {
                                match = false;
                            }
                        }
                        if ( match )
                        {
                            done = true;
                        }
                        else
                        {
                            startPos++;
                        }
                    }
                }
            }
        }
        else
        {
            startPos = -1;
        }
        return startPos;
    }

    /** Find the position within your string of a particular character. <p>

     This method is case sensitive.  Searching begins with the first character in your string. <p>

     @param c The character to search for. <p>
     @return The position of the character that matches c.  -1 is returned if a matching character could not be found. <p>
     */
    public int indexOf( char c )
    {
        return indexOf( c , 0 );
    }

    /** Find the position within your string of a particular sequence of characters. <p>

     This method is case sensitive.  Searching begins with the first character in your string. <p>

     @param s A String object representing the character sequence to search for. <p>
     @return -1 is returned if a match could not be found.  Otherwise the position of the first character of the match is returned. <p>
     */
    public int indexOf( String s )
    {
        return indexOf( s , 0 );
    }


    /** Find the position within your string of a particular character, ignoring case. <p>

     This method is not case sensitive.  Searching begins with the first character in your string. <p>

     @param c The character to search for. <p>
     @return The position of the character that matches c.  -1 is returned if a matching character could not be found. <p>
     */
    public int indexOfIgnoreCase( char c )
    {
        return indexOfIgnoreCase( c , 0 );
    }

    /** Find the position within your string of a particular sequence of characters, ignoring case. <p>

     This method is not case sensitive.  Searching begins with the first character in your string. <p>

     @param s A String object representing the character sequence to search for. <p>
     @return -1 is returned if a match could not be found.  Otherwise the position of the first character of the match is returned. <p>
     */
    public int indexOfIgnoreCase( String s )
    {
        return indexOfIgnoreCase( s , 0 );
    }


    public int lastIndexOf( char c )
    {
        int returnVal = -1;
        if ( len > 0 )
        {
            int pos = len - 1;
            boolean done = false;
            while ( !done )
            {
                if ( pos < 0 )
                {
                    done = true;
                }
                else
                {
                    if ( buffy[ pos ] == c )
                    {
                        returnVal = pos;
                        done = true;
                    }
                    else
                    {
                        pos--;
                    }
                }
            }
        }
        return returnVal;
    }

    /** Does the string object end with this bit of text. <p>

     @param s the suffix you are looking for. <p>
     */
    public boolean endsWith( String s )
    {
        boolean returnVal = true;
        if ( s.length() > len )
        {
            returnVal = false;
        }
        else
        {
            int buffyPos = len - s.length();
            for ( int i = 0 ; i < s.length() ; i++ )
            {
                if ( buffy[ buffyPos + i ] != s.charAt( i ) )
                {
                    returnVal = false;
                }
            }
        }
        return returnVal;
    }

    /** Returns the index of the first digit, or returns -1 if there are no digits. <p>
     */
    public int firstDigit()
    {
        int returnVal = -1;
        int pos = 0;
        boolean done = false;
        while ( !done )
        {
            if ( pos >= len )
            {
                done = true;
            }
            else
            {
                char c = buffy[ pos ];
                if ( Numbers.inRange( c , '0' , '9' ) )
                {
                    returnVal = pos;
                    done = true;
                }
                else
                {
                    pos++;
                }
            }
        }
        return returnVal;
    }

    /** Replace all instances of one character with another character. <p>

     All occurances are replaced. <p>

     Searching for the "next instance" of character will begin immediately after the last replacement so that there is no chance of an infinite loop. <p>

     @param c1 The character to search for. <p>
     @param c2 The character that is to replace c1. <p>
     */
    public void replace( char c1 , char c2 )
    {
        for ( int i = 0; i < len ; i++ )
        {
            if ( buffy[ i ] == c1 )
            {
                buffy[ i ] = c2;
            }
        }
    }

    // used by the replace stuff
    private void copyStringTo( String s , int index )
    {
        for ( int i = 0; i < s.length() ; i++ )
        {
            buffy[ index + i ] = s.charAt( i );
        }
    }

    /** Replace all instances of one character with a String object. <p>

     All occurances are replaced. <p>

     Searching for the "next instance" of character will begin immediately after the last replacement so that there is no chance of an infinite loop. <p>

     @param c The character to search for. <p>
     @param s The String object that is to replace c. <p>
     */
    public void replace( char c , String s )
    {
        if ( s == null )
        {
            s = "";
        }
        int pos = 0;
        while ( pos != -1 )
        {
            pos = indexOf( c , pos );
            if ( pos != -1 )
            {
                if ( s.length() > 1 )
                {
                    insertJunk( s.length() - 1 , pos );
                }
                else if ( s.length() == 0 )
                {
                    delete( pos , 1 );
                }
                copyStringTo( s , pos );
                pos += s.length();
            }
        }
    }

    /** Search for a particular character sequence and replace it with one character. <p>

     All occurances are replaced. <p>

     Searching for the "next instance" of a character sequence will begin immediately after the last replacement so that there is no chance of an infinite loop. <p>

     @param s A String object representing a character sequence to search for. <p>
     @param c The character that is to replace s. <p>
     */
    public void replace( String s , char c )
    {
        int pos = 0;
        while ( pos != -1 )
        {
            pos = indexOf( s , pos );
            if ( pos != -1 )
            {
                if ( s.length() > 1 )
                {
                    delete( pos , s.length() - 1 );
                }
                buffy[ pos ] = c;
                pos++;
            }
        }
    }

    /** Search for a particular character sequence and replace it with a different character sequence. <p>

     All occurances are replaced. <p>

     Searching for the "next instance" of a character sequence will begin immediately after the last replacement so that there is no chance of an infinite loop. <p>

     @param s1 A String object representing a character sequence to search for. <p>
     @param s2 A String object representing a character sequence that is to replace s1. <p>
     */
    public void replace( String s1 , String s2 )
    {
        if ( s2 == null )
        {
            s2 = "";
        }
        int pos = 0;
        while ( pos != -1 )
        {
            pos = indexOf( s1 , pos );
            if ( pos != -1 )
            {
                if ( s1.length() < s2.length() )
                {
                    insertJunk( s2.length() - s1.length() , pos );
                }
                else if ( s1.length() > s2.length() )
                {
                    delete( pos , s1.length() - s2.length() );
                }
                copyStringTo( s2 , pos );
                pos += s2.length();
            }
        }
    }

    // if buffy is not big enough to hold len, buffy is grown
    private void testBuffyLen( int newLen )
    {
        if ( newLen > buffy.length )
        {
            setCapacity( newLen + extra );
        }
    }

    // initialize this string to be a substring of c
    private void init( char[] c , int index , int length )
    {
        len = 0;
        if ( index < c.length )
        {
            if ( index + length > c.length )
            {
                length = c.length - index;
            }
            testBuffyLen( length );
            System.arraycopy( c, index, buffy, 0, length );
            len = length;
        }
    }

    /** Eliminate all occurances of two more space in a row. <p>

     When this method is done, there will not be an occurance of two spaces in a row.   Anywhere that there was two or more spaces, there is now only one space. <p>
     */
    public void removeDoubleSpaces()
    {
        boolean prevCharIsSpace = false;
        int newI = 0;
        for ( int i = 0; i < len ; i++ )
        {
            if ( buffy[ i ] == ' ' )
            {
                if ( !prevCharIsSpace )
                {
                    prevCharIsSpace = true;
                    buffy[ newI ] = buffy[ i ];
                    newI++;
                }
            }
            else
            {
                prevCharIsSpace = false;
                buffy[ newI ] = buffy[ i ];
                newI++;
            }
        }
        len = newI;
    }

    /** Count how many times a particular character occurs within your string. <p>

     As dumb as this sounds, the method does end up getting used a lot. <p>

     @param c The character to look for. <p>
     @return The number of times the character occured in this string. <p>
     */
    public int charCount( char c )
    {
        int count = 0;
        for ( int i = 0 ; i < len ; i++ )
        {
            if ( buffy[ i ] == c )
            {
                count++;
            }
        }
        return count;
    }

    /** Count how many times a particular string occurs within your string. <p>

     @param s The string to look for. <p>
     @return The number of times the string occured in this string. <p>
     */
    public int stringCount( String s )
    {
        int count = 0;
        int pos = 0 ;
        boolean done = false ;
        while ( ! done )
        {
            pos = indexOf( s , pos );
            if ( pos == -1 )
            {
                done = true ;
            }
            else
            {
                count++ ;
                pos++ ;
            }
        }
        return count;
    }

    /** Trims all leading spaces, no trailing spaces. <p>
     */
    public void trimLead()
    {
        if ( len > 0 )
        {
            int i = 0;
            while ( ( i < len ) && ( buffy[ i ] == ' ' ) )
            {
                i++;
            }
            if ( i > 0 )
            {
                deleteFirst( i );
            }
        }
    }

    /** Trims all trailing spaces, no leading spaces. <p>
     */
    public void trimTrail()
    {
        if ( len > 0 )
        {
            len--;
            while ( ( len >= 0 ) && ( buffy[ len ] == ' ' ) )
            {
                len--;
            }
            len++;
        }
    }

    /** Trims all leading spaces and all trailing spaces. <p>
     */
    public void trim()
    {
        trimLead();
        trimTrail();
    }

    private static final String whitespace = " \n\r\t";

    /** Trims all leading spaces, newlines, returns and tabs. <p>
     */
    public void trimLeadWhitespace()
    {
        if ( len > 0 )
        {
            int i = 0;
            while ( ( i < len ) && ( whitespace.indexOf( buffy[ i ] ) != -1 ) )
            {
                i++;
            }
            if ( i > 0 )
            {
                deleteFirst( i );
            }
        }
    }

    /** Trims all trailing spaces, newlines, returns and tabs. <p>
     */
    public void trimTrailWhitespace()
    {
        if ( len > 0 )
        {
            len--;
            while ( ( len >= 0 ) && ( whitespace.indexOf( buffy[ len ] ) != -1 ) )
            {
                len--;
            }
            len++;
        }
    }

    /** Trims all leading spaces, newlines, returns and tabs and all trailing spaces, newlines, returns and tabs. <p>
     */
    public void trimWhitespace()
    {
        trimLeadWhitespace();
        trimTrailWhitespace();
    }

    /** Trims all characters from end of the string until a particular character is encountered. <p>

     The character being searched for is also trimmed. <p>

     @param c The character to search for. <p>
     */
    public void trimTrailTo( char c )
    {
        if ( len > 0 )
        {
            len--;
            while ( ( len > 0 ) && ( buffy[ len ] != c ) )
            {
                len--;
            }
        }
    }

    /** Extract and return the first word. <p>

     Space delimited (all double spaces are removed). <p>

     @return A Str object representing the first word found in the string.  No spaces are returned.  If there are no words left, an empty Str object is returned. <p>
     */
    public Str extractWord()
    {
        Str s = new Str();
        if ( len > 0 )
        {
            removeDoubleSpaces();
            trim();
            int pos = indexOf( ' ' );
            if ( pos == -1 )
            {
                s.set( this );
                len = 0;
            }
            else
            {
                s.init( buffy , 0 , pos );
                deleteFirst( pos + 1 );
            }
        }
        return s;
    }


    public int hashCode()
    {
        return toString().hashCode();
    }

    public boolean equals( Object obj )
    {
        boolean returnVal = false;
        if ( obj != null )
        {
            if ( obj instanceof Str )
            {
                Str s = (Str)obj;
                returnVal = toString().equals( s.toString() );
            }
            else if ( obj instanceof String )
            {
                String s = (String)obj;
                returnVal = toString().equals( s );
            }
        }
        return returnVal;
    }

    /** Convert this Str object to a String object. <p>

     @return A String object with an exact copy of your string. <p>
     */
    public String toString()
    {
        return new String( buffy , 0 , len );
    }

    /** Force string to be the first number found. <p>

     Everything that is not a number, period or hyphen is converted to a space.  The resulting string is the first word extracted. <p>

     */
    public void forceNumeric()
    {
        for ( int i = 0 ; i < len ; i++ )
        {
            char c = buffy[ i ];
            if ( !Numbers.inRange( c , '0' , '9' ) )
            {
                if ( ( c != '.' ) && ( c != '-' ) )
                {
                    buffy[ i ] = ' ';
                }
            }
        }
        set( extractWord() );
    }

    /** Try to convert this string to an integer value. <p>

     @return Zero if the value is supposed to be zero or if there were any problems. <p>
     */
    public int toInt()
    {
        int i = 0;
        try
        {
            i = Integer.parseInt( toString().trim() );
        }
        catch ( NumberFormatException e )
        {
        }
        return i;
    }

    /** Try to convert this string to a long value. <p>

     @return Zero if the value is supposed to be zero or if there were any problems. <p>
     */
    public long toLong()
    {
        long i = 0;
        try
        {
            i = Long.parseLong( toString().trim() );
        }
        catch ( NumberFormatException e )
        {
        }
        return i;
    }

    /** Try to convert this string to a double value. <p>

     @return Zero if the value is supposed to be zero or if there were any problems. <p>
     */
    public double toDouble()
    {
        double d = 0.0;
        try
        {
            d = Double.valueOf( toString() ).doubleValue();
        }
        catch ( NumberFormatException e )
        {
        }
        return d;
    }

    /** Report how many leading spaces there are. <p>

     @return the number of leading spaces in your string.<p>
     */
    public int leftSpaceCount()
    {
        int i = 0;
        while ( ( i < len ) && ( buffy[ i ] == ' ' ) )
        {
            i++;
        }
        return i;
    }

    /** Report how many trailing spaces there are. <p>

     @return the number of trailing spaces in your string. <p>
     */
    public int rightSpaceCount()
    {
        int i = len - 1;
        while ( ( i > 0 ) && ( buffy[ i ] == ' ' ) )
        {
            i--;
        }
        return ( ( len - i ) - 1 );
    }

    /** Force the length of the string and keep text to the left. <p>

     If the new length is longer, spaces will be added to the right. <p>
     If the new length is shorter, characters are chopped off from the right. <p>

     Useful for formatting text to be left justified. <p>

     @param newLen The desired new length. <p>
     */
    public void left( int newLen )
    {
        if ( len < newLen )
        {
            testBuffyLen( newLen );
            for ( int i = len; i < newLen ; i++ )
            {
                buffy[ i ] = ' ';
            }
        }
        len = newLen;
    }

    /** Force the length of the string and keep text to the right. <p>

     If the new length is longer, spaces will be added to the left. <p>
     If the new length is shorter, characters are chopped off from the right (not the left). <p>

     Useful for formatting text to be right justified. <p>

     @param newLen The desired new length. <p>
     */
    public void right( int newLen )
    {
        if ( len < newLen )
        {
            int howMany = newLen - len;
            insertJunk( newLen - len , 0 );
            for ( int i = 0; i < howMany ; i++ )
            {
                buffy[ i ] = ' ';
            }
        }
        else
        {
            len = newLen;
        }
    }

    /** Try to center the text in a pad of spaces. <p>

     First, all leading and trailing spaces are removed. <p>

     If the new length is longer, spaces will be added to the left and right. <p>
     If the new length is shorter, the string is unchanged.  In this case, the resulting string is longer than the new length! <p>

     Useful for formatting text to be centered. <p>

     @param newLen The desired new length. <p>
     */
    public void center( int newLen )
    {
        trim();
        if ( newLen > len )
        {
            int totalSpaces = newLen - len;
            int leftSpaces = totalSpaces / 2;
            int rightSpaces = totalSpaces - leftSpaces;
            right( len + rightSpaces );
            left( len + leftSpaces );
        }
    }

    /** All characters are forced to upper case. <p>
     */
    public void toUpper()
    {
        for ( int i = 0 ; i < len ; i++ )
        {
            toUpper( i );
        }
    }

    /** Char at index is forced to upper case. <p>
     */
    public void toUpper( int index )
    {
        if ( ( index >= 0 ) && ( index < len ) )
        {
            buffy[ index ] = Character.toUpperCase( buffy[ index ] );
        }
    }

    /** Is char at index upper case?
     */
    public boolean isUpper( int index )
    {
        boolean returnVal = false;
        if ( ( index >= 0 ) && ( index < len ) )
        {
            returnVal = Character.isUpperCase( buffy[ index ] );
        }
        return returnVal;
    }

    /** All characters are forced to lower case. <p>
     */
    public void toLower()
    {
        for ( int i = 0 ; i < len ; i++ )
        {
            toLower( i );
        }
    }


    /** Char at index is forced to lower case. <p>
     */
    public void toLower( int index )
    {
        if ( ( index >= 0 ) && ( index < len ) )
        {
            buffy[ index ] = Character.toLowerCase( buffy[ index ] );
        }
    }

    /** Is char at index lower case?
     */
    public boolean isLower( int index )
    {
        boolean returnVal = false;
        if ( ( index >= 0 ) && ( index < len ) )
        {
            returnVal = Character.isLowerCase( buffy[ index ] );
        }
        return returnVal;
    }


    /** Count the number of trailing digits. <p>

     Only values '0' through '9' are considered. <p>
     */
    public int trailingDigits()
    {
        int count = 0;
        int index = length() - 1;
        boolean done = false;
        while ( !done )
        {
            if ( index < 0 )
            {
                done = true;
            }
            else
            {
                char c = get( index );
                if ( Numbers.inRange( c , '0' , '9' ) )
                {
                    count++;
                    index--;
                }
                else
                {
                    done = true;
                }
            }
        }
        return count;
    }

    /** the number of digits that occur within this range.
     *
     * Pass in 'a' and 'c' with the string "abcdefg" and the result will be 3.
     *
     * @param low this char is included in the range
     * @param high this char is included in the range
     * @return the number of characters found within the range
     */
    public int countRange( char low , char high )
    {
        int count = 0;
        for ( int i = 0 ; i < length() ; i++ )
        {
            if ( Numbers.inRange( get( i ) , low , high ) )
            {
                count++;
            }
        }
        return count;
    }

    /**
     * @return the number of digits found in the string.
     */
    public int countDigits()
    {
        return countRange( '0' , '9' );
    }

    /** Reverse the order of all the characters. <p>

     Designed to behave the same way as StringBuffer.reverse(). <p>
     */
    public void reverse()
    {
        int halfway = len / 2;
        int lastChar = len - 1;

        // swap the the last chars for the first
        for ( int i = 0 ; i < halfway ; i++ )
        {
            char temp = buffy[ i ];
            int swapIndex = lastChar - i;
            buffy[ i ] = buffy[ swapIndex ];
            buffy[ swapIndex ] = temp;
        }
    }


    /////////////////////////////// static methods





    /** Converts a double to a string with two decimal places. <p>

     *   <pre>
     *   Examples:   In       Out
     *
     *               0.0      "0.00"
     *               1.9999   "2.00"
     *               222.2222 "222.22"
     *   </pre>

     @param d The double value. <p>
     @return A String object with the formated number. <p>
     */
    public static String moneyStr( double d )
        // uses the format ############0.00 and then trims spaces
    {
        DecimalFormat f = new DecimalFormat( "############0.00" );
        return f.format( d ).trim();
    }

    /** Converts a double to a string with a specified number of decimal places. <p>

     @param d The double value. <p>
     @param decimalPlaces The number of decimal places needed. <p>
     @return A String object with the formated number. <p>
     */
    public static String formatDouble( double d , int decimalPlaces )
    {
        String format = "###################0." + stringOf( decimalPlaces , '0' );
        DecimalFormat f = new DecimalFormat( format );
        return f.format( d ).trim();
    }

    /** Converts a double to a string with commas inserted for thousands, millions, etc. <p>

     Fractional values are ignored. <p>

     @param val The double value. <p>
     @return A String object with the formated number. <p>
     */
    public static String commaStr( double val )
    {
        DecimalFormat f = new DecimalFormat( "###,###,###,##0" );
        return f.format( val ).trim();
    }

    private static String[] numStr =
        {
            "zero", "one", "two", "three", "four", "five", "six", "seven", "eight",
            "nine", "ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen",
            "sixteen", "seventeen", "eighteen", "nineteen"
        };

    private static String[] tee =
        {
            "twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety"
        };

    /** Like the ANSI c function atoi(). <p>

     @param s The string that begins with a number. <p>
     @return The integer that was extracted or 0 if any problems were encountered. <p>
     */
    static public int atoi( String s )
    {
        int i = 0;
        try
        {
            i = Integer.valueOf( s ).intValue();
        }
        catch ( NumberFormatException e )
        {
        }
        return i;
    }

    /** Sort of like the ANSI c function atoi(). <p>

     Instead of passing in a string, pass in a character. <p>

     @param c The char containing a single digit. <p>
     @return The integer that was extracted ( 0 through 9 ) or 0 if any problems were encountered. <p>
     */
    static public int atoi( char c )
    {
        int returnVal = 0;
        int charVal = (int) c;
        final int zeroVal = (int) '0';
        final int nineVal = (int) '9';
        if ( ( charVal > zeroVal ) && ( charVal <= nineVal ) )
        {
            returnVal = charVal - zeroVal;
        }
        return returnVal;
    }

    /** Like the ANSI c function atod(). <p>

     @param s The string that begins with a number. <p>
     @return The double that was extracted or 0.0 if any problems were encountered. <p>
     */
    static public double atod( String s ) // ascii to double
    {
        double d = 0.0;
        try
        {
            d = Double.parseDouble( s );
        }
        catch ( Exception e )
        {
            //System.out.println(e);
        }
        return d;
    }

    // used by the verboseMoney() method
    private static Str littleMoney( Str n )
    {
        // pass in a number that is one to three digits
        Str s = new Str(); // return value
        if ( n.length() == 3 )
        {
            char c = n.get( 0 );
            if ( c != '0' )
            {
                s.append( numStr[ atoi( c ) ] );
                s.append( " hundred " );
            }
            n.deleteFirst( 1 );
        }
        if ( n.length() == 2 )
        {
            char c = n.get( 0 );
            if ( c == '0' )
            {
                n.deleteFirst( 1 );
            }
            else
            {
                if ( c == '1' )
                {
                    s.append( numStr[ n.toInt() ] );
                }
                else
                {
                    s.append( tee[ atoi( c ) - 2 ] );
                    if ( n.get( 1 ) != '0' )
                    {
                        s.append( '-' );
                        n.deleteFirst( 1 );
                    }
                }
            }
        }
        if ( n.length() == 1 )
        {
            int i = atoi( n.get( 0 ) );
            if ( i > 0 )
            {
                s.append( numStr[ i ] );
            }
        }
        return s;
    }

    /** Convert a double to English ( 12.01 -> "twelve and 01/100" ). <p>

     *   <pre>
     *   Examples:   In          Out
     *
     *               0.0         "zero and 00/100"
     *               23.23       "twenty-three and 23/100"
     *               12345678.90 "twelve million three hundred forty-five thousand six hundred seventy-eight and 90/100"
     *   </pre>

     @param x The double to convert. <p>
     @return The Str object containing the results. <p>
     */
    public static Str verboseMoney( double x )
    {
        Str n = new Str( moneyStr( x ) );
        int decimalPos = n.length() - 3;
        Str cents = n.after( decimalPos );
        n = n.before( decimalPos );
        Str returnVal = new Str();
        if ( n.length() > 6 )
        {
            int millionLen = n.length() - 6;
            returnVal.append( littleMoney( n.before( millionLen ) ) );
            returnVal.append( " million " );
            n = n.from( millionLen );
        }
        if ( n.length() > 3 )
        {
            int thousandLen = n.length() - 3;
            returnVal.append( littleMoney( n.before( thousandLen ) ) );
            returnVal.append( " thousand " );
            n = n.from( thousandLen );
        }
        returnVal.append( littleMoney( n ) );
        returnVal.append( " and " + cents + "/100" );
        return returnVal;
    }

    /** Get the current local date and time. <p>

     @return A String object with the current local date and time. <p>
     */
    public static String nowStr()
    {
        SimpleDateFormat f = new SimpleDateFormat( "yyyy.MM.dd EEE hh:mm:ss" );
        return f.format( new Date() );
    }

    /** Create a Str object containing nothing but a quantity of one character. <p>

     Example:  calling stringOf( 20 , '-' ) returns a string of 20 hyphens. <p>

     @param quan The desired string length. <p>
     @param c The desired character. <p>
     @return The new Str object. <p>
     */
    public static Str stringOf( int quan , char c )
    {
        Str returnVal = new Str( quan );
        if ( quan > 0 )
        {
            for ( int i = 0; i < quan ; i++ )
            {
                returnVal.buffy[ i ] = c;
            }
            returnVal.len = quan;
        }
        return returnVal;
    }

    /** Create a Str object containing nothing but a quantity of spaces. <p>

     @param quan The number of spaces desired. <p>
     */
    public static Str spaces( int quan )
    {
        return stringOf( quan , ' ' );
    }

    /** A static sub-string method that works the way you expect it to work. <p>

     The Java substring thing is, IMO, twisted.  This is the normal way to do substrings.  Plus, this object makes the
     best of awkward requests without throwing an exception. <p>

     @param s A String object that you want to extract a sub-string from. <p>
     @param index Where the sub-string begins. <p>
     @param length How long you want your sub-string to be. <p>
     */
    public static String subString( String s , int index , int length )
    {
        String returnVal = "";
        if ( index < s.length() )
        {
            int rightIndex = index + length;
            if ( rightIndex >= s.length() )
            {
                returnVal = s.substring( index );
            }
            else
            {
                returnVal = s.substring( index , rightIndex );
            }
        }
        return returnVal;
    }

    /** Same as subString, but allows all lower case. <p>
     */
    public static String substring( String s , int index , int length )
    {
        return subString( s , index , length );
    }


    /** Get all of the substring that occurs before a particular character. <p>

     @param s The String object to extract the substring from. <p>
     @param index The character that marks the end of the substring.  This character is not included in the substring. <p>
     @return A String object containing the substring. <p>
     */
    public static String before( String s , int index )
    {
        return subString( s , 0 , index );
    }

    /** Get all of the substring that occurs through a particular character. <p>

     @param s The String object to extract the substring from. <p>
     @param index The character that marks the end of the substring.  This character is included in the substring. <p>
     @return A String object containing the substring. <p>
     */
    public static String through( String s , int index )
    {
        return subString( s , 0 , index );
    }

    /** Get all of the substring that occurs after a particular character. <p>

     @param s The String object to extract the substring from. <p>
     @param index The character that marks the beginning of the substring.  This character is not included in the substring. <p>
     @return A String object containing the substring. <p>
     */
    public static String after( String s , int index )
    {
        return subString( s , index + 1 , ( s.length() - index ) - 1 );
    }

    /** Get all of the substring that occurs from a particular character to the end of the string. <p>

     @param s The String object to extract the substring from. <p>
     @param index The character that marks the beginning of the substring.  This character is included in the substring. <p>
     @return A String object containing the substring. <p>
     */
    public static String from( String s , int index )
    {
        return subString( s , index , s.length() - index );
    }

    /** Force the length of the string and keep text to the left. <p>

     If the new length is longer, spaces will be added to the right. <p>
     If the new length is shorter, characters are chopped off from the right. <p>

     Useful for formatting text to be left justified. <p>

     @param s The String object to doAction. <p>
     @param newLen The desired new length. <p>
     @return The new string object. <p>
     */
    static public String left( String s , int newLen )
    {
        Str temp = new Str( s );
        temp.left( newLen );
        return temp.toString();
    }

    /** Force the length of the string and keep text to the right. <p>

     If the new length is longer, spaces will be added to the left. <p>
     If the new length is shorter, characters are chopped off from the right (not the left). <p>

     Useful for formatting text to be right justified. <p>

     @param s The String object to doAction. <p>
     @param newLen The desired new length. <p>
     @return The new string object. <p>
     */
    static public String right( String s , int newLen )
    {
        Str temp = new Str( s );
        temp.right( newLen );
        return temp.toString();
    }

    /** Try to center the text in a pad of spaces. <p>

     First, all leading and trailing spaces are removed. <p>

     If the new length is longer, spaces will be added to the left and right. <p>
     If the new length is shorter, the string is unchanged.  In this case, the resulting string is longer than the new length! <p>

     Useful for formatting text to be centered. <p>

     @param s The String object to doAction. <p>
     @param newLen The desired new length. <p>
     @return The new string object. <p>
     */
    static public String center( String s , int newLen )
    {
        Str temp = new Str( s );
        temp.center( newLen );
        return temp.toString();
    }

    static public String trimWhitespace( String s )
    {
        Str temp = new Str( s );
        temp.trimWhitespace();
        return temp.toString();
    }

    /** Count the number of trailing digits. <p>

     Only values '0' through '9' are considered. <p>
     */
    static public int trailingDigits( String s )
    {
        int count = 0;
        int index = s.length() - 1;
        boolean done = false;
        while ( !done )
        {
            if ( index < 0 )
            {
                done = true;
            }
            else
            {
                char c = s.charAt( index );
                if ( Numbers.inRange( c , '0' , '9' ) )
                {
                    count++;
                    index--;
                }
                else
                {
                    done = true;
                }
            }
        }
        return count;
    }

    /**
     * @return true if the string is not null and has at least one non whitespace character.
     */
    public static boolean usable( String s )
    {
        boolean returnVal = false;
        if ( s != null )
        {
            s = s.trim();
            if ( s.length() > 0 )
            {
                returnVal = true;
            }
        }
        return returnVal;
    }

    /** Try to convert a string to an integer value. <p>

     @return Zero if the value is supposed to be zero or if there were any problems. <p>
     */
    public static int toInt( String s )
    {
        int i = 0;
        if ( s != null )
        {
            try
            {
                i = Integer.parseInt( s.trim() );
            }
            catch ( NumberFormatException e )
            {
            }
        }
        return i;
    }

    /** Try to convert a string to a long value. <p>

     @return Zero if the value is supposed to be zero or if there were any problems. <p>
     */
    public static long toLong( String s )
    {
        long i = 0;
        if ( s != null )
        {
            try
            {
                i = Long.parseLong( s.trim() );
            }
            catch ( NumberFormatException e )
            {
            }
        }
        return i;
    }

    /** Try to convert a string to a double value. <p>

     @return Zero if the value is supposed to be zero or if there were any problems. <p>
     */
    public static double toDouble( String s )
    {
        double d = 0.0;
        try
        {
            d = Double.valueOf( s ).doubleValue();
        }
        catch ( NumberFormatException e )
        {
        }
        return d;
    }

    /** Try to convert a string to a Timestamp object. <p>

     @return null if null is passed in or were any format problems. <p>
     */
    public static Timestamp toTimestamp( String s )
    {
        Timestamp returnVal = null;
        if ( s != null )
        {
            try
            {
                returnVal = Timestamp.valueOf( s );
            }
            catch ( NumberFormatException e )
            {
            }
        }
        return returnVal;
    }

    /** Try to convert a string to a boolean value. <p>

     @return True if the first char of the string is 'T', 't', 'Y' or 'y'. Anything else (including null or empty string) returns false. <p>
     */
    public static boolean toBoolean( String s )
    {
        boolean returnVal = false;
        if ( usable( s ) )
        {
            char c = Character.toLowerCase( s.charAt( 0 ) );
            returnVal = ( ( c == 't' ) || ( c == 'y' ) || ( c == '1' ) );
        }
        return returnVal;
    }

    /** Return a string that is either null or has at least one character.
     *
     * Note that this is a bit different than String.trim() or the instance method of this class.  Those methods will never
     * return a null.
     *
     * This method is useful if your goal is to reduce the number of objects for serialization.
     *
     * The inverse of this method is Str.normalize()
     */
    public static String trim( String s )
    {
        String returnVal = null;
        if ( usable( s ) )
        {
            returnVal = s;
        }
        return returnVal;
    }

    /** Return a string that is not null.
     *
     * If s is null, return "".  Else return s.
     *
     * This method is useful if you could be passed a null string and you want to treat a null as an empty string.
     *
     * Frequently used when showing data that has been serialized and the empty strings were converted to null to
     * save space.
     */
    public static String normalize( Object s )
    {
        String returnVal = "";
        if ( s != null )
        {
            returnVal = s.toString();
        }
        return returnVal;
    }

    /** Uses String.equals(), but will consider that either or both strings might be null.
     *
     * @return true if the contents of both strings are the same or if both strings are null.
     */
    public static boolean equal( String s1 , String s2 )
    {
        boolean returnVal = false;
        if ( ( s1 == null ) && ( s2 == null ) )
        {
            returnVal = true;
        }
        else
        {
            if ( s1 != null )
            {
                returnVal = s1.equals( s2 );
            }
        }
        return returnVal;
    }

    /** Uses String.equals(), but will consider that either or both arrays/strings might be null.
     *
     * @return true if the contents of both arrays are the same or if both arrays are null.
     */
    public static boolean equal( String[] s1 , String[] s2 )
    {
        boolean returnVal = false;
        if ( s1 == s2 )
        {
            returnVal = true;
        }
        else
        {
            if ( ( s1 != null ) && ( s2 != null ) )
            {
                if ( s1.length == s2.length )
                {
                    boolean done = false;
                    boolean same = true;
                    int i = 0;
                    while ( !done )
                    {
                        if ( i >= s1.length )
                        {
                            done = true;
                        }
                        else
                        {
                            if ( equal( s1[ i ] , s2[ i ] ) )
                            {
                                i++;
                            }
                            else
                            {
                                same = false;
                                done = true;
                            }
                        }
                    }
                    returnVal = same;
                }
            }
        }
        return returnVal;
    }


    public static String[] toStringArray( int[] values )
    {
        String[] s = new String[ values.length ];
        for ( int i = 0 ; i < s.length ; i++ )
        {
            s[ i ] = String.valueOf( values[ i ] );
        }
        return s;
    }

    public static int[] toIntArray( String[] values )
    {
        int[] a = new int[ values.length ];
        for( int i = 0 ; i < a.length ; i++ )
        {
            a[ i ] = toInt( values[ i ] );
        }
        return a ;
    }

    /** Just like String.charAt() except for null strings or for strings that are too short, an exception will not be
     * thrown and instead, a null char will be returned.
     */
    public static char getChar( String s , int index )
    {
        char returnVal = (char)0 ;
        if ( ( s != null ) && ( s.length() > index ) )
        {
            returnVal = s.charAt( index );
        }
        return returnVal ;
    }

}

