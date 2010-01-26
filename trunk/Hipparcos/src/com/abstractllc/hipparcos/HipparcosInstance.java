package com.abstractllc.hipparcos;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author gef
 */
public class HipparcosInstance {

    NameFactory planetNameFactory;
    NameFactory starNameFactory;
    Map<Integer, HipEntry> hipEntries;
    public MainFrame mf;

    public HipparcosInstance() {
        planetNameFactory = new NameFactory(new InputStreamReader(getClass().getResourceAsStream("/com/abstractllc/hipparcos/data/PlanetNames.txt")));
        starNameFactory = new NameFactory(new InputStreamReader(getClass().getResourceAsStream("/com/abstractllc/hipparcos/data/StarNames.txt")));
        try {
            ObjectInputStream ois = new ObjectInputStream(getClass().getResourceAsStream("/com/abstractllc/hipparcos/data/HipEntries.bin"));
            hipEntries = (Map<Integer, HipEntry>) ois.readObject();
            ois.close();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(HipparcosInstance.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(HipparcosInstance.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        HipparcosInstance hi = new HipparcosInstance();
        hi.mf = new MainFrame(hi);
        hi.mf.setVisible(true);

    }
}
