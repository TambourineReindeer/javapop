package com.novusradix.JavaPop.Client.Tools;

import com.novusradix.JavaPop.Client.Client;
import com.novusradix.JavaPop.Messaging.Tools.Volcano;
import java.awt.Point;

/**
 *
 * @author gef
 */
public class VolcanoTool extends Tool {

    public VolcanoTool(ToolGroup tg, Client c) {
        super(tg, c);
    }

    @Override
    public void PrimaryAction(Point p) {
        client.sendMessage(new Volcano(p));
    }

    public String getIconName() {
        return "/com/novusradix/JavaPop/icons/Volcano.png";
    }

    public String getToolTip() {
        return "Volcano";
    }

}
