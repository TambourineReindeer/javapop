/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novusradix.JavaPop.Messaging;

import com.novusradix.JavaPop.Client.HeightMap;
import java.awt.Dimension;
import java.awt.Point;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author erinhowie
 */
public class HeightMapUpdateTest {

    public HeightMapUpdateTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    /**
     * Test of execute method, of class HeightMapUpdate.
     */
    @Test
    public void testExecute() {
        System.out.println("execute");
        HeightMapUpdate instance = null;
        com.novusradix.JavaPop.Server.HeightMap serverh;
        com.novusradix.JavaPop.Client.HeightMap clienth;
        serverh = new com.novusradix.JavaPop.Server.HeightMap(128, 128);
        clienth = new com.novusradix.JavaPop.Client.HeightMap(new Dimension(128, 128));

        serverh.up(0, 0);
        serverh.up(0, 0);
        serverh.up(5, 5);
        serverh.up(106, 7);
        serverh.up(106, 7);
        serverh.up(106, 7);
        serverh.up(106, 7);

        serverh.up(112, 25);
        serverh.up(112, 25);

        serverh.up(112, 125);
        serverh.up(112, 125);
        serverh.up(127, 127);
        serverh.up(127, 127);
        serverh.setTexture(new Point(0, 0), 7);
        serverh.setTexture(new Point(127, 127), 7);


        instance = serverh.GetUpdate();
        try {
            FileOutputStream fo;
            fo = new FileOutputStream("HeightMapUpdateTest.dat");
            ObjectOutputStream oos;

            oos = new ObjectOutputStream(fo);


            oos.writeObject(instance);
            oos.close();
            fo.close();

            instance = null;

            FileInputStream fi = new FileInputStream("HeightMapUpdateTest.dat");
            ObjectInputStream ois = new ObjectInputStream(fi);


            instance = (HeightMapUpdate) ois.readObject();
            ois.close();
            fi.close();
        } catch (ClassNotFoundException ex) {
            fail(ex.getLocalizedMessage());
        } catch (FileNotFoundException ex) {
            fail(ex.getLocalizedMessage());
        } catch (IOException ex) {
            fail(ex.getLocalizedMessage());
        }

        instance.clientMap = clienth;
        assertTrue(instance.texture.size() == 2);
        assertTrue(instance.texture.get(new Point(127, 127)) == 7);
        instance.texture = new HashMap<Point, Integer>();
        instance.execute();
        assertTrue(clienth.getHeight(0, 0) == 2);
        assertTrue(clienth.getHeight(5, 5) == 1);
        assertTrue(clienth.getHeight(106, 7) == 4);
        assertTrue(clienth.getHeight(112, 25) == 2);
        assertTrue(clienth.getHeight(112, 125) == 2);
        assertTrue(instance.texture.size() == 2);
        assertTrue(instance.texture.get(new Point(127, 127)) == 7);

    }
}