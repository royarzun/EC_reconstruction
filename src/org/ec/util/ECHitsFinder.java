package org.ec.util;

import java.util.Comparator;
import java.util.HashMap;

import org.ec.detector.ECGeneral;
import org.ec.detector.ECLayer;
import org.ec.detector.ECLayerName;
import org.ec.detector.ECSector;
import org.ec.detector.ECView;
import org.ec.detector.ECViewLabel;
import org.ec.fit.ECFitHit;
import org.ec.fit.ECFitPeak;
import org.ec.fit.ECPeakHit;

/**
 * The <code>ECHitsFinder</code> class fits peaks to hits.
 * <p>
 * This class is used after the class {@link ECPeaksFinder} has been called to
 * find the peaks for each view of the current layer, and it tries to match
 * them together into {@link ECFitHit hits}.
 * <p>
 * For the <b>first section</b>, defined by the method {@link #findHits
 * findHits}, there is a threefold loop which looks at all possible
 * combinations of peaks in the <em>U</em>, <em>V</em>, and <em>W</em> views.
 * For each iteration of the loop, the {@link ECDalitz dalitz} condition is
 * checked using the centroids in each of the three views, using the RMS as an
 * estimate of the uncertainty in each view. A limit is imposed on the number
 * of possible hits by the parameter {@link ECGeneral#MAX_HITS MAX_HITS},
 * which is currently defined in as being equal to 10. If this limit is
 * exceeded, the event is not analyzed.
 * <p>
 * If the <em>dalitz</em> condition is satisfied (geometrically plausible
 * event), the path from the hit position along the strip is calculated, and
 * the path length, identity of the peaks, and identity of the hit is stored
 * and the threefold loop ends.
 * <p>
 * Now that the path lengths are known for all three views, the control
 * returns to service, who call the second section of the {@link
 * ECPeaksFinder} algorithm, to re-calculates the peak characteristics after
 * doing an attenuation length correction, so that in principle a more
 * accurate centroid, RMS and higher moments are obtained. 
 * <p>
 * For the <b>second section</b>, defined by the method {@link #correctEnergy
 * correctEnergy}, peaks which are part of more than one hit are treated. If
 * for all hits there is only one peak in each view, then the routine proceeds
 * to the next step. If a given peak contributes to multiple hits, then the
 * energy in each hit due to that peak is calculated as being proportional to
 * the relative sizes of the multiple hits as measured in other views.  That
 * is, if there are two hits, both of which have the same <em>U</em> peak, the
 * energy in v and w is added for each of the hits, and the ratio of these
 * summed energies determines how much of the u peak's energy is assigned to
 * each of the two hits. The code does not attempt to handle more complicated
 * events, which, of course, are topologically possible.
 * <p>
 * For the <b>third section</b>, defined by the method {@link #correctHits
 * correctHits}, the hit is again checked for the <em>dalitz</em> condition,
 * using the refined (attenuation-length-corrected) centroid and RMS values;
 * at the present time no action is taken based on the results of this check.
 * Next the hits are sorted by energy, and a threshold cut is applied. This
 * threshold value is set by the TCL parameter {@link ECGeneral#HIT_THRESHOLD
 * HIT_THRESHOLD} and it is a true energy threshold since attenuation
 * corrections have already been applied. After this, a number of
 * characteristics of the hit are stored, and the method returns.
 * <p>
 * <font size = 1>JSA: Thomas Jefferson National Accelerator Facility<br>
 * This software was developed under a United States Government license,<br>
 * described in the NOTICE file included as part of this distribution.<br>
 * Copyright (c), Feb 22, 2011</font>
 *
 * @author      smancill
 * @version     0.1
 */
public class ECHitsFinder
{
    private ECSector  sector;
    private ECHitMaps map;
    private boolean needCalculation;
    
    private HashMap<String, Integer> peakStatus;
    
    /**
     * Construct an object to find hits from peaks.  See class documentation
     * for the algorithm explanation.
     *
     * @param sector  the sector object with all the data
     * @param map     the map object with all the extra maps
     * @see           ECSector
     */
    public ECHitsFinder(ECSector sector, ECHitMaps map)
    {
        this.sector = sector;
        this.map    = map;
        this.needCalculation = true;
    }
    
    
    /**
     * Find the hits from all the combinations of peaks in the layer.  This is
     * the first section of the algorithm.  See the class documentation. The
     * service needs to call first the {@link ECPeaksFinder#findPeaks findPeaks}
     * method to get all the peaks hits from the all the views.
     *
     * @param layer  the layer object with the data
     * @see          ECLayer
     */
    public void findHits(ECLayer layer)
    {
        for (ECFitPeak pu : layer.getView(ECViewLabel.U).getPeakList()) {
            for (ECFitPeak pv : layer.getView(ECViewLabel.V).getPeakList()) {
                for (ECFitPeak pw : layer.getView(ECViewLabel.W).getPeakList()) {
                    if (getPeakStatus(pu, pv, pw) == 0) {
                        ECDalitz dalitz = new ECDalitz(layer, pu, pv, pw, 1);
                        
                        if (dalitz.isPoint() && layer.getNHits() >= ECGeneral.MAX_HITS) {
                            // Event is skipped
                            layer.clearHitList();
                            return;
                        }
                        
                        if (dalitz.isPoint()) {
                            ECFitHit hit = layer.newHit(pu, pv, pw);
                            hit.setCh2(dalitz.getError());
                            
                            double i = dalitz.getProjection("i");
                            double j = dalitz.getProjection("j");
                            
                            ECPath path = new ECPath(layer, i, j);
                            
                            double u = path.getU();
                            double v = path.getV();
                            double w = path.getW();
                            
                            hit.setPaths(u, v, w);
                            
                            map.addHit(hit);
                        }
                    }
                }
            }
        }
        
        needCalculation = false;
    }


