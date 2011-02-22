package org.ec.util;

import java.util.HashMap;

import org.ec.detector.ECSector;

/**
 * Class to transfer from <code>IJK</code> to <code>XYZ</code>.
 * <p>
 * <font size = 1>JSA: Thomas Jefferson National Accelerator Facility<br>
 * This software was developed under a United States Government license,<br>
 * described in the NOTICE file included as part of this distribution.<br>
 * Copyright (c), Feb 22, 2011</font>
 *
 * @author      smancill
 * @version     0.1
 */
public class ECCoordTransfer
{
    // TODO Get this values from geometry
    private static final double D2RAD = Math.PI / 180;
    private static final double BSC_A = 25;

    private HashMap<String, Double> coordinates;


    /**
     * Construct an object to make a coordinates transformation.  The
     * constructor calculate the new coordinates, then with the getters they
     * can be obtained.
     *
     * @param s      the sector
     * @param di     the <code>i</code> coordinate
     * @param dj     the <code>i</code> coordinate
     * @param dk     the <code>i</code> coordinate
     * @param iterr  1 or 2, see {@link #getCoordinate}
     */
    public ECCoordTransfer(ECSector s, double di, double dj, double dk, int iterr)
    {
        double[]   p   = new double[3];
        double[]   pv  = new double[3];
        double[][] rot = new double[3][3];

        rot[0][0] =  Math.cos(BSC_A * D2RAD) * Math.cos(s.getPhi() * D2RAD);
        rot[0][1] = -Math.cos(s.getPhi() * D2RAD);
        rot[0][2] =  Math.sin(BSC_A * D2RAD) * Math.cos(s.getPhi() * D2RAD);
        rot[1][0] =  Math.cos(BSC_A * D2RAD) * Math.sin(s.getPhi() * D2RAD);
        rot[1][1] =  Math.cos(s.getPhi() * D2RAD);
        rot[1][2] =  Math.sin(BSC_A * D2RAD) * Math.sin(s.getPhi() * D2RAD);
        rot[2][0] = -Math.sin(BSC_A * D2RAD);
        rot[2][1] =  0;
        rot[2][2] =  Math.cos(BSC_A * D2RAD);

        p[0] = di;
        p[1] = dj;
        p[2] = dk;

        for (int i = 0; i < 3; i++) {
            pv[i] = 0;
            for (int j = 0; j < 3; j++) {
                pv[i] = pv[i] + rot[i][j] * p[j];
            }
        }

        double dx;
        double dy;
        double dz;

        coordinates = new HashMap<String, Double>();

        if (iterr == 1) {
            dx = pv[0] + s.getOrigins("x");
            dy = pv[1] + s.getOrigins("y");
            dz = pv[2] + s.getOrigins("z");

            coordinates.put("x", dx);
            coordinates.put("y", dy);
            coordinates.put("z", dz);
        } else {
            dx = Math.abs(pv[0]);
            dy = Math.abs(pv[1]);
            dz = Math.abs(pv[2]);

            coordinates.put("dx", dx);
            coordinates.put("dy", dy);
            coordinates.put("dz", dz);
        }
    }


    /**
     * Get one of the axis in the <code>XYZ</code> coordinate.  The axis are
     * represented by a letter.
     * <p>
     * If the <code>iterr</code> parameter in the constructor was 1, then the
     * axis are: <code>"i"</code>, <code>"j"</code> or <code>"k"</code>.
     * <p>
     * If the <code>iterr</code> parameter in the constructor was 2, then the
     * axis are: <code>"di"</code>, <code>"dj"</code> or <code>"dk"</code>.
     *
     * @param axis the desired axis
     * @return     the position in the axis
     */
    public double getCoordinate(String axis)
    {
        if (coordinates.containsKey(axis))
            return coordinates.get(axis);
        else
            return 0.0;
    }
}
