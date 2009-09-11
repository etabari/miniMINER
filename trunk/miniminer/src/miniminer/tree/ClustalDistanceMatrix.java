package miniminer.tree;

import miniminer.AminoAcid;
import miniminer.MultipleSequenceAlignment;

public class ClustalDistanceMatrix extends BaseDistanceMatrix {

	int overspill;
	
	private final static String KIMURA_DESC =  "\n Distances up to 0.75 corrected by Kimura's empirical method:"
		+ "\n\n Kimura, M. (1983) The Neutral Theory of Molecular Evolution."
		+ "\n Page 75. Cambridge University Press, Cambridge, England.\n\n";


	public static final int[] dayhoff_pams = { 195, /*
													 * 75.0% observed d; 195
													 * PAMs estimated = 195%
													 * estimated d
													 */
	196, /* 75.1% observed d; 196 PAMs estimated */
	197, 198, 199, 200, 200, 201, 202, 203, 204, 205, 206, 207, 208, 209, 209,
			210, 211, 212, 213, 214, 215, 216, 217, 218, 219, 220, 221, 222,
			223, 224, 226, 227, 228, 229, 230, 231, 232, 233, 234, 236, 237,
			238, 239, 240, 241, 243, 244, 245, 246, 248, 249, 250, /*
																	 * 250 PAMs
																	 * = 80.3%
																	 * observed
																	 * d
																	 */
			252, 253, 254, 255, 257, 258, 260, 261, 262, 264, 265, 267, 268,
			270, 271, 273, 274, 276, 277, 279, 281, 282, 284, 285, 287, 289,
			291, 292, 294, 296, 298, 299, 301, 303, 305, 307, 309, 311, 313,
			315, 317, 319, 321, 323, 325, 328, 330, 332, 335, 337, 339, 342,
			344, 347, 349, 352, 354, 357, 360, 362, 365, 368, 371, 374, 377,
			380, 383, 386, 389, 393, 396, 399, 403, 407, 410, 414, 418, 422,
			426, 430, 434, 438, 442, 447, 451, 456, 461, 466, 471, 476, 482,
			487, 493, 498, 504, 511, 517, 524, 531, 538, 545, 553, 560, 569,
			577, 586, 595, 605, 615, 626, 637, 649, 661, 675, 688, 703, 719,
			736, 754, 775, 796, 819, 845, 874, 907, 945,
			/* 92.9% observed; 945 PAMs */
			988 /* 93.0% observed; 988 PAMs */
	};


	
	public ClustalDistanceMatrix(MultipleSequenceAlignment al,
			boolean ExcludeGaps, boolean Kimura) {
		super(al, ExcludeGaps);

		if (Kimura)
			methodDescription = KIMURA_DESC;		

		overspill = calculateDM(al, Kimura);
	}
	
	public ClustalDistanceMatrix(ClustalDistanceMatrix mat) {
		super(mat);
		overspill = mat.overspill;
	}

	private int calculateDM(MultipleSequenceAlignment al, boolean Kimura) {

		double p, e, k;
		int overspill = 0;

		for (int m = 0; m < al.size(); m++) {

			for (int n = m + 1; n < al.size(); n++) {
				p = e = 0.0;
				setDistance(m, n, 0.0);
				for (int i = 0; i < al.getSequenceLength(); i++) {
					if (treeGaps[i])
						continue;

					AminoAcid res1 = al.get(m).getBaseAt(i);
					AminoAcid res2 = al.get(n).getBaseAt(i);

					if (res1 == AminoAcid.Gap || res2 == AminoAcid.Gap)
						continue;

					e += 1.0;
					if (res1 != res2)
						p += 1.0;

				}

				if (p <= 0.0)
					k = 0.0;
				else
					k = p / e;

				if (Kimura) {
					if (k < 0.75) {
						if (k > 0.0)
							k = -Math.log(1.0 - k - k * k / 5.0);
					} else {
						if (k > 0.93) {
							overspill++;
							k = 10.0; // arbitrary set to 1000%
						} else {
							double tableEntry = (k * 1000.0) - 750.0;
							k = (double) dayhoff_pams[(int) tableEntry];
							k /= 100.0;
						}
					}
				}
				setDistance(m, n, k);
				setLength(m, n, e);
			}
		}
		return overspill;

	}

	public int getOverSpill() {
		return overspill;
	}
}
