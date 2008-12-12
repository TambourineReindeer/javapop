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
    enum ToolType {RaiseLower, Lightning};
    
    public void PrimaryAction(Point p);
    public void SecondaryAction(Point p);
  
}
