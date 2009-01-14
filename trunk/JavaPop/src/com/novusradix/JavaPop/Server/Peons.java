package com.novusradix.JavaPop.Server;

import com.novusradix.JavaPop.Math.Helpers;
import com.novusradix.JavaPop.Math.MultiMap;
import com.novusradix.JavaPop.Math.Vector2;
import com.novusradix.JavaPop.Messaging.PeonUpdate;
import com.novusradix.JavaPop.Server.Houses.House;
import com.novusradix.JavaPop.Tile;
import java.awt.Point;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import static java.lang.Math.*;

public class Peons {

    public Game game;

    public enum State {

        ALIVE, DEAD, SETTLED, WALKING, DROWNING, MERGING, WANDER
    };
    private Vector<Peon> peons;
    private MultiMap<Point, Peon> map;
    private int nextId = 0;

    public Peons(Game g) {
        game = g;
        peons = new Vector<Peon>();
        map = new MultiMap<Point, Peon>();
    }

    public Collection<Peon> getPeons(Point p) {
        return map.get(p);
    }

    public void addPeon(float x, float y, float strength, Player player) {
        Peon p = new Peon(x, y, strength, player);
        peons.add(p);
        map.put(p.getPoint(), p);

    }

    public void addPeon(Point p, float strength, Player player) {
        addPeon(p.x + 0.5f, p.y + 0.5f, strength, player);
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
                    case SETTLED:
                        i.remove();
                        map.remove(p.getPoint(), p);
                        break;
                }
            }
            if (pds.size() > 0) {
                game.sendAllPlayers(new PeonUpdate(pds));
                pds.clear();
            }
        }
    }

    public class Peon {

        public int id;
        public Vector2 pos;
        float strength;
        private Point dest; // destination to walk to.
        private State state;
        private float dx,  dy;
        Player player;
        private float walkingTime;
        private int wanderCount;

        public Point getPoint() {
            return new Point((int) floor(pos.x), (int) floor(pos.y));
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
            float dp, dh;
            if (nearestHouse == null && nearestPeon == null) {
                return findFlatLand(getPoint());
            }
            if (nearestPeon == null) {
                return nearestHouse;
            }
            if (nearestHouse == null) {
                return nearestPeon;
            }
            dp = (pos.x - nearestPeon.x) * (pos.x - nearestPeon.x) + (pos.y - nearestPeon.y) * (pos.y - nearestPeon.y);
            dh = (pos.x - nearestHouse.x) * (pos.x - nearestHouse.x) + (pos.y - nearestHouse.y) * (pos.y - nearestHouse.y);
            if (dp < dh) {
                return nearestPeon;
            }
            return nearestHouse;
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
            return new PeonUpdate.Detail(id, state, pos, dest, dx, dy, player.getId());
        }

        private PeonUpdate.Detail step(float seconds) {
            float dist;
            Point oldPos = getPoint();
            strength -= seconds;
            player.info.mana -= seconds;

            if (strength < 0) {
                player.info.mana -= this.strength;
                return changeState(State.DEAD);
            }
            if (game.heightMap.getTile(oldPos) == Tile.SEA) {
                return changeState(State.DROWNING);
            }
            if (game.heightMap.getTile(oldPos) == Tile.LAVA) {
                return changeState(State.DEAD);
            }
            if (game.heightMap.getTile(oldPos) == Tile.SWAMP) {
                return changeState(State.DEAD);
            }
            if (state != state.MERGING) {
                if (map.size(oldPos) > 1) {
                    return changeState(State.MERGING);
                }
            }
            switch (state) {
                case WALKING:
                    if (reachedDest() || walkingTime > 2.0f) {
                        //Yay we're here! Or we're taking a breather.
                        return changeState(State.ALIVE);
                    }
                    float newx,
                     newy;
                    newx = pos.x + seconds * dx;
                    newy = pos.y + seconds * dy;
                    Point newPos = new Point((int) floor(newx), (int) floor(newy));
                    if (game.heightMap.getTile(newPos).isObstruction) {
                        setDest(oldPos);
                        wanderCount = 3;
                        return changeState(State.WANDER);
                    }
                    pos.x = newx;
                    pos.y = newy;
                    if (!oldPos.equals(newPos)) {
                        map.remove(oldPos, this);
                        map.put(newPos, this);
                        House h = game.houses.getHouse(newPos);
                        if (h != null) {
                            h.addPeon(this);
                            return changeState(State.DEAD);
                        }
                    }

                    walkingTime += seconds;
                    return null;

                case ALIVE:
                    switch (player.peonMode) {
                        case SETTLE:
                            if (game.houses.canBuild(oldPos)) {
                                game.houses.addHouse(oldPos, player, strength);
                                return changeState(State.SETTLED);
                            }
                            setDest(findFlatLand(oldPos));
                            break;
                        case ANKH:
                            setDest(player.info.ankh);
                            break;
                        case FIGHT:
                            setDest(findEnemy());
                            break;
                        case GROUP:
                            if (game.houses.canBuild(oldPos)) {
                                game.houses.addHouse(oldPos, player, strength);
                                return changeState(State.SETTLED);
                            }
                            setDest(findFriend());
                            break;
                        default:
                    }

                    if (dest == null) {
                        wanderCount = 3;
                        setDest(oldPos);
                        return changeState(State.WANDER);
                    }

                    walkingTime = 0;
                    return changeState(State.WALKING);


                case DROWNING:
                    if (!(game.heightMap.getTile(oldPos) == Tile.SEA)) {
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
                    if (other.player == this.player) {
                        //merge
                        other.strength += strength;
                        map.remove(oldPos, this);
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
                    if (dest == null) {
                        for (Point p : Helpers.shuffledRings.get(1)) {
                            Point p2 = new Point(oldPos.x + p.x, oldPos.y + p.y);
                            if (game.heightMap.tileInBounds(p2) && !game.heightMap.getTile(p2).isObstruction) {
                                setDest(p2);
                                dx = dest.x + 0.5f - pos.x;
                                dy = dest.y + 0.5f - pos.y;
                                dist = (float) sqrt(dx * dx + dy * dy);
                                dx = dx / dist;
                                dy = dy / dist;
                                return changeState(State.WANDER);
                            }
                        }
                        //Every surrounding tile is blocking
                        strength -= 20;

                        return null;
                    } else {
                        pos.x += seconds * dx;
                        pos.y += seconds * dy;
                        newPos = getPoint();

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
                default:
                    throw new UnsupportedOperationException("Unsupported state" + state);
            }
        }

        private Point findFlatLand(Point start) {
            // TODO Auto-generated method stub
            Point p;
            for (Collection<Point> ring : Helpers.shuffledRings.subList(0, 15)) {
                for (Point offset : ring) {
                    p = new Point(start.x + offset.x, start.y + offset.y);
                    if (game.heightMap.tileInBounds(p)) {
                        if (game.houses.canBuild(p)) {
                            double d = start.distance(p);
                            if (d > 5) {
                                p = new Point(start.x + (int) (offset.x * 5.0 / d), start.y + (int) (offset.y * 5.0 / d));
                            }
                            return p;
                        }
                    }
                }
            }

            return null;
        }

        private void setDest(Point p) {
            dest = p;
            if (dest != null) {
                dx = dest.x + 0.5f - pos.x;
                dy = dest.y + 0.5f - pos.y;
                float dist = (float) sqrt(dx * dx + dy * dy);
                dx = dx / dist;
                dy = dy / dist;
            } else {
                dx = dy = 0;
            }
        }

        private boolean reachedDest() {
            float ex = pos.x - (dest.x + 0.5f);
            float ey = pos.y - (dest.y + 0.5f);
            return
             ((abs(ex) < 0.1 && abs(ey)<0.1));
        
        }
    }
}
