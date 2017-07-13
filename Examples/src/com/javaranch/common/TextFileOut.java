package com.javaranch.common ;

import java.io.*;

/** A convenience class for writing text files. <p>

    Example usage of TextFileOut:<p>

    <pre>

    * String[] s;
    *
    * // fill array with lovely prose
    *
    * TextFileOut f = new TextFileOut( "file.txt" );
    * for( int i = 0 ; i < s.length ; i++ )
    * {
    *     f.println( s[ i ] );
    * }
    * f.close();

    </pre><p>


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
public final class TextFileOut extends BufferedWriter
{
    /** Create a new TextFileOut object that will start writing at the beginning of the file. <p>
    */
    public TextFileOut( String filename ) throws IOException
    {
        super( new FileWriter( filename ) );
    }

    /** Same as the other constructor only this will open the file for appending. <p>

        @param filename The name of the file to open. <p>
        @param append Pass in "true" to append. <p>
    */
    public TextFileOut( String filename , boolean append ) throws IOException
    {
        super( new FileWriter( filename , append ) );
    }

    /** Send a line of text to the file with a trailing new line appropriate for the o/s. <p>

        @param s The string to send. <p>
    */
    public void println( String s ) throws IOException
    {
        print( s );
        newLine();
    }

    /** Send a newline to the file. <p>
    */
    public void println() throws IOException
    {
        newLine();
    }

    /** Send a line of text to the file with a trailing new line appropriate for the o/s. <p>

        @param s The string to send. <p>
    */
    public void writeLine( String s ) throws IOException
    {
        println( s );
    }

    /** Send a line of text to the file. <p>

        Newlines embedded in the string are properly converted.  If that is not
        what you want, call write() instead. <p>

        @param s The string to send. <p>
        @exception IOException
    */
    public void print( String s ) throws IOException
    {
        if ( s.indexOf( '\n' ) == -1 )
        {
            write( s );
        }
        else
        {
            // there are newline characters and BufferedWriter does not handle them properly
            Str buffy = new Str( s );
            boolean done = false ;
            while( ! done )
            {
                int newlinePos = buffy.indexOf( '\n' );
                if ( newlinePos == -1 )
                {
                    write( buffy.toString() );
                    done = true ;
                }
                else
                {
                    write( buffy.before( newlinePos ).toString() );
                    newLine();
                    buffy = buffy.after( newlinePos );
                }
            }
        }
    }

}
