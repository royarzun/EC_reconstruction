package org.ec.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import org.ec.fit.ECFitHit;
import org.ec.fit.ECFitPeak;
import org.ec.fit.ECPeakHit;

/**
 * Class used to store maps between objects of the data structure hierarchy.
 * The idea is to move those mappings out of the default classes to store data
 * and give flexibility to the algorithm working with those classes.
 * <p>
 * The <code>HitsFinderService</code> use this class to have a list of hits
 * for each peak.
 * <p>
 * <font size = 1>JSA: Thomas Jefferson National Accelerator Facility<br>
 * This software was developed under a United States Government license,<br>
 * described in the NOTICE file included as part of this distribution.<br>
 * Copyright (c), Feb 22, 2011</font>
 *
 * @author      smancill
 * @version     0.1
 */
public class ECHitMaps
{
    HashMap<String, ArrayList<ECPeakHit>> hits;


    /**
     * Construct an auxiliary map object.
     */
    public ECHitMaps()
    {
        hits = new HashMap<String, ArrayList<ECPeakHit>>();
    }


    /**
     * Add a new peak to the map.
     *
     * Put the peak in the map using the unique key that identifies the peak
     * in the data hierarchy.  Initialize its list of hits as empty.
     *
     * @param peak  the peak to be added
     */
    public void addPeak(ECFitPeak peak)
    {
        String key = peak.getKey();
        hits.put(key, new ArrayList<ECPeakHit>());
    }


    /**
     * Add the hit to the list of hits of each peak that compose it.
     * <p>
     * A hit is composed of three peaks, one for each axis.  Add that hit to
     * the end of the list of hits for each one of those peaks.
     *
     * @param hit  the hit to be mapped
     */
    public void addHit(ECFitHit hit)
    {
        for (ECPeakHit ph : hit.getAllPeakHits()) {
            ECFitPeak peak = ph.getPeak();
            String key = peak.getKey();
            hits.get(key).add(ph);
        }
    }


    /**
     * Get the list of hits that are composed by the peak.
     *
     * @param peak  the desired peak
     * @return      a {@link Collection} with the hits composed by the peak
     */
    public Collection<ECPeakHit> getHitList(ECFitPeak peak)
    {
        String key = peak.getKey();
        return Collections.unmodifiableList(hits.get(key));
    }


    /**
     * Get the number of hits that are composed by the peak.
     *
     * @param peak  the desired peak
     * @return      the number of hits
     */
    public int getNHits(ECFitPeak peak)
    {
        String key = peak.getKey();
        return hits.get(key).size();
    }


    /**
     * Set the peak as part of one single hit.
     *
     * @param peak  the desired peak
     */
    public void setOneHit(ECFitPeak peak)
    {
        String key = peak.getKey();

        ArrayList<ECPeakHit> single = new ArrayList<ECPeakHit>();
        single.add(hits.get(key).get(0));

        hits.put(key, single);
    }


    /**
     * For all the mapped peaks, clear its list of hits.
     */
    public void clearHitLists()
    {
        for (ArrayList<ECPeakHit> list : hits.values()) {
            list.clear();
        }
    }
}
