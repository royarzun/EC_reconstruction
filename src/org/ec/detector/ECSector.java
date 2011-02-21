package org.ec.detector;

import java.util.Collection;
import java.util.HashMap;
import java.util.TreeMap;


/**
 * The <code>ECSector</code> class represents a sector in the EC detector.
 * This detector has 6 sectors, and each sector object stores the data of one
 * of those sectors.
 * <p>
 * Each sector has a fixed list of four {@link ECLayer layers} that store the
 * sector data for each of the <em>WHOLE</em>, <em>INNER</em>, <em>OUTER</em>
 * and <em>COVER</em> layers of the detector, and an <code>ID</code> to
 * identify the sector (1 to 6).
 * <p>
 * You can get the data for all the layers by getting and iterating over the
 * list of layers, or you can get the data of one specific layer by giving its
 * name.
 * <p>
 * <font size = 1>JSA: Thomas Jefferson National Accelerator Facility<br>
 * This software was developed under a United States Government license,<br>
 * described in the NOTICE file included as part of this distribution.<br>
 * Copyright (c), Feb 15, 2011</font>
 *
 * @author      smancill
 * @version     0.1
 */
public class ECSector
{
    private int ID;
    private TreeMap<ECLayerName, ECLayer> layerList;

    private double phi;
    private HashMap<String, Double> origins;

    /**
     * Construct an object representing the EC sector with the given ID.
     * Initialize all the properties to zero and create the list of the four
     * layers on the sector.
     *
     * @param id   the number of the sector (1 to 6)
     */
    public ECSector(int id)
    {
        this.ID  = id;
        this.phi = 0;

        // Create the hash for the local coordinate system
        this.origins   = new HashMap<String, Double>();

        origins.put("x", 0.0);
        origins.put("y", 0.0);
        origins.put("z", 0.0);

        // Create the layer list
        this.layerList = new TreeMap<ECLayerName, ECLayer>();
        for (ECLayerName name : ECLayerName.values()) {
            layerList.put(name, new ECLayer(name, id));
        }
    }


    /**
     * Get the list of layers in the sector, to iterate over it.
     *
     * @return  a {@link Collection} with the four layers of the sector.
     * @see     ECLayer
     */
    public Collection<ECLayer> getLayerList()
    {
        return layerList.values();
    }


    /**
     * Get a specific layer in the sector.
     *
     * @param name  the name of the desired layer
     * @return      the desired layer
     * @see         ECLayer
     */
    public ECLayer getLayer(ECLayerName name)
    {
        return layerList.get(name);
    }


    /**
     * Set the phi angle of the sector.
     *
     * @param phi the phi to set
     */
    public void setPhi(double phi)
    {
        this.phi = phi;
    }


    /**
     * Get the phi angle of the sector.
     *
     * @return the phi angle
     */
    public double getPhi()
    {
        return phi;
    }


    /**
     *  Get the origin of the local coordinate system.  This method gets the
     *  origin of one axis at the time.  Each of the three axes is represented
     *  by a letter, that can be <code>"x"</code>, <code>"y"</code> or
     *  <code>"z"</code>.
     *
     * @param axis the desired axis
     * @return     the origin of the local coordinate system for the given
     *             axis
     */
    public double getOrigins(String axis)
    {
        if (origins.containsKey(axis))
            return origins.get(axis);
        else
            return 0.0;
    }


    /**
     * Set the origin of the local coordinate system in the sector.  This
     * method sets the origin of one axis at the time.  Each of the three axes
     * is represented by a letter that must be pased as parameter, and it can
     * be <code>"x"</code>, <code>"y"</code> or <code>"z"</code>.
     *
     * @param axis     the axis to set
     * @param position the position for the given axis
     */
    public void setOrigins(String axis, double position)
    {
        if (origins.containsKey(axis))
            origins.put(axis, position);
    }


    /**
     * Get the ID of the sector.
     *
     * @return the sector ID
     */
    public int getID()
    {
        return ID;
    }
}
