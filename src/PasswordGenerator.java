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
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.text.*;
import java.util.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.lang.reflect.*;

public class PasswordGenerator extends JPanel
	implements PropertyChangeListener, ActionListener {
	private int min_length = 8;
	private int max_length = 8;

	private JLabel minlengthLabel;
	private JLabel maxlengthLabel;
	private JLabel passwordLabel;
	private JLabel patternLabel;
	private JLabel minalphaLabel;
	private JLabel minnumericLabel;
	private static String min_length_string = "Minimum Length: ";
	private static String max_length_string = "Maximum Length: ";
	private static String password_result = "New Password: ";
	private static String pattern_string = "Allowed Characters: ";
	private static String min_alpha_string = "Minimum Alpha-numeric characters: ";
	private static String min_numeric_string = "Minimum Numeric characters: ";

	private JFormattedTextField min_length_field;
	private JFormattedTextField max_length_field;
	private JTextField resulting_password;
	protected JSpinner min_alpha, min_numeric;
	private NumberFormat MinAmountFormat;
	private NumberFormat MaxAmountFormat;

	String currentPattern;
	boolean includeSets[];

	java.util.List uppercase, lowercase, numerics, ud;
	public void createAndShowGUI() {

		String[] patternExamples = {
			"{A-Z},{a-z},{0-9}",
			"{a-z},{0-9}",
			"{A-Z},{0-9}",
			"{A-Z},{a-z}",
			"{0-9}",
			"{A-Z}",
			"{a-z}"
		};
		currentPattern = patternExamples[0];
		setUpFormats();
		JComboBox patternList = new JComboBox(patternExamples);
		patternList.setEditable(true);
		patternList.addActionListener(this);

		patternLabel = new JLabel(pattern_string);
		minlengthLabel = new JLabel(min_length_string);
		maxlengthLabel = new JLabel(max_length_string);
		passwordLabel = new JLabel(password_result);
		minalphaLabel = new JLabel(min_alpha_string);
		minnumericLabel = new JLabel(min_numeric_string);

		min_length_field = new JFormattedTextField(MinAmountFormat);
		min_length_field.setValue(new Integer(min_length));
		min_length_field.setColumns(3);
		min_length_field.addPropertyChangeListener(this);

		max_length_field = new JFormattedTextField(MaxAmountFormat);
		max_length_field.setValue(new Integer(max_length));
		max_length_field.setColumns(3);
		max_length_field.addPropertyChangeListener(this);

		resulting_password = new JTextField();
		resulting_password.setColumns(20);
		resulting_password.setEditable(false);

		SpinnerModel model = new SpinnerNumberModel(1, 0, 100, 1);
		min_alpha = new JSpinner(model);
		model = new SpinnerNumberModel(2, 0, 100, 1);
		min_numeric = new JSpinner(model);

		patternLabel.setLabelFor(patternList);
		minlengthLabel.setLabelFor(min_length_field);
		maxlengthLabel.setLabelFor(max_length_field);
		minalphaLabel.setLabelFor(min_alpha);
		minnumericLabel.setLabelFor(min_numeric);
		passwordLabel.setLabelFor(resulting_password);

		JPanel labelPane = new JPanel(new GridLayout(0,1));
		labelPane.add(patternLabel);
		labelPane.add(minlengthLabel);
		labelPane.add(maxlengthLabel);
		labelPane.add(minalphaLabel);
		labelPane.add(minnumericLabel);
		labelPane.add(passwordLabel);

		JPanel fieldPane = new JPanel(new GridLayout(0,1));
		fieldPane.add(patternList);
		fieldPane.add(min_length_field);
		fieldPane.add(max_length_field);
		fieldPane.add(min_alpha);
		fieldPane.add(min_numeric);
		fieldPane.add(resulting_password);

		setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
		add(labelPane, BorderLayout.CENTER);
		add(fieldPane, BorderLayout.LINE_END);
	}

	public void actionPerformed(ActionEvent e) {
		JComboBox cb = (JComboBox)e.getSource();
		String newSelection = (String)cb.getSelectedItem();
		currentPattern = newSelection;
		for(int i = 0; i < 3; i++) {
			includeSets[i] = false;
		}
		int index,indeX;
		String udp = new String();
		if((index = currentPattern.indexOf('(')) != -1) {
			if((indeX = currentPattern.indexOf(')', index)) != -1) {
				udp = currentPattern.substring(index + 1, indeX);
				ud.clear();
				for(int b = 0; b < udp.length(); b++) {
					ud.add(new Character(udp.charAt(b)));
				}
			}
		}
		else {
			ud.clear();
		}
		if(currentPattern.indexOf("{a-z}") != -1) {
			includeSets[0] = true;
		}
		if(currentPattern.indexOf("{A-Z}") != -1) {
			includeSets[1] = true;
		}
		if(currentPattern.indexOf("{0-9}") != -1) {
			includeSets[2] = true;
		}
		generatePassword();
	}
	public void propertyChange(PropertyChangeEvent e) {
		Object source = e.getSource();
		if(source == min_length_field) {
			min_length = ((Number)min_length_field.getValue()).intValue();
		} 
		if(source == max_length_field) {
			max_length = ((Number)max_length_field.getValue()).intValue();
		}
		generatePassword();
	}
	private void setGrid() {
		char c;
		lowercase = new ArrayList();
		for(c = 'a'; c != 'z'; c++) {
			lowercase.add(new Character(c));
		}
		uppercase = new ArrayList();
		for(c = 'A'; c != 'Z'; c++) {
			uppercase.add(new Character(c));
		}
		numerics = new ArrayList();
		for(c = '0'; c != '9'; c++) {
			numerics.add(new Character(c));
		}
		ud = new ArrayList();
		includeSets = new boolean[3];
		includeSets[0] = true;
		includeSets[1] = true;
		includeSets[2] = true;
	}
	private void generatePassword() {
		int niter;
		niter = (int)(Math.random() * (max_length - min_length +1) ) + min_length;
		int s;
		int indx;
		java.util.List union = new ArrayList();
		if(includeSets[0]) {
			union.addAll(lowercase);
		}
		if(includeSets[1]) {
			union.addAll(uppercase);
		}
		if(includeSets[2]) {
			union.addAll(numerics);
		}
		union.addAll(ud);
		StringBuffer S = new StringBuffer();
		for(s = 0; s < niter; s++) {
			indx = (int)(Math.random() *((union.size()-1 ) - 1 + 1)  ) + 1;
			S.append(union.get(indx));
		}
		resulting_password.setText(S.toString());
	}
	public PasswordGenerator() {
		super(new BorderLayout());
		JFrame.setDefaultLookAndFeelDecorated(true);
		JFrame frame = new JFrame("Password Generator");

		setGrid();
		createAndShowGUI();
		JComponent newContentPane = this;
		newContentPane.setOpaque(true);
		frame.setContentPane(newContentPane);
		frame.pack();
		frame.setVisible(true);
	}
	private void setUpFormats() {
		MinAmountFormat = NumberFormat.getNumberInstance();
		MaxAmountFormat = NumberFormat.getNumberInstance();
	}
}
