package graphicalfoodsearch;

import graphicalfoodsearch.beans.ClickBean;
import graphicalfoodsearch.beans.FileBean;
import graphicalfoodsearch.beans.MouseMoveBean;
import graphicalfoodsearch.enums.BrowserType;
import graphicalfoodsearch.enums.OperationType;
import graphicalfoodsearch.listeners.IFileListener;
import graphicalfoodsearch.listeners.IMouseListener;
import graphicalfoodsearch.listeners.IMouseMoveListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Window extends JFrame implements ActionListener {
//File browser
	private String Extension;
	private final JFileChooser FileBrowser;
	private FileNameExtensionFilter Filter;
	
//Menu handles
	private JMenu File;
	private JMenuBar MenuBar;
	private JPopupMenu PopUp;
	private Boolean PopUpOpen = false;
	
	private JMenuItem New;
	private JMenuItem Open;
	private JMenuItem OpenPop;
	private JMenuItem Save;
	private JMenuItem SavePop;
	private JMenuItem SaveAs;
	private JMenuItem Exit;
	
//Window setup and properties
	private final Canvas Canvas;
	private final Dimension Size;
	private final Toolkit Toolkit;
	
//Event listener registry
	List<IMouseListener> ClickHandlers;
        List<IMouseListener> RightClickHandlers;
	List<IFileListener> FileHandlers;
        List<IMouseMoveListener> MouseMoveHandlers;

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
		Canvas = new Canvas(this);
		Canvas.setBackground(Color.WHITE);
		getContentPane().add(Canvas, BorderLayout.CENTER);
		
	//Configure the file browser extension
		Extension = "*.*";
		Filter = new FileNameExtensionFilter(Extension, "All Readable Files");
		
	//Create the menu
		SetupMenu();
		revalidate();
		repaint();
		
	//Prepare the event registry
		ClickHandlers = new ArrayList<>();
                RightClickHandlers = new ArrayList<>();
		FileHandlers = new ArrayList<>();
                MouseMoveHandlers = new ArrayList<>();
		
	//Listen for JPanel mouse clicks
		Canvas.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent me) {
				if(SwingUtilities.isLeftMouseButton(me)) {
					if(PopUpOpen) {
						PopUpOpen = false;
					} else {
					//Populate the Java bean
						ClickBean click = new ClickBean();
						click.SetX(me.getX());
						click.SetY(me.getY());
						
					//Dispatch the event
						ClickHandlers.stream().forEach((l) -> {
							l.ClickHandler(click);
						});
					}
				} else if (SwingUtilities.isRightMouseButton(me)) {
					// NOTE: I've commented this out for now so I can implement right-click
                                        // menus on GUI nodes. Making these coexist with the "global" right-click
                                        // menu should be possible, but it's not a high priority at the moment
                                        // since Open and Save are accessible from the menu.
                                        //PopUp.show(me.getComponent(), me.getX(), me.getY());
					//PopUpOpen = true;
                                    
                                        //Populate the Java bean
                                                ClickBean click = new ClickBean();
                                                click.SetX(me.getX());
                                                click.SetY(me.getY());

                                        //Dispatch the event
                                                RightClickHandlers.stream().forEach((l) -> {
                                                    l.ClickHandler(click);
                                                });
				}
			}

			@Override
			public void mousePressed(MouseEvent me) { }

			@Override
			public void mouseReleased(MouseEvent me) { }

			@Override
			public void mouseEntered(MouseEvent me) { }

			@Override
			public void mouseExited(MouseEvent me) { }
		});
                
        //Listen for mouse move events within the JPanel
                Canvas.addMouseMotionListener(new MouseMotionListener() {

                    @Override
                    public void mouseDragged(MouseEvent e) { }

                    @Override
                    public void mouseMoved(MouseEvent e) {
                        //Populate the Java bean
                        MouseMoveBean move = new MouseMoveBean();
                        move.SetX(e.getX());
                        move.SetY(e.getY());
                        
                        //Dispatch the event
                        MouseMoveHandlers.stream().forEach((l) -> {
                            l.MouseMoveHandler(move);
                        });
                    }
                    
                });
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		File file;
		
		if(e.getSource() == New) {
			FileHandlers.stream().forEach((l) -> {
				l.NewHandler();
			});
		} else if (e.getSource() == Open || e.getSource() == OpenPop) {
			file = OpenFileBrowser(BrowserType.OPEN);
			
			if(file != null) {
			//Populate the Java bean
				FileBean event = new FileBean();
				event.SetDirectory(file.getParent());
				event.SetFileName(file.getAbsoluteFile().getName());
				event.SetFilePath(file.getAbsolutePath());
				event.SetOperation(OperationType.OPEN);
				
			//Dispatch the event
				FileHandlers.stream().forEach((l) -> {
					l.OpenHandler(event);
				});
			}
		} else if (e.getSource() == Save || e.getSource() == SavePop || e.getSource() == SaveAs) {
			file = OpenFileBrowser(BrowserType.SAVE);
			
			if(file != null) {
			//Populate the Java bean
				FileBean event = new FileBean();
				event.SetDirectory(file.getParent());
				event.SetFileName(file.getAbsoluteFile().getName());
				event.SetFilePath(file.getAbsolutePath());
				event.SetOperation((e.getSource() == Save || e.getSource() == SavePop) ? OperationType.SAVE : OperationType.SAVE_AS);
				
			//Dispatch the event
				FileHandlers.stream().forEach((l) -> {
					if (e.getSource() == Save || e.getSource() == SavePop) {
						l.SaveHandler(event);
					} else {
						l.SaveAsHandler(event);
					}
				});
			}
		} else if (e.getSource() == Exit) {
			dispose();
		}
	}
	
	public Canvas GetCanvas() {
		return Canvas;
	}
	
	private File OpenFileBrowser(BrowserType type) {
		FileBrowser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		FileBrowser.addChoosableFileFilter(Filter);
		FileBrowser.setAcceptAllFileFilterUsed(false);
		FileBrowser.setFileFilter(Filter);
		
		if (type == BrowserType.OPEN) {
			if(FileBrowser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
				return null;
			}
		} else {
			if(FileBrowser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
				return null;
			}
		}
		
		return FileBrowser.getSelectedFile();
	}
	
	public void RegisterClickListener(IMouseListener handler) {
		ClickHandlers.add(handler);
	}
        
        public void RegisterRightClickListener(IMouseListener handler) {
                RightClickHandlers.add(handler);
        }
        
        public void RegisterMouseMoveListener(IMouseMoveListener handler) {
                MouseMoveHandlers.add(handler);
        }
	
	public void RegisterFileListener(IFileListener handler) {
		FileHandlers.add(handler);
	}
	
	public void SetExtension(String extension) {
		Extension = extension;
		Filter = new FileNameExtensionFilter(String.format("BigOven Graph Files (.%s)", extension), extension);
	}
	
	private void SetupMenu() {
		MenuBar = new JMenuBar();
		PopUp = new JPopupMenu();
		
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
		
	//Add items to the context menu
		OpenPop = PopUp.add("Open");
		SavePop = PopUp.add("Save");
		
		OpenPop.setAccelerator(KeyStroke.getKeyStroke('O', InputEvent.CTRL_DOWN_MASK));
		SavePop.setAccelerator(KeyStroke.getKeyStroke('S', InputEvent.CTRL_DOWN_MASK));
		
		OpenPop.addActionListener(this);
		SavePop.addActionListener(this);
	}
}