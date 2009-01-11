package com.novusradix.JavaPop.Server.Effects;

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
        this.target  = target;
    }

    @Override
    public void execute(Game g) {
        
        Random r = new Random();
        int n = r.nextInt(5) + 1;
        for (int m = 0; m < n; m++) {
            Point p = new Point(target.x + r.nextInt(7) - 3, target.y + r.nextInt(7) - 3);
            if (g.heightMap.tileInBounds(p)) {
                if (g.heightMap.getTile(p).isFertile) {
                    g.heightMap.setTile(p, Tile.SWAMP);
                }
            }

        }
        g.deleteEffect(this);
    }

    @Override
    public void display(GL gl, float time, com.novusradix.JavaPop.Client.Game g) {
            }
}
