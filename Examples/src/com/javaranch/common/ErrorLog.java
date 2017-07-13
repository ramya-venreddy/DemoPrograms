package com.javaranch.common ;

import java.io.*;
import java.util.*;
import java.util.List;
import java.awt.*;
import java.text.*;

/** ErrorLog is a simple way of handling warnings and errors in your programs. <p>

    Rather than having 20 lines of error handling for each occurance of an error,
    you can have just one.  When the time comes to handle your errors a different
    way, you don't have to find a thousand error locations and rewrite the handling:
    just change the ErrorLog initialization. <p>

    To use ErrorLog, create a log instance:

    <ul><pre>
    ErrorLog log = new ErrorLog();
    </pre></ul>

    and then when the error occurs:
    <ul><pre>
    log.add( "file " + fileName + " corrupted at byte " + pos );
    </pre></ul>

    The time and date will be added to your message and written to System.err. <p>


    <ul>
    FILE LOGGING<p>
    <ul>
    To activate this feature, use the setLogFile() method.  To deactivate this feature,
    or to complete file logging, use the setLogFileOff() method.  When activated, all
    error messages will be copied to a text file (using the name provided in setLogFile()).
    By default, this feature is off. <p>

    Example:<ul><pre>

    ErrorLog log = new ErrorLog();
    log.setLogFile( "errors.txt" ); // turn on text file logging

    ...

    log.add( "file " + fileName + " corrupted at byte " + pos );
    // this error message is sent to the console <b><i>and</i></b> copied to the file "errors.txt"

    ...

    // end of the program
    log.setLogFileOff();
    </pre></ul>

    Note that a "file flush" is performed after each error message is processed.
    This is in case your program is prematurely terminated:  the file will still contain
    all of the messages you sent (instead of having them in a buffer in memory waiting for
    the file to be closed). <p>

    Note also that setLogFileOff() does not have to be called when your program finishes, although it
    is good programming practice.  In other words, you will not lose any data from your file
    if you do not call setLogFileOff(). <p>
    </ul>
    INTERNAL LOGGING<p>
    <ul>
    To activate/deactivate this feature, use the setInternalLog() method.  When activated,
    all error messages will be stored inside the ErrorLog object.  By default,
    this feature is off. <p>

    Example:
    <ul><pre>

    ErrorLog log = new ErrorLog();
    log.setInternalLog( true ); // turn on internal logging

    ...

    log.add( "file " + fileName + " corrupted at byte " + pos );
    // this error message is stored in Errors <b><i>and</i></b> copied to the console

    ...

    //When you are ready to extract the log:

    String[] errors = log.getList();
    </pre></ul>

    </ul>
    DIALOG DISPLAY<p>
    <ul>
    If your program is a GUI application or applet, you may want to notify your user of an
    error as you add it to the log.  Do this by using the addAndDisplay() method.  It works exactly
    the same as the add() method except it also copies the message into a Dialog which it creates. <p>

    Example:<ul><pre>

    ErrorLog log = new ErrorLog();

    ...

    log.addAndDisplay( myPanel , "file " + fileName + " corrupted" );
    // this error message is written to the console <b><i>and</i></b> instantly
    // appears in a Dialog
    </pre></ul>

    </ul>
    </ul>
    If you have different levels of severity you wish to keep track of, you can create different ErrorLog
    objects:
    <ul><pre>
    ErrorLog errors      = new ErrorLog();
    ErrorLog warnings    = new ErrorLog();
    ErrorLog suggestions = new ErrorLog();

    ...

    errors.add( "file " + fileName + " corrupted at byte " + pos );

    ...

    warnings.add( "employee " + empName + " found twice - could be a duplicate" );

    ...


    if ( password.length() < 5 )
    {
    <ul>
    suggestions.add( "short passwords are not very secure" );
    </ul>
    }
    </pre></ul>


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
public class ErrorLog implements Serializable
{

    // contains all of the error strings that occurred while error logging is turned on.  If
    // this is null, error logging is currently off.
    private List errors = null ;

    // if errors are to be directed to a file, f specifies the file.  If f is null
    // then this feature is currently turned off.
    private transient TextFileOut f = null ;

    // should errors be sent to the console?
    private boolean logToConsole = true ;

    // the number of errors that this object has encountered so far
    private int count = 0 ;

    /** Create an ErrorLog object where errors are sent to System.err
    */
    public ErrorLog()
    {
        // nothing to do
    }

    /** Create an ErrorLog object where errors are sent to a text file. <p>

        If the text file already exists, it will first be deleted.  If the text file does not already exist,
        it will be created. <p>

        This constructor defaults to console logging off.  If you want both console logging and file logging,
        call this constructor and then call setConsole( true );<p>

        @param fileName The name of the file that will be created and store future error messages. <p>
        @exception IOException thrown for any IO errors encountered while creating the file. <p>
    */
    public ErrorLog( String fileName ) throws IOException
    {
        logToConsole = false ;
        setLogFile( fileName ) ;
    }

    /** Tell the ErrorLog object that all future error messages are to be copied to a text file. <p>

        If the text file already exists, it will first be deleted.  If the text file does not already exist,
        it will be created. <p>

        @param fileName The name of the file that will be created and store future error messages. <p>
        @exception IOException thrown for any IO errors encountered while creating the file. <p>
    */
    public void setLogFile( String fileName ) throws IOException
    {
        if ( f != null )
        {
            setLogFileOff();
        }
        f = new TextFileOut( fileName );
    }


    /** Tell the ErrorLog object that all future error messages are to be copied to a text file. <p>

        If the text file already exists, new messages will be appended to the end.  If the text file does not already exist,
        it will be created. <p>

        @param fileName The name of the file that will store future error messages. <p>
        @exception IOException thrown for any IO errors encountered while creating/opening the file. <p>
    */
    public void appendLogFile( String fileName ) throws IOException
    {
        if ( f != null )
        {
            setLogFileOff();
        }
        f = new TextFileOut( fileName , true );
    }


    /** returns the current local date and time. <p>

        @return a string containing the current local date and time. <p>
    */
    private static String nowStr()
    {
        SimpleDateFormat f = new SimpleDateFormat("yyyy.MM.dd EEE hh:mm:ss");
        return f.format( new Date() );
    }

    // add a string to the errors vector if it is not null.
    private void addToErrors( String s )
    {
        if ( errors != null )
        {
            errors.add( s );
            if ( errors.size() > 5000 )
            {
                int numToKeep = 3000 ;
                for( int i = 0 ; i < numToKeep ; i++ )
                {
                    errors.set( i , errors.get( i + ( errors.size() - numToKeep ) ) );
                }
                errors = errors.subList( 0 , numToKeep - 1 );
            }
        }
    }

    // handle an internal error.  In case of any problems within this class,
    // they need to be passed along without stopping the program.
    private void internalError( String s )
    {
        s = nowStr() + ' ' + s ;
        addToErrors( s );
        // send message to console whether they want it or not
        System.err.println( s );
    }

    /** Tell the ErrorLog object that no further errors are to be copied to a text file. <p>

        This method also properly closes the log file. <p>
    */
    public void setLogFileOff()
    {
        if ( f != null )
        {
            try
            {
                f.close();
            }
            catch ( IOException e )
            {
                internalError( "could not properly close log file" );
            }
            f = null ;
        }
    }

    /** Test to see if this ErrorLog object currently copies error messages to a text file. <p>

        @return <b>true</b> if all messages are copied to a text file.<br><b>false</b> if no messages are copied to a text file. <p>
    */
    public boolean isFileLogOn()
    {
        return ( f != null ) ;
    }

    /** Tell the ErrorLog object to start/stop copying error messages to the console. <p>

        @param on <b>true</b>: start sending messages to the console.  <b>false</b>: stop sending messages to the console. <p>
    */
    public void setConsole( boolean on )
    // if true, all errors are copied to STDERR
    {
        logToConsole = on ;
    }

    /** Test to see if this ErrorLog object currently copies error messages to the console. <p>

        @return <b>true</b> if all messages are copied to the console.<br><b>false</b> if no messages are copied to the console. <p>
    */
    public boolean isConsoleLogOn()
    {
        return logToConsole ;
    }

    /** Add an error message to the ErrorLog object. <p>

        "message" has a time/date stamp added to it. <p>

        If internal logging is on, the message is appended to the internal collection of error messages. <p>

        If console logging is on, the message is copied to the console. <p>

        If file logging is on, the message is copied to the log file. <p>

        @param message this can be any text you want. <p>
    */
    public void add( String message )
    {
        String s = nowStr() + ' ' + message ;
        addToErrors( s );
        if ( logToConsole )
        {
            System.err.println( s );
        }
        if ( f != null )
        {
            try
            {
                f.writeLine( s );
                f.flush();
            }
            catch ( IOException e )
            {
                internalError( "error occured while writing to log file" );
            }
        }
        count++;
    }

    /** Add an error message to the ErrorLog object and display that mesage to the user in a Dialog. <p>

        "message" has a time/date stamp added to it. <p>

        A diolog box is created and the message is displayed in that dialog. <p>

        If internal logging is on, the message is appended to the internal collection of error messages. <p>

        If console logging is on, the message is copied to the console. <p>

        If file logging is on, the message is copied to the log file. <p>

        @param c any AWT or JFC component.  It will be used to find an AWT Frame which is required for all Dialogs. <p>
        @param message this can be any text you want.
    */
        public void addAndDisplay( Component c , String message )
    {
        add( message );
        GUI.tellUser( c , message );
    }

    /** Get the number of errors that this object has encountered. <p>

        @return the number of error that this object has encountered. <p>
    */
    public int numErrors()
    {
        return count ;
    }

    /** Get a list of all error messages that have been passed in to the ErrorLog object. <p>

        @return an array of strings which represent the error messages that were passed to the ErrorLog object.  Each
        message includes a date and time stamp.  If errors are not currently stored in this object, null is returned. <p>
    */
    public String[] getList()
    {
        String[] s = null ;
        if ( errors != null )
        {
            s = new String[ errors.size() ];
            errors.toArray( s );
        }
        return s ;
    }

    /** Tell the ErrorLog object to start/stop copying error messages to an internal buffer. <p>

        @param on <b>true</b> start copying messages to the internal buffer.  <b>false</b> stop copying messages to the internal buffer.
    */
    public void setInternalLog( boolean on )
    {
        if ( on )
        {
            if ( errors == null )
            {
                errors = new ArrayList  ();
            }
        }
        else
        {
            errors = null ;
        }
    }

    /** Test to see if this ErrorLog object currently copies error messages to an internal buffer. <p>

        @return <b>true</b> if all messages are copied to the internal buffer.<br><b>false</b> if no messages are copied to the internal buffer. <p>
    */
    public boolean isInternalLogOn()
    {
        return ( errors != null ) ;
    }


}


