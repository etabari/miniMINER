package miniminer.tree;


public class PhyloTree {
	int [][] treeDesc;
	double[] leftBranch;
	double[] rightBranch;
	
	public PhyloTree(int numSeq){
		treeDesc = new int[numSeq+1][numSeq+1];
		leftBranch = new double[numSeq + 2];
		rightBranch = new double[numSeq + 2];
	}
}
