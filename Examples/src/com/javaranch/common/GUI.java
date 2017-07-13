package com.javaranch.common ;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.geom.*;
import javax.swing.*;

/** Contains a collection of static methods related to GUI's (AWT and Swing stuff). <p>


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
    @author Nathan Paris
*/

public class GUI extends AWT
{

    // make sure no one instantiates this class
    private GUI(){}

    /** Create an image that looks like a beveled rectangle. <p>

        Good for making panels and buttons. <p>

        @param width The width of the image. <p>
        @param height The height of the image. <p>
        @param color The color of all that stuff in the middle of the image. <p>
        @param topColor The color to be used on the top edge. <p>
        @param rightColor The color to be used on the right edge. <p>
        @param bottomColor The color to be used on the bottom edge. <p>
        @param leftColor The color to be used on the left edge. <p>
        @param borderThickness How many lines thick should this beveled rectangle be?<p>
        @return The rectangle that fits perfectly within the beveled rectangle.<p>
    */
    public static BufferedImage createBeveledImage( int width , int height , Color color , Color topColor , Color rightColor , Color bottomColor , Color leftColor , int borderThickness  )
    {
        BufferedImage buffy = new BufferedImage( width , height , BufferedImage.TYPE_INT_RGB );
        Graphics2D g = buffy.createGraphics();
        g.setColor( color );
        g.fillRect( 0 , 0 , width , height );
        IRect r = new IRect( 0 , 0 , width , height );
        drawBeveledRect( g , r , topColor , rightColor , bottomColor , leftColor , borderThickness );
        return buffy ;
    }

    private static int darken( int color , double factor )
    {
        return (int)Math.round( ((double)color) * factor );
    }

    public static Color darken( Color c , double factor )
    {
        return new Color( darken( c.getRed() , factor ) , darken( c.getGreen() , factor ) , darken( c.getBlue() , factor ) );
    }

    private static int lighten( int color , double factor )
    {
        int antiColor = 255 - color ;
        return ( 255 - (int)Math.round( ((double)antiColor) * factor ) );
    }

    public static Color lighten( Color c , double factor )
    {
        return new Color( lighten( c.getRed() , factor ) , lighten( c.getGreen() , factor ) , lighten( c.getBlue() , factor ) );
    }

    public static BufferedImage createBeveledImage( int width , int height , Color color , int borderThickness )
    {
        Color bottom = darken( color , 0.6 );
        Color right = bottom ;
        Color left = lighten( color , 0.6 );
        Color top = lighten( color , 0.3 );
        return createBeveledImage( width , height , color , top , right , bottom , left , borderThickness );
    }

    public static Image softBeveledImage( int width , int height , Color color , int borderThickness )
    {
        BufferedImage i = createBeveledImage( width , height , color , borderThickness );
        blur( i );
        return i ;
    }

    public static void blur( BufferedImage i )
    {
        AffineTransform at = new AffineTransform();
        BufferedImage buffy = new BufferedImage( i.getWidth( null ), i.getHeight( null ), BufferedImage.TYPE_INT_RGB );
        float[] elements = {0.1111f , 0.1111f , 0.1111f , 0.1111f , 0.1111f , 0.1111f , 0.1111f , 0.1111f , 0.1111f};
        Kernel kernel = new Kernel( 3, 3, elements );
        ConvolveOp cop = new ConvolveOp( kernel, ConvolveOp.EDGE_NO_OP, null );
        cop.filter( i, buffy );
        BufferedImageOp biop = new AffineTransformOp( at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR );
        Graphics2D g = i.createGraphics();
        g.drawImage( buffy, biop, 0, 0 );
    }

    /** A convenience method that makes a JMenuItem out of some text and adds that to a JMenu. <p>

        @param m A JMenu object to add the new JMenuItem to. <p>
        @param desc The text of the new menu item. <p>
        @return The newly created JMenuItem. <p>
    */
    public static JMenuItem addMenuItem( JComponent m , String desc )
    {
        JMenuItem mi = new JMenuItem( desc );
        m.add( mi );
        return mi ;
    }

    /** A convenience method that makes a JMenuItem out of some text, adds an ActionListener and adds the result to a JMenu. <p>

        @param m A JMenu object to add the new JMenuItem to. <p>
        @param desc The text of the new menu item. <p>
        @param listener The action listener that will want to be notified when this item is selected. <p>
        @return The newly created JMenuItem. <p>
    */
    public static JMenuItem addMenuItem( JComponent m , String desc , ActionListener listener )
    {
        JMenuItem mi = new JMenuItem( desc );
        mi.addActionListener( listener );
        m.add( mi );
        return mi ;
    }

    /** A convenience function that adds an ActionListener to a JMenuItem and adds the item to a JMenu. <p>

        @param m A JMenu object to add the JMenuItem to. <p>
        @param mi The JMenuItem. <p>
        @param listener The action listener that will want to be notified when this item is selected. <p>
        @return The newly created JMenuItem. <p>
    */
    public static JMenuItem addMenuItem( JComponent m , JMenuItem mi , ActionListener listener )
    {
        mi.addActionListener( listener );
        m.add( mi );
        return mi ;
    }

    public static JLabel label( String text , int x , int y , int width , int height )
    {
        JLabel label = new JLabel( text );
        label.setBounds( x , y , width , height );
        return label ;
    }

    /** Creates a label with the correct size for the provided FontMetric. <p>
     */
    public static JLabel label( String text , int x , int y , Font font , FontMetrics metrics )
    {
        int width = metrics.stringWidth( text );
        JLabel label = new JLabel( text );
        label.setFont( font );
        label.setBounds( x , y , width , metrics.getHeight() + 5 );
        return label ;
    }

    public static JTextField textField( int columns , int x , int y , int width , int height )
    {
        JTextField field = new JTextField( columns );
        field.setBounds( x , y , width , height );
        return field ;
    }

    public static JButton button( String text , int x , int y , int width , int height )
    {
        JButton button = new JButton( text );
        button.setBounds( x , y , width , height );
        return button ;
    }

    public static JButton button( String text , int x , int y , Font font , FontMetrics metrics )
    {
        JButton button = new JButton( text );
        button.setFont( font );
        int width = metrics.stringWidth( text ) + 40 ;
        button.setBounds( x , y , width , metrics.getHeight() + 5 );
        return button ;
    }

    public static JCheckBox checkBox(  int x , int y , int width , int height )
    {
        JCheckBox checkBox = new JCheckBox();
        checkBox.setBounds( x , y , width , height );
        return checkBox ;
    }

    public static void messageDialog( Frame f , String message )
    {
        JOptionPane.showMessageDialog( f , message );
    }

    public static void warningDialog( Frame f , String message )
    {
        JOptionPane.showMessageDialog( f , message , "" , JOptionPane.WARNING_MESSAGE );
    }

    public static void errorDialog( Frame f , String message )
    {
        JOptionPane.showMessageDialog( f , message , "" , JOptionPane.ERROR_MESSAGE );
    }

}



