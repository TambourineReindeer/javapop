package com.novusradix.JavaPop.Server.Peons;

import com.novusradix.JavaPop.Math.Helpers;
import com.novusradix.JavaPop.Messaging.PeonUpdate.Detail;
import com.novusradix.JavaPop.Server.ServerHouses.ServerHouse;
import com.novusradix.JavaPop.Server.ServerPeons.Action;
import java.awt.Point;
import static java.lang.Math.*;

/**
 *
 * @author gef
 */
public class Perseus extends Peon {

    public Perseus(Peon p) {
        super(p);
    }

    @Override
    protected float getSpeed() {
        return 2.0f;
    }

    @Override
    public Detail step(float seconds) {
        Point oldPos = getPoint();
        stateTimer += seconds;
        if (strength < 0) {
            return changeState(State.DEAD);
        }
        State oldState = state;
        switch (game.heightMap.getTile(oldPos.x, oldPos.y).action) {
            case DROWN:
                state = State.DROWNING;
                break;
            case BURN:
                state = State.BURNT;
                break;
            case FALL:
                state = State.FALLING;
                break;
            default:
        }


        if (inMiddleOfTile()) {
            for (Peon other : game.peons.getPeons(getPoint())) {
                if (other.player != player) {
                    if (other.inMiddleOfTile()) {
                        //fight
                        float damage = seconds * (2 + max(strength, other.strength));
                        strength -= damage;
                        other.strength -= damage;
                        state = State.FIGHTING;
                        stateTimer = 0;
                        break;
                    }
                    if (other.shortDest.x == getPoint().x && other.shortDest.y == getPoint().y) {//wait for them to catch up
                        return changeState(State.WAITING);
                    }
                }
            }
        }
        if (state != oldState) {
            changed = true;
        }

        switch (state) {
            case FALLING:
            case BURNT:
                if (stateTimer > 1.0f) {
                    return changeState(State.DEAD);
                }
                break;
            case FIGHTING:
            case ELECTRIFIED:
                if (stateTimer < 0.2f) {
                    break; //still electrified
                }
            //worn off - fallthrough.
            case DROWNING:
            case WAITING:
                changed = true;
                state = State.WALKING;
            //fall through
            case WALKING:
                strength -= seconds;
                if (reachedDest()) {

                    ServerHouse h = game.houses.getHouse(getPoint().x, getPoint().y);
                    if (h != null && h.player != player) {
                        h.damage(min(strength, h.strength));
                    }
                    pickNextShortDest();
                    changed = true;
                }
                pos.x += seconds * dx;
                pos.y += seconds * dy;
                pos.x = Helpers.clip(pos.x, 0.5f, game.heightMap.width - 1.5f);
                pos.y = Helpers.clip(pos.y, 0.5f, game.heightMap.breadth - 1.5f);

            default:

        }

        if (changed) {
            changed = false;
            return changeState(state);
        }

        return null;
    }

    @Override
    protected Point pickNextTarget() {
        return findEnemy(game.heightMap.width + game.heightMap.breadth);
    }
}
