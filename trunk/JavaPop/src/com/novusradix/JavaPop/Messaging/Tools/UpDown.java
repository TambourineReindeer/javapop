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
	public Point p;
    public boolean primaryAction;
    
    public UpDown(Point p, boolean primary) {
        this.p = p;
        primaryAction = primary;
    }

    @Override
    public void execute() {
        if (primaryAction) {
            serverGame.heightMap.up(p);
        } else {
            serverGame.heightMap.down(p);
        }
    }
}
