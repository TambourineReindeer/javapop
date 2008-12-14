/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novusradix.JavaPop.Messaging;

import java.awt.Point;

/**
 *
 * @author mom
 */
public class MoveAnkh extends Message {

    private Point where;

    public MoveAnkh(Point p) {
        where = p;
    }

    @Override
    public void execute() {
           serverPlayer.MoveAnkh(where);    
    }
}