    /**
     * Treat peaks which are part of more than one hit.  This is the second
     * section of the algorithm.  See the class documentation. The service
     * needs to call first the {@link ECPeaksFinder#correctPeaks correctPeaks}
     * method to correct the energies of the peaks.
     *
     * @param layer  the layer object with the data
     * @see          ECLayer
     */
    public void correctEnergy(ECLayer layer)
    {
        // Count how many peaks belong to a single hit
        int hitCount = 0;
        for (ECFitHit hit : layer.getHitList()) {
            for (ECPeakHit ph : hit.getAllPeakHits()) {
                if (map.getNHits(ph.getPeak()) == 1) {
                    ph.setHitFraction(1.0);
                    hitCount++;
                } else {
                    ph.setHitFraction(0.0);
                }
            }
        }
        
        // If each peak belongs to a single hit (cluster), go to determination
        // of energy and time of clusters
        if (hitCount == 3 * layer.getNHits())
            return;
        
        // For peaks that belong to more than one cluster, calculate energy
        // fractions
        for (ECView view : layer.getViewList()) {
            for (ECFitPeak peak : view.getPeakList()) {
                if (map.getNHits(peak) > 1) {
                    double sumE0    = 0.0;
                    double[] sumE   = new double[map.getNHits(peak)];
                    double[] nValid = new double[map.getNHits(peak)];
                    
                    int i = 0;
                    for (ECPeakHit ph : map.getHitList(peak)) {
                        sumE[i]   = 0.0;
                        nValid[i] = 0;
                        
                        // Find peaks on other two edges that are involved
                        // in this cluster only
                        for (ECPeakHit other : ph.getOtherPeakHits()) {
                            if (map.getNHits(other.getPeak()) == 1) {
                                sumE[i] += other.getEnergy() * other.getHitFraction();
                                nValid[i]++;
                            }
                        }
                        
                        // At least one of the peaks should belong to one cluster
                        if (nValid[i] > 0) sumE[i] = sumE[i] / nValid[i];
                        sumE0 += sumE[i];
                        i++;
                    }
                    
                    if (sumE0 <= 0) break;
                    
                    i = 0;
                    for (ECPeakHit ph : map.getHitList(peak)) {
                        ph.setHitFraction(sumE[i] / sumE0);
                        i++;
                    }
                    
                    hitCount += map.getNHits(peak);
                    map.setOneHit(peak);
                }
            }
        }
        
        if (hitCount == 0) {
            for (ECFitHit hit1 : layer.getHitList()) {
                for (ECFitHit hit2 : layer.getHitList(hit1.getID())) {
                    ECFitPeak u1 = hit1.getPeakHit(ECViewLabel.U).getPeak();
                    ECFitPeak u2 = hit2.getPeakHit(ECViewLabel.U).getPeak();
                    
                    ECFitPeak v1 = hit1.getPeakHit(ECViewLabel.V).getPeak();
                    ECFitPeak v2 = hit2.getPeakHit(ECViewLabel.V).getPeak();
                    
                    ECFitPeak w1 = hit1.getPeakHit(ECViewLabel.W).getPeak();
                    ECFitPeak w2 = hit2.getPeakHit(ECViewLabel.W).getPeak();
                    
                    if (u1.getID() == u2.getID()) hitCount++;
                    if (v1.getID() == v2.getID()) hitCount++;
                    if (w1.getID() == w2.getID()) hitCount++;
                    
                    if (hitCount >= 2) {
                        needCalculation = true;
                        if (hit1.getCh2() < hit2.getCh2())
                            setPeakStatus(u2, v2, w2, -1);
                        else
                            setPeakStatus(u1, v1, w1, -1);
                    }
                }
            }
            
            if (needCalculation) {
                clearAssignments();
                layer.clearHitList();
            }
        }
    }
    
    
    /**
     * Correct all the hit energies and cut the list using a threshold.  This
     * is the third section of the algorithm.  See the class documentation.
     *
     * @param layer  the layer object with the data
     * @see          ECLayer
     */
    public void correctHits(ECLayer layer)
    {
        for (ECFitHit hit : layer.getHitList()) {
            double energy = 0;
            double time   = 0;
            double timeWe = 0;
            
            for (ECPeakHit ph : hit.getAllPeakHits()) {
                energy += ph.getEnergy();
                time   += ph.getTime();
                timeWe += ph.getTimeWe();
            }
            
            if (layer.getName() != ECLayerName.WHOLE && timeWe > 0)
                time = time / timeWe;
            else
                time = -999;
            
            hit.setEnergy(energy);
            hit.setTime(time);
            
            ECFitPeak u = hit.getPeakHit(ECViewLabel.U).getPeak();
            ECFitPeak v = hit.getPeakHit(ECViewLabel.V).getPeak();
            ECFitPeak w = hit.getPeakHit(ECViewLabel.W).getPeak();
            
            ECDalitz dalitz = new ECDalitz(layer, u, v, w, 2);
            
            hit.setLocalCoord("i",  dalitz.getProjection("i"));
            hit.setLocalCoord("j",  dalitz.getProjection("j"));
            hit.setLocalCoord("k",  dalitz.getProjection("k"));
            hit.setLocalCoord("di", dalitz.getProjection("di"));
            hit.setLocalCoord("dj", dalitz.getProjection("dj"));
            
            hit.setWidth(dalitz.getRms());
            hit.setCh2(dalitz.getError());
        }
        
        sortHits(layer);
        
        for (ECFitHit hit : layer.getHitList()) {
            int ntstripc = hit.getNStrips();
            for (ECPeakHit ph : hit.getAllPeakHits()) {
                int nstripc  = ph.getPeak().getNStrips();
                ntstripc += nstripc;
                
                // TODO Can this be moved to PeakHit constructor?
                ph.setNStrips(nstripc);
                
                // TODO Iterate over strips
            }
            
            // TODO Can this be moved to constructors?
            hit.setNStrips(ntstripc);
                
            
            // TODO Find where the hell coord. k is set
            ECCoordTransfer ct = new ECCoordTransfer(sector,
                                                hit.getLocalCoord("i"),
                                                hit.getLocalCoord("j"),
                                                hit.getLocalCoord("k"),
                                                2);
            
            hit.setClasCoord("x", ct.getCoordinate("x"));
            hit.setClasCoord("y", ct.getCoordinate("y"));
            hit.setClasCoord("z", ct.getCoordinate("z"));
            
            
            ECCoordTransfer ctd = new ECCoordTransfer(sector,
                                            hit.getLocalCoord("di"),
                                            hit.getLocalCoord("dj"),
                                            hit.getLocalCoord("dk"),
                                            2);
            
            hit.setClasCoord("dx", ctd.getCoordinate("dx"));
            hit.setClasCoord("dy", ctd.getCoordinate("dy"));
            hit.setClasCoord("dz", ctd.getCoordinate("dz"));
        }
    }

    
    private void sortHits(ECLayer layer)
    {
        // Sort hits by decreasing energy
        layer.sortHits(new OrderByDecreasingEnergy());
        
        // Delete peaks with energy below the threshold
        int cont = 0;
        for (ECFitHit hit : layer.getHitList()) {
            if (hit.getEnergy() < ECGeneral.HIT_THRESHOLD) break;
            cont++;
        }
        
        layer.resizeHitList(cont);
    }

    
    /**
     * Check if the algorithm have to be executed.
     *
     * @return  true to execute the algorithm, false to not
     * @see     #initializePeakStatus
     */
    public boolean calculate()
    {
        return needCalculation;
    }
    
    
    private void clearAssignments()
    {
        map.clearHitLists();
    }


