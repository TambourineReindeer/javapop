package com.novusradix.JavaPop.Client.Tools;

import com.novusradix.JavaPop.Client.Client;
import com.novusradix.JavaPop.Client.GLToolButton;
import java.awt.Point;

/**
 *
 * @author gef
 */
public abstract class Tool {

    protected ToolGroup toolGroup;
    protected Client client;
    private Point position;

    private Tool() {
    }

    protected Tool(ToolGroup tg, Client c) {
        toolGroup = tg;
        client = c;
        position = new Point();
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
}
