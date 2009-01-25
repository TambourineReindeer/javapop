package com.novusradix.JavaPop.Client;

import java.util.Map;

/**
 *
 * @author gef
 */
public interface AbstractHouses {

    public void setLeaders(Map<Integer, Integer> leaderHouses);

    public void step(float seconds);
    
    public void updateHouse(int id, int x, int y, Player p, int level, float strength);

}