package org.ec.util;

import org.ec.detector.ECGeneral;
import org.ec.detector.ECLayer;
import org.ec.detector.ECLayerName;
import org.ec.detector.ECSector;
import org.ec.fit.ECFitHit;

/**
 * This class is used to match hits between layers.  Assume that particles
 * trajectory are straight line starting at the origin.
 * <p>
 * <font size = 1>JSA: Thomas Jefferson National Accelerator Facility<br>
 * This software was developed under a United States Government license,<br>
 * described in the NOTICE file included as part of this distribution.<br>
 * Copyright (c), Feb 23, 2011</font>
 *
 * @author      smancill
 * @version     0.1
 */
public class ECMatchFinder
{
    ECSector sector;

    ECLayer  whole;
    ECLayer  inner;
    ECLayer  outer;
    ECLayer  cover;


    /**
     * Construct an object to match hits in different layers.
     *
     * @param sector  the sector object with all the data
     * @see           ECSector
     */
    public ECMatchFinder(ECSector sector)
    {
        this.sector = sector;

        whole = sector.getLayer(ECLayerName.WHOLE);
        inner = sector.getLayer(ECLayerName.INNER);
        outer = sector.getLayer(ECLayerName.OUTER);
        cover = sector.getLayer(ECLayerName.COVER);
    }


    /**
     * Project all hits to the front face of EC.  Use the <code>(I,J)</code>
     * coordinate system.
     */
    public void projectAllHits()
    {
        for (ECLayer layer : sector.getLayerList()) {
            for (ECFitHit hit : layer.getHitList()) {
                double[] pos = new double[6];
                pos[0] = hit.getClasCoord("x");
                pos[1] = hit.getClasCoord("y");
                pos[2] = hit.getClasCoord("z");
                pos[3] = pos[0] / Math.sqrt(pos[0] * pos[0] + pos[1] * pos[1] + pos[2] * pos[2]);
                pos[4] = pos[1] / Math.sqrt(pos[0] * pos[0] + pos[1] * pos[1] + pos[2] * pos[2]);
                pos[5] = pos[2] / Math.sqrt(pos[0] * pos[0] + pos[1] * pos[1] + pos[2] * pos[2]);

                // TODO find what the hell is n2sect
                // double costh = pos[3] * sector.getN2(1) + pos[4] * sector.getN2(1) + pos[5] * sector.getN2(3);
                double costh = 0;

                double i    = hit.getLocalCoord("i");
                double j    = hit.getLocalCoord("j");
                double radm = Math.sqrt(i * i + j * j);

                double ci;
                double cj;
                if (radm < 1E-8) {
                    ci = 0;
                    cj = 0;
                } else {
                    ci = i / radm;
                    cj = j / radm;
                }

                double radp = layer.getDepth() * Math.tan(Math.acos(costh));

                // Save the coordinates on the face of EC for later
                hit.setFaceCoord("i", i - radp * ci);
                hit.setFaceCoord("j", j - radp * cj);
                hit.setThick(layer.getDepth() / costh);
            }
        }
    }


    /**
     * Match hits in inner layer.  Assumption is that every inner hit should
     * associate with whole hit, but not necessary whit outer.
     */
    public void matchInnerLayer()
    {
        // First match inners with whole.
        matchInnerWithWhole();
        matchInnerWithOuter();

        // These for the preshower detector.  Matching will be done only with inner.
        matchInnerWithCover();
    }


    /**
     * Match hits in outer layer.  Assumption is that every outer hit should
     * associate with whole hit, but not necessary whit inner.
     */
    public void matchOuterLayer()
    {
        matchOuterWithWhole();
    }


