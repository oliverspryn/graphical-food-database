package graphicalfoodsearch;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;

enum BrowserType {
	OPEN, SAVE;
}

class BigOvenFiles extends FileFilter {
	private final String Description;
	private final String Extension;
	
	public BigOvenFiles(String extension, String description) {
		Description = description;
		Extension = extension;
	}
	
	@Override
	public boolean accept(File file) {
		return (file.isDirectory() || file.getName().toLowerCase().endsWith(Extension));
	}

	@Override
	public String getDescription() {
		return Description;
	}
}

public class Window extends JFrame implements ActionListener {
//File browser
	private String Extension;
	private final JFileChooser FileBrowser;
	private BigOvenFiles Filter;
	
//Menu handles
	private JMenu File;
	private JMenuBar MenuBar;
	
	private JMenuItem New;
	private JMenuItem Open;
	private JMenuItem Save;
	private JMenuItem SaveAs;
	private JMenuItem Exit;
	
//Window setup and properties
	private final JPanel Canvas;
	private final Dimension Size;
	private final Toolkit Toolkit;

	public Window(String title) {
		super(title);
		
		FileBrowser = new JFileChooser();
		Toolkit = getToolkit();
		Size = Toolkit.getScreenSize();
		
	//Place the window in the center of the screen, at half screen size
		setBounds(Size.width / 4, Size.height / 4, Size.width / 2, Size.height / 2);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		
	//Setup the JPanel as a canvas
		Canvas = new JPanel();
		Canvas.setBackground(Color.WHITE);
		add(Canvas);
		
	//Configure the file browser extension
		Extension = "*.*";
		Filter = new BigOvenFiles(Extension, "All Readable Files");
		
	//Create the menu
		SetupMenu();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String path;
		
		if(e.getSource() == New) {
			System.out.println("Super dude");
		} else if (e.getSource() == Open) {
			path = OpenFileBrowser(BrowserType.OPEN);
		} else if (e.getSource() == Save || e.getSource() == SaveAs) {
			path = OpenFileBrowser(BrowserType.SAVE);
		} else if (e.getSource() == Exit) {
			dispose();
		}
	}
	
	public JPanel GetPanel() {
		return Canvas;
	}
	
	private String OpenFileBrowser(BrowserType type) {
		int result = -1;//FileBrowser.show
		
		if (type == BrowserType.OPEN) {
			result = FileBrowser.showOpenDialog(this);
		} else {
			result = FileBrowser.showSaveDialog(this);
		}
		
		FileBrowser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		FileBrowser.addChoosableFileFilter(Filter);
		FileBrowser.setFileFilter(Filter);
		return "Things";
	}
	
	public void SetExtension(String extension) {
		Extension = extension;
		Filter = new BigOvenFiles(extension, String.format("BigOven Graph Files (%s)", extension));
	}
	
	private void SetupMenu() {
		MenuBar = new JMenuBar();
		
	//Add in the top-level "File" menu
		File = new JMenu("File");
		File.setMnemonic('F');
		
	//Add all of the items to the "File" menu
		New    = File.add("New");
		Open   = File.add("Open");
		File.addSeparator();
		
		Save   = File.add("Save");
		SaveAs = File.add("Save As...");
		File.addSeparator();
		
		Exit   = File.add("Exit");
		
	//Add all of the keyboard event listeners
		New.setAccelerator(KeyStroke.getKeyStroke('N', InputEvent.CTRL_DOWN_MASK));
		Open.setAccelerator(KeyStroke.getKeyStroke('O', InputEvent.CTRL_DOWN_MASK));
		Save.setAccelerator(KeyStroke.getKeyStroke('S', InputEvent.CTRL_DOWN_MASK));
		SaveAs.setAccelerator(KeyStroke.getKeyStroke('S', InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
		Exit.setAccelerator(KeyStroke.getKeyStroke('X', InputEvent.CTRL_DOWN_MASK));
		
	//Register the event listeners
		New.addActionListener(this);
		Open.addActionListener(this);
		Save.addActionListener(this);
		SaveAs.addActionListener(this);
		Exit.addActionListener(this);
		
	//Add all menu items to the menu bar
		MenuBar.add(File);
		setJMenuBar(MenuBar);
	}
}