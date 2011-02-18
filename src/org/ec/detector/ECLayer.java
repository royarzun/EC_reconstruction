package org.ec.detector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.TreeMap;

import org.ec.fit.ECFitHit;
import org.ec.fit.ECFitPeak;


/**
 * The <code>ECLayer</code> class represents a layer in the EC detector.  The
 * layers belong to one {@link ECSector sector}, and they are used to store
 * the sector data for each of the four layers of the detector.
 * <p>
 * Each layer object has a fixed list of three {@link ECView views} that store
 * the layer data for each of the <em>U</em>, <em>V</em> and <em>W</em> views
 * of the detector; a dynamic list of the found {@link ECFitHit hits} in the
 * layer (after finding peaks the views), and a name to identify the layer.
 * <p>
 * <font size = 1>JSA: Thomas Jefferson National Accelerator Facility<br>
 * This software was developed under a United States Government license,<br>
 * described in the NOTICE file included as part of this distribution.<br>
 * Copyright (c), Feb 15, 2011</font>
 *
 * @author      smancill
 * @version     0.1
 * @see ECView
 * @see ECFitHit
 */
public class ECLayer
{
    private String      key;
    private ECLayerName name;

    private TreeMap<ECViewLabel, ECView> viewList;
    private ArrayList<ECFitHit> hitList;


    /**
     * Construct an object representing the EC layer of the given name.  This
     * layer is part of a sector object and is also identified by an unique
     * key constructed using the sector ID and the layer name.  All the
     * properties of the layer and the number of found hits are initialized to
     * zero.  Create the list with its three views.
     *
     * @param name      the name identifying the layer
     * @param sectorID  the ID of the sector that the layer belongs
     * @see             ECView
     * @see             ECFitHit
     */
    public ECLayer(ECLayerName name, int sectorID)
    {
        this.name   = name;
        this.key    = "S" + sectorID + "." + name;

        // Create the list of views and hits
        viewList = new TreeMap<ECViewLabel, ECView>();
        hitList  = new ArrayList<ECFitHit>();

        for (ECViewLabel label : ECViewLabel.values()) {
            viewList.put(label, new ECView(label, this.key));
        }
    }


    /**
     * Get the list of views in the layer, to iterate over it.
     *
     * @return  a {@link Collection} with the three views of the layer.
     * @see     ECView
     */
    public Collection<ECView> getViewList()
    {
        return viewList.values();
    }


    /**
     * Get a specific view in the layer.
     *
     * @param label  the label of the desired view
     * @return       the desired view
     * @see          ECView
     */
    public ECView getView(ECViewLabel label)
    {
        return viewList.get(label);
    }


    /**
     * Get the number of found hits in the layer.
     *
     * @return  the number of hits
     */
    public int getNHits()
    {
        return hitList.size();
    }


    /**
     * Create a new {@link ECFitHit hit} object.  Each hit is identified by a
     * triplet of peaks, one for each axis.  Set the right correlative ID
     * number for it.  The created hit is returned, so it can be used and more
     * of its properties can be set.
     *
     * @param u the peak related to the new hit in the U axis
     * @param v the peak related to the new hit in the V axis
     * @param w the peak related to the new hit in the W axis
     * @return  the created hit
     * @see     ECFitHit
     */
    public ECFitHit newHit(ECFitPeak u, ECFitPeak v, ECFitPeak w)
    {
        int id = hitList.size() + 1;
        ECFitHit h = new ECFitHit(id, u, v, w);
        return h;
    }


    /**
     * Get the list of all the hits in the layer, to iterate over it.
     *
     * @return  a {@link Collection} with the found hits in the layer.
     * @see     ECFitHit
     */
    public Collection<ECFitHit> getHitList()
    {
        return Collections.unmodifiableList(hitList);
    }


    /**
     * Get the name identifying the layer.
     *
     * @return The name of the layer.
     */
    public ECLayerName getName()
    {
        return name;
    }
}
