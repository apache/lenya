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

package org.apache.lenya.config;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.apache.lenya.config.core.Configuration;
import org.apache.lenya.config.core.FileConfiguration;
import org.apache.lenya.config.core.Parameter;
import org.apache.lenya.config.impl.BuildPropertiesConfiguration;


/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
public class ConfigureGUI {
    protected static final Component Next = null;
    protected static final Component Previous = null;

    private JLabel paraValueLabel;
    private JLabel jLabel4;
    private JLabel jLabel3;

	private JButton jButton2;
	private JButton jButton1;
	private JButton nextButton;
//	private JButton save;
	private JTextField newLocalValueTextField;
	private JPanel jPanel4;
	private JPanel jPanel3;
	private JPanel jPanel2;
	private JPanel jPanel1;
	private Parameter[] params;
	private int steps = 0;
	public static void main(String[] args) {
		// ausgabe in Console
		System.out.println("\nWelcome to the GUI to configure the building process of Apache Lenya");
		//wenn Pfad nicht da ist
		if (args.length != 1) {
			//error ausgabe in Console
			System.err.println("No root dir specified (e.g. /home/USERNAME/src/lenya/trunk)!");
			return;
		}
		String rootDir = args[0];

		new ConfigureGUI(rootDir);
	}

	public ConfigureGUI(String rootDir) {
		//ausgabe in Console
		System.out.println("Starting GUI ...");
		// Alle Configuration Files definieren
		FileConfiguration buildProperties = new BuildPropertiesConfiguration();
		buildProperties.setFilenameDefault(rootDir + "/build.properties");
		buildProperties.setFilenameLocal(rootDir + "/local.build.properties");
		// neue Vektor
		Vector configs = new Vector();
		configs.addElement(buildProperties);

		JFrame.setDefaultLookAndFeelDecorated(true);
		JFrame frame = new JFrame("Apache Lenya Configuration");
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

		frame.pack();
                int frameWidth = 408;
                int frameHeight = 222;
		frame.setSize(frameWidth, frameHeight);
		frame.setVisible(true);

                JPanel paraNamePanel = new JPanel();
		paraNamePanel.setPreferredSize(new java.awt.Dimension(frameWidth, 91));
                frame.getContentPane().add(paraNamePanel);
		JLabel paraNameLabel = new JLabel("Parameter:");
		paraNamePanel.add(paraNameLabel);
		paraValueLabel = new JLabel(params[0].getName());
		paraNamePanel.add(paraValueLabel);

		jPanel1 = new JPanel();
		//jPanel1.setBorder(new Border());
		jPanel1.setLayout(new FlowLayout(FlowLayout.LEFT));
                int width = 126;
                int height = 91;
		jPanel1.setPreferredSize(new java.awt.Dimension(width, height));
		frame.getContentPane().add(jPanel1);

		JLabel defaultValueLabel = new JLabel("Default Value:");
		defaultValueLabel.setPreferredSize(new java.awt.Dimension(width, 18));
		jPanel1.add(defaultValueLabel);

		JLabel localValueLabel = new JLabel("Local Value:");
		localValueLabel.setPreferredSize(new java.awt.Dimension(width, 18));
		jPanel1.add(localValueLabel);

		JLabel newLocalValueLabel= new JLabel("New Local Value:");
		newLocalValueLabel.setPreferredSize(new java.awt.Dimension(width, 18));
		jPanel1.add(newLocalValueLabel);



		jPanel3 = new JPanel();
		frame.getContentPane().add(jPanel3);
		FlowLayout jPanel3Layout = new FlowLayout();
		jPanel3Layout.setAlignment(FlowLayout.RIGHT);
		jPanel3.setPreferredSize(new java.awt.Dimension(164, 93));
		jPanel3.setLayout(jPanel3Layout);

		jPanel2 = new JPanel();
		ButtonGroup g = new ButtonGroup();

		frame.getContentPane().add(jPanel2);
		jPanel2.setPreferredSize(new java.awt.Dimension(90, 95));

		JRadioButton jRadioButton3 = new JRadioButton();
		jPanel2.add(jRadioButton3);
		jRadioButton3.setPreferredSize(new java.awt.Dimension(57, 18));
		g.add(jRadioButton3);

		JRadioButton jRadioButton2 = new JRadioButton();
		jPanel2.add(jRadioButton2);
		jRadioButton2.setSelected(true);
		jRadioButton2.setPreferredSize(new java.awt.Dimension(57, 18));
		g.add(jRadioButton2);
		

		jLabel3 = new JLabel();
		jPanel3.add(jLabel3);
		jLabel3.setText(params[0].getDefaultValue() + "\n");

		jLabel4 = new JLabel();
		jPanel3.add(jLabel4);
		jLabel4.setText(params[0].getLocalValue() + "\n");

		newLocalValueTextField = new JTextField();
		jPanel3.add(newLocalValueTextField);
		newLocalValueTextField.setPreferredSize(new java.awt.Dimension(163, 20));

		JRadioButton jRadioButton1;
		jRadioButton1 = new JRadioButton();
		jRadioButton1.setText("False");
		jRadioButton1.setPreferredSize(new java.awt.Dimension(61, 18));
		jPanel4 = new JPanel();
		frame.getContentPane().add(jPanel4);
		FlowLayout jPanel4Layout = new FlowLayout();
		jPanel4Layout.setAlignment(FlowLayout.RIGHT);
		jPanel4.setPreferredSize(new java.awt.Dimension(309, 80));
		jPanel4.setLayout(jPanel4Layout);

		jButton2 = new JButton();
		jPanel4.add(jButton2);
		jButton2.setText("Cancel");
		jButton2.setPreferredSize(new java.awt.Dimension(74, 22));
		jButton2.addActionListener(new ActionListener() {

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

		jButton1 = new JButton();
		jPanel4.add(jButton1);
		jButton1.setText("<Back");
		jButton1.setPreferredSize(new java.awt.Dimension(74, 22));
		jButton1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jButton1.setEnabled(true);
				if (jPanel4.isVisible())
					jButton1.setEnabled(true);
				moveParameter("back");
			}
		});
		int configIndex = 0;
		if (configIndex == 0) {
			jButton1.setEnabled(false);
		}

