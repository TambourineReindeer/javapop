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
public class SwampEffect extends Effect {

    Point target;

    public SwampEffect(Point target) {
        this.target = target;
    }

    @Override
    public void execute(Game g) {
        Random r = new Random();
        int n = r.nextInt(5) + 1;
        int px, py;
        for (int m = 0; m < n; m++) {
            px = target.x + r.nextInt(7) - 3;
            py = target.y + r.nextInt(7) - 3;
            if (g.heightMap.tileInBounds(px, py)) {
                if (g.heightMap.getTile(px, py).isFertile) {
                    g.heightMap.setTile(px, py, Tile.SWAMP);
                }
            }
        }
        g.deleteEffect(this);
    }

    @Override
    public void display(GL gl, float time, com.novusradix.JavaPop.Client.Game g) {
    }
}
