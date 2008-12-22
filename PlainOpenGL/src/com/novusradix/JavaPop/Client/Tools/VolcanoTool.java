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
public class VolcanoTool extends BaseTool {

    public void PrimaryAction(Point p) {
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
}
