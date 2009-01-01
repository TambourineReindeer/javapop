package com.novusradix.JavaPop.AI;

import com.novusradix.JavaPop.Client.*;
import com.novusradix.JavaPop.Math.MultiMap;
import com.novusradix.JavaPop.Math.SortedMultiMap;
import java.awt.Point;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Houses implements AbstractHouses {

    private Game game;
    private int[][] map;
    private Map<Integer, House> houses;
    private MultiMap<Player, House> playerHouses;
    private static final int TEAMS = 4;
    private static final int EMPTY = 0;
    private static final int FARM = EMPTY + 1;
    private static final int HOUSE = FARM + TEAMS;
    private static final int NEXT = HOUSE + TEAMS;
    private SortedMultiMap<Integer, House> xmap,  ymap;

    public Houses(Game g) {
        game = g;
        map = new int[game.heightMap.getWidth()][game.heightMap.getBreadth()];
        houses = new HashMap<Integer, House>();
        playerHouses = new MultiMap<Player, House>();
        xmap = new SortedMultiMap<Integer, House>();
        ymap = new SortedMultiMap<Integer, House>();
    }

    public Collection<House> getHouses(Player p) {
        return playerHouses.get(p);
    }

    public void updateHouse(int id, Point pos, Player p, int level) {
        synchronized (houses) {
            if (level < 0) {
                //remove
                if (houses.containsKey(id)) {
                    House h = houses.get(id);
                    playerHouses.remove(p, h);
                    xmap.remove(h.pos.x, h);
                    ymap.remove(h.pos.y, h);
                    houses.remove(id);
                }

            } else {
                if (houses.containsKey(id)) {
                    houses.get(id).level = level;
                } else {
                    House h = new House(pos, p, level);
                    houses.put(id, h);
                    playerHouses.put(p, h);
                    xmap.put(h.pos.x, h);
                    ymap.put(h.pos.y, h);
                }
            }
        }
    }

   
    public boolean canBuild(Point p) {
        if (game.heightMap.tileInBounds(p)) {
            return (map[p.x][p.y] == EMPTY && game.heightMap.getHeight(p) > 0 && game.heightMap.isFlat(p));

        }
        return false;
    }

    public class House {

        public Point pos;
        public int level;
        public Player player;

        public House(Point p, Player player, int level) {
            map[p.x][p.y] = HOUSE;
            pos = (Point) p.clone();
            this.level = level;
            this.player = player;
        }
    }
}
