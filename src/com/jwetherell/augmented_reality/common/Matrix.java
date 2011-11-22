/*
 * Copyright (C) 2010- Peer internet solutions
 * 
 * This file was an original part of mixare.
 * 
 * This program is free software: you can redistribute it and/or modify it 
 * under the terms of the GNU General Public License as published by 
 * the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version. 
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS 
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License 
 * for more details. 
 * 
 * You should have received a copy of the GNU General Public License along with 
 * this program. If not, see <http://www.gnu.org/licenses/>
 */
package com.jwetherell.augmented_reality.common;


/**
 * A Matrix representation which adds many of the mathematical operations involved in Matrices.
 * 
 * This file was adapted from Mixare <http://www.mixare.org/>
 * 
 * @author Daniele Gobbetti <info@mixare.org>
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class Matrix {
    private static final Vector worldUp = new Vector(0, 1, 0);
    private static final Vector dir = new Vector();
    private static final Vector right = new Vector();
    private static final Vector up = new Vector();
    private static final Matrix tmp = new Matrix();

    private static final float[] dirArray = new float[3];
    private static final float[] rightArray = new float[3];
    private static final float[] upArray = new float[3];
    
    private volatile float a1=0f, a2=0f, a3=0f;
    private volatile float b1=0f, b2=0f, b3=0f;
    private volatile float c1=0f, c2=0f, c3=0f;
    
    public Matrix() { }
    
    public Matrix(Matrix m) {
        if (m==null) throw new NullPointerException();

        set(m.a1,m. a2, m.a3, m.b1, m.b2, m.b3, m.c1, m.c2, m.c3);
    }
    
    public synchronized float getA1() {
        return a1;
    }
    public synchronized void setA1(float a1) {
        this.a1 = a1;
    }

    public synchronized float getA2() {
        return a2;
    }
    public synchronized void setA2(float a2) {
        this.a2 = a2;
    }

    public synchronized float getA3() {
        return a3;
    }
    public synchronized void setA3(float a3) {
        this.a3 = a3;
    }

    public synchronized float getB1() {
        return b1;
    }
    public synchronized void setB1(float b1) {
        this.b1 = b1;
    }

    public synchronized float getB2() {
        return b2;
    }
    public synchronized void setB2(float b2) {
        this.b2 = b2;
    }

    public synchronized float getB3() {
        return b3;
    }
    public synchronized void setB3(float b3) {
        this.b3 = b3;
    }

    public synchronized float getC1() {
        return c1;
    }
    public synchronized void setC1(float c1) {
        this.c1 = c1;
    }

    public synchronized float getC2() {
        return c2;
    }
    public synchronized void setC2(float c2) {
        this.c2 = c2;
    }

    public synchronized float getC3() {
        return c3;
    }
    public synchronized void setC3(float c3) {
        this.c3 = c3;
    }

    /**
     * Get the Matrix values.
     * 
     *  array[0] = a1
     *  array[1] = a2
     *  array[2] = a3
     *  array[3] = b1
     *  array[4] = b2
     *  array[5] = b3
     *  array[6] = c1
     *  array[7] = c2
     *  array[8] = c3
     * 
     * @param array float[] array of size containing the matrix data.
     */
    public synchronized void get(float[] array) {
        if (array==null || array.length!=9) 
            throw new IllegalArgumentException("get() array must be non-NULL and size of 9");
        
        array[0] = this.a1;
        array[1] = this.a2;
        array[2] = this.a3;

        array[3] = this.b1;
        array[4] = this.b2;
        array[5] = this.b3;

        array[6] = this.c1;
        array[7] = this.c2;
        array[8] = this.c3;
    }
    
    /**
     * Set the Matrix from a given Matrix.
     * @param m Matrix to use values from.
     */
    public void set(Matrix m) {
        if (m==null) throw new NullPointerException();

        set(m.a1,m. a2, m.a3, m.b1, m.b2, m.b3, m.c1, m.c2, m.c3);
    }

    /**
     * Set the Matrix values.
     * 
     *  array[0] = a1
     *  array[1] = a2
     *  array[2] = a3
     *  array[3] = b1
     *  array[4] = b2
     *  array[5] = b3
     *  array[6] = c1
     *  array[7] = c2
     *  array[8] = c3
     * 
     * @param array float[] array of size containing the matrix data.
     */
    public void set(float[] array) {
        if (array==null || array.length!=9) 
            throw new IllegalArgumentException("get() array must be non-NULL and size of 9");
        
        set(array[0], array[1], array[2],
            array[3], array[4], array[5],
            array[6], array[7], array[8]);
    }
    
    /**
     * Set the Matrix values.
     * @param a1 float representing the top row left value.
     * @param a2 float representing the top row middle value.
     * @param a3 float representing the top row right value.
     * @param b1 float representing the middle row left value.
     * @param b2 float representing the middle row left value.
     * @param b3 float representing the middle row left value.
     * @param c1 float representing the bottom row left value. 
     * @param c2 float representing the bottom row left value.
     * @param c3 float representing the bottom row left value.
     */
    public synchronized void set(float a1, float a2, float a3, float b1, float b2, float b3, float c1, float c2, float c3) {
        this.a1 = a1;
        this.a2 = a2;
        this.a3 = a3;

        this.b1 = b1;
        this.b2 = b2;
        this.b3 = b3;

        this.c1 = c1;
        this.c2 = c2;
        this.c3 = c3;
    }

    public void toIdentity() {
        set(1, 0, 0, 0, 1, 0, 0, 0, 1);
    }

    public void toXRot(float angleX) {
        set(  1f, 
                0f, 
                0f, 
                0f, 
                (float) Math.cos(angleX), 
                (float) -Math.sin(angleX), 
                0f, 
                (float) Math.sin(angleX), 
                (float) Math.cos(angleX)
        );
    }

    public void toYRot(float angleY) {
        set(  (float) Math.cos(angleY), 
                0f, (float) 
                Math.sin(angleY), 
                0f, 
                1f, 
                0f, 
                (float) -Math.sin(angleY), 
                0f, 
                (float) Math.cos(angleY)
        );
    }

    public void toZRot(float angleZ) {
        set(  (float) Math.cos(angleZ), 
                (float) -Math.sin(angleZ), 
                0f, 
                (float) Math.sin(angleZ), 
                (float) Math.cos(angleZ), 
                0f, 
                0f, 
                0f, 
                1f
        );
    }

    public void toScale(float scale) {
        set(scale, 0, 0, 0, scale, 0, 0, 0, scale);
    }

    public void toAt(Vector cam, Vector obj) {
        if (cam==null || obj==null) throw new NullPointerException();

        dir.set(0, 0, 0);
        dir.set(obj);
        dir.sub(cam);
        dir.mult(-1f);
        dir.norm();
        dir.get(dirArray);

        right.set(0, 0, 0);
        right.cross(worldUp, dir);
        right.norm();
        right.get(rightArray);

        up.set(0, 0, 0);
        up.cross(dir, right);
        up.norm();
        up.get(upArray);

        set(rightArray[0], rightArray[1], rightArray[2], upArray[0], upArray[1], upArray[2], dirArray[0], dirArray[1], dirArray[2]);
    }

    public synchronized void adj() {
        float a11 = this.a1;
        float a12 = this.a2;
        float a13 = this.a3;

        float a21 = this.b1;
        float a22 = this.b2;
        float a23 = this.b3;

        float a31 = this.c1;
        float a32 = this.c2;
        float a33 = this.c3;

        this.a1 = det2x2(a22, a23, a32, a33);
        this.a2 = det2x2(a13, a12, a33, a32);
        this.a3 = det2x2(a12, a13, a22, a23);

        this.b1 = det2x2(a23, a21, a33, a31);
        this.b2 = det2x2(a11, a13, a31, a33);
        this.b3 = det2x2(a13, a11, a23, a21);

        this.c1 = det2x2(a21, a22, a31, a32);
        this.c2 = det2x2(a12, a11, a32, a31);
        this.c3 = det2x2(a11, a12, a21, a22);
    }

    public void invert() {
        float det = this.det();

        adj();
        mult(1 / det);
    }

    public synchronized void transpose() {
        float a11 = this.a1;
        float a12 = this.a2;
        float a13 = this.a3;

        float a21 = this.b1;
        float a22 = this.b2;
        float a23 = this.b3;

        float a31 = this.c1;
        float a32 = this.c2;
        float a33 = this.c3;

        this.b1 = a12;
        this.a2 = a21;
        this.b3 = a32;
        this.c2 = a23;
        this.c1 = a13;
        this.a3 = a31;

        this.a1 = a11;
        this.b2 = a22;
        this.c3 = a33;
    }

    private float det2x2(float a, float b, float c, float d) {
        return (a * d) - (b * c);
    }

    public synchronized float det() {
        return (this.a1 * this.b2 * this.c3) - (this.a1 * this.b3 * this.c2) - (this.a2 * this.b1 * this.c3) +
        (this.a2 * this.b3 * this.c1) + (this.a3 * this.b1 * this.c2) - (this.a3 * this.b2 * this.c1);
    }

    public synchronized void mult(float c) {
        this.a1 = this.a1 * c;
        this.a2 = this.a2 * c;
        this.a3 = this.a3 * c;

        this.b1 = this.b1 * c;
        this.b2 = this.b2 * c;
        this.b3 = this.b3 * c;

        this.c1 = this.c1 * c;
        this.c2 = this.c2 * c;
        this.c3 = this.c3 * c;
    }

    public synchronized void add(Matrix n) {
        if (n==null) throw new NullPointerException();

        this.a1 += n.a1;
        this.a2 += n.a2;
        this.a3 += n.a3;

        this.b1 += n.b1;
        this.b2 += n.b2;
        this.b3 += n.b3;

        this.c1 += n.c1;
        this.c2 += n.c2;
        this.c3 += n.c3;
    }

    public synchronized void prod(Matrix n) {
        if (n==null) throw new NullPointerException();

        tmp.set(this);
        this.a1 = (tmp.a1 * n.a1) + (tmp.a2 * n.b1) + (tmp.a3 * n.c1);
        this.a2 = (tmp.a1 * n.a2) + (tmp.a2 * n.b2) + (tmp.a3 * n.c2);
        this.a3 = (tmp.a1 * n.a3) + (tmp.a2 * n.b3) + (tmp.a3 * n.c3);

        this.b1 = (tmp.b1 * n.a1) + (tmp.b2 * n.b1) + (tmp.b3 * n.c1);
        this.b2 = (tmp.b1 * n.a2) + (tmp.b2 * n.b2) + (tmp.b3 * n.c2);
        this.b3 = (tmp.b1 * n.a3) + (tmp.b2 * n.b3) + (tmp.b3 * n.c3);

        this.c1 = (tmp.c1 * n.a1) + (tmp.c2 * n.b1) + (tmp.c3 * n.c1);
        this.c2 = (tmp.c1 * n.a2) + (tmp.c2 * n.b2) + (tmp.c3 * n.c2);
        this.c3 = (tmp.c1 * n.a3) + (tmp.c2 * n.b3) + (tmp.c3 * n.c3);
    }

    public synchronized boolean equals(Matrix n) {
        if (n==null) return false;

        if (this.a1 != n.a1) return false;
        if (this.a2 != n.a2) return false;
        if (this.a3 != n.a3) return false;

        if (this.b1 != n.b1) return false;
        if (this.b2 != n.b2) return false;
        if (this.b3 != n.b3) return false;

        if (this.c1 != n.c1) return false;
        if (this.c2 != n.c2) return false;
        if (this.c3 != n.c3) return false;

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized String toString() {
        return "[ (" + this.a1 + "," + this.a2 + "," + this.a3 + ")"+
               " (" + this.b1 + "," + this.b2 + "," + this.b3 + ")"+
               " (" + this.c1 + "," + this.c2 + "," + this.c3 + ") ]";
    }
}
