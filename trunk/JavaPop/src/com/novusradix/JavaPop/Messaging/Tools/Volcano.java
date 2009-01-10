/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novusradix.JavaPop.Messaging.Tools;

import com.novusradix.JavaPop.Server.Effects.VolcanoEffect;
import com.novusradix.JavaPop.Messaging.*;
import java.awt.Point;

/**
 *
 * @author erinhowie
 */
public class Volcano extends Message {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    public Point p;
    public boolean primaryAction;

    public Volcano(Point p) {
        this.p = p;
    }

    @Override
    public void execute() {

        serverGame.addEffect(new VolcanoEffect(p));
                
    }
}
