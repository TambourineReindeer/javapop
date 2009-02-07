/*
 * This class is the container for all data required by an active game instance.
 * 
 */
package com.novusradix.JavaPop.Client;

import com.novusradix.JavaPop.Client.Tools.*;
import com.novusradix.JavaPop.Messaging.Lobby.GameStarted;
import com.novusradix.JavaPop.Effects.Effect;
import com.novusradix.JavaPop.Messaging.PlayerUpdate.Info;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author gef
 */
public class Game {

    public com.novusradix.JavaPop.Client.HeightMap heightMap;
    public Client client;
    public Peons peons;
    public AbstractHouses houses;

    public Map<Integer, Player> players;
    public Player me;
    GameFrame frame;
    Collection<GLObject> objects;
    Collection<GLObject> transparentObjects;
    public Map<Integer, Effect> effects;
    public Collection<ToolGroup> toolGroups;
    public ModelFactory modelFactory;

    protected Game() {
    }

    public Game(GameStarted g, Client c) {
        client = c;
        objects = new ArrayList<GLObject>();
        transparentObjects = new ArrayList<GLObject>();
        modelFactory = new ModelFactory();

        HeightMap hm1 = new HeightMap(g.gi.mapSize, this);
        heightMap = hm1;
        transparentObjects.add(hm1);

        peons = new Peons(this);
        objects.add(peons);

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
            if (i.id == c.getPlayerID()) {
                me = p;
            }
        }
        //Todo: receive these from the server
        toolGroups = new ArrayList<ToolGroup>();
        ToolGroup people = new ToolGroup("/com/novusradix/JavaPop/icons/People.png");
        toolGroups.add(people);
        people.addTool(new RaiseLowerTool(people, client));
        people.addTool(new MoveAnkhTool(people, client));
        people.addTool(new PerseusTool(people, client));
        people.addTool(new PlagueTool(people, client));
        people.addTool(new ArmageddonTool(people, client));

        ToolGroup veg = new ToolGroup("/com/novusradix/JavaPop/icons/Vegetation.png");
        toolGroups.add(veg);
        veg.addTool(new ForestTool(veg, client));
        veg.addTool(new RenewTool(veg, client));
        veg.addTool(new SwampTool(veg, client));
        veg.addTool(new FungusTool(veg, client));
        veg.addTool(new AdonisTool(veg, client));

        ToolGroup earth = new ToolGroup("/com/novusradix/JavaPop/icons/Earth.png");
        toolGroups.add(earth);
        earth.addTool(new RoadsTool(earth, client));
        earth.addTool(new WallsTool(earth, client));
        earth.addTool(new EarthquakeTool(earth, client));
        earth.addTool(new BatholithTool(earth, client));
        earth.addTool(new HeraclesTool(earth, client));

        ToolGroup air = new ToolGroup("/com/novusradix/JavaPop/icons/Air.png");
        toolGroups.add(air);
        air.addTool(new LightningTool(air, client));
        air.addTool(new WhirlwindTool(air, client));
        air.addTool(new StormTool(air, client));
        air.addTool(new Hurricane(air, client));
        air.addTool(new OdysseusTool(air, client));

        ToolGroup fire = new ToolGroup("/com/novusradix/JavaPop/icons/Fire.png");
        toolGroups.add(fire);
        fire.addTool(new FireColumnTool(fire, client));
        fire.addTool(new FireRainTool(fire, client));
        fire.addTool(new VolcanoTool(fire, client));
        fire.addTool(new AchillesTool(fire, client));

        ToolGroup water = new ToolGroup("/com/novusradix/JavaPop/icons/Water.png");
        toolGroups.add(water);
        water.addTool(new BasaltTool(water, client));
        water.addTool(new WhirlpoolTool(water, client));
        water.addTool(new BaptismalFontsTool(water, client));
        water.addTool(new TidalWaveTool(water, client));
        water.addTool(new HelenOfTroyTool(water, client));

        frame = new GameFrame(this);
    }

    public void step(float seconds) {
        //start a clock,
        //move people.
        peons.step(seconds);
        houses.step(seconds);
    }

    public void lookAt(Point p) {
        if (frame != null) {
            frame.mc.lookAt(p);
        }
    }

    public void kill()
    {

    }
}
