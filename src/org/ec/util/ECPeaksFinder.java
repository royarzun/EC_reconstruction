package org.ec.util;

import java.util.Comparator;

import org.ec.detector.ECGeneral;
import org.ec.detector.ECLayer;
import org.ec.detector.ECSector;
import org.ec.detector.ECStrip;
import org.ec.detector.ECView;
import org.ec.fit.ECFitPeak;
import org.ec.fit.ECPeakHit;

/**
 * The <code>ECPeaksFinder</code> class fits strips to peaks.
 * <p>
 * The algorithm has two sections.  The first section is used for the first
 * pass through the data to find the peaks from groups of strips.  The second
 * section refines the estimate of the peak locations by making attenuation
 * length corrections to the data and re-calculating the mean and RMS.
 * <p>
 * For the <b>first section</b>, defined by the method {@link #findPeaks
 * findPeaks}, the code idenfities the {@link ECFitPeak peaks}: a
 * <em>peak</em> is a collection of strips in each view.  The strips in a view
 * have the following characteristics:
 * <ol>
 *   <li>the energy deposit is above a threshold value given by the TCL
 *       variable {@link ECGeneral#STRIP_THRESHOLD STRIP_THRESHOLD}.
 *   <li>the strips are immediately adjacent to each other, or they are not
 *       separated by any more strips than is specified by the TCL variable
 *       <code>touch_id</code>.  If <code>touch_id = 1</code>, no missing
 *       strips are allowed in the peak.  If <code>touch_id = 2</code>, one
 *       missing strip is allowed, if equals to 3, two are allowed, etc.
 * </ol>
 * There are other limits imposed as well: the maximum number of groups is
 * limited by the parameter {@link ECGeneral#MAX_PEAKS MAX_PEAKS}.  Note that
 * the <em>threshold</em> has energy units, but it is actually not corrected
 * for attenuation lengths, so the threshold is actually for pulse size, not
 * deposited energy.
 * <p>
 * Next, the peaks are re-ordered from being sequential in strip number to
 * being sequential in energy deposit, the first entry being the strip with
 * the largest energy deposit.  The centroid and RMS of each peak is then
 * calculated (with a prescription for calculating the RMS for groups which
 * have only one strip).
 * <p>
 * For the <b>second section</b>, defined by the method {@link #findPeaks
 * findPeaks}, the code does the following: for each strip in each peak, the
 * energy is corrected for attenuation length.  If layer is equal to
 * <em>WHOLE</em> (reconstruction), the <em>INNER</em> and <em>OUTER</em>
 * energies are added together for each strip number, otherwise they are not.
 * <p>
 * The <em>INNER</em> or <em>OUTER</em> times for a given view are calculated
 * by averaging all the strip times (minus transit time in the scintillator)
 * for each strip in the peak using the square root of the adc value for a
 * weight.  The <em>WHOLE</em> time is not calculated in this section, but is
 * calculated by the class {@link ECHitsFinder}, using only the <em>INNER</em>
 * and/or <em>OUTER</em> mean times for each hit.
 * <p>
 * The peak energy in a given view and layer is obtained by adding all the
 * corrected energies in the peak together.  The centroid and RMS are
 * calculated; these are calculated using either of two weights: either
 * weighted by the energy in the strip, or by the logarithm of the energy in
 * the strip.  The two options are selected by the TCL variable
 * {@link ECGeneral#LN_WEIGHTS LN_WEIGHTS}.  The second moment, third moment,
 * and fourth moment of the distribution are calculated, and the method
 * returns.
 * <p>
 * <font size = 1>JSA: Thomas Jefferson National Accelerator Facility<br>
 * This software was developed under a United States Government license,<br>
 * described in the NOTICE file included as part of this distribution.<br>
 * Copyright (c), Feb 22, 2011</font>
 *
 * @author      smancill
 * @version     0.1
 */
public class ECPeaksFinder
{
    @SuppressWarnings("unused")
    private ECSector  sector;
    private ECHitMaps map;

    private int maxStrips;
    private double swId;


