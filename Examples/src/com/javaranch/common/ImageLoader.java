
package com.javaranch.common ;

import java.awt.* ;
import java.net.* ;
import javax.swing.* ;

/** This class helps you load images before you use them. <p>

    Java allows you to show parts of an image before the whole image is loaded.
    This class forces the entire image to be loaded before you attempt to use it. <p>


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

public class ImageLoader
{

    /** Pass in a GIF or JPEG image stored as a byte array and get a Java Image object back. <p>

        @return null if the byte array could not be converted. <p>
    */
    public static Image get( byte[] imageBytes )
    {
        Image returnVal = null ;

        if ( imageBytes != null )
        {
            ImageIcon i = new ImageIcon( imageBytes );
            if ( i.getIconWidth() > 0 )
            {
                returnVal = i.getImage();
            }
        }

        return returnVal ;
    }

    /** Pass in a filename or URL and get an image back. <p>

        If the name starts with "file:", "jar:" or "http:", the string will first be converted to a
        URL before getting the object.  Otherwise, the string is assumed to be a filename. <p>

        @return null if the file cannot be loaded. <p>
    */
    public static Image get( String where )
    {
        Image returnVal = null ;

        ImageIcon i = null ;

        if ( where != null )
        {
            if ( where.startsWith( "file:" ) || where.startsWith( "jar:" ) || where.startsWith( "http:" ) )
            {
                try
                {
                    i = new ImageIcon( new URL( where ) );
                }
                catch( MalformedURLException e )
                {
                    // do nothing
                }
            }
            else
            {
                i = new ImageIcon( where );
            }
        }

        if ( i != null )
        {
            if ( i.getIconWidth() > 0 )
            {
                returnVal = i.getImage();
            }
        }

        return returnVal ;
    }

    /** Pass in an object and a relative filename and the image will be pulled up relative to where the class came from. <p>

        Suppose you have a class called com.cows.MooPanel.  And in the constructor you want to load your image
        conveniently located at com/cows/images/mooing_cow.jpg.  This image sits in the subdirectory which sits in
        the same directory as the com.cows.MooPanel class!  But is your class being used from a jar file?  Over the
        internet?  Worry no more!  This method will load your image through the same pipe that the class was loaded.
        In this example, call ImageLoader.get( this , "images/mooing_cow.jpg" ); <p>

        @param obj The address for the image you are looking for is relative to this object. <p>
        @param where The address for the image you are looking for relative to the given object. <p>

        @return null if the file cannot be loaded. <p>
    */
    public static Image get( Object obj, String where )
    {
        Image returnVal = null;
        URL u = obj.getClass().getResource( where );
        if ( u != null )
        {
            ImageIcon i = new ImageIcon( u );
            if ( i.getIconWidth() > 0 )
            {
                returnVal = i.getImage();
            }
        }
        return returnVal;
    }

}


