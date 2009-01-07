/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novusradix.JavaPop.Client;

import com.novusradix.JavaPop.Client.Tools.BaseTool;
import com.novusradix.JavaPop.Client.Tools.Tool;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.MouseEvent;

/**
 *
 * @author erinhowie
 */
public class GLToolButton extends GLButton {

    Tool tool;

    public GLToolButton(Tool t) {
        super();
        tool = t;
        int[] x, y;
        x = new int[4];
        y = new int[4];
        Point p = t.getPosition();
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
        texname = t.getIconName();
        buttonShape = new Polygon(x, y, 4);
    }

    @Override
    public void mouseUp(MouseEvent e) {
        super.mouseUp(e);
        BaseTool.setTool(tool.getClass());
    }
}
