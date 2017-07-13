package com.javaranch.common ;

/** Like the Vector object, except rather than tracking dynamic array of pointers to different objects,
    this is simply a dynamic array of bytes.  <p>

    The advantage is speed and memory savings. <p>


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
public class ByteVector
{
    private byte[] buffy ; // where the bytes are stored.
    private int len = 0 ;  // the current number of bytes being used.
    private int extra = 50 ; // how much extra to grow when growth is needed

    /** Create a new ByteVector with a specific initial capacity. <p>

        @param initialSize The initial capacity. <p>
    */
    public ByteVector( int initialSize )
    {
        buffy = new byte[ initialSize ];
    }

    /** Create a new ByteVector with a specific initial capacity and to be able to grow by a
        specific increment. <p>

        @param initialSize The initial capacity. <p>
        @param initialExtra How many bytes the object will grow by when more memory is needed. <p>
    */
    public ByteVector( int initialSize , int initialExtra )
    {
        buffy = new byte[ initialSize ];
        extra = initialExtra ;
    }

    /** The object has memory allocated to store a moderate number of bytes. <p>
    */
    public ByteVector()
    {
        buffy = new byte[ extra ];
    }

    /** The object is inititialized to contain a copy of a byte array. <p>

        Enough memory is allocated for some growth. <p>

        @param b A byte array to copy. <p>
    */
    public ByteVector( byte[] b )
    {
        len = b.length ;
        buffy = new byte[ len + extra ];
        System.arraycopy( b, 0, buffy, 0, len );
    }

    /** The object is inititialized to contain a copy of another ByteVector. <p>

        Enough memory is allocated for some growth.  <p>

      @param b A ByteVector to copy. <p>
    */
    public ByteVector( ByteVector b )
    {
        len = b.len ;
        extra = b.extra ;
        buffy = new byte[ b.buffy.length ];
        System.arraycopy( b.buffy, 0, buffy, 0, len );
    }

    /** Get the number of bytes currently being used. <p>

        Not the same as the number of bytes currently allocated. <p>

        @return The number of bytes in use. <p>
    */
    public int length()
    {
        return len ;
    }

    /** Force the number of bytes to be currently used. <p>

        If you want to empty your ByteVector, use setLength(0); <p>

        If the new length is longer than the existing length, the previously unused bytes will be set to zero. <p>

        @param newLength The new length. <p>
    */
    public void setLength( int newLength )
    {
        if ( newLength > buffy.length )
        {
            setCapacity( newLength + extra ) ;
        }
        if ( newLength > len )
        {
            // fill new chars with zero
            while( len < newLength )
            {
                buffy[ len ] = (byte)0 ;
                len++;
            }
        }
        else
        {
            len = newLength ;
        }
    }

    /** Set how much to grow when growth is needed. <p>

        Smaller values will usually save memory, but frequent reallocation may take a lot of time. <p>

        @param howMuch The number of extra bytes to allocate when memory reallocation is required.  Values must be greater than zero. <p>
    */
    public void setExtra( int howMuch )
    {
        if ( howMuch > 0 )
        {
            extra = howMuch ;
        }
    }

    /** Get the number of bytes this object can hold without doing a reallocation. <p>

        @return The number of bytes this object can hold without doing a reallocation. <p>
    */
    public int getCapacity()
    {
        return buffy.length ;
    }

    /** Force this object to be able to hold a specific number of bytes without reallocation. <p>

        Any attempt to make the object grow bigger than this size will succeed. <p>

        Setting the capacity to be smaller than the current object size will result in trailing
        bytes being clipped. <p>

        @param howMuch How many bytes will be the new capacity. <p>
    */
    public void setCapacity( int howMuch )
    {
        byte[] b = new byte[ howMuch ] ;
        int toCopy = Math.min( len , howMuch ) ;
        System.arraycopy( buffy, 0, b, 0, toCopy );
        buffy = b ;
    }

    /** Get a copy of one byte. <p>

        @param index Which byte (0 is the first byte). <p>
        @return The retrieved byte.  Returns a zero if index is outside of 0..length. <p>
    */
    public byte get( int index )
    {
        byte returnValue = (byte)0 ;
        if ( Numbers.inRange( index , 0 , len - 1 ) )
        {
            returnValue = buffy[ index ] ;
        }
        return returnValue ;
    }

    /** Get a copy of the last byte. <p>

        @return A copy of the last byte. <p>
    */
    public byte last()
    {
        return get( len - 1 ) ;
    }

    /** Get a copy of a "sub-vector". <p>

        @param index Where to start copying. <p>
        @param length How many bytes to copy. <p>
        @return A ByteVector object that contains a copy of the "sub-vector". <p>
    */
    public ByteVector get( int index , int length )
    {
        ByteVector b = new ByteVector( length + extra );
        for( int i = 0; i < length ; i++ )
        {
            b.buffy[ i ] = get( index + i ) ;
        }
        b.len = length ;
        return b ;
    }

    /** Copy the existing object into a byte array. <p>

        @return A copy of this object as a byte array. <p>
    */
    public byte[] toByteArray()
    {
        byte[] b = new byte[ len ];
        System.arraycopy( buffy, 0, b, 0, len );
        return b ;
    }

    /** Set one byte in the object. <p>

        @param index Which byte to set.  If index references a byte beyond the last byte of the object, the object will be lengthened and the last byte will be set appropriately (undefined bytes will be set to zero).<p>
        @param b The byte to be placed at index.<p>
    */
    public void set( int index , byte b )
    {
        if ( index > 0 )
        {
            if ( index >= len )
            {
                setLength( index + 1 );
            }
            buffy[ index ] = b ;
        }
    }

    /** Add one byte to the end of the vector. <p>

        @param b The byte to append. <p>
    */
    public void append( byte b )
    {
        if ( len == buffy.length )
        {
            setCapacity( len + extra );
        }
        buffy[ len ] = b ;
        len++;
    }

    /** Add an array of bytes to the end of this object. <p>

        @param b The array containing the bytes to be appended. <p>
        @param numBytes The number of bytes to append from b
    */
    public void append( byte[] b , int numBytes )
    {
        if ( numBytes > b.length )
        {
            numBytes = b.length ;
        }
        int newLen = len + numBytes ;
        if ( newLen > buffy.length )
        {
            setCapacity( newLen + extra );
        }
        for( int i = 0; i < numBytes ; i++ )
        {
            buffy[ len + i ] = b[ i ] ;
        }
        len = newLen ;
    }

    /** Add an array of bytes to the end of this object. <p>

        @param b The byte array to be appended. <p>
    */
    public void append( byte[] b )
    {
        int newLen = len + b.length ;
        if ( newLen > buffy.length )
        {
            setCapacity( newLen + extra );
        }
        for( int i = 0; i < b.length ; i++ )
        {
            buffy[ len + i ] = b[ i ] ;
        }
        len = newLen ;
    }

    /** Add all of the bytes in another ByteVector to the end of this object. <p>

        @param b The ByteVector to be appended. <p>
    */
    public void append( ByteVector b )
    {
        int newLen = len + b.len ;
        if ( newLen > buffy.length )
        {
            setCapacity( newLen + extra );
        }
        for( int i = 0; i < b.len ; i++ )
        {
            buffy[ len + i ] = b.buffy[ i ] ;
        }
        len = newLen ;
    }

    /** Insert a byte immediately before a particular byte in the object. <p>

        @param b The byte to be inserted. <p>
        @param index Where to insert b.  The byte currently at index will me moved to the right. <p>
    */
    public void insert( byte b , int index )
    {
        if ( len == buffy.length )
        {
            setCapacity( len + extra );
        }

        // move bytes from index on one to the right
        for( int i = len - 1; i >= index ; i-- )
        {
            buffy[ i + 1 ] = buffy[ i ] ;
        }

        buffy[ index ] = b ;
        len++;
    }

    private void insertJunk( int howMany , int index )
    {
        int newLen = len + howMany ;
        if ( newLen > buffy.length )
        {
            setCapacity( newLen + extra );
        }

        // move bytes from index on to the right
        for( int i = len - 1; i >= index ; i-- )
        {
            buffy[ i + howMany ] = buffy[ i ] ;
        }

        len = newLen ;
    }

    /** Insert an array of bytes immediately before a particular byte in the object. <p>

        @param b The array of bytes to be inserted. <p>
        @param index Where to insert b.  The byte currently at index will me moved to the right. <p>
    */
    public void insert( byte[] b , int index )
    {
        insertJunk( b.length , index );

        // copy b
        for( int i = 0; i < b.length ; i++ )
        {
            buffy[ index + i ] = b[ i ] ;
        }
    }

    /** Remove bytes from your object. <p>

        @param index Referencing the first byte to be removed. <p>
        @param howMany The number of bytes to remove. <p>
    */
    public void delete( int index , int howMany )
    {
        int newLen = len - howMany ;
        for( int i = index; i < newLen ; i++ )
        {
            buffy[ i ] = buffy[ i + howMany ] ;
        }
        len = newLen ;
    }

    /** If you wish to treat your object like a stack, you can push and pop bytes. <p>

        @param b The byte that will be appended to the end of your object. <p>
    */
    public void push( byte b )
    {
        append( b );
    }

    /** If you wish to treat your object like a stack, you can push and pop bytes. <p>

        Remember the old assembly programming pearl:  May all your pushes be popped. <p>

        @return The last byte in your object.  If there are no more bytes in the object, a zero is returned. <p>
    */
    public byte pop()
    {
        byte returnVal = (byte)0 ;
        if ( len > 0 )
        {
            len--;
            returnVal = buffy[ len ];
        }
        return returnVal ;
    }

    /** A fast way to test the first few bytes of your object. <p>

        @param b An array of bytes to compare to. <p>
        @return True if all of the bytes in b exactly matches the first few bytes in your object. <p>
    */
    public boolean startsWith( byte[] b )
    {
        boolean returnValue = false ;
        if ( len > b.length )
        {
            boolean match = true ;
            for( int i = 0; i < b.length ; i++ )
            {
                if ( buffy[ i ] != b[ i ] )
                {
                    match = false ;
                }
            }
            returnValue = match ;
        }
        return returnValue ;
    }

    /** Find the position within your object of a particular byte. <p>

        @param b The byte to search for. <p>
        @param startPos The byte position in your object to start looking for b. <p>
        @return The position of the byte that matches b where 0 is the first byte of the object.  -1 is returned if a matching byte could not be found. <p>
    */
    public int indexOf( byte b , int startPos )
    {
        boolean done = false ;
        while( ! done )
        {
            if ( startPos >= len )
            {
                startPos = -1 ;
                done = true ;
            }
            else
            {
                if ( buffy[ startPos ] == b )
                {
                    done = true ;
                }
                else
                {
                    startPos++ ;
                }
            }
        }
        return startPos ;
    }

    /** Find the position within your object of a particular sequence of bytes. <p>

        @param b A byte array representing the byte sequence to search for. <p>
        @param startPos The byte position in your object to start looking for b. <p>
        @return -1 is returned if a match could not be found.  Otherwise the position of the first byte of the match is returned. <p>
    */
    public int indexOf( byte[] b , int startPos )
    {
        if ( b.length > 0 )
        {
            boolean done = false ;
            while( ! done )
            {
                startPos = indexOf( b[0] , startPos );
                if ( startPos == -1 )
                {
                    done = true ;
                }
                else
                {
                    if ( startPos + b.length > len )
                    {
                        startPos = -1 ;
                        done = true ;
                    }
                    else
                    {
                        boolean match = true ;
                        for( int i = 1; i < b.length ; i++ )
                        {
                            if ( b[ i ] != buffy[ startPos + i ] )
                            {
                                match = false ;
                            }
                        }
                        if ( match )
                        {
                            done = true ;
                        }
                        else
                        {
                            startPos++ ;
                        }
                    }
                }
            }
        }
        else
        {
            startPos = -1 ;
        }
        return startPos ;
    }

    /** Find the position within your object of a particular character. <p>

        Searching begins with the first byte in your object. <p>

        @param b The byte to search for. <p>
        @return The position of the byte that matches b.  -1 is returned if a matching byte could not be found. <p>
    */
    public int indexOf( byte b )
    {
        return indexOf( b , 0 );
    }

    /** Find the position within your object of a particular sequence of bytes. <p>

        Searching begins with the first byte in your object. <p>

        @param b A byte array representing the byte sequence to search for. <p>
        @return -1 is returned if a match could not be found.  Otherwise the position of the first byte of the match is returned. <p>
    */
    public int indexOf( byte[] b )
    {
        return indexOf( b , 0 );
    }

    /** Replace all instances of one byte with another byte. <p>

        All occurances are replaced. <p>

        Searching for the "next instance" of a byte will begin immediately after the last replacement so that there is no chance of an infinite loop. <p>

        @param b1 The byte to search for. <p>
        @param b2 The byte that is to replace b1. <p>
    */
    public void replace( byte b1 , byte b2 )
    {
        for( int i = 0; i < len ; i++ )
        {
            if ( buffy[ i ] == b1 )
            {
                buffy[ i ] = b2 ;
            }
        }
    }

    private void copyBytesTo( byte[] b , int index )
    {
        for( int i = 0; i < b.length ; i++ )
        {
            buffy[ index + i ] = b[ i ] ;
        }
    }

    /** Replace all instances of one byte with several bytes. <p>

        All occurances are replaced. <p>

        Searching for the "next instance" of a byte will begin immediately after the last replacement so that there is no chance of an infinite loop. <p>

        @param b1 The byte to search for. <p>
        @param b2 The array of bytes that is to replace b1. <p>
    */
    public void replace( byte b1 , byte[] b2 )
    {
        int pos = 0 ;
        while( pos != -1 )
        {
            pos = indexOf( b1 , pos );
            if ( pos != -1 )
            {
                if ( b2.length > 1 )
                {
                    insertJunk( b2.length - 1 , pos );
                }
                else if ( b2.length == 0 )
                {
                    delete( pos , 1 );
                }
                copyBytesTo( b2 , pos );
                pos += b2.length ;
            }
        }
    }

    /** Search for a particular sequence of bytes and replace them with one byte. <p>

        All occurances are replaced. <p>

        Searching for the "next instance" of a byte sequence will begin immediately after the last replacement so that there is no chance of an infinite loop. <p>

        @param b1 A byte array representing a byte sequence to search for. <p>
        @param b2 The byte that is to replace b1. <p>
    */
    public void replace( byte[] b1 , byte b2 )
    {
        int pos = 0 ;
        while( pos != -1 )
        {
            pos = indexOf( b1 , pos );
            if ( pos != -1 )
            {
                if ( b1.length > 1 )
                {
                    delete( pos , b1.length - 1 );
                }
                buffy[ pos ] = b2 ;
                pos++;
            }
        }
    }

    /** Search for a particular sequence of bytes and replace it with a different sequence of bytes. <p>

        All occurances are replaced. <p>

        Searching for the "next instance" of a byte sequence will begin immediately after the last replacement so that there is no chance of an infinite loop. <p>

        @param b1 A byte array representing a byte sequence to search for. <p>
        @param b2 A byte array representing a byte sequence that is to replace b1. <p>
    */
    public void replace( byte[] b1 , byte[] b2 )
    {
        int pos = 0 ;
        while( pos != -1 )
        {
            pos = indexOf( b1 , pos );
            if ( pos != -1 )
            {
                if ( b1.length < b2.length )
                {
                    insertJunk( b2.length - b1.length , pos );
                }
                else if ( b1.length > b2.length )
                {
                    delete( pos , b1.length - b2.length );
                }
                copyBytesTo( b2 , pos );
                pos += b2.length ;
            }
        }
    }

}
