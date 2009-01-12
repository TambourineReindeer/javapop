package com.novusradix.JavaPop.Math;

/**
 *
 * @author gef
 */
public class Plane3 {

    private Vector3 p, n;
    public Plane3(Vector3 point, Vector3 normal)
    {
        p=point;n=normal;
        n.normalize();
    }
    
    public Plane3(Vector3 a, Vector3 b, Vector3 c)
    {
        p=a;
        Vector3 ab, ac;
        ab = new Vector3(b);
        ac = new Vector3(c);
        ab.sub(a);
        ac.sub(a);
        
        n=new Vector3();
        n.cross(ab, ac);
        n.normalize();
    }
    
    public float distance(Vector3 point)
    {
        Vector3 pp = new Vector3(point);
        pp.sub(p);
        return pp.dot(n);
    }
}
