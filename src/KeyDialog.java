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
// THIS SOFTWARE IS PROVIDED `AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES,
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
import java.lang.reflect.*;

public class KeyDialog extends JDialog
	implements ActionListener {

		private JLabel nameLabel;
		private JLabel descriptionLabel;
		private JLabel passwordLabel;

		private static String min_length_string = "Name: ";
		private static String max_length_string = "Description: ";
		private static String password_result = "New Password: ";

		private JTextField nameField, descriptionField;
		private JPasswordField passwordField;
		protected JButton okButton, cancelButton;
		private static SecretsKey nkey;

		protected static KeyDialog dialog;

		private KeyDialog(Frame parent, boolean modal) {
			super(parent, modal);

			setTitle("Enter new record.");

			nameLabel = new JLabel(min_length_string);
			descriptionLabel = new JLabel(max_length_string);
			passwordLabel = new JLabel(password_result);
			nameField = new JTextField();
			nameField.setColumns(10);

			descriptionField = new JTextField();
			descriptionField.setColumns(10);

			passwordField = new JPasswordField();
			passwordField.setEchoChar('\u2022');
			passwordField.setColumns(20);

			nameLabel.setLabelFor(nameField);
			descriptionLabel.setLabelFor(descriptionField);
			passwordLabel.setLabelFor(passwordField);

			okButton = new JButton("Ok");
			okButton.setMnemonic(KeyEvent.VK_O);
			okButton.setActionCommand("ok");

			cancelButton = new JButton("Cancel");
			cancelButton.setMnemonic(KeyEvent.VK_C);
			cancelButton.setActionCommand("cancel");

			okButton.addActionListener(this);
			cancelButton.addActionListener(this);

			JPanel labelPane = new JPanel(new GridLayout(0,1));
			labelPane.add(nameLabel);
			labelPane.add(descriptionLabel);
			labelPane.add(passwordLabel);

			JPanel fieldPane = new JPanel(new GridLayout(0,1));
			fieldPane.add(nameField);
			fieldPane.add(descriptionField);
			fieldPane.add(passwordField);

			JPanel dialogPane = new JPanel();
			dialogPane.add(labelPane);
			dialogPane.add(fieldPane);

			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
			buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
			buttonPane.add(Box.createHorizontalGlue());
			buttonPane.add(cancelButton);
			buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
			buttonPane.add(okButton);

			Container contentPane = getContentPane();
			contentPane.add(dialogPane, BorderLayout.CENTER);
			contentPane.add(buttonPane, BorderLayout.PAGE_END);
			getRootPane().setDefaultButton(okButton);
			pack();
		}
		public void actionPerformed(ActionEvent e) {
			if("ok".equals(e.getActionCommand())) {
				nkey = new SecretsKey();
				nkey.key_name = new String(nameField.getText());
				nkey.description = new String(descriptionField.getText());
				nkey.key_value = new String(passwordField.getPassword());
			} else {
				nkey = null;
			}
			KeyDialog.dialog.setVisible(false);
		}
		public static SecretsKey showNewKeyDialog(Frame parent) {
			dialog = new KeyDialog(parent, true);
			dialog.setVisible(true);
			return nkey;
		}
	}
