package org.ec.detector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
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

    private double energy;

    private double depth;
    private double H;
    private double H1;
    private double H2;

    private int    maxStrips;


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

        this.energy = 0.0;

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
     * Get a sublist of the found hits in the layer. This sublist starts
     * from the position given by the index parameter, to the end of the list
     * of hits.
     *
     * @param index the index of the starting hit in the sublist
     * @return      a {@link Collection} with the sublist of the found hits
     */
    public Collection<ECFitHit> getHitList(int index)
    {
        int n = hitList.size();
        Collection<ECFitHit> sublist = hitList.subList(index, n + 1);
        return Collections.unmodifiableCollection(sublist);
    }


    /**
     * Sort the list of hits using the provided {@link Comparator}.
     *
     * @param c  the Comparator object
     */
    public  void sortHits(Comparator<ECFitHit> c)
    {
        Collections.sort(hitList, c);
    }


    /**
     * Reset the number of found hits in the layer to zero.
     */
    public void clearHitList()
    {
        hitList.clear();
    }


    /**
     * Resize the list of hits to the given size.  The list keeps the elements
     * with index between <code>0</code> and <code>size - 1</code>.
     *
     * @param size  the number of elements to keep in the list
     */
    public void resizeHitList(int size)
    {
        hitList = (ArrayList<ECFitHit>) hitList.subList(0, size + 1);
    }


    /**
     * Get the length of a side.
     *
     * @param view  the desired side
     * @return      the length of the side
     */
    public double getEdgeL(ECViewLabel view)
    {
        return viewList.get(view).getLength();
    }


    /**
     * Set the total energy of the found hits in the layer.
     *
     * @param energy  the amount of energy to set
     */
    public void setEnergy(double energy)
    {
        this.energy = energy;
    }


    /**
     * Get the total energy of the found hits in the layer.
     *
     * @return the amount of energy
     */
    public double getEnergy()
    {
        return energy;
    }


    /**
     * Set the depth of the layer
     *
     * @param depth the depth to set
     */
    public void setDepth(double depth)
    {
        this.depth = depth;
    }


    /**
     * Get the depth of the layer
     *
     * @return the depth of the layer
     */
    public double getDepth()
    {
        return depth;
    }


    /**
     * TODO Find out the right comment to put here.
     */
    public void setH(double h)
    {
        H = h;
    }


    /**
     * TODO Find out the right comment to put here.
     *
     * @return the h
     */
    public double getH()
    {
        return H;
    }


    /**
     * TODO Find out the right comment to put here.
     *
     * @param h1 the h1 to set
     */
    public void setH1(double h1)
    {
        H1 = h1;
    }


    /**
     * TODO Find out the right comment to put here.
     *
     * @return the h1
     */
    public double getH1()
    {
        return H1;
    }


    /**
     * TODO Find out the right comment to put here.
     *
     * @param h2 the h2 to set
     */
    public void setH2(double h2)
    {
        H2 = h2;
    }


    /**
     * TODO Find out the right comment to put here.
     *
     * @return the h2
     */
    public double getH2()
    {
        return H2;
    }


    /**
     * Set the maximum number of strips for the layer.  Not all the layers
     * have the same maximum for the number of strips in its views.
     *
     * @param maxStrips the maximum to set
     */
    public void setMaxStrips(int maxStrips)
    {
        this.maxStrips = maxStrips;
    }


    /**
     * Get the maximum number of strips for the layer.  Not all the layers
     * have the same maximum for the number of strips in its views.
     *
     * @return the maximum number of strips
     */
    public int getMaxStrips()
    {
        return maxStrips;
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
