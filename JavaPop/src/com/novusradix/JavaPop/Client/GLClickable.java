
package com.novusradix.JavaPop.Client;

import java.awt.Shape;
import java.awt.event.MouseEvent;

/**
 *
 * @author gef
 */
public interface GLClickable {
    public Shape getShape();
    public void mouseDown(MouseEvent e);
    public void mouseUp(MouseEvent e);
    public void mouseOver(MouseEvent e);
    public void mouseOut(MouseEvent e);
    public boolean anchorLeft();
    public boolean anchorTop();
    public boolean isVisible();
    public void setVisible(boolean visible);
}
