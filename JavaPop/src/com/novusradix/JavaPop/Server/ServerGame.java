package com.novusradix.JavaPop.Server;

import com.novusradix.JavaPop.Math.Helpers;
import com.novusradix.JavaPop.Messaging.EffectUpdate;
import com.novusradix.JavaPop.Messaging.Lobby.GameOver;
import com.novusradix.JavaPop.Messaging.Lobby.GameStarted;
import com.novusradix.JavaPop.Messaging.HeightMapUpdate;
import com.novusradix.JavaPop.Messaging.Lobby.JoinedGame;
import com.novusradix.JavaPop.Messaging.Message;
import com.novusradix.JavaPop.Messaging.PlayerUpdate;
import com.novusradix.JavaPop.Effects.Effect;
import com.novusradix.JavaPop.Messaging.RockUpdate;
import java.awt.Dimension;
import java.awt.Point;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gef
 */
public class ServerGame extends TimerTask {

    public volatile static int nextId = 0;
    private int id;
    public final Vector<ServerPlayer> players;
    private ServerPlayer owner;
    private final Server server;
    private Timer timer;
    private float seconds;
    public HeightMap heightMap;
    public ServerPeons peons;
    public ServerHouses houses;
    private int humancount;
    private final Map<Integer, Effect> effects;
    private final Map<Integer, Effect> newEffects;
    private final Collection<Integer> deletedEffects;
    public final Map<ServerPlayer, Map<Class,Effect>> persistentEffects;
    
    private long frame;

    public ServerGame(ServerPlayer owner) {
        this.owner = owner;
        server = owner.s;
        players = new Vector<ServerPlayer>();
        owner.currentGame = this;
        id = nextId++;
        humancount = 0;
        addPlayer(owner);
        effects = new HashMap<Integer, Effect>();
        newEffects = new HashMap<Integer, Effect>();
        deletedEffects = new HashSet<Integer>();
        persistentEffects = new HashMap<ServerPlayer, Map<Class, Effect>>();
    }

    public int getId() {
        return id;
    }

    public void addPlayer(ServerPlayer p) {
        synchronized (players) {
            players.add(p);
            p.currentGame = this;
            p.sendMessage(new JoinedGame(this));
            if (p.human) {
                humancount++;
            }
        }
    }

    public void removePlayer(ServerPlayer p) {
        synchronized (players) {
            players.remove(p);
            p.currentGame = null;
            if (p.human) {
                humancount--;
                if (humancount == 0) {
                    sendAllPlayers(new GameOver());
                }
            }

        }
    //TODO: send a message?
    }

    public void PlayerReady(ServerPlayer p) {
        p.ready = true;
        for (ServerPlayer pl : players) {
            if (!pl.ready) {
                return;
            }
        }
        startGame();
    }

    public void startGame() {
        heightMap = new HeightMap(new Dimension(128, 128));
        Random r = new Random();

        heightMap.randomize(r.nextInt());
        peons = new ServerPeons(this);
        houses = new ServerHouses(this);

        if (players.size() == 1) {
            //Add an AI player
            new com.novusradix.JavaPop.Client.AI.Client(server.port, id);
        }

        try {
            while (players.size() == 1) {
                //wait for AI Player to join
                Thread.sleep(500);
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(ServerGame.class.getName()).log(Level.SEVERE, null, ex);
        }

        
        Map<ServerPlayer, Point> startingPosition = new HashMap<ServerPlayer, Point>();
        for (ServerPlayer p : players) {
            startingPosition.put(p, new Point(r.nextInt(heightMap.getWidth()), r.nextInt(heightMap.getBreadth())));
            p.setPapalMagnet(heightMap.width / 2, heightMap.breadth / 2);
            persistentEffects.put(p, new HashMap<Class, Effect>());
        }
        
        //A universal effect bucket, not tied to players:
        persistentEffects.put(null, new HashMap<Class, Effect>());


        int numPeons = 3;
        nextPlayer:
        for (Map.Entry<ServerPlayer, Point> me : startingPosition.entrySet()) {
            int placed = 0;
            for (ArrayList<Point> ring : Helpers.shuffledRings) {
                for (Point p : ring) {
                    Point p2 = new Point(p.x + me.getValue().x, p.y + me.getValue().y);
                    if (heightMap.tileInBounds(p2.x, p2.y) && heightMap.getHeight(p2.x, p2.y) > 0) {
                        placed++;
                        peons.addPeon(p2.x, p2.y, 200, me.getKey(), true, false);
                        if (placed == numPeons) {
                            continue nextPlayer;
                        }
                    }
                }
            }
        }
        GameStarted go = new GameStarted(this);
        server.sendAllPlayers(go);
        HeightMapUpdate m = heightMap.GetUpdate();
        if (m != null) {
            sendAllPlayers(m);
        }
        timer = new Timer("Game " + id);
        seconds = 1.0f / 10.0f;
        timer.scheduleAtFixedRate(this, 0, (int) (seconds * 1000.0f));
    }

    public void run() {
        //start a clock,
        //move people.

        if (players.isEmpty()) {
            timer.cancel();
        }
        HeightMapUpdate m;
        peons.step(seconds);
        houses.step(seconds);
        m = heightMap.GetUpdate();
        if (m != null) {
            sendAllPlayers(m);
        }
        
        RockUpdate ru;
        ru= heightMap.updateRocks(seconds);
        if(ru!=null)
            sendAllPlayers(ru);
        
        for (Effect e : effects.values()) {
            e.execute(this);
        }
        synchronized (effects) {
            effects.putAll(newEffects);
            effects.keySet().removeAll(deletedEffects);
            if (newEffects.size() + deletedEffects.size() > 0) {
                EffectUpdate eu = new EffectUpdate(newEffects, deletedEffects);
                sendAllPlayers(eu);
                newEffects.clear();
                deletedEffects.clear();
            }
        }
        if (frame % 8 == 0) {
            sendAllPlayers(new PlayerUpdate(players));
        }
        frame++;
    }

    public void sendAllPlayers(Message m) {
        ObjectOutputStream o = null;
        try {
            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            o = new ObjectOutputStream(bs);
            o.flush();
            int start = bs.size();
            //bs.reset();

            o.writeObject(m);
            byte[] bytes = new byte[bs.size() - start];

            System.arraycopy(bs.toByteArray(), start, bytes, 0, bytes.length);
            synchronized (players) {
                for (ServerPlayer pl : players) {
                    pl.sendMessage(bytes);
                }
            }
        //System.out.println(System.currentTimeMillis() + ", " + m.getClass().getName() + ", " + bytes.length);

        } catch (IOException ex) {
            Logger.getLogger(ServerGame.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                o.close();
            } catch (IOException ex) {
                Logger.getLogger(ServerGame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void addEffect(Effect e) {
        synchronized (effects) {
            newEffects.put(e.id, e);
        }
    }

    public void deleteEffect(Effect e) {
        synchronized (effects) {
            deletedEffects.add(e.id);

        }
    }
}
