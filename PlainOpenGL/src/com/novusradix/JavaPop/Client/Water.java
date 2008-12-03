package com.novusradix.JavaPop.Client;
import com.novusradix.JavaPop.Math.Vector2;
import com.novusradix.JavaPop.Math.Vector3;
import com.novusradix.JavaPop.Math.Vector4;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

import javax.media.opengl.GL;

public class Water {

	private HeightMap h;
	private float[][] w, w2;
	private float[][] mx, my;
	private Vector<Vector2> springs;

	private int n = 0;

	private Vector<Vector4> drops;

	// private Vector<Vector2> momentum;

	public Water(HeightMap h) {
		this.h = h;
		w = new float[h.getWidth()][h.getBreadth()];
		w2 = new float[h.getWidth() - 1][h.getBreadth() - 1];
		mx = new float[h.getWidth()][h.getBreadth()];
		my = new float[h.getWidth()][h.getBreadth()];

		drops = new Vector<Vector4>();
		springs = new Vector<Vector2>();

		// momentum = new Vector<Vector2>();
	}

	public void drop(float x, float y) {
		if (x > 0 && x < h.getWidth() && y > 0 && y < h.getBreadth()) {
			Vector4 p;

			p = new Vector4(x, y, 0, 0);

			drops.add(p);
		}
	}

	private float dw(float ha, float wa, float hb, float wb) {
		float d;

		d = (hb + wb) - (ha + wa); // maximum column difference

		d = d / 3.0f; // averaging adjustment

		d = Math.min(d, wb / 8.0f); // max inflow adjustment;

		d = Math.max(d, -wa / 8.0f); // max outflow

		return d;
	}

	public void step2() {
		float[][] neww = new float[h.getWidth()][h.getBreadth()];
		float[][] neww2 = new float[h.getWidth() - 1][h.getBreadth() - 1];

		for (Vector2 s : springs) {
			for (int m = 0; m < 2; m++) {
				w[(int) s.x][(int) s.y] += 0.01f;
			}
		}

		float d;
		for (int x = 1; x < h.getWidth() - 1; x++) {
			for (int y = 1; y < h.getBreadth() - 1; y++) {
				d = dw((float) h.getHeight(x, y), w[x][y], (float) h.getHeight(x - 1, y), w[x - 1][y]);
				d += dw((float) h.getHeight(x, y), w[x][y], (float) h.getHeight(x + 1, y), w[x + 1][y]);
				d += dw((float) h.getHeight(x, y), w[x][y], (float) h.getHeight(x, y - 1), w[x][y - 1]);
				d += dw((float) h.getHeight(x, y), w[x][y], (float) h.getHeight(x, y + 1), w[x][y + 1]);
				d += dw((float) h.getHeight(x, y), w[x][y], (float) h.getHeight2(x, y), w2[x][y]);
				d += dw((float) h.getHeight(x, y), w[x][y], (float) h.getHeight2(x - 1, y), w2[x - 1][y]);
				d += dw((float) h.getHeight(x, y), w[x][y], (float) h.getHeight2(x, y - 1), w2[x][y - 1]);
				d += dw((float) h.getHeight(x, y), w[x][y], (float) h.getHeight2(x - 1, y - 1), w2[x - 1][y - 1]);

				neww[x][y] = Math.max(0, w[x][y] + d / 2.0f);
			}
		}

		for (int x = 0; x < h.getWidth() - 1; x++) {
			for (int y = 0; y < h.getBreadth() - 1; y++) {
				d = dw((float) h.getHeight2(x, y), w2[x][y], (float) h.getHeight(x, y), w[x][y]);
				d += dw((float) h.getHeight2(x, y), w2[x][y], (float) h.getHeight(x + 1, y), w[x + 1][y]);
				d += dw((float) h.getHeight2(x, y), w2[x][y], (float) h.getHeight(x, y + 1), w[x][y + 1]);
				d += dw((float) h.getHeight2(x, y), w2[x][y], (float) h.getHeight(x + 1, y + 1), w[x + 1][y + 1]);

				neww2[x][y] = Math.max(0, w2[x][y] + d / 2.0f);
			}
		}
		w = neww;
		w2 = neww2;
	}

