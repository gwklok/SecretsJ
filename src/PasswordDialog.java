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

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class PasswordDialog extends JDialog
		implements ActionListener {
	private JPasswordField passwordField;
	private JLabel passwordLabel;
	protected JButton okButton, cancelButton;
	private static PasswordDialog dialog;
	private static String password;

	public static String showPasswordDialog(Frame parent) {
		dialog = new PasswordDialog(parent, true);
		dialog.setVisible(true);
		return password;
	}
	private PasswordDialog(Frame parent, boolean modal) {
		super(parent, modal);

		setTitle("Enter Password");

		passwordField = new JPasswordField(10);
		passwordField.setEchoChar('\u2022');

		passwordLabel = new JLabel("Please enter the password for this secrets password database file.");
		passwordLabel.setLabelFor(passwordField);

		okButton = new JButton("Ok");
		okButton.setMnemonic(KeyEvent.VK_O);
		okButton.setActionCommand("ok");

		cancelButton = new JButton("Cancel");
		cancelButton.setMnemonic(KeyEvent.VK_C);
		cancelButton.setActionCommand("cancel");

		okButton.addActionListener(this);
		cancelButton.addActionListener(this);
		JPanel dialogPane = new JPanel();
		dialogPane.setLayout(new BoxLayout(dialogPane, BoxLayout.PAGE_AXIS));
		dialogPane.add(passwordLabel);
		dialogPane.add(Box.createRigidArea(new Dimension(0,5)));
		dialogPane.add(passwordField);
		dialogPane.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

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
			password = new String(passwordField.getPassword());
		} else {
			password = null;
		}
		PasswordDialog.dialog.setVisible(false);
	}
}
