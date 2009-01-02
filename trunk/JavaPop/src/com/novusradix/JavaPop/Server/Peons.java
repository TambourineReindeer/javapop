package com.novusradix.JavaPop.Server;

import com.novusradix.JavaPop.Math.Helpers;
import com.novusradix.JavaPop.Math.MultiMap;
import com.novusradix.JavaPop.Math.SortedMultiMap;
import com.novusradix.JavaPop.Math.Vector2;
import com.novusradix.JavaPop.Messaging.PeonUpdate;
import com.novusradix.JavaPop.Server.Houses.House;
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

        ALIVE, DEAD, SETTLED, WALKING, DROWNING, MERGING
    };
    private Vector<Peon> peons;
    private MultiMap<Point, Peon> map;
    private int nextId = 0;

    public Peons(Game g) {
        game = g;
        peons = new Vector<Peon>();
        map = new MultiMap<Point, Peon>();
    }

    public void addPeon(float x, float y, float strength, Player player) {
        Peon p = new Peon(x, y, strength, player);
        peons.add(p);
        map.put(p.getPoint(), p);

    }

    public void addPeon(int x, int y, float strength, Player player) {
        addPeon(x + 0.5f, y + 0.5f, strength, player);
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

    class Peon {

        public int id;
        public Vector2 pos;
        float strength;
        private Point dest; // destination to walk to.
        private State state;
        private float dx,  dy;
        Player player;
        private float walkingTime;

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

            Point oldPos = getPoint();
            strength -= seconds;
            if (strength < 0) {
                state = State.DEAD;
                return new PeonUpdate.Detail(id, state, pos, dest, dx, dy, player.getId());
            }

            switch (state) {
                case WALKING:
                    if (game.heightMap.isSea(oldPos)) {
                        state = State.DROWNING;
                        return new PeonUpdate.Detail(id, state, pos, dest, dx, dy, player.getId());
                    }
                    if (map.size(oldPos) > 1) {
                        state = State.MERGING;
                        return new PeonUpdate.Detail(id, state, pos, dest, dx, dy, player.getId());
                    }

                    if (oldPos.equals(dest) || walkingTime > 2.0f) {

                        //Yay we're here! Or we're taking a breather.
                        state = State.ALIVE;
                        return new PeonUpdate.Detail(id, state, pos, dest, dx, dy, player.getId());
                    }

                    pos.x += seconds * dx;
                    pos.y += seconds * dy;
                    Point newPos = new Point((int) floor(pos.x), (int) floor(pos.y));
                    if (!oldPos.equals(newPos)) {
                        map.remove(oldPos, this);
                        map.put(newPos, this);
                        House h = game.houses.getHouse(newPos);
                        if (h != null) {
                            h.addPeon(this);
                            state = state.DEAD;
                            return new PeonUpdate.Detail(id, state, pos, dest, dx, dy, player.getId());

                        }
                    }
                    walkingTime += seconds;
                    return null;

                case ALIVE:
                    switch (player.peonMode) {
                        case SETTLE:
                            if (game.houses.canBuild(oldPos)) {
                                state = State.SETTLED;
                                game.houses.addHouse(oldPos, player, strength);
                                return new PeonUpdate.Detail(id, state, pos, dest, dx, dy, player.getId());
                            }
                            dest = findFlatLand(oldPos);
                            break;
                        case ANKH:
                            dest = player.info.ankh;
                            break;
                        case FIGHT:
                            dest = findEnemy();
                            break;
                        case GROUP:
                            if (game.houses.canBuild(oldPos)) {
                                state = State.SETTLED;
                                game.houses.addHouse(oldPos, player, strength);
                                return new PeonUpdate.Detail(id, state, pos, dest, dx, dy, player.getId());
                            }
                            dest = findFriend();
                            break;
                        default:
                    }
                    dx = dest.x + 0.5f - pos.x;
                    dy = dest.y + 0.5f - pos.y;
                    float dist = (float) sqrt(dx * dx + dy * dy);
                    dx = dx / dist;
                    dy = dy / dist;
                    state = State.WALKING;
                    walkingTime = 0;
                    return new PeonUpdate.Detail(id, state, pos, dest, dx, dy, player.getId());

                case DROWNING:
                    if (!(game.heightMap.getHeight(oldPos) == 0 && game.heightMap.isFlat(oldPos))) {
                        state = State.ALIVE;
                        return new PeonUpdate.Detail(id, state, pos, dest, dx, dy, player.getId());
                    }
                    strength -= 100.0f * seconds;
                    return null;

                case MERGING:
                    if (map.size(oldPos) == 1) {
                        state = State.ALIVE;
                        return new PeonUpdate.Detail(id, state, pos, dest, dx, dy, player.getId());
                    }
                    Peon other =
                            map.get(oldPos).get(0);
                    if (other.player == this.player) {
                        //merge
                        other.strength += strength;
                        map.remove(oldPos, this);
                        state = state.DEAD;
                        return new PeonUpdate.Detail(id, state, pos, dest, dx, dy, player.getId());
                    } else {
                        //fight
                        float damage = 2 + max(strength, other.strength) / 10.0f;
                        strength -= damage;
                        other.strength -= damage;
                        return null;
                    //wait in merging queue until someone dies...

                    }
            }
            return null;
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
            Random r = new Random();
            p = new Point();
            p.x = start.x + r.nextInt(5) - 2;
            p.y = start.y + r.nextInt(5) - 2;
            p.x = max(0, p.x);
            p.y = max(0, p.y);
            p.x = min(p.x, game.heightMap.getWidth() - 1);
            p.y = min(p.y, game.heightMap.getBreadth() - 1);
            return p;
        }
    }
}
