package com.novusradix.JavaPop.Client.Tools;

import java.awt.Point;

/**
 *
 * @author gef
 */
public interface NotTool {
   
    public void PrimaryAction(Point p);
    public void ButtonDown(Point p);
    public void ButtonUp(Point p);
    public void SecondaryAction(Point p);
    public void Select();
    
    public String getIconName();
    public String getToolTip();
    public Point getPosition();
    public ToolGroup getGroup();

    void setPosition(Point p);
}
