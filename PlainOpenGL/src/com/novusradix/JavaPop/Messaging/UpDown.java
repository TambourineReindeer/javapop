/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novusradix.JavaPop.Messaging;

/**
 *
 * @author erinhowie
 */
public class UpDown extends Message {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int x,  y;
    public boolean primaryAction;
    
    public UpDown(int x, int y, boolean primary) {
        this.x = x;
        this.y = y;
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
