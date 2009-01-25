package com.novusradix.JavaPop.Client.Tools;

import com.novusradix.JavaPop.Client.Client;
import com.novusradix.JavaPop.Client.GLToolButton;
import com.novusradix.JavaPop.Direction;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;

/**
 *
 * @author gef
 */
public abstract class Tool {

    protected ToolGroup toolGroup;
    protected Client client;
    private Point position;
    private Cursor[] directionalCursors;
    private Cursor defaultCursor;

    private Tool() {
    }

    protected Tool(ToolGroup tg, Client c) {
        toolGroup = tg;
        client = c;
        position = new Point();
        Toolkit tk = Toolkit.getDefaultToolkit();
        directionalCursors = new Cursor[4];
        directionalCursors[0] = tk.createCustomCursor(tk.getImage(getClass().getResource("/com/novusradix/JavaPop/cursors/north.png")), new Point(0, 0), "north");
        directionalCursors[1] = tk.createCustomCursor(tk.getImage(getClass().getResource("/com/novusradix/JavaPop/cursors/east.png")), new Point(0, 0), "east");
        directionalCursors[2] = tk.createCustomCursor(tk.getImage(getClass().getResource("/com/novusradix/JavaPop/cursors/south.png")), new Point(0, 0), "south");
        directionalCursors[3] = tk.createCustomCursor(tk.getImage(getClass().getResource("/com/novusradix/JavaPop/cursors/west.png")), new Point(0, 0), "west");
        defaultCursor = tk.createCustomCursor(tk.getImage(getClass().getResource("/com/novusradix/JavaPop/cursors/standard.png")), new Point(0, 0), "standard");
    }

    public void PrimaryAction(Point p) {
    }

    public void SecondaryAction(Point p) {
        GLToolButton.selectDefault();
    }

    public void ButtonDown(Point p) {
    }

    public void ButtonUp(Point p) {
    }

    public void Select() {
    }

    public Cursor getCursor(Point selected) {
        return getCursor();
    }

    public ToolGroup getGroup() {
        return toolGroup;
    }

    public void setPosition(Point p) {
        position.setLocation(p);
    }

    public Point getPosition() {
        return position;
    }

    public abstract String getIconName();

    public Cursor getCursor() {
        return defaultCursor;
    }

    public Cursor getDirectionalCursor() {
        int dir = (int) ((System.currentTimeMillis() / 500) % 4);
        return directionalCursors[dir];
    }

    public Direction getDirection() {
        int dir = (int) ((System.currentTimeMillis() / 500) % 4);
        return Direction.values()[dir];
    }
}
