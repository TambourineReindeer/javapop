import javax.vecmath.Vector3f;

public class Helpers {

	public static float PointLineDistance(Vector3f line1, Vector3f line2, Vector3f point) {
		Vector3f a, b;
		a = new Vector3f();
		b = new Vector3f();
		a.sub(line2, line1);
		b.sub(line1, point);

		b.cross(a, b);
		return b.length() / a.length();
	}
}
