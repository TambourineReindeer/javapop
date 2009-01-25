package com.novusradix.JavaPop.Client.AI;

import com.novusradix.JavaPop.Client.*;
import com.novusradix.JavaPop.Math.MultiMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Houses implements AbstractHouses {

    private Game game;
    private int[][] map;
    private final Map<Integer, House> houses;
    private MultiMap<com.novusradix.JavaPop.Player, House> playerHouses;
    private static final int TEAMS = 4;
    private static final int EMPTY = 0;
    private static final int FARM = EMPTY + 1;
    private static final int HOUSE = FARM + TEAMS;
    private static final int NEXT = HOUSE + TEAMS;

    public Houses(Game g) {
        game = g;
        map = new int[game.heightMap.getWidth()][game.heightMap.getBreadth()];
        houses = new HashMap<Integer, House>();
        playerHouses = new MultiMap<com.novusradix.JavaPop.Player, House>();
    }

    public Collection<House> getHouses(Player p) {
        synchronized (houses) {
            return playerHouses.get(p);
        }
    }

    public void updateHouse(int id, int x, int y, Player p, int level, float strength) {
        synchronized (houses) {
            if (level < 0) {
                //remove
                if (houses.containsKey(id)) {
                    House h = houses.get(id);
                    playerHouses.remove(p, h);
                    houses.remove(id);
                }
            } else {
                if (houses.containsKey(id)) {
                    House h = houses.get(id);
                    h.level = level;
                    if (h.player != p) {
                        playerHouses.remove(h.player, h);
                        h.player = p;
                        playerHouses.put(p, h);
                    }
                } else {
                    House h = new House(x,y, p, level);
                    houses.put(id, h);
                    playerHouses.put(p, h);
                }
            }
        }
    }

    public boolean canBuild(int x, int y) {
        if (game.heightMap.tileInBounds(x,y)) {
            return (map[x][y] == EMPTY && game.heightMap.getHeight(x,y) > 0 && game.heightMap.isFlat(x,y));
        }
        return false;
    }

    public void step(float seconds) {
        
    }

    public class House extends com.novusradix.JavaPop.House{

        public final int x;
        public final int y;

        public House(int px, int py, Player player, int level) {
            x=px;y=py;
            map[x][y] = HOUSE;
            this.level = level;
            this.player = player;
        }
    }

    public void setLeaders(Map<Integer, Integer> leaderHouses) {
       //Todo
    }
}
