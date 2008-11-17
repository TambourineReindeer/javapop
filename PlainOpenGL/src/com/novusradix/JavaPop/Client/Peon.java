package com.novusradix.JavaPop.Client;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

import javax.media.opengl.GL;

public class Peon {

	public static HeightMap h;
	public float x;
	public float y;

	private int destX, destY; // destination to walk to.

	private float searchRadius = 0;

	public static final int ALIVE = 0;
	public static final int DEAD = 1;
	public static final int SETTLED = 3;

	private static Vector<Peon> peons;

	public static void init(HeightMap heightMap) {
		h = heightMap;
		peons = new Vector<Peon>();
	}

	public static void addPeon(float x, float y) {
		peons.add(new Peon(x, y));
	}

	private Peon(float x, float y) {
		this.x = x;
		this.y = y;

	}

	public static void stepall() {
		if (peons != null) {
			int status;
			Peon p;
			for (Iterator<Peon> i = peons.iterator(); i.hasNext();) {

				p = i.next();
				status = p.step();
				if (status == Peon.DEAD) {
					i.remove();
				}

				if (status == Peon.SETTLED) {
					if (House.canBuild((int) p.x, (int) p.y)) {
						i.remove();
						House.newHouse((int) p.x, (int) p.y, 1);
					}
				}
			}
		}
	}

	private int step() {
		// returns a peon status, e.g. DEAD

		// what can a peon do?
		// drown, die of exhaustion, settle down?

		// drown?
		int x1, y1;

		x1 = (int) Math.floor(x);
		y1 = (int) Math.floor(y);

		if (h.isFlat(x1, y1)) {
			if (h.getHeight(x1, y1) == 0) {
				// you're drowning
				// increment a drowning clock and PREPARE TO DIE
				return DEAD;
			} else {
				// we're on flat ground
				if (House.canBuild(x1, y1))
					return SETTLED;
			}
		}
		// We're on a hill or farm of some sort. Find a flat place to live.
		findFlatLand();

		float fdx = destX + 0.5f - x;
		float fdy = destY + 0.5f - y;
		float dist = (float) Math.sqrt(fdx * fdx + fdy * fdy);

		fdx = 0.05f * fdx / dist;
		fdy = 0.05f * fdy / dist;

		x += fdx;
		y += fdy;

		return ALIVE;
	}

	public static void displayall(GL gl) {
		for (Peon p : peons) {
			gl.glPushMatrix();
			gl.glTranslatef(p.x, p.y, h.getHeight(p.x, p.y));
			p.display(gl);
			gl.glPopMatrix();
		}
	}

	private void display(GL gl) {
		gl.glBegin(GL.GL_TRIANGLES);
		gl.glColor3f(0, 0, 1);

		gl.glVertex3f(0, 0, 0.3f);
		gl.glVertex3f(0.1f, -0.1f, 0);
		gl.glVertex3f(-0.1f, +0.1f, 0);

		gl.glEnd();

	}

	private void findFlatLand() {
		// TODO Auto-generated method stub
		float arcstep, arc;
		float deltax, deltay;
		Random r = new Random();
		destX = r.nextInt(h.getWidth());
		destY = r.nextInt(h.getBreadth());

		for (searchRadius = 0; searchRadius < 10; searchRadius += 0.5) {
			arcstep = 1.0f / (2.0f * 3.14159f * searchRadius);
			for (arc = 0; arc < 2.0 * 3.14159; arc += arcstep) {
				deltax = searchRadius * (float) Math.sin(arc);
				deltay = searchRadius * (float) Math.cos(arc);

				if (h.getHeight((int) (x + 0.5f + deltax), (int) (y + 0.5f + deltay)) > 0 
						&& h.isFlat((int) (x + 0.5f + deltax), (int) (y + 0.5f + deltay))
						&& House.canBuild((int) (x + 0.5f + deltax), (int) (y + 0.5f + deltay))) {
					destX = (int) (x + 0.5f + deltax);
					destY = (int) (y + 0.5f + deltay);
					searchRadius = 0;
					return;

				}

			}
		}
		if (searchRadius > 10)
			searchRadius = 0;
	}

}
