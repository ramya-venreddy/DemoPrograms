package com.javaranch.common ;

import java.util.* ;
import java.security.InvalidParameterException;
import javax.mail.* ;
import javax.mail.internet.* ;

/** Emails a message to a user. <p>
 *
 *  This class provides a central place for eliminating redundant email code. <br>
 *  Define your SMTP server once, and all future emails use that server. <p>
 *
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
public class Email
{

    public static final String smtpServerKey = "mail.smtp.host";

    private static String adminEmail = "";
    private static Properties props = new Properties();

    /**
     * @param smtpServer  for example, "192.168.1.1", "mail.megacorp.com", "127.0.0.1", etc.
     */
    public static void setSMTPServer( String smtpServer )
    {
        props.setProperty( smtpServerKey , smtpServer );
    }

    private static String getSMTPServer()
    {
        return props.getProperty( smtpServerKey );
    }

    public static String getAdminEmail()
    {
        return adminEmail ;
    }

    public static void setAdminEmail( String adminEmail )
    {
        Email.adminEmail = adminEmail ;
    }

    /**
     * SMTP server must be defined via the setSMTPServer() method before you can use this method.
     */
    public static void send( String to , String from , String subject , String body ) throws Exception
    {
        if ( ! Str.usable( getSMTPServer() ))
        {
            throw new Exception("SMTP server must be set before email can be sent");
        }
        Session emailSession = Session.getDefaultInstance( props , null );
        Message msg = new MimeMessage( emailSession );
        if ( ! Str.usable( to ) )
        {
            throw new InvalidParameterException("Destination email address required");
        }
        InternetAddress[] address = { new InternetAddress( to ) };
        msg.setRecipients( Message.RecipientType.TO , address );
        if ( Str.usable( from ) )
        {
            msg.setFrom( new InternetAddress( from ) );
        }
        if ( Str.usable( subject ) )
        {
            msg.setSubject( subject );
        }
        msg.setSentDate( new Date() );
        msg.setText( body );
        Transport.send( msg );
    }

    /** Send an e-mail to the admin defined by the setAdminEmail() method
     * and the server defined via the setSMTPServer() method. <p>
    */
    public static void send( String from , String body ) throws Exception
    {
        send( adminEmail , from , "", body );
    }

    /** Send an e-mail to the admin, from the admin defined by the setAdminEmail() method
     * via the specified server defined via the setSMTPServer() method. <p>
     *
     * If there are any problems, write the problem to the System.err. <p>
    */
    public static void reportError( String message )
    {
        try
        {
            send( adminEmail , message );
        }
        catch( Exception e )
        {
            System.err.println( "could not send e-mail (" + message + "): " + e );
        }
    }

}