                // Next button
		nextButton = new JButton();
		jPanel4.add(nextButton);
		nextButton.setText("Next>");
		nextButton.setPreferredSize(new java.awt.Dimension(74, 22));
		nextButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
				jButton1.setEnabled(true);
				if (jPanel4.isVisible())
					nextButton.setEnabled(true);
				moveParameter("next");
			}
		});

	}

	private void moveParameter(String richtung) {
		if (richtung.equals("next")) {
			steps++;
			if (steps >= params.length - 1) {
				nextButton.setEnabled(false);
				if (steps == 4) {
					jButton1.setEnabled(false);
					jPanel1.setVisible(false);
					jPanel2.setVisible(false);
					jPanel3.setVisible(false);
					jButton1.setVisible(false);
					jButton2.setVisible(false);
					nextButton.setVisible(false);
					
					final JLabel warning1 = new JLabel(
							"WARNING: Local configuration already exists!\n");
					final JLabel warning2 = new JLabel(
							"Do you want to overwrite?");
					final JButton yes = new JButton("yes");
					yes.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							jButton1.setEnabled(false);
						}
					});

					final JButton no = new JButton("no");
					no.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							jButton1.setEnabled(true);
							jPanel1.setVisible(true);
							jPanel2.setVisible(true);
							jPanel3.setVisible(true);
							warning1.setVisible(false);
							warning2.setVisible(false);
							yes.setVisible(false);
							no.setVisible(false);
							jButton1.setVisible(true);
							jButton2.setVisible(true);
							nextButton.setEnabled(false);
							nextButton.setVisible(true);
//							JButton save = new JButton("save");
//							jPanel4.add(save);
//							save.setPreferredSize(new java.awt.Dimension(74, 22));
//							save.setVisible(true);							
						}
					});
					jPanel4.add(warning1);
					jPanel4.add(warning2);
					jPanel4.add(yes);
					jPanel4.add(no);
				}
			}
			jLabel3.setText("          " + params[steps].getDefaultValue()
					+ "\n");
			jLabel4.setText("          " + params[steps].getLocalValue() + "\n");
			paraValueLabel.setText("          " + params[steps].getName());

		} else {
			steps--;
			if (steps == 0) {
				jButton1.setEnabled(false);
			}
//			if (steps == 4){
//				nextButton.setVisible(true);
//			}

			jLabel3.setText("          " + params[steps].getDefaultValue()
					+ "\n");
			jLabel4.setText("          " + params[steps].getLocalValue() + "\n");
			paraValueLabel.setText("          " + params[steps].getName());
			nextButton.setEnabled(true);
//			save.setVisible(false);
		}
	}
}

