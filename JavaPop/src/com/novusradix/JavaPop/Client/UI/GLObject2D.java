package com.novusradix.JavaPop.Client.UI;
import javax.media.opengl.GL;
/**
 *
 * @author gef
 */
public interface GLObject2D {
    public void display(GL gl, float time, int screenWidth, int screenHeight);
    public void init(GL gl);
}
