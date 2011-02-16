package org.ec.detector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.ec.bos.ECEvu;


/**
 * The <code>View</code> class represents an orientation or view in the EC
 * detector.<br><br>
 * 
 * <font size = 1>JSA: Thomas Jefferson National Accelerator Facility<br>
 * This software was developed under a United States Government license,<br>
 * described in the NOTICE file included as part of this distribution.<br>
 * Copyright (c), Feb 15, 2011</font>
 * 
 * @author      smancill
 * @version     0.1
 */
public class ECView
{
    ECViewLabel label;
    public ArrayList<ECEvu>   evuList;
    public ArrayList<ECStrip> stripList;
    
    public double[] calEch;
    public double[] calAtten;
    public double[] calEo;
    
    public double[] calTch;
    public double[] calTo;
    public double[] calTadc;
    public double[] caldT1;
    public double[] caldT2;
    public double[] calTrms;
    public double[] calTDCStat;
    
    
    public ECView(ECViewLabel label)
    {
        this.label      = label;
        this.evuList    = new ArrayList<ECEvu>();
        this.stripList  = new ArrayList<ECStrip>();
        
        this.calEch     = new double[ECGeneral.MAX_STRIPS];
        this.calAtten   = new double[ECGeneral.MAX_STRIPS];
        this.calEo      = new double[ECGeneral.MAX_STRIPS];
        this.calTch     = new double[ECGeneral.MAX_STRIPS];
        this.calTo      = new double[ECGeneral.MAX_STRIPS];
        this.calTadc    = new double[ECGeneral.MAX_STRIPS];
        this.caldT1     = new double[ECGeneral.MAX_STRIPS];
        this.caldT2     = new double[ECGeneral.MAX_STRIPS];
        this.calTrms    = new double[ECGeneral.MAX_STRIPS];
        this.calTDCStat = new double[ECGeneral.MAX_STRIPS];
    }
    
    
    public Collection getStripList()
    {
       return Collections.unmodifiableList(stripList);
    }
    
    
    public Collection getEvuList()
    {
       return Collections.unmodifiableList(evuList);
    }
    
    
    public void addEvu(ECEvu e)
    {
        evuList.add(e);
    }
    
    
    public void addStrip(ECStrip s)
    {
        stripList.add(s);
    }
    
    
    public ECViewLabel getLabel()
    {
        return label;
    }
}
