/*
 * This class is the container for all data required by an active game instance.
 * 
 */
package com.novusradix.JavaPop.Client;

import com.novusradix.JavaPop.Client.Tools.EarthquakeTool;
import com.novusradix.JavaPop.Client.Tools.LightningTool;
import com.novusradix.JavaPop.Client.Tools.MoveAnkhTool;
import com.novusradix.JavaPop.Client.Tools.RaiseLowerTool;
import com.novusradix.JavaPop.Client.Tools.TidalWaveTool;
import com.novusradix.JavaPop.Client.Tools.ToolGroup;
import com.novusradix.JavaPop.Client.Tools.VolcanoTool;
import com.novusradix.JavaPop.Messaging.Lobby.GameStarted;
import com.novusradix.JavaPop.Server.Effects.Effect;
import com.novusradix.JavaPop.Server.Player.Info;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author gef
 */
public class Game extends TimerTask {

    public com.novusradix.JavaPop.HeightMap heightMap;
    public Client client;
    public Peons peons;
    public AbstractHouses houses;
    private Timer timer;
    private float seconds;
    public Map<Integer, Player> players;
    public Player me;
    GameFrame frame;
    Collection<GLObject> objects;
    public Map<Integer, Effect> effects;
    public Collection<ToolGroup> toolGroups;
    
    protected Game() {
    }

    public Game(GameStarted g, Client c) {
        client = c;
        objects = new ArrayList<GLObject>();
        HeightMap hm1 = new HeightMap(g.gi.mapSize);
        heightMap = hm1;
        objects.add(hm1);
        Peons p1 = new Peons(this);
        peons = p1;
        objects.add(p1);
        Houses h1 = new Houses(this);
        houses = h1;
        objects.add(h1);

        effects = new HashMap<Integer, Effect>();
        players = new HashMap<Integer, Player>();
        int index = 0;
        for (Info i : g.gi.players.values()) {
            Player p = new Player(i, this, index++);
            players.put(i.id, p);
            objects.add(p);
            if (i.id == c.info.id) {
                me = p;
            }
        }
        toolGroups = new ArrayList<ToolGroup>();
        ToolGroup people = new ToolGroup("/com/novusradix/JavaPop/icons/People.png", new Point(50, -175));
        toolGroups.add(people);
        people.addTool(new RaiseLowerTool(people, client));
        people.addTool(new MoveAnkhTool(people, client));
        
        ToolGroup veg = new ToolGroup("/com/novusradix/JavaPop/icons/Vegetation.png", new Point(100, -150));
        toolGroups.add(veg);
        
        ToolGroup earth = new ToolGroup("/com/novusradix/JavaPop/icons/Earth.png", new Point(150, -125));
        toolGroups.add(earth);
        earth.addTool(new EarthquakeTool(earth, client));
        
        ToolGroup air = new ToolGroup("/com/novusradix/JavaPop/icons/Air.png", new Point(200, -100));
        toolGroups.add(air);
        air.addTool(new LightningTool(air, client));
        
        ToolGroup fire = new ToolGroup("/com/novusradix/JavaPop/icons/Fire.png", new Point(250, -75));
        toolGroups.add(fire);
        fire.addTool(new VolcanoTool(fire, client));
        
        ToolGroup water = new ToolGroup("/com/novusradix/JavaPop/icons/Water.png", new Point(300, -50));
        toolGroups.add(water);
        water.addTool(new TidalWaveTool(water, client));

        startTimer();
        frame = new GameFrame(this);
    }

    public void startTimer() {
        timer = new Timer("Client Game Animator");
        seconds = 1.0f / 20.0f;
        timer.scheduleAtFixedRate(this, 0, (int) (seconds * 1000.0f));
    }

    public void kill() {
        timer.cancel();
        timer = null;
    }

    public void run() {
        //start a clock,
        //move people.
        peons.step(seconds);
    //houses.step(seconds);
    }
}
