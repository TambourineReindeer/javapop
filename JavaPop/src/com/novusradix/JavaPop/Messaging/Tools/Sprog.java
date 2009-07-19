package com.novusradix.JavaPop.Messaging.Tools;

import com.novusradix.JavaPop.Messaging.*;
import com.novusradix.JavaPop.Server.ServerHouses.ServerHouse;
import java.awt.Point;

/**
 *
 * @author gef
 */
public class Sprog extends Message {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private int x,  y;
   
    public Sprog(Point p) {
        x = p.x;
        y = p.y;
    }

    @Override
    public void execute() {
        ServerHouse h = serverGame.houses.getHouse(x, y);
        if (h != null) {
            h.sprog();
        }
    }
}
