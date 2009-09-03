package miniminer.tree;

import miniminer.AminoAcid;
import miniminer.MultipleSequenceAlignment;

public class DistanceMatrix extends BaseDistanceMatrix {

	enum Function {
		NONE, SQRT
	};

	Function func;
	


	public DistanceMatrix(MultipleSequenceAlignment al, AminoAcidInfo aaInfo,
			Function f) {
		super(al, false);
		func = f;
		calculateDM(al, aaInfo);
	}

	private void calculateDM(MultipleSequenceAlignment al, AminoAcidInfo aaInfo) {

		for (int i = 0; i < getSize(); i++)
			for (int j = i + 1; j < getSize(); j++) {
				double sum = 0.0;
				AminoAcid[] iAAIdx = al.get(i).getData();
				AminoAcid[] jAAIdx = al.get(j).getData();

				for (int c = 0; c < iAAIdx.length; c++)
					sum += aaInfo.getRelation(iAAIdx[c], jAAIdx[c]);

				sum = applyFunction(sum);
				setDistance(i, j, sum);

			}
	}

	private double applyFunction(double sum) {
		switch (func) {
		case SQRT:
			return Math.sqrt(sum);
		default:
			return sum;
		}
	}

}
