package miniminer;

public enum AminoAcid {
	/* @@formatter:off */
	A("Alanine", "Ala", 'A', false, "neutral", 1.8, 0.072658), // 
	R("Arginine", "Arg", 'R', true, "positive", -4.5, 0.052012), // 
	N("Asparagine", "Asn", 'N', true, "neutral", -3.5, 0.042931), //
	D("Aspartic acid", "Asp", 'D', true, "negative", -3.5, 0.050007), //
	C("Cysteine", "Cys", 'C', false, "neutral", 2.5, 0.024692), //
	E("Glutamic acid", "Glu", 'E', true, "negative", -3.5, 0.061087), //
	Q("Glutamine", "Gln", 'Q', true, "neutral", -3.5, 0.039871), //
	G("Glycine", "Gly", 'G', false, "neutral", -0.4, 0.071589), //
	H("Histidine", "His", 'H', true, "positive", -3.2, 0.023392), //
	I("Isoleucine", "Ile", 'I', false, "neutral", 4.5, 0.052691), //
	L("Leucine", "Leu", 'L', false, "neutral", 3.8, 0.089093), //
	K("Lysine", "Lys", 'K', true, "positive", -3.9, 0.063923), //
	M("Methionine", "Met", 'M', false, "neutral", 1.9, 0.023150), //
	F("Phenylalanine", "Phe", 'F', false, "neutral", 2.8, 0.041774), //
	P("Proline", "Pro", 'P', false, "neutral", -1.6, 0.052228), //
	S("Serine", "Ser", 'S', true, "neutral", -0.8, 0.073087), //
	T("Threonine", "Thr", 'T', true, "neutral", -0.7, 0.055606), //
	W("Tryptophan", "Trp", 'W', false, "neutral", -0.9, 0.012720), // 
	Y("Tyrosine", "Tyr", 'Y', true, "neutral", -1.3, 0.032955), //
	V("Valine", "Val", 'V', false, "neutral", 4.2, 0.063321), //

	B("B", "B", 'B', false, "", 0.0, 0.0), //
	X("X", "X", 'X', false, "", 0.0, 0.0), //
	Z("Z", "Z", 'Z', false, "", 0.0, 0.0), //
	O("O", "O", 'O', false, "", 0.0, 0.0), //
	J("J", "J", 'J', false, "", 0.0, 0.0), //
	U("U", "U", 'U', false, "", 0.0, 0.0), //

	Gap("Gap", "Gap", '-', false, "", 0.0, 0.0), //
	Unknown("Unknown", "Unkwon", '?', false, "", 0.0, 0.0)//
	/* @@formatter:on */
	;//

	private final char letter;
	private final String shortName;
	private final String name;
	private final boolean polarity;
	private final String sidechainCharge;
	private final double hidropathyIndex;
	private final double backgroundAbundance;
	
	private final static String WarningMessage = "WARNING: threre is an illigal character ('%c') in the sequences. miniMiner is treating it as a [Unknown].\n";

	AminoAcid(String name, String shortName, char letter, boolean polarity, String sideChaneCharge,
		double hydropathyIndex, double backgroundAbundance) {
		this.letter = letter;
		this.shortName = shortName;
		this.name = name;
		this.hidropathyIndex = hydropathyIndex;
		this.polarity = polarity;
		this.sidechainCharge = sideChaneCharge;
		this.backgroundAbundance = backgroundAbundance;
	}

	public char getLetter() {
		return letter;
	}

	public String getShortName() {
		return shortName;
	}

	public String getName() {
		return name;
	}

	public boolean isPolarity() {
		return polarity;
	}

	public String getSidechainCharge() {
		return sidechainCharge;
	}

	public double getHidropathyIndex() {
		return hidropathyIndex;
	}

	public double getBackgroundAbundance() {
		return backgroundAbundance;
	}

	public static AminoAcid[] AminoAcids = AminoAcid.values();

	public static AminoAcid valueOf(char letter) {
		if (letter == ' ' && letter == '.' || letter == '-')
			return AminoAcid.Gap;
		else
			try {
				return AminoAcid.valueOf("" + letter);
			} catch (IllegalArgumentException iae) {
				System.err.printf(WarningMessage, letter);
				return AminoAcid.Unknown;
			}
	}

	@Override
	public String toString() {
		return "" + letter;
	}

}
