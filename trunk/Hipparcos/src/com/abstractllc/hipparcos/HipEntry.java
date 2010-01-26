/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.abstractllc.hipparcos;

import java.io.IOException;
import java.io.Serializable;
import java.io.StreamTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gef
 */
public class HipEntry implements Serializable{

    public int hipNo;
    public float x, y, z;
    public float plx;
    public float aMag;
    public float BV;
    public int comp;
    public String spectralType;
    

    private HipEntry() {
    }

    public static HipEntry getSol() {
        HipEntry sol = new HipEntry();
        sol.aMag = 4.83f;
        sol.BV = 0.63f;
        sol.x = sol.y = sol.z = 0;
        sol.comp = 1;
        sol.hipNo = 0;
        return sol;
    }

    public static HipEntry readFromTokenStream(StreamTokenizer tokens) {

        //        Bytes Format Units    Label   Explanations
        //
        //   1-  6  I6    ---      HIP     Hipparcos identifier
        //   8- 10  I3    ---      Sn      [0,159] Solution type new reduction (1)
        //      12  I1    ---      So      [0,5] Solution type old reduction (2)
        //      14  I1    ---      Nc      Number of components
        //  16- 28 F13.10 rad      RArad   Right Ascension in ICRS, Ep=1991.25
        //  30- 42 F13.10 rad      DErad   Declination in ICRS, Ep=1991.25
        //  44- 50  F7.2  mas      Plx     Parallax
        //  52- 59  F8.2  mas/yr   pmRA    Proper motion in Right Ascension
        //  61- 68  F8.2  mas/yr   pmDE    Proper motion in Declination
        //  70- 75  F6.2  mas     e_RArad  Formal error on DErad
        //  77- 82  F6.2  mas     e_DErad  Formal error on DErad
        //  84- 89  F6.2  mas      e_Plx   Formal error on Plx
        //  91- 96  F6.2  mas/yr   e_pmRA  Formal error on pmRA
        //  98-103  F6.2  mas/yr   e_pmDE  Formal error on pmDE
        // 105-107  I3    ---      Ntr     Number of field transits used
        // 109-113  F5.2  ---      F2      Goodness of fit
        // 115-116  I2    %        F1      Percentage rejected data
        // 118-123  F6.1  ---      var     Cosmic dispersion added (stochastic solution)
        // 125-128  I4    ---      ic      Entry in one of the suppl.catalogues
        // 130-136  F7.4  mag      Hpmag   Hipparcos magnitude
        // 138-143  F6.4  mag     e_Hpmag  Error on mean Hpmag
        // 145-149  F5.3  mag      sHp     Scatter of Hpmag
        //     151  I1    ---      VA      [0,2] Reference to variability annex
        // 153-158  F6.3  mag      B-V     Colour index
        // 160-164  F5.3  mag      e_B-V   Formal error on colour index
        // 166-171  F6.3  mag      V-I     V-I colour index
        // 172-276 15F7.2 ---      UW      Upper-triangular weight matrix (G1)

        HipEntry h = new HipEntry();
        double ra, dec, distance;
        try {
            tokens.nextToken();
            h.hipNo = (int) tokens.nval; //HIP
            tokens.nextToken(); //Sn
            tokens.nextToken(); //So
            tokens.nextToken(); //Nc
            h.comp = (int) tokens.nval;
            tokens.nextToken(); //RArad
            ra = tokens.nval;
            tokens.nextToken(); //DErad
            dec = tokens.nval;
            tokens.nextToken();
            h.plx = (float)tokens.nval;  //Plx
            tokens.nextToken(); //pmRA
            tokens.nextToken(); //pmDE
            tokens.nextToken(); //e_RArad
            tokens.nextToken(); //e_DErad
            tokens.nextToken(); //e_Plx
            tokens.nextToken(); //e_pmRA
            tokens.nextToken(); //e_pmDE
            tokens.nextToken(); //Ntr
            tokens.nextToken(); //F2
            tokens.nextToken(); //F1
            tokens.nextToken(); //var
            tokens.nextToken(); //ic
            tokens.nextToken(); //Hpmag
            h.aMag = (float) tokens.nval;
            tokens.nextToken(); //e_Hpmag
            tokens.nextToken(); //sHp
            tokens.nextToken(); //VA
            tokens.nextToken(); //B-V
            h.BV = (float) tokens.nval;
            tokens.nextToken(); //e_B-V
            tokens.nextToken(); //V-I
            for (int n = 0; n < 15; n++) {
                tokens.nextToken(); //UW matrix
            }

            if (h.plx < 0.01) {
                return null;
            }

            distance = 1000.0f / h.plx;                  //Calculate distance
            h.aMag = h.aMag + (float) (5.0 * Math.log10(h.plx) - 10.0);   //Calculate absolute magnitude
            h.x = (float) (distance * Math.sin(-ra) * Math.cos(dec));
            h.y = (float) (distance * Math.sin(dec));
            h.z = -(float) (distance * Math.cos(-ra) * Math.cos(dec));



        } catch (IOException ex) {
            Logger.getLogger(HipEntry.class.getName()).log(Level.SEVERE, null, ex);
        }
        return h;
    }
}
