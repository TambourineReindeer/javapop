package com.novusradix.JavaPop.Effects;

import com.novusradix.JavaPop.Math.Helpers;
import com.novusradix.JavaPop.Server.Game;
import com.novusradix.JavaPop.Tile;
import java.awt.Point;
import javax.media.opengl.GL;

/**
 *
 * @author gef
 */
public class VolcanoEffect extends Effect {

    Point target;
    int age;

    public VolcanoEffect(Point p) {
        target = p;
        age = 0;
    }

    @Override
    public void execute(Game g) {
        if (age < 15) {
            if (age % 2 == 0) {
                g.heightMap.up(target.x, target.y);
                byte h = g.heightMap.getHeight(target.x, target.y);
                byte radius = (byte) (age / 2);
                int px, py;
                for (Point p : Helpers.smallRings[radius]) {
                    px = p.x + target.x;
                    py = p.y + target.y;
                    g.heightMap.setTile(px, py, Tile.BASALT);
                }
            }
        }
        if (age == 20) {
            g.heightMap.down(target.x, target.y);
            int px, py;
            for (Point p : Helpers.smallRings[0]) {
                px = p.x + target.x;
                py = p.y + target.y;
                g.heightMap.setTile(px, py, Tile.LAVA);
            }
            g.addEffect(new LavaTrailEffect(target, LavaTrailEffect.Direction.NORTH));
            g.addEffect(new LavaTrailEffect(new Point(target.x - 1, target.y), LavaTrailEffect.Direction.WEST));
            g.addEffect(new LavaTrailEffect(new Point(target.x, target.y - 1), LavaTrailEffect.Direction.EAST));
            g.addEffect(new LavaTrailEffect(new Point(target.x - 1, target.y - 1), LavaTrailEffect.Direction.SOUTH));
            g.deleteEffect(this);
        }
        age++;
    }

    @Override
    public void display(GL gl, float time, com.novusradix.JavaPop.Client.Game g) {
    }
}
