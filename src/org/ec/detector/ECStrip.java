package org.ec.detector;

/**
 * The <code>ECStrip</code> class represents a strip in the EC detector and
 * holds information about it.
 * <p>
 * <font size = 1>JSA: Thomas Jefferson National Accelerator Facility<br>
 * This software was developed under a United States Government license,<br>
 * described in the NOTICE file included as part of this distribution.<br>
 * Copyright (c), Feb 15, 2011</font>
 *
 * @author      smancill
 * @version     0.1
 */
public class ECStrip
{
    private int    ID;
    private double energy;
    private double time;


    /**
     * Create an object for a strip in a view in the EC.  This strips belongs
     * to a view object.  Each physical view in the detector have a fixed
     * number of strips, and this strips object stores the information for the
     * strip with the number of the <code>id</code> parameter.  Initialize all
     * its properties to zero.
     *
     * @param id  the identification number of the strip.
     */
    public ECStrip(int id)
    {
        this.ID         = id;
        this.energy     = 0.0;
        this.time       = 0.0;
    }


    /**
     * Create an object for a strip in a view in the EC and set its energy.
     * This strips belongs to a view object.  Each physical view in the
     * detector have a fixed number of strips, and this strips object stores
     * the information for the strip with the number of the <code>id</code>
     * parameter.  Initialize all its other properties to zero.
     *
     * @param id  the identification number of the strip.
     */
    public ECStrip(int id, double energy)
    {
        this.ID         = id;
        this.energy     = energy;
        this.time       = 0.0;
    }


    /**
     * Set the calculated energy of the strip.
     *
     * @param energy  the energy to set
     */
    public void setEnergy(double energy)
    {
        this.energy = energy;
    }


    /**
     * Get the calculated energy of the strip.
     *
     * @return  the energy
     */
    public double getEnergy()
    {
        return energy;
    }


    /**
     * Set the time of the strip.
     *
     * @param time  the time to set
     */
    public void setTime(double time)
    {
        this.time = time;
    }


    /**
     * Get the time of the strip.
     *
     * @return  the time
     */
    public double getTime()
    {
        return time;
    }


    /**
     * Get the identification number of the strip.
     *
     * @return  the ID of the strip
     */
    public int getID()
    {
        return ID;
    }
}