    /**
     * Initialize the algorithm.  Mark all the posible combinations of three
     * peaks as GOOD.  If the algorithm needs to be executed again to
     * recalculate the list of hits, some of the peaks combinations have
     * changed its status.
     *
     * @param layer  the layer object with the data
     */
    public void initializePeakStatus(ECLayer layer)
    {
        for (ECFitPeak u : layer.getView(ECViewLabel.U).getPeakList())
            for (ECFitPeak v : layer.getView(ECViewLabel.V).getPeakList())
                for (ECFitPeak w : layer.getView(ECViewLabel.W).getPeakList())
                    setPeakStatus(u, v, w, 0);
    }
    
    
    private void setPeakStatus(ECFitPeak u, ECFitPeak v, ECFitPeak w, int value) {
        String key = u.getID() + "." + v.getID() + "." + w.getID();
        peakStatus.put(key, value);
    }
    
    
    private int getPeakStatus(ECFitPeak u, ECFitPeak v, ECFitPeak w) {
        String key = u.getID() + "." + v.getID() + "." + w.getID();
        return peakStatus.get(key);
    }
    
    
    // TODO check if the order is OK
    class OrderByDecreasingEnergy implements Comparator<ECFitHit>
    {
        public int compare(ECFitHit h1, ECFitHit h2)
        {
            if (h1.getEnergy() > h2.getEnergy())
                return -1;
            else if (h1.getEnergy() == h2.getEnergy())
                return 0;
            else
                return 1;
        }
    }
}
