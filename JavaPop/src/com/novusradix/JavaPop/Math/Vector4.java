package com.novusradix.JavaPop.Math;

import java.io.Serializable;

/**
 *
 * @author gef
 */
public class Vector4 implements Serializable {

    public float x,  y,  z,  w;

    public Vector4() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.w = 1;
    }

    public Vector4(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Vector4(Vector4 v) {
        x = v.x;
        y = v.y;
        z = v.z;
        w = v.w;
    }

    public Vector4(Vector3 v) {
        x = v.x;
        y = v.y;
        z = v.z;
        w = 1.0f;
    }

}
