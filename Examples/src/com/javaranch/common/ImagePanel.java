package com.javaranch.common ;

import java.awt.* ;
//import java.awt.event.* ;

/** Lightweight panel that is the perfect size of given image. <p>

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
public class ImagePanel extends Container
{

    private Image image = null ;

    /** If you want to load an image based on where the parent class is loaded, you
        don't have access to "this" until after the ImagePanel constructor is done. <p>

        Use init() after using the no-arg constructor. <p>
    */
    public ImagePanel(){}

    private void commonConstructor( int x , int y )
    {
        setLayout( null );
        setBounds( x , y , image.getWidth( null ) , image.getHeight( null ) );
    }

    public void init( Object obj , String filename , int x , int y )
    {
        image = ImageLoader.get( obj , filename );
        commonConstructor( x , y );
    }

    public void init( Object obj , String filename )
    {
        init( obj , filename , 0 , 0 );
    }

    public ImagePanel( Image image , int x , int y )
    {
        this.image = image ;
        commonConstructor( x , y );
    }

    public ImagePanel( Image image )
    {
        this( image , 0 , 0 );
    }

    public ImagePanel( Object obj , String image , int x , int y )
    {
        init( obj , image );
    }

    public ImagePanel( Object obj , String image )
    {
        this( obj , image , 0 , 0 );
    }

    /** For internal use only. <p>
    */
    public Dimension getPreferredSize()
    {
        return new Dimension( image.getWidth( null ) , image.getHeight( null ) );
    }

    public void update( Graphics g )
    {
        paint( g );
    }

    /** For internal use only. <p>
    */
    public void paint( Graphics g )
    {
        g.drawImage( image , 0 , 0 , null );
        super.paint( g );
    }

}




