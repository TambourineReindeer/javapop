/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.novusradix.JavaPop.Client.Tools;

import com.novusradix.JavaPop.Client.*;
import com.novusradix.JavaPop.Client.Tools.Tool;
import com.novusradix.JavaPop.Messaging.UpDown;

/**
 *
 * @author mom
 */
public class RaiseLowerTool extends Tool {

    Client client;
    public RaiseLowerTool(Client c)
    {
        client = c;
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
