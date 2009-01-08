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
public interface Tool {
   
    public String getIconName();
    public String getToolTip();
    public String getType();
    public void PrimaryAction(Point p);
    public void StopPrimaryAction();
    public void SecondaryAction(Point p);
    public Point getPosition();
}
