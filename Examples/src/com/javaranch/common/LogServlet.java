package com.javaranch.common;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

/** Used for tracking events in a Servlet. <p>

 Log messages are sent to a logging
 object that defaults to being turned off.  Activating the servlet via a web browser
 gives you access to activate logging for debugging purposes.  <p>

 Override this class and call logMessage() to add messages to the log. <p>

 Override doGet() to disable the error logging feature for production. <p>

 Example:

 <pre>

 *     public class EchoServlet extends LogServlet
 *     {
 *
 *         public void doPost( PrintWriter out , CastMap parms )
 *         {
 *             String text = parms.getString( "text" );
 *             if ( usable( text )  )
 *             {
 *                 for ( int i = 0 ; i < 100 ; i++ )
 *                 {
 *                     out.println( text + "&lt;br>" );
 *                 }
 *             }
 *             else
 *             {
 *                 reportProblem( out , "invalid parameters" );
 *             }
 *         }
 *
 *     }


 </pre>

 - - - - - - - - - - - - - - - - - <p>

 Copyright (c) 1999-2004 Paul Wheaton <p>
 Copyright (c) 1999-2000 EarthWatch, Inc. <p>

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
public abstract class LogServlet extends HttpServlet
{

    private static String accessParameterName = "secretServletKey";
    private static String accessParameterValue = "funkyMonkey";

    /** increment this if you override the traditional doPost() */
    protected int postCount = 0;

    /** increment this if you override the traditional doGet() */
    protected int getCount = 0;

    private ErrorLog log;

    // during the init cycle, console logging is temporarily turned on.  If the user
    // calls setLogConsole during the init cycle, consoleLock is set to true and console
    // logging won't be turned off. <p>
    private boolean consoleLock = false;

    protected LogServlet()
    {
        log = new ErrorLog();
    }

    public static void setAccessParameterName( String accessParameterName )
    {
        LogServlet.accessParameterName = accessParameterName;
    }

    public static void setAccessParameterValue( String accessParameterValue )
    {
        LogServlet.accessParameterValue = accessParameterValue;
    }

    private static String getAccessParameterPhrase()
    {
        return accessParameterName + '=' + accessParameterValue;
    }

    /** Messages are logged to memory so they can be viewed by a GET command. <p>
     */
    protected void setLogMemory( boolean on )
    {
        log.setInternalLog( on );
    }

    /** Messages are sent to STDOUT so they can be tracked in the servlet server logs. <p>
     */
    protected void setLogConsole( boolean on )
    {
        log.setConsole( on );
        consoleLock = true;
    }

    /** Call this as the first thing you do if you override the traditional doGet() or doPost(). <p>

     This makes sure that console logging is turned off if it is no longer desired. <p>
     */
    protected void checkConsoleLogging()
    {
        if ( !consoleLock )
        {
            log.setConsole( false );
            consoleLock = true;
        }
    }

    /** Add a message to the log. <p>
     */
    public void logMessage( String s )
    {
        s = '(' + getClass().getName() + ") " + s;
        log.add( s );
    }

    /** This convenience method sends the message to the log and sends it to out. <p>
     */
    protected void reportProblem( PrintWriter out , String message )
    {
        logMessage( message );
        out.println( message );
    }

    /** Convenience method - returns true if the string is not null and not empty. <p>
     *
     * @deprecated use Str.usable() instead
     */
    static protected boolean usable( String s )
    {
        return ( ( s != null ) && ( s.length() > 0 ) );
    }

    /** E-mail a message to the admin and add the same message to the log. <p>
     */
    public void emailMessage( String s )
    {
        String from = getClass().getName();  // the name of this servlet
        try
        {
            String mailBody = "This e-mail is automatically generated by the servlet " + from + ".\n\n" + s;
            Email.reportError( mailBody );
            logMessage( "e-mail sent: " + s );
        }
        catch ( Exception e )
        {
            logMessage( "Could not e-mail message: " + s );
        }
    }

    /** E-mail a message to the admin, send it to the user and add it to the log. <p>
     */
    public void emailMessage( PrintWriter out , String s )
    {
        out.println( s );
        emailMessage( s );
    }

    /** Override this if you have more to say in the GET debug information. <p>
     */
    public void debugReport( PrintWriter out )
    {
    }

    private static boolean validAccess( HttpServletRequest req )
    {
        boolean returnVal = false;
        String password = req.getParameter( accessParameterName );
        if ( password != null )
        {
            returnVal = password.equals( accessParameterValue );
        }
        return returnVal;
    }

    // extract all of the parameters from HttpServletRequest so they can be passes seperately.
    private static CastMap getParms( HttpServletRequest req )
    {
        CastMap parms = new CastMap();
        Enumeration enumeration = req.getParameterNames();
        while ( enumeration.hasMoreElements() )
        {
            String key = (String) enumeration.nextElement();
            parms.put( key , req.getParameter( key ) );
        }
        return parms;
    }

    /** if you need doPost(), override this instead of the normal doPost(). <p>

     @param out use this to send HTML to the browser. <p>
     @param parameters all of the parameters sent to the servlet are stored in this hash table. <p>
     */
    protected void doPost( PrintWriter out , CastMap parameters )
    {
    }

    void trackDoPost()
    {
        checkConsoleLogging();
        postCount++;
    }

    public void doPost( HttpServletRequest req , HttpServletResponse resp ) throws ServletException , IOException
    {
        trackDoPost();
        resp.setContentType( "text/html" );
        try
        {
            PrintWriter out = resp.getWriter();
            CastMap parms = getParms( req );
            try
            {
                doPost( out , parms );
            }
            catch ( Exception e )
            {
                logMessage( "exception encountered in POST: " + e );
            }
            out.close();
        }
        catch ( Exception e )
        {
            logMessage( "cannot get writer: " + e );
        }
    }

    /** if you need doGet, override this instead of the normal doGet. <p>

     @param out use this to send HTML to the browser. <p>
     @param parameters all of the parameters sent to the servlet are stored in this hash table. <p>
     */
    protected void doGet( PrintWriter out , CastMap parameters )
    {
    }

    boolean showStatus( HttpServletRequest req , HttpServletResponse resp )
    {
        boolean returnVal = false;
        if ( validAccess( req ) )
        {
            returnVal = true;
            resp.setContentType( "text/html" );
            String url = req.getRequestURI();

            // some servers strip this info out, so I need to put it back in
//            if ( url.indexOf( "servlet/" ) == -1 )
//            {
//                Str fix = new Str( url );
//                fix.insert( "/servlet" , fix.lastIndexOf( '/' ) );
//                url = fix.toString();
//            }

            try
            {
                PrintWriter out = resp.getWriter();
                String parm = req.getParameter( "log" );
                String accessPhrase = '?' + getAccessParameterPhrase();
                if ( parm == null )
                {
                    out.println( "<html>" );

                    String name = "status page for " + getClass().getName();
                    out.println( "<head><title>" + name + "</title></head>" );
                    out.println( "<h2>" + name + "</h2>" );

                    out.println( "POST count = " + postCount + "<p>" );
                    out.println( "GET count = " + getCount + "<p>" );
                    out.print( "logging to stdout.log is currently " );
                    if ( log.isConsoleLogOn() )
                    {
                        out.println( "on . . . . <a href=" + url + accessPhrase + "&log=conoff>turn off</a><p>" );
                    }
                    else
                    {
                        out.println( "off . . . . <a href=" + url + accessPhrase + "&log=conon>turn on</a><p>" );
                    }
                    out.print( "logging to memory buffer is currently " );
                    if ( log.isInternalLogOn() )
                    {
                        out.println( "on . . . . <a href=" + url + accessPhrase + "&log=memoff>turn off</a><p>" );
                        String[] s = log.getList();
                        if ( s == null )
                        {
                            out.println( "no log messages.<p>" );
                        }
                        else
                        {
                            out.println( "log messages:<p><ul>" );
                            for ( int i = 0; i < s.length ; i++ )
                            {
                                out.println( "    <li>" + s[ i ] + "<p>" );
                            }
                            out.println( "</ul>" );
                        }
                    }
                    else
                    {
                        out.println( "off . . . . <a href=" + url + accessPhrase + "&log=memon>turn on</a><p>" );
                    }
                    out.println( "<p><hr><p>" );
                    debugReport( out );
                    out.println( "</html>" );
                }
                else
                {
                    if ( parm.equals( "conon" ) )
                    {
                        log.setConsole( true );
                        out.println( "future log entries will now be routed to stdout.log<p>" );
                    }
                    else if ( parm.equals( "conoff" ) )
                    {
                        log.setConsole( false );
                        out.println( "further log entries will not be routed to stdout.log<p>" );
                    }
                    else if ( parm.equals( "memon" ) )
                    {
                        log.setInternalLog( true );
                        out.println( "future log entries will now be kept in memory<p>" );
                    }
                    else if ( parm.equals( "memoff" ) )
                    {
                        log.setInternalLog( false );
                        out.println( "The memory log has been erased and further log entries will not be kept in memory<p>" );
                    }
                }
                out.println( "\n\n<p><a href=" + url + accessPhrase + ">get new servlet data</a><p>" );
                out.close();
            }
            catch ( IOException e )
            {
                logMessage( "could not get 'out' for logging ..." );
            }
        }
        return returnVal;
    }

    void trackDoGet()
    {
        checkConsoleLogging();
        getCount++;
    }

    /** Override this to make the debugger log unavailable. <p>
     */
    public void doGet( HttpServletRequest req , HttpServletResponse resp )
    {
        trackDoGet();
        if ( ! showStatus( req , resp ) )
        {
            resp.setContentType( "text/html" );
            try
            {
                PrintWriter out = resp.getWriter();
                CastMap parms = getParms( req );
                try
                {
                    doGet( out , parms );
                }
                catch ( Exception e )
                {
                    reportProblem( out , "exception encountered in GET: " + e );
                }
                out.close();
            }
            catch ( Exception e )
            {
                logMessage( "cannot get writer: " + e );
            }
        }
    }


}




