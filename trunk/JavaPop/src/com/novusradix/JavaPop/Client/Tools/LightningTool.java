package com.novusradix.JavaPop.Client.Tools;

import com.novusradix.JavaPop.Client.Client;
import com.novusradix.JavaPop.Client.UI.ToolButton;
import com.novusradix.JavaPop.Messaging.Tools.StartLightning;
import com.novusradix.JavaPop.Messaging.Tools.StopLightning;
import java.awt.Point;

/**
 *
 * @author gef
 */
public class LightningTool extends Tool {

    public LightningTool(ToolGroup tg, Client c) {
        super(tg,c);
    }

    @Override
    public void PrimaryDown(Point p) {
        client.sendMessage(new StartLightning(p));
    }

    @Override
    public void PrimaryUp(Point p) {
        client.sendMessage(new StopLightning());
        ToolButton.selectDefault();
    }

    public String getIconName() {
        return "/com/novusradix/JavaPop/icons/Lightning.png";
    }

    public String getToolTip() {
        return "Lightning";
    }
}
