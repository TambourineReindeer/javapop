import java.util.Random;

public class Peon {

	public static HeightMap h;
	public float x;
	public float y;

	private int destX, destY; // destination to walk to.

	private float searchRadius =0;
	
	public static final int ALIVE = 0;
	public static final int DEAD = 1;
	public static final int SETTLED = 3;

	public Peon(float x, float y) {
		this.x = x;
		this.y = y;

	}

	public int step() {
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
				return SETTLED;
			}
		}
		// We're on a hill of some sort. Find a flat place to live.
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

				if (h.isFlat((int) (x + 0.5f + deltax),
						(int) (y + 0.5f + deltay))) {
					destX = (int) (x + 0.5f + deltax);
					destY = (int) (y + 0.5f + deltay);
					searchRadius = 0;
					return;
					
				}

			}
		}
		if(searchRadius>10)
			searchRadius = 0;
	}

}
