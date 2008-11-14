package com.novusradix.JavaPop;


import javax.media.opengl.GLCapabilities;
import javax.swing.JFrame;
public class JavaPop {


	public static void main(final String[] args) {
            
                
		HeightMap h = new HeightMap(128, 128);
                GLCapabilities caps = new GLCapabilities();
		caps.setSampleBuffers(true);
		caps.setNumSamples(8);
                
                JFrame f = new JFrame();
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                f.setSize(1024, 768);
                f.setTitle("JavaPop");
                MainCanvas c = new MainCanvas(h, caps);
                
                f.add(c);
                
                
                f.setVisible(true);
               
                ControlFrame cf = new ControlFrame();
           
                cf.setVisible(true);
        }
	
}