    private void matchInnerWithWhole()
    {
        if (inner.getNHits() > 0 && whole.getNHits() > 0) {
            for (ECFitHit innerHit : inner.getHitList()) {
                if (innerHit.getMatch(whole) != null) continue;

                double closest = ECGeneral.EC_MATCH;
                double closer  = closest;

                ECFitHit wholeMatch = null;

                for (ECFitHit wholeHit : whole.getHitList()) {
                    if (wholeHit.getMatch(inner) != null) {
                        closest = Math.min(closest, wholeHit.getC2Match(inner));
                    }

                    double idiff = Math.pow(innerHit.getFaceCoord("i") - wholeHit.getFaceCoord("i"), 2) /
                                    (Math.pow(wholeHit.getFaceCoord("di"), 2) + Math.pow(innerHit.getFaceCoord("di"), 2));
                    double jdiff = Math.pow(wholeHit.getFaceCoord("j") - wholeHit.getFaceCoord("j"), 2) /
                                    (Math.pow(wholeHit.getFaceCoord("dj"), 2) + Math.pow(innerHit.getFaceCoord("dj"), 2));
                    double diff  = idiff + jdiff;

                    if (diff < closest) {
                        closest    = diff;
                        closer     = closest;
                        wholeMatch = wholeHit;
                    } else {
                        closest    = closer;
                    }
                }  // End loop over hits of whole

                if (wholeMatch != null) {
                    ECFitHit innerMatch = wholeMatch.getMatch(inner);
                    if (innerMatch != null) {
                        double idiff = Math.pow(innerHit.getFaceCoord("i") - innerMatch.getFaceCoord("i"), 2) /
                                        (Math.pow(innerMatch.getFaceCoord("di"), 2) + Math.pow(innerHit.getFaceCoord("di"), 2));
                        double jdiff = Math.pow(innerHit.getFaceCoord("j") - innerMatch.getFaceCoord("j"), 2) /
                                        (Math.pow(innerMatch.getFaceCoord("dj"), 2) + Math.pow(innerHit.getFaceCoord("dj"), 2));
                        double diff  = idiff + jdiff;

                        if (diff < closest) {
                            sector.substractMatch(inner, whole);
                            innerMatch.setMatch(whole, null);
                        }
                    }

                    sector.addMatch(inner, whole);
                    innerHit.setMatch(whole, wholeMatch);
                    innerHit.setC2Match(whole, closest);
                    wholeMatch.setMatch(inner, innerHit);
                    wholeMatch.setC2Match(inner, closest);

                    double time = innerHit.getTime() - innerHit.getThick();
                    wholeMatch.setTime(time);
                }
            } // End loop over hits of inner
        }
    }


