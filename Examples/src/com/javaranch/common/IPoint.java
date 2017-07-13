package com.javaranch.common ;

import java.io.* ;

/** A very lean serialized point type where X and Y are of type int. <p>


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
public class IPoint implements Serializable
{

    private int x = 0 ;
    private int y = 0 ;

    /** Create an int point object initialized to 0,0. <p>
    */
    public IPoint()
    {
        // already set to 0, 0
    }

    /** Create an int point object initialized to x,y. <p>

        @param x The x coordinate. <p>
        @param y The y coordinate. <p>

    */
    public IPoint( int x , int y )
    {
        this.x = x ;
        this.y = y ;
    }

    /** Create a new int point object containing the same value and another int point object. <p>
    */
    public IPoint( IPoint p )
    {
        x = p.x ;
        y = p.y ;
    }

    public void set( IPoint p )
    {
        x = p.x ;
        y = p.y ;
    }

    public void setX( int x )
    {
        this.x = x ;
    }

    public int getX()
    {
        return x ;
    }

    public void setY( int y )
    {
        this.y = y ;
    }

    public int getY()
    {
        return y ;
    }

    public void set( int x , int y )
    {
        this.x = x ;
        this.y = y ;
    }

    /** Increment the X value without modifying Y. <p>
    */
    public void incX( int val )
    {
        x += val ;
    }

    /** Increment the Y value without modifying X. <p>
    */
    public void incY( int val )
    {
        y += val ;
    }

    /** Increment the X value and the Y value with the x and y (respectively) of p. <p>
    */
    public void inc( IPoint p )
    {
        x += p.x ;
        y += p.y ;
    }

    /** returns something like "(35,20)". <p>
    */
    public String toString()
    {
        return "(" + x + ',' + y + ')' ;
    }

    public boolean equals( Object obj )
    {
        boolean returnVal = false ;
        if ( obj instanceof IPoint )
        {
            IPoint p = (IPoint) obj ;
            if ( ( p.x == x ) && ( p.y == y ) )
            {
                returnVal = true ;
            }
        }
        return returnVal ;
    }

}
