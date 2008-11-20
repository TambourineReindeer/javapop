/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novusradix.JavaPop.Client;

import com.novusradix.JavaPop.Messaging.GameStarted;
import javax.media.opengl.GLCapabilities;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.ListModel;

/**
 *
 * @author mom
 */
public class Game {

    public HeightMap h;
 
    public Game(GameStarted g) {
        h = new HeightMap(g.gi.mapSize);
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
        cf.setBounds(1024, 0, cf.getWidth(), cf.getHeight());
        cf.setVisible(true); 
    }
 }
