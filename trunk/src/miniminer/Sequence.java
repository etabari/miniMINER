package miniminer;

public class Sequence {
	public String name;
	private int[] data;

	public final static String AminoAcidCodes = "ABCDEFGHIJKLMNOPQRSTUVWXYZ-";
	public final static int Gap = AminoAcidCodes.indexOf('-');;

	public Sequence(Sequence sequence) {
		this.data = sequence.data.clone();
		this.name = sequence.name;
	}

	public Sequence(String Name, int[] Data) {
		this.name = Name;
		this.data = new int[Data.length];
		for (int i = 0; i < Data.length; i++)
			this.data[i] = Data[i];
	}

	public Sequence(String Name, String Data) {
		this.name = Name;
		this.data = new int[Data.length()];
		for (int i = 0; i < Data.length(); i++)
			data[i] = AminoAcidCodes.indexOf(Data.charAt(i));
		
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new Sequence(this);
	};

	public String getName() {
		return name;
	}

	public int getDataLength() {
		return data.length;
	}

	public int[] getData() {
		return data;
	}

	public int[] getData(int start, int length) {
		int[] result = new int[length];
		for (int i = 0; i < length; i++)
			result[i] = data[start + i];

		return result;
	}

	public String getDataString() {
		StringBuilder rb = new StringBuilder(data.length + 1);
		for (int i = 0; i < data.length; i++)
			rb.append(AminoAcidCodes.charAt(data[i]));
		return new String(rb);
	}

	public int getBaseIndexAt(int i) {
		return data[i];
	}

	public char getBaseAt(int i) {
		return AminoAcidCodes.charAt(data[i]);
	}

//	public static int getBaseIndex(char c) {
//		return AminoAcidCodes.indexOf(c);
//	}
//
	public void setBaseIndexAt(int i, int baseIndex) {
		data[i] = baseIndex;// AminoAcidCodes.indexOf(c);
	}

	public void setBaseAt(int i, char base) {
		// data = data.substring(0,i) + base + data.substring(i+1,data.length());
		data[i] = AminoAcidCodes.indexOf(base);
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return getDataString();
	}

}
