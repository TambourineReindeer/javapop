package com.novusradix.JavaPop.Client.Tools;

import com.novusradix.JavaPop.Client.Client;
import com.novusradix.JavaPop.Messaging.Tools.Plague;
import java.awt.Point;

/**
 *
 * @author gef
 */
public class PlagueTool extends Tool {

    public PlagueTool(ToolGroup tg, Client c) {
        super(tg, c);
    }

    @Override
    public void PrimaryDown(Point p) {
        client.sendMessage(new Plague(p));

    }

    public String getIconName() {
        return "/com/novusradix/JavaPop/icons/Plague.png";
    }

    public String getToolTip() {
        return "Plague";
    }

}
