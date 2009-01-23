package com.novusradix.JavaPop.Effects;

import com.novusradix.JavaPop.Direction;
import com.novusradix.JavaPop.Server.Game;
import com.novusradix.JavaPop.Tile;
import java.awt.Point;
import java.util.Arrays;
import java.util.Random;
import javax.media.opengl.GL;

/**
 *
 * @author gef
 */
public class EarthquakeEffect extends Effect {

    Point origin;
    byte[] path;
    transient Direction direction;
    transient int age = 0;
    transient Random r;

    public EarthquakeEffect(Point o, Direction d) {
        origin = o;
        direction = d;
        age = 0;
        r = new Random();
        path = new byte[r.nextInt(10) + 10];
        Arrays.fill(path, (byte) -1);

    }

    @Override
    public void execute(Game g) {
        age++;
        int px, py;
        Direction d = direction;
        px = origin.x;
        py = origin.y;
        int n;
        for (n = 0; n < path.length; n++) {
            g.heightMap.setTile(px, py, Tile.EARTHQUAKE);
            if (path[n] == (byte) -1) {
                //end of earthquake
                if (age % 2 == 0) {
                    int pLeft = 1, pStraight = 3, pRight = 1;
                    int turn;
                    if (direction == d) {
                        pStraight += 2;
                    }
                    if (direction.ordinal() == (d.ordinal() - 1) % 4) {
                        pLeft += 2;
                    }
                    if (direction.ordinal() == (d.ordinal() + 1) % 4) {
                        pRight += 2;
                    }
                    int rand = r.nextInt(pLeft + pStraight + pRight);
                    if (rand < pLeft) {
                        turn = -1;
                    } else if (rand < pLeft + pStraight) {
                        turn = 0;
                    } else {
                        turn = 1;
                    }
                    d = Direction.directions[((d.ordinal() + turn) + 4) % 4];
                    path[n] = (byte) d.ordinal();
                    px += d.dx;
                    py += d.dy;
                    if (!g.heightMap.tileInBounds(px, py)) {
                        g.heightMap.setTile(px, py, Tile.EARTHQUAKE);
                    }
                    g.addEffect(this);
                }
                break;
            } else {
                //not at end of eatrhquake
                d = Direction.directions[path[n]];
                px += d.dx;
                py += d.dy;
                if (!g.heightMap.tileInBounds(px, py)) {
                    g.deleteEffect(this);
                }
            }
        }
        if (n == path.length - 1) {
            g.deleteEffect(this);
        }


    }

    @Override
    public void display(GL gl, float time, com.novusradix.JavaPop.Client.Game g) {
        Direction d = null, newd;
        int px, py;
        px = origin.x;
        py = origin.y;
        for (int n = 0; n < path.length; n++) {
            if (path[n] == (byte) -1) {
                newd = null;
            } else {
                newd = Direction.directions[path[n]];
            }
            if (g.heightMap.tileInBounds(px, py)) {
                if (g.heightMap.isFlat(px, py)) {
                    Direction in = null, out = null;
                    if (d != null && (!g.heightMap.inBounds(px - d.dx, py - d.dy) || g.heightMap.isFlat(px - d.dx, py - d.dy))) {
                        in = d;
                    }
                    if (newd != null && (!g.heightMap.inBounds(px + newd.dx, py + newd.dy) || g.heightMap.isFlat(px + newd.dx, py + newd.dy))) {
                        out = newd;
                    }
                    g.heightMap.earthquakeRenderer.addTile(px, py, in, out);
                }
                if (newd == null) {
                    break;
                }
            }
            px += newd.dx;
            py += newd.dy;
            d = newd;

        }
    }
}
