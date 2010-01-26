/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.abstractllc.hipparcos;

import com.abstractllc.hipparcos.GLHelper.GLHelperException;
import com.sun.opengl.util.BufferUtil;
import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;


import java.awt.event.MouseWheelListener;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.opengl.DebugGL;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;

import javax.vecmath.Vector3f;
import static java.awt.event.KeyEvent.*;
import static javax.media.opengl.GL.*;

/**
 *
 * @author gef
 */
public class MainCanvas extends GLCanvas implements GLEventListener, KeyListener, MouseWheelListener, MouseMotionListener, MouseListener {

    HipparcosInstance hi;
    private boolean[] keys;
    private GalaxyRenderer galaxyRenderer;
    private Robot robot;
    private Point screenOrigin;
    private boolean mouseGrabbed;
    private int mousedX, mousedY, mouseX, mouseY;
    private Vector3f forward, up, right, position;
    private int[] mainFBOs;
    private int[] mainTextures;
    private int[] bloomFBOs;
    private int[] bloomTextures;
    private int[] luminanceDownsampleFBOs;
    private int[] luminanceDownsampleTextures;
    private int width, height;
    static final private int bloomBufferMax = 1;
    private int luminanceDownsampleBufferCount;
    private int bloomBufferCount = 0;
    private int downsampleShader, downsampleLogShader, toneMapShader, brightPassShader, filter5shader;
    private float exposure = 1;
    private float targetLuminance;
    private FloatBuffer luminancePixels;
    private int luminancePixelsWidth, luminancePixelsHeight;

    public MainCanvas(GLCapabilities caps, HipparcosInstance hi) {
        super(caps);

        this.hi = hi;
        galaxyRenderer = new GalaxyRenderer(hi);
        keys = new boolean[0x20e];
        mainFBOs = new int[1];
        mainTextures = new int[1];
        bloomFBOs = new int[bloomBufferMax];
        bloomTextures = new int[bloomBufferMax];
        addGLEventListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
        addMouseWheelListener(this);
        try {
            robot = new Robot();
        } catch (AWTException ex) {
            robot = null;
        }
        mouseGrabbed = false;
        forward = new Vector3f(new float[]{0, 0, -1});
        up = new Vector3f(new float[]{0, 1, 0});
        right = new Vector3f(new float[]{1, 0, 0});
        position = new Vector3f();
        targetLuminance = 0.01f;
    }

