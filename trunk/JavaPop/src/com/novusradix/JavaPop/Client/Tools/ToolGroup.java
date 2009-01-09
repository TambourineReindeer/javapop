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

    public ToolGroup(String icon, Point position) {
        tools = new ArrayList<Tool>();
        iconName = icon;
        this.position = position;
    }

    public Collection<Tool> getTools() {
        return tools;
    }

    public void addTool(Tool t) {
        tools.add(t);
    }

    public String getIconName() {
        return iconName;
    }

    public Point getPosition() {
        return position;
    }
}
