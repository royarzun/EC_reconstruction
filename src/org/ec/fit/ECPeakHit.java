package org.ec.fit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;


/**
 * The <code>ECPeakHit</code> class stores the hit information for a view.
 * It saves the peak in that view which originated the hit, the path length of
 * the hit in that axis, and several other properties. Three objects of this
 * class are used by the hit object to store the data for all the three views
 * in the layer.
 * <p>
 * The <code>ECPeakHit</code> objects do not save any reference to the view
 * that they represent in the total information of the hit. It is
 * responsability of that parent {@link ECFitHit hit} to keep mapped the
 * objects with the views.
 * <p>
 * <font size = 1>JSA: Thomas Jefferson National Accelerator Facility<br>
 * This software was developed under a United States Government license,<br>
 * described in the NOTICE file included as part of this distribution.<br>
 * Copyright (c), Feb 15, 2011</font>
 *
 * @author      smancill
 * @version     0.1
 * @see ECFitHit
 */
public class ECPeakHit
{
    private ECFitHit  hit;

    private ECFitPeak peak;
    private double    path;
    private double    hitFraction;

    private double    energy;
    private double    time;
    private double    timeWe;
    private double    width;
    private double    dist;

    private HashMap<Integer, Double> moments;

    private int       nStrips;


    /**
     * Contruct an object to store hit information for a view.  It keeps a
     * reference to its parent hit object in the layer, and a reference to the
     * peak object that originated the hit for the view that it represents.
     * @param fitHit
     * @param peak
     */
    public ECPeakHit(ECFitHit fitHit, ECFitPeak peak)
    {
        this.hit         = fitHit;

        this.peak        = peak;
        this.path        = 0;
        this.hitFraction = 0;

        this.energy      = 0;
        this.time        = 0;
        this.timeWe      = 0;
        this.width       = 0;
        this.dist        = 0;

        this.nStrips     = 0;

        // Create the list of the moments
        moments = new HashMap<Integer, Double>();
        moments.put(2, 0.0);
        moments.put(3, 0.0);
        moments.put(4, 0.0);
    }


    /**
     * Get the peak that originated the hit in the view.
     *
     * @return the peak object
     */
    public ECFitPeak getPeak()
    {
        return peak;
    }


    /**
     * Get the other two {@link ECPeakHit} objects of the hit.
     * <p>
     * All the {@link ECFitHit hits} have three of them, one for each view in
     * the layer.  This method returns the objects for the other two views.
     *
     * @return  a {@link Collection} with the other two objects
     */
    public Collection<ECPeakHit> getOtherPeakHits()
    {
        ArrayList<ECPeakHit> others = new ArrayList<ECPeakHit>();
        for (ECPeakHit ph : hit.getAllPeakHits()) {
            if (ph != this) others.add(ph);
        }
        return Collections.unmodifiableList(others);
    }


    /**
     * Set the path length of the hit in this axis.
     *
     * @param path the path length
     */
    public void setPath(double path)
    {
        this.path = path;
    }


    /**
     * Get the path length of the hit in this axis.
     *
     * @return the path length
     */
    public double getPath()
    {
        return path;
    }


    /**
     * Set the energy fraction of the hit in this axis.
     *
     * @param fraction the fraction of the energy
     */
    public void setHitFraction(double fraction)
    {
        this.hitFraction = fraction;
    }


    /**
     * Get the energy fraction of the hit in this axis.
     *
     * @return the hit_fraction
     */
    public double getHitFraction()
    {
        return hitFraction;
    }


    /**
     * Set the energy of the hit in this axis.
     *
     * @param energy the energy amount
     */
    public void setEnergy(double energy)
    {
        this.energy = energy;
    }


    /**
     * Get the energy of the hit in this axis.
     *
     * @return the energy amount
     */
    public double getEnergy()
    {
        return energy;
    }


    /**
     * Set the time of the hit in this axis.
     *
     * @param time the time to set
     */
    public void setTime(double time)
    {
        this.time = time;
    }


    /**
     * Get the time of the hit in this axis.
     *
     * @return the time
     */
    public double getTime()
    {
        return time;
    }


    /**
     * Set the time we of the hit in this axis.
     *
     * @param timeWe the time to set
     */
    public void setTimeWe(double timeWe)
    {
        this.timeWe = timeWe;
    }


    /**
     * Get the time we of the hit in this axis.
     *
     * @return the time we
     */
    public double getTimeWe()
    {
        return timeWe;
    }


    /**
     * Set the width of the hit in this axis.
     *
     * @param width the width to set
     */
    public void setWidth(double width)
    {
        this.width = width;
    }


    /**
     * Get the width of the hit in this axis.
     *
     * @return the width
     */
    public double getWidth()
    {
        return width;
    }


    /**
     * Set the dist of the hit in this axis.
     *
     * @param dist the dist to set
     */
    public void setDist(double dist)
    {
        this.dist = dist;
    }


    /**
     * Get the dist of the hit in this axis.
     *
     * @return the dist
     */
    public double getDist()
    {
        return dist;
    }


    /**
     * Set the <em>nth</em>-moment of the hit in this axis.
     * <p>
     * You need to pass the number of which moment you want, i.e.,
     * <code>2</code> for the second moment, <code>3</code> for the third
     * momentum, etc.
     *
     * @param nth      the desired moment
     * @param value    the value of the moment
     */
    public void setMoment(int nth, double value)
    {
        if (moments.containsKey(nth))
            moments.put(nth, value);
    }


    /**
     * Get the <em>nth</em>-momentum of the hit in this axis.
     * <p>
     * You need to pass the number of which momentum you want, i.e.,
     * <code>2</code> for the second moment, <code>3</code> for the third
     * moment, etc.
     *
     * @param nth  the desired moment
     * @return     the value of the moment
     */
    public double getMoment(int nth)
    {
        if (moments.containsKey(nth))
            return moments.get(nth);
        else
            return 0.0;
    }


    /**
     * Get the number of strips  of the hit in this axis.
     *
     * @param number  the number to set
     */
    public void setNStrips(int number)
    {
        nStrips = number;
    }


    /**
     * Get the number of strips  of the hit in this axis.
     *
     * @return  the number of strips
     */
    public double getNStrips()
    {
        return nStrips;
    }
}