    private void matchInnerWithOuter()
    {
        if (inner.getNHits() > 0 && outer.getNHits() > 0) {
            for (ECFitHit innerHit : inner.getHitList()) {
                if (innerHit.getMatch(outer) != null) continue;

                double closest = 20;
                double closer  = closest;

                ECFitHit outerMatch = null;

                for (ECFitHit outerHit : outer.getHitList()) {
                    if (outerHit.getMatch(inner) != null) {
                        closest = Math.min(closest, outerHit.getC2Match(inner));
                    }

                    double idiff = Math.pow(innerHit.getFaceCoord("i") - outerHit.getFaceCoord("i"), 2) /
                                    (Math.pow(outerHit.getFaceCoord("di"), 2) + Math.pow(innerHit.getFaceCoord("di"), 2));
                    double jdiff = Math.pow(innerHit.getFaceCoord("j") - outerHit.getFaceCoord("j"), 2) /
                                    (Math.pow(outerHit.getFaceCoord("dj"), 2) + Math.pow(innerHit.getFaceCoord("dj"), 2));
                    double diff  = idiff + jdiff;

                    if (diff < closest) {
                        closest    = diff;
                        closer     = closest;
                        outerMatch = outerHit;
                    } else {
                        closest    = closer;
                    }
                }  // End loop over hits of outer

                if (outerMatch != null) {
                    ECFitHit innerMatch = outerMatch.getMatch(inner);
                    if (innerMatch != null) {
                        double idiff = Math.pow(innerHit.getFaceCoord("i") - innerMatch.getFaceCoord("i"), 2) /
                                        (Math.pow(innerMatch.getFaceCoord("di"), 2) + Math.pow(innerHit.getFaceCoord("di"), 2));
                        double jdiff = Math.pow(innerHit.getFaceCoord("j") - innerMatch.getFaceCoord("j"), 2) /
                                        (Math.pow(innerMatch.getFaceCoord("dj"), 2) + Math.pow(innerHit.getFaceCoord("dj"), 2));
                        double diff  = idiff + jdiff;

                        if (diff < closest) {
                            sector.substractMatch(inner, outer);
                            innerMatch.setMatch(outer, null);

                            ECFitHit wholeMatch = outerMatch.getMatch(whole);
                            if (wholeMatch != null) {
                                sector.substractMatch(outer, whole);
                                outerMatch.setMatch(whole, null);
                                wholeMatch.setMatch(outer, null);
                            }
                        }
                    }

                    sector.addMatch(inner, outer);
                    innerHit.setMatch(outer, outerMatch);
                    innerHit.setC2Match(outer, closest);
                    outerMatch.setMatch(inner, innerHit);
                    outerMatch.setC2Match(inner, closest);

                    ECFitHit wholeMatch = innerHit.getMatch(whole);
                    if (wholeMatch != null) {
                        sector.addMatch(outer, whole);
                        outerMatch.setMatch(whole, wholeMatch);
                        wholeMatch.setMatch(outer, outerMatch);

                        double idiff = Math.pow(wholeMatch.getFaceCoord("i") - outerMatch.getFaceCoord("i"), 2) /
                                        (Math.pow(wholeMatch.getFaceCoord("di"), 2) + Math.pow(outerMatch.getFaceCoord("di"), 2));
                        double jdiff = Math.pow(wholeMatch.getFaceCoord("j") - outerMatch.getFaceCoord("j"), 2) /
                                        (Math.pow(outerMatch.getFaceCoord("dj"), 2) + Math.pow(wholeMatch.getFaceCoord("dj"), 2));
                        double diff  = idiff + jdiff;

                        outerMatch.setC2Match(whole, diff);
                        wholeMatch.setC2Match(outer, diff);

                        double timeInner = (innerHit.getTime()   - innerHit.getThick()   / ECGeneral.SPEED_OF_LIGHT) * innerHit.getEnergy();
                        double timeOuter = (outerMatch.getTime() - outerMatch.getThick() / ECGeneral.SPEED_OF_LIGHT) * outerMatch.getEnergy();
                        double sumEnergy = innerHit.getEnergy() + outerMatch.getEnergy();

                        double time = (timeInner + timeOuter) / sumEnergy;
                        wholeMatch.setTime(time);
                    }
                }
            }  // End loop over hits of inner
        }
    }


