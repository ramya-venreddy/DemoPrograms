package com.javaranch.common ;

import java.awt.*;
import java.awt.geom.* ;
import java.awt.font.* ;

/** contains a collection of static methods related to GUI's (AWT only). <p>


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

public class AWT
{

    public static final int center = Integer.MIN_VALUE ;
    public static final int right = center + 1 ;
    public static final int rightPad = center + 2 ;

    AWT(){} // just making sure that nobody tries to instantiate this class

    /** For creating quick GridBagConstraints objects. <p>
    */
    public static GridBagConstraints gbc( int x, int y , int wide , int high , double weightx , double weighty )
    {
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = x ;
        g.gridy = y ;
        g.gridwidth = wide ;
        g.gridheight = high ;
        g.weightx = weightx ;
        g.weighty = weighty ;
        g.fill = GridBagConstraints.BOTH ;
        return g ;
    }

    /** For creating quick GridBagConstraints objects. <p>
    */
    public static GridBagConstraints gbc( int x , int y )
    {
        return gbc( x , y , 1 , 1 , 1 , 1 );
    }

    /** Returns an object that can be used for whitespace in GridLayout. <p>

        @return An object that will take up a bit of space.<p>
    */
    public static Button blank()
    {
        Button b = new Button();
        b.setVisible( false );
        return b;
    }

    /** Given any component, find the closest parent frame. <p>

        @param c Any AWT or Swing component. <p>
        @return The nearest Frame object that c uses. <p>
    */
    public static Frame getFrame( Component c )
    {
        synchronized ( c.getTreeLock() )
        {
            while( c != null && !( c instanceof Frame ) )
            {
                c = c.getParent();
            }
        }
        return (Frame)c ;
    }

    /** Pop up a dialog to pass a message to the user. <p>

        @param c Any component. <p>
        @param s The message. <p>
    */
    public static void tellUser( Component c , String s )
    {
        new QuickDialog( getFrame( c ) , s );
    }

    /** Center any GUI object on the screen. <p>

        @param c The component to be centered. <p>
    */
    public static void center( Component c )
    {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension cSize = c.getSize();
        c.setLocation( ( screenSize.width - cSize.width ) / 2 , ( screenSize.height - cSize.height ) / 2);
    }

    /** Get a rectangle that fits perfectly within another rectangle. <p>

        @param r The rectangle that the new rectangle will fit inside of.<p>
        @return The new rectangle that fits perfectly within r.<p>
    */
    public static Rectangle shrink( Rectangle r )
    {
        return new Rectangle( r.x + 1 , r.y + 1 , r.width - 2 , r.height - 2 );
    }

    /** Draw a beveled rectangle. <p>

        @param g A Graphics object that will be drawn on.<p>
        @param r The rectangle that defines the outermost edge of the beveled rectangle.<p>
        @param thickness How many lines thick should this beveled rectangle be?<p>
        @param outtie Should the bevel be made to look like a button sticking out (true) or an indent in the canvas (false).<p>
        @return The rectangle that fits perfectly within the beveled rectangle.<p>
    */
    public static Rectangle drawBeveledRect( Graphics g , Rectangle r , int thickness , boolean outtie , Color c )
    {
        g.setColor( c );
        while ( thickness > 0 )
        {
            g.draw3DRect( r.x , r.y , r.width - 1 , r.height - 1 , outtie );
            r = shrink(r);
            thickness--;
        }
        return r;
    }

    /** Draw a beveled rectangle and assumes the color of gray. <p>

        @param g A Graphics object that will be drawn on.<p>
        @param r The rectangle that defines the outermost edge of the beveled rectangle.<p>
        @param thickness How many lines thick should this beveled rectangle be?<p>
        @param outtie Should the bevel be made to look like a button sticking out (true) or an indent in the canvas (false).<p>
        @return The rectangle that fits perfectly within the beveled rectangle.<p>
    */
    public static Rectangle drawBeveledRect( Graphics g , Rectangle r , int thickness , boolean outtie )
    {
        return drawBeveledRect( g , r , thickness , outtie , Color.gray );
    }

    // brightness is determined by the sum of the red, green and blue.
    // the higher the number, the brighter.
    private static int getBrightSum( Color c )
    {
        return c.getRed() + c.getGreen() + c.getBlue() ;
    }

    // the x and y of r specify the coordinates of the upper left outside corner pixel.
    // the width and height of r specify the number of pixels on the outside edge.
    /** Draw a beveled rectangle. <p>

        @param g A Graphics object that will be drawn on.<p>
        @param r The rectangle that defines the outermost edge of the beveled rectangle.<p>
        @param topColor The color to be used on the top edge. <p>
        @param rightColor The color to be used on the right edge. <p>
        @param bottomColor The color to be used on the bottom edge. <p>
        @param leftColor The color to be used on the left edge. <p>
        @param borderThickness How many lines thick should this beveled rectangle be?<p>
        @return The rectangle that fits perfectly within the beveled rectangle.<p>
    */
    public static IRect drawBeveledRect( Graphics g , IRect r , Color topColor , Color rightColor , Color bottomColor , Color leftColor , int borderThickness )
    {
        int left = r.getLeft();
        int right = r.getRight();
        int top = r.getTop();
        int bottom = r.getBottom();

        int topBright = getBrightSum( topColor );
        int bottomBright = getBrightSum( bottomColor );
        int leftBright = getBrightSum( leftColor );
        int rightBright = getBrightSum( rightColor );

        // the top line can be drawn as a solid line without bevels
        g.setColor( topColor );
        for( int i = 0 ; i < borderThickness ; i++ )
        {
            g.drawLine( left , top + i , right , top + i );
        }

        // the right line can be flat at the bottom, but we need to do the bevel at the top
        g.setColor( rightColor );

        // the brighter color dominates at the corners.
        int dominantFactor = ( topBright > rightBright ) ? 1 : 0 ;
        for( int i = 0 ; i < borderThickness ; i++ )
        {
            // start from the outside and work our way in
            g.drawLine( right - i , top + i + dominantFactor , right - i , bottom );
        }

        // the bottom line is beveled at the right and can be flat at the left
        g.setColor( bottomColor );
        dominantFactor = ( rightBright > bottomBright ) ? 1 : 0 ;
        for( int i = 0 ; i < borderThickness ; i++ )
        {
            // start from the bottom and work our way up
            g.drawLine( left , bottom - i , right - ( i + dominantFactor ) , bottom - i );
        }

        // the left line is beveled at the top and bottom
        g.setColor( leftColor );
        int topDominantFactor = ( topBright > leftBright ) ? 1 : 0 ;
        int bottomDominantFactor = ( bottomBright > leftBright ) ? 1 : 0 ;
        for( int i = 0 ; i < borderThickness ; i++ )
        {
            // start from the left and work our way in
            g.drawLine( left + i , top + i + topDominantFactor , left + i , bottom - ( i + bottomDominantFactor ) );
        }

        IRect returnVal = new IRect( r );
        returnVal.shrink( borderThickness );
        return returnVal ;
    }



    /** Get the ascent associated with the font used by a component. <p>
    */
    public static int ascent( Component c )
    {
        Font f = c.getFont();
        return c.getFontMetrics( f ).getAscent();
    }

    /** Get the height associated with the font used by a component. <p>
    */
    public static int fontHeight( Component c )
    {
        Font f = c.getFont();
        return c.getFontMetrics( f ).getHeight();
    }

    public static Dimension getSize( Font f , String s )
    {
        // using font data and the given string, compute the pixel width and height
        FontRenderContext context = new FontRenderContext( f.getTransform() , true , false );
        Rectangle2D r = f.getStringBounds( s , context );
        int width = (int)Math.ceil( r.getWidth() );
        int height = (int)Math.ceil( r.getHeight() );
        return new Dimension( width , height );
    }

}



