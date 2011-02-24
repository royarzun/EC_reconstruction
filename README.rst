EC Package
==========

This is the development version of the EC hits detection program using a
service oriented approach with the ClaRA framework.

:Authors: S. Mancilla,
          R. Oyarzún

:Version: 0.1

Design
------

The original FORTRAN algorithm has the following structure ::

    get information from BOS file
    for each sector do
        fill strips information
        for each layer do
            for each view do
                fit strips to peaks
            fit peaks to hits
        match hits between layers
    fill output BOS file

To move the old FORTRAN programming style to a modern object oriented design,
several classes have been created to represent the data structures:

=========  ===================================================================
ECSector   Represent a sector in the detector. It is the base class and it
           stores all the data used by the algorithm. Each sector have a list
           of four layers: *WHOLE*, *INNER*, *OUTER* and *COVER*. 
ECLayer    Represent one layer in the detector. It is the principal class, as
           it stores the list of found hits in that layer.  Each layer have a
           list with all the three views of the detector: *U*, *V* and *W*.
ECView     Represent a view in the detector.  For each view exits a list of
           information about its strips, and a list with the found peaks in
           that view.
ECStrip    The basic class, represent a strip and store the information
           obtained from BOS file.
ECFitPeak  Represent a peak, saving the necessary information for it.
ECFitHit   Represent a hit, saving the necessary information for it.
=========  ===================================================================

So, in a nutshell, each sector has four layers, each layer has three views,
and each view has a list of strips.  The algorithm finds peaks in each view
object, and then finds hits in the parent layer using the found peaks in its
three views.  This composition of objects allows us to represent the detector
in a more natural way.

See the Javadoc documentation in the ``doc`` directory for more details.

Services
--------

We decided to separate the algorithm in a chain of three services, using a
sector object as the data that should be passed between them::

    get information from BOS file
    for each sector do
        service1 -> service2 -> service3
    fill BOS file

The orchestrator will read the data for one event from the BOS file, and then
for each of the six sectors it will start the chain of services, each one
doing the following:

* The first service will fill the information of strips using the TDC and ADC
  data from BOS file, and the calibration data of the detector.

* The second service will be the main one. It will iterate over the layers of
  the sector, to get the peaks from the strips, and then the hits from the
  peaks.
  
* The third service will match the hits between the layers in the sector.

Finally, when all the six sectors have been evaluated, the orchestrator will
fill the BOS file with the calculated values.

Install
-------

The current version is still in development. To compile it, you need to setup
the ``CLARA_HOME`` environment variable, as you can see in the ClaRA
documentation.  Then just type in ``ant`` in your command line. It should
compile without problem, but there is nothing to be tested or executed.

Current status
--------------

* The data structures have been defined.  They will probably need small
  changes in the future.  That classes just provide access to the data.  All
  the manipulation is left to the algorithm.

* But there is no inheritance, interfaces or abstract classes defined yet.
  The idea is to extract the general information and move it to base classes.
  We need help to define that, because we don't have an idea of the other
  kinds of applications or analysis that could use our data structures.

* As said above, the services have been established.  But probably the first
  one is too simple, and the second one could be split in other services.  We
  need to run tests and evaluate the performance.

* Just the second service can be found in the ``services`` package.  We are
  writing the other ones, but that is a relatively easy task, so they should
  be finished in a couple of days.  The second service has also some sections
  not implemented yet.

* We need to get the calibration data yet.  There is no implementation for it.

* We are defining some XML structure to store the data from the BOS file, so
  an external application read the BOS, create the XML file and then our
  java application uses that XML as its input data.
