/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novusradix.JavaPop.Client;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.*;
import java.util.LinkedList;

/**
 *
 * @author gef
 */
public class ClickableHandler implements MouseMotionListener, MouseListener {

    private LinkedList<GLClickable> buttons;
    private GLClickable over;
    private boolean bOver;
    private MouseMotionListener mml;
    private MouseListener ml;
    private Component win;
    public ClickableHandler(MouseListener fallthrough, MouseMotionListener fallthroughmotion, Component w) {
        buttons = new LinkedList<GLClickable>();
        ml = fallthrough;
        mml = fallthroughmotion;
        win = w;
    }

    public void addClickable(GLClickable b) {
        buttons.addFirst(b);
    }

    private boolean isOver(Point p, GLClickable c)
    {
        return c.isVisible() && c.getShape().contains(transformPoint(p, c));
    }
    
    public void mouseDragged(MouseEvent e) {
        if (over == null) {
            mml.mouseDragged(e);
        }
        else
        if (isOver(e.getPoint(), over) != bOver) {
            if (bOver) {
                over.mouseOut(e);
            } else {
                over.mouseOver(e);
            }
            bOver = !bOver;
        }
    }

    public void mouseMoved(MouseEvent e) {
        for (GLClickable b : buttons) {
            if (isOver(e.getPoint(), b)) {
                if (over == b) {
                    return;
                }
                if (over != null) {
                    over.mouseOut(e);
                } else {
                    ml.mouseExited(e);
                }
                over = b;
                bOver = true;
                over.mouseOver(e);
                win.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                return;
            }
        }
        if (over != null) {
            over.mouseOut(e);
        }
        over = null;
        ml.mouseEntered(e);
        mml.mouseMoved(e);
    }

    public void mouseClicked(MouseEvent e) {
        if (over == null) {
            ml.mouseClicked(e);
        }
    }

    public void mousePressed(MouseEvent e) {
        if (over != null) {
            over.mouseDown(e);
        } else {
            ml.mousePressed(e);
        }

    }

    public void mouseReleased(MouseEvent e) {
        if (over != null) {
            over.mouseUp(e);
        } else {
            ml.mouseEntered(e);
            ml.mouseReleased(e);
        }
    }

    public void mouseEntered(MouseEvent e) {
        ml.mouseEntered(e);
    }

    public void mouseExited(MouseEvent e) {
        ml.mouseExited(e);
    }
    
    private Point transformPoint(Point p, GLClickable b)
    {
        Point p1 = new Point(p);
        if(!b.anchorLeft())
            p1.x=win.getWidth()-p1.x;
        if(!b.anchorTop())
            p1.y = win.getHeight()-p1.y;
        return p1;
    }
}

