package com.javaranch.common ;

import java.awt.* ;
import java.awt.event.* ;

/** Lightweight button that uses images for the button. <p>

    Each button has its own up, down and disabled image. <p>


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
public class LButton extends Component implements MouseListener, MouseMotionListener
{

    private Image up ;
    private Image down ;
    private Image dim ;
    private Image current ;
    private PressListener press = null ;
    private ReleaseListener release = null ;
    private MoveListener move = null ;


    private void commonConstructor( int x , int y , ButtonAdapter a )
    {
       current = up ;
       setBounds( x , y , up.getWidth( null ) , up.getHeight( null ) );
       addButtonListener( a );
       addMouseListener( this );
       addMouseMotionListener( this );
    }


    /* Create a new LButton object. <p>

        Use of the "null" Layout manager is assumed. <p>

        The x, y and area of the "up" button are used for setBounds(). <p>

        Attempts will be made to load three images.  The first ending with "_up.gif",
        the second with "_down.gif" and the third with "_dim.gif". The "up" image is required.
        The other two are optional.<p>

        Throws an exception if the _up.gif file cannot be found or if LButton.init() has not
        yet been called (or ImageLoader.init()). <p>

        @param rootImageName If you pass "submit", "submit_up.gif", "submit_down.gif" and "submit_dim.gif"
                             in the same directory that obj is found. <p>

        @param useDim Is there a dim button? <p>
        @param useDown Is there a down button? <p>
    */
    public LButton( String rootImageName , int x , int y , ButtonAdapter a ) throws Exception
    {
       up = ImageLoader.get( rootImageName + "_up.gif" );
       if ( up == null )
       {
           throw new Exception( "could not find button image " + rootImageName + "_up.gif" );
       }
       down = ImageLoader.get( rootImageName + "_down.gif" );
       dim = ImageLoader.get( rootImageName + "_dim.gif" );
       commonConstructor( x , y , a );
    }

    /* Create a new LButton object. <p>

        Use of the "null" Layout manager is assumed. <p>

        The x, y and area of the "up" button are used for setBounds(). <p>

        Attempts will be made to load three images.  The first ending with "_up.gif",
        the second with "_down.gif" and the third with "_dim.gif". The "up" image is required.
        The other two are optional.<p>

        Throws an exception if the _up.gif file cannot be found or if LButton.init() has not
        yet been called (or ImageLoader.init()). <p>

        @param obj the root image name will be relative to where this class can be found. <p>
        @param rootImageName If you pass "submit", "submit_up.gif", "submit_down.gif" and "submit_dim.gif"
                             in the same directory that obj is found. <p>

        @param useDim Is there a dim button? <p>
        @param useDown Is there a down button? <p>
    */
    public LButton( Object obj , String rootImageName , int x , int y , ButtonAdapter a ) throws Exception
    {
       up = ImageLoader.get( obj , rootImageName + "_up.gif" );
       if ( up == null )
       {
           throw new Exception( "could not find button image " + rootImageName + "_up.gif" );
       }
       down = ImageLoader.get( obj , rootImageName + "_down.gif" );
       dim = ImageLoader.get( obj , rootImageName + "_dim.gif" );
       commonConstructor( x , y , a );
    }

    /** Create a new LButton object from serialized data. <p>

        Passing in the down or dim images is optional. <p>
    */
    public LButton( ButtonData d , ButtonAdapter a ) throws Exception
    {
        up = ImageLoader.get( d.getUpImage() );
        if ( up == null )
        {
            throw new Exception( "up button image could not be created" );
        }
        down = ImageLoader.get( d.getDownImage() );
        dim = ImageLoader.get( d.getDimImage() );
        IPoint p = d.getPos();
        commonConstructor( p.getX() , p.getY() , a );
    }

    public LButton( Image upImage , Image downImage , Image dimImage , int x , int y , ButtonAdapter a )
    {
        up = upImage ;
        down = downImage ;
        dim = dimImage ;
        commonConstructor( x , y , a );
    }

    /** For internal use only. <p>
    */
    public Dimension getPreferredSize()
    {
        return new Dimension( up.getWidth( null ) , up.getHeight( null ) );
    }

    public void addButtonListener( ButtonAdapter a )
    {
       press = a ;
       release = a ;
       move = a ;
    }

    public void update( Graphics g )
    {
        paint( g );
    }

    /** For internal use only. <p>
    */
    public void paint( Graphics g )
    {
        g.drawImage( current , 0 , 0 , null );
    }

    /** Overrides the Component.setEnabled method so the proper image can be used. <p>
    */
    public void setEnabled( boolean yes )
    {
        if ( yes )
        {
            forceUp();
        }
        else
        {
            if ( dim != null )
            {
                current = dim ;
                repaint();
            }
        }
        super.setEnabled( yes );
    }


    /** Capture mouse press events. <p>
    */
    public void addPressListener( PressListener pl )
    {
        press = pl ;
    }


    /** Capture mouse release events. <p>
    */
    public void addReleaseListener( ReleaseListener rl )
    {
        release = rl ;
    }


    /** Capture mouse press or release events. <p>
    */
    public void addButtonListener( ButtonListener bl )
    {
        press = bl ;
        release = bl ;
    }


    /** Force the button to "up" mode. <p>
    */
    public void forceUp()
    {
        current = up ;
        repaint();
    }


    /** Force the button to "down" mode. <p>
    */
    public void forceDown()
    {
        if ( down != null )
        {
            current = down ;
            repaint();
        }
    }


    /** For internal use only. <p>
    */
    public void mouseReleased( MouseEvent e )
    {
        if ( isEnabled() )
        {
            forceUp();
            if ( release != null )
            {
                if ( e.getModifiers() == InputEvent.BUTTON1_MASK )
                {
                    release.buttonReleased();
                }
                else
                {
                    release.rightButtonReleased();
                }
            }
        }
    }


    /** For internal use only. <p>
    */
    public void mouseEntered( MouseEvent e )
    {
        if ( move != null )
        {
            move.mouseEntered();
        }
    }


    /** For internal use only. <p>
    */
    public void mouseExited( MouseEvent e )
    {
        if ( move != null )
        {
            move.mouseExited();
        }
    }


    /** For internal use only. <p>
    */
    public void mousePressed( MouseEvent e )
    {
        if ( isEnabled() )
        {
            forceDown();
            if ( press != null )
            {
                press.buttonPressed();
            }
        }
    }


    /** For internal use only. <p>
    */
    public void mouseClicked( MouseEvent e )
    {
    }


    /** For internal use only. <p>
    */
    public void mouseDragged( MouseEvent e )
    {
    }


    /** For internal use only. <p>
    */
    public void mouseMoved( MouseEvent e )
    {
        if ( move != null )
        {
            move.mouseHovering();
        }
    }

}




