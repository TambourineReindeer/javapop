package com.novusradix.JavaPop.Client.Tools;

import java.util.Collection;

/**
 *
 * @author gef
 */
public interface ToolGroup {

    public String getIconName();
    public Collection<Tool> getTools();
    public void addTool(Tool t);
    
}
