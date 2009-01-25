package com.novusradix.JavaPop.Effects;

import com.novusradix.JavaPop.Direction;
import com.novusradix.JavaPop.Server.ServerGame;
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

    

    public LavaTrailEffect(Point o, Direction d) {
        origin = o;
        direction = d;
        length = 1;
        age = 0;
    }

    @Override
    public void execute(ServerGame g) {
        age++;
        if (g.heightMap.isFlat(origin.x, origin.y)) {
            boolean blocked = false;
            int newLength = 0;
            for (int n = 0; n < length; n++) {
                int px = origin.x + direction.dx * n;
                int py = origin.y + direction.dy * n;
                if (g.heightMap.tileInBounds(px, py)) {
                    if (blocked) {
                        if (g.heightMap.getTile(px, py) == Tile.LAVA) {
                            g.heightMap.setTile(px, py, Tile.BASALT);
                        }
                    } else {
                        int ha, hb;
                        if (g.heightMap.isSeaLevel(px, py)) {
                            if(!blocked && g.heightMap.getTile(px, py)==Tile.SEA)
                                g.addEffect(new BasaltEffect(new Point(px,py), direction));
                            blocked = true;
                            newLength = n;
                            
                        //Start a basalt effect
                        } else {
                            ha = g.heightMap.getHeight(px + direction.frontx1, py + direction.fronty1);
                            hb = g.heightMap.getHeight(px + direction.frontx2, py + direction.fronty2);

                            if (ha != hb) {
                                blocked = true;
                                newLength = n;
                                if (g.heightMap.getTile(px, py) == Tile.LAVA) {
                                    g.heightMap.setTile(px, py, Tile.BASALT);
                                }
                            } else {
                                g.heightMap.setTile(px, py, Tile.LAVA);
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
            int px, py;
            for (int n = 0; n < length; n++) {
                px = origin.x + direction.dx * n;
                py = origin.y + direction.dy * n;
                if (g.heightMap.tileInBounds(px, py)) {
                    if (g.heightMap.getTile(px, py) == Tile.LAVA) {
                        g.heightMap.setTile(px, py, Tile.BASALT);
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
