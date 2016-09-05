package miniminer.io;

import java.io.BufferedWriter;

public class ScoreFile extends OutputFile {

	private int[] scores_i;
	private double[] scores_d;

	public ScoreFile(String filename, int[] scores) {
		super(filename);
		setData(scores, null);
	}


	public ScoreFile(BufferedWriter writer, int[] scores) {
		super(writer);
		setData(scores, null);
	}

	public ScoreFile(String filename, double[] scores) {
		super(filename);
		setData(null, scores);
	}
	

	public ScoreFile(BufferedWriter writer, double[] scores) {
		super(writer);
		setData(null, scores);
	}
	
	private void setData(int[] iScores, double[]dScores) {
		this.scores_i = iScores;
		this.scores_d = dScores;
	}

	@Override
	protected void writeContent() {
		if (scores_i != null)
			for (int i = 0; i < scores_i.length; i++) {
				write(String.format("%d\t%d\n", i + 1, scores_i[i]));
			}
		else if (scores_d != null)
			for (int i = 0; i < scores_d.length; i++)
				write(String.format("%d\t%3.2f\n", i + 1, scores_d[i]));
		
	}

}
