package miniminer.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import miniminer.MultipleSequenceAlignment;
import miniminer.utility.Converter;

public class NJTree {

	private final static boolean debug = false;

	private MultipleSequenceAlignment msa;
	private BaseDistanceMatrix distMat;
	private String treeString;
	private PhyloTree phyTree;

	// private HashSet<String> leaves;

	protected NJTree(int NumSeq) {
		this.msa = null;
		treeString = "";
		phyTree = new PhyloTree(NumSeq);
	}

	public NJTree(MultipleSequenceAlignment msa) {
		this.msa = msa;
		treeString = "";
		phyTree = new PhyloTree(msa.size());
	}

	public NJTree(String treeString) {
		this.treeString = treeString;
	}

	public static NJTree createTree(BaseDistanceMatrix dm) {
		if (dm.isAllGapTossed()) {
			NJTree njTree = new NJTree(dm.getSize());
			njTree.distMat = dm;
			BaseDistanceMatrix mat = new BaseDistanceMatrix(njTree.distMat);
			njTree.generateTree(mat, 1, dm.getSize(), dm.getSize());
			mat = null;
			return njTree;
		}
		return null;
	}

	public boolean createTree(boolean tossGaps, boolean kimura) {
		if (msa != null) {
			distMat = new ClustalDistanceMatrix(msa, tossGaps, kimura);
			BaseDistanceMatrix mat = new BaseDistanceMatrix(distMat);
			generateTree(mat, 1, msa.size(), msa.size());
			mat = null;

		}
		msa = null;
		return treeString.length() > 0;
	}

	public BaseDistanceMatrix getDistanceMatrix() {
		return distMat;
	}

	public String getTreeString() {
		return treeString;
	}

	public PhyloTree getTree() {
		return phyTree;
	}

	// public HashSet<String> getLeaves() {
	// return leaves;
	// }

