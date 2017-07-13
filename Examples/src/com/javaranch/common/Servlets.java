package com.javaranch.common ;

import java.io.* ;
import java.util.zip.* ;
import javax.servlet.http.* ;

/** This class simplifies the exchange of objects between clients and Servlets via the HTTP
    protocol. <p>

    All receiving is embedded within an HTTP POST command. <p>

    All objects sent and received must implement Serializable.  <p>

    The server-side methods in this class interact with the client-side methods in
    com.javaranch.common.HTTP. <p>

    Example:
    <pre>

 *   <b><i>Client applet</i></b>
 *
 *
 *    import java.applet.* ;
 *    import java.awt.* ;
 *    import java.awt.event.* ;
 *    import com.javaranch.common.* ;
 *
 *    public class ObjectApplet extends Applet implements ActionListener
 *    {
 *
 *        private static final String servlet = "http://www.javaranch.com/servlet/ObjectServer" ;
 *        private static final String[] data = {"jan","feb","mar","apr","may","jun"};
 *        private TextField field;  // shows my results
 *
 *        public void actionPerformed( ActionEvent e )
 *        {
 *            try
 *            {
 *                String[] s = (String[]) HTTP.send( servlet , data );
 *                field.setText( s[0] +'|'+ s[1] +'|'+ s[2] +'|'+ s[3] +'|'+ s[4] +'|'+ s[5] );
 *            }
 *            catch( Exception ex )
 *            {
 *                field.setText("uh oh... " + ex );
 *            }
 *        }
 *
 *        public void init()
 *        {
 *            Button b = new Button("send data");
 *            b.addActionListener( this );
 *            add( b );
 *            field = new TextField("start",50);
 *            add( field );
 *        }
 *
 *    }
 *
 *
 *   <b><i>Server servlet</i></b>
 *
 *   import java.io.* ;
 *   import javax.servlet.http.* ;
 *   import javax.servlet.* ;
 *   import com.javaranch.common.* ;
 *
 *   public class ObjectServer extends HttpServlet implements SingleThreadModel
 *   {
 *
 *       public void doPost( HttpServletRequest req , HttpServletResponse resp )
 *       {
 *           resp.setContentType("application/binary");
 *           try
 *           {
 *               // the object sent is a string array.  Sort it backwards and send it back.
 *               String[] old = (String[])Servlets.getObjectFromClient( req );
 *               String[] s = new String[ old.length ];
 *               int topIndex = old.length - 1 ;
 *               for( int i = 0 ; i < old.length ; i++ )
 *               {
 *                   s[ topIndex - i ] = old[ i ] ;
 *               }
 *               Servlets.sendObjectToClient( resp , s );
 *           }
 *           catch ( Exception e )
 *           {
 *               System.out.println( "ObjectServer: " + e );
 *           }
 *       }
 *
 *   }

    </pre>

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
public class Servlets
{

    // since this class just holds static methods, I don't want anybody to instantiate it
    private Servlets(){}

    /** Called by servlets to receive objects from applets or other client programs. <p>

    */
    public static Object getObjectFromClient( HttpServletRequest req ) throws Exception
    {
        ObjectInputStream in = new ObjectInputStream( new GZIPInputStream( req.getInputStream() ) );
        Object obj = in.readObject();
        in.close();
        return obj ;
    }

    private static byte[] convertObjectToByteArray( Object obj ) throws IOException
    {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream( b );
        out.writeObject( obj );
        return b.toByteArray();
    }

    /** Called by servlets to send objects to applets or other client programs. <p>
    */
    public static void sendObjectToClient( HttpServletResponse resp , Object obj ) throws Exception
    {
        // I used to just pass the object.  But a certain quantity of objects freaks something out.
        // Converting the object to a byte array and passing just the array, then converting the array
        // to the object works fine.
        byte[] b = convertObjectToByteArray( obj );
        ObjectOutputStream out = new ObjectOutputStream( new GZIPOutputStream( resp.getOutputStream() ) );
        out.writeObject( b );
        out.close();
    }

    /** When making lists of things with radio buttons or checkboxes in PRE areas. <p>

        @param out Where all this stuff will be written. <p>
        @param text The text that the users will look at and will be arranged nice. <p>
        @param tags The tags that go with the text. <p>
        @param indent How many spaces to indent each line. <p>
        @param width The number of characters wide after the indent. <p>
    */
    public static void makeColumns( PrintWriter out , String[] text , String[] tags , int indent , int width )
    {
        int longest = 0 ;
        for( int i = 0 ; i < text.length ; i++ )
        {
            longest = Math.max( longest , text[ i ].length() );
        }

        // now that we know how long the longest is, let's figure out how
        // many columns we're going to have.
        int columns = width / ( longest + 4 ) ; // add 4 for the checkbox/button and some space
        int rows = ( text.length / columns ) + 1 ;

        if ( rows == 1 )
        {
            out.print( "\n" + Str.spaces( indent ) );
            for( int i = 0 ; i < text.length ; i++ )
            {
                out.print( tags[ i ] );
                out.print( text[ i ] );
                out.print( "  " );
            }
        }
        else
        {
            // try to trim columns that are needlessly wide
            int[] columnWidths = new int[ columns ];
            for( int col = 0 ; col < columns ; col++ )
            {
                int columnLongest = 0 ;
                int textStart = rows * col ;
                int textStop = textStart + rows ;
                if ( textStop > text.length ) // last column!
                {
                    textStop = text.length ;
                }
                for( int i = textStart ; i < textStop ; i++ )
                {
                    columnLongest = Math.max( columnLongest , text[ i ].length() );
                }
                columnWidths[ col ] = columnLongest + 2 ;
            }

            for( int row = 0 ; row < rows ; row++ )
            {
                out.print( "\n" + Str.spaces( indent ) );
                for( int col = 0 ; col < columns ; col++ )
                {
                    int index = ( col * rows ) + row ;
                    if ( index < text.length )
                    {
                        out.print( tags[ index ] );
                        out.print( Str.left( text[ index ] , columnWidths[ col ] ) );
                    }
                }
            }
        }

    }

}



