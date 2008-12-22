package com.novusradix.JavaPop.Client.Tools;

import java.awt.Point;

/**
 *
 * @author mom
 */
public class EarthquakeTool extends BaseTool {
    
    public void PrimaryAction(Point p) {
    }

    public String getIconName() {
        return "/com/novusradix/JavaPop/icons/Earthquake.png";
    }

    public String getToolTip() {
        return "Earthquake";
    }

    public String getType() {
        return "Earth";
    }
 
}
