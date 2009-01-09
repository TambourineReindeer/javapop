/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novusradix.JavaPop.Messaging.Tools;

import com.novusradix.JavaPop.HeightMap;
import com.novusradix.JavaPop.Messaging.*;
import com.novusradix.JavaPop.Tile;
import java.awt.Point;
import java.util.logging.Level;
import java.util.logging.Logger;

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

        new Thread(new Runnable() {

            public void run() {
                try {
                    for (int i = 0; i < 10; i++) {
                        serverGame.heightMap.up(p);
                        Thread.sleep(200);
                    }
                    serverGame.heightMap.down(p);
                    serverGame.heightMap.setTile(p, Tile.LAVA);

                    //new VolcanoEffect(p)??
                    Thread.sleep(200);

                } catch (InterruptedException ex) {
                    Logger.getLogger(Volcano.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }).start();
    }
}
