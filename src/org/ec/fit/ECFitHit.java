package org.ec.fit;

import java.util.Collection;
import java.util.HashMap;
import java.util.TreeMap;

import org.ec.detector.ECLayer;
import org.ec.detector.ECLayerName;
import org.ec.detector.ECViewLabel;


/**
 * The <code>ECFitHit</code> class represents a hit in a layer of the EC
 * detector.  A hit object is constructed using three peaks (one for each view
 * of the layer) that satisfy the <em>dalitz</em> condition.  It stores the
 * general information of the hit, and it also has specific information for
 * each view, using a list of {@link ECPeakHit} objects.
 * <p>
 * <font size = 1>JSA: Thomas Jefferson National Accelerator Facility<br>
 * This software was developed under a United States Government license,<br>
 * described in the NOTICE file included as part of this distribution.<br>
 * Copyright (c), Feb 15, 2011</font>
 *
 * @author      smancill
 * @version     0.1
 * @see ECPeakHit
 */
public class ECFitHit
{
    private int ID;

    private double energy;
    private double width;
    private double time;
    private double ch2;

    private HashMap<String, Double> clasCoordinates;
    private HashMap<String, Double> localCoordinates;
    private HashMap<String, Double> faceCoordinates;

    private TreeMap<ECViewLabel, ECPeakHit> peakHitList;

    private TreeMap<ECLayerName, ECFitHit>  matched;
    private TreeMap<ECLayerName, Double>    c2matched;

    private int nStrips;


    /**
     * Create an object for a found hit.  The hit has the correlative number
     * <code>id</code> in the list of hits of the layer, and it is identified
     * in each view by the three peaks given as parameters.
     *
     * Initialize all the hit properties to zero, and create the list with the
     * {@link ECPeakHit} objects that stores the hit information for each
     * view.
     *
     * @param id  the correlative ID in the list of hits
     * @param u   the peak for the hit in the view U
     * @param v   the peak for the hit in the view V
     * @param w   the peak for the hit in the view W
     */
    public ECFitHit(int id, ECFitPeak u, ECFitPeak v, ECFitPeak w, ECLayer layer)
    {
        this.ID      = id;

        this.energy  = 0;
        this.width   = 0;
        this.time    = 0;
        this.ch2     = 0;       // Should be used outside, or with a different initial value?
        this.nStrips = 0;

        initializeCoordinates();

        // Create PeakHit objects
        peakHitList.put(ECViewLabel.U, new ECPeakHit(this, u));
        peakHitList.put(ECViewLabel.V, new ECPeakHit(this, v));
        peakHitList.put(ECViewLabel.W, new ECPeakHit(this, w));

        // Create matched sets
        matched   = new TreeMap<ECLayerName, ECFitHit>();
        c2matched = new TreeMap<ECLayerName, Double>();

        ECLayerName layerName = layer.getName();
        for (ECLayerName name : ECLayerName.values()) {
            if (name != layerName) {
                matched.put(name, null);
                c2matched.put(name, 0.0);
            }
        }
    }


    /**
     * Create and initialize to zero the three coordinates hashmaps.
     * These maps store the information of the hit for the local, CLAS and
     * face coordinate systems.
     */
    private void initializeCoordinates()
    {
        // Set coordinates names
        localCoordinates = new HashMap<String, Double>();
        clasCoordinates  = new HashMap<String, Double>();
        faceCoordinates  = new HashMap<String, Double>();

        localCoordinates.put("i",  0.0);
        localCoordinates.put("j",  0.0);
        localCoordinates.put("k",  0.0);
        localCoordinates.put("di", 0.0);
        localCoordinates.put("dj", 0.0);
        localCoordinates.put("dk", 0.0);

        clasCoordinates.put("x",  0.0);
        clasCoordinates.put("y",  0.0);
        clasCoordinates.put("z",  0.0);
        clasCoordinates.put("dx", 0.0);
        clasCoordinates.put("dy", 0.0);
        clasCoordinates.put("dz", 0.0);

        faceCoordinates.put("i",  0.0);
        faceCoordinates.put("j",  0.0);
        faceCoordinates.put("di", 0.0);
        faceCoordinates.put("dj", 0.0);
    }


