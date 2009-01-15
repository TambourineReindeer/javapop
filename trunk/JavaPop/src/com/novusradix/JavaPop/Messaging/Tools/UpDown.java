/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novusradix.JavaPop.Messaging.Tools;

import com.novusradix.JavaPop.Messaging.*;
import java.awt.Point;

/**
 *
 * @author erinhowie
 */
public class UpDown extends Message {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private int x,y;
    private boolean primaryAction;

    public UpDown(Point p, boolean primary) {
        x=p.x;
        y=p.y;
        primaryAction = primary;
    }

    @Override
    public void execute() {
        if (primaryAction) {
            serverGame.heightMap.up(x, y);
        } else {
            serverGame.heightMap.down(x, y);
        }
    }
}
