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
                g.heightMap.up(target);
                byte h = g.heightMap.getHeight(target);
                byte radius = (byte) (age / 2);
                for (Point p : Helpers.smallRings[radius]) {
                    Point p2 = new Point(p.x + target.x, p.y + target.y);
                    g.heightMap.setTile(p2, Tile.BASALT);
                }
            }
        }
        if (age == 20) {
            g.heightMap.down(target);
            for (Point p : Helpers.smallRings[0]) {
                Point p2 = new Point(p.x + target.x, p.y + target.y);
                g.heightMap.setTile(p2, Tile.LAVA);
            }
            g.addEffect(new LavaTrailEffect(target, LavaTrailEffect.Direction.NORTH));
            g.addEffect(new LavaTrailEffect(new Point(target.x-1, target.y), LavaTrailEffect.Direction.WEST));
            g.addEffect(new LavaTrailEffect(new Point(target.x, target.y-1), LavaTrailEffect.Direction.EAST));
            g.addEffect(new LavaTrailEffect(new Point(target.x-1, target.y-1), LavaTrailEffect.Direction.SOUTH));
            g.deleteEffect(this);
        }

        age++;
    }

    @Override
    public void display(GL gl, float time, com.novusradix.JavaPop.Client.Game g) {
    }
}