	protected void generateTree(BaseDistanceMatrix distMat, int firstSeq,
			int lastSeq, int numSeqs) {

		StringBuilder tree = new StringBuilder();

		double[] av;
		int[] tkill;
		boolean verbose = true;

		int i;
		int[] l = new int[4];
		int nude, k;
		int nc, mini, minj, j, ii, jj;
		double fnseqs, fnseqs2 = 0, sumd;
		double diq, djq, dij, dio, djo, da;
		double tmin, total, dmin;
		double bi, bj, b1, b2, b3;
		double[] branch = new double[4];
		int typei, typej; /* 0 = node; 1 = OTU */

		// int firstSeq = seqInfo.firstSeq;
		// int lastSeq = seqInfo.lastSeq;
		// int numSeqs = seqInfo.numSeqs;

		/* IMPROVEMENT 1, STEP 0 : declare variables */
		double[] sumCols, sumRows, join;

		sumCols = new double[numSeqs + 1];
		sumRows = new double[numSeqs + 1];
		join = new double[numSeqs + 1];

		/* IMPROVEMENT 2, STEP 0 : declare variables */
		int loop_limit;

		ValidNodeID[] tvalid;
		ValidNodeID lpi, lpj, lpii, lpjj;

		/*
		 * correspondence of the loop counter variables. i .. lpi->n, ii ..
		 * lpii->n j .. lpj->n, jj .. lpjj->n
		 */

		fnseqs = (double) lastSeq - firstSeq + 1;

		if (verbose) {
			tree.append("\n\n\t\t\tNeighbor-joining Method\n");
			tree
					.append("\n Saitou, N. and Nei, M. (1987) The Neighbor-joining Method:");
			tree
					.append("\n A New Method for Reconstructing Phylogenetic Trees.");
			tree
					.append("\n Mol. Biol. Evol., 4(4), 406-425\n\n\n This is an UNROOTED tree\n");
			tree.append("\n Numbers in parentheses are branch lengths\n\n");
		}

		if (fnseqs == 2) {
			if (verbose) {
				double d = distMat.getDist1(firstSeq, firstSeq + 1);
				String line = String
						.format(
								"Cycle   1     =  SEQ:   1 (%9.5f) joins  SEQ:   2 (%9.5f)",
								d, d);
				tree.append(line);
			}
			treeString = new String(tree);
			return;
		}
		mini = minj = 0;

		/* IMPROVEMENT 1, STEP 1 : Allocate memory */
		/* IMPROVEMENT 1, STEP 2 : Initialize arrays to 0 */

		tkill = new int[numSeqs + 1];
		av = new double[numSeqs + 1];

		for (int iii = 0; iii < numSeqs + 2; iii++) {
			phyTree.leftBranch[iii] = 0.0;
			phyTree.rightBranch[iii] = 0.0;

			if (iii < numSeqs + 1) {
				av[iii] = 0.0;
				tkill[iii] = 0;
			}
		}

		/* IMPROVEMENT 2, STEP 1 : Allocate memory */

		tvalid = new ValidNodeID[numSeqs + 1];
		for (int iii = 0; iii < tvalid.length; iii++)
			tvalid[iii] = new ValidNodeID();

		/*
		 * tvalid[0] is special entry in array. it points a header of valid
		 * entry list
		 */
		tvalid[0].n = 0;
		tvalid[0].prev = null;
		tvalid[0].next = tvalid[1];

		/* IMPROVEMENT 2, STEP 2 : Construct and initialize the entry chain list */
		for (i = 0, loop_limit = lastSeq - firstSeq + 1; i <= loop_limit; ++i) {

			av[i] = 0.0;
			tkill[i] = 0;
			distMat.setDistance(i, i, 0.0);

			tvalid[i].n = i;
			if (i > 0)
				tvalid[i].prev = tvalid[i - 1];
			if (i < loop_limit)
				tvalid[i].next = tvalid[i + 1];
		}
		tvalid[loop_limit].next = null;

		/*
		 * IMPROVEMENT 1, STEP 3 : Calculate the sum of score value that is
		 * sequence[i] to others.
		 */
		double matValue;
		sumd = 0.0;
		for (lpj = tvalid[0].next; lpj != null; lpj = lpj.next) {
			double tmp_sum = 0.0;
			j = lpj.n;
			/* calculate sumRows[j] */
			for (lpi = tvalid[0].next; lpi.n < j; lpi = lpi.next) {
				i = lpi.n;
				matValue = distMat.getDist1(i, j);
				tmp_sum = tmp_sum + matValue;
			}
			sumRows[j] = tmp_sum;

			tmp_sum = 0.0;
			/* Set lpi to that lpi->n is greater than j */
			if ((lpi != null) && (lpi.n == j)) {
				lpi = lpi.next;
			}
			/* calculate sumCols[j] */
			for (; lpi != null; lpi = lpi.next) {
				i = lpi.n;
				tmp_sum += distMat.getDist1(j, i);
			}
			sumCols[j] = tmp_sum;
		}

		/*********************** Enter The Main Cycle ***************************/

		for (nc = 1, loop_limit = (lastSeq - firstSeq + 1 - 3); nc <= loop_limit; ++nc) {

			sumd = 0.0;
			/* IMPROVEMENT 1, STEP 4 : use sum value */
			for (lpj = tvalid[0].next; lpj != null; lpj = lpj.next) {
				sumd += sumCols[lpj.n];
			}

			if (debug) {
				tree.append("\n TEST: tvalid ");
				for (lpj = tvalid[0].next; lpj != null; lpj = lpj.next)
					tree.append(String.format("%d ", lpj.n));

				tree.append("\n TEST: sumCols ");
				for (lpj = tvalid[0].next; lpj != null; lpj = lpj.next)
					tree.append(String.format("%9.5f ", sumCols[lpj.n]));

				tree.append("\n TEST: sumRows ");
				for (lpj = tvalid[0].next; lpj != null; lpj = lpj.next)
					tree.append(String.format("%9.5f ", sumRows[lpj.n]));

				tree.append(String.format("\n TEST: step=%d sumd=%9.5f", nc,
						sumd));
			}

			/* IMPROVEMENT 3, STEP 0 : multiply tmin and 2*fnseqs2 */
			fnseqs2 = fnseqs - 2.0; /* Set fnseqs2 at this point. */
			tmin = 99999.0 * 2.0 * fnseqs2;

			/*
			 * .................compute SMATij values and find the smallest one
			 * ........
			 */

			mini = minj = 0;

			/* jj must starts at least 2 */
			if ((tvalid[0].next != null) && (tvalid[0].next.n == 1)) {
				lpjj = tvalid[0].next.next;
			} else {
				lpjj = tvalid[0].next;
			}

			for (; lpjj != null; lpjj = lpjj.next) {
				jj = lpjj.n;
				for (lpii = tvalid[0].next; lpii.n < jj; lpii = lpii.next) {
					ii = lpii.n;
					diq = djq = 0.0;

					/* IMPROVEMENT 1, STEP 4 : use sum value */
					diq = sumCols[ii] + sumRows[ii];
					djq = sumCols[jj] + sumRows[jj];
					/*
					 * always ii < jj in this point. Use upper triangle of score
					 * matrix.
					 */
					dij = distMat.getDist1(ii, jj);
					/*
					 * IMPROVEMENT 3, STEP 1 : fnseqs2 is already calculated.
					 */
					/* fnseqs2 = fnseqs - 2.0 */

					/* IMPROVEMENT 4 : transform the equation */
					/*-------------------------------------------------------------------*
					 * OPTIMIZE of expression 'total = d2r + fnseqs2*dij + dr*2.0'       *
					 * total = d2r + fnseq2*dij + 2.0*dr                                 *
					 *       = d2r + fnseq2*dij + 2(sumd - dij - d2r)                    *
					 *       = d2r + fnseq2*dij + 2*sumd - 2*dij - 2*d2r                 *
					 *       =       fnseq2*dij + 2*sumd - 2*dij - 2*d2r + d2r           *
					 *       = fnseq2*dij + 2*sumd - 2*dij - d2r                         *
					 *       = fnseq2*dij + 2*sumd - 2*dij - (diq + djq - 2*dij)         *
					 *       = fnseq2*dij + 2*sumd - 2*dij - diq - djq + 2*dij           *
					 *       = fnseq2*dij + 2*sumd - 2*dij + 2*dij - diq - djq           *
					 *       = fnseq2*dij + 2*sumd  - diq - djq                          *
					 *-------------------------------------------------------------------*/
					total = fnseqs2 * dij + 2.0 * sumd - diq - djq;
					/*
					 * IMPROVEMENT 3, STEP 2 : abbrevlate the division on
					 * comparison between total and tmin.
					 */
					/* total = total / (2.0*fnseqs2); */

					if (total < tmin) {
						tmin = total;
						mini = ii;
						minj = jj;
					}
				}
			}

			if (debug) {
				tree.append(String
						.format("\n TEST: tmin=%9.5f mini=%d minj=%d", tmin,
								mini, minj));

				tree.append("\n TEST: av ");
				for (i = 1; i <= lastSeq - firstSeq + 1; ++i)
					tree.append(String.format("%9.5f ", av[i]));
			}

			/* MEMO: always ii < jj in avobe loop, so mini < minj */

			/*
			 * .................compute branch lengths and print the results
			 * ........
			 */

			dio = djo = 0.0;

			/* IMPROVEMENT 1, STEP 4 : use sum value */
			dio = sumCols[mini] + sumRows[mini];
			djo = sumCols[minj] + sumRows[minj];

			dmin = distMat.getDist1(mini, minj);
			dio = (dio - dmin) / fnseqs2;
			djo = (djo - dmin) / fnseqs2;
			bi = (dmin + dio - djo) * 0.5;
			bj = dmin - bi;
			bi = bi - av[mini];
			bj = bj - av[minj];

			if (av[mini] > 0.0) {
				typei = 0;
			} else {
				typei = 1;
			}
			if (av[minj] > 0.0) {
				typej = 0;
			} else {
				typej = 1;
			}

			if (debug)
				tree
						.append(String
								.format(
										"\n TEST: dio=%9.5f djo=%9.5f dmin=%9.5f bi=%9.5f bj=%9.5f typei=%d typej=%d",
										dio, djo, dmin, bi, bj, typei, typej));
			if (verbose) {
				tree.append(String.format("\n Cycle%4d     = ", nc));
			}

			/*
			 * set negative branch lengths to zero. Also set any tiny positive
			 * branch lengths to zero.
			 */
			if (Math.abs(bi) < 0.0001) {
				bi = 0.0;
			}
			if (Math.abs(bj) < 0.0001) {
				bj = 0.0;
			}

			if (verbose) {
				if (typei == 0) {
					tree.append(String.format("Node:%4d (%9.5f) joins ", mini,
							bi));
				} else {
					tree.append(String.format(" SEQ:%4d (%9.5f) joins ", mini,
							bi));
				}

				if (typej == 0) {
					tree.append(String.format("Node:%4d (%9.5f)", minj, bj));
				} else {
					tree.append(String.format(" SEQ:%4d (%9.5f)", minj, bj));
				}

				tree.append("\n");
			}

			phyTree.leftBranch[nc] = bi;
			phyTree.rightBranch[nc] = bj;

			for (i = 1; i <= lastSeq - firstSeq + 1; i++) {
				phyTree.treeDesc[nc][i] = 0;
			}

			if (typei == 0) {
				for (i = nc - 1; i >= 1; i--)
					if (phyTree.treeDesc[i][mini] == 1) {
						for (j = 1; j <= lastSeq - firstSeq + 1; j++)
							if (phyTree.treeDesc[i][j] == 1) {
								phyTree.treeDesc[nc][j] = 1;
							}
						break;
					}
			} else {
				phyTree.treeDesc[nc][mini] = 1;
			}

			if (typej == 0) {
				for (i = nc - 1; i >= 1; i--)
					if (phyTree.treeDesc[i][minj] == 1) {
						for (j = 1; j <= lastSeq - firstSeq + 1; j++)
							if (phyTree.treeDesc[i][j] == 1) {
								phyTree.treeDesc[nc][j] = 1;
							}
						break;
					}
			} else {
				phyTree.treeDesc[nc][minj] = 1;
			}

			/*
			 * Here is where the -0.00005 branch lengths come from for 3 or more
			 * identical seqs.
			 */
			/* if(dmin <= 0.0) dmin = 0.0001; */
			if (dmin <= 0.0) {
				dmin = 0.000001;
			}
			av[mini] = dmin * 0.5;

			/*
			 * ........................Re-initialisation...........................
			 * .....
			 */

			fnseqs = fnseqs - 1.0;
			tkill[minj] = 1;

			/* IMPROVEMENT 2, STEP 3 : Remove tvalid[minj] from chain list. */
			/*
			 * [ Before ] +---------+ +---------+ +---------+ |prev
			 * |<-------|prev |<-------|prev |<--- | n | | n(=minj)| | n | |
			 * next|------.| next|------.| next|---- +---------+ +---------+
			 * +---------+
			 * 
			 * [ After ] +---------+ +---------+ |prev
			 * |<--------------------------|prev |<--- | n | | n | |
			 * next|-------------------------.| next|---- +---------+
			 * +---------+ +---------+ null---|prev | | n(=minj)| | next|---null
			 * +---------+
			 */
			(tvalid[minj].prev).next = tvalid[minj].next;
			if (tvalid[minj].next != null) {
				(tvalid[minj].next).prev = tvalid[minj].prev;
			}
			tvalid[minj].prev = tvalid[minj].next = null;

			/* IMPROVEMENT 1, STEP 5 : re-calculate sum values. */
			for (lpj = tvalid[0].next; lpj != null; lpj = lpj.next) {
				double tmp_di = 0.0;
				double tmp_dj = 0.0;
				j = lpj.n;

				/*
				 * subtrace a score value related with 'minj' from sum arrays .
				 */
				if (j < minj) {
					tmp_dj = distMat.getDist1(j, minj);
					sumCols[j] -= tmp_dj;
				} else if (j > minj) {
					tmp_dj = distMat.getDist1(minj, j);
					sumRows[j] -= tmp_dj;
				} /* nothing to do when j is equal to minj. */

				/*
				 * subtrace a score value related with 'mini' from sum arrays .
				 */
				if (j < mini) {
					tmp_di = distMat.getDist1(j, mini);
					sumCols[j] -= tmp_di;
				} else if (j > mini) {
					tmp_di = distMat.getDist1(mini, j);
					sumRows[j] -= tmp_di;
				} /* nothing to do when j is equal to mini. */

				/*
				 * calculate a score value of the new inner node. then, store it
				 * temporary to join[] array.
				 */
				join[j] = (tmp_dj + tmp_di) * 0.5;
			}

			/*
			 * 1) Set the score values (stored in join[]) into the matrix,
			 * row/column position is 'mini'. 2) Add a score value of the new
			 * inner node to sum arrays.
			 */
			for (lpj = tvalid[0].next; lpj != null; lpj = lpj.next) {
				j = lpj.n;
				if (j < mini) {
					distMat.setDist1(j, mini, join[j]);
					sumCols[j] += join[j];
				} else if (j > mini) {
					distMat.setDist1(mini, j, join[j]);
					sumRows[j] += join[j];
				} /* nothing to do when j is equal to mini. */
			}

			/* Re-calculate sumRows[mini],sumCols[mini]. */
			sumCols[mini] = sumRows[mini] = 0.0;

			/* calculate sumRows[mini] */
			da = 0.0;
			for (lpj = tvalid[0].next; lpj.n < mini; lpj = lpj.next) {
				da = da + join[lpj.n];
			}
			sumRows[mini] = da;

			/* skip if 'lpj.n' is equal to 'mini' */
			if ((lpj != null) && (lpj.n == mini)) {
				lpj = lpj.next;
			}

			/* calculate sumCols[mini] */
			da = 0.0;
			for (; lpj != null; lpj = lpj.next) {
				da = da + join[lpj.n];
			}
			sumCols[mini] = da;

			/*
			 * Clean up sumRows[minj], sumCols[minj] and score matrix related
			 * with 'minj'.
			 */
			sumCols[minj] = sumRows[minj] = 0.0;
			for (j = 1; j <= lastSeq - firstSeq + 1; ++j) {
				distMat.setDist1(minj, j, 0.0);
				distMat.setDist1(j, minj, 0.0);
				join[j] = 0.0;
			}

		}
		/** end main cycle **/

		/****************************** Last Cycle (3 Seqs. left) ********************/

		if (debug)
			tree.append("\n TEST: l ");

		nude = 1;
		for (lpi = tvalid[0].next; lpi != null; lpi = lpi.next) {
			l[nude] = lpi.n;
			++nude;

			if (debug)
				tree.append(lpi.n).append(" ");
		}

		b1 = (distMat.getDist1(l[1], l[2]) + distMat.getDist1(l[1], l[3]) - distMat
				.getDist1(l[2], l[3])) * 0.5;
		b2 = distMat.getDist1(l[1], l[2]) - b1;
		b3 = distMat.getDist1(l[1], l[3]) - b1;

		branch[1] = b1 - av[l[1]];
		branch[2] = b2 - av[l[2]];
		branch[3] = b3 - av[l[3]];

		/* Reset tiny negative and positive branch lengths to zero */
		if (Math.abs(branch[1]) < 0.0001) {
			branch[1] = 0.0;
		}
		if (Math.abs(branch[2]) < 0.0001) {
			branch[2] = 0.0;
		}
		if (Math.abs(branch[3]) < 0.0001) {
			branch[3] = 0.0;
		}

		phyTree.leftBranch[lastSeq - firstSeq + 1 - 2] = branch[1];
		phyTree.leftBranch[lastSeq - firstSeq + 1 - 1] = branch[2];
		phyTree.leftBranch[lastSeq - firstSeq + 1] = branch[3];

		for (i = 1; i <= lastSeq - firstSeq + 1; i++) {
			phyTree.treeDesc[lastSeq - firstSeq + 1 - 2][i] = 0;
		}

		if (debug) {
			tree.append(String.format("\n TEST: b1=%9.5f b2=%9.5f b3=%9.5f",
					b1, b2, b3));
			tree.append(String.format("\n TEST: branch %9.5f %9.5f %9.5f",
					branch[1], branch[2], branch[3]));
		}

		if (verbose) {
			tree.append(String.format(
					"\n Cycle%4d (Last cycle, trichotomy):\n", nc));
		}

		for (i = 1; i <= 3; ++i) {
			if (av[l[i]] > 0.0) {
				if (verbose) {
					tree.append(String.format("\n\t\t Node:%4d (%9.5f) ", l[i],
							branch[i]));
				}
				for (k = lastSeq - firstSeq + 1 - 3; k >= 1; k--)
					if (phyTree.treeDesc[k][l[i]] == 1) {
						for (j = 1; j <= lastSeq - firstSeq + 1; j++)
							if (phyTree.treeDesc[k][j] == 1) {
								phyTree.treeDesc[lastSeq - firstSeq + 1 - 2][j] = i;
							}
						break;
					}
			} else {
				if (verbose) {
					tree.append(String.format("\n\t\t  SEQ:%4d (%9.5f) ", l[i],
							branch[i]));
				}
				phyTree.treeDesc[lastSeq - firstSeq + 1 - 2][l[i]] = i;
			}
			if (i < 3) {
				if (verbose) {
					tree.append("joins");
				}
			}
		}

		if (verbose) {
			tree.append("\n");
		}
		this.treeString = new String(tree);

	}

