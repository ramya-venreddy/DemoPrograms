package com.javaranch.common ;

import java.io.* ;
import java.net.* ;
import java.util.zip.* ;

/** This class simplifies the exchange of objects with Servlets via the HTTP
    protocol. <p>

    This class is usually used by Applets, but it can also be used by applications
    and other servlets. <p>

    All sending is embedded within an HTTP POST command. <p>

    All objects sent and received must implement Serializable.  <p>

    The client-side methods in this class interact with the server-side methods in
    com.digitalglobe.common.Servlets, or a servlet that extends com.digitalglobe.common.ObjectServlet. <p>


    - - - - - - - - - - - - - - - - - <p>

    Copyright (c) 1999-2004 Paul Wheaton <p>
    Copyright (c) 1999-2000 EarthWatch, Inc.

    You are welcome to do whatever you want to with this source file provided
    that you maintain this comment fragment (between the dashed lines).
    Modify it, change the package name, change the class name ... personal or business
    use ...  sell it, share it ... add a copyright for the portions you add ... <p>

    My goal in giving this away and maintaining the copyright is to hopefully direct
    developers back to JavaRanch. <p>

    I originally developed this class while working as a contractor at EarthWatch, Inc. in
    Longmont, Colorado.  They gave me permission to distribute this code this way provided
    that their message would also be carried along.  Their message is that they hire
    Java programmers and would like you to consider working with them.  I have to say
    that my experience with them was first rate and I would encourage engineers to work there.
    Check out their web site at <a href=http://www.digitalglobe.com>http://www.digitalglobe.com</a>. <p>

    The original source can be found at <a href=http://www.javaranch.com>JavaRanch</a> <p>

    - - - - - - - - - - - - - - - - - <p>

    @author Paul Wheaton <p>
*/
public class HTTP
{

    // just to keep people from creating an instance of this
    private HTTP(){}

    private static URLConnection getConnection( String servlet ) throws IOException
    {
        // I don't specify POST at any point, but somehow a POST is done

        URL u = new URL( servlet );

        URLConnection con = u.openConnection();
          // supposed to return a HttpURLConnection object, but doesn't

        con.setDoInput( true );
        con.setDoOutput( true );
        con.setUseCaches( false );
        con.setRequestProperty("Content-type","application/octet-stream");
        con.setAllowUserInteraction( false );

        // Work around a Netscape bug?
        // I found this "fix" in a couple of different places.  But if you
        // put it in, it screws up the object pipeline.
        // con.setRequestProperty("Content-Type",
        //                        "application/x-www-form-urlencoded");

        // con.setRequestMethod("POST");
        // part of HttpURLConnection, not URLConnection

        return con ;
    }

    private static void sendObject( URLConnection con , Object obj ) throws IOException
    {
        ObjectOutputStream out = new ObjectOutputStream( new GZIPOutputStream( con.getOutputStream() ) );
        if ( obj != null )
        {
            out.writeObject( obj );
        }
        out.close();
    }

    private static Object convertByteArrayToObject( byte[] b ) throws Exception
    {
        ObjectInputStream in = new ObjectInputStream( new ByteArrayInputStream( b ) );
        return in.readObject();
    }

    // I used to just pass the object.  But a certain quantity of objects freaks something out.
    // Converting the object to a byte array and passing just the array, then converting the array
    // to the object works fine.
    private static Object receiveObject( URLConnection con ) throws Exception
    {
        ObjectInputStream in = new ObjectInputStream( new GZIPInputStream( con.getInputStream() ) );
        byte[] b = (byte[])in.readObject();
        in.close();
        return convertByteArrayToObject( b );
    }

    /** Send an HTTP POST command and an object to a servlet and generate Exceptions, if any. <p>

        @param servlet The URL for the servlet being accessed. <p>

        @param obj Any serializable object. <p>
    */
    public static Object sendE( String servlet , Object obj ) throws Exception
    {
        //System.out.println( "xxx trying to connect to " + servlet );
        URLConnection con = getConnection( servlet );
        sendObject( con , obj );
        return receiveObject( con );
    }

    /** Send an HTTP POST command and an object to a servlet and absorb Exceptions, if any. <p>

        Exceptions will be printed to STDOUT. <p>

        @param servlet The URL for the servlet being accessed. <p>

        @param obj Any serializable object. <p>
    */
    public static Object send( String servlet , Object obj )
    {
        Object returnVal = null ;
        try
        {
            returnVal = sendE( servlet , obj );
        }
        catch( Exception e )
        {
            System.out.println( "HTTP: " + e );
        }
        return returnVal ;
    }

    /** Will eliminate leading typos and make sure the string starts with "http://" or doesn't. <p>
    */
    public static String normalizeURL( String s )
    {
        String returnVal = null ;
        if ( s != null )
        {
            if ( s.startsWith( "http://" ) )
            {
                returnVal = s ;
            }
            else
            {
                int pos = s.indexOf( '.' ) - 1 ;
                if ( pos != -1 )
                {
                    // move backwards until a colon or a slash is found.
                    boolean done = false ;
                    while ( ! done )
                    {
                        if ( pos < 0 )
                        {
                            done = true ;
                            pos = 0 ;
                            returnVal = "http://" + s ;
                        }
                        else
                        {
                            if ( "/:".indexOf( s.charAt( pos ) ) == -1 )
                            {
                                pos-- ;
                            }
                            else
                            {
                                done = true ;
                                returnVal = "http://" + s.substring( pos + 1 );
                            }
                        }
                    }
                }
            }
        }
        return returnVal ;
    }

}



