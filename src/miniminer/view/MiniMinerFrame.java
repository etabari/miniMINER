package miniminer.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Date;
import java.util.zip.DataFormatException;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import miniminer.MultipleSequenceAlignment;
import miniminer.files.NJFile;
import miniminer.tree.NJTree;
import miniminer.utility.FileSelector;

public class MiniMinerFrame extends JFrame implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8159562742563891875L;

	public static final String title = "MiniMiner - A protein active site prediction tool";

	MultipleSequenceAlignment msa;

	JMenuItem jmiOpenMsa;
	JCheckBoxMenuItem jmiTossGaps;
	JCheckBoxMenuItem jmiKimura;
	JMenuItem jmiCreateTree;

	JTextArea logText;

	public MiniMinerFrame() {
		super(title);

		logText = new JTextArea(5, 20);
		JScrollPane logScrollPane = new JScrollPane(logText);
		logText.setEditable(false);

		this.getContentPane().add(logScrollPane);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(200, 300);
		setupMenus();

	}

	private void setupMenus() {

		JMenu jmMsa = new JMenu("MSA");
		jmMsa.setMnemonic(KeyEvent.VK_F);

		jmiOpenMsa = new JMenuItem("Load Sequences...");
		jmiOpenMsa.setMnemonic(KeyEvent.VK_O);
		jmiOpenMsa.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		jmiOpenMsa.addActionListener(this);
		jmMsa.add(jmiOpenMsa);

		JMenu jmTree = new JMenu("Tree");
		jmTree.setMnemonic(KeyEvent.VK_T);

		jmiTossGaps = new JCheckBoxMenuItem("Exclude Positions with gaps");
		jmiTossGaps.setMnemonic(KeyEvent.VK_G);
		jmiTossGaps.setSelected(true);
		jmTree.add(jmiTossGaps);

		jmiKimura = new JCheckBoxMenuItem("Correct for Multiple Substitutions (Kimura)");
		jmiKimura.setMnemonic(KeyEvent.VK_M);
		jmTree.add(jmiKimura);

		jmTree.addSeparator();

		jmiCreateTree = new JMenuItem("Draw Tree");
		jmiCreateTree.setMnemonic(KeyEvent.VK_T);
		jmiCreateTree.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.CTRL_MASK));
		jmiCreateTree.addActionListener(this);
		jmTree.add(jmiCreateTree);

		JMenuBar menuBar = new JMenuBar();
		menuBar.add(jmMsa);
		menuBar.add(jmTree);

		this.setJMenuBar(menuBar);
	}

	//@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == jmiOpenMsa)
			loadMSA();

		if (e.getSource() == jmiCreateTree && msa != null)
			createTree();
	}

	private void loadMSA() {
		logText.append("<<<<<<< " + new Date().toString() + " >>>>>>>\n");
		logText.append("Select a Multiple Sequence Alignment file...\n");
		try {
			Object file = FileSelector.selectAFileToRead(this);
			if (!FileSelector.isFileOk(file)) {
				logText.append("  No file selected.\n");
				return;
			}
			msa = new MultipleSequenceAlignment();
			if (!msa.loadSeqs(file)) {
				logText.append("  No file selected or file access failed.\n");
				return;
			}

			logText.append("MSA file loaded.\n  ");
			logText.append(Integer.toString(msa.size()) + " sequences are loaded from file.\n");
		} catch (DataFormatException ex) {
			logText.append("This might be a bad file:\n  ");
			logText.append(ex.getMessage() + "\n");
		}
	}

	private void createTree() {
		logText.append("Create Tree invoked for the current MSA.\n");
		logText.append(" Creating Clustal Distance matrix\n");


		logText.append("   done. OverSpill value \n");
		logText.append(" Creating NT Jtree\n");
		NJTree njTree = new NJTree(msa);

		njTree.createTree(false, jmiKimura.isSelected());

		logText.append("   done - tree Created:\n");
		logText.append(njTree.getTreeString());

		logText.append("\n\nSelect a file to write ...\n");
		Object file = FileSelector.selectAFileToWrite(this);
		if (!FileSelector.isFileOk(file)) {
			logText.append("  No file selected.\n");
			return;
		}
		NJFile njFile = new NJFile(file, njTree);
		njFile.createFile();

		logText.append("Creating the file ...\n");
		njFile.createFile();
		logText.append("   done - file Created.\n");

	}
}
