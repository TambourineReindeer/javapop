package com.novusradix.JavaPop;

/**
 *
 * @author gef
 */
public enum Direction {

    NORTH(0, 1, 0, 1, 1, 1),  EAST(1, 0, 1, 0, 1, 1), SOUTH(0, -1, 0, 0, 1, 0), WEST(-1, 0, 0, 0, 0, 1);
    public int dx, dy, frontx1, frontx2, fronty1, fronty2;

    Direction(int x, int y, int fx1, int fy1, int fx2, int fy2) {
        this.dx = x;
        this.dy = y;
        this.frontx1 = fx1;
        this.fronty1 = fy1;
        this.frontx2 = fx2;
        this.fronty2 = fy2;
    }
}