	public void step() {
		Random r = new Random();

		for (Vector2 s : springs) {
			for (int m = 0; m < 2; m++) {
				if (r.nextInt(10) > 7)
					drop(s.x + r.nextFloat(), s.y + r.nextFloat());
				w[(int) s.x][(int) s.y] += 0.1f;
			}
		}

		float[][] neww = new float[h.getWidth()][h.getBreadth()];
		float[][] newmx = new float[h.getWidth()][h.getBreadth()];
		float[][] newmy = new float[h.getWidth()][h.getBreadth()];

		Vector2 s;
		Vector4 d;

		for (Iterator<Vector4> i = drops.iterator(); i.hasNext();) {

			d = i.next();
			s = h.getSlope(d.x, d.y);
			// s.scale(1.0f / (1.0f + (w[(int) d.x][(int) d.y])) / 2.0f);
			d.z -= s.x / 100.0f;
			d.w -= s.y / 100.0f;

			d.z = 0.99f * d.z + r.nextFloat() * 0.006f - 0.003f;
			d.w = 0.99f * d.w + r.nextFloat() * 0.006f - 0.003f;

			if (Math.abs(d.z) > Math.abs(newmx[(int) d.x][(int) d.y]))
				newmx[(int) d.x][(int) d.y] = d.z;
			if (Math.abs(d.w) > Math.abs(newmy[(int) d.x][(int) d.y]))
				newmy[(int) d.x][(int) d.y] = d.w;
			// newmc[(int) d.x][(int) d.y] += 1;

			// if (mc[(int) d.x][(int) d.y] > 0) {
			d.z = 0.95f * d.z + 0.05f * mx[(int) d.x][(int) d.y];
			d.w = 0.95f * d.w + 0.05f * my[(int) d.x][(int) d.y];
			// }

			d.x += d.z;
			d.y += d.w;

			if (d.x < 0 || d.x >= h.getWidth() - 1 || d.y < 0 || d.y >= h.getBreadth() - 1 || r.nextInt(200) == 0)
				i.remove();
			else
				neww[(int) d.x][(int) d.y]++;

		}
		for (int x = 1; x < h.getWidth() - 1; x++) {
			for (int y = 1; y < h.getBreadth() - 1; y++) {
				mx[x][y] = (9.0f * mx[x][y] + newmx[x - 1][y - 1] + newmx[x - 1][y] + newmx[x - 1][y + 1]
						+ newmx[x][y - 1] + newmx[x][y] + newmx[x][y + 1] + newmx[x + 1][y - 1] + newmx[x + 1][y] + newmx[x + 1][y + 1]) / 18.0f;
				my[x][y] = (9.0f * my[x][y] + newmy[x - 1][y - 1] + newmy[x - 1][y] + newmy[x - 1][y + 1]
						+ newmy[x][y - 1] + newmy[x][y] + newmy[x][y + 1] + newmy[x + 1][y - 1] + newmy[x + 1][y] + newmy[x + 1][y + 1]) / 18.0f;
			}
		}

		w = neww;
		// mx = newmx;
		// my = newmy;
	}

	public void display(GL gl) {
		n++;
		int m = 0;
		float s = 0.5f;

		float r1, g1, b1;
		float r2, g2, b2;

		gl.glEnable(GL.GL_BLEND);
		gl.glShadeModel(GL.GL_SMOOTH);
		gl.glDepthMask(false);
		gl.glDisable(GL.GL_DEPTH_TEST);
		gl.glBegin(GL.GL_TRIANGLES);
		Vector4 d;
		for (Iterator<Vector4> i = drops.iterator(); i.hasNext();) {
			m++;
			d = i.next();
			r1 = 0.1f + 0.1f * (float) Math.sin(n * 0.01f + d.z);
			r2 = 0.1f + 0.1f * (float) Math.sin(n * 0.05f + d.w);
			g1 = 0.5f + 0.1f * (float) Math.sin(n * 0.01f + d.z);
			g2 = 0.5f + 0.1f * (float) Math.sin(n * 0.02f + d.w);
			b1 = 0.9f + 0.1f * (float) Math.sin(n * 0.01f + d.z);
			b2 = 0.9f + 0.1f * (float) Math.sin(n * 0.012f + d.w);

			gl.glColor4f(r1, g1, b1, 1);
			gl.glVertex3f(d.x, d.y, h.getHeight(d.x, d.y) + 0.05f);
			gl.glColor4f(r2, g2, b2, 0);
			gl.glVertex3f(d.x + s, d.y - s, h.getHeight(d.x, d.y));
			gl.glVertex3f(d.x + s, d.y + s, h.getHeight(d.x, d.y));

			gl.glColor4f(r1, g1, b1, 1);
			gl.glVertex3f(d.x, d.y, h.getHeight(d.x, d.y) + 0.05f);
			gl.glColor4f(r2, g2, b2, 0);
			gl.glVertex3f(d.x - s, d.y - s, h.getHeight(d.x, d.y));
			gl.glVertex3f(d.x - s, d.y + s, h.getHeight(d.x, d.y));

			gl.glColor4f(r1, g1, b1, 1);
			gl.glVertex3f(d.x, d.y, h.getHeight(d.x, d.y) + 0.05f);
			gl.glColor4f(r2, g2, b2, 0);
			gl.glVertex3f(d.x - s, d.y - s, h.getHeight(d.x, d.y));
			gl.glVertex3f(d.x + s, d.y - s, h.getHeight(d.x, d.y));

			gl.glColor4f(r1, g1, b1, 1);
			gl.glVertex3f(d.x, d.y, h.getHeight(d.x, d.y) + 0.05f);
			gl.glColor4f(r2, g2, b2, 0);
			gl.glVertex3f(d.x - s, d.y + s, h.getHeight(d.x, d.y));
			gl.glVertex3f(d.x + s, d.y + s, h.getHeight(d.x, d.y));

		}
		gl.glEnd();
		gl.glDisable(GL.GL_BLEND);
		gl.glShadeModel(GL.GL_FLAT);
		gl.glEnable(GL.GL_DEPTH_TEST);

		gl.glDepthMask(true);

	}

