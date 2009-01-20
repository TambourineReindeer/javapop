package com.novusradix.JavaPop.Client.Tools;

import com.novusradix.JavaPop.Client.Client;
import com.novusradix.JavaPop.Messaging.Tools.Basalt;
import java.awt.Cursor;
import java.awt.Point;

/**
 *
 * @author gef
 */
public class BasaltTool extends Tool {

    
    public BasaltTool(ToolGroup tg, Client c) {
        super(tg, c);
    }

    @Override
    public void PrimaryAction(Point p) {
        client.sendMessage(new Basalt(p, getDirection()));
    }

    public String getIconName() {
        return "/com/novusradix/JavaPop/icons/Basalt.png";
    }

    public String getToolTip() {
        return "Basalt";
    }
    
    @Override
    public Cursor getCursor()
    {
        return getDirectionalCursor();
    }

}
