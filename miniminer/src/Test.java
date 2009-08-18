import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.DataFormatException;

import sun.misc.Sort;
import miniminer.MultipleSequenceAlignment;
import miniminer.files.MSAFile;
import miniminer.files.NJFile;
import miniminer.tree.ClustalDistanceMatrix;
import miniminer.tree.NJTree;
import miniminer.tree.PhyloTree;
import miniminer.utility.Converter;
import miniminer.utility.DataFileReader;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		

//		Pattern pattern = Pattern
//			.compile("(?:SEQ|Node):\\s+(\\d+)\\s+\\(\\s+(-?\\d+\\.?\\d+)\\).*(?:SEQ|Node):\\s+(\\d+)\\s+\\(\\s+(-?\\d+\\.?\\d+)\\).*");
//
//		String line = " Cycle  28     =  SEQ:  10 (  0.18832) joins  SEQ:  17 (  0.20850) joins ";
//		Hashtable<Integer, String> part = new Hashtable<Integer, String>();
//		Matcher matcher;
//		if (line.contains("Cycle") && ( matcher = pattern.matcher(line)).find()) {
//
//			String og_part = "";
//			int g1 = Converter.toInt(matcher.group(1));
//			int g3 = Converter.toInt(matcher.group(3));
//			double g2 = Converter.toDouble(matcher.group(2));
//			double g4 = Converter.toDouble(matcher.group(4));
//			
//			if (part.containsKey(g3)) {
//				if (g2 == g4 || g2==0.0 || g4==0.0)
//					og_part = part.get(g3);
//			
//				part.put(g1, Converter.checkString(part.get(g1)) + g3 + " "+part.get(g3) + " ");
//				part.remove(g3);
//				
//			}
//			else {
//				
//				String g1Value = Converter.checkString(part.get(g1));
//				
//				part.put(g1,  g1Value + g3);
//			}
//		}
//		
//		for(Integer key: part.keySet())
//			System.out.printf("%d: %s\n", key, part.get(key));
			
	
			

		// MultipleSequenceAlignment ms = new MultipleSequenceAlignment();
		// try {
		// ms.loadSeqs("D:\\Data\\UNCC\\Lab\\Phylogenetic Tree\\sample2.txt");
		// } catch (DataFormatException e) {
		// e.printStackTrace();
		// }
		// //ms.loadPsl(FileName)
		// // MultipleSequenceAlignment mss = (MultipleSequenceAlignment)
		// ms.clone();
		// // mss.get(0).name = "222";
		// // System.out.println(ms.get(0).name);
		// //
		// // Sequence c = new Sequence("Sasa", "12345");
		// // c.changeData(2, 'e');
		// //
		// // System.out.println(c.getData());
		//
		// double [][] dist = FortranPorted.calculateDistances(ms);
		//		
		// for(int i=0; i<dist.length; i++) {
		// for(int j=0; j<=i; j++)
		// System.out.printf("%8.2f,",dist[i][j]);
		// System.out.println("");
		// }
		//		
		// AminoAcidInfo a = new AminoAcidInfo(AminoAcidInfo.Type.PROTEIN);
		// System.out.println(
		// a.loadAminoAcidRelations("D:\\Data\\UNCC\\Lab\\Phylogenetic Tree\\aainfo.txt"));
		//		
		// String s = "\ndffdsfs";
		// s += " ";
		// String[] l = s.split("\n");
		//
		// System.out.println(l.length);
		//
		// for (int i = 0; i < l.length - 1; i++) {
		// System.out.println(l[i]);
		// }
		// String last = l[l.length - 1];
		// if (last.length() > 1)
		// System.out.print(last.substring(0, last.length() - 1));
		// System.out.println("END!");

		// MultipleSequenceAlignment ms = new MultipleSequenceAlignment();
		// try {
		// ms.loadSeqs("D:\\Data\\UNCC\\Lab\\Phylogenetic Tree\\sample2.txt");
		// } catch (DataFormatException e) {
		// e.printStackTrace();
		// }
		//
		// ClustalDistanceMatrix cmat = new ClustalDistanceMatrix(ms, true,
		// false);
		// NJTree njTree = new NJTree();
		//
		// PhyloTree phyloTree = new PhyloTree(ms.size());
		// StringBuilder tree = new StringBuilder();
		//
		// njTree.generateTree(phyloTree, cmat, 1, ms.size(), ms.size(), tree);
		//		
		//
		// NJFile file = new NJFile("d:\\1.txt", cmat, netree);
		// file.createFile();
		//
