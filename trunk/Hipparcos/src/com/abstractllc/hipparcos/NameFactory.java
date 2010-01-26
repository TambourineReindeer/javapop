package com.abstractllc.hipparcos;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
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
public class NameFactory {

    public NameFactory(Reader names) {
        givenNames = new HashMap<Integer, String>();
        int id;
        String name;
        try {
            StreamTokenizer st = new StreamTokenizer(names);
            do {
                switch (st.nextToken()) {
                    case StreamTokenizer.TT_NUMBER:
                        id = (int) st.nval;
                        st.nextToken();
                        name = st.sval;
                        learnName(id, name);
                        break;
                    case StreamTokenizer.TT_WORD:
                    case '"':
                        name = st.sval;
                        learnName(-1, name);
                        break;
                    case StreamTokenizer.TT_EOF:
                    case StreamTokenizer.TT_EOL:
                }
            } while (st.ttype != StreamTokenizer.TT_EOF);

        } catch (FileNotFoundException ex) {
            Logger.getLogger(NameFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(NameFactory.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    private int[][][] freq = new int[128][128][128];
    Map<Integer, String> givenNames;
    Random rand = new Random();

    private void learnName(int id, String line) {
        if (id >= 0) {
            givenNames.put(id, line);
        }
        char a = 0, b = 0;
        for (char c : line.toCharArray()) {
            if (c >= '0' && c <= '9') {
                c = '9';
            }
            if (c < 1) {
                c = 1;
            }
            if (c > 126) {
                c = 126;
            }
            freq[a][b][c]++;
            freq[a][b][127]++;
            a = b;
            b = c;
        }
        freq[a][b][0]++;
        freq[a][b][127]++;
    }

    public String getName(int ulKey) {
        if (givenNames.containsKey(ulKey)) {
            return (givenNames.get(ulKey));
        }

        rand.setSeed(ulKey);
        StringBuilder sb = new StringBuilder(80);

        char a = 0, b = 0, r = 0;
        int cumFrq;
        int len = 0;
        do {
            cumFrq = freq[a][b][127];
            cumFrq = 1+rand.nextInt(cumFrq);
            for (r = 0; r < 127; r++) {
                cumFrq -= freq[a][b][r];
                if (cumFrq <= 0) {
                    break;
                }
            }
            a = b;
            b = r;
            if (r == '9') {
                r = (char) ('0' + rand.nextInt(10));
            }
            sb.append(r);
            len++;


        } while (r > 0 && len < 80);
        return sb.toString();
    }
}
