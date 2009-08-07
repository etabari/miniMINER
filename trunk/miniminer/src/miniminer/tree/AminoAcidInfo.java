package miniminer.tree;

import java.io.BufferedReader;
import java.io.IOException;

import miniminer.Sequence;
import miniminer.utility.DataFileReader;

public class AminoAcidInfo {

	// public static final int PROTEIN = 0;
	// public static final int DNA = 1;

	public enum Type {
		PROTEIN, DNA
	};

	protected final String Bases;
	public double[][] relations;

	public AminoAcidInfo() {
		this(Type.PROTEIN);
	}

	public AminoAcidInfo(Type type) {
		if (type == Type.PROTEIN)
			Bases = "-DEKRHNQSTILVFWYCMAGP";
		else
			Bases = "-ACGTU";
		relations = new double[Sequence.AminoAcidCodes.length()][Sequence.AminoAcidCodes.length()];
	}

//	public int aaIndex(char aminoAcid) {
//		return Bases.indexOf(Character.toUpperCase(aminoAcid));
//	}

//	public int[] aaIndex(Sequence seq) {
//		int[] r = new int[seq.getData().length()];
//		for (int i = 0; i < seq.getData().length(); i++)
//			r[i] = aaIndex(seq.getData().charAt(i));
//		return r;
//	}

	public boolean loadAminoAcidRelations(String fileName) {
		BufferedReader reader = null;
		try {
			reader = DataFileReader.readAFile(fileName);
			String line = "";
			int row = 0;
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (line.length() > 0 && line.charAt(0) != ';') {
					String[] cols = line.split("\\s+");

					if (cols.length != Bases.length() + 1)
						return false;

					for (int i = 0; i < cols.length; i++)
						try {
							int _row = Sequence.AminoAcidCodes.indexOf(Bases.charAt(row));
							int _i = Sequence.AminoAcidCodes.indexOf(Bases.charAt(i));
							relations[_row][_i] = Double.parseDouble(cols[i]);
						} catch (NumberFormatException e) {
							return false;
						}
					row++;
				}
			}
			if (row != Bases.length() + 1)
				return false;
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			if (reader != null)
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}

		return true;
	}
	
	public double getRelation(int AA1, int AA2) {
		return relations[AA1][AA2];
	}

	public double getRelation(char AA1, char AA2) {
		int AA1idx = Sequence.AminoAcidCodes.indexOf(AA1);
		int AA2idx = Sequence.AminoAcidCodes.indexOf(AA2);
		return relations[AA1idx][AA2idx];
	}

	
}
