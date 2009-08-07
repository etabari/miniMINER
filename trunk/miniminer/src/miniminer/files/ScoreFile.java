package miniminer.files;

public class ScoreFile extends OutputFile {

	private int[] scores_i;
	private double[] scores_d;

	public ScoreFile(Object file, int[] scores) {
		super(file);
		this.scores_i = scores;
		this.scores_d = null;
	}

	public ScoreFile(Object file, double[] scores) {
		super(file);
		this.scores_d = scores;
		this.scores_i = null;
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
