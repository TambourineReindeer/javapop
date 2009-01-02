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
public class TidalWaveTool extends BaseTool {
    
    public void PrimaryAction(Point p) {
    }

    public String getIconName() {
        return "/com/novusradix/JavaPop/icons/TidalWave.png";
    }

    public String getToolTip() {
        return "TidalWave";
    }

    public String getType() {
        return "Water";
    }
 
}