    /**
     * Set the path length of the hit for all three views.
     *
     * @param u  the path length for the view U
     * @param v  the path length for the view V
     * @param w  the path length for the view W
     */
    public void setPaths(double u, double v, double w)
    {
        peakHitList.get(ECViewLabel.U).setPath(u);
        peakHitList.get(ECViewLabel.V).setPath(v);
        peakHitList.get(ECViewLabel.W).setPath(w);
    }


    /**
     * Get the hit information for the given view.
     *
     * @param view  the desired view
     * @return      a {@link ECPeakHit} object with the view information
     */
    public ECPeakHit getPeakHit(ECViewLabel view)
    {
        return peakHitList.get(view);
    }


    /**
     * Get a list with the hit information for each view, to iterate over it.
     *
     * @return  a {@link Collection} with the information for all the views
     */
    public Collection<ECPeakHit> getAllPeakHits()
    {
        return peakHitList.values();
    }


    /**
     * Set the total energy of the hit.
     *
     * @param energy the energy to set
     */
    public void setEnergy(double energy)
    {
        this.energy = energy;
    }


    /**
     * Get the total energy of the hit.
     *
     * @return the energy
     */
    public double getEnergy()
    {
        return energy;
    }


    /**
     * Set the width of the hit.
     *
     * @param width the width to set
     */
    public void setWidth(double width)
    {
        this.width = width;
    }


    /**
     * Get the width of the hit.
     *
     * @return the width
     */
    public double getWidth()
    {
        return width;
    }


    /**
     * Set the time of the hit.
     *
     * @param time the time to set
     */
    public void setTime(double time)
    {
        this.time = time;
    }


    /**
     * Get the time of the hit.
     *
     * @return the time
     */
    public double getTime()
    {
        return time;
    }


    /**
     * Set the <em>chi-square</em> of the hit.
     *
     * @param ch2 the ch2 to set
     */
    public void setCh2(double ch2)
    {
        this.ch2 = ch2;
    }


    /**
     * Get the <em>chi-square</em> of the hit.
     *
     * @return the ch2
     */
    public double getCh2()
    {
        return ch2;
    }


    /**
     * Set the axis position in the local coordinate system.  This method sets
     * the position of the hit in the local coordinate system, one axis at the
     * time.
     * <p>
     * Each of the three axes is represented by a letter, that can be
     * <code>"i"</code>, <code>"j"</code> or <code>"k"</code>, and for the
     * three of them exist a delta of the position, that can be
     * <code>"di"</code>, <code>"dj"</code> or <code>"dk"</code>,
     * respectively.
     *
     * @param axis      the desired axis
     * @param position  the position in the axis
     */
    public void setLocalCoord(String axis, double position)
    {
        if (localCoordinates.containsKey(axis))
            localCoordinates.put(axis, position);
    }


    /**
     * Get the axis position in the local coordinate system.  This method gets
     * the position of the hit in the local coordinate system, one axis at the
     * time.
     * <p>
     * Each of the three axes is represented by a letter, that can be
     * <code>"i"</code>, <code>"j"</code> or <code>"k"</code>, and for the
     * three of them exist a delta of the position, that can be
     * <code>"di"</code>, <code>"dj"</code> or <code>"dk"</code>,
     * respectively.
     *
     * @param axis  the desired axis
     * @return      the position of the hit in the axis
     */
    public double getLocalCoord(String axis)
    {
        if (localCoordinates.containsKey(axis))
            return localCoordinates.get(axis);
        else
            return 0.0;
    }


