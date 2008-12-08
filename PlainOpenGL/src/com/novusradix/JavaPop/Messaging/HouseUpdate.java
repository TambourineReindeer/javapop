/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novusradix.JavaPop.Messaging;

import java.awt.Point;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

/**
 *
 * @author mom
 */
public class HouseUpdate extends Message {

    public Vector<Detail> details;

    public HouseUpdate(Vector<Detail> ds) {
        details = (Vector<Detail>) ds.clone();
    }

    @Override
    public void execute() {
        for (Detail d : details) {
            client.game.houses.updateHouse(d.pos, d.team, d.level);
        }
    }

    public static class Detail implements Serializable {

        Point pos;
        int level;
        int team;

        public Detail(Point pos, int team, int level) {
            this.pos = pos;
            this.team = team;
            this.level = level;
        }
    }
}
