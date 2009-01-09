package com.novusradix.JavaPop.Client.Tools;

import com.novusradix.JavaPop.Client.Client;
import com.novusradix.JavaPop.Messaging.Tools.Swamp;
import java.awt.Point;

/**
 *
 * @author gef
 */
public class SwampTool extends BaseTool {
public SwampTool(ToolGroup tg, Client c)
    {
        super(tg, c);
    }
    
    @Override
    public void PrimaryAction(Point p) {
        client.sendMessage(new Swamp(p));
    }

    public String getIconName() {
        return "/com/novusradix/JavaPop/icons/Swamp.png";
    }

    public String getToolTip() {
        return "Swamp";
    }
 
    public Point getPosition() {
        return new Point(100,-100);
    }
}
