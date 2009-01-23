package com.novusradix.JavaPop.Server;

import com.novusradix.JavaPop.Math.Helpers;
import com.novusradix.JavaPop.Math.MultiMap;
import com.novusradix.JavaPop.Math.Vector2;
import com.novusradix.JavaPop.Messaging.PeonUpdate;
import com.novusradix.JavaPop.Server.Houses.House;
import com.novusradix.JavaPop.Tile;
import java.awt.Point;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import static java.lang.Math.*;

public class Peons {

    public Game game;

    public enum Action {

        DROWN, BURN, FALL, NONE;
    }

    public enum State {

        ALIVE, DEAD, SETTLED, WALKING, DROWNING, MERGING, WANDER, FALLING
    }
    private Vector<Peon> peons;
    private MultiMap<Point, Peon> map;
    private int nextId = 1;
    private Map<Player, Peon> leaders;

    public Peons(Game g) {
        game = g;
        peons = new Vector<Peon>();
        map = new MultiMap<Point, Peon>();
        leaders = new HashMap<Player, Peon>();
    }

    public Collection<Peon> getPeons(Point p) {
        return map.get(p);
    }

    public void addPeon(float x, float y, float strength, Player player, boolean leader) {
        Peon p = new Peon(x, y, strength, player);
        peons.add(p);
        map.put(p.getPoint(), p);
        if (leader) {
            leaders.put(player, p);
        }
    }

    public void addPeon(Point p, float strength, Player player, boolean leader) {
        addPeon(p.x + 0.5f, p.y + 0.5f, strength, player, leader);
    }

    public void step(float seconds) {
        if (peons != null) {
            Peon p;

            Vector<PeonUpdate.Detail> pds = new Vector<PeonUpdate.Detail>();
            PeonUpdate.Detail pd;

            for (Iterator<Peon> i = peons.iterator(); i.hasNext();) {

                p = i.next();

                pd = p.step(seconds);
                if (pd != null) {
                    pds.add(pd);
                }
                switch (p.state) {
                    case DEAD:
                        if (leaders.containsValue(p)) {
                            p.player.info.ankh.setLocation(p.getPoint());
                            leaders.remove(p.player);
                        }
                    case SETTLED:
                        i.remove();
                        map.remove(p.getPoint(), p);
                        break;
                }
            }
            if (pds.size() > 0) {
                game.sendAllPlayers(new PeonUpdate(pds, leaders));
                pds.clear();
            }
        }
    }

    public class Peon {

        public int id;
        public Vector2 pos;
        float strength;
        private int destx,  desty; // destination to walk to.
        private State state;
        private float dx,  dy;
        Player player;
        private float stateTimer;
        private int wanderCount;
        private Point p;

        public Point getPoint() {
            int fx, fy;
            fx = (int) floor(pos.x);
            fy = (int) floor(pos.y);
            if (p == null || fx != p.x || fy != p.y) {
                p = new Point(fx, fy);
            }
            return p;
        }

        public Peon(float x, float y, float strength, Player p) {
            id = nextId++;
            pos = new Vector2(x, y);
            this.strength = strength;
            state = State.ALIVE;
            player = p;
        }

        public void hurt(float i) {
            strength -= i;
            player.info.mana -= i;
        }

        private Point findEnemy() {
            Set<Player> enemies = new HashSet<Player>(game.players);
            enemies.remove(player);
            Point nearestPeon = nearestPeon(getPoint(), enemies, 8);
            Point nearestHouse = game.houses.nearestHouse(getPoint(), enemies);
            Point nearest;
            if (nearestHouse == null && nearestPeon == null) {
                return findFlatLand(getPoint());
            }
            if (nearestPeon == null) {
                nearest = nearestHouse;
            } else if (nearestHouse == null) {
                nearest = nearestPeon;
            } else {
                float dp, dh;
                dp = (pos.x - nearestPeon.x) * (pos.x - nearestPeon.x) + (pos.y - nearestPeon.y) * (pos.y - nearestPeon.y);
                dh = (pos.x - nearestHouse.x) * (pos.x - nearestHouse.x) + (pos.y - nearestHouse.y) * (pos.y - nearestHouse.y);
                if (dp < dh) {
                    nearest = nearestPeon;
                } else {
                    nearest = nearestHouse;
                }
            }
            if ((p.x - nearest.x) * (p.x - nearest.x) + (p.y - nearest.y) + (p.y - nearest.y) > 100) {
                return findFlatLand(getPoint());
            }
            return nearest;

        }

