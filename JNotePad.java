/*
 * 
 * Name:		Sepulveda, Baldemar
 * Project: 	#3
 * Due: 		March 12, 2018
 * Course: 		CS-245-01-w18
 * 
 * Description:	
 * 		A notepad application designed to mimic the design of Microsoft's
 * 		notepad. But programmed in java using swing. 
 * 
 * 
 * */



import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.text.Highlighter.HighlightPainter;
import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;

class JNotePadFileFilter extends FileFilter{
	public boolean accept(File file){
		if(file.getName().endsWith(".txt")){return true;}
		if(file.getName().endsWith(".java")){return true;}
		if(file.isDirectory()){return true;}
		
		return false;
	}
	public String getDescription(){
		return "Text Files and Java Source Files";
	}
}

public class JNotePad extends JFrame implements ActionListener{
	
	private JDialog aboutDialog;
	
	// Flag to check if document has changed
	private static boolean changed;
	
	private boolean highlightByFind;
	
	// Text field
	private JTextArea pad;
	
	// File Choosers for opening and closing files
	private JFileChooser saveFileChooser;
	private JFileChooser openFileChooser;
	
	// Used to Highlight Text For User
	private Highlighter highlighter;
	private HighlightPainter painter;
	
	private File currentWorkingFile;
	
	// Find Dialog 
	JDialog findDialog; 
	
	// Font Chooser Dialog
	JFontChooser fontChooser;
	
	// ScrollPane For NotePad
	JScrollPane scrollPane;
	
