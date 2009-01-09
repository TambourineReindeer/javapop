package com.novusradix.JavaPop.Client;

import com.novusradix.JavaPop.Client.Tools.Tool;
import com.novusradix.JavaPop.Client.Tools.ToolGroup;
import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author gef
 */
public class GLToolGroupButton extends GLButton {

    private ToolGroup toolGroup;
    private static GLToolGroupButton selected;
    private Collection<GLToolButton> buttons;

    @Override
    protected boolean isSelected() {
        return selected == this;
    }

    @Override
    public void select() {
        if (selected != null) {
            for (GLToolButton b : selected.buttons) {
                b.setVisible(false);
            }
        }
        selected = this;
        for (GLToolButton b : selected.buttons) {
            b.setVisible(true);
        }
    }

    public ToolGroup getToolGroup() {
        return toolGroup;
    }

    public GLToolGroupButton(ToolGroup tg, ClickableHandler ch, Collection<GLObject> objects) {
        super(ch, objects);
        toolGroup = tg;
        int[] x,  y;
        x = new int[4];
        y = new int[4];
        Point p = tg.getPosition();
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
        texname = tg.getIconName();
        buttonShape = new Polygon(x, y, 4);
        buttons = new ArrayList<GLToolButton>();
        for (Tool t : tg.getTools()) {
            buttons.add(new GLToolButton(t, this, ch, objects));
        }
    }
}