    private void matchInnerWithCover()
    {
        if (inner.getNHits() > 0 && cover.getNHits() > 0) {
            for (ECFitHit innerHit : inner.getHitList()) {
                if (innerHit.getMatch(cover) != null) continue;

                double closest = 20;
                double closer  = closest;

                ECFitHit coverMatch = null;

                for (ECFitHit coverHit : cover.getHitList()) {
                    if (coverHit.getMatch(inner) != null) {
                        closest = Math.min(closest, coverHit.getC2Match(inner));
                    }
                    double idiff = Math.pow(innerHit.getFaceCoord("i") - coverHit.getFaceCoord("i"), 2) /
                                    (Math.pow(coverHit.getFaceCoord("di"), 2) + Math.pow(innerHit.getFaceCoord("di"), 2));
                    double jdiff = Math.pow(innerHit.getFaceCoord("j") - coverHit.getFaceCoord("j"), 2) /
                                    (Math.pow(coverHit.getFaceCoord("dj"), 2) + Math.pow(innerHit.getFaceCoord("dj"), 2));
                    double diff  = idiff + jdiff;

                    if (diff < closest) {
                        closest    = diff;
                        closer     = closest;
                        coverMatch = coverHit;
                    } else {
                        closest    = closer;
                    }
                }  // End loop over hits of cover

                if (coverMatch != null) {
                    ECFitHit innerMatch = coverMatch.getMatch(inner);
                    if (innerMatch != null) {
                        sector.substractMatch(inner, cover);
                        innerMatch.setMatch(cover, null);
                        ECFitHit wholeMatch = coverMatch.getMatch(whole);
                        if (wholeMatch != null) {
                            sector.substractMatch(cover, whole);
                            coverMatch.setMatch(whole, null);
                            wholeMatch.setMatch(cover, null);
                        }
                    }

                    sector.addMatch(inner, cover);
                    innerHit.setMatch(cover, coverMatch);
                    innerHit.setC2Match(cover, closest);
                    coverMatch.setMatch(inner, innerHit);
                    coverMatch.setC2Match(inner, closest);

                    ECFitHit wholeMatch = innerHit.getMatch(whole);
                    if (wholeMatch != null) {
                        sector.addMatch(cover, whole);
                        coverMatch.setMatch(whole, wholeMatch);
                        wholeMatch.setMatch(cover, coverMatch);

                        double idiff = Math.pow(wholeMatch.getFaceCoord("i") - coverMatch.getFaceCoord("i"), 2) /
                                        (Math.pow(wholeMatch.getFaceCoord("di"), 2) + Math.pow(coverMatch.getFaceCoord("di"), 2));
                        double jdiff = Math.pow(wholeMatch.getFaceCoord("j") - coverMatch.getFaceCoord("j"), 2) /
                                        (Math.pow(coverMatch.getFaceCoord("dj"), 2) + Math.pow(wholeMatch.getFaceCoord("dj"), 2));
                        double diff  = idiff + jdiff;

                        coverMatch.setC2Match(whole, diff);
                        wholeMatch.setC2Match(cover, diff);

                        double timeInner = (innerHit.getTime()   - innerHit.getThick()   / ECGeneral.SPEED_OF_LIGHT) * innerHit.getEnergy();
                        double timeCover = (coverMatch.getTime() - coverMatch.getThick() / ECGeneral.SPEED_OF_LIGHT) * coverMatch.getEnergy();
                        double sumEnergy = innerHit.getEnergy() + coverMatch.getEnergy();

                        double time = (timeInner + timeCover) / sumEnergy;
                        wholeMatch.setTime(time);
                    }
                }
            } // End loop over hits of inner
        }
    }


    private void matchOuterWithWhole()
    {
        if (outer.getNHits() > 0 && whole.getNHits() > 0) {
            for (ECFitHit outerHit : outer.getHitList()) {
                if (outerHit.getMatch(whole) != null) continue;

                double closest = 20;
                double closer  = closest;

                ECFitHit wholeMatch = null;

                for (ECFitHit wholeHit : whole.getHitList()) {
                    if (wholeHit.getMatch(outer) != null) {
                        closest = Math.min(closest, wholeHit.getC2Match(outer));
                    }
                    double idiff = Math.pow(outerHit.getFaceCoord("i") - wholeHit.getFaceCoord("i"), 2) /
                                    (Math.pow(wholeHit.getFaceCoord("di"), 2) + Math.pow(outerHit.getFaceCoord("di"), 2));
                    double jdiff = Math.pow(outerHit.getFaceCoord("j") - wholeHit.getFaceCoord("j"), 2) /
                                    (Math.pow(wholeHit.getFaceCoord("dj"), 2) + Math.pow(outerHit.getFaceCoord("dj"), 2));
                    double diff  = idiff + jdiff;

                    if (diff < closest) {
                        closest    = diff;
                        closer     = closest;
                        wholeMatch = wholeHit;
                    } else {
                        closest    = closer;
                    }
                } // End loop over hits of whole

                if (wholeMatch != null) {
                    ECFitHit outerMatch = wholeMatch.getMatch(outer);
                    if (outerMatch != null) {
                        sector.substractMatch(outer, whole);
                        outerMatch.setMatch(whole, null);
                    }

                    sector.addMatch(outer, whole);
                    outerHit.setMatch(whole, wholeMatch);
                    outerHit.setC2Match(whole, closest);
                    wholeMatch.setMatch(outer, outerHit);
                    wholeMatch.setC2Match(outer, closest);
                }

                double time = outerHit.getTime() - outerHit.getThick();
                wholeMatch.setTime(time);
            } // End loop over hits of outer
        }
    }
}
