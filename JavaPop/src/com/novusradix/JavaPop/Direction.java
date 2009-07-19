package com.novusradix.JavaPop;

/**
 * This class is used in several places where directions need to be passed. By passing a <code>Direction</code>, the receiving function can figure out which is the leading edge of a tile and reason accordingly.
 * @author gef
 */
public enum Direction {

    NORTH(0, 1, 0, 1, 1, 1),  EAST(1, 0, 1, 0, 1, 1), SOUTH(0, -1, 0, 0, 1, 0), WEST(-1, 0, 0, 0, 0, 1);
    public int dx, dy, frontx1, frontx2, fronty1, fronty2;
    public static final Direction[] directions;
    
    static {
        directions = values();
    }
    
    Direction(int x, int y, int fx1, int fy1, int fx2, int fy2) {
        this.dx = x;
        this.dy = y;
        this.frontx1 = fx1;
        this.fronty1 = fy1;
        this.frontx2 = fx2;
        this.fronty2 = fy2;
    }
}