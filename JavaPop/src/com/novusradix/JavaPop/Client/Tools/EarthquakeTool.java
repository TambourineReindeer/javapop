package com.novusradix.JavaPop.Client.Tools;

import com.novusradix.JavaPop.Client.Client;
import com.novusradix.JavaPop.Messaging.Tools.Earthquake;
import java.awt.Point;

/**
 *
 * @author gef
 */
public class EarthquakeTool extends Tool {
    
    public EarthquakeTool(ToolGroup tg, Client c)
    {
        super(tg, c);
    }
    
    @Override
    public void PrimaryAction(Point p) {
        client.sendMessage(new Earthquake(p));
    }

    public String getIconName() {
        return "/com/novusradix/JavaPop/icons/Earthquake.png";
    }

    public String getToolTip() {
        return "Earthquake";
    }

}
