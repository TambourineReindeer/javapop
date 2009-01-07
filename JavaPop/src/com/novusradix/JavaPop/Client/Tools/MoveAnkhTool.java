/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.novusradix.JavaPop.Client.Tools;

import com.novusradix.JavaPop.Messaging.Tools.MoveAnkh;
import java.awt.Point;

/**
 *
 * @author mom
 */
public class MoveAnkhTool extends BaseTool{

    public void PrimaryAction(Point p) {
        client.sendMessage(new MoveAnkh(p));
    }
    public String getIconName() {
        return "/com/novusradix/JavaPop/icons/GoToAnkh.png";
    }

    public String getToolTip() {
        return "MoveAnkh";
    }

    public String getType() {
        return "Earth";
    }
    
    public Point getPosition() {
        return new Point(50,-125);
    }
}
