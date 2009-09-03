package miniminer.tree;

import miniminer.AminoAcid;
import miniminer.MultipleSequenceAlignment;
import miniminer.Sequence;

public class BaseDistanceMatrix {

	private double[][] distances;
	private double[][] lengths;
	private boolean tossAllGaps;
	protected boolean[] treeGaps = null;
	protected String methodDescription;

	public BaseDistanceMatrix(BaseDistanceMatrix mat) {
		tossAllGaps = mat.tossAllGaps;
		methodDescription = mat.methodDescription;

		distances = new double[mat.distances.length][mat.distances.length];
		lengths = new double[mat.distances.length][mat.distances.length];

		for (int i = 0; i < distances.length; i++)
			for (int j = 0; j < distances.length; j++) {
				distances[i][j] = mat.distances[i][j];
				lengths[i][j] = mat.lengths[i][j];
			}

		treeGaps = new boolean[mat.treeGaps.length];

		for (int i = 0; i < treeGaps.length; i++)
			treeGaps[i] = mat.treeGaps[i];

	}

	public BaseDistanceMatrix(MultipleSequenceAlignment alignments, boolean tossAllGaps) {

		distances = new double[alignments.size()][alignments.size()];
		lengths = new double[alignments.size()][alignments.size()];
		for (int i = 0; i < distances.length; i++)
			distances[i][i] = 0.0;

		this.tossAllGaps = tossAllGaps;
		treeGaps = new boolean[alignments.getSequenceLength()];
		for (int i = 0; i < treeGaps.length; i++)
			treeGaps[i] = false;

		if (tossAllGaps)
			findGaps(alignments);

		methodDescription = "";
	}

	private void findGaps(MultipleSequenceAlignment al) {
		for (int pos = 0; pos < treeGaps.length; pos++) 
			for (int seq = 0; seq < al.size(); seq++)
				if (al.get(seq).getBaseAt(pos) == AminoAcid.Gap) {
					treeGaps[pos] = true;
					break;
				}
	}

	public double getDistance(int i, int j) {
		return distances[i][j];
	}

	public double getDist1(int i, int j) {
		return getDistance(i - 1, j - 1);
	}

	protected void setDistance(int i, int j, double d) {
		if (i != j) {
			distances[i][j] = d;
			distances[j][i] = d;
		}
	}

	public void setDist1(int i, int j, double d) {
		setDistance(i - 1, j - 1, d);
	}

	public double getLength(int i, int j) {
		return lengths[i][j];
	}

	protected void setLength(int i, int j, double d) {
		if (i != j) {
			lengths[i][j] = d;
			lengths[j][i] = d;
		}
	}
	
	public boolean isAllGapTossed() {
		boolean gaps = true;
		for (int i = 0; i < treeGaps.length; i++)
			gaps &= treeGaps[i];
		return gaps;
	}

	public int getSize() {
		return distances.length;
	}

	public boolean excludeGaps() {
		return tossAllGaps;
	}

	public String getMethodDescription() {
		return methodDescription;
	}

}
