/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novusradix.JavaPop.Math;

import java.io.Serializable;

/**
 *
 * @author mom
 */
public class Matrix4 implements Serializable {

    public static final Matrix4 identity;
    static{
        identity = new Matrix4();
        identity.m[0]=1;
        identity.m[5]=1;
        identity.m[10]=1;
        identity.m[15]=1;
        
    }
    private float[] m;
    
    public Matrix4() {
        m = new float[16];
    }

    public Matrix4(Matrix4 m) {
        this.m = m.m.clone();
    }

    public Matrix4(Vector3 front, Vector3 left, Vector3 up) {
        m = new float[16];
        m[0] = front.x;
        m[1] = front.y;
        m[2] = front.z;
        m[4] = left.x;
        m[5] = left.y;
        m[6] = left.z;
        m[8] = up.x;
        m[9] = up.y;
        m[10] = up.z;
        m[15] = 1;
        
    }

    public float determinant() {
        float f = m[0] * ((m[5] * m[10] * m[15] + m[6] * m[11] * m[13] + m[7] * m[9] * m[14]) - m[7] * m[10] * m[13] - m[5] * m[11] * m[14] - m[6] * m[9] * m[15]);
        f -= m[1] * ((m[4] * m[10] * m[15] + m[6] * m[11] * m[12] + m[7] * m[8] * m[14]) - m[7] * m[10] * m[12] - m[4] * m[11] * m[14] - m[6] * m[8] * m[15]);
        f += m[2] * ((m[4] * m[9] * m[15] + m[5] * m[11] * m[12] + m[7] * m[8] * m[13]) - m[7] * m[9] * m[12] - m[4] * m[11] * m[13] - m[5] * m[8] * m[15]);
        f -= m[3] * ((m[4] * m[9] * m[14] + m[5] * m[10] * m[12] + m[6] * m[8] * m[13]) - m[6] * m[9] * m[12] - m[4] * m[10] * m[13] - m[5] * m[8] * m[14]);
        return f;
    }

    /**
     * Calculate the determinant of a 3x3 matrix
     * @return result
     */
    private static float determinant3x3(float t00, float t01, float t02,
            float t10, float t11, float t12,
            float t20, float t21, float t22) {
        return t00 * (t11 * t22 - t12 * t21) + t01 * (t12 * t20 - t10 * t22) + t02 * (t10 * t21 - t11 * t20);
    }

    public float[] getArray() {
        return m.clone();
    }

    /**
     * Invert this matrix
     * @return this if successful, null otherwise
     */
    public Matrix4 invert() {
        return invert(this, this);
    }

    /**
     * Invert the source matrix and put the result in the destination
     * @param src The source matrix
     * @param dest The destination matrix, or null if a new matrix is to be created
     * @return The inverted matrix if successful, null otherwise
     */
    public static Matrix4 invert(Matrix4 src, Matrix4 dest) {
        float determinant = src.determinant();

        if (determinant != 0) {
            /*
             * m[0] m[1] m[2] m[3]
             * m[4] m[5] m[6] m[7]
             * m[8] m[9] m[10] m[11]
             * m[12] m[13] m[14] m[15]
             */
            if (dest == null) {
                dest = new Matrix4();
            }
            float determinant_inv = 1f / determinant;

            // first row
            float t0 = determinant3x3(src.m[5], src.m[6], src.m[7], src.m[9], src.m[10], src.m[11], src.m[13], src.m[14], src.m[15]);
            float t1 = -determinant3x3(src.m[4], src.m[6], src.m[7], src.m[8], src.m[10], src.m[11], src.m[12], src.m[14], src.m[15]);
            float t2 = determinant3x3(src.m[4], src.m[5], src.m[7], src.m[8], src.m[9], src.m[11], src.m[12], src.m[13], src.m[15]);
            float t3 = -determinant3x3(src.m[4], src.m[5], src.m[6], src.m[8], src.m[9], src.m[10], src.m[12], src.m[13], src.m[14]);
            // second row
            float t4 = -determinant3x3(src.m[1], src.m[2], src.m[3], src.m[9], src.m[10], src.m[11], src.m[13], src.m[14], src.m[15]);
            float t5 = determinant3x3(src.m[0], src.m[2], src.m[3], src.m[8], src.m[10], src.m[11], src.m[12], src.m[14], src.m[15]);
            float t6 = -determinant3x3(src.m[0], src.m[1], src.m[3], src.m[8], src.m[9], src.m[11], src.m[12], src.m[13], src.m[15]);
            float t7 = determinant3x3(src.m[0], src.m[1], src.m[2], src.m[8], src.m[9], src.m[10], src.m[12], src.m[13], src.m[14]);
            // third row
            float t8 = determinant3x3(src.m[1], src.m[2], src.m[3], src.m[5], src.m[6], src.m[7], src.m[13], src.m[14], src.m[15]);
            float t9 = -determinant3x3(src.m[0], src.m[2], src.m[3], src.m[4], src.m[6], src.m[7], src.m[12], src.m[14], src.m[15]);
            float t10 = determinant3x3(src.m[0], src.m[1], src.m[3], src.m[4], src.m[5], src.m[7], src.m[12], src.m[13], src.m[15]);
            float t11 = -determinant3x3(src.m[0], src.m[1], src.m[2], src.m[4], src.m[5], src.m[6], src.m[12], src.m[13], src.m[14]);
            // fourth row
            float t12 = -determinant3x3(src.m[1], src.m[2], src.m[3], src.m[5], src.m[6], src.m[7], src.m[9], src.m[10], src.m[11]);
            float t13 = determinant3x3(src.m[0], src.m[2], src.m[3], src.m[4], src.m[6], src.m[7], src.m[8], src.m[10], src.m[11]);
            float t14 = -determinant3x3(src.m[0], src.m[1], src.m[3], src.m[4], src.m[5], src.m[7], src.m[8], src.m[9], src.m[11]);
            float t15 = determinant3x3(src.m[0], src.m[1], src.m[2], src.m[4], src.m[5], src.m[6], src.m[8], src.m[9], src.m[10]);

            // transpose and divide by the determinant
            dest.m[0] = t0 * determinant_inv;
            dest.m[5] = t5 * determinant_inv;
            dest.m[10] = t10 * determinant_inv;
            dest.m[15] = t15 * determinant_inv;
            dest.m[1] = t4 * determinant_inv;
            dest.m[4] = t1 * determinant_inv;
            dest.m[8] = t2 * determinant_inv;
            dest.m[2] = t8 * determinant_inv;
            dest.m[6] = t9 * determinant_inv;
            dest.m[9] = t6 * determinant_inv;
            dest.m[3] = t12 * determinant_inv;
            dest.m[12] = t3 * determinant_inv;
            dest.m[7] = t13 * determinant_inv;
            dest.m[13] = t7 * determinant_inv;
            dest.m[14] = t11 * determinant_inv;
            dest.m[11] = t14 * determinant_inv;
            return dest;
        } else {
            return null;
        }
    }

