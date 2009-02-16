package com.novusradix.JavaPop.Client.UI;

import com.novusradix.JavaPop.Client.Tools.Tool;
import com.novusradix.JavaPop.Client.Tools.ToolGroup;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Polygon;
import java.util.HashMap;
import java.util.Map;
import javax.media.opengl.GL;

/**
 *
 * @author gef
 */
public class ToolGroupButton extends Button {

    private ToolGroup toolGroup;
    private static ToolGroupButton selected;
    private Map<Integer, ToolButton> buttons;
    private static Map<Integer, ToolGroupButton> groupButtons = new HashMap<Integer, ToolGroupButton>();
    private static int nextId = 1;

    public static void selectVisibleToolButton(int index) {
        if (selected.buttons.containsKey(index)) {
            selected.buttons.get(index).select();
        }
    }

    public static void selectVisibleToolGroupButton(int index) {
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
            for (ToolButton b : selected.buttons.values()) {
                b.setVisible(false);
            }
        }
        selected = this;
        for (ToolButton b : selected.buttons.values()) {
            b.setVisible(true);
        }
    }

    public ToolGroup getToolGroup() {
        return toolGroup;
    }

    public ToolGroupButton(ToolGroup tg, ClickableHandler ch) {
        super(ch);
        toolGroup = tg;
        int[] x, y;
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
        buttons = new HashMap<Integer, ToolButton>();
        int n = 1;
        for (Tool t : tg.getTools()) {
            buttons.put(n++, new ToolButton(t, this, ch));
        }
        groupButtons.put(nextId++, this);
    }

    @Override
    public void display(GL gl, float time, int screenWidth, int screenHeight) {
        super.display(gl, time, screenWidth, screenHeight);
        for (Button b : buttons.values()) {
            b.display(gl, time, screenWidth, screenHeight);
        }
    }

    @Override
    public void init(GL gl) {
        super.init(gl);
        for (Button b : buttons.values()) {
            b.init(gl);
        }
    }

    public Cursor getCursor() {
        return Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
    }
}