package com.novusradix.JavaPop.Server.Peons;

import com.novusradix.JavaPop.Math.Helpers;
import com.novusradix.JavaPop.Math.Vector2;
import com.novusradix.JavaPop.Messaging.PeonUpdate;
import com.novusradix.JavaPop.Server.ServerGame;
import com.novusradix.JavaPop.Server.ServerPeons.State;
import com.novusradix.JavaPop.Server.ServerPlayer;
import com.novusradix.JavaPop.Server.ServerPlayer.PeonMode;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import static java.lang.Math.*;

/**
 *
 * @author gef
 */
public class Peon {

    public final int id;
    public final Vector2 pos;
    public float strength;
    private State state;
    public ServerPlayer player;
    private static int nextId = 1;
    private Point p;
    private final ServerGame game;
    private float dx,  dy;
    private Point shortDest;
    private boolean changed;
    private float stateTimer = 0;

    public Point getPoint() {
        int fx, fy;
        fx = (int) floor(pos.x);
        fy = (int) floor(pos.y);
        if (p == null || fx != p.x || fy != p.y) {
            p = new Point(fx, fy);
        }
        return p;
    }

    public Peon(float x, float y, float strength, ServerPlayer sp) {
        id = nextId++;
        pos = new Vector2(x, y);
        shortDest = new Point(getPoint());
        this.strength = strength;
        state = State.WALKING;
        changed = true;
        player = sp;
        game = sp.currentGame;
        pickNextShortDest();

    }

    public PeonUpdate.Detail step(float seconds) {
        Point oldPos = getPoint();
        stateTimer += seconds;
        switch (game.heightMap.getTile(oldPos.x, oldPos.y).action) {
            case DROWN:
                return changeState(State.DROWNING);
            case BURN:
                return changeState(State.DEAD);
            case FALL:
                return changeState(State.FALLING);
            default:
        }

        if (inMiddleOfTile()) {
            for (Peon other : game.peons.getPeons(getPoint())) {
                if (other.inMiddleOfTile()) {
                    if (other.player == player) {
                        if (!game.peons.isLeader(this)) {
                            //merge    
                            other.strength += strength;
                            return changeState(State.DEAD);
                        }
                    } else {
                        //fight
                        float damage = seconds * (2 + max(strength, other.strength) / 10.0f);
                        strength -= damage;
                        other.strength -= damage;
                        return changeState(State.FIGHTING);

                    }
                }

                if (other.shortDest.x == getPoint().x && other.shortDest.y == getPoint().y) {//wait for them to catch up
                    return changeState(State.WAITING);
                }
            }
        }

        switch (state) {
            case FALLING:
            case BURNT:
                if (stateTimer > 1.0f) {
                    return changeState(State.DEAD);
                }
                break;

            case ELECTRIFIED:

                if (stateTimer < 0.2f) {
                    break; //still electrified
                }
            //worn off - fallthrough.
            case FIGHTING:
            case DROWNING:
            case WAITING:
                changed = true;
                state = State.WALKING;
            //fall through
            case WALKING:

                strength -= seconds;

                if (strength < 0) {
                    return changeState(State.DEAD);
                }

                if (reachedDest()) {
                    if (player.peonMode == PeonMode.ANKH && getPoint().distanceSq(player.getPapalMagnet()) == 0) {
                        game.peons.getLeaders().put(player, this);

                    }
                    float settleProb;
                    Random r = new Random();
                    switch (player.peonMode) {
                        case ANKH:
                            settleProb = 0;
                            break;
                        case FIGHT:
                        case GROUP:
                            settleProb = 0.3f;
                            break;
                        case SETTLE:
                        default:
                            settleProb = 1.0f;
                            break;
                    }
                    if (r.nextFloat() < settleProb) {
                        if (tryBuildHouse() == State.SETTLED) {
                            return changeState(State.SETTLED);
                        }
                    }
                    pickNextShortDest();
                    changed = true;
                }
                pos.x += seconds * dx;
                pos.y += seconds * dy;
            default:

        }

        if (changed) {
            changed = false;
            return changeState(state);
        }

        return null;
    }

    public void hurt(float i) {
        strength -= i;
    }

    public void setState(State s) {
        state = s;
        changed = true;
    }

    private Point findEnemy() {
        Set<ServerPlayer> enemies = new HashSet<ServerPlayer>(game.players);
        enemies.remove(player);
        Point nearestPeon = nearestPeon(getPoint(), enemies, 8);
        Point nearestHouse = game.houses.nearestHouse(getPoint(), enemies);
        Point nearest;
        if (nearestHouse == null && nearestPeon == null) {
            return findFlatLand(getPoint(), 15);
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
            return findFlatLand(getPoint(), 15);
        }
        return nearest;

    }

