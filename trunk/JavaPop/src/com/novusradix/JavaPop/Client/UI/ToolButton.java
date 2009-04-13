package com.novusradix.JavaPop.Client.UI;

import com.novusradix.JavaPop.Client.Tools.RaiseLowerTool;
import com.novusradix.JavaPop.Client.Tools.Tool;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Polygon;

/**
 *
 * @author gef
 */
public class ToolButton extends Button {

    private static ToolButton selected;
    private static ToolButton defaultToolButton;
    private Tool tool;
    private ToolGroupButton groupButton;

    public Tool getTool() {
        return tool;
    }

    public ToolButton(Tool t, ToolGroupButton parent, ClickableHandler ch) {
        super(ch);
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

        visible = false;
        texname = t.getIconName();
        buttonShape = new Polygon(x, y, 4);
        groupButton = parent;

        if (t.getClass() == RaiseLowerTool.class) {
            defaultToolButton = this;
        }
    }

    public static void selectDefault() {
        if (defaultToolButton != null) {
            defaultToolButton.select();
        }
    }

    public static Tool getSelected() {
        return selected.tool;
    }

    @Override
    public void select() {
        selected = this;
        //groupButton.select();
        tool.Select();
    }

    @Override
    protected boolean isSelected() {
        return selected == this;
    }

    public Cursor getCursor() {
        return Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
    }


}
