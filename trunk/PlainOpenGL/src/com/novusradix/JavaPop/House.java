package com.novusradix.JavaPop;
import java.awt.Point;
import java.util.Iterator;
import java.util.Vector;

import javax.media.opengl.GL;

public class House {

	public static HeightMap heightMap;
	private static int[][] map;
	private static Vector<House> houses;
	private static final int TEAMS = 4;
	private static final int EMPTY = 0;
	private static final int FARM = EMPTY + 1;
	private static final int HOUSE = FARM + TEAMS;
	private static final int NEXT = HOUSE + TEAMS;

	private Point pos;
	private int level;
	private int team;

	public static void init(HeightMap heightMap) {
		House.heightMap = heightMap;
		map = new int[heightMap.getWidth()][heightMap.getBreadth()];
		houses = new Vector<House>();
	}

	private House(int x, int y, int team) {
		map[x][y] = HOUSE + team;
		pos = new Point(x, y);
		level = 0;
		this.team = team;
		houses.add(this);
	}

	public static House newHouse(int x, int y, int team) {
		if (heightMap.isFlat(x, y) && canBuild(x, y)) {
			return new House(x, y, team);
		}
		return null;
	}

	public static boolean canBuild(int x, int y) {
		return map[x][y] == EMPTY;
	}

	public static void stepAll() {
		int[][] newmap;
		newmap = new int[heightMap.getWidth()][heightMap.getBreadth()];
		Iterator<House> i = houses.iterator();
		House h;
		for (; i.hasNext();) {
			h = i.next();
			if (heightMap.isFlat(h.pos.x, h.pos.y) && newmap[h.pos.x][h.pos.y] ==0) {
				h.setLevel();
				h.paintmap(newmap);
			} else {
				i.remove();
				Peon.addPeon(h.pos.x, h.pos.y);
			}
		}
		
		unpaint();
		
		map = newmap;
		paint();
	}

	private static void paint() {
		int x,y;
		for(y=0;y<heightMap.getBreadth();y++)
			for(x=0;x<heightMap.getWidth();x++)
			{
				if(map[x][y] != EMPTY)
					heightMap.setTexture(x, y, 2);
			}		
	}

	private static void unpaint() {
		int x,y;
		for(y=0;y<heightMap.getBreadth();y++)
			for(x=0;x<heightMap.getWidth();x++)
			{
				if(map[x][y] != EMPTY)
					heightMap.setTexture(x, y, 1);
			}
	}

	private void paintmap(int[][] newmap) {
		// TODO Auto-generated method stub
		newmap[pos.x][pos.y] = HOUSE + team;
		int radiuslimit =0;
		if(level>0)
			radiuslimit = 1;
		if(level>8)
			radiuslimit = 2;
		if(level==48)
			radiuslimit = 3;
		int x, y;
		
		int h = heightMap.getHeight(pos.x, pos.y);
		for (int radius = 1; radius <= radiuslimit; radius++) {
			y = -radius;
			for (x = -radius; x < radius; x++)
				if (heightMap.getHeight(pos.x + x, pos.y + y) == h && heightMap.isFlat(pos.x + x, pos.y + y))
					newmap[pos.x+x][pos.y+y] = FARM + team;
			x = radius;
			for (y = -radius; y < radius; y++)
				if (heightMap.getHeight(pos.x + x, pos.y + y) == h && heightMap.isFlat(pos.x + x, pos.y + y))
					newmap[pos.x+x][pos.y+y] = FARM + team;
			y = radius;
			for (x = radius; x > -radius; x--)
				if (heightMap.getHeight(pos.x + x, pos.y + y) == h && heightMap.isFlat(pos.x + x, pos.y + y))
					newmap[pos.x+x][pos.y+y] = FARM + team;
			x = -radius;
			for (y = radius; y > -radius; y--)
				if (heightMap.getHeight(pos.x + x, pos.y + y) == h && heightMap.isFlat(pos.x + x, pos.y + y))
					newmap[pos.x+x][pos.y+y] = FARM + team;
		}

	}

	private void setLevel() {
		level = calcLevel();
	}

	private int calcLevel() {
		int l = countFlatLand();

		return l;
	}


	public int countFlatLand() {
		int x, y;
		int flat = 0;
		int h = heightMap.getHeight(pos.x, pos.y);
		for (int radius = 1; radius <= 3; radius++) {
			y = -radius;
			for (x = -radius; x < radius; x++)
				if (heightMap.getHeight(pos.x + x, pos.y + y) == h && heightMap.isFlat(pos.x + x, pos.y + y))
					flat++;
			x = radius;
			for (y = -radius; y < radius; y++)
				if (heightMap.getHeight(pos.x + x, pos.y + y) == h && heightMap.isFlat(pos.x + x, pos.y + y))
					flat++;
			y = radius;
			for (x = radius; x > -radius; x--)
				if (heightMap.getHeight(pos.x + x, pos.y + y) == h && heightMap.isFlat(pos.x + x, pos.y + y))
					flat++;
			x = -radius;
			for (y = radius; y > -radius; y--)
				if (heightMap.getHeight(pos.x + x, pos.y + y) == h && heightMap.isFlat(pos.x + x, pos.y + y))
					flat++;
			if (flat < (2 * radius + 1) * (2 * radius + 1) - 1)
				break;
		}

		return flat;
	}

	public static void display(GL gl) {
		if (houses != null) {
			for (House h : houses) {
				gl.glPushMatrix();
				gl.glTranslatef(h.pos.x, h.pos.y, heightMap.getHeight(h.pos.x, h.pos.y));

				gl.glEnable(GL.GL_LIGHTING);
				gl.glBegin(GL.GL_QUADS);
				gl.glColor3f(1, 1, 1);
				gl.glNormal3f(-1, 0, 0);
				gl.glVertex3f(0.2f, 0.2f, 0.0f);
				gl.glVertex3f(0.2f, 0.8f, 0.0f);
				gl.glVertex3f(0.2f, 0.8f, 0.8f);
				gl.glVertex3f(0.2f, 0.2f, 0.8f);

				gl.glNormal3f(1, 0, 0);
				gl.glVertex3f(0.8f, 0.2f, 0.0f);
				gl.glVertex3f(0.8f, 0.8f, 0.0f);
				gl.glVertex3f(0.8f, 0.8f, 0.8f);
				gl.glVertex3f(0.8f, 0.2f, 0.8f);

				gl.glNormal3f(0, -1, 0);
				gl.glVertex3f(0.2f, 0.2f, 0.0f);
				gl.glVertex3f(0.8f, 0.2f, 0.0f);
				gl.glVertex3f(0.8f, 0.2f, 0.8f);
				gl.glVertex3f(0.2f, 0.2f, 0.8f);

				gl.glNormal3f(0, 1, 0);
				gl.glVertex3f(0.2f, 0.8f, 0.0f);
				gl.glVertex3f(0.8f, 0.8f, 0.0f);
				gl.glVertex3f(0.8f, 0.8f, 0.8f);
				gl.glVertex3f(0.2f, 0.8f, 0.8f);

				gl.glEnd();
				gl.glPopMatrix();
			}
		}
	}
}
