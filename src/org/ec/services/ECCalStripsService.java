package org.ec.services;

import org.jlab.coda.clara.core.CServiceParameter;
import org.jlab.coda.clara.core.ICService;

import org.ec.bos.ECEvu;
import org.ec.detector.ECLayer;
import org.ec.detector.ECSector;
import org.ec.detector.ECStrip;
import org.ec.detector.ECView;
import org.ec.detector.base.ECLayerName;

public class ECCalStripsService implements ICService{
	
	@Override
	public void configure(CServiceParameter arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object executeService(int arg0, Object arg1) {
		ECSector Ec = (ECSector) arg1;

		double[] energySum = new double[2];
		ECLayer[] layers = new ECLayer[2];
		layers[0] = Ec.getLayer(ECLayerName.INNER);
		layers[1] = Ec.getLayer(ECLayerName.OUTER);

		double dADC;
		double dTDC;
		double TDCSraw;
		double ADCSraw;
		double energy;
		double time = 0;
		int idEvu;

		for (int i = 0; i < 2; ++i)
			for (ECView view : layers[i].getViewList())
				for (ECEvu Evu : view.getEvuList()) {
					idEvu = Evu.getID();
					dADC  = Evu.getADC();
					dTDC  = Evu.getTDC();
					TDCSraw = dTDC; /* Saves ADC minus pedestal */
					ADCSraw = dADC - view.calEo[idEvu];
					energy  = TDCSraw * view.calEch[idEvu];
					if (energy < 0)
						energy = 0;

					ECStrip strip = new ECStrip(Evu.getID(), energy);
					
					if (dTDC > 0 && dTDC < 9000 && ADCSraw > 0)
						/*
						 * Then !filter out bad tdcs upper limit for dTDC was
						 * changed from 4096 to 9000 because of the new multihit
						 * pipeline TDC --- H.S. Jo November 2005
						 */
						time = dTDC * view.calTch[idEvu] + view.calTo[idEvu]
								+ view.calTadc[idEvu] / Math.sqrt(ADCSraw);
					else
						time = -999;

					strip.setTime(time);
					strip.setRawAdcs(ADCSraw);
					energySum[i] += energy;
					Ec.getLayer(layers[i].getName()).getView(view.getLabel())
					.addStrip(strip);
				}

		/*for (ECViewLabel lView : ECViewLabel.values())
			if (layers[0].getView(lView).getNStrips()
					+ layers[1].getView(lView).getNStrips() > 0) {

			}*/
		return Ec;
	}

	@Override
	public Object executeService(int[] arg0, Object[] arg1) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getAuthor() {
		// TODO Auto-generated method stub
		return "Ricardo Oyarzun";
	}
	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public int getInputType() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int[] getInputTypes() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "ECCalStripService";
	}
	@Override
	public int getOutputType() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public String getVersion() {
		// TODO Auto-generated method stub
		return "1.0";
	}
}
