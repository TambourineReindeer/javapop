/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.novusradix.JavaPop.Client.Tools;

import com.novusradix.JavaPop.Messaging.UpDown;

/**
 *
 * @author mom
 */
public class RaiseLowerTool extends Tool {

    private static RaiseLowerTool t;
    
    private RaiseLowerTool()
    {
        
    }
    
    public static Tool getTool()
    {
        if(t==null)
            t = new RaiseLowerTool();
        return t;                 
    }
    
    @Override
    public void PrimaryAction(int x, int y) {
        client.sendMessage(new UpDown(x, y, true));
    }

    @Override
    public void SecondaryAction(int x, int y) {
        client.sendMessage(new UpDown(x, y, false));
    }

}
