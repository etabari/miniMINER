package miniminer.io;

import java.io.BufferedWriter;

import miniminer.MultipleSequenceAlignment;
import miniminer.Sequence;

public class MSAFile extends OutputFile {

	MultipleSequenceAlignment msa;

	public MSAFile(String filename, MultipleSequenceAlignment msa) {
		super(filename);
		this.msa = msa;
	}

	public MSAFile(BufferedWriter writer, MultipleSequenceAlignment msa) {
		super(writer);
		this.msa = msa;
	}

	@Override
	protected void writeContent() {
		if (msa != null) {
			for (Sequence s : msa) {
				write(">" + s.getName());
				newLine();
				write(s.getDataString());
				newLine();
			}

		}
	}

}
