/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novusradix.JavaPop.Client.Tools;

import com.novusradix.JavaPop.Messaging.Tools.Volcano;
import java.awt.Point;

/**
 *
 * @author mom
 */
public class VolcanoTool extends BaseTool {

    public void PrimaryAction(Point p) {
        client.sendMessage(new Volcano(p));
    }

    public String getIconName() {
        return "/com/novusradix/JavaPop/icons/Volcano.png";
    }

    public String getToolTip() {
        return "Volcano";
    }

    public String getType() {
        return "Fire";
    }
    
    public Point getPosition() {
        return new Point(0,250);
    }
}
