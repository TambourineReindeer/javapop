import java.nio.FloatBuffer;
import java.util.Random;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.vecmath.Point2f;
import javax.vecmath.Vector3f;

import com.sun.opengl.util.BufferUtil;

public class HeightMap {
	private int width, breadth;

	private FloatBuffer b;
	private static int rowstride, tilestride, vertexstride;
	private static final int VX = 0, VY = 1, VZ = 2, NX = 3, NY = 4, NZ = 5;

	/*
	 * b layout: float x,y,z,nx,ny,nz;
	 * 
	 * 12 vertices per tile:
	 * 
	 * 7-------6 9 \ / 4 |\ \ / /| | \ \ / / | | \ 8 / | | 11 5 | | / 2 \ | | /
	 * / \ \ | |/ / \ \| 10/ \ 3 0-------1
	 */

	public HeightMap(int width, int breadth) {

		vertexstride = 6;
		tilestride = 12 * vertexstride;
		rowstride = tilestride * width;

		b = BufferUtil.newFloatBuffer(width * breadth * tilestride);

		this.breadth = breadth;
		this.width = width;
		// h = new int[width][breadth];
		// h2 = new float[width - 1][breadth - 1];
		int x, y;
		for (y = 0; y < breadth; y++) {
			for (x = 0; x < width; x++) {
				// 0
				b.put((float) x);
				b.put((float) y);
				b.put(1.0f);
				b.put(0);
				b.put(0);
				b.put(1);
				// 1
				b.put((float) x + 1);
				b.put((float) y);
				b.put(1.0f);
				b.put(0);
				b.put(0);
				b.put(1);
				// 2
				b.put((float) x + 0.5f);
				b.put((float) y + 0.5f);
				b.put(1.0f);
				b.put(0);
				b.put(0);
				b.put(1);
				// 3
				b.put((float) x + 1);
				b.put((float) y);
				b.put(1.0f);
				b.put(0);
				b.put(0);
				b.put(1);
				// 4
				b.put((float) x + 1);
				b.put((float) y + 1);
				b.put(1.0f);
				b.put(0);
				b.put(0);
				b.put(1);
				// 5
				b.put((float) x + 0.5f);
				b.put((float) y + 0.5f);
				b.put(1.0f);
				b.put(0);
				b.put(0);
				b.put(1);
				// 6
				b.put((float) x + 1);
				b.put((float) y + 1);
				b.put(1.0f);
				b.put(0);
				b.put(0);
				b.put(1);
				// 7
				b.put((float) x);
				b.put((float) y + 1);
				b.put(1.0f);
				b.put(0);
				b.put(0);
				b.put(1);
				// 8
				b.put((float) x + 0.5f);
				b.put((float) y + 0.5f);
				b.put(1.0f);
				b.put(0);
				b.put(0);
				b.put(1);
				// 9
				b.put((float) x);
				b.put((float) y + 1);
				b.put(1.0f);
				b.put(0);
				b.put(0);
				b.put(1);
				// 10
				b.put((float) x);
				b.put((float) y);
				b.put(1.0f);
				b.put(0);
				b.put(0);
				b.put(1);
				// 11
				b.put((float) x + 0.5f);
				b.put((float) y + 0.5f);
				b.put(1.0f);
				b.put(0);
				b.put(0);
				b.put(1);
			}
		}
		b.flip();
		randomize();
	}

	public int getWidth() {
		return width;
	}

	public int getBreadth() {
		return breadth;
	}

	private static int bufPos(int x, int y, int vertex, int index) {
		return y * rowstride + x * tilestride + vertex * vertexstride + index;
	}

	public int getHeight(int x, int y) {
		try {
			return (int) b.get(bufPos(x, y, 0, VZ));
		} catch (Exception e) {
			System.out.println(x);
			System.out.println(y);

		}
		return 0;
	}

	public float getHeight2(int x, int y) {
		try {
			return b.get(bufPos(x, y, 2, VZ));
		} catch (Exception e) {
			System.out.println(x);
			System.out.println(y);

		}
		return 0;
	}

