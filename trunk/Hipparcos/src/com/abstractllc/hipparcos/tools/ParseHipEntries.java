/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.abstractllc.hipparcos.tools;

import com.abstractllc.hipparcos.HipEntry;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.StreamTokenizer;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gef
 */
public class ParseHipEntries {

    private static HashMap<Integer, HipEntry> hipEntries;

    public static void main(String[] args) throws FileNotFoundException {
        hipEntries = new HashMap<Integer, HipEntry>();
        StreamTokenizer st = new StreamTokenizer(new InputStreamReader(ParseHipEntries.class.getResourceAsStream("/com/abstractllc/hipparcos/data/hip2.dat")));
        HipEntry h = HipEntry.getSol();
        hipEntries.put(h.hipNo, h);
        try {
            while (true) {

                if (st.nextToken() == StreamTokenizer.TT_EOF) {
                    break;
                }
                st.pushBack();
                h = HipEntry.readFromTokenStream(st);
                if (h != null) {
                    hipEntries.put(h.hipNo, h);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ParseHipEntries.class.getName()).log(Level.SEVERE, null, ex);
        }

        BufferedReader r = new BufferedReader(new InputStreamReader(ParseHipEntries.class.getResourceAsStream("/com/abstractllc/hipparcos/data/hip_sup.dat")));
        String line;
        String[] tokens = new String[3];
        int hNo;
        float vmag;
        try {
            while ((line = r.readLine()) != null) {

                tokens = line.split("\\|", 3);
                hNo = Integer.valueOf(tokens[0].trim());

                h = hipEntries.get(hNo);
                if (h == null) {
                    continue;
                }
                vmag = Float.valueOf(tokens[1].trim());
                h.aMag = (float) (vmag + (5.0 * Math.log10(h.plx) - 10.0));
                h.spectralType = tokens[2].trim();

            }
        } catch (IOException ex) {
            Logger.getLogger(ParseHipEntries.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NumberFormatException nfe) {
            Logger.getLogger(ParseHipEntries.class.getName()).log(Level.SEVERE, null, nfe);
        }
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("HipEntries.bin"));
            oos.writeObject(hipEntries);
            oos.close();
        } catch (IOException ex) {
            Logger.getLogger(ParseHipEntries.class.getName()).log(Level.SEVERE, null, ex);
        }


    }
}


