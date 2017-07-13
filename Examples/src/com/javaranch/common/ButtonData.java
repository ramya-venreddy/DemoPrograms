
package com.javaranch.common ;

import java.io.* ;

/** This is a serializable object that can be passed in to create a new LButton. <p>


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
public class ButtonData implements Serializable
{
    private byte[] upImage ;
    private byte[] downImage ;
    private byte[] dimImage ;
    private IPoint pos ;

    /** Create a new ButtonData object. <p>

        @param upImage Represents the button in the "up" state - can be a GIF or JPEG. <p>
        @param downImage Represents the button in the "down" state - can be a GIF or JPEG. <p>
        @param dimImage Represents the button in the "dim" state - can be a GIF or JPEG. <p>
        @param pos The button's location. <p>
    */
    public ButtonData( byte[] upImage , byte[] downImage , byte[] dimImage , IPoint pos )
    {
        this.upImage = upImage ;
        this.downImage = downImage ;
        this.dimImage = dimImage ;
        if ( pos == null )
        {
            this.pos = new IPoint( 0 , 0 );
        }
        else
        {
            this.pos = pos ;
        }
    }

    public byte[] getUpImage()
    {
        return upImage ;
    }

    public byte[] getDownImage()
    {
        return downImage ;
    }

    public byte[] getDimImage()
    {
        return dimImage ;
    }

    public IPoint getPos()
    {
        return pos ;
    }

}