//		
//		MultipleSequenceAlignment msa = new MultipleSequenceAlignment();
//		try {
//			msa.loadSeqs("D:\\a\\5FIT--_msa.seq");
//		} catch (DataFormatException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		String treeString="";
//		DataFileReader fr = new DataFileReader();
//		try {
//			BufferedReader reader = fr.readAFile("D:\\a\\e\\miner_14332\\nj_trees\\1-5_.nj");
//			String line;
//			while ((line = reader.readLine()) != null) {
//				treeString +=line + "\n";
//			}
//			reader.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block\n
//			e.printStackTrace();
//		}
//		
//		
//		
//		
//		NJTree njTree = new NJTree(treeString);
//		njTree.createTree(true, true);
//		String[] lines = njTree.getTreeString().split("\n");
//
//		Pattern pattern = Pattern
//			.compile("(?:SEQ|Node):\\s+(\\d+)\\s+\\(\\s+(-?\\d+\\.?\\d+)\\).*(?:SEQ|Node):\\s+(\\d+)\\s+\\(\\s+(-?\\d+\\.?\\d+)\\).*");
//		// Matcher matcher = pattern.matcher(inputStr);
//		// boolean matchFound = matcher.find();
//		Hashtable<Integer, String> part = new Hashtable<Integer, String>();
//		HashSet<String> sum_id = new HashSet<String>();
//		HashSet<String> leafs = new HashSet<String>();
//		Matcher matcher;
//
//		for (String line : lines)
//			if (line.contains("Last cycle") || line.contains("trichotomy"))
//				break;
//			else if (line.contains("Cycle") && (matcher = pattern.matcher(line)).find()) {
//
//				String og_part = "";
//				int g1 = Converter.toInt(matcher.group(1));
//				int g3 = Converter.toInt(matcher.group(3));
//				double g2 = Converter.toDouble(matcher.group(2));
//				double g4 = Converter.toDouble(matcher.group(4));
//
//				if (part.containsKey(g3)) {
//					if (g2 == g4 || g2 == 0.0 || g4 == 0.0)
//						og_part = part.get(g3);
//					part.put(g1, Converter.checkString(part.get(g1)) + g3 + " " + part.get(g3)
//						+ " ");
//					part.remove(g3);
//					
//					sum_id.add(g1 + " => " + part.get(g1));
//					if (og_part != null || og_part != "")
//						sum_id.remove(g3 + " => " + og_part);
//
//						
//				} else {
//					if (part.get(g1) != null || g2 == g4 || g2 == 0.0 || g4 == 0.0)
//						og_part = part.get(g1);
//					part.put(g1, Converter.checkString(part.get(g1)) + g3);
//					
//					sum_id.add(g1 + " => " + part.get(g1));
//					if (og_part != null || og_part != "")
//						sum_id.remove(g1 + " => " + og_part);
//				}
//
//			}
//		
//		for(Integer key: part.keySet())
//			System.out.printf("%d=%s\n", key, part.get(key));
//		
//		System.out.println("\n\n all sum_id are: \n\n");
//		for (String s: sum_id) 
//			System.out.println(s);
//		
//		for (String s: sum_id) {
//			String pmv_str = s.replace("=>", "");
//			int [] pmv = Converter.toIntArray(pmv_str); 
//			Arrays.sort(pmv);
//			
//			String r = Converter.toString(pmv);
//			
//			leafs.add(new String(r));
//				
//			
//		}
//		
//		System.out.println("\n\n all leafs are: \n\n");
//		for (String s: leafs) 
//			System.out.println(s);

		MultipleSequenceAlignment msa = new MultipleSequenceAlignment();
		
		try {
			msa.loadSeqs("d:\\a\\e\\2CND--_msa.seq");
		} catch (DataFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		msa = msa.getMasked(50);
		
		NJTree njt = new NJTree(msa);
		njt.createTree(false, false);

		HashSet<String> leavesAll = njt.createPmSum();
		int wholeLeafCount = leavesAll.size();
		System.out.println("Leaves All: ");
		for (String s : leavesAll) {
			System.out.println(s);
		}
		
		String treeString="";

		try {
	//		BufferedReader reader = DataFileReader.readAFile("D:\\a\\e\\miner_14332\\nj_trees\\nj (248).nj");
			BufferedReader reader = DataFileReader.readAFile("D:\\a\\e\\miner_1432_e\\njtrees\\nj (248).nj");
			String line;
			while ((line = reader.readLine()) != null) {
				treeString +=line + "\n";
			}
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block\n
			e.printStackTrace();
		}
		
		
		NJTree njTree = new NJTree(treeString);

		HashSet<String> leaves = njTree.createPmSum();
		
		System.out.println("Leaves: ");
		
		int leafCount = leaves.size();

		int similar = 0;
		for (String s : leaves)
			if (leavesAll.contains(s)) {
				similar++;
				System.out.println("+ "+ s);
			}
			else 
				System.out.println("- "+ s);
		int score = wholeLeafCount - similar + leafCount - similar;

		System.out.printf("\nSimilar: %d, wholeleaf:%d, leaf:%d Score %d\n",similar, wholeLeafCount, leafCount, score);

		
	}



}
