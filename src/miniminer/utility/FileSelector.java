package miniminer.utility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import javax.jnlp.FileContents;
import javax.jnlp.FileOpenService;
import javax.jnlp.ServiceManager;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

public class FileSelector {

	public static boolean isFileOk(Object file) {
		return (file != null)
				&& (file instanceof String || file instanceof BufferedReader || file instanceof BufferedWriter);
	}

	public static Object selectAFileToRead(JFrame f) {
		try {
			JFileChooser fc = new JFileChooser(".");
			if (fc.showOpenDialog(f) == JFileChooser.APPROVE_OPTION)
				return fc.getSelectedFile().getPath();
			else
				return (String) null;

		} catch (Throwable e) {
			e.printStackTrace();
		}
		// if not returned any thing and got out, try JWS stuff
		try {
			FileOpenService fos = null;
			FileContents fileContents = null;

			fos = (FileOpenService) ServiceManager
					.lookup("javax.jnlp.FileOpenService");
			if (fos != null) {
				fileContents = fos.openFileDialog(null, null);
				if (fileContents != null) {
					InputStreamReader ir = new InputStreamReader(fileContents
							.getInputStream());
					return new BufferedReader(ir);
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Object selectAFileToWrite(JFrame f) {
		try {
			JFileChooser fc = new JFileChooser(".");
			if (fc.showSaveDialog(f) == JFileChooser.APPROVE_OPTION)
				return fc.getSelectedFile().getPath();
			else
				return (String) null;

		} catch (Throwable e) {
			e.printStackTrace();
		}
		// if not returned any thing and got out, try JWS stuff
		try {
			FileOpenService fos = null;
			FileContents fileContents = null;

			fos = (FileOpenService) ServiceManager
					.lookup("javax.jnlp.FileOpenService");
			if (fos != null) {
				fileContents = fos.openFileDialog(null, null);
				if (fileContents != null) {
					OutputStreamWriter ir = new OutputStreamWriter(fileContents
							.getOutputStream(true));
					return new BufferedWriter(ir);
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

}
