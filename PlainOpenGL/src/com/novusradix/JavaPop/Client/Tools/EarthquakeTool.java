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
public class EarthquakeTool extends BaseTool {
    
    public void PrimaryAction(Point p) {
    }

    public void SecondaryAction(Point p) {
         setTool(ToolType.RaiseLower);
    }
 
}
