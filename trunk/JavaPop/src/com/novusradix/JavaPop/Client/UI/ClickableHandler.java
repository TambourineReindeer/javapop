package com.novusradix.JavaPop.Client.UI;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.util.LinkedList;

/**
 *
 * @author gef
 */
public class ClickableHandler implements MouseMotionListener, MouseListener {

    private LinkedList<Clickable> buttons;
    private Clickable over;
    private boolean bOver;
    private MouseMotionListener mml;
    private MouseListener ml;
    private Component win;
    private float lastX,  lastY;
    private boolean virtualRightClick;

    public ClickableHandler(MouseListener fallthrough, MouseMotionListener fallthroughmotion, Component w) {
        buttons = new LinkedList<Clickable>();
        ml = fallthrough;
        mml = fallthroughmotion;
        win = w;
        virtualRightClick=false;
    }

    public void addClickable(Clickable b) {
        buttons.addFirst(b);
    }

    public void removeClickable(Clickable b) {
        buttons.remove(b);
    }

    private boolean isOver(Point p, Clickable c) {
        return c.isVisible() && c.getShape().contains(transformPoint(p, c));
    }

    public void mouseDragged(MouseEvent e) {
        if (over == null) {
            mml.mouseDragged(e);
            return;
        }
        if (over != null) {
            if (over.inScreenSpace()) {
                over.mouseDrag(lastX, lastY, e.getPoint().x, e.getPoint().y);
            } else {
                over.mouseDrag(1.0f * lastX / win.getWidth(), 1.0f * lastY / win.getHeight(), 1.0f * e.getPoint().x / win.getWidth(), 1.0f * e.getPoint().y / win.getHeight());
            }
        }
        if (isOver(e.getPoint(), over) != bOver) {
            if (bOver) {
                over.mouseOut(e);
            } else {
                over.mouseOver(e);
            }
            bOver = !bOver;
        }
        lastX = e.getPoint().x;
        lastY = e.getPoint().y;
    }

    public void mouseMoved(MouseEvent e) {
        lastX = e.getPoint().x;
        lastY = e.getPoint().y;
        for (Clickable b : buttons) {
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
            ml.mouseEntered(e);
        }
        over = null;
        mml.mouseMoved(e);
    }

    public void mouseClicked(MouseEvent e) {
        MouseEvent e2=e;
        if((e.getModifiersEx() & MouseEvent.CTRL_DOWN_MASK) == MouseEvent.CTRL_DOWN_MASK)
        {
            e2=new MouseEvent((Component)e.getSource(),e.getID(), e.getWhen(), MouseEvent.BUTTON3_DOWN_MASK , e.getX(), e.getY(), e.getClickCount(),false, MouseEvent.BUTTON3);
        }
        if (over == null) {
            ml.mouseClicked(e2);
        }
    }

    public void mousePressed(MouseEvent e) {
        MouseEvent e2=e;
        if((e.getModifiersEx() & MouseEvent.CTRL_DOWN_MASK) == MouseEvent.CTRL_DOWN_MASK)
        {
            e2=new MouseEvent((Component)e.getSource(),e.getID(), e.getWhen(), MouseEvent.BUTTON3_DOWN_MASK , e.getX(), e.getY(), e.getClickCount(),false, MouseEvent.BUTTON3);
        virtualRightClick = true;
        }
        if (over != null) {
            over.mouseDown(e2);
        } else {
            ml.mousePressed(e2);
        }
    }

    public void mouseReleased(MouseEvent e) {
        MouseEvent e2=e;
        if(virtualRightClick)
        {
            virtualRightClick=false;
            e2=new MouseEvent((Component)e.getSource(),e.getID(), e.getWhen(), MouseEvent.BUTTON3_DOWN_MASK , e.getX(), e.getY(), e.getClickCount(),false, MouseEvent.BUTTON3);

        }
        if (over != null) {
            over.mouseUp(e2);
        } else {
            ml.mouseEntered(e2);
            ml.mouseReleased(e2);
        }
    }

    public void mouseEntered(MouseEvent e) {
        ml.mouseEntered(e);
    }

    public void mouseExited(MouseEvent e) {
        ml.mouseExited(e);
    }

    private Point2D transformPoint(Point p, Clickable b) {
        Point2D p1 = new Point2D.Float();
        if (b.inScreenSpace()) {
            p1.setLocation(p);
            if (!b.anchorLeft()) {
                p1.setLocation(win.getWidth() - p1.getX(), p1.getY());
            }
            if (!b.anchorTop()) {
                p1.setLocation(p1.getX(), win.getHeight() - p1.getY());
            }
            return p1;
        } else {
            p1.setLocation(1.0f * p.x / win.getWidth(), 1.0f * p.y / win.getHeight());
            if (!b.anchorLeft()) {
                p1.setLocation(1 - p1.getX(), p1.getY());
            }
            if (!b.anchorTop()) {
                p1.setLocation(p1.getX(), 1 - p1.getY());
            }
            return p1;
        }
    }
}