    public void init(GLAutoDrawable glDrawable) {
        glDrawable.setGL(new DebugGL(glDrawable.getGL()));

        final GL gl = glDrawable.getGL();
        Logger.getLogger(MainCanvas.class.getName()).log(Level.INFO, "GL Init");
        GLHelper.glHelper.init(gl);
        try {
            gl.setSwapInterval(1);
            GLHelper.glHelper.checkGL(gl);
            galaxyRenderer.init(gl);
        } catch (GLHelperException ex) {
            Logger.getLogger(MainCanvas.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            downsampleShader = GLHelper.glHelper.LoadShaderProgram(gl, null, "/com/abstractllc/hipparcos/shaders/DownsampleX2.shader");
            //downsampleLogShader = GLHelper.glHelper.LoadShaderProgram(gl, null, "/com/abstractllc/hipparcos/shaders/DownsampleLog.shader");
            toneMapShader = GLHelper.glHelper.LoadShaderProgram(gl, null, "/com/abstractllc/hipparcos/shaders/tonemap.shader");
            brightPassShader = GLHelper.glHelper.LoadShaderProgram(gl, null, "/com/abstractllc/hipparcos/shaders/tonemap.shader");
            filter5shader = GLHelper.glHelper.LoadShaderProgram(gl, null, "/com/abstractllc/hipparcos/shaders/5filter.shader");

        } catch (IOException ex) {
            Logger.getLogger(GalaxyRenderer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GLHelperException ex) {
            Logger.getLogger(GalaxyRenderer.class.getName()).log(Level.SEVERE, null, ex);
        }
        gl.glGenTextures(bloomBufferMax, bloomTextures, 0);
        gl.glGenFramebuffersEXT(bloomBufferMax, bloomFBOs, 0);
        gl.glGenTextures(1, mainTextures, 0);
        gl.glGenFramebuffersEXT(1, mainFBOs, 0);
        //luminance downsample textures and fbos are generated in reshape();



    }

    public void display(GLAutoDrawable glAD) {

        pollMouse();
        moveCamera();
        GL gl = glAD.getGL();
        try {
            //Draw to floating point texture
            gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, mainFBOs[0]);

            GLHelper.glHelper.checkGL(gl);
            gl.glBlendFunc(GL.GL_ONE, GL.GL_ZERO); //Replace
            gl.glEnable(GL_BLEND);
            gl.glClearColor(0f, 0f, 0f, 0f);
            gl.glClear(GL.GL_COLOR_BUFFER_BIT);
            setViewMatrix(gl);
            
            
        galaxyRenderer.display(gl, exposure);

            //Set matrices for full screen quad rendering
            gl.glMatrixMode(GL.GL_MODELVIEW);
            gl.glPushMatrix();
            gl.glLoadIdentity();
            gl.glMatrixMode(GL.GL_PROJECTION);
            gl.glPushMatrix();
            gl.glLoadIdentity();
            gl.glEnable(GL.GL_TEXTURE_2D);

            float lum = getSceneLuminance(gl);
            float targetExposure = targetLuminance / lum;
            exposure = 0.99f * exposure + 0.01f * targetExposure;
            if (exposure < 0.02f) {
                exposure = 0.02f;
            }
            if (exposure > 5f) {
                exposure = 5f;
            }
            System.out.println(lum + ": " + exposure);

            drawBloom(gl);

            composeScene(gl);
            gl.glDisable(GL_TEXTURE_2D);
            gl.glMatrixMode(GL.GL_PROJECTION);
            gl.glPopMatrix();
            gl.glMatrixMode(GL.GL_MODELVIEW);
            gl.glPopMatrix();

            gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
            gl.glFlush();

        } catch (GLHelper.GLHelperException glhe) {
            Logger.getLogger(MainCanvas.class.getName()).log(Level.SEVERE, null, glhe);
        }

    }

    /**
     * Called when the display changes shape
     * @param glDrawable
     * @param x
     * @param y
     * @param w
     * @param h
     */
    public void reshape(final GLAutoDrawable glDrawable, final int x, final int y, final int w, int h) {
        Logger.getLogger(MainCanvas.class.getName()).log(Level.INFO, "GL Reshape");
        width = w;
        height = h;
        float fov = 1.0f;
        float aspect = (float) w / (float) h;
        float far = 10000f, near = 0.001f;
        final GL gl = glDrawable.getGL();


        if (h <= 0) // avoid a divide by zero error!
        {
            h = 1;
        }
        gl.glViewport(0, 0, w, h); //strictly unneccesary as the component calls this automatically
        gl.glMatrixMode(GL.GL_PROJECTION);
        //Standard perspective projection
        gl.glLoadMatrixf(new float[]{fov, 0, 0, 0,
                    0, fov * aspect, 0, 0,
                    0, 0, (far + near) / (near - far), -1,
                    0, 0, (2 * far * near) / (near - far), 0}, 0);

        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();

        //Main floating point render target
        gl.glBindTexture(GL.GL_TEXTURE_2D, mainTextures[0]);
        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGB16F_ARB, width, height, 0, GL.GL_RGBA, GL.GL_HALF_FLOAT_ARB, null);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
        gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);

        gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, mainFBOs[0]);
        gl.glFramebufferTexture2DEXT(GL.GL_FRAMEBUFFER_EXT, GL.GL_COLOR_ATTACHMENT0_EXT, GL.GL_TEXTURE_2D, mainTextures[0], 0);

        //Floating point luminance downsampling buffers
        setupLuminanceDownsampleBuffers(gl);
        setupBloomBuffers(gl);
        gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, 0);

    }

    public void displayChanged(GLAutoDrawable arg0, boolean arg1, boolean arg2) {
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(final KeyEvent e) {
        switch (e.getKeyCode()) {
            case VK_F:
                hi.mf.setFullscreen(!hi.mf.fullscreen);
                grabMouse();
                break;
            case VK_ESCAPE:
                hi.mf.setFullscreen(false);
                releaseMouse();
                break;
        }
        if (e.getKeyCode() < 0x20e) {
            keys[e.getKeyCode()] = true;
        }
    }

    public void keyReleased(final KeyEvent e) {
        if (e.getKeyCode() < 0x20e) {
            keys[e.getKeyCode()] = false;
        }
    }

    public void mouseDragged(MouseEvent e) {
    }

    public void mouseMoved(MouseEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
        grabMouse();
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseWheelMoved(MouseWheelEvent e) {
    }

    private void composeScene(GL gl) {
        //Draw to default screen buffer
        gl.glViewport(0, 0, width, height);
        gl.glUseProgram(toneMapShader);
        GLHelper.glHelper.setShaderUniform(gl, toneMapShader, "tex", 0);
        GLHelper.glHelper.setShaderUniform(gl, toneMapShader, "exposure", exposure);
        gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, 0);
        gl.glClearColor(0f, 0f, 0f, 0f);
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glDisable(GL_BLEND);
        gl.glBindTexture(GL.GL_TEXTURE_2D, mainTextures[0]);
        gl.glBegin(GL.GL_QUADS);
        {
            gl.glTexCoord2f(0, 0);
            gl.glVertex2f(-1, -1);
            gl.glTexCoord2f(1, 0);
            gl.glVertex2f(1, -1);
            gl.glTexCoord2f(1, 1);
            gl.glVertex2f(1, 1);
            gl.glTexCoord2f(0, 1);
            gl.glVertex2f(-1, 1);
        }
        gl.glEnd();

        gl.glUseProgram(0);

        gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE); //Additive
        gl.glEnable(GL_BLEND);
        for (int i = 0; i < bloomBufferCount; i++) {
            gl.glBindTexture(GL.GL_TEXTURE_2D, bloomTextures[i]);
            gl.glBegin(GL.GL_QUADS);
            {
                gl.glTexCoord2f(0, 0);
                gl.glVertex2f(-1, -1);
                gl.glTexCoord2f(1, 0);
                gl.glVertex2f(1, -1);
                gl.glTexCoord2f(1, 1);
                gl.glVertex2f(1, 1);
                gl.glTexCoord2f(0, 1);
                gl.glVertex2f(-1, 1);
            }
            gl.glEnd();
        }
    }

    private void drawFullscreenQuad(GL gl) {
        gl.glBegin(GL.GL_QUADS);
        {
            gl.glTexCoord2f(0, 0);
            gl.glVertex2f(-1, -1);
            gl.glTexCoord2f(1, 0);
            gl.glVertex2f(1, -1);
            gl.glTexCoord2f(1, 1);
            gl.glVertex2f(1, 1);
            gl.glTexCoord2f(0, 1);
            gl.glVertex2f(-1, 1);
        }
        gl.glEnd();
    }

    private float getSceneLuminance(GL gl) throws GLHelperException {

        gl.glDisable(GL_BLEND);
        /*
        gl.glUseProgram(downsampleLogShader);
        GLHelper.glHelper.setShaderUniform(gl, downsampleShader, "tex", 0);
         * */
        gl.glUseProgram(downsampleShader);

        GLHelper.glHelper.setShaderUniform(gl, downsampleShader, "tex", 0);
        for (int i = 0; i < luminanceDownsampleBufferCount; i++) {
            if (i == 1) {
                gl.glUseProgram(downsampleShader);
                GLHelper.glHelper.setShaderUniform(gl, downsampleShader, "tex", 0);
            }
            gl.glViewport(0, 0, width >> (i + 1), height >> (i + 1));
            if (i == 0) {
                gl.glBindTexture(GL.GL_TEXTURE_2D, mainTextures[0]);
            } else {
                gl.glBindTexture(GL.GL_TEXTURE_2D, luminanceDownsampleTextures[i - 1]);
            }
            gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, luminanceDownsampleFBOs[i]);
            gl.glUseProgram(downsampleShader);

            GLHelper.glHelper.setShaderUniform(gl, downsampleShader, "texelX", 1f / (width >> (i + 1)));
            GLHelper.glHelper.setShaderUniform(gl, downsampleShader, "texelY", 1f / (height >> (i + 1)));

            gl.glBegin(GL.GL_QUADS);
            {
                gl.glTexCoord2f(0, 0);
                gl.glVertex2f(-1, -1);
                gl.glTexCoord2f(1, 0);
                gl.glVertex2f(1, -1);
                gl.glTexCoord2f(1, 1);
                gl.glVertex2f(1, 1);
                gl.glTexCoord2f(0, 1);
                gl.glVertex2f(-1, 1);
            }
            gl.glEnd();
        }

        float averageLuminance = 0;
        luminancePixels.clear();

        gl.glReadPixels(0, 0, luminancePixelsWidth, luminancePixelsHeight, GL_RGB, GL_FLOAT, luminancePixels);
        for (int i = 0; i < luminancePixels.capacity(); i++) {
            averageLuminance += luminancePixels.get();
        }

        return averageLuminance / (luminancePixelsWidth * luminancePixelsHeight);

    }

    private void pollMouse() {
        screenOrigin = getLocationOnScreen();
        if (mouseGrabbed) {
            screenOrigin.x += getWidth() / 2;
            screenOrigin.y += getHeight() / 2;
            mousedX = MouseInfo.getPointerInfo().getLocation().x - screenOrigin.x;
            mousedY = MouseInfo.getPointerInfo().getLocation().y - screenOrigin.y;
            robot.mouseMove(screenOrigin.x, screenOrigin.y);
            mouseX += mousedX;
            mouseY += mousedY;
        } else {
            mouseX = MouseInfo.getPointerInfo().getLocation().x - screenOrigin.x;
            mouseY = MouseInfo.getPointerInfo().getLocation().y - screenOrigin.y;
        }
    }

    public void grabMouse() {
        mouseGrabbed = true;
        screenOrigin = getLocationOnScreen();
        screenOrigin.x += getWidth() / 2;
        screenOrigin.y += getHeight() / 2;
        robot.mouseMove(screenOrigin.x, screenOrigin.y);
    }

    public void releaseMouse() {
        mouseGrabbed = false;
        mousedX = mousedY = 0;
    }

    private void setViewMatrix(GL gl) {
        float x, y, z;
        x = right.dot(position);
        y = up.dot(position);
        z = forward.dot(position);


        gl.glMatrixMode(GL.GL_MODELVIEW);
        FloatBuffer view = BufferUtil.newFloatBuffer(16);
        view.put(right.x);
        view.put(up.x);
        view.put(-forward.x);
        view.put(0);
        view.put(right.y);
        view.put(up.y);
        view.put(-forward.y);
        view.put(0);
        view.put(right.z);
        view.put(up.z);
        view.put(-forward.z);
        view.put(0);
        view.put(-x);
        view.put(-y);
        view.put(z);
        view.put(1);
        view.flip();
        gl.glLoadMatrixf(view);
    }

    private void moveCamera() {
        forward.scaleAdd(0.001f * mousedX, right, forward);
        forward.normalize();
        right.cross(forward, up);

        forward.scaleAdd(-0.001f * mousedY, up, forward);
        forward.normalize();
        up.cross(right, forward);
        float speed;

        if (keys[VK_SHIFT]) {
            speed = 1.0f;
        } else {
            speed = 0.1f;
        }

        if (keys[VK_W]) {
            position.scaleAdd(speed, forward, position);
        }
        if (keys[VK_S]) {
            position.scaleAdd(-speed, forward, position);
        }
        if (keys[VK_A]) {
            position.scaleAdd(-speed, right, position);
        }
        if (keys[VK_D]) {
            position.scaleAdd(speed, right, position);
        }
        if (keys[VK_H]) {
            position.set(0, 0, 0);
        }
        if (keys[VK_UP]) {
            targetLuminance *= 1.01;
        }
        if (keys[VK_DOWN]) {
            targetLuminance /= 1.01;
        }
    }

    private void setupBloomBuffers(final GL gl) {
        for (int i = 0; i < bloomBufferMax; i++) {
            if (width >> (i + 1) <= 0 || height >> (i + 1) <= 0) {
                break;
            }
            gl.glBindTexture(GL.GL_TEXTURE_2D, bloomTextures[i]);
            gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGB16F_ARB, width >> (i + 1), height >> (i + 1), 0, GL.GL_RGB, GL.GL_HALF_FLOAT_ARB, null);
            gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
            gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
            gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
            gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);
            bloomBufferCount = i + 1;
        }
        gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
        try {
            for (int i = 0; i < bloomBufferCount; i++) {
                gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, bloomFBOs[i]);
                gl.glFramebufferTexture2DEXT(GL.GL_FRAMEBUFFER_EXT, GL.GL_COLOR_ATTACHMENT0_EXT, GL.GL_TEXTURE_2D, bloomTextures[i], 0);
                GLHelper.glHelper.checkGLFrameBuffer(gl);
            }
        } catch (GLHelperException ex) {
            Logger.getLogger(MainCanvas.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void setupLuminanceDownsampleBuffers(final GL gl) {
        int Log2width = 0;
        int Log2height = 0;
        int t;
        t = width;
        while ((t >>= 1) != 0) {
            Log2width++;
        }
        t = height;
        while ((t >>= 1) != 0) {
            Log2height++;
        }
        t = luminanceDownsampleBufferCount;
        luminanceDownsampleBufferCount = (Log2height < Log2width) ? Log2height : Log2width - 1;
        luminancePixelsWidth = width >> luminanceDownsampleBufferCount;
        luminancePixelsHeight = height >> luminanceDownsampleBufferCount;
        luminancePixels = BufferUtil.newFloatBuffer(luminancePixelsWidth * luminancePixelsHeight * 3);
        if (t != luminanceDownsampleBufferCount) {
            //number of buffers required has changed, regen texture and fbo names.
            if (luminanceDownsampleTextures != null) {
                gl.glDeleteTextures(luminanceDownsampleTextures.length, luminanceDownsampleTextures, 0);
            }
            luminanceDownsampleTextures = new int[luminanceDownsampleBufferCount];
            gl.glGenTextures(luminanceDownsampleBufferCount, luminanceDownsampleTextures, 0);
            if (luminanceDownsampleFBOs != null) {
                gl.glDeleteFramebuffersEXT(luminanceDownsampleFBOs.length, luminanceDownsampleFBOs, 0);
            }
            luminanceDownsampleFBOs = new int[luminanceDownsampleBufferCount];
            gl.glGenFramebuffersEXT(luminanceDownsampleBufferCount, luminanceDownsampleFBOs, 0);
        }
        for (int i = 0; i < luminanceDownsampleBufferCount; i++) {
            gl.glBindTexture(GL.GL_TEXTURE_2D, luminanceDownsampleTextures[i]);
            gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGB16F_ARB, width >> (i + 1), height >> (i + 1), 0, GL.GL_RGB, GL.GL_HALF_FLOAT_ARB, null);
            gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
            gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
            gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
            gl.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);
        }
        try {
            for (int i = 0; i < luminanceDownsampleBufferCount; i++) {
                gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, luminanceDownsampleFBOs[i]);
                gl.glFramebufferTexture2DEXT(GL.GL_FRAMEBUFFER_EXT, GL.GL_COLOR_ATTACHMENT0_EXT, GL.GL_TEXTURE_2D, luminanceDownsampleTextures[i], 0);
                GLHelper.glHelper.checkGLFrameBuffer(gl);
            }
        } catch (GLHelperException ex) {
            Logger.getLogger(MainCanvas.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void drawBloom(GL gl) {
        gl.glUseProgram(brightPassShader);
        gl.glDisable(GL_BLEND);
        GLHelper.glHelper.setShaderUniform(gl, brightPassShader, "exposure", exposure*4f);
        GLHelper.glHelper.setShaderUniform(gl, brightPassShader, "tex", 0);
        gl.glViewport(0, 0, width >> (1), height >> (1));

        gl.glBindTexture(GL.GL_TEXTURE_2D, luminanceDownsampleTextures[0]);
        gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, bloomFBOs[0]);
        drawFullscreenQuad(gl);

        gaussianX(gl, bloomTextures[0], luminanceDownsampleFBOs[0]);
        gaussianX(gl, luminanceDownsampleTextures[0], bloomFBOs[0]);
        
        gaussianY(gl, bloomTextures[0], luminanceDownsampleFBOs[0]);
        gaussianY(gl, luminanceDownsampleTextures[0], bloomFBOs[0]);



    }

    private void gaussianX(GL gl, int sourceTexture, int destFBO) {
        gl.glUseProgram(filter5shader);
        gl.glDisable(GL_BLEND);
        GLHelper.glHelper.setShaderUniform(gl, brightPassShader, "source", 0);
        gl.glBindTexture(GL.GL_TEXTURE_2D, sourceTexture);
        gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, destFBO);
        float texelWidth = 1.0f / (width >> 1);
        float[] offsets = new float[]{-2 * texelWidth, 0, -1 * texelWidth, 0, 0, 0, 1 * texelWidth, 0, 2 * texelWidth, 0};
        float gauss = 1.0f / (1 + 4 + 6 + 4 + 1);
        float[] coefficients = new float[]{1 * gauss, 4 * gauss, 6 * gauss, 4 * gauss, 1 * gauss};
        GLHelper.glHelper.setShaderUniform1fv(gl, filter5shader, "coefficients", coefficients);
        GLHelper.glHelper.setShaderUniform2fv(gl, filter5shader, "offsets", offsets);
        drawFullscreenQuad(gl);
    }

    private void gaussianY(GL gl, int sourceTexture, int destFBO) {
        gl.glUseProgram(filter5shader);
        gl.glDisable(GL_BLEND);
        GLHelper.glHelper.setShaderUniform(gl, brightPassShader, "source", 0);
        gl.glBindTexture(GL.GL_TEXTURE_2D, sourceTexture);
        gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, destFBO);
        float texelHeight = 1.0f / (height >> 1);
        float[] offsets = new float[]{0, -2 * texelHeight, 0, -1 * texelHeight, 0, 0, 0, 1 * texelHeight, 0, 2 * texelHeight};
        float gauss = 1.0f / (1 + 4 + 6 + 4 + 1);
        float[] coefficients = new float[]{1 * gauss, 4 * gauss, 6 * gauss, 4 * gauss, 1 * gauss};
        GLHelper.glHelper.setShaderUniform1fv(gl, filter5shader, "coefficients", coefficients);
        GLHelper.glHelper.setShaderUniform2fv(gl, filter5shader, "offsets", offsets);
        drawFullscreenQuad(gl);
    }
}
