package com.novusradix.JavaPop.Client.Tools;

import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author gef
 */
public abstract class BaseToolGroup implements ToolGroup {

    private Collection<Tool> tools;

    protected BaseToolGroup()
    {
        tools = new ArrayList<Tool>();
    }

    public Collection<Tool> getTools() {
        return tools;
    }

    public void addTool(Tool t) {
        tools.add(t);
    }

}
