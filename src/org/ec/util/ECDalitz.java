package org.ec.util;

import java.util.HashMap;

import org.ec.detector.ECLayer;
import org.ec.detector.ECViewLabel;
import org.ec.fit.ECFitPeak;

/**
 * <em></em><code></code>
 * This class uses <em>Dalitz</em> sum to see if <code>(u,v,w)</code> is a
 * point in the UVW coord system. If so the <coe>(I,J)</code> coordinate are
 * calculated.
 * <p>
 * <font size = 1>JSA: Thomas Jefferson National Accelerator Facility<br>
 * This software was developed under a United States Government license,<br>
 * described in the NOTICE file included as part of this distribution.<br>
 * Copyright (c), Feb 22, 2011</font>
 *
 * @author      smancill
 * @version     0.1
 */
public class ECDalitz
{
    private HashMap<String, Double> projections;

    private double   error;
    private double   rms;
    private boolean  isPoint;


    /**
     * Construct an object to evaluate the <em>Dalitz</em> condition.
     *
     * @param layer  the layer of the sector
     * @param u      U projection
     * @param v      V projection
     * @param w      W projection
     * @param iterr  1 to not calculate the <code>(i,j)</code> coordinate, 2 to do the calculation
     */
    public ECDalitz(ECLayer layer, ECFitPeak u, ECFitPeak v, ECFitPeak w, int iterr)
    {
        double du = u.getDist();
        double dv = v.getDist();
        double dw = w.getDist();

        double wu = u.getWidth();
        double wv = v.getWidth();
        double ww = w.getWidth();

        double lu = layer.getEdgeL(ECViewLabel.U);
        double lv = layer.getEdgeL(ECViewLabel.V);
        double lw = layer.getEdgeL(ECViewLabel.W);

        double dalitz   = (du / lu) + (dv / lv) + (dw / lw);
        double dDalitz  = Math.pow(wu / lu, 2) + Math.pow(wv / lv, 2) + Math.pow(ww / lw, 2);
        double maxError = 2 * Math.sqrt(dDalitz);

        error   = Math.abs(dalitz - 2) / maxError;

        isPoint = Math.abs(dalitz - 2) < maxError;

        if (isPoint) {
            double i = layer.getH() * (du / lu - dv / lv - dw / lw) / 2 + layer.getH2();
            double j = lv * (dw / lw - dv / lv) / 2;
            double k = layer.getDepth();

            projections.put("i", i);
            projections.put("j", j);
            projections.put("k", k);

            if (iterr == 2) {
                double di = Math.sqrt(Math.pow(wu / lu, 2) + Math.pow(wv / lv, 2) + Math.pow(ww / lw, 2)) * layer.getH();
                double dj = Math.sqrt(Math.pow(wv / lv, 2) + Math.pow(ww / lw, 2)) * lv / 2;

                projections.put("di", di);
                projections.put("dj", dj);

                rms = Math.sqrt(Math.pow(di, 2) + Math.pow(dj, 2));
            }
        }
    }


    /**
     * Return the evaluation of the <em>Dalitz</em> condition.  If the
     * condition is safisfied, the <code>(u,v,w)</code> is a point in the UVW
     * coord system, and the event is geometrically pausible.
     *
     * @return true if it is a point, false if not
     */
    public boolean isPoint()
    {
        return this.isPoint;
    }


    /**
     * Get the error of the <em>Dalitz</em> condition evaluation.
     *
     * @return  the obtained error
     */
    public double getError()
    {
        return error;
    }


    /**
     * Get the root mean square of crossing.
     *
     * @return  root mean square of <code>UV</code>, <code>VW</code>,
     *          <code>WU</code> crossings
     */
    public double getRms()
    {
        return rms;
    }


    /**
     * Get the projection of mean crossings.
     * <p>
     * The coordinates in the <code>IJK</code> local coordinate system are
     * represented by a letter, that can be <code>"i"</code>, <code>"j"</code>
     * or <code>"k"</code>.
     * <p>
     * The coordinates in the <code>IJ</code> coordinate system are
     * represented by <code>"di"</code> and <code>"dj"</code>.
     *
     * @param axis the desired axis
     * @return     the position in the axis
     */
    public double getProjection(String axis)
    {
        if (projections.containsKey(axis))
            return projections.get(axis);
        else
            return 0.0;
    }
}
