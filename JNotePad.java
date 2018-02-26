import javax.swing.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;

public class JNotePad extends JFrame{

	// Text field
	static JTextArea pad;
	
	static final JFileChooser saveFileChooser = new JFileChooser(System.getProperty("user.dir"));
	static final JFileChooser openFileChooser = new JFileChooser(System.getProperty("user.dir"));
	
	
	static File currentWorkingFile;
	
	JNotePad(){
		// Initializes the JFrame
		setTitle("JNotePad");
		setSize(500, 500);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new GridLayout(1, 1));
		
		// Initialize currentWorkingFile to null
		currentWorkingFile = null;
		
		JMenuBar menuBar = new JMenuBar();
		
		JMenu[] menus = createMenus();
		
		addMenusToMenuBar(menuBar, menus);
		
		pad = new JTextArea();
		
		JScrollPane scrollPane = new JScrollPane(pad);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		add(scrollPane);
		setJMenuBar(menuBar);
		
		setLocationRelativeTo(null);
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
		
		JMenuItem font = new JMenuItem("Font...");
		format.add(font);
		
		JMenu help = new JMenu("Help");
		
		JMenuItem viewHelp = new JMenuItem("View Help");
		JMenuItem about = new JMenuItem("About JNotePad");
		
		help.add(viewHelp);
		help.addSeparator();
		help.add(about);
		
		
		JMenu[] menus = {file, edit, format, help};
		
		return menus;
		
	}
	
	public JMenuItem[] createEditMenuItems(){
		JMenuItem undo = new JMenuItem("Undo");
		undo.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK));
		
		JMenuItem cut = new JMenuItem("Cut");
		cut.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
		
		JMenuItem copy = new JMenuItem("Copy");
		copy.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));
		
		JMenuItem paste = new JMenuItem("Paste");
		paste.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK));
		
		JMenuItem delete = new JMenuItem("Delete");
			
		JMenuItem find = new JMenuItem("Find...");
		find.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_MASK));
			
		JMenuItem findNext = new JMenuItem("Find Next");
		JMenuItem replace = new JMenuItem("Replace...");
		replace.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.CTRL_MASK));
		
		JMenuItem goTo = new JMenuItem("Go To...");
		goTo.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_MASK));
		
		JMenuItem selectAll = new JMenuItem("Select All");
		
		JMenuItem[] items = {undo, cut, copy, paste, delete, find, findNext, 
							replace, goTo, selectAll};
							
		return items;
		
	}
	
	public JMenuItem[] createFileMenuItems(){
		// NEWITEM Menu Item
		JMenuItem newItem = new JMenuItem("New");
		newItem.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
		
		// OPEN Menu Item
		JMenuItem open = new JMenuItem("Open...");
		open.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
		
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
		
		// EXIT Menu Item
		JMenuItem exit = new JMenuItem("Exit");
		exit.setAccelerator(
			KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_MASK));
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae){
				System.exit(0);
			}
		});
		
		// All Menu Items Specific to File Menu
		JMenuItem[] menuItems = {newItem, open, save, saveAs, pageSetUp, exit};
		
		// Return all Menu Items Specific to File Menu
		return menuItems;
		
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
		}catch(IOException e){
			System.out.println(e);
		}
	
	}
	
	public void addMenuItemsToFileMenu(JMenu menu, JMenuItem[] items){
		for(int i = 0; i < items.length; i++){
			if(i == 4 || i == 5){
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

	public static void main(String[] args){
		SwingUtilities.invokeLater(new Runnable() {
			public void run(){
				new JNotePad();
			}
			
		});
		
	}

}
