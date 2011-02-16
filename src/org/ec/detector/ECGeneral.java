package org.ec.detector;

/**
 * This class store the constants used by all the other classes.<br><br>
 * 
 * <font size = 1>JSA: Thomas Jefferson National Accelerator Facility<br>
 * This software was developed under a United States Government license,<br>
 * described in the NOTICE file included as part of this distribution.<br>
 * Copyright (c), Feb 15, 2011</font>
 * 
 * @author      smancill
 * @version     0.1
 */
public final class ECGeneral
{
    public final static int     MAX_SECTORS             =     6;
    public final static int     MAX_STRIPS              =   108;
    public final static int     MAX_PEAKS               =    30;
    public final static int     MAX_HITS                =    10;
    
    
    public final static double  SPEED_OF_LIGHT         =     29.9792458;
    public final static double  INDEX_OF_REFRACTION    =      1.581;
    public final static double  SPEED_IN_PLASTIC       =     18.0;
    public final static double  EFF_SAMPLING_FRACTION  =      0.275;
    
    
    public final static double  DEFAULT_ECH            =      0.0001;
    public final static double  DEFAULT_TCH            =      0.050;
    public final static double  DEFAULT_TRMS           =      1.0;
    public final static double  DEFAULT_ATTEN          =    376.0;
}
