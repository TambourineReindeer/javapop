package com.novusradix.JavaPop.Effects;

import com.novusradix.JavaPop.Client.Game;
import com.novusradix.JavaPop.Server.ServerGame;
import com.novusradix.JavaPop.Tile;
import javax.media.opengl.GL;

/**
 *
 * @author gef
 */
public class FungusEffect extends Effect {

    transient byte[][] map;
    transient byte[][] newmap;
    transient ServerGame game;

    public FungusEffect(ServerGame g) {
        game = g;
        map = new byte[g.heightMap.width + 1][g.heightMap.breadth + 1];
        newmap = new byte[g.heightMap.width + 1][g.heightMap.breadth + 1];
    }

    public void addFungus(int x, int y) {
        if (game.heightMap.getTile(x, y).isFertile) {
            map[x + 1][y + 1] = 1;
            game.heightMap.setTile(x, y, Tile.FUNGUS);
        }
    }

    @Override
    public void execute(ServerGame g) {
        byte[][] temp;
        int count;
        Tile t;
        for (int x = 1; x < g.heightMap.width; x++) {
            for (int y = 1; y < g.heightMap.breadth; y++) {
                t = game.heightMap.getTile(x - 1, y - 1);
                if (t.isFertile || t == Tile.FUNGUS) {
                    count = map[x][y] + map[x - 1][y] + map[x + 1][y] + map[x][y - 1] + map[x][y + 1];
                    newmap[x][y] = (byte) ((count > 0 ) ? 1 : 0);
                } else {
                    newmap[x][y] = 0;
                }
                if (newmap[x][y] != map[x][y]) {
                    if (newmap[x][y] == 1) {
                        game.heightMap.setTile(x - 1, y - 1, Tile.FUNGUS);
                    } else {
                        game.heightMap.clearTile(x - 1, y - 1);
                    }
                }
            }
        }

        temp = newmap;
        newmap = map;
        map = temp;
    }

    @Override
    public void display(GL gl, float time, Game g) {
    }
}
