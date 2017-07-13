package com.javaranch.common ;

import java.awt.* ;
import java.awt.event.* ;
import javax.swing.* ;

/** Looks and smells like a JTextField, but gets picky about the input being the right date format. <p>

    Dates must be in the YYY/MM/DD format.  Slashes can replaced with hyphens, backslashes, spaces,
    periods, commas or almost anything.  If the day is left out, the first is assumed.  If the month
    is left out, January is assumed.  Blank dates default to "today". <p>

    Invalid dates are accepted and turned red. <p>

    After a date is entered, it is converted to the standard format and shown in the standard format. <p>


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
public class DateField extends JTextField implements FocusListener
{

    private GDate date ;

    /* Create a new DateField object initialized to today's date. <p>
    */
    public DateField()
    {
        date = new GDate();
        initialize();
    }

    /* Create a new DateField object initialized to the provided date. <p>

       @param the desired default date as a string. <p>
    */
    public DateField( String textDate )
    {
        date = new GDate( textDate );
        initialize();
    }

    /* Create a new DateField object initialized to the provided date. <p>

       If you use an ADate object, the MM/DD/YYYY format will be used throughout this object. <p>

       @param the desired default date as a GDate object. <p>
    */
    public DateField( GDate date )
    {
        this.date = date ;
        initialize();
    }

    private void initialize()
    {
        setColumns( 11 );
        setText( date.toString() );
        setForeground( isValid() ? Color.black : Color.red );
        addFocusListener( this );
    }

    /** Is the current date invalid (red) or valid (black). <p>

        @return <b>true</b> if the current date is valid.<br><b>false</b> if the current date is invalid. <p>
    */
    public boolean isValid()
    {
        date.set( getText() );
        return JDate.isValid( date );
    }

    /** Internal use only. <p>
    */
    public void focusGained( FocusEvent e )
    {
    }

    /** Internal use only. <p>
    */
    public void focusLost( FocusEvent e )
    {
        if ( isValid() )
        {
            setText( date.toString() );
            setForeground( Color.black );
        }
        else
        {
            setForeground( Color.red );
        }
    }

    /** Returns the inner date copy. <p>

        If ADate was passed in, this return object will actually be an ADate object. <p>
    */
    public GDate getGDate()
    {
        GDate returnVal = (GDate)date.clone();
        returnVal.set( getText() );
        return returnVal ;
    }

    /** Used strictly for a GUI unit test. <p>
    */
    public static void main( String[] args )
    {
        JFrame f = new JFrame("testing DateField stuff");
        Container fContainer = f.getContentPane();

        fContainer.setLayout( new GridLayout( 5 , 1 ) );

        fContainer.add( new DateField( new ADate() ) );
        fContainer.add( new DateField( new ADate() ) );
        fContainer.add( new DateField( new ADate() ) );
        fContainer.add( new DateField( new ADate() ) );
        fContainer.add( new DateField( new ADate() ) );

        f.setSize( 300 , 300 );
        f.setVisible( true );
    }

}
