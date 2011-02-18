package org.ec.fit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.ec.detector.ECStrip;

/**
 * The <code>ECFitPeak</code> class represents a peak in a view of the EC
 * detector. It is constructed using the strip energies of the view, and it
 * contains a list with references to the strip objects that form the peak.
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
public class ECFitPeak
{
    private int    ID;
    private String key;

    private ArrayList<ECStrip> stripList;

    private double energy;
    private double width;
    private double dist;


    /**
     * Create an object for a found peak in a view.
     * <p>
     * The peak has the correlative number <code>id</code> in the view's list
     * of peaks, and using the view's key identificator given as the second
     * parameter, an unique key is created for this peak.  This unique key can
     * be used to identify the peak if the algorithm requires some kind of
     * mapping.
     * <p>
     * All the properties of the peak are initialized to zero, and the list of
     * strips is empty.  It is responsibility of the algorithm to fill the
     * list with references to the right strips that form the peak.
     *
     * @param id       the correlative id number of the peak
     * @param viewKey  the key of the view this peak belongs
     */
    public ECFitPeak(int id, String viewKey)
    {
        this.ID     = id;
        this.key    = viewKey + ".PEAK:" + id;
        this.energy = 0.0;

        this.stripList = new ArrayList<ECStrip>();
    }


    /**
     * Add a new strip to the list of strips of the peak.
     *
     * @param s  the strip object to be added
     */
    public void addStrip(ECStrip s)
    {
        stripList.add(s);
    }


    /**
     * Get the number of strips in the peak.
     *
     * @return  the number of strips
     */
    public int getNStrips()
    {
        return stripList.size();
    }


    /**
     * Get the list of strips of the peak to iterate over it.
     *
     * @return  a {@link Collection} with the strips of the peak
     */
    public Collection<ECStrip> getStripList()
    {
        return Collections.unmodifiableList(stripList);
    }


    /**
     * Set the total energy of the peak.
     *
     * @param energy  the energy to set
     */
    public void setEnergy(double energy)
    {
        this.energy = energy;
    }


    /**
     * Get the total energy of the peak.
     *
     * @return  the energy
     */
    public double getEnergy()
    {
        return energy;
    }


    /**
     * Set the width of the peak.
     *
     * @param width  the width to set
     */
    public void setWidth(double width)
    {
        this.width = width;
    }


    /**
     * Get the width of the peak.
     *
     * @return the width
     */
    public double getWidth()
    {
        return width;
    }


    /**
     * Set the dist of the peak.
     *
     * @param dist  the dist to set
     */
    public void setDist(double dist)
    {
        this.dist = dist;
    }


    /**
     * Get the dist of the peak.
     *
     * @return  the dist
     */
    public double getDist()
    {
        return dist;
    }


    /**
     * Get the correlative identification number of the peak.
     *
     * @return  the ID number
     */
    public int getID()
    {
        return ID;
    }


    /**
     * Get the unique key that identifies the peak globally.
     *
     * @return  the unique key identifying the peak
     */
    public String getKey()
    {
        return key;
    }
}
