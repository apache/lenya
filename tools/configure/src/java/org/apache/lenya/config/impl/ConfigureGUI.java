/*
 * Copyright  1999-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.apache.lenya.config.impl;

import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.apache.lenya.config.core.Configuration;
import org.apache.lenya.config.core.FileConfiguration;
import org.apache.lenya.config.core.Parameter;

/**
 * This code was edited or generated using CloudGarden's Jigloo SWT/Swing GUI
 * Builder, which is free for non-commercial use. If Jigloo is being used
 * commercially (ie, by a corporation, company or business for any purpose
 * whatever) then you should purchase a license for each developer using Jigloo.
 * Please visit www.cloudgarden.com for details. Use of Jigloo implies
 * acceptance of these licensing terms. A COMMERCIAL LICENSE HAS NOT BEEN
 * PURCHASED FOR THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED LEGALLY FOR
 * ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
public class ConfigureGUI {
	protected static final Component Next = null;

	protected static final Component Previous = null;

	private JPanel contentPanel;

	private JPanel checkBoxPanel;

	private JPanel buttonPanel;

	private JPanel messageSavePanel;

	private JCheckBox[] checkBoxes;

	private JLabel defaultValueLabel;

	private JLabel localValueLabel;

	private JLabel newLocalValueLabel;

	private JRadioButton RadioButton1;

	private JRadioButton RadioButton2;

	private JRadioButton RadioButton3;

	private JLabel stepsLabel;

	private JLabel paraValueLabel;

	private JTextField LocalValueTextField;

	private JTextField DefaultValueTextField;

	private JTextField newLocalValueTextField;

	private JComboBox DefaultValueComboBox;

	private JComboBox LocalValueComboBox;

	private JButton cancelButton;

	private JButton backButton;

	private JButton nextButton;

	private JButton saveButton;

	private JButton yesButton;

	private JButton noButton;

	private Parameter[] params;

	private JFrame frame;

	private JLabel warning1;

	private JLabel warning2;

	private JLabel saveMessage;

	private int steps = 0;

	public final static boolean RIGHT_TO_LEFT = false;

	private FileConfiguration buildProperties;

	public static void main(String[] args) {

		System.out
				.println("\nWelcome to the GUI to configure the building process of Apache Lenya");

		if (args.length != 1) {

			System.err
					.println("No root dir specified (e.g. /home/USERNAME/src/lenya/trunk)!");
			return;
		}
		String rootDir = args[0];

		new ConfigureGUI(rootDir);
	}

	public ConfigureGUI(String rootDir) {

		System.out.println("Starting GUI ...");

		buildProperties = new BuildPropertiesConfiguration();
		buildProperties.setFilenameDefault(rootDir + "/build.properties");
		buildProperties.setFilenameLocal(rootDir + "/local.build.properties");

		Vector configs = new Vector();
		configs.addElement(buildProperties);

		JFrame.setDefaultLookAndFeelDecorated(true);
		frame = new JFrame("Apache Lenya Configuration");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new FlowLayout(FlowLayout.LEFT));

		for (int i = 0; i < configs.size(); i++) {
			Configuration config = (Configuration) configs.elementAt(i);
			config.read();
			params = config.getConfigurableParameters();
		}

		Configuration config = (Configuration) configs.elementAt(0);
		config.read();
		params = config.getConfigurableParameters();

		contentPanel = new JPanel();
		checkBoxPanel = new JPanel();
		buttonPanel = new JPanel();

		defaultValueLabel = new JLabel();
		localValueLabel = new JLabel();
		newLocalValueLabel = new JLabel();

		DefaultValueTextField = new JTextField();
		LocalValueTextField = new JTextField();
		newLocalValueTextField = new JTextField();

		DefaultValueComboBox = new JComboBox();
		LocalValueComboBox = new JComboBox();

		RadioButton1 = new JRadioButton();
		RadioButton2 = new JRadioButton();
		RadioButton3 = new JRadioButton();
		ButtonGroup g = new ButtonGroup();

		cancelButton = new JButton();
		backButton = new JButton();
		nextButton = new JButton();
		saveButton = new JButton();
		noButton = new JButton();
		yesButton = new JButton();

		warning1 = new JLabel();
		warning2 = new JLabel();

		saveMessage = new JLabel();
		messageSavePanel = new JPanel();

		Container contentPane = frame.getContentPane();
		contentPane.setLayout(new FlowLayout(FlowLayout.LEFT));

                //TODO: Somehow this doesn't work
		//contentPane.setPreferredSize(new java.awt.Dimension(380, 182));

		contentPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		stepsLabel = new JLabel();
		stepsLabel.setText("Parameters  ");
		c.gridx = 0;
		c.gridy = 0;
		contentPanel.add(stepsLabel, c);

		checkBoxPanel = new JPanel();
		checkBoxPanel.setLayout(new BoxLayout(checkBoxPanel, BoxLayout.Y_AXIS));

		c.gridx = 0;
		c.gridy = 1;
		c.gridheight = 4;
		c.ipadx = 20;
		contentPanel.add(checkBoxPanel, c);

		checkBoxes = new JCheckBox[params.length];

		for (int i = 0; i < params.length; ++i) {
			checkBoxes[i] = new JCheckBox();
			checkBoxes[i].setEnabled(false);
			checkBoxes[i].setText(params[i].getName());
			checkBoxes[0].setSelected(true);
			checkBoxPanel.add(checkBoxes[i]);
		}

		paraValueLabel = new JLabel();
		c.gridx = 1;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		paraValueLabel = new JLabel("Parameter: " + params[0].getName());
		contentPanel.add(paraValueLabel, c);

		defaultValueLabel.setText("Default Value:");
		contentPanel.add(defaultValueLabel, new GridBagConstraints(1, 1, 1, 1,
				0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 0), 0, 0));
		contentPanel.add(DefaultValueTextField, new GridBagConstraints(2, 1, 1,
				1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		DefaultValueTextField.setText(params[0].getDefaultValue());
		DefaultValueTextField.addMouseListener(new MouseListener() {

			public void mouseClicked(MouseEvent event) {
				RadioButton1.setSelected(true);
			}

			public void mousePressed(MouseEvent event) {
			}

			public void mouseReleased(MouseEvent event) {
			}

			public void mouseEntered(MouseEvent event) {
			}

			public void mouseExited(MouseEvent event) {
			}
		});

		contentPanel.add(RadioButton1, new GridBagConstraints(3, 1, 1, 1, 0.0,
				0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 0), 0, 0));
		g.add(RadioButton1);

		localValueLabel.setText("Local Value:");
		contentPanel.add(localValueLabel, new GridBagConstraints(1, 2, 1, 1,
				0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 0), 0, 0));
		contentPanel.add(LocalValueTextField, new GridBagConstraints(2, 2, 1,
				1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		LocalValueTextField.setText(params[0].getLocalValue());
		LocalValueTextField.addMouseListener(new MouseListener() {

			public void mouseClicked(MouseEvent event) {
			}

			public void mousePressed(MouseEvent event) {
				RadioButton2.setSelected(true);
			}

			public void mouseReleased(MouseEvent event) {
			}

			public void mouseEntered(MouseEvent event) {
			}

			public void mouseExited(MouseEvent event) {
			}
		});

		contentPanel.add(RadioButton2, new GridBagConstraints(3, 2, 1, 1, 0.0,
				0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 0), 0, 0));
		g.add(RadioButton2);
		RadioButton2.setSelected(true);

		newLocalValueLabel.setText("new Local Value:");
		contentPanel.add(newLocalValueLabel, new GridBagConstraints(1, 3, 1, 1,
				0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 0), 0, 0));
		contentPanel.add(newLocalValueTextField, new GridBagConstraints(2, 3,
				1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		newLocalValueTextField.addMouseListener(new MouseListener() {

			public void mouseClicked(MouseEvent event) {
			}

			public void mousePressed(MouseEvent event) {
				RadioButton3.setSelected(true);
			}

			public void mouseEntered(MouseEvent event) {
			}

			public void mouseExited(MouseEvent event) {
			}

			public void mouseReleased(MouseEvent event) {
			}
		});

		contentPanel.add(RadioButton3, new GridBagConstraints(3, 3, 1, 1, 0.0,
				0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 0), 0, 0));
		g.add(RadioButton3);

		buttonPanel = new JPanel();
		cancelButton.setText("Cancel");
		contentPanel.add(cancelButton, new GridBagConstraints(1, 4, 1, 1, 0.0,
				0.0, GridBagConstraints.SOUTH, GridBagConstraints.PAGE_END,
				new Insets(0, 0, 0, 0), 0, 0));
		cancelButton.setPreferredSize(new java.awt.Dimension(74, 22));
		cancelButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent evt) {
				int n = JOptionPane
						.showConfirmDialog((Component) null,
								"Do you want to Exit?", "Exit...",
								JOptionPane.YES_NO_OPTION,
								JOptionPane.QUESTION_MESSAGE);
				if (n == JOptionPane.YES_OPTION) {
					System.exit(0);
				}
			}
		});

		backButton.setText("<Back");
		contentPanel.add(backButton, new GridBagConstraints(2, 4, 1, 1, 0.0,
				0.0, GridBagConstraints.SOUTH, GridBagConstraints.PAGE_END,
				new Insets(0, 0, 0, 0), 0, 0));
		backButton.setPreferredSize(new java.awt.Dimension(74, 22));
		backButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				backButton.setEnabled(true);
				if (contentPanel.isVisible())
					backButton.setEnabled(true);
				if (steps != params.length) {
					saveButton.setVisible(false);
				}
				moveBack();
			}
		});

		backButton.setEnabled(false);

		nextButton.setText("Next>");
		contentPanel.add(nextButton, new GridBagConstraints(3, 4, 1, 1, 0.0,
				0.0, GridBagConstraints.SOUTH, GridBagConstraints.PAGE_END,
				new Insets(0, 0, 0, 0), 0, 0));
		nextButton.setPreferredSize(new java.awt.Dimension(74, 22));
		nextButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				backButton.setEnabled(true);
				if (contentPanel.isVisible())
					nextButton.setEnabled(true);
				moveNext();
			}
		});

		buttonPanel.add(cancelButton);
		buttonPanel.add(backButton);
		buttonPanel.add(nextButton);
		contentPane.add(contentPanel);
		contentPanel.add(buttonPanel, new GridBagConstraints(2, 4, 1, 1, 0.0,
				0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 0), 0, 0));

		int frameWidth = 570;
		int frameHeight = 250;
		frame.pack();
		frame.setSize(frameWidth, frameHeight);
		frame.setVisible(true);
	}

	public void moveBack() {
		steps--;
		frame.repaint();
		nextButton.setVisible(true);
		checkFirst();
		currentStep("down");
		showNormalOptions();
		comboBox();
		checkLast();
		newLocalValueTextField.setText("");
	}

	public void moveNext() {
		steps++;
		frame.repaint();
		checkFirst();
		currentStep("up");
		showNormalOptions();
		comboBox();
		checkLast();
		newLocalValueTextField.setText("");
	}

	public void currentStep(String direction) {
		if (direction.equals("up")) {
			for (int i = 1; i <= getStep(); ++i) {
				checkBoxes[i].setSelected(true);
			}
		}
		if (direction.equals("down")) {
			checkBoxes[getStep() + 1].setSelected(false);
		}
	}

	public void checkFirst() {

		if (getStep() == 0) {
			backButton.setEnabled(false);
		} else {
			backButton.setEnabled(true);
		}
	}

	public void checkLast() {
		System.out.println(getStep());
		saveButton = new JButton("Save");

		warning1 = new JLabel("WARNING: Local configuration already exists!");
		warning2 = new JLabel("Do you want to overwrite?");
		if (getStep() == params.length - 1) {
			nextButton.setEnabled(false);
			nextButton.setVisible(false);

			buttonPanel.add(saveButton);
			contentPanel.add(buttonPanel, new GridBagConstraints(2, 4, 1, 1,
					0.0, 0.0, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

			saveButton.setPreferredSize(new java.awt.Dimension(74, 22));
			saveButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					showSaveScreen();
					showWarningScreen();
				}

			});

		} else {
			nextButton.setEnabled(true);
			warning1.setVisible(false);
			warning2.setVisible(false);

		}
	}

	public void showNormalOptions() {

		if (steps < params.length) {
			DefaultValueTextField.setText(params[steps].getDefaultValue());
			LocalValueTextField.setText(params[steps].getLocalValue());
			paraValueLabel.setText(params[steps].getName());
		}

	}

	private void showSaveScreen() {

		paraValueLabel.setVisible(false);
		defaultValueLabel.setVisible(false);
		localValueLabel.setVisible(false);
		newLocalValueLabel.setVisible(false);
		DefaultValueTextField.setVisible(false);
		LocalValueTextField.setVisible(false);
		newLocalValueTextField.setVisible(false);
		RadioButton1.setVisible(false);
		RadioButton2.setVisible(false);
		RadioButton3.setVisible(false);
		cancelButton.setVisible(false);
		nextButton.setVisible(false);
		backButton.setVisible(false);
		saveButton.setVisible(false);

		yesButton.setVisible(true);
		noButton.setVisible(true);

		warning1.setVisible(true);
		warning2.setVisible(true);

		// frame.repaint();

	}

	private void showWarningScreen() {
		contentPanel.add(warning1, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						0, 0, 0, 0), 0, 0));
		contentPanel.add(warning2, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						0, 0, 0, 0), 0, 0));
		yesButton.setText("yes");
		buttonPanel.add(yesButton);
		contentPanel.add(buttonPanel, new GridBagConstraints(2, 4, 1, 1, 0.0,
				0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 0), 0, 0));
		yesButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				showYesScreen();

			}
		});

		noButton.setText("no");
		buttonPanel.add(noButton);
		contentPanel.add(buttonPanel, new GridBagConstraints(2, 4, 1, 1, 0.0,
				0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 0), 0, 0));
		noButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				showNoScreen();

			}
		});

	}

	private void showYesScreen() {
		buildProperties.writeLocal();
		saveMessage.setText("saved!");
		// messageSavePanel.add(saveMessage);
		contentPanel.add(saveMessage, new GridBagConstraints(3, 5, 0, 0, 0.0,
				0.0, GridBagConstraints.SOUTH, GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 0), 0, 0));
		saveMessage.setVisible(true);

		contentPanel.revalidate();
		contentPanel.repaint();
		System.out.println("saved");

	}

	private void showNoScreen() {

		saveMessage.setVisible(false);
		paraValueLabel.setVisible(true);
		defaultValueLabel.setVisible(true);
		localValueLabel.setVisible(true);
		newLocalValueLabel.setVisible(true);
		DefaultValueTextField.setVisible(true);
		LocalValueTextField.setVisible(true);
		newLocalValueTextField.setVisible(true);

		RadioButton1.setVisible(true);
		RadioButton2.setVisible(true);
		RadioButton3.setVisible(true);
		cancelButton.setVisible(true);
		nextButton.setVisible(false);

		backButton.setVisible(true);
		saveButton.setVisible(true);

		yesButton.setVisible(false);
		noButton.setVisible(false);

		saveMessage.setVisible(false);

	}

	public void comboBox() {
		/* Hardcoded, we cant know where the dropdown could be... */

		if (steps == 3) {
			DefaultValueTextField.setVisible(false);
			LocalValueTextField.setVisible(false);
			newLocalValueTextField.setVisible(true);

			String labels[] = { "Jetty", "Tomcat", "Wls" };
			DefaultValueComboBox = new JComboBox(labels);
			DefaultValueComboBox.setMaximumRowCount(3);
			contentPanel.add(DefaultValueComboBox, new GridBagConstraints(2, 1,
					1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
			LocalValueComboBox = new JComboBox(labels);
			LocalValueComboBox.setMaximumRowCount(3);
			contentPanel.add(LocalValueComboBox, new GridBagConstraints(2, 2,
					1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
			contentPanel.revalidate();
		} else {
			DefaultValueComboBox.setVisible(false);
			LocalValueComboBox.setVisible(false);
			DefaultValueTextField.setVisible(true);
			LocalValueTextField.setVisible(true);
			warning1.setVisible(false);
			warning2.setVisible(false);

		}
	}

	public int getStep() {
		return steps;
	}

}
