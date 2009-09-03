package miniminer;



public class Sequence {
	public String name;
	private AminoAcid[] data;

	public Sequence(Sequence Sequence) {
		this.data = Sequence.data.clone();
		this.name = Sequence.name;
	}

	public Sequence(String Name, AminoAcid[] Data) {
		this.name = Name;
		this.data = new AminoAcid[Data.length];
		for (int i = 0; i < Data.length; i++)
			this.data[i] = Data[i];
	}

	public Sequence(String Name, String Data) {
		
		this.name = Name;
		this.data = new AminoAcid[Data.length()];
		for (int i = 0; i < Data.length(); i++)
			data[i] = AminoAcid.valueOf(Data.charAt(i));
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

	public AminoAcid[] getData() {
		return data;
	}

	public AminoAcid[] getData(int start, int length) {
		AminoAcid[] result = new AminoAcid[length];
		for (int i = 0; i < length; i++)
			result[i] = data[start + i];

		return result;
	}

	public String getDataString() {
		StringBuilder rb = new StringBuilder(data.length + 1);
		for (int i = 0; i < data.length; i++)
			rb.append(data[i].getLetter());
		return new String(rb);
	}

	public AminoAcid getBaseAt(int i) {
		return data[i];
	}

	public char getBaseCharAt(int i) {
		return getBaseAt(i).getLetter();
	}


	public void setBaseAt(int i, AminoAcid base) {
		data[i] = base;// AminoAcidCodes.indexOf(c);
	}

	public void setBaseAt(int i, char base) {
		data[i] = AminoAcid.valueOf(base);
	}
	
	@Override
	public String toString() {
		return getDataString();
	}

}
