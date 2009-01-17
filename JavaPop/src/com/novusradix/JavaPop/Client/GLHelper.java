package com.novusradix.JavaPop.Client;

import com.sun.opengl.util.texture.*;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import static javax.media.opengl.GL.*;

public class GLHelper {

    private Map<String, Texture> textures;

    public GLHelper() {
        }

    public int LoadShaderProgram(GL gl, String vertexSource, String fragmentSource) throws IOException, GLHelperException {
        int v, f;
        String[] strings;
        int shaderprogram = gl.glCreateProgram();
        if (vertexSource != null) {
            v = gl.glCreateShader(GL.GL_VERTEX_SHADER);
            strings = loadResource(vertexSource);
            gl.glShaderSource(v, strings.length, strings, (int[]) null, 0);
            gl.glCompileShader(v);
            gl.glAttachShader(shaderprogram, v);
        }
        if (fragmentSource != null) {
            f = gl.glCreateShader(GL.GL_FRAGMENT_SHADER);
            strings = loadResource(fragmentSource);
            gl.glShaderSource(f, strings.length, strings, (int[]) null, 0);
            gl.glCompileShader(f);
            gl.glAttachShader(shaderprogram, f);
        }
        gl.glLinkProgram(shaderprogram);
        gl.glValidateProgram(shaderprogram);
        gl.glUseProgram(shaderprogram);
        checkGL(gl);
        return shaderprogram;
    }

    public Texture getTexture(GL gl, String textureName) throws IOException {
        if (textures.containsKey(textureName)) {
            return textures.get(textureName);
        }

        Texture tex;
        URL u = getClass().getResource(textureName);
        tex = TextureIO.newTexture(u, true, "png");
        tex.bind();
        tex.setTexParameteri(GL_TEXTURE_WRAP_S, GL_REPEAT);
        tex.setTexParameteri(GL_TEXTURE_WRAP_T, GL_REPEAT);
        textures.put(textureName, tex);
        return tex;
    }

    public void init(GL gl)
    {
        textures = new HashMap<String, Texture>();
    }
    
    public void checkGL(GL gl) throws GLHelperException {
        int e = gl.glGetError();
        if (e != 0) {
            throw new GLHelperException(e);
        }
    }

    private String[] loadResource(String resource) throws IOException {
        BufferedReader brv;
        ArrayList<String> vsrc = new ArrayList<String>();
        String line;
        brv = new BufferedReader(new InputStreamReader(getClass().getResource(resource).openStream()));
        while ((line = brv.readLine()) != null) {
            vsrc.add(line);
        }
        String[] strings = new String[vsrc.size()];
        vsrc.toArray(strings);
        return strings;
    }

    class GLHelperException extends Exception {

        private String message;
        public int code;

        public GLHelperException(GL gl) {
            this(gl.glGetError());
        }

        public GLHelperException(int errorcode) {
            GLU glu = new GLU();
            message = glu.gluErrorString(errorcode);
            code = errorcode;
        }

        @Override
        public String getMessage() {
            return message;
        }
    }
}
