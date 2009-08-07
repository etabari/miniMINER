import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import miniminer.MultipleSequenceAlignment;
import miniminer.Sequence;


public class FortranPorted {

	public static double[][] calculateDistances(
			MultipleSequenceAlignment SeqAlignment) {
		return calculateDistances(SeqAlignment, false, false, true);
	}

	public static double[][] calculateDistances(
			MultipleSequenceAlignment SeqAlignment, boolean TossGaps,
			boolean IsDna, boolean SmithAA) {

		MultipleSequenceAlignment SeqAl = (MultipleSequenceAlignment) SeqAlignment
				.clone();
		if (SeqAl.size() < 2)
			return null;

		String aacids;
		double[][] aamat = new double[21][21]; // 0:20,0:20
		int conlen = SeqAl.getSequenceLength();
		int sequence_number = SeqAl.size();
		double[][] distance = new double[sequence_number][sequence_number];

		// boolean dna;
		// int i;

		// int, parameter :: input_unit = 66
		// int ios
		// int it
		// int j
		// character ( len = 100 ) output_name
		// int, parameter :: output_unit = 67
		// character sequence(sequence_max,sequence_length_max)
		int[][] sequence_index = new int[sequence_number][conlen];
		// # read_sequence ( input_unit, sequence_max, sequence_length_max,
		// # sequence_number, sequence, sequence_name, sequence_length, conlen )

		if (TossGaps)
			toss_gaps(SeqAl);

		if (IsDna)
			aacids = "ACGTU";
		else
			aacids = "DEKRHNQSTILVFWYCMAGP";

		for (int i = 0; i < aamat.length; i++)
			for (int j = 0; j < aamat[i].length; j++) {
				if (i == j)
					aamat[i][j] = 0.0;
				else
					aamat[i][j] = 1.0;
			}

		aamat[0][0] = 1.0;
		aamat[4][5] = 0.0;
		aamat[5][4] = 0.0;

		if (!IsDna) {
			if (SmithAA)
				set_aamat(aamat, aacids);

		}

		printaamat(aamat);
		//
		// Replace the sequence character by the equivalent sequence index
		// number.
		//
		for (int i = 0; i < sequence_number; i++)
			for (int j = 0; j < conlen; j++)
				sequence_index[i][j] = aacids
						.indexOf(SeqAl.get(i).getBaseAt(j));

		dist_mat_set(SeqAl.size(), SeqAl.getSequenceLength(),
				sequence_index, aamat, distance);
		// !
		// ! Write the distance matrix to a file.
		// !
		return distance;
	}

	private static void printaamat(double[][] aamat) {
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new FileWriter("d:\\a.txt"));
			for (int i = 0; i < aamat.length; i++) {
				for (int j = 0; j < aamat.length; j++)
					out.write(String.format("%4.2f ", aamat[i][j]));
				out.newLine();
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	// /*****************************************************************************80
	// /
	// // SET_AAMAT sets up the amino acid distance matrix.
	//
	// Author:
	//
	// Des Higgins,
	// EMBL Data Library,
	// April 1992
	//
	// Modified:
	//
	// 06 April 2000
	//
	// Parameters:
	//
	// Output, double AAMAT(0:20,0:20), the matching score matrix.
	//
	// Input, character ( len = 20 ) AACIDS, the one letter codes for
	// the amino acids.
	//
	static void set_aamat(double[][] aamat, String aacids) {
		// implicit none

		// character ( len = 20 ) aacids
		// double aamat(0:20,0:20)

		double idmatch = 0.0;
		double match1 = 1.0;
		double match2 = 2.0;
		double nomatch = 3.0;

		//
		// Initialize the matrix to no matching.
		//
		// aamat(0:20,0:20) = nomatch

		for (int i = 0; i < aamat.length; i++)
			for (int j = 0; j < aamat[i].length; j++)
				aamat[i][j] = nomatch;

		//
		// Set groups that are somewhat close.
		//
		set_aascore("DEKRHNQST", match2, aamat, aacids);
		set_aascore("ILVFWYCM", match2, aamat, aacids);
		//
		// Set subgroups that are very close.
		//
		set_aascore("DE", match1, aamat, aacids);
		set_aascore("KRH", match1, aamat, aacids);
		set_aascore("NQ", match1, aamat, aacids);
		set_aascore("ST", match1, aamat, aacids);
		set_aascore("ILV", match1, aamat, aacids);
		set_aascore("FWY", match1, aamat, aacids);
		set_aascore("AG", match1, aamat, aacids);
		//
		// Set the score for the identity match.
		//
		for (int i = 0; i < 20; i++)
			aamat[i][i] = idmatch;

		return;
	}

	private static void set_aascore(String group, double match,
			double[][] aamat, String aacids) {
		int grlen = group.length();

		for (int i = 0; i < grlen; i++)
			for (int j = 0; j < grlen; j++) {
				int col = aacids.indexOf(group.charAt(i)) + 1;
				int row = aacids.indexOf(group.charAt(j)) + 1;
				aamat[col][row] = match;
				aamat[row][col] = match;

			}
	}

	private static void toss_gaps(MultipleSequenceAlignment seqAl) {
		for (int i = 0; i < seqAl.getSequenceLength(); i++)
			for (int j = 1; j < seqAl.size(); j++)
				if (seqAl.get(j).getBaseIndexAt(j) == Sequence.Gap) {
					for (int k = 0; k < seqAl.size(); k++)
						seqAl.get(k).setBaseIndexAt(i, Sequence.Gap);
					break;
				}
	}

	private static void dist_mat_set(int seqCount, int seqLength,
			int[][] sequence_index, double[][] aamat, double[][] distance) {
		for (int i = 0; i < seqCount; i++) {
			for (int j = i + 1; j < seqCount; j++) {

				double dist = 0.0E+00;

				for (int k = 0; k < seqLength; k++) {

					int ik = sequence_index[i][k];
					int jk = sequence_index[j][k];

					if (ik != -1 && jk != -1)
						dist += aamat[ik + 1][jk + 1];

				}

				dist = Math.sqrt(dist);
				distance[i][j] = dist;
				distance[j][i] = dist;

			}
		}

		//
		// The diagonal of the matrix is zero.
		//
		for (int i = 0; i < seqCount; i++)
			distance[i][i] = 0.0;

	}

}
