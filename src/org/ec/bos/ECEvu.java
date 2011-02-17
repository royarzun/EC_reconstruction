package org.ec.bos;

/**
 * The <code>ECEVu</code> object represents the RAW data as read from the BOS
 * file. It stores the <code>TDC</code> and <code>ADC</code> values for the
 * strip with the given <code>ID</code>, in the view that owns the object.
 *
 * <p><font size = 1>JSA: Thomas Jefferson National Accelerator Facility<br>
 * This software was developed under a United States Government license,<br>
 * described in the NOTICE file included as part of this distribution.<br>
 * Copyright (c), Feb 15, 2011</font>
 *
 * @author      smancill
 * @version     0.1
 */
public class ECEvu
{
    private int ID;
    private double TDC;
    private double ADC;


    /**
     * Construct a new {@link ECEvu} object, who stores the RAW data from BOS
     * file.  The data is for one strip.
     *
     * @param ID   the ID of the strip
     * @param TDC  the TDC value of the strip
     * @param ADC  the ADC value of the strip
     */
    public ECEvu(int ID, double TDC, double ADC)
    {
        this.ID  = ID;
        this.TDC = TDC;
        this.ADC = ADC;
    }


    /**
     * Get the ID of the strip identified by this object.
     *
     * @return  the ID of the strip
     */
    public int getID()
    {
        return ID;
    }


    /**
     * Get the TDC value of the strip identified by this object.
     *
     * @return  the TDC of the strip
     */
    public double getTDC()
    {
        return TDC;
    }


    /**
     * Get the ADC value of the strip identified by this object.
     *
     * @return  the ADC of the strip
     */
    public double getADC()
    {
        return ADC;
    }
}
