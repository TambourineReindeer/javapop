/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.novusradix.JavaPop.Client.Tools;

import com.novusradix.JavaPop.Messaging.MoveAnkh;
import java.awt.Point;

/**
 *
 * @author mom
 */
public class MoveAnkhTool extends BaseTool{

    public void PrimaryAction(Point p) {
        client.sendMessage(new MoveAnkh(p));
    }
    
}
