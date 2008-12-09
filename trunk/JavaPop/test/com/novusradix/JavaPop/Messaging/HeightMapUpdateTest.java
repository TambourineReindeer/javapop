/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.novusradix.JavaPop.Messaging;

import com.novusradix.JavaPop.Client.HeightMap;
import java.awt.Dimension;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
        serverh = new com.novusradix.JavaPop.Server.HeightMap(16, 16);
        clienth = new com.novusradix.JavaPop.Client.HeightMap(new Dimension(16, 16));

        serverh.up(5, 5);
        serverh.up(7, 7);
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
            Logger.getLogger(HeightMapUpdateTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(HeightMapUpdateTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(HeightMapUpdateTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        instance.clientMap = clienth;
        instance.execute();
        assertTrue(clienth.getHeight(0, 0) == 0);
        assertTrue(clienth.getHeight(5, 5) == 1);
        assertTrue(clienth.getHeight(7, 7) == 1);

        
    }
}