	public float getHeight(float x, float y) {
		int x1, x2, y1, y2;
		float a, b, c, d, m;
		x1 = (int) Math.floor(x);
		x2 = (int) Math.ceil(x);
		y1 = (int) Math.floor(y);
		y2 = (int) Math.ceil(y);

		x = x - x1;
		y = y - y1;

		a = getHeight(x1, y1);
		b = getHeight(x1, y2);
		c = getHeight(x2, y1);
		d = getHeight(x2, y2);
		m = a;
		if (b > a || c > a || d > a)
			m = a + 0.5f;
		if (b < a || c < a || d < a)
			m = a - 0.5f;

		if (y > x) {
			if (y > 1 - x) {
				// BMD
				return b + (d - b) * x + (m - (d + b) / 2.0f) * (1 - y);
			} else {
				// AMB
				return a + (b - a) * y + (m - (b + a) / 2.0f) * x;
			}
		} else {
			if (y > 1 - x) {
				// CMD
				return c + (d - c) * y + (m - (d + c) / 2.0f) * (1 - x);
			} else {
				// AMC
				return a + (c - a) * x + (m - (a + b) / 2.0f) * y;
			}
		}
	}

	public Point2f getSlope(float x, float y) {
		int x1, x2, y1, y2;
		float a, b, c, d, m;
		x1 = (int) Math.floor(x);
		x2 = (int) Math.ceil(x);
		y1 = (int) Math.floor(y);
		y2 = (int) Math.ceil(y);

		x = x - x1;
		y = y - y1;

		a = getHeight(x1, y1);
		b = getHeight(x1, y2);
		c = getHeight(x2, y1);
		d = getHeight(x2, y2);

		if (a == b && b == c && c == d)
			return new Point2f(0, 0);

		m = a;
		if (b > a || c > a || d > a)
			m = a + 0.5f;
		if (b < a || c < a || d < a)
			m = a - 0.5f;

		if (y > x) {
			if (y > 1 - x) {
				// BMD
				return new Point2f(d - b, 2.0f * ((d + b) / 2.0f - m));
			} else {
				// AMB
				return new Point2f(2.0f * (m - (a + b) / 2.0f), b - a);
			}
		} else {
			if (y > 1 - x) {
				// CMD
				return new Point2f(2.0f * ((c + d) / 2.0f - m), d - c);
			} else {
				// AMC
				return new Point2f(c - a, 2.0f * (m - (a + c) / 2.0f));
			}
		}
	}

	public void randomize() {
		int n;
		Random r = new Random();
		for (n = 0; n < 1000; n++) {
			up(r.nextInt(width), r.nextInt(breadth));
			down(r.nextInt(width), r.nextInt(breadth));
		}
	}

	public void up(int x, int y) {
		setHeight(x, y, getHeight(x, y) + 1);
		conform(x, y, getHeight(x, y), 1);
	}

	public void down(int x, int y) {
		setHeight(x, y, Math.max(getHeight(x, y) - 1, 0));
		conform(x, y, getHeight(x, y), 1);
	}

	private void setHeight(int x, int y, int height) {
		if (x >= 0 && y >= 0 && x < width && y < breadth) {
			b.put(bufPos(x, y, 0, VZ), (float) height);
			b.put(bufPos(x, y, 10, VZ), (float) height);
			if (x >= 1 && y >= 1) {
				b.put(bufPos(x - 1, y - 1, 4, VZ), (float) height);
				b.put(bufPos(x - 1, y - 1, 6, VZ), (float) height);
			}
			if (y >= 1) {
				b.put(bufPos(x, y - 1, 7, VZ), (float) height);
				b.put(bufPos(x, y - 1, 9, VZ), (float) height);
			}
			if (x >= 1) {
				b.put(bufPos(x - 1, y, 1, VZ), (float) height);
				b.put(bufPos(x - 1, y, 3, VZ), (float) height);
			}
		}
	}

	private void setMidTile(int x, int y) {
		if (x < 0 || y < 0 || x + 1 >= width || y + 1 >= breadth)
			return;
		float m;
		m = Math.max(Math.max(getHeight(x, y), getHeight(x, y + 1)), Math.max(
				getHeight(x + 1, y), getHeight(x + 1, y + 1)))
				+ Math.min(Math.min(getHeight(x, y), getHeight(x, y + 1)), Math
						.min(getHeight(x + 1, y), getHeight(x + 1, y + 1)));

		b.put(bufPos(x, y, 2, VZ), m * 0.5f);
		b.put(bufPos(x, y, 5, VZ), m * 0.5f);
		b.put(bufPos(x, y, 8, VZ), m * 0.5f);
		b.put(bufPos(x, y, 11, VZ), m * 0.5f);

		setNormals(x, y);

	}

