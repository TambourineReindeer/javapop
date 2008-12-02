/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novusradix.JavaPop.Client.Tools;

/**
 *
 * @author mom
 */
public class LightningTool extends BaseTool {


    public void PrimaryAction(int x, int y) {
    }

  
    public void SecondaryAction(int x, int y) {
        setTool(ToolType.RaiseLower);
    }
 
}
