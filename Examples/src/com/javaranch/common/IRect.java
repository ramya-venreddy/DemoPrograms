package com.javaranch.common ;

import java.io.* ;
import java.awt.Rectangle ;

/** A very lean serialized rectangle type made of ints. <p>


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
public class IRect implements Serializable
{
    private IPoint p = null ;
    private int width = 0 ;
    private int height = 0 ;

    /** Create an IRect object at 0,0 with a width of 0 and a height of 0. <p>
    */
    public IRect()
    {
        p = new IPoint();
    }

    /** Create an IRect object with the same size and location as another IRect object. <p>
    */
    public IRect( IRect r )
    {
        p = new IPoint( r.p );
        width = r.width ;
        height = r.height ;
    }

    /** Create an IRect object with a specific X, Y, Width and Height. <p>

        @param x The left edge. <p>
        @param y The top edge. <p>
    */
    public IRect( int x , int y , int width , int height )
    {
        p = new IPoint( x , y );
        this.width = width ;
        this.height = height ;
    }

    /** Create an IRect object with the same size and location as a java.awt.Rectangle object. <p>
    */
    public IRect( Rectangle r )
    {
        this( r.x , r.y , r.width , r.height );
    }

    /** Create an IRect object with a specific point, Width and Height. <p>

        @param p The top left corner. <p>
    */
    public IRect( IPoint p , int width , int height )
    {
        this.p = new IPoint( p );
        this.width = width ;
        this.height = height ;
    }

    /** Create an IRect object from two opposing points. <p>
    */
    public IRect( IPoint p1 , IPoint p2 )
    {
        p = new IPoint( Math.min( p1.getX() , p2.getX() ) , Math.min( p1.getY() , p2.getY() ) );
        width = Math.abs( p1.getX() - p2.getX() ) + 1 ;
        height = Math.abs( p1.getY() - p2.getY() ) + 1 ;
    }

    /** Create an IRect object with a specified location, a width of zero and a height of zero. <p>
    */
    public IRect( IPoint p )
    {
        p = new IPoint();
    }

    /** Are the two IRect objects at the same location and the same size. <p>
    */
    public boolean equals( IRect r )
    {
        boolean returnVal = false ;
        if ( p.equals( r.p ) && ( width == r.width ) && ( height == r.height ) )
        {
            returnVal = true ;
        }
        return returnVal ;
    }

    public boolean equals( Object obj )
    {
        boolean returnVal = false ;
        if ( obj instanceof IRect )
        {
            returnVal = equals( (IRect)obj );
        }
        return returnVal ;
    }

    /** The left edge. <p>
    */
    public int getX()
    {
        return p.getX();
    }

    /** The top edge. <p>
    */
    public int getY()
    {
        return p.getY();
    }

    public int getWidth()
    {
        return width ;
    }

    public int getHeight()
    {
        return height ;
    }

    /** Get the X value of the left edge of the rectangle. <p>
    */
    public int getLeft()
    {
        return p.getX();
    }

    /** Get the Y value of the top edge of the rectangle. <p>
    */
    public int getTop()
    {
        return p.getY();
    }

    /** Get the X value of the right edge of the rectangle. <p>
    */
    public int getRight()
    {
        return p.getX() + width - 1 ;
    }

    /** Get the Y value of the bottom edge of the rectangle. <p>
    */
    public int getBottom()
    {
        return p.getY() + height - 1 ;
    }

    /** Get the point that represents the top, left corner of the rectangle. <p>
    */
    public IPoint getTopLeft()
    {
        return p ;
    }

    /** Get the point that represents the top, right corner of the rectangle. <p>
    */
    public IPoint getTopRight()
    {
        return new IPoint( getRight() , getTop() );
    }

    /** Get the point that represents the bottom, left corner of the rectangle. <p>
    */
    public IPoint getBottomLeft()
    {
        return new IPoint( getLeft() , getBottom() );
    }

    /** Get the point that represents the bottom, right corner of the rectangle. <p>
    */
    public IPoint getBottomRight()
    {
        return new IPoint( getRight() , getBottom() );
    }

    /** Convert the IRect format to a java.awt.Rectangle object. <p>
    */
    public Rectangle getRectangle()
    {
        return new Rectangle( p.getX() , p.getY() , width , height );
    }

    /** Set this object to be equal to another IRect object. <p>
    */
    public void set( IRect r )
    {
        p.set( r.p );
        width = r.width ;
        height = r.height ;
    }

    /** Move the top left corner of this object to a specific point. <p>

        Width and height are not changed. <p>
    */
    public void set( IPoint p )
    {
        this.p.set( p );
    }

    /** Change the width of this object so that the right edge will line up to a specific position. <p>
    */
    public void setRight( int r )
    {
        if ( r > p.getX() )
        {
            width = ( r - p.getX() ) + 1 ;
        }
    }

    /** Change the height of this object so that the bottom edge will line up to a specific position. <p>
    */
    public void setBottom( int b )
    {
        if ( b > p.getY() )
        {
            height = ( b - p.getY() ) + 1 ;
        }
    }

    /** Move this object so the left edge will will line up with a specific position. <p>

        Width is not changed. <p>
    */
    public void setX( int newX )
    {
        p.setX( newX );
    }

    /** Move this object so the top edge will will line up with a specific position. <p>

        Height is not changed. <p>
    */
    public void setY( int newY )
    {
        p.setY( newY );
    }

    /** Move the right edge of this object to accomidate the new width. <p>
    */
    public void setWidth( int newWidth )
    {
        width = newWidth ;
    }

    /** Move the bottom edge of this object to accomidate the new height. <p>
    */
    public void setHeight( int newHeight )
    {
        height = newHeight ;
    }

    /** Move the object without changing the width or height. <p>

        Positive values in p.x will move this object to the right.  Negative values to the left. <p>

        Positive values in p.y will move this object down.  Negative values up. <p>
    */
    public void inc( IPoint p )
    {
        this.p.inc( p );
    }

    /** Adjust all possible attributes of this object. <p>

        @param x Change the left edge without changing the width. <p>
        @param y Change the top edge without changing the height. <p>
        @param width Change the width without changing the left edge. <p>
        @param height Change the height without changing the top edge. <p>
    */
    public void adjust( int x , int y , int width  , int height )
    {
        p.incX( x );
        p.incY( y );
        this.width += width ;
        this.height += height ;
    }

    /** Change the left edge without changing the width. <p>
    */
    public void incX( int val )
    {
        p.incX( val ) ;
    }

    /** Change the top edge without changing the height. <p>
    */
    public void incY( int val )
    {
        p.incY( val ) ;
    }

    /** Change the width without changing the left edge. <p>
    */
    public void incWidth( int val )
    {
        width += val ;
    }

    /** Change the height without changing the top edge. <p>
    */
    public void incHeight( int val )
    {
        height += val ;
    }

    /** Change the rectangle to be a slightly smaller rectangle centered within the old rectangle. <p>

        Calling shrink(1) will make this rectangle fit exactly within the old rectangle. The left edge
        will be one pixel further right than it was.  The right edge will be one pixel to the left
        of where it was.  The top edge will be one pixel lower and the bottom edge will be one
        pixel higher.  <p>

        @param val how many pixels to shrink. <p>
    */
    public void shrink( int val )
    {
        p.incX( val );
        p.incY( val );
        width -= ( val * 2 );
        height -= ( val * 2 );
    }

    /** Change the rectangle to be a slightly larger rectangle. <p>

        Calling swell(1) will make this rectangle wrap exactly around the old rectangle. The left edge
        will be one pixel further left than it was.  The right edge will be one pixel to the right
        of where it was.  The top edge will be one pixel higher and the bottom edge will be one
        pixel lower.  <p>

        @param val how many pixels to swell. <p>
    */
    public void swell( int val )
    {
        p.incX( -val );
        p.incY( -val );
        width += ( val * 2 );
        height += ( val * 2 );
    }

    /** Does the given point touch the given rectangle. <p>
    */
    public static boolean pointOnRect( IPoint p , IRect r )
    {
        int a = r.getLeft() ;
        int b = r.getRight() ;
        boolean returnVal = Numbers.inRange( p.getX() , a , b );
        a = r.getTop();
        b = r.getBottom();
        returnVal = returnVal && Numbers.inRange( p.getY() , a , b );
        return returnVal ;
    }

    /** Do these two rectangles touch. <p>
    */
    public static boolean rectOnRect( IRect r1 , IRect r2 )
    {
        boolean returnVal = false ;
        if ( ( r1.getLeft() <= r2.getRight() ) || ( r1.getRight() >= r2.getLeft() ) )
        {
            if ( ( r1.getTop() <= r2.getBottom() ) || ( r1.getBottom() >= r2.getTop() ) )
            {
                returnVal = true ;
            }
        }
        return returnVal ;
    }

}









