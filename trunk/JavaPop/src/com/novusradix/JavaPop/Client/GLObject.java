package com.novusradix.JavaPop.Client;

import javax.media.opengl.GL;

public interface GLObject {
    public void display(GL gl, float time);
    public void init(GL gl);
}
