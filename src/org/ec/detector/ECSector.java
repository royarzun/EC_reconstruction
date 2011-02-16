package org.ec.detector;

import java.io.Serializable;
import java.util.Collection;
import java.util.TreeMap;

import org.ec.detector.base.ECLayerName;

/**
 * The <code>Sector</code> class represents a sector in the EC
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
public class ECSector implements Serializable
{
    private int ID;
    public TreeMap<ECLayerName, ECLayer> layerList;
    
    
	/**
     * Construct a {@link ECSector} object, who represents one of the
     * six sectors in the EC detector. Set the <code>ID</code> of the sector,
     * and create the list of the four layers on it.
	 * 
	 * @param id   the number of the sector
	 */
    public ECSector(int id)
    {
        this.ID        = id;
        this.layerList = new TreeMap<ECLayerName, ECLayer>();
        
        for(ECLayerName name : ECLayerName.values()) {
            layerList.put(name, new ECLayer(name));
        }
    }

    
    public Collection<ECLayer> getLayerList()
    {
        return layerList.values();
    }
    
    
    public ECLayer getLayer(ECLayerName name)
    {
        return layerList.get(name);
    }
    
    
    /**
     * @return the sector number.
     */
    public int getID()
    {
        return ID;
    }
}
