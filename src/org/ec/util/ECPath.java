package org.ec.util;

import org.ec.detector.ECLayer;
import org.ec.detector.ECViewLabel;

/**
 * Class to find the distance parallel to strips to edge from
 * <code>(i,j)</code>.
 * <p>
 * <font size = 1>JSA: Thomas Jefferson National Accelerator Facility<br>
 * This software was developed under a United States Government license,<br>
 * described in the NOTICE file included as part of this distribution.<br>
 * Copyright (c), Feb 22, 2011</font>
 *
 * @author      smancill
 * @version     0.1
 */
public class ECPath
{

    private double u;
    private double v;
    private double w;


    /**
     * Construc an object to calculate the path length for each axis.
     *
     * @param layer  the layer of the sector
     * @param i      the <code>i</code> coordinate
     * @param j      the <code>j</code> coordinate
     */
    public ECPath(ECLayer layer, double i, double j)
    {
        double lu = layer.getEdgeL(ECViewLabel.U);
        double lv = layer.getEdgeL(ECViewLabel.V);
        double lw = layer.getEdgeL(ECViewLabel.W);

        double h  = Math.sqrt(lu * lu - lv * lv / 4);
        double h1 = layer.getH1();

        double du = (i / h + h1 / h) * lu;
        double dv = lv - h1 / 2. / h * lv - j - lv / 2. / h * i;
        double dw = lw / lv * j - lw / 2. / h * i - h1 / 2. / h * lw + lw;

        u = du / lu * lv - (lv- dv);
        v = dv / lv * lw - (lw - dw);
        w = dw / lw * lu - (lu - du);
    }


    /**
     * Get the path length in the U axis
     *
     * @return  the path length
     */
    public double getU()
    {
        return u;
    }


    /**
     * Get the path length in the V axis
     *
     * @return  the path length
     */
    public double getV()
    {
        return v;
    }


    /**
     * Get the path length in the W axis
     *
     * @return  the path length
     */
    public double getW()
    {
        return w;
    }
}
