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
            serverGame.h.up(x, y);
        } else {
            serverGame.h.down(x, y);
        }
    }
}
