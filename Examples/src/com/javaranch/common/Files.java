package com.javaranch.common ;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

/**
 * Contains a collection of static methods related to files. <p>
 * <p/>
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
 * @author Carl Trusiak
 */

public class Files
{

    private Files()
    {
    } // just to make sure someone doesn't try to instantiate this.


    /**
     * Pass in an object and a relative filename and the file will be opened relative to where the class came from. <p>
     * <p/>
     * Suppose you have a class called com.cows.Moo.  And in the constructor you want to load your essay on cows
     * conveniently located at com/cows/data/mooing_cow.txt.  This file sits in the subdirectory which sits in the same
     * directory as the com.cows.Moo class!  But is your class being used from a jar file?  Over the internet?  Worry no
     * more!  This method will load your file through the same pipe that the class was loaded. In this example, call
     * getResourceAsString( this , "data/mooing_cow.txt" ); <p>
     *
     * @param obj      The address for the file you are looking for is relative to this object. <p>
     * @param filename The address for the file you are looking for relative to the given object. <p>
     * @return null if the file cannot be loaded. <p>
     */
    public static String getResourceAsString( Object obj, String filename ) throws Exception
    {
        String returnVal = null;

        URL u = obj.getClass().getResource( filename );

        if ( u != null )
        {
            InputStream in = u.openStream();
            if ( in != null )
            {
                BufferedReader r = new BufferedReader( new InputStreamReader( in ) );
                Str s = new Str();
                boolean done = false;
                while ( ! done )
                {
                    String line = r.readLine();
                    if ( line == null )
                    {
                        done = true;
                    }
                    else
                    {
                        s.append( line );
                        s.append( '\n' );
                    }
                }
                r.close();
                in.close();
                returnVal = s.toString();
            }
        }

        return returnVal;
    }

    /**
     * All of a text file is read in and stored in a Vector. <p>
     *
     * @param fileName The name of the text file to read in. <p>
     * @return The Vector where each object in the Vector contains one line from the text file. <p>
     * @throws IOException <p/>
     * @deprecated
     */
    public static Vector fileToVector( String fileName ) throws IOException
    {
        Vector v = new Vector( 100 );
        TextFileIn f = new TextFileIn( fileName );
        String s;
        while ( ( s = f.readLine() ) != null )
        {
            v.addElement( s );
        }
        f.close();
        return v;
    }

    /**
     * All of a text file is read in and stored in a Vector. <p>
     *
     * @param fileName The name of the text file to read in. <p>
     * @return The Vector where each object in the Vector contains one line from the text file. <p>
     * @throws IOException <p/>
     */
    public static List fileToList( String fileName ) throws IOException
    {
        List list = new ArrayList();
        TextFileIn f = new TextFileIn( fileName );
        String s;
        while ( ( s = f.readLine() ) != null )
        {
            list.add( s );
        }
        f.close();
        return list;
    }

    /**
     * All of a text file is read in and stored in a String array. <p>
     *
     * @param fileName The name of the text file to read in. <p>
     * @return The String array where each String contains one line from the text file. <p>
     * @throws IOException <p/>
     */
    public static String[] fileToArray( String fileName ) throws IOException
    {
        List list = fileToList( fileName );
        String[] a = new String[ list.size() ];
        list.toArray( a );
        return a;
    }

    /**
     * Replaced with toTextFile(). <p>
     */
    public static void arrayToFile( String[] s, String fileName ) throws IOException
    {
        toTextFile( s, fileName );
    }

    /**
     * Each element of a List has the toString() method performed on it and written to a text file. <p>
     *
     * @param list     The collection of objects to be written out. <p>
     * @param fileName The name of the text file to be created. <p>
     */
    public static void toTextFile( List list, String fileName ) throws IOException
    {
        TextFileOut f = new TextFileOut( fileName );
        for ( int i = 0; i < list.size() ; i++ )
        {
            f.writeLine( list.get( i ).toString() );
        }
        f.close();
    }

    /**
     * Each element of a string array is written to a text file. <p>
     *
     * @param s        The collection of objects to be written out. <p>
     * @param fileName The name of the text file to be created. <p>
     */
    public static void toTextFile( String[] s, String fileName ) throws IOException
    {
        TextFileOut f = new TextFileOut( fileName );
        for ( int i = 0; i < s.length ; i++ )
        {
            f.writeLine( s[ i ] );
        }
        f.close();
    }

    /**
     * Delete a specific file. <p>
     * <p/>
     * If the file does not exist, nothing is done. <p>
     */
    public static void delete( String fileName )
    {
        File f = new File( fileName );
        f.delete();
    }

