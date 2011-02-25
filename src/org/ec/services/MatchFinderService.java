package org.ec.services;

import org.ec.detector.ECSector;
import org.ec.util.ECMatchFinder;
import org.jlab.coda.clara.core.CServiceParameter;
import org.jlab.coda.clara.core.ICService;

/**
 * The <code>MatchFinderService</code> find matches of hits between layers.
 * <p>
 * <font size = 1>JSA: Thomas Jefferson National Accelerator Facility<br>
 * This software was developed under a United States Government license,<br>
 * described in the NOTICE file included as part of this distribution.<br>
 * Copyright (c), Feb 24, 2011</font>
 *
 * @author      smancill
 * @version     0.1
 */
public class MatchFinderService implements ICService
{

    /* (non-Javadoc)
     * @see org.jlab.coda.clara.core.ICService#configure(org.jlab.coda.clara.core.CServiceParameter)
     */
    @Override
    public void configure(CServiceParameter arg0)
    {
        // TODO Auto-generated method stub

    }


    /* (non-Javadoc)
     * @see org.jlab.coda.clara.core.ICService#executeService(int, java.lang.Object)
     */
    @Override
    public Object executeService(int arg0, Object arg1)
    {
        ECSector      sector = (ECSector) arg1;
        ECMatchFinder matches = new ECMatchFinder(sector);

        matches.projectAllHits();
        matches.matchInnerLayer();
        matches.matchOuterLayer();

        return sector;
    }


    /* (non-Javadoc)
     * @see org.jlab.coda.clara.core.ICService#executeService(int[], java.lang.Object[])
     */
    @Override
    public Object executeService(int[] arg0, Object[] arg1)
    {
        // TODO Auto-generated method stub
        return null;
    }


    /* (non-Javadoc)
     * @see org.jlab.coda.clara.core.ICService#getAuthor()
     */
    @Override
    public String getAuthor()
    {
        // TODO Auto-generated method stub
        return null;
    }


    /* (non-Javadoc)
     * @see org.jlab.coda.clara.core.ICService#getDescription()
     */
    @Override
    public String getDescription()
    {
        // TODO Auto-generated method stub
        return null;
    }


    /* (non-Javadoc)
     * @see org.jlab.coda.clara.core.ICService#getInputType()
     */
    @Override
    public int getInputType()
    {
        // TODO Auto-generated method stub
        return 0;
    }


    /* (non-Javadoc)
     * @see org.jlab.coda.clara.core.ICService#getInputTypes()
     */
    @Override
    public int[] getInputTypes()
    {
        // TODO Auto-generated method stub
        return null;
    }


    /* (non-Javadoc)
     * @see org.jlab.coda.clara.core.ICService#getName()
     */
    @Override
    public String getName()
    {
        // TODO Auto-generated method stub
        return null;
    }


    /* (non-Javadoc)
     * @see org.jlab.coda.clara.core.ICService#getOutputType()
     */
    @Override
    public int getOutputType()
    {
        // TODO Auto-generated method stub
        return 0;
    }


    /* (non-Javadoc)
     * @see org.jlab.coda.clara.core.ICService#getVersion()
     */
    @Override
    public String getVersion()
    {
        // TODO Auto-generated method stub
        return null;
    }

}