        private Point findFriend() {
            Set<Player> me = new HashSet<Player>();
            me.add(player);
            Point nearestPeon = nearestPeon(getPoint(), me, 8);
            if (nearestPeon == null) {
                return findFlatLand(getPoint());
            }
            return nearestPeon;
        }

        public Point nearestPeon(Point p, Set<Player> players, int searchRadius) {
            float d2 = game.heightMap.getWidth() * game.heightMap.getBreadth() + 1;
            Peon nearest = null;
            for (Peon peon : peons) {
                Point p2 = peon.getPoint();
                float nd2 = (p.x - p2.x) * (p.x - p2.x) + (p.y - p2.y) * (p.y - p2.y);
                if (nd2 > 0 && nd2 < d2) {
                    if (players.contains(peon.player)) {
                        nearest = peon;
                        d2 = nd2;
                    }
                }
            }
            if (nearest != null) {
                return nearest.getPoint();
            }
            return null;
        }

        private PeonUpdate.Detail changeState(State s) {
            state = s;
            stateTimer = 0;
            return new PeonUpdate.Detail(id, state, pos.x, pos.y, destx, desty, dx, dy, player.getId());
        }

        private PeonUpdate.Detail step(float seconds) {
            Point oldPos = getPoint();
            strength -= seconds;
            player.info.mana -= seconds;
            stateTimer += seconds;

            if (strength < 0) {
                player.info.mana -= this.strength;
                return changeState(State.DEAD);
            }

            {
                State newstate = state;
                switch (game.heightMap.getTile(oldPos.x, oldPos.y).action) {
                    case DROWN:
                        newstate = State.DROWNING;
                        break;
                    case BURN:
                        newstate = State.DEAD;
                        break;
                    case FALL:
                        newstate = State.FALLING;
                        break;
                    default:
                        if (map.size(oldPos) > 1) {
                            newstate = State.MERGING;
                        }
                        break;
                }

                if (state != newstate) {
                    return changeState(newstate);
                }
            }



            switch (state) {
                case WALKING:
                    if (reachedDest() || stateTimer > 2.0f) {
                        //Yay we're here! Or we're taking a breather.
                        return changeState(State.ALIVE);
                    }
                    float newx,
                     newy;
                    newx = pos.x + seconds * dx;
                    newy = pos.y + seconds * dy;
                    Point newPos = new Point((int) floor(newx), (int) floor(newy));
                    if (game.heightMap.getTile(newPos.x, newPos.y).isObstruction) {
                        setDest(oldPos);
                        wanderCount = 3;
                        return changeState(State.WANDER);
                    }
                    pos.x = newx;
                    pos.y = newy;
                    if (!oldPos.equals(newPos)) {
                        map.remove(oldPos, this);
                        map.put(newPos, this);
                        House h = game.houses.getHouse(newPos.x, newPos.y);
                        if (h != null) {
                            return changeState(h.addPeon(this, leaders.containsValue(this)));
                        }
                    }

                    return null;

                case ALIVE:
                    switch (player.peonMode) {
                        case SETTLE:
                            if (game.houses.canBuild(oldPos.x, oldPos.y)) {
                                game.houses.addHouse(oldPos.x, oldPos.y, player, strength, leaders.containsValue(this));
                                return changeState(State.SETTLED);
                            }
                            setDest(findFlatLand(oldPos));
                            break;
                        case ANKH:
                            if (leaders.containsKey(player)) {
                                Peon l = leaders.get(player);
                                if (this == l) {
                                    setDest(player.info.ankh);
                                } else {
                                    setDest(l.getPoint());
                                }
                            } else {
                                setDest(player.info.ankh);
                                if (reachedDest()) {
                                    leaders.put(player, this);
                                }
                            }
                            break;
                        case FIGHT:
                            if (game.houses.canBuild(oldPos.x, oldPos.y)) {
                                game.houses.addHouse(oldPos.x, oldPos.y, player, strength, leaders.containsValue(this));
                                return changeState(State.SETTLED);
                            }
                            setDest(findEnemy());
                            break;
                        case GROUP:
                            if (game.houses.canBuild(oldPos.x, oldPos.y)) {
                                game.houses.addHouse(oldPos.x, oldPos.y, player, strength, leaders.containsValue(this));
                                return changeState(State.SETTLED);
                            }
                            setDest(findFriend());
                            break;
                        default:
                    }

                    if (destx == -1) {//no destination set
                        wanderCount = 3;
                        setDest(oldPos);
                        return changeState(State.WANDER);
                    }
                   
                    return changeState(State.WALKING);


                case DROWNING:
                    if (!(game.heightMap.getTile(oldPos.x, oldPos.y) == Tile.SEA)) {
                        return changeState(State.ALIVE);
                    }
                    strength -= 100.0f * seconds;
                    player.info.mana -= 100.0f * seconds;
                    return null;

                case MERGING:
                    if (map.size(oldPos) == 1) {
                        return changeState(State.ALIVE);

                    }
                    Peon other = map.get(oldPos).get(0);
                    if (this == other) {
                        other = map.get(oldPos).get(1);
                    }
                    if (other.player == this.player) {
                        //merge
                        other.strength += strength;
                        map.remove(oldPos, this);
                        if (leaders.containsValue(this)) { //consider leaving the leader alone??
                            leaders.put(player, other);
                        }
                        return changeState(State.DEAD);

                    } else {
                        //fight
                        float damage = 2 + max(strength, other.strength) / 10.0f;
                        strength -= damage;
                        player.info.mana -= damage;
                        other.strength -= damage;
                        other.player.info.mana -= damage;
                        return null;
                    //wait in merging queue until someone dies...
                    }

                case WANDER:
                    if (destx == -1) {
                        int px, py;
                        for (Point p2 : Helpers.shuffledRings.get(1)) {
                            px = oldPos.x + p2.x;
                            py = oldPos.y + p2.y;
                            if (game.heightMap.tileInBounds(px, py) && !game.heightMap.getTile(px, py).isObstruction) {
                                setDest(px, py);
                                return changeState(State.WANDER);
                            }
                        }
                        //Every surrounding tile is blocking
                        strength -= 20;

                        return null;
                    } else {
                        pos.x += seconds * dx;
                        pos.y += seconds * dy;
                        newPos = new Point((int) floor(pos.x), (int) floor(pos.y));

                        if (!oldPos.equals(newPos)) {
                            map.remove(oldPos, this);
                            map.put(newPos, this);

                        }
                        if (reachedDest()) {
                            setDest(null);
                            wanderCount--;
                            if (wanderCount <= 0) {
                                return changeState(State.ALIVE);
                            }
                        }
                        return null;
                    }
                case FALLING: {
                    if (stateTimer > 1.0f) {
                        return changeState(State.DEAD);
                    }
                    return null;
                }
                default:
                    throw new UnsupportedOperationException("Unsupported state" + state);
            }
        }

