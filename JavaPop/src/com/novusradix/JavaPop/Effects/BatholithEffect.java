package com.novusradix.JavaPop.Effects;

import com.novusradix.JavaPop.Server.Game;
import com.novusradix.JavaPop.Tile;
import java.awt.Point;
import java.util.Random;
import javax.media.opengl.GL;

/**
 *
 * @author gef
 */
public class BatholithEffect extends Effect {

    Point target;
    int age = 0;
    Random r;

    public BatholithEffect(Point target) {
        this.target = target;
        r = new Random();
    }

    @Override
    public void execute(Game g) {
        if (age % 5 == 0) {

            int px, py;
            px = target.x + r.nextInt(9) - 4;
            py = target.y + r.nextInt(9) - 4;
            if (g.heightMap.inBounds(px, py)) {
                g.heightMap.up(px, py);
            }
            px = target.x + r.nextInt(9) - 4;
            py = target.y + r.nextInt(9) - 4;
            if (g.heightMap.tileInBounds(px, py)) {
                g.heightMap.setTile(px, py, Tile.ROCK);
            }
        }
        age++;
    }

    @Override
    public void display(GL gl, float time, com.novusradix.JavaPop.Client.Game g) {
    }
}
