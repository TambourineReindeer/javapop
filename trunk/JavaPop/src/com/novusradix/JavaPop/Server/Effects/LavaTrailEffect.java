package com.novusradix.JavaPop.Server.Effects;

import com.novusradix.JavaPop.Server.Game;
import com.novusradix.JavaPop.Tile;
import java.awt.Point;
import javax.media.opengl.GL;

/**
 *
 * @author gef
 */
public class LavaTrailEffect extends Effect {

    Point origin;
    Direction direction;
    int length;
    int age = 0;

    public enum Direction {

        NORTH(0, 1, 0, 1, 1, 1), SOUTH(0, -1, 0, 0, 1, 0), EAST(1, 0, 1, 0, 1, 1), WEST(-1, 0, 0, 0, 0, 1);
        int x, y, fx1, fx2, fy1, fy2;

        Direction(int x, int y, int fx1, int fy1, int fx2, int fy2) {
            this.x = x;
            this.y = y;
            this.fx1 = fx1;
            this.fy1 = fy1;
            this.fx2 = fx2;
            this.fy2 = fy2;
        }
    }

    public LavaTrailEffect(Point o, Direction d) {
        origin = o;
        direction = d;
        length = 1;
        age = 0;
    }

    @Override
    public void execute(Game g) {
        age++;
        if (g.heightMap.isFlat(origin)) {
            boolean blocked = false;
            int newLength = 0;
            for (int n = 0; n < length; n++) {
                Point p = new Point(origin.x + direction.x * n, origin.y + direction.y * n);
                if (g.heightMap.tileInBounds(p)) {
                    if (blocked) {
                        if (g.heightMap.getTile(p) == Tile.LAVA) {
                            g.heightMap.setTile(p, Tile.BASALT);
                        }
                    } else {
                        int ha, hb;
                        if (g.heightMap.isSeaLevel(p)) {
                            blocked = true;
                            newLength = n;
                        //Start a basalt effect
                        } else {
                            ha = g.heightMap.getHeight(new Point(p.x + direction.fx1, p.y + direction.fy1));
                            hb = g.heightMap.getHeight(new Point(p.x + direction.fx2, p.y + direction.fy2));

                            if (ha != hb) {
                                blocked = true;
                                newLength = n;
                                if (g.heightMap.getTile(p) == Tile.LAVA) {
                                    g.heightMap.setTile(p, Tile.BASALT);
                                }
                            } else {
                                g.heightMap.setTile(p, Tile.LAVA);
                            }
                        }
                    }
                }
            }
            if (!blocked) {
                if (age % 10 == 0) {
                    length++;
                }
            } else {
                length = newLength;
            }
        } else {
            for (int n = 0; n < length; n++) {
                Point p = new Point(origin.x + direction.x * n, origin.y + direction.y * n);
                if (g.heightMap.tileInBounds(p)) {
                    if (g.heightMap.getTile(p) == Tile.LAVA) {
                        g.heightMap.setTile(p, Tile.BASALT);
                    }
                }
                g.deleteEffect(this);
            }
        }

    }

    @Override
    public void display(GL gl, float time, com.novusradix.JavaPop.Client.Game g) {
    }
}
