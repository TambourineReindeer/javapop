package com.novusradix.JavaPop.Client;

import java.io.*;
import java.util.ArrayList;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

public class GLHelper {

    public int LoadShaderProgram(GL gl, String vertexSource, String fragmentSource) throws IOException, GLHelperException {
        int v = gl.glCreateShader(GL.GL_VERTEX_SHADER);
        int f = gl.glCreateShader(GL.GL_FRAGMENT_SHADER);   
        
        String[] strings;
        strings = loadResource(vertexSource);
        gl.glShaderSource(v, strings.length, strings, (int[]) null, 0);
        gl.glCompileShader(v);
        
        strings = loadResource(fragmentSource);
        gl.glShaderSource(f, strings.length, strings, (int[]) null, 0);
        gl.glCompileShader(f);

        int shaderprogram = gl.glCreateProgram();
        gl.glAttachShader(shaderprogram, v);
        gl.glAttachShader(shaderprogram, f);
        gl.glLinkProgram(shaderprogram);
        gl.glValidateProgram(shaderprogram);
        gl.glUseProgram(shaderprogram);
        checkGL(gl);
        return shaderprogram;
    }

    public void checkGL(GL gl) throws GLHelperException {
        int e = gl.glGetError();
        if (e != 0) {
            throw new GLHelperException(e);
        }
    }

    private String[] loadResource(String resource) throws IOException
    {
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