    /**
     * Construct an object to find peaks from strips.  See class documentation
     * for the algorithm explanation.
     *
     * @param sector  the sector object with all the data
     * @param map     the map object with all the extra maps
     * @see           ECSector
     */
    public ECPeaksFinder(ECSector sector, ECHitMaps map)
    {
        this.sector = sector;
        this.map    = map;

        this.maxStrips = 0;
        this.swId      = 0;
    }


    /**
     * Find the peaks in all the views of the layer.  This is the first
     * section of the algorithm.  See the class documentation.
     *
     * @param layer  the layer object with the data
     * @see          ECLayer
     */
    public void findPeaks(ECLayer layer)
    {
        maxStrips = layer.getMaxStrips();

        for (ECView view : layer.getViewList()) {
            swId = layer.getEdgeL(view.getLabel()) / this.maxStrips;
            getPeaksFromStrips(view);
            if (view.getNPeaks() > 0) {
                sortPeaks(view);
                getPeaksPosition(view);
            }
        }
    }


    /**
     * Correct the the peak energies in all the views of the layer.  This is
     * the second section of the algorithm.  See the class documentation.  The
     * service needs to call first the {@link ECHitsFinder#findHits findHits}
     * method to get all the posible hits from the found peaks.
     *
     * @param layer  the layer object with the data
     * @see          ECLayer
     */
    public void correctPeaks(ECLayer layer)
    {
        for (ECView view : layer.getViewList()) {
            attenuationLenght(view);
        }
    }


    private void getPeaksFromStrips(ECView view)
    {
        // Artificial, so not touch with any strip
        int id = -1 - ECGeneral.TOUCH_ID;
        ECFitPeak new_peak = null;

        if (view.getNStrips() <= 0 || view.getNStrips() > maxStrips) {
            // Skip event
            view.clearPeakList();
            return;
        } else if (ECGeneral.TOUCH_ID > 0) {
            for (ECStrip strip : view.getStripList()) {
                if (strip.getID() <= 0 || strip.getID() >= maxStrips)
                    // TODO Use recmes function to print error
                    // Bad strip ID
                    continue;
                if (strip.getEnergy() > ECGeneral.STRIP_THRESHOLD) {
                    if (strip.getID() - id > ECGeneral.TOUCH_ID) {
                        new_peak = view.newPeak();
                        if (view.getNPeaks() > ECGeneral.MAX_PEAKS) {
                            // Skip event
                            view.clearPeakList();
                            return;
                        }

                        map.addPeak(new_peak);
                    }
                    id = strip.getID();
                    strip.setPeakEfr(1.0);
                    double e = new_peak.getEnergy() + strip.getEnergy();
                    new_peak.setEnergy(e);
                    new_peak.addStrip(strip);
                }
            }
        } else if (ECGeneral.TOUCH_ID == 0) {
            // TODO Call group0()
        }
    }


    private void sortPeaks(ECView view)
    {
        // Sort peaks by decreasing energy
        view.sortPeaks(new OrderByDecreasingEnergy());

        // Delete peaks with energy below the threshold
        int cont = 0;
        for (ECFitPeak peak : view.getPeakList()) {
            if (peak.getEnergy() < ECGeneral.PEAK_THRESHOLD) break;
            cont++;
        }

        view.resizePeakList(cont);
    }


    private void getPeaksPosition(ECView view)
    {
        // Calculate DIST and WIDTH for every peak
        for (ECFitPeak peak : view.getPeakList()) {
            double sumEprj     = 0;
            double sumEprj2    = 0;
            double sum_weights = 0;

            for (ECStrip strip : peak.getStripList()) {
                double dprj = strip.getID() * swId - swId / 2; // Position of the strip
                double dE   = strip.getEnergy();
                if (ECGeneral.LN_WEIGHTS) {
                    dE = Math.log(10000 * dE);
                } else {
                    dE = 0;
                }

                sumEprj     += dE * dprj;
                sumEprj2    += dE * dprj * dprj;
                sum_weights += dE;

                double energy = strip.getEnergy() * strip.getPeakEfr();
                strip.setPeakEnergy(energy);
            }

            // Centroid
            double cntrd = sumEprj / sum_weights;   // Av. Position (first moment)
            // Set RMS width
            double width;
            if (peak.getNStrips() > 1) {
                double width2 = sumEprj2 / sum_weights - cntrd * cntrd;
                width  = Math.sqrt(Math.abs(width2));
            } else {
                width = swId / Math.sqrt(12.0);
            }

            peak.setWidth(width);
            peak.setDist(cntrd);            // Position on axis
        }
    }


