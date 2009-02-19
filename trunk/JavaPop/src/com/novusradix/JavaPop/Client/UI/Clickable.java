package com.novusradix.JavaPop.Client.UI;

import java.awt.Cursor;
import java.awt.Shape;
import java.awt.event.MouseEvent;

/**
 *
 * @author gef
 */
public interface Clickable extends GLObject2D{

    public Shape getShape();
    public boolean anchorLeft();
    public boolean anchorTop();
    public boolean inScreenSpace();
    public void mouseDown(MouseEvent e);
    public void mouseUp(MouseEvent e);
    public void mouseOver(MouseEvent e);
    public void mouseOut(MouseEvent e);
    public void mouseDrag(float oldX, float oldY, float newX, float newY);
    public boolean isVisible();
    public void setVisible(boolean visible);
    public Cursor getCursor();
}
