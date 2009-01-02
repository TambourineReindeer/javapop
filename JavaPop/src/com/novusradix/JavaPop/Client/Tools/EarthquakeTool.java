package com.novusradix.JavaPop.Client.Tools;

import com.novusradix.JavaPop.Messaging.Tools.Earthquake;
import java.awt.Point;

/**
 *
 * @author mom
 */
public class EarthquakeTool extends BaseTool {
    
    public void PrimaryAction(Point p) {
        client.sendMessage(new Earthquake(p));
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
