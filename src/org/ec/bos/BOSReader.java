package org.ec.bos;

import java.util.*;

import org.ec.detector.ECCore;
import org.ec.detector.ECSector;
import org.ec.detector.base.ECLayerName;
import org.ec.detector.base.ECViewLabel;


public class BOSReader extends ArrayList<ECSector> {

	private static final long serialVersionUID = 1L;
	private ECCore Ec;

	public int IW[];
	public int index;
	public int indexECT;
	public int indexEC2;

	private static int nami = 0;
	private static int namiECT = 0;
	private static int namiEC2 = 0;

	private static int Repl = 0;
	private static int SectorECT = 0;
	private static int SectorFlag = 0;

	private static boolean resetCount;
	private double tVL[];
	private double aVL[];
	private int idVL[];
	static int[] nVL = new int[6]; // #View-Layer

	public int i16[]; /*		*/
	public int i32[]; /* 		*/
	public int j16[]; /* EC */
	public int j32[]; /* ECT */
	
	public BOSReader(ECCore Ec) {
		this.Ec = Ec;
		// Bank is read and saved information into in array of Sector Objects
		ReadBank();

	}

	private void ReadBank() {
		boolean preShower = false;
		int nSector, nData;
		ECSector fSector[] = new ECSector[Ec.MaxSectors];

		for (int i = 0; i < Ec.MaxSectors; ++i)
			fSector[i] = new ECSector(i);

		if (nami == 0)
			index = nami + 1;
		if (namiECT == 0)
			namiECT++; // Just for now
		if (namiEC2 == 0)
			namiEC2++;

		IW = new int[100]; // Just for now the getting data method is missing

		while (true) {
			index = IW[index - 1];
			if (index == 0) {
				if (indexEC2 > 0 && !preShower) {
					index = indexEC2;
					preShower = true;
					continue;
				} else
					break;
			}
			nSector = IW[index - 2];
			/* for now references to the Core Object, not definitive */
			if (nSector < 1 || nSector > Ec.MaxSectors)
				continue;
			nData = IW[index];

			if (nData == 0)

				for (int i = 0; i < nData; i++)
					i32[i + 1] = IW[index + i + 1];// Keeping Fortran style of
													// arrays (+1)
			int itest = nData / 3 * 3;
			int nDataECT;
			int nData2ECT = 0;
			@SuppressWarnings("unused")
			int nData2 = 0;

			if (itest < nData)
				nData2 = 2 * nData - 1;
			if (itest == nData)
				nData2 = 2 * nData;

			if (!preShower) {
				SectorFlag = 0;
				nData2ECT = 0;
				nDataECT = namiECT + 1;
				while (true) {
					indexECT = IW[indexECT - 1];
					if (indexECT > 0) {
						SectorECT = IW[indexECT - 2];
						if (SectorECT == nSector)
							SectorFlag = 1;
						if (SectorECT != nSector && SectorECT != 0
								&& SectorECT != Ec.MaxSectors)
							continue;
						if (SectorFlag == 1) {
							nDataECT = IW[indexECT];
							if (nData == 0)
								continue;
							for (int j = 0; j < nDataECT; j++)
								j32[j + 1] = IW[indexECT + j + 1]; // Keeping
																	// Fortran
																	// convention
							nData2ECT = 2 * nDataECT;
						}
					}
				}
			}

			resetCount = true;
			for (int i = 1; i <= nData; i += 3) {
				int ichk = i16[i];
				int id = ichk % 256; // Number of Strip
				int il = ichk / 256; // View-Layer Combination

				if (resetCount) {
					/* View-layer 0=UO, 1=UI, 2=VO, 3=VI.... */
					resetCount = false;
					for (int j = 0; j < 6; j++)
						nVL[i] = 0;
				} else {
					nVL[il]++;
					// Later this will need to be ordered!!!
					idVL[nVL[il]] = id;
					tVL[nVL[il]] = (double) i16[i + 1];
					aVL[nVL[il]] = (double) i16[i + 2];

					if (SectorFlag == 1) { // missing TDCStatus==1 condition
						for (int j = 1; j <= nData2ECT; j += 2) {
							int ichkECT = j16[j];
							int idECT = ichkECT % 256;
							@SuppressWarnings("unused")
							int ilECT = ichkECT / 256;
							if (j == il && idECT == id && Repl == 0) {
								if ((double) j16[j + 1] != 0)
									tVL[nVL[il]] = 5700 - (double) j16[j + 1];
								Repl = 1;
							}
						}
					}
					if (!preShower) {
						for (int k = 0; k < 7; ++k)
							for (int l = 0; l < nVL[k]; ++l) {
								switch (k) {
								case 0: /* View:U, Layer:INNER */
									fSector[nSector]
											.getLayer(ECLayerName.INNER)
											.getView(ECViewLabel.U)
											.addEvu(new ECEvu(idVL[l], tVL[l],
													aVL[l]));
									break;
								case 1: /* View:U, Layer:OUTER */
									fSector[nSector]
											.getLayer(ECLayerName.OUTER)
											.getView(ECViewLabel.U)
											.addEvu(new ECEvu(idVL[l], tVL[l],
													aVL[l]));
									break;
								case 2: /* View:V, Layer:INNER */
									fSector[nSector]
											.getLayer(ECLayerName.INNER)
											.getView(ECViewLabel.V)
											.addEvu(new ECEvu(idVL[l], tVL[l],
													aVL[l]));
									break;
								case 3: /* View:V, Layer:OUTER */
									fSector[nSector]
											.getLayer(ECLayerName.OUTER)
											.getView(ECViewLabel.V)
											.addEvu(new ECEvu(idVL[l], tVL[l],
													aVL[l]));
									break;
								case 4: /* View:W, Layer:INNER */
									fSector[nSector]
											.getLayer(ECLayerName.INNER)
											.getView(ECViewLabel.W)
											.addEvu(new ECEvu(idVL[l], tVL[l],
													aVL[l]));
									break;

								case 5: /* View:W, Layer:OUTER */
									fSector[nSector]
											.getLayer(ECLayerName.OUTER)
											.getView(ECViewLabel.W)
											.addEvu(new ECEvu(idVL[l], tVL[l],
													aVL[l]));
									break;
								default:
									break;
								}
							}
					} else
						for (int k = 0; k < 7; ++k)
							for (int l = 0; l < nVL[k]; ++l) {
								switch (k) {
								/* View:U, Layer:INNER */
								case 1:
									fSector[nSector]
											.getLayer(ECLayerName.INNER)
											.getView(ECViewLabel.U)
											.addEvu(new ECEvu(idVL[l], tVL[l],
													aVL[l]));
									break;
								/* View:V, Layer:INNER */
								case 3:
									fSector[nSector]
											.getLayer(ECLayerName.INNER)
											.getView(ECViewLabel.V)
											.addEvu(new ECEvu(idVL[l], tVL[l],
													aVL[l]));
									break;
								/* View:W, Layer:INNER */
								case 5:
									fSector[nSector]
											.getLayer(ECLayerName.INNER)
											.getView(ECViewLabel.W)
											.addEvu(new ECEvu(idVL[l], tVL[l],
													aVL[l]));
									break;
								default:
									break;
								}
							}
				}

			}

		}
		// Set the BOSReader class extended array attributes
		for (int i = 0; i < Ec.MaxSectors; ++i) {
			this.add(fSector[i]);
		}
	}

}
