package com.novusradix.JavaPop.Client.Tools;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author gef
 */
public class ToolGroup {

    private Collection<Tool> tools;
    private String iconName;
    private Point position;
    private Point nextToolPosition;
    static private Point nextGroupPosition;
    
    static {
        nextGroupPosition = new Point(50, -175);
    }

    public ToolGroup(String icon) {
        tools = new ArrayList<Tool>();
        iconName = icon;
        this.position = new Point(nextGroupPosition);
        nextGroupPosition.translate(50, 25);
        nextToolPosition = new Point(0,-150);
    }

    public Collection<Tool> getTools() {
        return tools;
    }

    public void addTool(Tool t) {
        tools.add(t);
        t.setPosition(nextToolPosition);
        nextToolPosition.translate(50, 25);
    }

    public String getIconName() {
        return iconName;
    }

    public Point getPosition() {
        return position;
    }
}
