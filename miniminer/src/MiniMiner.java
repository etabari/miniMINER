import java.util.HashSet;

import miniminer.MultipleSequenceAlignment;
import miniminer.files.OutputFile;
import miniminer.tree.NJTree;

public class MiniMiner {

	private MultipleSequenceAlignment msa;
	private NJTree tree;

	private boolean tossGaps;
	private boolean kimura;

	private MultipleSequenceAlignment[] windowMSA;
	private NJTree[] windowTree;
	private int[] score;
	private double[] zScore;

	public MiniMiner(MultipleSequenceAlignment msa, boolean tossGaps, boolean kimura) {
		this.msa = msa;
		this.tossGaps = tossGaps;
		this.kimura = kimura;
		this.windowMSA = null;
	}

	public NJTree getWholeTree() {
		return tree;
	}

	public int getTreeCount() {
		return windowTree.length;
	}

	public MultipleSequenceAlignment getMsas(int idx) {
		if (windowMSA != null)
			return windowMSA[idx];
		return null;
	}

	public NJTree getTrees(int idx) {
		return windowTree[idx];
	}

	public int[] getScores() {
		return score;
	}

	public double[] getZScores() {
		return zScore;
	}

	public void createAll(int WindowSize, boolean saveMSA) {

		// create the total NJ Tree
		tree = new NJTree(msa);
		tree.createTree(tossGaps, kimura);

		// creating the sub-msa and their trees
		int seqLength = msa.getSequenceLength();
		int windowCount = seqLength - WindowSize + 1;

		windowTree = new NJTree[windowCount];
		if (saveMSA)
			windowMSA = new MultipleSequenceAlignment[windowCount];

		for (int i = 0; i < windowCount; i++) {
			MultipleSequenceAlignment msaw = msa.getWindow(i, WindowSize);

			NJTree njt = new NJTree(msaw);
			if (njt.createTree(tossGaps, kimura)) {
				windowTree[i] = njt;
				if (saveMSA)
					windowMSA[i] = msaw;
			}
		}
	}

	public void createScores() {
		HashSet<String> leavesAll = tree.createPmSum();
		score = new int[windowTree.length];
		int wholeLeafCount = leavesAll.size();
		 
		//LeavesFile l = new LeavesFile("d:\\a\\e\\out.txt");
		int scoreSum = 0;
		for (int i = 0; i < windowTree.length; i++) {

			HashSet<String> leaves = windowTree[i].createPmSum();
			
			int leafCount = leaves.size();

			int similar = 0;
			for (String s : leaves)
				if (leavesAll.contains(s))
					similar++;
			
			score[i] = wholeLeafCount - similar + leafCount - similar;
			
			if (i==0)
			System.out.println(leaves);
			System.out.printf("score[%d]: %d (all=%d, this=%d, sim=%d)\n",i+1, score[i], wholeLeafCount, leafCount, similar);
			
			scoreSum += score[i];
		}
		//l.close();
		calculateZScores(scoreSum);
	}

	private void calculateZScores(int scoreSum) {

		int size = windowTree.length;

		zScore = new double[size];

		double avg = 1.0 * scoreSum / size;
		double sum_diff = 0.0;

		for (int i = 0; i < size; i++)
			sum_diff += (score[i] - avg) * (score[i] - avg);

		double stdev = Math.sqrt(sum_diff / (size - 1));

		for (int i = 0; i < zScore.length; i++)
			zScore[i] = (1.0 * score[i] - avg) / stdev;

	}

}