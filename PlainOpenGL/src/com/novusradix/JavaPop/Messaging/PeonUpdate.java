/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novusradix.JavaPop.Messaging;

import com.novusradix.JavaPop.Math.Vector2;
import com.novusradix.JavaPop.Server.Peons;
import java.awt.Point;
import java.io.Serializable;
import java.util.Vector;

/**
 *
 * @author mom
 */
public class PeonUpdate extends Message {

    Vector<Detail> details;

    @SuppressWarnings("unchecked")
    public PeonUpdate(Vector<Detail> pds) {
        details = (Vector<Detail>) pds.clone();
    }

    @Override
    public void execute() {
        for (Detail d : details) {
            client.game.peons.Update(d);
        }
    }

    public static class Detail implements Serializable {

        public int id;
        public Peons.State state;
        public float dx,  dy;
        public Point dest;
        public Vector2 pos;
        public int playerId;

        public Detail(int id, Peons.State state, Vector2 pos, Point dest, float dx, float dy, int playerId) {
            this.id = id;
            this.state = state;
            this.dx = dx;
            this.dy = dy;
            this.dest = dest;
            this.pos = pos;
            this.playerId = playerId;
        }

        @Override
        public String toString() {
            return id + ": " + state.toString();
        }
    }
}
