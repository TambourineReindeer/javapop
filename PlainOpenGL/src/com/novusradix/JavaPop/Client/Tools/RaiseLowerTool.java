/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.novusradix.JavaPop.Client.Tools;

import com.novusradix.JavaPop.Messaging.UpDown;
import java.awt.Point;

/**
 *
 * @author mom
 */
public class RaiseLowerTool extends BaseTool {
    

    public void PrimaryAction(Point p) {
        client.sendMessage(new UpDown(p, true));
    }


    public void SecondaryAction(Point p) {
        client.sendMessage(new UpDown(p, false));
    }

}
