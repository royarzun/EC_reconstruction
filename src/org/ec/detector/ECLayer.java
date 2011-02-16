package org.ec.detector;

import java.io.Serializable;
import java.util.Collection;
import java.util.TreeMap;

import org.ec.detector.base.ECLayerName;
import org.ec.detector.base.ECViewLabel;


/**
 * The <code>Layer</code> class represents a layer in the EC
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
public class ECLayer implements Serializable
{
    private ECLayerName name;
    public TreeMap<ECViewLabel, ECView> viewList;
    
    
    public ECLayer(ECLayerName name)
    {
        this.name     = name; 
        
        viewList = new TreeMap<ECViewLabel, ECView>();
        for(ECViewLabel label : ECViewLabel.values()) {
            viewList.put(label, new ECView(label));
        }
    }
    
    
    public Collection<ECView> getViewList()
    {
        return viewList.values();
    }
    
    
    public ECView getView(ECViewLabel label)
    {
        return viewList.get(label);
    }
    
    
    /**
     * @return The name of the layer.
     */
    public ECLayerName getName()
    {
        return name;
    }
}
