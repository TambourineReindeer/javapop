/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novusradix.JavaPop.Client;

import javax.media.opengl.GLCapabilities;
import javax.swing.JFrame;

import com.novusradix.JavaPop.Messaging.GameStarted;

/**
 *
 * @author mom
 */
public class Game {

    public HeightMap h;
    public Client client;
    
    public Game(GameStarted g, Client c) {
        h = new HeightMap(g.gi.mapSize);
       client = c;
        GLCapabilities caps = new GLCapabilities();
        caps.setSampleBuffers(true);
        caps.setNumSamples(8);
        
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(1024, 768);
        f.setTitle("JavaPop");
        MainCanvas mc = new MainCanvas(h, caps, client);
        
        f.add(mc);
        
        
        f.setVisible(true);
        
        ControlFrame cf = new ControlFrame();
        cf.setBounds(1024, 0, cf.getWidth(), cf.getHeight());
        cf.setVisible(true); 
    }
 }
