package miniminer;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Vector;
import java.util.zip.DataFormatException;

import miniminer.utility.DataFileReader;

public class MultipleSequenceAlignment extends Vector<Sequence> {

	private static final long serialVersionUID = 2051485568194956515L;

	public MultipleSequenceAlignment(int initialCapacity) {
		super(initialCapacity);
	}

	public MultipleSequenceAlignment() {
		this(16);
	}

	public boolean loadSeqs(Object data) throws DataFormatException {
		if (data == null)
			return false;
		else if (data instanceof String)
			return loadSeqs((String) data);
		else if (data instanceof BufferedReader)
			return loadSeqs((BufferedReader) data);
		else
			return false;
	}

	protected boolean loadSeqs(String fileName) throws DataFormatException {
		BufferedReader reader;
		try {
			reader = DataFileReader.readAFile(fileName);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return loadSeqs(reader);
	}

	protected boolean loadSeqs(BufferedReader reader) throws DataFormatException {
		if (reader == null)
			return false;
		try {

			String line = "";
			String seq = "";
			String name = "";
			int count = 0;
			while ((line = reader.readLine()) != null) {
				if (line.length() > 0 && line.charAt(0) == '>') {
					if (seq.length() > 0) {
						seq = seq.replace("*", "");
						this.add(new Sequence(name, seq));
						// System.out.printf("%s: %s\n", name, seq);
						seq = "";
						count++;
					}
					name = line.substring(1);

				} else
					seq += line.trim();
			}
			if (seq.length() > 0) {
				seq = seq.replace("*", "");
				this.add(new Sequence(name, seq));
				count++;
			}
			if (count != size())
				throw new DataFormatException(String.format("Not all sequences are loaded."
					+ " %d of %d sequences are loaded. "
					+ "Other sequences do not have the same length", size(), count));

		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			return false;

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			// CLOSE the READER ! don't be stupid !
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return true;
	}

	public MultipleSequenceAlignment getWindow(int start, int windowSize) {
		MultipleSequenceAlignment window = new MultipleSequenceAlignment(this.size());
		for (Sequence s : this)
			window.add(new Sequence(s.getName(), s.getData(start, windowSize)));
		return window;
	}

	@Override
	public synchronized Object clone() {
		MultipleSequenceAlignment result = new MultipleSequenceAlignment(this.size());
		for (Sequence s : this)
			result.add(new Sequence(s));

		return result;
	}

	@Override
	public synchronized boolean add(Sequence e) {
		if (size() > 0) {
			int length = getSequenceLength();
			if (e.getDataLength() != length)
				return false;
		}

		return super.add(e);
	}

	@Override
	public synchronized String toString() {
		String s = "(";
		for (Sequence seq : this)
			s += seq.getDataString() + ", ";
		return s + ")";
	}

	public int getSequenceLength() {
		return get(0).getDataLength();
	}

	public MultipleSequenceAlignment getMasked(int maskFilter) {

		MultipleSequenceAlignment result = new MultipleSequenceAlignment();

		int[] gapCount = new int[getSequenceLength()];
		int newSeqLength = 0;

		for (int pos = 0; pos < gapCount.length; pos++) {
			gapCount[pos] = 0;
			for (int seq = 0; seq < size(); seq++)
				if (get(seq).getBaseIndexAt(pos) == Sequence.Gap)
					gapCount[pos]++;

			if (1.0 * gapCount[pos] / size() <= maskFilter / 100.0) {
				gapCount[pos] = 0;
				newSeqLength++;
			}
		}

		int[] maskedSeq = new int[newSeqLength];

		for (int seq = 0; seq < size(); seq++) {
			int newSeqPos = 0;
			for (int pos = 0; pos < gapCount.length; pos++)
				if (gapCount[pos] == 0) {
					maskedSeq[newSeqPos] = get(seq).getBaseIndexAt(pos);
					newSeqPos++;
				}
			Sequence sequence = new Sequence(get(seq).getName(), maskedSeq);
			result.add(sequence);
		}

		return result;
	}
}
