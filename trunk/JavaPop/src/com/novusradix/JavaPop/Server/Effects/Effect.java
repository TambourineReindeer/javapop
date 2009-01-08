package com.novusradix.JavaPop.Server.Effects;

import com.novusradix.JavaPop.Server.Game;
import java.io.Serializable;
import javax.media.opengl.GL;

/**
 *
 * @author gef
 */
public abstract class Effect implements Serializable{

    private static int nextId = 0;
    public int id;

    protected Effect() {
        id = nextId++;
    }

    public abstract void execute(Game g);

    public abstract void display(GL gl, float time, com.novusradix.JavaPop.Client.Game g);
}
