package com.javaranch.common ;

/** Extend this and override the methods that you want to implement. <p>

    This is what is passed to LButton for event handling. <p>
*/
public abstract class ButtonAdapter implements ButtonListener, MoveListener
{

    public void buttonPressed(){}
    public void buttonReleased(){}
    public void rightButtonReleased(){}
    public void mouseHovering(){}
    public void mouseEntered(){}
    public void mouseExited(){}

}




