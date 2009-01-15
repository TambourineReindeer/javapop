package com.novusradix.JavaPop.Client;

import java.awt.Point;
import java.util.Map;

/**
 *
 * @author gef
 */
public interface AbstractHouses {

    public void setLeaders(Map<Integer, Integer> leaderHouses);
    
    public void updateHouse(int id, Point pos, Player p, int level);

}