	JNotePad(){
		// Is the pad highlighted by find or by normal use.
		highlightByFind = false;
		
		// Dialog for About
		aboutDialog = null;
		
		// Font Chooser Dialog
		fontChooser = null; 
		
		// Has the document been changed?
		changed = false;
		
		// Initialize Find Dialog
		findDialog = null;
		
		JPopupMenu popup;
		
		// Initialize JFileChooser Objects
		saveFileChooser = new JFileChooser(System.getProperty("user.dir"));
		saveFileChooser.setFileFilter(new JNotePadFileFilter());
		openFileChooser = new JFileChooser(System.getProperty("user.dir"));
		openFileChooser.setFileFilter(new JNotePadFileFilter());
		
		// Initializes the JFrame
		setTitle("JNotePad");
		setSize(500, 500);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setLayout(new GridLayout(1, 1));
		
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				if(changed){
					int result;
					if(currentWorkingFile == null){
						result = JOptionPane.showConfirmDialog(null, "Do you want to save DOCUMENT", 
														"JNotepad",
														JOptionPane.YES_NO_CANCEL_OPTION);
					}else{
						result = JOptionPane.showConfirmDialog(null, "Do you want to save " + currentWorkingFile.getName(), 
														"JNotepad",
														JOptionPane.YES_NO_CANCEL_OPTION);
					}
					switch(result){
						case JOptionPane.YES_OPTION:
							saveDocument();
							System.exit(0);
							break;
						case JOptionPane.NO_OPTION:
							System.exit(0);
							break;
						default:
							break;
					}
							
				}else{
					System.exit(0);
				}
				
			}
		});
		
		// Initialize currentWorkingFile to null
		currentWorkingFile = null;
		
		// Creates The Menu Bar and sets the JMenuBar of the JFrame
		JMenuBar menuBar = new JMenuBar();
		JMenu[] menus = createMenus();
		addMenusToMenuBar(menuBar, menus);
		setJMenuBar(menuBar);
		
		// Default Font Style and size
		Font defaultFont = new Font("Courier", Font.PLAIN, 12);
		
		// This is the area that you type text into
		pad = new JTextArea();
		pad.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e){
				changed = true;
			}
			public void removeUpdate(DocumentEvent e){
				changed = true;
			}
			public void changedUpdate(DocumentEvent e){
				changed = true;
			}
		});
		pad.setFont(defaultFont);
		
		pad.addCaretListener(new CaretListener(){
			public void caretUpdate(CaretEvent e){
				if(highlightByFind){
					highlighter.removeAllHighlights();
				}
			}
		});
		
		pad.setWrapStyleWord(true);
		
		
		popup = new JPopupMenu();
		Action cut = new DefaultEditorKit.CutAction();
		cut.putValue(Action.NAME, "cut");
		Action copy = new DefaultEditorKit.CopyAction();
		copy.putValue(Action.NAME, "copy");
		Action paste = new DefaultEditorKit.PasteAction();
		paste.putValue(Action.NAME, "paste");
		popup.add(cut);
		popup.add(copy);
		popup.add(paste);
		pad.setComponentPopupMenu(popup);
		
		// Scroll pane will hold the pad
		scrollPane = new JScrollPane(pad);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		add(scrollPane);
		
		highlighter = pad.getHighlighter();
		painter = new DefaultHighlighter.DefaultHighlightPainter(Color.yellow);
		
		
		// Centers the window
		setLocationRelativeTo(null);
		
		// Sets the frame visible
		setVisible(true);
	
	}
	
	public JMenu[] createMenus(){
		JMenu file = new JMenu("File");
		
		JMenuItem[] fileItems = createFileMenuItems();
		
		addMenuItemsToFileMenu(file, fileItems);
		
		JMenu edit = new JMenu("Edit");
		
		JMenuItem[] editItems = createEditMenuItems();
		
		addMenuItemsToEditMenu(edit, editItems);
		
		JMenu format = new JMenu("Format");
		
		JMenuItem wordWrap = new JMenuItem("Word Wrap");
		wordWrap.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(pad.getLineWrap()){
					pad.setLineWrap(false);
					scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
				}else{
					pad.setLineWrap(true);
					scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
				}
			}
		});
		
		format.add(wordWrap);
		
		JMenuItem font = new JMenuItem("Font...");
		font.addActionListener(this);
		format.add(font);
		
		JMenu help = new JMenu("Help");
		
		JMenuItem viewHelp = new JMenuItem("View Help");
		JMenuItem about = new JMenuItem("About JNotePad");
		about.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				showAboutDialog();
			}
		});
		
		help.add(viewHelp);
		help.addSeparator();
		help.add(about);
		
		
		JMenu[] menus = {file, edit, format, help};
		
		return menus;
		
	}
	
	public void showAboutDialog(){
		if(aboutDialog == null){
			aboutDialog = new JDialog(this, "About JNotepad");
			aboutDialog.setLayout(new FlowLayout());
			aboutDialog.setSize(300, 100);
			
			JLabel aboutLabel = new JLabel("(c) Baldemar Sepulveda");
			
			aboutDialog.add(aboutLabel);
			
			
			aboutDialog.setVisible(true);
			
			
		}else{
			aboutDialog.setVisible(true);
		}
	}
	
	public void actionPerformed(ActionEvent e){
		if(fontChooser == null){
			fontChooser = new JFontChooser();
			fontChooser.setDefault(pad.getFont());
			fontChooser.setDefault(Color.black);
			
			fontChooser.showDialog(this);
		}else{
			if(fontChooser.showDialog(this)){
				pad.setFont(fontChooser.getFont());
				pad.setForeground(fontChooser.getColor());
			}
		
		}
	}
	
	public JMenuItem[] createEditMenuItems(){
		JMenuItem undo = new JMenuItem("Undo");
		undo.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK));
		
		JMenuItem cut = new JMenuItem("Cut");
		cut.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
		cut.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				pad.cut();
			}
		});
		
		JMenuItem copy = new JMenuItem("Copy");
		copy.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));
		copy.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				pad.copy();
			}
		});
		
		JMenuItem paste = new JMenuItem("Paste");
		paste.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK));
		paste.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				pad.paste();
			}
		});
		
		JMenuItem delete = new JMenuItem("Delete");
		delete.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_MASK));
		delete.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(pad.getSelectedText() != null){
					pad.cut();
				}
			}
		});
			
		JMenuItem find = new JMenuItem("Find...");
		find.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_MASK));
		find.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				openFindDialog();
			}
		});
			
		JMenuItem findNext = new JMenuItem("Find Next");
		
		JMenuItem replace = new JMenuItem("Replace...");
		replace.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.CTRL_MASK));
		
		JMenuItem goTo = new JMenuItem("Go To...");
		goTo.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_MASK));
		
		JMenuItem selectAll = new JMenuItem("Select All");
		
		JMenuItem timeDate = new JMenuItem("Time/Date");
		timeDate.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				Calendar c = Calendar.getInstance();
				
				String am_pm = "";
				if(Calendar.AM_PM == Calendar.AM){
					am_pm += "AM";
				}else{
					am_pm += "PM";
				}
				

				
				
				String timeDate = "" + c.get(Calendar.HOUR) + ":" + c.get(Calendar.MINUTE)
									+ " " + am_pm + " " + (c.get(Calendar.MONTH) + 1) + "/"
									+ c.get(Calendar.DAY_OF_MONTH) + "/" + c.get(Calendar.YEAR);
				pad.insert(timeDate, pad.getCaretPosition());
			}
			
		});
		
		JMenuItem[] items = {undo, cut, copy, paste, delete, find, findNext, 
							replace, goTo, selectAll, timeDate};
		selectAll.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				pad.selectAll();
			}
		});
							
		return items;
		
	}
	
	
	
	public void openFindDialog(){
		if(findDialog == null){
			findDialog = new JDialog(this, "Find", false);
			findDialog.setSize(500, 150);
			findDialog.setLayout(new FlowLayout());
			
			JPanel findPanel = new JPanel(new FlowLayout());
			findPanel.setPreferredSize(new Dimension(380, 50));
			JLabel findLabel = new JLabel("Find What:");
			JTextField enter = new JTextField(20);
			findPanel.add(findLabel);
			findPanel.add(enter);
			
			JPanel findButtons = new JPanel(new FlowLayout());
			findButtons.setPreferredSize(new Dimension(100, 60));
			JButton next = new JButton("Find Next");
			next.setPreferredSize(new Dimension(100, 25));
			JButton cancel = new JButton("Cancel");
			cancel.setPreferredSize(new Dimension(100, 25));
			findButtons.add(next);
			findButtons.add(cancel);
			
			JPanel directionPanel = new JPanel(new FlowLayout());
			directionPanel.setPreferredSize(new Dimension(150, 50));
			directionPanel.setBorder(BorderFactory.createEtchedBorder());
			JLabel direction = new JLabel("Direction");
			direction.setPreferredSize(new Dimension(150, 10));
			direction.setHorizontalAlignment(SwingConstants.CENTER);
			JRadioButton up = new JRadioButton("Up");
			JRadioButton down = new JRadioButton("Down");
			ButtonGroup bg = new ButtonGroup();
			bg.add(up);
			bg.add(down);
			down.setSelected(true);
			directionPanel.add(direction);
			directionPanel.add(up);
			directionPanel.add(down);
			
			JCheckBox matchCase = new JCheckBox("Match Case");
			
			next.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					String findThis = enter.getText();
					selectFoundText(findThis, up.isSelected(), down.isSelected(), matchCase.isSelected());
				}
			});
			
			enter.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					String findThis = enter.getText();
					
					selectFoundText(findThis, up.isSelected(), down.isSelected(), matchCase.isSelected());
					
				}
			});
			
			cancel.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					findDialog.setVisible(false);
				}
			});
			
			findDialog.add(findPanel);
			findDialog.add(findButtons);
			findDialog.add(matchCase);
			findDialog.add(directionPanel);
			findDialog.setVisible(true);
		}
		else{
			findDialog.setVisible(true);
		}
	}
	
	public void selectFoundText(String find, boolean up, boolean down, boolean match){
		String padText;
		
		if(match){
			padText = pad.getText();
		}else{
			padText = pad.getText().toLowerCase();
			find = find.toLowerCase();
		}
		
		if(down){
			int startInd = padText.indexOf(find, pad.getCaretPosition());
			if(startInd > -1){
				try{
					highlighter.removeAllHighlights();
					int endInd = startInd + find.length();
					highlighter.addHighlight(startInd, endInd, painter);
					pad.select(startInd, endInd);
					
					highlightByFind = true;
				}catch(BadLocationException e){
					System.out.println(e);
				}
			}else{
				JOptionPane.showMessageDialog(this,"Cannot find" + " \"" + find + "\"", "Error", JOptionPane.ERROR_MESSAGE);
			}
			
		}else if(up){
			int startInd = padText.lastIndexOf(find, pad.getCaretPosition() - 1);
			if(startInd > -1){
				try{
					highlighter.removeAllHighlights();
					int endInd = startInd + find.length();
					
					highlighter.addHighlight(startInd, endInd, painter);
					
					pad.setCaretPosition(endInd);
					pad.moveCaretPosition(startInd);
					
					highlightByFind = true;
					
				}catch(BadLocationException e){
					System.out.println(e);
				}
			}else{
				JOptionPane.showMessageDialog(this,"Cannot find" + " \"" + find + "\"", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	public void resetPad(){
		pad.setText("");
		currentWorkingFile = null;
		changed = false;	
	}
	public JMenuItem[] createFileMenuItems(){
		// NEWITEM Menu Item
		JMenuItem newItem = new JMenuItem("New");
		newItem.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
		newItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae){
				if(changed == true){
					int userChoice = askUserToSaveDoc();
					if(userChoice == JOptionPane.YES_OPTION){
						saveAsDocument();
						resetPad();
					}else if(userChoice == JOptionPane.NO_OPTION){
						resetPad();
					}
				}else{
					resetPad();
				}
			}
		});
		
		// OPEN Menu Item
		JMenuItem open = new JMenuItem("Open...");
		open.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
		open.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae){
				File file = openDocument();
				loadDataToPad(file);
			}
		});
		// SAVE Menu Item
		JMenuItem save = new JMenuItem("Save");
		save.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae){
				saveDocument();
			}
		});
			
		// SAVEAS Menu Item
		JMenuItem saveAs = new JMenuItem("Save As...");
		saveAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae){
				saveAsDocument();
			}
		});
		
		// PAGESETUP Menu Item
		JMenuItem pageSetUp = new JMenuItem("Page Setup...");
		
		// PRINT Menu Item
		JMenuItem print = new JMenuItem("Print...");
		print.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_MASK));
		
		// EXIT Menu Item
		JMenuItem exit = new JMenuItem("Exit");
		exit.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_MASK));
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae){
				if(changed){
					int result;
					if(currentWorkingFile == null){
						result = JOptionPane.showConfirmDialog(null, "Do you want to save DOCUMENT", 
														"JNotepad",
														JOptionPane.YES_NO_CANCEL_OPTION);
					}else{
						result = JOptionPane.showConfirmDialog(null, "Do you want to save " + currentWorkingFile.getName(), 
														"JNotepad",
														JOptionPane.YES_NO_CANCEL_OPTION);
					}
					switch(result){
						case JOptionPane.YES_OPTION:
							saveDocument();
							System.exit(0);
							break;
						case JOptionPane.NO_OPTION:
							System.exit(0);
							break;
						default:
							break;
					}
							
				}else{
					System.exit(0);
				}
			}
		});
		
		// All Menu Items Specific to File Menu
		JMenuItem[] menuItems = {newItem, 
								open, 
								save, 
								saveAs, 
								pageSetUp, 
								print, 
								exit};
		
		// Return all Menu Items Specific to File Menu
		return menuItems;
		
	}
	
	public File openDocument(){
		int result = openFileChooser.showOpenDialog(this);
		
		if(result == JFileChooser.APPROVE_OPTION){
			currentWorkingFile = openFileChooser.getSelectedFile();
			return currentWorkingFile;
		}else{
			return null;
		}
	}
	
	public void loadDataToPad(File file){
		try{
			FileReader fr = new FileReader(file.getPath());
			pad.read(fr, null);
			fr.close();
			
		}catch(Exception e){
			System.out.println(e);
		}
	}
	
	public void saveDocument(){
		if (currentWorkingFile == null){
			int result = saveFileChooser.showSaveDialog(this);
			
			if(result == JFileChooser.APPROVE_OPTION){
				currentWorkingFile = saveFileChooser.getSelectedFile();
					
				if(checkIfFileAlreadyExists(currentWorkingFile) == true){
					if(askUserToOverwriteFile() == true){
						writeFile(currentWorkingFile);
					}else{
						currentWorkingFile = null;
					}
				}else{
					writeFile(currentWorkingFile);
				}
			}else if(result == JFileChooser.ERROR_OPTION){
				JOptionPane.showMessageDialog(this, "Document Could Not be Saved", "Error",  JOptionPane.ERROR_MESSAGE);
			}
		}else{
			writeFile(currentWorkingFile);
		}
		
		
	}
	
	public void saveAsDocument(){
		int result = saveFileChooser.showSaveDialog(this);
			
		if(result == JFileChooser.APPROVE_OPTION){
			currentWorkingFile = saveFileChooser.getSelectedFile();
				
			if(checkIfFileAlreadyExists(currentWorkingFile) == true){
				if(askUserToOverwriteFile() == true){
					writeFile(currentWorkingFile);
				}else{
					currentWorkingFile = null;
				}
			}else{
				writeFile(currentWorkingFile);
			}
		}else if(result == JFileChooser.ERROR_OPTION){
			JOptionPane.showMessageDialog(this, "Document Could Not be Saved", "Error",  JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public boolean checkIfFileAlreadyExists(File file){
		return file.exists();
	}
	
	public boolean askUserToOverwriteFile(){
		int result = JOptionPane.showConfirmDialog(saveFileChooser, "File Already Exists!", 
				"Overwrite File?", JOptionPane.YES_NO_OPTION);
		if(result == JOptionPane.YES_OPTION){
			return true;
		}
		
		return false;
	}
	
	public void writeFile(File file){
		try{
			FileWriter writer = new FileWriter(file);
			writer.write(pad.getText());
			writer.close();
			changed = false;
		}catch(IOException e){
			System.out.println(e);
		}
	
	}
	
	
	public void addMenuItemsToFileMenu(JMenu menu, JMenuItem[] items){
		for(int i = 0; i < items.length; i++){
			if(i == 4 || i == 6){
				menu.addSeparator();
			}
			
			menu.add(items[i]);
		}
	}
	
	public void addMenuItemsToEditMenu(JMenu menu, JMenuItem[] items){
		for(int i = 0; i < items.length; i++){
			if(i == 1 || i == 5 || i == 9){
				menu.addSeparator();
			}
			menu.add(items[i]);
		}
	}
	
	public void addMenusToMenuBar(JMenuBar menuBar, JMenu[] menus){
		for(int i = 0; i < menus.length; i++){
			menuBar.add(menus[i]);
		}
	}
	
	public int askUserToSaveDoc(){
		int result = JOptionPane.showConfirmDialog(this,
									"Do you want to save changes to this file?",
									this.getTitle(),
									JOptionPane.YES_NO_CANCEL_OPTION,
									JOptionPane.QUESTION_MESSAGE);
									
		return result;
	}

	public static void main(String[] args){
		SwingUtilities.invokeLater(new Runnable() {
			public void run(){
				new JNotePad();
			}
			
		});
		
	}

}
