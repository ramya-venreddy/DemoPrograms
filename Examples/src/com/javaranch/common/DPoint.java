package com.javaranch.common ;

import java.io.* ;

/** A very lean serialized point type where X and Y are of type double. <p>

    Should the day ever arise that everyone supports Java 1.2 or higher,
    this class should inherit Point2D.Double. <p>


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

public class DPoint implements Serializable
{

    private double x = 0.0 ;
    private double y = 0.0 ;

    /** Create a double point object initialized to 0.0,0.0. <p>
    */
    public DPoint()
    {
        // already set to 0.0, 0.0
    }

    /** Create a double point object initialized to x,y. <p>

        @param x The x coordinate. <p>
        @param y The y coordinate. <p>

    */
    public DPoint( double x , double y )
    {
        this.x = x ;
        this.y = y ;
    }

    public void setX( double x )
    {
        this.x = x ;
    }

    public double getX()
    {
        return x ;
    }

    public void setY( double y )
    {
        this.y = y ;
    }

    public double getY()
    {
        return y ;
    }

    public void set( double x , double y )
    {
        this.x = x ;
        this.y = y ;
    }

    /** Increment x (negative numbers decrement x). <p>

        @param inc The amount to adjust x. <p>
    */
    public void incX( double inc )
    {
        x += inc ;
    }

    /** Increment y (negative numbers decrement y). <p>

        @param inc The amount to adjust y. <p>
    */
    public void incY( double inc )
    {
        y += inc ;
    }

    /** returns something like "(35.8765,20.0042)". <p>
    */
    public String toString()
    {
        return "(" + x + ',' + y + ')' ;
    }

    public boolean equals( Object obj )
    {
        boolean returnVal = false ;
        if ( this == obj )
        {
            returnVal = true ;
        }
        else if ( ( obj != null ) && ( getClass() == obj.getClass() ) )
        {
            DPoint p = (DPoint) obj ;
            if ( ( Double.compare( p.x , x ) == 0 ) && ( Double.compare( p.y , y ) == 0 ) )
            {
                returnVal = true ;
            }
        }
        return returnVal ;
    }

}
