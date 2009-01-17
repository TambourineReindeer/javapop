/*
 * This class is the container for all data required by an active game instance.
 * 
 */
package com.novusradix.JavaPop.Client;

import com.novusradix.JavaPop.Client.Tools.AchillesTool;
import com.novusradix.JavaPop.Client.Tools.AdonisTool;
import com.novusradix.JavaPop.Client.Tools.ArmageddonTool;
import com.novusradix.JavaPop.Client.Tools.BaptismalFontsTool;
import com.novusradix.JavaPop.Client.Tools.BasaltTool;
import com.novusradix.JavaPop.Client.Tools.Tool;
import com.novusradix.JavaPop.Client.Tools.BatholithTool;
import com.novusradix.JavaPop.Client.Tools.EarthquakeTool;
import com.novusradix.JavaPop.Client.Tools.FireColumnTool;
import com.novusradix.JavaPop.Client.Tools.FireRainTool;
import com.novusradix.JavaPop.Client.Tools.ForestTool;
import com.novusradix.JavaPop.Client.Tools.FungusTool;
import com.novusradix.JavaPop.Client.Tools.HelenOfTroyTool;
import com.novusradix.JavaPop.Client.Tools.HeraclesTool;
import com.novusradix.JavaPop.Client.Tools.Hurricane;
import com.novusradix.JavaPop.Client.Tools.LightningTool;
import com.novusradix.JavaPop.Client.Tools.MoveAnkhTool;
import com.novusradix.JavaPop.Client.Tools.OdysseusTool;
import com.novusradix.JavaPop.Client.Tools.PerseusTool;
import com.novusradix.JavaPop.Client.Tools.PlagueTool;
import com.novusradix.JavaPop.Client.Tools.RaiseLowerTool;
import com.novusradix.JavaPop.Client.Tools.RenewTool;
import com.novusradix.JavaPop.Client.Tools.RoadsTool;
import com.novusradix.JavaPop.Client.Tools.StormTool;
import com.novusradix.JavaPop.Client.Tools.SwampTool;
import com.novusradix.JavaPop.Client.Tools.TidalWaveTool;
import com.novusradix.JavaPop.Client.Tools.ToolGroup;
import com.novusradix.JavaPop.Client.Tools.VolcanoTool;
import com.novusradix.JavaPop.Client.Tools.WallsTool;
import com.novusradix.JavaPop.Client.Tools.WhirlpoolTool;
import com.novusradix.JavaPop.Client.Tools.WhirlwindTool;
import com.novusradix.JavaPop.Messaging.Lobby.GameStarted;
import com.novusradix.JavaPop.Effects.Effect;
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

    public void lookAt(Point p) {
        if (frame != null) {
            frame.mc.lookAt(p);
        }
    }
}
