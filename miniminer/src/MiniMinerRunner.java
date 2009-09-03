import java.io.File;
import java.util.zip.DataFormatException;

import miniminer.MultipleSequenceAlignment;
import miniminer.io.MSAFile;
import miniminer.io.NJFile;
import miniminer.io.ScoreFile;
import miniminer.tree.NJTree;
import miniminer.utility.Converter;
import miniminer.view.MiniMinerFrame;

public class MiniMinerRunner {
	public static void main(String[] args) {

		if ((args.length == 0)
			|| (args.length < 2 && !args[0].equalsIgnoreCase("-x"))
			|| !(args[0].equalsIgnoreCase("-x") || args[0].equalsIgnoreCase("-n")
				|| args[0].equalsIgnoreCase("-a") || args[0].equalsIgnoreCase("-m"))) {

			System.err.println("Syntax:");
			System.err.println("  MiniMiner (-x|-n|-a|-m) [-tXX] [-k] [-wXX] [-s] inputAlignmentfilename\n");
			System.err.println("mode (-x|-n|-a|-m):");
			System.err.println("   -x: run in Window mode");
			System.err.println("   -n: runs and create the total NJ Tree for the alignment");
			System.err.println("   -a: runs and create all NJ Trees for the alignment, windows size should be determined\n");
			System.err.println("   -m: runs and create score file.\n");

			System.err.println("\noptions:");
			System.err.println("   -tXX: include -t with a number to toss Gaps (exclude postions with gaps in all sequences)");
			System.err.println("         -t with an integer (xx) will toss gaps in all sequences if there is a Gap in xx percent of sequences");

			System.err.println("         It is considered 50 if not specified.");

			System.err.println("   -k: include -k to apply Kimura method");
			System.err.println("   -b: include -k to get exact MINER results (used for -m mode)");
			System.err.println("   -j: include -j to apply Clustal Exclude Gaps methods (removes all gaps if a sequence has a gap)");

			System.err.println("   -wXX: include -w with a number to determine the window size");
			System.err.println("         (used for -a mode), it is considered 5 if not specified.");
			System.err.println("   -s: include -s to create windowed alingment files");
			System.err.println("         (used for -a mode)");
			return;
		}

		if (args[0].equalsIgnoreCase("-x")) {
			System.out.print("running windows !");
			MiniMinerFrame frame = new MiniMinerFrame();
			frame.setVisible(true);
			return;
		}

		if (!args[0].equalsIgnoreCase("-n") && !args[0].equalsIgnoreCase("-a")
			&& !args[0].equalsIgnoreCase("-m"))
			return;

		int tossGaps = 100;
		boolean kimura = false;
		boolean clustalTossGaps = false;
		boolean minerBug = false;

		for (String s : args) {
			if (s.startsWith("-t"))
				tossGaps = Converter.toBound(Converter.toInt(s.substring(2), 50), 0, 100);
			if (s.equalsIgnoreCase("-k"))
				kimura = true;
			if (s.equalsIgnoreCase("-j"))
				clustalTossGaps = true;
			if (s.equalsIgnoreCase("-b"))
				minerBug = true;
		}

		String in_filename = args[args.length - 1];
		String out_filename = args[args.length - 1] + ".nj";

		if (!(new File(in_filename)).exists()) {
			System.err.printf("Input file '%s' does not exists.\n", in_filename);
			return;
		}

		MultipleSequenceAlignment msa = new MultipleSequenceAlignment();
		try {
			if (!msa.loadSeqs(in_filename)) {
				System.err.printf("  No file selected or file access failed (%s).\n", in_filename);
				return;
			}
			System.out.println("MSA file loaded.");
			System.out.printf("%d sequences are loaded from file (sequence length: %d).\n", msa.size(), msa.getSequenceLength());
			if (tossGaps != 100) {
				msa = msa.getMasked(tossGaps);
				System.out.printf("%d%% masking applied (sequence length: %d).\n", tossGaps, msa.getSequenceLength());
				System.out.println("Saving masked msa file");
				MSAFile f = new MSAFile(in_filename+".masked", msa);
				f.createFile();
			}
		} catch (DataFormatException ex) {
			System.err.printf("The file '%s' might be a bad file format: ", in_filename);
			System.err.println(ex.getMessage());
		}

		if (args[0].equalsIgnoreCase("-n")) {
			System.out.println("Creating NJ Tree...");
			NJTree njTree = new NJTree(msa);
			njTree.createTree(clustalTossGaps, kimura);
			System.out.println("Creating file: " + out_filename);
			NJFile njFile = new NJFile(out_filename, njTree);
			njFile.createFile();

			System.out.println("Done.");
			return;
		}

		if (args[0].equalsIgnoreCase("-a")) {
			int windowSize = 5;
			boolean storeAndCreateSubAlignments = false;
			for (String s : args) {
				if (s.startsWith("-w"))
					windowSize = Converter.toInt(s.substring(2), 5);
				if (s.equalsIgnoreCase("-s"))
					storeAndCreateSubAlignments = true;

			}
			if (windowSize < 1)
				windowSize = 1;
			
			if (windowSize>msa.getSequenceLength())
				windowSize = msa.getSequenceLength();
				
			System.out.printf("Creating All NJ Trees (Window size: %d)...\n", windowSize);
			MiniMiner mm = new MiniMiner(msa, clustalTossGaps, kimura);
			mm.createAll(windowSize, storeAndCreateSubAlignments);
			System.out.println("Creating files ...");
			for (int i = 0; i < mm.getTreeCount(); i++) {
				String fn = in_filename + "." + Integer.toString(i + 1) + ".";

				NJFile njFile = new NJFile(fn + "nj", mm.getTrees(i));
				njFile.createFile();

				if (storeAndCreateSubAlignments) {
					MSAFile msaFile = new MSAFile(fn + "seq", mm.getMsas(i));
					msaFile.createFile();
				}
			}
			System.out.println("Done.");
			return;
		}

		if (args[0].equalsIgnoreCase("-m")) {
			int windowSize = 5;
			boolean storeAndCreateSubAlignments = false;
			for (String s : args) {
				if (s.startsWith("-w"))
					windowSize = Converter.toInt(s.substring(2), 5);
				 if (s.equalsIgnoreCase("-s"))
					 storeAndCreateSubAlignments = true;

			}
			
			if (windowSize < 1)
				windowSize = 5;

			MiniMiner mm = new MiniMiner(msa, clustalTossGaps, kimura);
			System.out.println("Creating All NJ Trees...");
			mm.createAll(windowSize, storeAndCreateSubAlignments);
			
			System.out.println("Creating All Scores...");
			mm.createScores(minerBug);

			String fn = in_filename + ".scores.txt";
			ScoreFile scoreFile = new ScoreFile(fn, mm.getScores());
			System.out.println("Saving score file");
			scoreFile.createFile();

			fn = in_filename + ".scores.z.txt";
			scoreFile = new ScoreFile(fn, mm.getZScores());
			scoreFile.createFile();
			System.out.println("Done.");
			return;
		}

	}
}