    /**
     * Set the axis position in the CLAS coordinate system.  This method sets
     * the position of the hit in the CLAS coordinate system, one axis at the
     * time.
     * <p>
     * Each of the three axes is represented by a letter, that can be
     * <code>"x"</code>, <code>"i"</code> or <code>"z"</code>, and for the
     * three of them exist a delta of the position, that can be
     * <code>"dx"</code>, <code>"dy"</code> or <code>"dz"</code>,
     * respectively.
     *
     * @param axis      the desired axis
     * @param position  the position in the axis
     */
    public void setClasCoord(String axis, double position)
    {
        if (clasCoordinates.containsKey(axis))
            clasCoordinates.put(axis, position);
    }


    /**
     * Get the axis position in the CLAS coordinate system.  This method gets
     * the position of the hit in the CLAS coordinate system, one axis at the
     * time.
     * <p>
     * Each of the three axes is represented by a letter, that can be
     * <code>"x"</code>, <code>"i"</code> or <code>"z"</code>, and for the
     * three of them exist a delta of the position, that can be
     * <code>"dx"</code>, <code>"dy"</code> or <code>"dz"</code>,
     * respectively.
     *
     * @param axis  the desired axis
     * @return      the position of the hit in the axis
     */
    public double getClasCoord(String axis)
    {
        if (clasCoordinates.containsKey(axis))
            return clasCoordinates.get(axis);
        else
            return 0.0;
    }


    /**
     * Set the axis position in coordinate system on the face of the EC .
     * This method sets the position of the hit in the coordinate system on
     * the face of the EC, one axis at the time.
     * <p>
     * The two axes are represented by a letter, that can be <code>"i"</code>
     * and <code>"j"</code>, and for both exist a delta of the position, that
     * can be <code>"di"</code> or <code>"dj"</code>, respectively.
     *
     * @param axis      the desired axis
     * @param position  the position in the axis
     */
    public void setFaceCoord(String axis, double position)
    {
        if (faceCoordinates.containsKey(axis))
            faceCoordinates.put(axis, position);
    }


    /**
     * Get the axis position in coordinate system on the face of the EC .
     * This method gets the position of the hit in the coordinate system on
     * the face of the EC, one axis at the time.
     * <p>
     * The two axes are represented by a letter, that can be <code>"i"</code>
     * and <code>"j"</code>, and for both exist a delta of the position, that
     * can be <code>"di"</code> or <code>"dj"</code>, respectively.
     *
     * @param axis  the desired axis
     * @return      the position of the hit in the axis
     */
    public double getFaceCoord(String axis)
    {
        if (faceCoordinates.containsKey(axis))
            return faceCoordinates.get(axis);
        else
            return 0.0;
    }


    /**
     * Set the number of strips of the hit.
     *
     * @param n  the number of strips
     */
    public void setNStrips(int n) {
        nStrips = n;
    }


    /**
     * Get the number of strips of the hit.
     *
     * @return  the number of strips
     */
    public int getNStrips() {
        return nStrips;
    }


    /**
     * Set the hit in the desired layer that matches this hit.
     *
     * @param layer  the matching layer
     * @param hit    the hit matching this hit in that layer
     */
    public void setMatch(ECLayer layer, ECFitHit hit)
    {
        matched.put(layer.getName(), hit);
    }


    /**
     * Get the hit in the desired layer that matches this hit.
     *
     * @param layer  the matching layer
     * @return       the hit matching this hit in that layer
     */
    public ECFitHit getMatch(ECLayer layer)
    {
        return matched.get(layer.getName());
    }


    /**
     * Set the c2match of the hit with the given layer.
     *
     * @param layer  the matching layer
     * @param value  the c2match of the hit with the layer
     */
    public void setC2Match(ECLayer layer, double value)
    {
        c2matched.put(layer.getName(), value);
    }


    /**
     * Get the c2match of the hit with the given layer.
     *
     * @param layer  the matching layer
     * @return       the c2 match of the hit with the layer
     */
    public double getC2Match(ECLayer layer)
    {
        return c2matched.get(layer.getName());
    }


    /**
     * Get the correlative identification number of the hit.
     *
     * @return  the ID number
     */
    public int getID()
    {
        return ID;
    }
}