    private void attenuationLenght(ECView view)
    {
        for (ECFitPeak peak : view.getPeakList()) {
            if (map.getNHits(peak) > 0) {
                double shortestPath = 1000;
                double highestAdc   = 0;
                @SuppressWarnings("unused")
                int    highestAdcID = 0;

                for (ECPeakHit hit : map.getHitList(peak)) {
                    double path = hit.getPath();
                    if (path < shortestPath)
                        shortestPath = path;
                }

                for (ECStrip strip : peak.getStripList()) {
                    double radc = strip.getRawAdcs();
                    int    id   = strip.getID();
                    if (radc > highestAdc && strip.getTime() > 0 && view.calTrms[id] > 0) {
                        highestAdc   = radc;
                        highestAdcID = id;
                    }
                }

                for (ECPeakHit hit : map.getHitList(peak)) {
                    @SuppressWarnings("unused")
                    double path       = hit.getPath();
                    double sumEprj    = 0;
                    double sumEprj2   = 0;
                    double sumEprj3   = 0;
                    double sumEprj4   = 0;
                    double sumWeights = 0;

                    // TODO iterate over strips

                    if (sumWeights < 1E-6) sumWeights = 1E-6;
                    if (sumWeights > 1E6)  sumWeights = 1E6;

                    double cntrd = sumEprj / sumWeights;

                    double width;
                    if (peak.getNStrips() > 1) {
                        double width2 = sumEprj2 / sumWeights - cntrd * cntrd;
                        width = Math.sqrt(Math.abs(width2));
                    } else {
                        width = Math.sqrt(12.0);
                    }
                    hit.setWidth(width);

                    sumEprj2 = 0;
                    sumEprj4 = 0;
                    for (ECStrip strip : peak.getStripList()) {
                        double dE = strip.getEnergy();
                        if (ECGeneral.LN_WEIGHTS) {
                            if (dE > 10000)
                                dE = Math.log(10000 * dE);
                            else
                                dE = 0;
                        }

                        double dprj = strip.getID() * swId - swId / 2 - cntrd;

                        sumEprj2   += dE * dprj * dprj;
                        sumEprj3   += dE * dprj * dprj * dprj;
                        sumEprj4   += dE * dprj * dprj * dprj * dprj;
                        sumWeights += dE;
                    }

                    if (sumWeights < 1E-6) sumWeights = 1E-6;
                    if (sumWeights > 1E6)  sumWeights = 1E6;

                    double myEprj2 = sumEprj2 / sumWeights;
                    if (Math.abs(myEprj2) < 1E-8) myEprj2 = 1E-8;
                    if (Math.abs(myEprj2) > 1E8)  myEprj2 = 1E8 * Math.signum(myEprj2);

                    double myEprj3 = sumEprj3 / sumWeights;
                    if (Math.abs(myEprj3) < 1E-8) myEprj3 = 1E-8;
                    if (Math.abs(myEprj3) > 1E8)  myEprj3 = 1E8 * Math.signum(myEprj3);

                    double myEprj4 = sumEprj4 / sumWeights;
                    if (Math.abs(myEprj4) < 1E-8) myEprj4 = 1E-8;
                    if (Math.abs(myEprj4) > 1E8)  myEprj4 = 1E8 * Math.signum(myEprj4);

                    hit.setDist(cntrd);
                    hit.setMoment(2, myEprj2);
                    hit.setMoment(3, myEprj3);
                    hit.setMoment(4, myEprj4);
                }
            }
        }
    }


    // TODO check if the order is OK
    class OrderByDecreasingEnergy implements Comparator<ECFitPeak>
    {
        public int compare(ECFitPeak p1, ECFitPeak p2)
        {
            if (p1.getEnergy() > p2.getEnergy())
                return -1;
            else if (p1.getEnergy() == p2.getEnergy())
                return 0;
            else
                return 1;
        }
    }
}
