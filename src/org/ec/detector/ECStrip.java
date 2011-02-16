package org.ec.detector;

import java.io.Serializable;

/**
 * The <code>Strip</code> class represents a strip in the EC detector and
 * holds information about it.<br><br>
 * 
 * <font size = 1>JSA: Thomas Jefferson National Accelerator Facility<br>
 * This software was developed under a United States Government license,<br>
 * described in the NOTICE file included as part of this distribution.<br>
 * Copyright (c), Feb 15, 2011</font>
 * 
 * @author      smancill
 * @version     0.1
 */
public class ECStrip implements Serializable
{
    public double ID;           // the ID of the strip
    public double energy;       // the energy of the strip
    public double time;         // the time of the strip
    
    
    public ECStrip(int id)
    {
        this.ID     = id;
        this.energy = 0.0;
        this.time   = 0.0;
    }
    
    
    public ECStrip(int id, double energy)
    {
        this.ID     = id;
        this.energy = energy;
        this.time   = 0.0;
    }
    
    
    public void setEnergy(double energy)
    {
        this.energy = energy;
    }
    
    
    public double getEnergy()
    {
        return energy;
    }
}