    private Point findFriend() {
        Set<ServerPlayer> me = new HashSet<ServerPlayer>();
        me.add(player);
        Point nearestPeon = nearestPeon(getPoint(), me, 8);
        if (nearestPeon == null) {
            return findFlatLand(getPoint(), 15);
        }
        return nearestPeon;
    }

    public Point nearestPeon(Point p, Set<ServerPlayer> players, int searchRadius) {
        float d2 = game.heightMap.getWidth() * game.heightMap.getBreadth() + 1;
        Peon nearest = null;
        for (Peon peon : game.peons.getPeons()) {
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
        return new PeonUpdate.Detail(id, state, pos.x, pos.y, shortDest.x, shortDest.y, dx, dy, player.getId());
    }
    Point temp = new Point();

    private void pickNextShortDest() {
        int tx, ty;
        Point target;
        switch (player.peonMode) {
            case ANKH:
                if (game.peons.getLeaders().containsKey(player)) {
                    Peon l = game.peons.getLeaders().get(player);
                    if (this == l) {
                        target = player.getPapalMagnet();
                    } else {
                        target = l.getPoint();
                    }
                } else {
                    target = player.getPapalMagnet();
                }
                break;
            case FIGHT:
                target = findEnemy();
                break;
            case GROUP:
                target = findFriend();
                break;
            case SETTLE:
            default:
                target = findFlatLand(getPoint(), 15);
                break;
        }
        int[] score = new int[9];
        Point[] ps = Helpers.neighbours;

        for (int i = 0; i < 9; i++) {
            temp.setLocation(getPoint().x + ps[i].x, getPoint().y + ps[i].y);
            if (game.heightMap.tileInBounds(temp.x, temp.y) && !game.heightMap.getTile(temp.x, temp.y).isObstruction) {
                if (target != null) {
                    if (ps[i].x == Integer.signum(target.x - getPoint().x)) {
                        score[i]++;
                    }
                    if (ps[i].y == Integer.signum(target.y - getPoint().y)) {
                        score[i]++;
                    }
                }
            }
        }

        int maxscore = -1;
        for (int i = 0; i < 9; i++) {
            if (score[i] > maxscore) {
                temp.setLocation(getPoint().x + ps[i].x, getPoint().y + ps[i].y);
                maxscore = score[i];
            }
        }

        if (maxscore > -1) {
            setShortDest(temp);
        }
        //nowhere to go - die.
        strength *= 0.8;
    }

    private Point findFlatLand(Point start, int radius) {

        int px, py;
        assert (radius > 0);
        for (Collection<Point> ring : Helpers.shuffledRings.subList(0, radius)) {
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

    private void setShortDest(Point p) {

        shortDest.setLocation(p);
        dx = shortDest.x + 0.5f - pos.x;
        dy = shortDest.y + 0.5f - pos.y;
        float dist = (float) sqrt(dx * dx + dy * dy);
        if (dist > 0.01) {
            dx = dx / dist;
            dy = dy / dist;
        }
    }

    private boolean reachedDest() {
        float ex = pos.x - (shortDest.x + 0.5f);
        float ey = pos.y - (shortDest.y + 0.5f);
        if ((abs(ex) < 0.1 && abs(ey) < 0.1)) {
            pos.x = shortDest.x + 0.5f;
            pos.y = shortDest.y + 0.5f;
            return true;
        }
        return false;
    }

    private boolean inMiddleOfTile() {
        float ex = pos.x - (shortDest.x + 0.5f);
        float ey = pos.y - (shortDest.y + 0.5f);
        if ((abs(ex) < 0.1 && abs(ey) < 0.1)) {
            pos.x = shortDest.x + 0.5f;
            pos.y = shortDest.y + 0.5f;
            return true;
        }
        ex = (float) (pos.x - floor(pos.x) - 0.5f);
        ey = (float) (pos.y - floor(pos.y) - 0.5f);
        if ((abs(ex) < 0.1 && abs(ey) < 0.1)) {
            return true;
        }
        return false;
    }

    private State tryBuildHouse() {
        if (game.houses.canBuild((int) pos.x, (int) pos.y)) {
            game.houses.addHouse((int) pos.x, (int) pos.y, player, strength, game.peons.isLeader(this));
            return State.SETTLED;
        }
        return null;
    }
}

