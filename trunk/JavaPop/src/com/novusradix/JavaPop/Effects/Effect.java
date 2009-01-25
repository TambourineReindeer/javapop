package com.novusradix.JavaPop.Effects;

import com.novusradix.JavaPop.Server.ServerGame;
import java.io.Serializable;
import javax.media.opengl.GL;

/**
 *
 * @author gef
 */
public abstract class Effect implements Serializable{

    private static int nextId = 0;
    public int id;

    //Effects are created on the server and should be passed to game.addEffect()
    protected Effect() {
        id = nextId++;
    }

    //Effects are executed each frame by the server. To remove the effect, call game.deleteEffect(this)
    public abstract void execute(ServerGame g);

    //Effects are drawn on the client if any are needed.
    public abstract void display(GL gl, float time, com.novusradix.JavaPop.Client.Game g);
}
