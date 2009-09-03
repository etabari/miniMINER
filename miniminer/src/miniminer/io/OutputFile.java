package miniminer.io;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public abstract class OutputFile {

	String filename = null;
	BufferedWriter bw = null;

	public OutputFile(Object file) {
		if (file == null || file instanceof String)
			this.filename = (String) file;
		else if (file instanceof BufferedWriter) {
			this.filename = "";
			bw = (BufferedWriter) file;
		}
	}

	
	protected abstract void writeContent();
	
	public void createFile() {
		if (openFile()) {
			writeContent();
			close();
		}
	}
	
	protected boolean openFile() {
		if (filename == null)
			return false;
		if (bw != null)
			return true;
		try {
			bw = new BufferedWriter(new FileWriter(filename));
			return true;

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	protected boolean write(String str) {
		if (bw == null)
			return false;
		try {

			String[] lines = (str + " ").split("\n");
			for (int i = 0; i < lines.length - 1; i++) {
				bw.write(lines[i]);
				bw.newLine();
				// System.err.println(lines[i]);
			}
			String lastLine = lines[lines.length - 1];
			if (lastLine.length() > 1) {
				bw.write(lastLine.substring(0, lastLine.length() - 1));
				// System.err.print(lastLine.substring(0, lastLine.length() -
				// 1));
			}

			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	protected boolean newLine(int count) {
		if (bw == null)
			return false;
		try {
			for (int i = 0; i < count; i++)
				bw.newLine();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	protected boolean newLine() {
		return newLine(1);
	}

	protected boolean close() {
		if (bw == null)
			return false;
		try {
			bw.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;

		}
	}

}
