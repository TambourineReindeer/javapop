/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novusradix.JavaPop.Messaging.Tools;

import com.novusradix.JavaPop.Messaging.*;
import java.awt.Point;
import java.util.logging.Level;
import java.util.logging.Logger;


import java.util.Random;

/**
 *
 * @author erinhowie
 */
public class Earthquake extends Message {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    public Point p;
    public boolean primaryAction;

    public Earthquake(Point p) {
        this.p = p;
    }

    @Override
    public void execute() {

        new Thread(new Runnable() {

            public void run() {
                Point q = p;
                Random r = new Random();
                int d = r.nextInt(4);
                try {
                    for (int i = 0; i < 10; i++) {
                        serverGame.heightMap.down(q);
                        switch (d) {
                            case 0:
                                q.x += 1;
                                break;
                            case 1:
                                q.y += 1;
                                break;
                            case 2:
                                q.x -= 1;
                                break;
                            case 3:
                                q.y -= 1;
                                break;

                        }
                        int move = r.nextInt(6);
                        if (move == 0) {
                            d = (d + 1) % 4;

                        }
                        if (move == 1) {
                            d = (d - 1) % 4;
                        }
                        Thread.sleep(200);
                    }
                    for (int i = 0; i < 2; i++) {
                        serverGame.heightMap.down(p);
                        Thread.sleep(200);
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(Earthquake.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }).start();
    }
}
