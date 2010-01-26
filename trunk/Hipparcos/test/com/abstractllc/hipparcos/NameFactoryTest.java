package com.abstractllc.hipparcos;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import com.abstractllc.hipparcos.NameFactory;
import java.io.File;
import java.io.InputStreamReader;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author gef
 */
public class NameFactoryTest {

    public NameFactoryTest() {
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

    @After
    public void tearDown() {
    }

    /**
     * Test of getName method, of class NameFactory.
     */
    @Test
    public void testGetName() {
        System.out.println("getName");
        int ulKey = 0;
        NameFactory instance = new NameFactory(new InputStreamReader(getClass().getResourceAsStream("/com/abstractllc/hipparcos/data/PlanetNames.txt")));
        String result = instance.getName(ulKey);
        assertNotNull(result);
        assertTrue(result.length()>0);
        result = instance.getName(3);
        assertEquals(result, "Earth");
        
        System.out.println("Example planet names:");
        for(int n=0;n<100;n++)
        {
            System.out.println(instance.getName(n+100));
        }

        ulKey = 0;
        instance = new NameFactory(new InputStreamReader(getClass().getResourceAsStream("/com/abstractllc/hipparcos/data/StarNames.txt")));
        result = instance.getName(ulKey);
        assertEquals(result, "Sol");

        System.out.println("Example star names:");
        for(int n=0;n<100;n++)
        {
            System.out.println(instance.getName(n+100));
        }
    }

}