    public void mul(Matrix4 a, Matrix4 b) {
        float[] m = new float[16];
        m[0] = a.m[0] * b.m[0] + a.m[1] * b.m[4] + a.m[2] * b.m[8] + a.m[3] * b.m[12];
        m[1] = a.m[0] * b.m[1] + a.m[1] * b.m[5] + a.m[2] * b.m[9] + a.m[3] * b.m[13];
        m[2] = a.m[0] * b.m[2] + a.m[1] * b.m[6] + a.m[2] * b.m[10] + a.m[3] * b.m[14];
        m[3] = a.m[0] * b.m[3] + a.m[1] * b.m[7] + a.m[2] * b.m[11] + a.m[3] * b.m[15];

        m[4] = a.m[4] * b.m[0] + a.m[5] * b.m[4] + a.m[6] * b.m[8] + a.m[7] * b.m[12];
        m[5] = a.m[4] * b.m[1] + a.m[5] * b.m[5] + a.m[6] * b.m[9] + a.m[7] * b.m[13];
        m[6] = a.m[4] * b.m[2] + a.m[5] * b.m[6] + a.m[6] * b.m[10] + a.m[7] * b.m[14];
        m[7] = a.m[4] * b.m[3] + a.m[5] * b.m[7] + a.m[6] * b.m[11] + a.m[7] * b.m[15];

        m[8] = a.m[8] * b.m[0] + a.m[9] * b.m[4] + a.m[10] * b.m[8] + a.m[11] * b.m[12];
        m[9] = a.m[8] * b.m[1] + a.m[9] * b.m[5] + a.m[10] * b.m[9] + a.m[11] * b.m[13];
        m[10] = a.m[8] * b.m[2] + a.m[9] * b.m[6] + a.m[10] * b.m[10] + a.m[11] * b.m[14];
        m[11] = a.m[8] * b.m[3] + a.m[9] * b.m[7] + a.m[10] * b.m[11] + a.m[11] * b.m[15];

        m[12] = a.m[12] * b.m[0] + a.m[13] * b.m[4] + a.m[14] * b.m[8] + a.m[15] * b.m[12];
        m[13] = a.m[12] * b.m[1] + a.m[13] * b.m[5] + a.m[14] * b.m[9] + a.m[15] * b.m[13];
        m[14] = a.m[12] * b.m[2] + a.m[13] * b.m[6] + a.m[14] * b.m[10] + a.m[15] * b.m[14];
        m[15] = a.m[12] * b.m[3] + a.m[13] * b.m[7] + a.m[14] * b.m[11] + a.m[15] * b.m[15];
        
        this.m = m;
    }

    public void set(float[] buf) {
        m = buf.clone();
    }

    public void transform(Vector3 v) {

        float x, y, z, w;
        x = m[0] * v.x + m[1] * v.y + m[2] * v.z + m[3];
        y = m[4] * v.x + m[5] * v.y + m[6] * v.z + m[7];
        z = m[8] * v.x + m[9] * v.y + m[10] * v.z + m[11];
        w = m[12] * v.x + m[13] * v.y + m[14] * v.z + m[15];
        
        v.x = x/w;
        v.y = y/w;
        v.z = z/w;
    }

    public void transpose() {
        float[] buf = new float[16];
        buf[0] = m[0];
        buf[1] = m[4];
        buf[2] = m[8];
        buf[3] = m[12];

        buf[4] = m[1];
        buf[5] = m[5];
        buf[6] = m[9];
        buf[7] = m[13];

        buf[8] = m[2];
        buf[9] = m[6];
        buf[10] = m[10];
        buf[11] = m[14];

        buf[12] = m[3];
        buf[13] = m[7];
        buf[14] = m[11];
        buf[15] = m[15];

        m = buf;
    }
}
