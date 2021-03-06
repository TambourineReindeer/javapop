package com.novusradix.JavaPop.Client.UI;

import com.novusradix.JavaPop.Client.*;
import com.novusradix.JavaPop.Server.ServerPlayer.PeonMode;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Polygon;

/**
 *
 * @author gef
 */
public class BehaviourButton extends Button {

    PeonMode mode;
    private static Button selected;
    private Client client;
    
    public BehaviourButton(PeonMode m, ClickableHandler ch, Client c) {
        super(ch);
        client = c;
        mode = m;
        int[] x, y;
        x = new int[4];
        y = new int[4];
        Point p;
        switch (mode) {
            case ANKH:
                p = new Point(-151, -25);
                texname = "/com/novusradix/JavaPop/icons/Ankh.png";
                break;
            case SETTLE:
                p = new Point(-101, -50);
                texname = "/com/novusradix/JavaPop/icons/Settle.png";
                break;
            case FIGHT:
                p = new Point(-51, -75);
                texname = "/com/novusradix/JavaPop/icons/Fight.png";
                break;
            default:
            case GROUP:
                p = new Point(-1, -100);
                texname = "/com/novusradix/JavaPop/icons/Gather.png";
                break;
        }

        if (p.y < 0) {
            p.y = -p.y;
            flipy = true;
        }
        if (p.x < 0) {
            p.x = -p.x;
            flipx = true;
        }
        x[0] = p.x;
        x[1] = p.x + 50;
        x[2] = p.x + 100;
        x[3] = p.x + 50;
        y[0] = p.y;
        y[1] = p.y + 25;
        y[2] = p.y;
        y[3] = p.y - 25;

        visible = true;

        buttonShape = new Polygon(x, y, 4);
    }

    @Override
    protected boolean isSelected() {
        return selected==this;
    }

    @Override
    public void select() {
        client.setBehaviour(mode);
        selected = this;
    }

    public Cursor getCursor() {
        return Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
    }

    public Clickable getClickableAtPoint(float x, float y) {
        return this;
    }
}
