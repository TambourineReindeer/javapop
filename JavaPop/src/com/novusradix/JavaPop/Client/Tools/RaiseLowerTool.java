/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novusradix.JavaPop.Client.Tools;

import com.novusradix.JavaPop.Client.Client;
import com.novusradix.JavaPop.Messaging.Tools.UpDown;
import java.awt.Point;

/**
 *
 * @author mom
 */
public class RaiseLowerTool extends BaseTool {

    public RaiseLowerTool(ToolGroup tg, Client c) {
        super(tg, c);
    }

    @Override
    public void PrimaryAction(Point p) {
        client.sendMessage(new UpDown(p, true));
    }

    @Override
    public void SecondaryAction(Point p) {
        client.sendMessage(new UpDown(p, false));
    }

    public String getIconName() {
        return "/com/novusradix/JavaPop/icons/UpDown.png";
    }

    public String getToolTip() {
        return "Raise/Lower land";
    }

    public Point getPosition() {
        return new Point(0, -150);
    }
}
