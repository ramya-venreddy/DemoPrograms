package com.javaranch.common ;

import java.awt.*;
import java.awt.event.*;

/** This class is used by the AWT class. <p>


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

class QuickDialog extends Dialog implements ActionListener
{

    /** For internal use only. <p>
    */
    public void actionPerformed( ActionEvent e )
    {
        dispose();
    }

    /** For use by the AWT class only. <p>
    */
    QuickDialog( Frame parent , String message )
    {
        super( parent , "" , true );
        add( new Panel() , BorderLayout.WEST );
        add( new Panel() , BorderLayout.EAST );
        Panel p = new Panel( new GridLayout(4,1) );
        p.add( GUI.blank() );
        p.add( new Label( message ) );
        p.add( GUI.blank() );
        Panel buttonPanel = new Panel();
        Button b = new Button("ok");
        b.addActionListener( this );
        buttonPanel.add( b , BorderLayout.SOUTH );
        p.add( buttonPanel );
        add( p , BorderLayout.CENTER );
        pack();
        GUI.center( this );
        setVisible( true );
    }

}


