/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novusradix.JavaPop.Client.Tools;

import java.awt.Point;

/**
 *
 * @author mom
 */
public class LightningTool extends BaseTool {
    
    public void PrimaryAction(Point p) {
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
        return new Point(0,250);
    }
}
