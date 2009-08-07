package miniminer.files;

import miniminer.tree.BaseDistanceMatrix;
import miniminer.tree.NJTree;

public class NJFile extends OutputFile {

	NJTree tree;

	public NJFile(Object file, NJTree tree) {
		super(file);
		this.tree = tree;
	}


	private void writeDMToFile() {
		BaseDistanceMatrix mat = tree.getDistanceMatrix();
		newLine();
		write("\n DIST   = percentage divergence (/100)");
		write("\n Length = number of sites used in comparison");
		newLine(2);

		if (mat.excludeGaps()) {
			write("\n All sites with gaps (in any sequence) deleted");
			newLine();
		}

		write(mat.getMethodDescription());

		for (int m = 0; m < mat.getSize(); m++)
			for (int n = m + 1; n < mat.getSize(); n++) {
				write(String.format("%4d vs.%4d  DIST = %5.4f;  length = %6.0f\n", m + 1, n + 1,
									mat.getDistance(m, n), mat.getLength(m, n)));
			}
	}

	@Override
	protected void writeContent() {
		if (tree != null) {
			writeDMToFile();
			write(tree.getTreeString());
		}
	}

}
