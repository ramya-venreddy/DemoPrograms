
package com.javaranch.common ;

import javax.servlet.* ;
import javax.servlet.http.* ;
import java.io.* ;

/** Used for passing objects back and forth between applets and servlets. <p>

    All objects are passed via the post method. <p>

    Override doObject() to provide functionality to the servlet. <p>

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

    @author Paul Wheaton
*/
public abstract class ObjectServlet extends LogServlet
{

    /** You receive an object and you give an object back. <p>

        Use this if you don't need the req object. <p>
    */
    public Object doObject( Object obj ) throws Exception
    {
        return null ;
    }

    /** You receive an object and you give an object back. <p>

        Use this if you need the req object. <p>
    */
    public Object doObject( Object obj , HttpServletRequest req ) throws Exception
    {
        return doObject( obj );
    }

    /** Internal use only. <p>

        It must be made public so the servlet server can find it.  It will catch the
        POST request, collect the object from the client and pass it to you via doObject().
        The object you return from doObject() will be passed back to the client. <p>
    */
    public final void doPost( HttpServletRequest req , HttpServletResponse resp )
    {
        checkConsoleLogging();
        postCount++ ;
        logMessage("POST");
        try
        {
            Object obj = Servlets.getObjectFromClient( req ) ;
            if ( obj == null )
            {
                logMessage( "received null from client" );
            }
            else
            {
                logMessage( "object received" );
                try
                {
                    obj = doObject( obj , req );
                    logMessage( "doObject done" );
                    try
                    {
                        Servlets.sendObjectToClient( resp , obj );
                    }
                    catch( Exception e )
                    {
                        logMessage( "error sending object to client: " + e );
                    }
                }
                catch( Exception e )
                {
                    logMessage( "error in doObject:" + e );
                }
            }
        }
        catch ( Exception e )
        {
            logMessage( "error getting object from client: " + e );
        }
        logMessage( "POST complete" );
    }

}



