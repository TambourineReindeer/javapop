import java.awt.Point;
import java.util.Vector;

import javax.media.opengl.GL;

public class House {

	public static HeightMap heightMap;
	private static int[][] farm;
	private static Vector<House> houses;

	private Point pos;

	public static void init(HeightMap heightMap) {
		House.heightMap = heightMap;
		farm = new int[heightMap.getWidth()][heightMap.getBreadth()];
		houses = new Vector<House>();
	}

	private House(int x, int y, int team) {
		farm[x][y] = team;
		pos = new Point(x, y);
		houses.add(this);
	}

	public static House newHouse(int x, int y, int team) {
		if (heightMap.isFlat(x, y) && canBuild(x, y)) {
			return new House(x, y, team);
		}
		return null;
	}

	public static boolean canBuild(int x, int y) {
		return farm[x][y] == 0;
	}

	public static void display(GL gl) {
		if (houses != null) {
			for (House h : houses) {
			gl.glPushMatrix();
				gl.glTranslatef(h.pos.x, h.pos.y, heightMap.getHeight(h.pos.x, h.pos.y));
				
				gl.glEnable(GL.GL_LIGHTING);
				gl.glBegin(GL.GL_QUADS);
				gl.glColor3f(1, 1, 1);
				gl.glNormal3f(-1,0,0);
				gl.glVertex3f(0.2f, 0.2f, 0.0f);
				gl.glVertex3f(0.2f, 0.8f, 0.0f);
				gl.glVertex3f(0.2f, 0.8f, 0.8f);
				gl.glVertex3f(0.2f, 0.2f, 0.8f);
				
				gl.glNormal3f(1,0,0);
				gl.glVertex3f(0.8f, 0.2f, 0.0f);
				gl.glVertex3f(0.8f, 0.8f, 0.0f);
				gl.glVertex3f(0.8f, 0.8f, 0.8f);
				gl.glVertex3f(0.8f, 0.2f, 0.8f);
				
				gl.glNormal3f(0,-1,0);
				gl.glVertex3f(0.2f, 0.2f, 0.0f);
				gl.glVertex3f(0.8f, 0.2f, 0.0f);
				gl.glVertex3f(0.8f, 0.2f, 0.8f);
				gl.glVertex3f(0.2f, 0.2f, 0.8f);
				
				gl.glNormal3f(0,1,0);
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