	private void setNormals(int x, int y) {
		setNormals(x, y, 0, 1, 2);
		setNormals(x, y, 3, 4, 5);
		setNormals(x, y, 6, 7, 8);
		setNormals(x, y, 9, 10, 11);
	}

	private void setNormals(int x, int y, int vertA, int vertB, int vertC) {
		Vector3f va, vb, vc, vn;
		va = new Vector3f();
		vb = new Vector3f();
		vc = new Vector3f();

		va.x = b.get(bufPos(x, y, vertA, VX));
		va.y = b.get(bufPos(x, y, vertA, VY));
		va.z = b.get(bufPos(x, y, vertA, VZ));

		vb.x = b.get(bufPos(x, y, vertB, VX));
		vb.y = b.get(bufPos(x, y, vertB, VY));
		vb.z = b.get(bufPos(x, y, vertB, VZ));

		vc.x = b.get(bufPos(x, y, vertC, VX));
		vc.y = b.get(bufPos(x, y, vertC, VY));
		vc.z = b.get(bufPos(x, y, vertC, VZ));

		vn = calcNormal(vc, va, vb);

		b.put(bufPos(x, y, vertA, NX), vn.x);
		b.put(bufPos(x, y, vertA, NY), vn.y);
		b.put(bufPos(x, y, vertA, NZ), vn.z);

		b.put(bufPos(x, y, vertB, NX), vn.x);
		b.put(bufPos(x, y, vertB, NY), vn.y);
		b.put(bufPos(x, y, vertB, NZ), vn.z);

		b.put(bufPos(x, y, vertC, NX), vn.x);
		b.put(bufPos(x, y, vertC, NY), vn.y);
		b.put(bufPos(x, y, vertC, NZ), vn.z);
	}

	private void conform(int x, int y, int height, int radius) {
		int ex, wy;
		boolean bChanged = false;
		for (ex = x - radius; ex <= x + radius; ex++) {
			for (wy = y - radius; wy <= y + radius; wy++) {
				if (ex >= 0 && ex < width && wy >= 0 && wy < breadth)
					if (getHeight(ex, wy) - height > radius) {
						bChanged = true;
						setHeight(ex, wy, height + radius);
					} else if (height - getHeight(ex, wy) > radius) {
						bChanged = true;
						setHeight(ex, wy, height - radius);
					}
			}
		}
		if (bChanged) {
			conform(x, y, height, radius + 1);
		} else {
			for (ex = x - radius; ex <= x + radius; ex++) {
				for (wy = y - radius; wy <= y + radius; wy++) {

					setMidTile(ex, wy);

				}
			}
		}
	}

	public boolean isFlat(int x, int y) {
		int a = 0, b = 0, c = 0, d = 0;
		if (x < 0 || y < 0 || x + 1 >= width || y + 1 >= breadth)
			return false;
		a = getHeight(x, y);
		b = getHeight(x, y + 1);
		c = getHeight(x + 1, y);
		d = getHeight(x + 1, y + 1);
		return (a == b && b == c && c == d);
	}

	public void display(GL gl) {

		Vector3f l = new Vector3f(new float[] { -9, -5, 10 });
		l.normalize();

		gl.glLightfv(GL.GL_LIGHT1, GL.GL_POSITION, FloatBuffer
				.wrap(new float[] { l.x, l.y, l.z, 0.0f }));

		gl.glColor3f(0, 1, 0);

		gl.glDrawArrays(GL.GL_TRIANGLES, 0, width * breadth * 4 * 3);

	}

	private Vector3f calcNormal(final Vector3f a, final Vector3f b,
			final Vector3f c) {
		Vector3f ab, ac, n;
		ab = (Vector3f) b.clone();
		ab.sub(a);
		ac = (Vector3f) c.clone();
		ac.sub(a);

		n = new Vector3f();
		n.cross(ab, ac);
		n.normalize();

		return n;
	}

	public void init(final GLAutoDrawable glDrawable) {
		final GL gl = glDrawable.getGL();
		gl.glEnableClientState(GL.GL_NORMAL_ARRAY);
		gl.glEnableClientState(GL.GL_VERTEX_ARRAY);

		b.position(0);
		gl.glVertexPointer(3, GL.GL_FLOAT, 6 * 4, b);
		b.position(3);
		gl.glNormalPointer(GL.GL_FLOAT, 6 * 4, b);
	}

}