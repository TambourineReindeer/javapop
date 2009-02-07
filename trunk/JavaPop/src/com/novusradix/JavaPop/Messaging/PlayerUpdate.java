package com.novusradix.JavaPop.Messaging;

import com.novusradix.JavaPop.Client.Player;
import com.novusradix.JavaPop.Server.ServerPlayer;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author gef
 */
public class PlayerUpdate extends Message implements Externalizable {

    private static final long serialVersionUID = 1L;
    private Collection<Info> info;

    public PlayerUpdate(ServerPlayer p) {
        info = new ArrayList<Info>(1);
        info.add(new Info(p));
    }

    public PlayerUpdate(Collection<ServerPlayer> ps) {
        info = new ArrayList<Info>();
        for (ServerPlayer p : ps) {
            info.add(new Info(p));
        }
    }

    @Override
    public void execute() {
        if (clientGame != null) {
            for (Info i : info) {
                Player p = clientGame.players.get(i.id);
                if(p!=null)
                    p.update(i);
            }
        }
    }

    public PlayerUpdate() {
    }

    public void writeExternal(ObjectOutput o) throws IOException {
        o.writeInt(info.size());
        for (Info i : info) {
            o.writeObject(i);
        }
    }

    public void readExternal(ObjectInput i) throws IOException, ClassNotFoundException {
        info = new ArrayList<Info>();
        int n = i.readInt();
        for (; n > 0; n--) {
            Info inf = (Info) i.readObject();

            info.add(inf);
        }
    }

    public static class Info implements Externalizable {

        public int id;
        public String name;
        public int ankhx,  ankhy;
        public float[] colour;
        public double mana;

        public Info() {
        }

        public Info(ServerPlayer p) {
            this.id = p.getId();
            this.name = p.getName();
            this.ankhx = p.getPapalMagnet().x;
            this.ankhy = p.getPapalMagnet().y;
            this.colour = p.getColour();
            this.mana = p.getMana();
        }

        public void writeExternal(ObjectOutput o) throws IOException {
            o.writeInt(id);
            o.writeUTF(name);
            o.writeInt(ankhx);
            o.writeInt(ankhy);
            o.writeFloat(colour[0]);
            o.writeFloat(colour[1]);
            o.writeFloat(colour[2]);
            o.writeDouble(mana);
        }

        public void readExternal(ObjectInput i) throws IOException, ClassNotFoundException {
            id = i.readInt();
            name = i.readUTF();
            ankhx = i.readInt();
            ankhy = i.readInt();
            colour = new float[3];
            colour[0] = i.readFloat();
            colour[1] = i.readFloat();
            colour[2] = i.readFloat();
            mana = i.readDouble();
        }
    }
}
