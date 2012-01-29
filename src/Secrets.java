// Copyright 2003 Gordon Willem Klok, All rights reserved.
// 
// Redistribution and use in source and binary forms, with or without modification,
// are permitted provided that the following conditions are met:
// 
//    1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
// 
//    2. Redistributions in binary form must reproduce the above copyright notice,
// this list of conditions and the following disclaimer in the documentation and/or
// other materials provided with the distribution.
// 
// THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES,
// INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
// FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT
// HOLDER> OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
// SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
// PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
// OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
// WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
// OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
// ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
import java.util.Properties;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;
import javax.swing.table.TableModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.DefaultTableCellRenderer;
import java.util.Vector;
import BlowfishJ.*;

public class Secrets extends JPanel
	implements ActionListener, TableModelListener {
	private JFileChooser fc;
	Properties appProps;
	private static JFrame frame;
	private JScrollPane scrollPane;
	private byte[] nkey;
	private String pwdbName;
	private Vector keys;
	private boolean isSaved = false;
	private JTable table;
	private int selectedRow = -1;
	private SecretsTableModel tblModel;

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

	public byte[] sha1_key(String passwd) {
		int nI, nC;
		SHA1 sh = null;
		byte[] hash;

		sh = new SHA1();
		for(nI = 0, nC = passwd.length(); nI < nC; nI++) {
			sh.update((byte) (passwd.charAt(nI) & 0x0ff));
		}
		sh.finalize();
		hash = new byte[SHA1.DIGEST_SIZE];
		sh.getDigest(hash, 0);
		return hash;
	}
	public void actionPerformed(ActionEvent e) {
		JMenuItem source = (JMenuItem)(e.getSource());
		if(source.getText() == "Open") {
			String openName = selectDatabase();
			if(openName != null) {
			}
		}
		if(source.getText() == "Close") {
		}
		if(source.getText() == "Save") {
			if(!isSaved) {
				if(SecretsWriteFile(pwdbName, nkey, SHA1.DIGEST_SIZE)) {
					isSaved = true;
				}
			}
		}
		if(source.getText() == "Save As") {
		String fileName = selectDatabase();
			if(fileName != null) {
				SecretsWriteFile(fileName, nkey, SHA1.DIGEST_SIZE);
			}
		}
		if (source.getText() == "Quit") {
			if(isSaved) {
				System.exit(0);
			}
		}
		if (source.getText() == "Change Password") {
			String password;
			if((password = PasswordDialog.showPasswordDialog(frame)) != null) {
				nkey = sha1_key(password);
				isSaved = false;
			}
		}
		if (source.getText() == "New Password") {
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                        PasswordGenerator pg = new PasswordGenerator();
                                }
                        });
		}
		if (source.getText() == "Add") {
			SecretsKey k;
			if((k = KeyDialog.showNewKeyDialog(frame)) != null) {
				tblModel.addRow(k);
				isSaved = false;
			}
		}
		if(source.getText() == "Delete") {
			if(selectedRow != -1) {
				tblModel.deleteRow(selectedRow);
				selectedRow = -1;
			}
		}
	}
	public JMenuBar createMenuBar() {
		JMenuBar menuBar;
		JMenu menu;
		JMenuItem menuItem;

		menuBar = new JMenuBar();

		menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_F);
		menuBar.add(menu);

		menuItem = new JMenuItem("Open", KeyEvent.VK_O);
		menuItem.addActionListener(this);
		menu.add(menuItem);
		menuItem.addActionListener(this);
		menuItem = new JMenuItem("Close", KeyEvent.VK_C);
		menuItem.addActionListener(this);
		menu.add(menuItem);
		menuItem = new JMenuItem("Save", KeyEvent.VK_S);
		menuItem.addActionListener(this);
		menu.add(menuItem);
		menuItem = new JMenuItem("Save As");
		menuItem.addActionListener(this);
		menu.add(menuItem);
		menuItem = new JMenuItem("Change Password");
		menuItem.addActionListener(this);
		menu.add(menuItem);
		menuItem = new JMenuItem("Quit");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menu = new JMenu("Edit");
		menu.setMnemonic(KeyEvent.VK_E);
		menuBar.add(menu);

		menuItem = new JMenuItem("Delete");
		menuItem.addActionListener(this);
		menu.add(menuItem);
		menuItem = new JMenuItem("Copy");
		menuItem.addActionListener(this);
		menu.add(menuItem);
		menuItem = new JMenuItem("Add");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menu = new JMenu("Utillities");
		menu.setMnemonic(KeyEvent.VK_U);
		menuBar.add(menu);
		menuItem = new JMenuItem("New Password");
		menuItem.addActionListener(this);
		menu.add(menuItem);
		return menuBar;
	}
	private static void createAndShowGUI() {
		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);
		frame = new JFrame("Secrets");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Secrets smain = new Secrets();
		frame.setJMenuBar(smain.createMenuBar());
		JComponent newContentPane = smain;
		newContentPane.setOpaque(true);
		frame.setContentPane(newContentPane);
		frame.setSize(600, 400);
		frame.pack();
		frame.setVisible(true);
	}
	public Secrets() {
		Properties sysProps = System.getProperties();
		String homedir = sysProps.getProperty("user.home");

		String fileSeperator = sysProps.getProperty("file.separator");
		StringBuffer appPropertiesName = new StringBuffer(homedir);
		appPropertiesName.append(fileSeperator);
		appPropertiesName.append(".secrets");
		appPropertiesName.append(fileSeperator);
		appPropertiesName.append("secretsProperties");
		FileInputStream in;
		FileOutputStream out;
		Properties appProps = new Properties();

		try {
			in = new FileInputStream(appPropertiesName.toString());
			appProps.load(in);
			in.close();
		}
		catch(FileNotFoundException NFex) {
			//create new properties
			try {
				// make a new .secrets directory
				StringBuffer appPropertiesDirName = new StringBuffer(homedir);
				appPropertiesDirName.append(fileSeperator);
				appPropertiesDirName.append(".secrets");
				try {
					(new File(appPropertiesDirName.toString())).mkdir();
				} catch (Exception ex) {
					System.err.println("Error: " + ex.getMessage());
				}

				out = new FileOutputStream(appPropertiesName.toString());
				//default file
				StringBuffer dPasswdDBFileName = new StringBuffer(appPropertiesDirName);
				dPasswdDBFileName.append(fileSeperator);
				dPasswdDBFileName.append("secrets.db");
				appProps.put("default_passwordDBfile", dPasswdDBFileName.toString());
				appProps.store(out, "---Secrets Application Properties ---");
			} catch(IOException ioex) {
				System.exit(-1);
			}
		}
		catch(IOException ioex) {
			System.exit(-1);
		}

		if((pwdbName = appProps.getProperty("default_passwordDBfile")) == null) {
			String newPDBname = selectDatabase();
			if (newPDBname != null) {
				try {
					out = new FileOutputStream(appPropertiesName.toString());
					appProps.put("default_passwordDBfile", newPDBname);
					appProps.store(out, "---Secrets Application Properties ---");
				} catch(IOException ioex) {
					System.exit(-1);
				}
			}
			else {
				System.exit(-1);
			}
		}

		//prompt for password
		String password;
		if((password = PasswordDialog.showPasswordDialog(frame)) != null) {
			nkey = sha1_key(password);
			File pwdb = new File(pwdbName);
			keys = new Vector <Object> ();
			if (!pwdb.exists()) {
				try {
					pwdb.createNewFile();
				} catch(IOException ex) {
					System.exit(-1);
				}
			}
			else {
				if(!SecretsReadFile(pwdbName, nkey, SHA1.DIGEST_SIZE)) {
					//add dialog
					//System.exit(-1);
				} else {
					isSaved = true;
				}
			}
			tblModel = new SecretsTableModel(keys);
			table = new JTable(tblModel);
			table.getModel().addTableModelListener(this);
			table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			ListSelectionModel rowSM = table.getSelectionModel();
			rowSM.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
					if (e.getValueIsAdjusting()) return;
					ListSelectionModel lsm = (ListSelectionModel)e.getSource();
					if(lsm.isSelectionEmpty()) {
					} else {
						selectedRow = lsm.getMinSelectionIndex();
        				}
    				}
			});
			TableColumn passwordColumn = table.getColumnModel().getColumn(2);
			//JPasswordField passwordField = new JPasswordField();
			//passwordField.setEchoChar('\u2022');
			//passwordColumn.setCellEditor(new DefaultCellEditor(passwordField));
			//passwordColumn.setCellRenderer(new PasswordRenderer());	
			table.setPreferredScrollableViewportSize( new Dimension(500, 700) );
			scrollPane = new JScrollPane(table);

			add(scrollPane, BorderLayout.CENTER);

		} else {
			System.exit(-1);
		}
	}
	public void tableChanged(TableModelEvent e) {
		isSaved = false;
	}
	private static class PasswordRenderer extends DefaultTableCellRenderer {
		public void setValue(Object value) {
			StringBuffer s = new StringBuffer();
			String t = new String((String)value);
			for(int i = 0; i < t.length(); i++) {
				s.append('\u2022');
			}
			this.setToolTipText(t);
			setText((value == null) ? "" : s.toString());
		}
	}
	public boolean SecretsReadFile(String filename, byte key[], int key_size) {
		ObjectInputStream ois;
		BlowfishInputStream bfis;
		try {
			try {
				bfis = new BlowfishInputStream(key, 0, key_size, new DataInputStream(new FileInputStream(filename)));
			}
			catch(FileNotFoundException ex) {
				return false;
			}
			ois = new ObjectInputStream(bfis);
			keys = (Vector)ois.readObject();
			ois.close();
		}
		catch(ClassNotFoundException ex) {
			return false;
		}
		catch(IOException ex) {
			System.out.println(ex.toString());
			return false;
		}
		return true;
	}
	public boolean SecretsWriteFile(String filename, byte key[], int key_size) {
		ObjectOutputStream ous;
		BlowfishOutputStream bfos;
		try {
			try {
				bfos = new BlowfishOutputStream(key, 0, key_size, new DataOutputStream(new FileOutputStream(filename)));
			} catch(FileNotFoundException ex) {
				return false;
			}
			ous = new ObjectOutputStream(bfos);
			ous.writeObject(keys);
			ous.flush();
			ous.close();
		}
		catch(IOException ex) {
			return false;
		}
		return true;
	}
	class SecretsTableModel extends AbstractTableModel {
		String[] columnNames = {"Name",
					"Description",
					"Secret"
					};
		Vector <Object> data = null;
		public SecretsTableModel(Vector<Object> keys) {
			data = keys;
		}

		public int getColumnCount() {
			return columnNames.length;
		}

		public int getRowCount() {
			return data.size();
		}

		public String getColumnName(int col) {
			return columnNames[col];
		}

		public Object getValueAt(int row, int col) {
			try {
				SecretsKey k = (SecretsKey)data.elementAt(row);
				switch(col) {
					case 0:
					return new String(k.key_name);
					case 1:
					return new String(k.description);
					case 2:
					return new String(k.key_value);
				}
			} catch(Exception e) {
			}
			return "";
		}

		public Class getColumnClass(int c) {
			return getValueAt(0, c).getClass();
		}

		public boolean isCellEditable(int row, int col) {
			return true;
		}

		public void setValueAt(Object value, int row, int col) {
			try {
			    SecretsKey k = (SecretsKey)data.elementAt(row);
			    switch(col) {
				case 0:
					k.key_name = (String)value;
				case 1:
					k.description = (String)value;
				case 2:
					k.key_value = (String)value;
				}
			} catch(Exception e) {
			}
			fireTableCellUpdated(row, col);
		}
		public void deleteRow(int row) {
				data.remove(row);
				fireTableRowsDeleted(row, row);
		}
		public void addRow(Object value) {
			data.add(value);
			int nRows = data.size() - 1;
			fireTableRowsInserted(nRows, nRows);
		}
	}
	private String selectDatabase() {
		fc = new JFileChooser();
		int retVal = fc.showOpenDialog(Secrets.this);
		if(retVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			return file.getAbsolutePath();
		} else {
			return null;
		}
	}
}