    /**
     * Deletes files in a specific directory. <p>
     * <p/>
     * All subdirectories are ignored. <p>
     */
    public static void deleteAllFilesInDir( String directoryName )
    {
        File directory = new File( directoryName );
        if ( directory.exists() && directory.isDirectory() )
        {
            String[] files = getAllFilesInDirectory( directoryName );
            for ( int i = 0; i < files.length ; i++ )
            {
                File file = new File( directory , files[ i ] );
                if ( file.isFile() )
                {
                    file.delete();
                }
            }
        }
    }

    /**
     * Change the file name. <p>
     * <p/>
     * If dest already exists, it is deleted. <p>
     */
    public static void rename( String source, String dest )
    {
        delete( dest );
        File f = new File( source );
        f.renameTo( new File( dest ) );
    }

    /**
     * Write one object to a file. <p>
     * <p/>
     * If the file already exists, it is overwritten. <p>
     *
     * @return true if file write successful. <p>
     */
    public static boolean writeObject( String fileName, Object obj )
    {
        boolean returnVal = false;
        try
        {
            ObjectOutputStream f = new ObjectOutputStream( new FileOutputStream( fileName ) );
            f.writeObject( obj );
            f.close();
            returnVal = true;
        }
        catch ( Exception e )
        {
            System.out.println( "could not write file " + fileName + ": " + e );
        }

        return returnVal;
    }

    /**
     * Read one object from a file. <p>
     *
     * @return object or null if file read unsuccessful. <p>
     */
    public static Object readObject( String fileName )
    {
        Object returnVal = null;
        try
        {
            ObjectInputStream f = new ObjectInputStream( new FileInputStream( fileName ) );
            try
            {
                returnVal = f.readObject();
            }
            finally
            {
                f.close();
            }
        }
        catch ( Exception e )
        {
            System.out.println( "could not read file " + fileName + ": " + e );
        }

        return returnVal;
    }


    /**
     * Read a file from the disk into a byte array. <p>
     * <p/>
     * Will throw an IOException if one is encountered. <p>
     *
     * @param filename The name of the file to read. <p>
     * @return A byte array that is the exact same size as the file. <p>
     * @throws IOException
     */
    public static byte[] fileToByteArrayE( String filename ) throws IOException
    {
        RandomAccessFile f = new RandomAccessFile( filename, "r" );
        byte[] buffy = new byte[ (int)f.length() ];
        f.read( buffy );
        f.close();
        return buffy;
    }


    /**
     * Read a file from the disk into a byte array. <p>
     * <p/>
     * If an IOException is encountered, a message is printed to STDOUT and null is returned. <p>
     *
     * @param filename The name of the file to read. <p>
     * @return A byte array that is the exact same size as the file. <p>
     */
    public static byte[] fileToByteArray( String filename )
    {
        byte[] returnVal = null;
        try
        {
            returnVal = fileToByteArrayE( filename );
        }
        catch ( IOException e )
        {
            System.out.println( "error reading file " + filename + ": " + e );
        }
        return returnVal;
    }

    public static void byteArrayToFile( byte[] data, String filename ) throws IOException
    {
        FileOutputStream out = new FileOutputStream( filename );
        out.write( data );
        out.close();
    }


    /**
     * Return a string array containing the current directory. <p>
     * <p/>
     * Directories will have angle brackets. <p>
     * <p/>
     * If there is a parent directory, the first entry will be a .. directory. <p>
     */
    public static String[] getDirectory( File f )
    {
        boolean hasParent = ( f.getParent() != null );
        String[] original = f.list();
        String[] s; // where the new string array will be
        int offset = 0; // if there is a parent, the offset will bump up one to make room for <..>
        if ( hasParent )
        {
            offset++;
            s = new String[ original.length + 1 ];
            s[ 0 ] = "<..>";
        }
        else
        {
            s = new String[ original.length ];
        }
        for ( int i = 0; i < original.length ; i++ )
        {
            String filename = original[ i ];
            File tempFile = new File( f, filename );
            if ( tempFile.isDirectory() )
            {
                filename = '<' + filename + '>';
            }
            s[ i + offset ] = filename;
        }
        return s;
    }

    /**
     * Return a string array containing the names of all the files (not directories) in the current directory. <p>
     * <p/>
     * If an invalid directory is passed in, or if the directory contains no valid files, an array of length zero is
     * returned.
     */
    public static String[] getAllFilesInDirectory( String directory )
    {
        String[] returnVal = new String[0];
        File f = new File( directory );
        if ( f.isDirectory() )
        {
            File[] files = f.listFiles();
            List list = new ArrayList();
            for ( int i = 0; i < files.length ; i++ )
            {
                File file = files[ i ];
                if ( ! file.isDirectory() )
                {
                    list.add( file.getName() );
                }
            }
            String[] names = new String[ list.size() ];
            if ( names.length > 0 )
            {
                list.toArray( names );
            }
            returnVal = names;
        }
        return returnVal;
    }

