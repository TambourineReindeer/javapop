/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novusradix.JavaPop.Messaging;

import com.novusradix.JavaPop.Math.Vector2;
import com.novusradix.JavaPop.Server.Peons;
import java.awt.Point;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author mom
 */
public class PeonUpdate extends Message implements Externalizable {

    private static final long serialVersionUID = 1L;
    Collection<Detail> details;

    @SuppressWarnings("unchecked")
    public PeonUpdate(Collection<Detail> pds) {
        details = new ArrayList(pds);
    }

    @Override
    public void execute() {
        for (Detail d : details) {
            client.game.peons.Update(d);
        }
    }

    public static class Detail {

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
            if (dest == null) {
                this.dest = new Point();
            } else {
                this.dest = dest;
            }
            this.pos = pos;
            this.playerId = playerId;
        }

        private Detail() {
        }

        @Override
        public String toString() {
            return id + ": " + state.toString();
        }
    }

    public PeonUpdate() {
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(details.size());
        for (Detail d : details) {
            out.writeInt(d.id);
            out.writeInt(d.state.ordinal());
            out.writeFloat(d.dx);
            out.writeFloat(d.dy);
            out.writeFloat(d.pos.x);
            out.writeFloat(d.pos.y);
            out.writeInt(d.dest.x);
            out.writeInt(d.dest.y);
            out.writeInt(d.playerId);
        }

    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        int i = in.readInt();
        details = new ArrayList<PeonUpdate.Detail>();
        for (; i > 0; i--) {
            Detail d = new Detail();
            d.id = in.readInt();
            d.state = Peons.State.values()[in.readInt()];
            d.dx = in.readFloat();
            d.dy = in.readFloat();
            d.pos = new Vector2();
            d.pos.x = in.readFloat();
            d.pos.y = in.readFloat();
            d.dest = new Point();
            d.dest.x = in.readInt();
            d.dest.y = in.readInt();
            d.playerId = in.readInt();
            details.add(d);
        }
    }
}
