package miniminer.files;

import miniminer.MultipleSequenceAlignment;
import miniminer.Sequence;

public class MSAFile extends OutputFile {

	MultipleSequenceAlignment msa;

	public MSAFile(Object file, MultipleSequenceAlignment msa) {
		super(file);
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
