package com.novusradix.JavaPop.Client.AI;

import com.novusradix.JavaPop.Client.AI.Houses.House;
import com.novusradix.JavaPop.Math.Helpers;
import com.novusradix.JavaPop.Messaging.Tools.UpDown;
import java.awt.Point;
import java.util.Collections;

/**
 *
 * @author gef
 */
public class AI implements Runnable {

    Game game;
    Houses.House targetHouse;
    int currentRadius;
    Thread thread;
    boolean killme;

    AI(Game g) {
        game = g;
        thread = new Thread(this, "AI Player");
        killme = false;
        thread.start();
    }

    public void kill() {
        killme = true;
        thread.interrupt();
    }

    public void run() {
        ailoop:
        while (!killme) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                return;
            }
            if (targetHouse != null) {
                if (!game.AIHouses.getHouses(game.me).contains(targetHouse)) {
                    targetHouse = null;                //select a house
                }
            }
            if (targetHouse == null) {
                currentRadius = 1;
                int minLevel = 40;
                for (House h : game.AIHouses.getHouses(game.me)) {
                    if (h.level < minLevel) {
                        minLevel = h.level;
                        targetHouse = h;

                    }
                }
                continue ailoop;
            }
            //flatten some land
            if (targetHouse != null) {
                Collections.shuffle(Helpers.shuffledRings.get(currentRadius));
                for (Point offset : Helpers.shuffledRings.get(currentRadius)) {
                    Point p = new Point(targetHouse.pos.x + offset.x, targetHouse.pos.y + offset.y);
                    if (game.heightMap.inBounds(p)) {
                        int diff = game.heightMap.getHeight(p) - game.heightMap.getHeight(targetHouse.pos);
                        if (diff != 0) {
                            game.client.sendMessage(new UpDown(p, diff < 0));
                            continue ailoop;
                        }
                    }
                }
                if (currentRadius == 5) {
                    targetHouse = null;
                }
                currentRadius++;
                continue ailoop;

            }

        }
    }
}
