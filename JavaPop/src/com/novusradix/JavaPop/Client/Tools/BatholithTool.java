package com.novusradix.JavaPop.Client.Tools;

import com.novusradix.JavaPop.Client.Client;
import com.novusradix.JavaPop.Messaging.Tools.StartBatholith;
import com.novusradix.JavaPop.Messaging.Tools.StopBatholith;
import java.awt.Point;

/**
 *
 * @author gef
 */
public class BatholithTool extends Tool {

    public BatholithTool(ToolGroup tg, Client c) {
        super(tg, c);
    }

    @Override
    public void PrimaryDown(Point p) {
        client.sendMessage(new StartBatholith(p));
    }

    @Override
    public void PrimaryUp(Point p) {
        client.sendMessage(new StopBatholith());
    }

    public String getIconName() {
        return "/com/novusradix/JavaPop/icons/Batholith.png";
    }

    public String getToolTip() {
        return "Batholith";
    }
}