    /**
     * Deletes a directory structure.
     *
     * @param dir Directory to delete.
     */
    public static void deleteDirectory( File dir ) throws IOException
    {
        if ( ( dir == null ) || ! dir.isDirectory() )
        {
            throw new IllegalArgumentException( "Argument " + dir + " is not a directory. " );
        }
        File[] contents = dir.listFiles();
        for ( int i = 0; i < contents.length ; i++ )
        {
            if ( contents[ i ].isDirectory() )
            {
                deleteDirectory( contents[ i ] );
            }
            else
            {
                contents[ i ].delete();
            }
        }
        dir.delete();
    }


    /**
     * Return a string array containing the current directory with timestamps and file sizes. <p>
     * <p/>
     * Directories will have angle brackets. <p>
     * <p/>
     * If there is a parent directory, the first entry will be a .. directory. <p>
     */
    public static String[] getDirectoryDetails( File f )
    {
        // create two arrays:  s contains the filenames and details contains the timestamps and file sizes.
        // After the arrays are created, create one nice looking string array and return that.

        boolean hasParent = ( f.getParent() != null );

        // When running Netscape on Win95, "C:\" is apparently not a directory, but "C:\." is
        Str temp = new Str( f.toString() );
        if ( temp.getLast() == '\\' )
        {
            temp.append( '.' );
            f = new File( temp.toString() );
        }

        String[] original = f.list();
        String[] s; // where the new string array will be
        String[] details;
        int offset = 0; // if there is a parent, the offset will bump up one to make room for <..>
        if ( hasParent )
        {
            offset++;
            s = new String[ original.length + 1 ];
            details = new String[ original.length + 1 ];
            s[ 0 ] = "<..>";
            details[ 0 ] = "";
        }
        else
        {
            s = new String[ original.length ];
            details = new String[ original.length ];
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy/MM/dd hh:mm:ss " );
        int longestFilename = 4;
        int longestDetail = 0;
        for ( int i = 0; i < original.length ; i++ )
        {
            String filename = original[ i ];
            String detail = "";
            File tempFile = new File( f, filename );
            if ( tempFile.isDirectory() )
            {
                filename = '<' + filename + '>';
            }
            else
            {
                Date d = new Date( tempFile.lastModified() );
                detail = dateFormat.format( d ) + Str.commaStr( tempFile.length() );
            }

            longestFilename = Math.max( longestFilename, filename.length() );
            longestDetail = Math.max( longestDetail, detail.length() );

            s[ i + offset ] = filename;
            details[ i + offset ] = detail;
        }

        // mash the details into s

        for ( int i = 0; i < s.length ; i++ )
        {
            Str str = new Str( s[ i ] );

            // left align the names
            str.left( longestFilename + 1 );

            Str detail = new Str( details[ i ] );

            if ( detail.length() > 0 )
            {
                // right align the file size
                int spacesToInsert = longestDetail - detail.length();
                if ( spacesToInsert > 0 )
                {
                    detail.insert( Str.spaces( spacesToInsert ), 19 );
                }
                str.append( detail );
                str.append( ' ' );
            }

            s[ i ] = str.toString();
        }

        return s;
    }

    public static void copy( InputStream in, OutputStream out ) throws Exception
    {
        int bufferSize = 64 * 1024;
        byte[] buffy = new byte[ bufferSize ];
        boolean done = false;
        while ( ! done )
        {
            int bytesRead = in.read( buffy );
            if ( bytesRead == -1 )
            {
                done = true;
            }
            else
            {
                out.write( buffy, 0, bytesRead );
                if ( bytesRead < bufferSize )
                {
                    done = true;
                }
            }
        }
        in.close();
        out.close();
    }

    public static boolean appendText( String filename, String text )
    {
        boolean returnVal = false;
        try
        {
            TextFileOut out = new TextFileOut( filename, true );
            out.println( text );
            out.close();
            returnVal = true;
        }
        catch ( Exception e )
        {
        }
        return returnVal;
    }


    /**
     * All of a text file is read in and unique Strings are stored in a String array. <p>
     *
     * @param fileName The name of the text file to read in. <p>
     * @return The String array where each String contains one unique line from the text file. <p>
     * @throws IOException <p/>
     */
    public static String[] fileToUniqueArray( String fileName ) throws IOException
    {
        Set h = fileToHashSet( fileName );
        String[] a = new String[ h.size() ];
        a = (String[])h.toArray( a );
        return a;
    }

    /**
     * All of a text file is read in and unique elements are stored in a HashSet. <p>
     *
     * @param fileName The name of the text file to read in. <p>
     * @return The HashSet where each element contains one unique line from the text file. <p>
     * @throws IOException <p/>
     */
    public static Set fileToHashSet( String fileName ) throws IOException
    {
        TextFileIn f = new TextFileIn( fileName );
        String s;
        Set h = new HashSet();
        while ( ( s = f.readLine() ) != null )
        {
            h.add( s );
        }
        return h;
    }


}

