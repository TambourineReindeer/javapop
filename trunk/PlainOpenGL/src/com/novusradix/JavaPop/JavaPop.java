package com.novusradix.JavaPop;

import com.novusradix.JavaPop.Client.PlayerState;

/*import com.novusradix.JavaPop.Client.ControlFrame;
import com.novusradix.JavaPop.Client.HeightMap;
import com.novusradix.JavaPop.Client.MainCanvas;
import java.awt.GridLayout;
import javax.media.opengl.GLCapabilities;
import javax.swing.JFrame;*/


public class JavaPop {

    public static void main(final String[] args) {

        LobbyFrame f = new LobbyFrame();
        
        f.setVisible(true);


        /*HeightMap h = new HeightMap(128, 128);
        GLCapabilities caps = new GLCapabilities();
        caps.setSampleBuffers(true);
        caps.setNumSamples(8);
        
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(1024, 768);
        f.setTitle("JavaPop");
        f.setLayout(new GridLayout());
        MainCanvas c = new MainCanvas(h, caps);
        
        f.add(c);
        
        
        f.setVisible(true);
        
        ControlFrame cf = new ControlFrame();
        cf.setBounds(1024, 0, cf.getWidth(), cf.getHeight());
        cf.setVisible(true);
         */
    }
}
