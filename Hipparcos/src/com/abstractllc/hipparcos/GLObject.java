/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.abstractllc.hipparcos;

import javax.media.opengl.GL;
/**
 * A simple interface for managing any objects that need to be drawn with openGL.
 * The application keeps track of these objects and then typically iterates over them in the GLDrawable implementation.
 * @author erinhowie
 */
public interface GLObject {
    public void display(GL gl, float time);
    public void init(GL gl);
}
