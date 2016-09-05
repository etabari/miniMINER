package miniminer.utility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class Converter {

	public static int toBound(int value, int min, int max) {
		return Math.max(min, Math.min(value, max));
	}
	
	public static double toDouble(String s) {
		try {
			return Double.parseDouble(s);
		} catch (NumberFormatException e) {
			return Double.NaN;
		}
	}

	public static double toDouble(String s, double def) {
		try {
			return Double.parseDouble(s);
		} catch (NumberFormatException e) {
			return def;
		}
	}

	public static int toInt(String s) {
		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return Integer.MAX_VALUE;
		}
	}

	public static int toInt(String s, int def) {
		try {
			if (s.length()==0)
				return def;
			return Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return def;
		}
	}

	public static String checkString(String str) {
		if (str == null || str == "")
			return "";
		return str + " ";
	}
	
	public static int[] toIntArray(String s) {
		int [] result = null;
		try {
			String [] rs = s.split("\\s+");
			result = new int [rs.length];
			for(int i=0;i<rs.length; i++)
				result[i] =  Integer.parseInt(rs[i]);
		} catch (NumberFormatException e) {
		}
		return result;
	}
	
	public static String toString(int []p) {
		StringBuilder s = new StringBuilder(p.length*3);
		for(int i: p) {
			s.append(i);
			s.append(' ');
		}
		return new String(s);
	}
	
	public static <T extends Comparable<? super T>> Collection<T> sortCollection(Collection<T> c) {
		List<T> sortedC = new ArrayList<T>();
		sortedC.addAll(c);
		Collections.sort(sortedC);
		return sortedC;
	}
	
}
