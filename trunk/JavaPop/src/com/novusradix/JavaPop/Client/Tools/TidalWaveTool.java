/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novusradix.JavaPop.Client.Tools;

import java.awt.Point;

/**
 *
 * @author gef
 */
public class TidalWaveTool extends BaseTool {
    
    public void PrimaryAction(Point p) {
    }

    public String getIconName() {
        return "/com/novusradix/JavaPop/icons/Tidalwave.png";
    }

    public String getToolTip() {
        return "TidalWave";
    }

    public String getGroup() {
        return "Water";
    }
 
    public Point getPosition() {
        return new Point(250,-25);
    }
}