        private Point findFlatLand(Point start) {
             
             
                int px, py;

            for (Collection<Point> ring : Helpers.shuffledRings.subList(0, 15)) {
                for (Point offset : ring) {
                    px = start.x + offset.x;
                    py = start.y + offset.y;
                    if (game.heightMap.tileInBounds(px, py)) {
                        if (game.houses.canBuild(px, py)) {
                            double d = (px - p.x) * (px - p.x) + (py - p.y) * (py - p.y);
                            d = sqrt(d);
                            if (d > 5) {
                                px = start.x + (int) (offset.x * 5.0 / d);
                                py = start.y + (int) (offset.y * 5.0 / d);
                            }
                            return new Point(px, py);
                        }
                    }
                }
            }

            return null;
        }

        private void setDest(Point p) {
            if (p != null) {
                setDest(p.x, p.y);
            } else {
                dx = dy = 0;
                destx = desty = -1;
            }
        }

        private void setDest(int x, int y) {
            destx = x;
            desty = y;
            dx = destx + 0.5f - pos.x;
            dy = desty + 0.5f - pos.y;
            float dist = (float) sqrt(dx * dx + dy * dy);
            if (dist > 0.01) {
                dx = dx / dist;
                dy = dy / dist;
            }
        }

        private boolean reachedDest() {
            float ex = pos.x - (destx + 0.5f);
            float ey = pos.y - (desty + 0.5f);
            return ((abs(ex) < 0.1 && abs(ey) < 0.1));

        }
    }
}