	public HashSet<String> createPmSum() {

		String[] lines = treeString.split("\n");

		Pattern pattern = Pattern
				.compile("(?:SEQ|Node):\\s+(\\d+)\\s+\\(\\s+(-?\\d+\\.?\\d+)\\).*(?:SEQ|Node):\\s+(\\d+)\\s+\\(\\s+(-?\\d+\\.?\\d+)\\).*");
		// Matcher matcher = pattern.matcher(inputStr);
		// boolean matchFound = matcher.find();
		Hashtable<Integer, String> part = new Hashtable<Integer, String>();
		HashSet<String> sum_id = new HashSet<String>();
		HashSet<String> leaves = new HashSet<String>();
		Matcher matcher;

		for (String line : lines)
			if (line.contains("Last cycle") || line.contains("trichotomy"))
				break;
			else if (line.contains("Cycle")
					&& (matcher = pattern.matcher(line)).find()) {
				// System.out.println(part.toString());

				String og_part = "";
				int g1 = Converter.toInt(matcher.group(1));
				int g3 = Converter.toInt(matcher.group(3));
				double g2 = Converter.toDouble(matcher.group(2));
				double g4 = Converter.toDouble(matcher.group(4));

				if (debug) {
					System.out.printf("TEST: Line: %s\n", line);
					System.out.printf("TEST: (%d,%d,%6.5f,%6.5f) ",g1,g3,g2,g4);
				}

				if (part.containsKey(g3)) {
					if (debug)
						System.out.printf("[%d in ] ", g3);
					
					//TODO I Changed this line of code so that it matches Miner  
					if (g2 == g4 /*|| g2 == 0.0 || g4 == 0.0*/) {
						og_part = part.get(g3);
						if (debug)
							System.out.print("[eqz] ");
					}

					part.put(g1, Converter.checkString(part.get(g1)) + g3 + " "
							+ part.get(g3) + " ");
					part.remove(g3);

					sum_id.add(g1 + " => " + part.get(g1));

					if (og_part != null && og_part != "") {
						sum_id.remove(g3 + " => " + og_part);

						if (debug)
							System.out.print("[rm] ");
					}

				} else {
					System.out.printf("[%d out] ", g3);

					//TODO I Changed this line of code so that it matches Miner   
					if (part.get(g1) != null || g2 == g4 /*|| g2 == 0.0
							|| g4 == 0.0*/) {
						og_part = part.get(g1);
						if (debug)
							System.out.print("[eqz] ");
					}

					part.put(g1, Converter.checkString(part.get(g1)) + g3);

					sum_id.add(g1 + " => " + part.get(g1));

					if (og_part != null && og_part != "") {
						sum_id.remove(g1 + " => " + og_part);

						if (debug)
							System.out.print("[rm] ");
					}
				}

				if (debug) {
					if (og_part == null || og_part == "")
						System.out.print("[]\n");
					else
						System.out.printf("[%s ]\n", og_part);

					System.out.print("TEST: Part {");
					ArrayList<String> ll = new ArrayList<String>(part.size());
					for (Integer k : part.keySet())
						ll.add(String.valueOf(k));
					for (String k : Converter.sortCollection(ll))
						System.out.printf("%s=%s , ", k, part.get(Integer
								.parseInt(k)));
					System.out.print("}\n");

					System.out.print("TEST: Sum_id {");
					for (String k : Converter.sortCollection(sum_id))
						System.out.printf("%s , ", k);

					System.out.print("}\n\n");
				}

			}

		if (debug) {
			System.out.print("\nTEST: sum_id: ");
			Collection<String> ll = new ArrayList<String>(sum_id.size());
			for (String s : Converter.sortCollection(sum_id))
				ll.add(s + " ");

			System.out.println(ll);
		}

		for (String s : sum_id) {
			String pmv_str = s.replace("=>", "");
			int[] pmv = Converter.toIntArray(pmv_str);
			Arrays.sort(pmv);
			String r = Converter.toString(pmv);
			leaves.add(new String(r));

		}

		return leaves;
	}

	private class ValidNodeID {
		int n = 0;
		ValidNodeID prev = null;
		ValidNodeID next = null;
	};
}
