/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novusradix.JavaPop.Client.Tools;

import com.novusradix.JavaPop.Messaging.Tools.StartLightning;
import com.novusradix.JavaPop.Messaging.Tools.StopLightning;
import java.awt.Point;

/**
 *
 * @author mom
 */
public class LightningTool extends BaseTool {

    @Override
    public void ButtonDown(Point p) {
        client.sendMessage(new StartLightning(p));
    }

    @Override
    public void ButtonUp(Point p) {
        client.sendMessage(new StopLightning());
    }

    public String getIconName() {
        return "/com/novusradix/JavaPop/icons/Lightning.png";
    }

    public String getToolTip() {
        return "Lightning";
    }

    public String getType() {
        return "Air";
    }

    public Point getPosition() {
        return new Point(150, -75);
    }
}
