package com.novusradix.JavaPop.Client;

import com.novusradix.JavaPop.Client.Tools.Tool;
import com.novusradix.JavaPop.Client.Tools.ToolGroup;
import java.awt.Point;
import java.awt.Polygon;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author gef
 */
public class GLToolGroupButton extends GLButton {

    private ToolGroup toolGroup;
    private static GLToolGroupButton selected;
    private Map<Integer, GLToolButton> buttons;
    private static Map<Integer, GLToolGroupButton> groupButtons = new HashMap<Integer, GLToolGroupButton>();
    private static int nextId = 1;
    
    public static void selectVisibleToolButton(int index) {
        if (selected.buttons.containsKey(index)) {
            selected.buttons.get(index).select();
        }
    }

    public static void selectVisibleToolGroupButton(int index){
        if (groupButtons.containsKey(index)) {
            groupButtons.get(index).select();
        }
    }
    
    @Override
    protected boolean isSelected() {
        return selected == this;
    }

    @Override
    public void select() {
        if (selected != null) {
            for (GLToolButton b : selected.buttons.values()) {
                b.setVisible(false);
            }
        }
        selected = this;
        for (GLToolButton b : selected.buttons.values()) {
            b.setVisible(true);
        }
    }

    public ToolGroup getToolGroup() {
        return toolGroup;
    }

    public GLToolGroupButton(ToolGroup tg, ClickableHandler ch, Collection<GLObject> objects) {
        super(ch, objects);
        toolGroup = tg;
        int[] x,   y;
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
        buttons = new HashMap<Integer, GLToolButton>();
        int n = 1;
        for (Tool t : tg.getTools()) {
            buttons.put(n++, new GLToolButton(t, this, ch, objects));
        }
        groupButtons.put(nextId++, this);
    }
}