	public void display2(GL gl) {
		int x, y;
		Vector3 a, b, c, d, e;
		float midHeight;
		for (y = 0; y < h.getBreadth(); y++) {
			for (x = 0; x < h.getWidth(); x++) {

				b = new Vector3(x, y, h.getHeight(x, y) + w[x][y]);

				if (y < h.getBreadth() - 1 && x < h.getWidth() - 1) {
					midHeight = h.getHeight2(x, y);

					gl.glDisable(GL.GL_LIGHTING);
					gl.glEnable(GL.GL_BLEND);
					gl.glShadeModel(GL.GL_SMOOTH);
					gl.glBegin(GL.GL_TRIANGLES);

					a = new Vector3(x + 0.5f, y + 0.5f, midHeight + w2[x][y]);
					// b already set
					c = new Vector3(x + 1, y, h.getHeight(x + 1, y) + w[x + 1][y]);
					d = new Vector3(x + 1, y + 1, h.getHeight(x + 1, y + 1) + w[x + 1][y + 1]);
					e = new Vector3(x, y + 1, h.getHeight(x, y + 1) + w[x][y + 1]);

					gl.glColor4f(0.0f, 0.3f, 0.8f, 0.1f + Math.min(0.9f, w2[x][y]));
					gl.glVertex3f(a.x, a.y, a.z);
					gl.glColor4f(0.0f, 0.3f, 0.8f, 0.1f + Math.min(1, w[x][y]));
					gl.glVertex3f(b.x, b.y, b.z);
					gl.glColor4f(0.0f, 0.3f, 0.8f, 0.1f + Math.min(1, w[x + 1][y]));
					gl.glVertex3f(c.x, c.y, c.z);

					gl.glColor4f(0.0f, 0.3f, 0.8f, 0.1f + Math.min(1, w2[x][y]));
					gl.glVertex3f(a.x, a.y, a.z);
					gl.glColor4f(0.0f, 0.3f, 0.8f, 0.1f + Math.min(1, w[x + 1][y]));
					gl.glVertex3f(c.x, c.y, c.z);
					gl.glColor4f(0.0f, 0.3f, 0.8f, 0.1f + Math.min(1, w[x + 1][y + 1]));
					gl.glVertex3f(d.x, d.y, d.z);

					gl.glColor4f(0.0f, 0.3f, 0.8f, 0.1f + Math.min(1, w2[x][y]));
					gl.glVertex3f(a.x, a.y, a.z);
					gl.glColor4f(0.0f, 0.3f, 0.8f, 0.1f + Math.min(1, w[x + 1][y + 1]));
					gl.glVertex3f(d.x, d.y, d.z);
					gl.glColor4f(0.0f, 0.3f, 0.8f, 0.1f + Math.min(1, w[x][y + 1]));
					gl.glVertex3f(e.x, e.y, e.z);

					gl.glColor4f(0.0f, 0.3f, 0.8f, 0.1f + Math.min(1, w2[x][y]));
					gl.glVertex3f(a.x, a.y, a.z);
					gl.glColor4f(0.0f, 0.3f, 0.8f, 0.1f + Math.min(1, w[x][y + 1]));
					gl.glVertex3f(e.x, e.y, e.z);
					gl.glColor4f(0.0f, 0.3f, 0.8f, 0.1f + Math.min(1, w[x][y]));
					gl.glVertex3f(b.x, b.y, b.z);

					gl.glEnd();

					gl.glShadeModel(GL.GL_FLAT);
					gl.glDisable(GL.GL_BLEND);
					gl.glEnable(GL.GL_LIGHTING);

				}
			}
		}

	}

	public void addSpring(int x, int y) {
		// TODO Auto-generated method stub
		springs.add(new Vector2(x, y));
	}
}
