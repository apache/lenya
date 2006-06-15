/*
 * Copyright 1999-2006 The Apache Software Foundation Licensed under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable
 * law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 * for the specific language governing permissions and limitations under the License.
 */

package org.apache.lenya.config.impl;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

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
import javax.swing.UIManager;

import org.apache.lenya.config.core.FileConfiguration;
import org.apache.lenya.config.core.ContextEventQueue;
import org.apache.lenya.config.core.Parameter;

/**
 * A GUI tool to configure Lenya 1.4 build
 */
public class ConfigureGUI {

    protected JFrame frame;
    private JPanel contentPanel;
    private JPanel checkBoxPanel;
    private JPanel buttonPanel;
    private JCheckBox[] checkBoxes;
    private JLabel defaultValueLabel;
    private JLabel localValueLabel;
    private JLabel newLocalValueLabel;
    private JLabel stepsLabel;
    private JLabel paraValueLabel;
    private JLabel warning1;
    private JLabel warning2;
    private JLabel saveMessage;
    private JRadioButton radioButtonDefault;
    private JRadioButton radioButtonLocal;
    private JRadioButton radioButtonNewLocal;
    private JTextField localValueTextField;
    private JTextField defaultValueTextField;
    private JTextField newLocalValueTextField;
    private JTextField defaultValueCTextField;
    private JTextField localValueCTextField;
    private JComboBox newLocalValueComboBox;
    private JButton cancelButton;
    protected JButton backButton;
    protected JButton nextButton;
    private JButton saveButton;
    private JButton yesButton;
    private JButton noButton;
    private JButton exitButton;
    private Parameter[] params;
    private Parameter[] tmpParams;
    private Parameter[] tmpSubParams;
    protected Parameter[] subParams;
    private int steps = 0;
    private String rootDir;
    private SubParamsGUI spGui;
    private FileConfiguration buildProperties;
    protected FileConfiguration tmpBuildProperties;

    /**
     * Main method which creates the GUI
     * 
     * @param args
     */
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

    /**
     * .ctor
     * 
     * @param rootDir
     */
    public ConfigureGUI(String rootDir) {

        // pushes the eventQueue which will take care of copy/paste operations
        Toolkit.getDefaultToolkit().getSystemEventQueue().push(
                new ContextEventQueue());

        this.rootDir = rootDir;
        System.out.println("Starting GUI ...");

        buildProperties = new BuildPropertiesConfiguration();
        buildProperties.setFilenameDefault(rootDir + "/build.properties");
        buildProperties.setFilenameLocal(rootDir + "/local.build.properties");

        buildProperties.read();
        params = buildProperties.getConfigurableParameters();

        tmpBuildProperties = new BuildPropertiesConfiguration();
        tmpBuildProperties.setFilenameDefault(rootDir + "/build.properties");
        tmpBuildProperties
                .setFilenameLocal(rootDir + "/local.build.properties");
        tmpBuildProperties.read();
        tmpParams = tmpBuildProperties.getConfigurableParameters();
        // Empty temporary local fields of temporary parameters
        for (int k = 0; k < tmpParams.length; k++) {
            tmpParams[k].setLocalValue("");
        }       

        JFrame.setDefaultLookAndFeelDecorated(true);
        try {
            UIManager.setLookAndFeel(UIManager
                    .getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.getMessage();
        }

        frame = new JFrame("Apache Lenya Configuration");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new FlowLayout(FlowLayout.LEFT));

        contentPanel = new JPanel();
        checkBoxPanel = new JPanel();
        buttonPanel = new JPanel();

        defaultValueLabel = new JLabel();
        localValueLabel = new JLabel();
        newLocalValueLabel = new JLabel();

        defaultValueTextField = new JTextField(20);
        localValueTextField = new JTextField(20);
        newLocalValueTextField = new JTextField(20);

        defaultValueCTextField = new JTextField();
        localValueCTextField = new JTextField();
        newLocalValueComboBox = new JComboBox();

        radioButtonDefault = new JRadioButton();
        radioButtonLocal = new JRadioButton();
        radioButtonNewLocal = new JRadioButton();
        ButtonGroup g = new ButtonGroup();

        cancelButton = new JButton();
        backButton = new JButton();
        nextButton = new JButton();
        saveButton = new JButton();
        noButton = new JButton();
        yesButton = new JButton();
        exitButton = new JButton();

        warning1 = new JLabel();
        warning2 = new JLabel();

        saveMessage = new JLabel();

        Container contentPane = frame.getContentPane();
        contentPane.setLayout(new FlowLayout(FlowLayout.LEFT));

        contentPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        stepsLabel = new JLabel();
        stepsLabel.setText("Parameters");
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

        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 1;
        paraValueLabel = new JLabel(params[0].getName());
        contentPanel.add(paraValueLabel, c);

        defaultValueLabel.setText("Default Value:");
        contentPanel.add(defaultValueLabel, new GridBagConstraints(1, 1, 1, 1,
                0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        contentPanel.add(defaultValueTextField, new GridBagConstraints(2, 1, 1,
                1, 0.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        defaultValueTextField.setText(params[0].getDefaultValue());
        defaultValueTextField.setEditable(false);
        defaultValueTextField.addMouseListener(new MouseListener() {

            public void mouseClicked(MouseEvent event) {
                radioButtonDefault.setSelected(true);
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

        contentPanel.add(radioButtonDefault, new GridBagConstraints(3, 1, 1, 1,
                0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        g.add(radioButtonDefault);

        localValueLabel.setText("Local Value:");
        contentPanel.add(localValueLabel, new GridBagConstraints(1, 2, 1, 1,
                0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        contentPanel.add(localValueTextField, new GridBagConstraints(2, 2, 1,
                1, 0.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        localValueTextField.setText(params[0].getLocalValue());
        localValueTextField.setEditable(false);
        localValueTextField.addMouseListener(new MouseListener() {

            public void mouseClicked(MouseEvent event) {
            }

            public void mousePressed(MouseEvent event) {
                radioButtonLocal.setSelected(true);
            }

            public void mouseReleased(MouseEvent event) {
            }

            public void mouseEntered(MouseEvent event) {
            }

            public void mouseExited(MouseEvent event) {
            }
        });

        contentPanel.add(radioButtonLocal, new GridBagConstraints(3, 2, 1, 1,
                0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        g.add(radioButtonLocal);
        radioButtonLocal.setSelected(true);

        newLocalValueLabel.setText("New Local Value:");
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
                radioButtonNewLocal.setSelected(true);
            }

            public void mouseEntered(MouseEvent event) {
            }

            public void mouseExited(MouseEvent event) {
            }

            public void mouseReleased(MouseEvent event) {
            }
        });

        contentPanel.add(radioButtonNewLocal, new GridBagConstraints(3, 3, 1,
                1, 0.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        g.add(radioButtonNewLocal);

        buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

        cancelButton.setText("Cancel");
        cancelButton.setPreferredSize(new java.awt.Dimension(74, 22));
        cancelButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {

                int n = JOptionPane
                        .showConfirmDialog(contentPanel,
                                "Do you want to Exit?", "Exit...",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.QUESTION_MESSAGE);

                if (n == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });

        backButton.setText("<Back");
        backButton.setPreferredSize(new java.awt.Dimension(74, 22));
        backButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                backButton.setEnabled(true);
                if (contentPanel.isVisible())
                    backButton.setEnabled(true);
                if (getStep() != params.length) {
                    saveButton.setVisible(false);
                }
                moveBack();
            }
        });

        backButton.setEnabled(false);

        nextButton.setText("Next>");
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

        contentPanel.add(buttonPanel, new GridBagConstraints(1, 4, 6, 0, 0.0,
                0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        contentPane.add(contentPanel);
        contentPanel.revalidate();
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Controls behavior if back button is pressed
     */
    public void moveBack() {
        steps--;
        frame.repaint();
        nextButton.setVisible(true);
        checkFirst();
        currentStep("down");
        showNormalOptions();
        comboBox();
        checkLast();
        newLocalValueTextField.setText(tmpParams[getStep()].getLocalValue());
        setRadioButton();
    }

    /**
     * Controls behavior if next button is pressed
     */
    public void moveNext() {

        setLocalValue();
        String[] av = params[getStep()].getAvailableValues();

        if (av != null) {

            String selectedComboBoxValue = "";
            // Grab the correct value out of the combobox
            if (radioButtonDefault.isSelected())
                selectedComboBoxValue = defaultValueCTextField.getText();
            if (radioButtonLocal.isSelected())
                selectedComboBoxValue = localValueCTextField.getText();
            if (radioButtonNewLocal.isSelected())
                selectedComboBoxValue = newLocalValueComboBox.getSelectedItem()
                        .toString();

            subParams = params[getStep()].getSubsequentParameters(
                    selectedComboBoxValue, buildProperties);
            tmpSubParams = params[getStep()].getSubsequentParameters(
                    selectedComboBoxValue, tmpBuildProperties);

            spGui = new SubParamsGUI(this, tmpSubParams, subParams,
                    selectedComboBoxValue);
        }

        steps++;
        frame.repaint();
        checkFirst();
        currentStep("up");
        showNormalOptions();
        comboBox();
        checkLast();
        newLocalValueTextField.setText(tmpParams[getStep()].getLocalValue());
        setRadioButton();
    }

    /**
     * Set radio button
     */
    public void setRadioButton() {
        if (tmpParams[getStep()].getLocalValue() != "") {
            radioButtonNewLocal.setSelected(true);
        } else if (params[getStep()].getLocalValue() != "") {
            radioButtonLocal.setSelected(true);
        } else {
            radioButtonDefault.setSelected(true);
        }
    }

    /**
     * Set local value depending on chosen value
     */
    public void setLocalValue() {
        String[] av = params[getStep()].getAvailableValues();

        if (av == null) {
            if (radioButtonDefault.isSelected()) {
                tmpParams[getStep()].setLocalValue(defaultValueTextField
                        .getText());
            } else if (radioButtonLocal.isSelected()) {
                tmpParams[getStep()].setLocalValue(localValueTextField
                        .getText());
            } else if (radioButtonNewLocal.isSelected()) {
                tmpParams[getStep()].setLocalValue(newLocalValueTextField
                        .getText());
            } else {
                System.err.println("Fatal Error 0123456789!");
            }
        }
    }

    /**
     * Takes care about the steps progress (list on left side)
     * 
     * @param direction
     */
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

    /**
     * Checks if its first step and disables the back button
     */
    public void checkFirst() {

        if (getStep() == 0) {
            backButton.setEnabled(false);
        } else {
            backButton.setEnabled(true);
        }
    }

    /**
     * Checks if its last step and disables next button but adding a save button
     */
    public void checkLast() {
        saveButton = new JButton("Save");

        warning1 = new JLabel("WARNING: Local configuration already exists!");
        warning2 = new JLabel("Do you want to overwrite?");
        if (getStep() == params.length - 1) {
            nextButton.setEnabled(false);
            nextButton.setVisible(false);

            buttonPanel.add(saveButton);
            contentPanel.revalidate();

            saveButton.setPreferredSize(new java.awt.Dimension(74, 22));
            saveButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    setLocalValue();
                    showSaveScreen();
                    showWarningScreen();
                }
            });

        } else {
            nextButton.setEnabled(true);
            warning1.setVisible(false);
            warning2.setVisible(false);

            if (spGui != null && spGui.subFrame.isVisible()) {
                frame.setEnabled(false);
            }
        }
    }

    /**
     * Shows the normal options (paramaters)
     */
    public void showNormalOptions() {

        if (getStep() < params.length) {
            defaultValueTextField.setText(params[getStep()].getDefaultValue());
            localValueTextField.setText(params[getStep()].getLocalValue());
            paraValueLabel.setText(params[getStep()].getName());
            frame.pack();
        }
    }

    /**
     * Shows the save screen
     */
    private void showSaveScreen() {

        paraValueLabel.setVisible(false);
        defaultValueLabel.setVisible(false);
        localValueLabel.setVisible(false);
        newLocalValueLabel.setVisible(false);

        defaultValueTextField.setVisible(false);
        localValueTextField.setVisible(false);
        newLocalValueTextField.setVisible(false);

        radioButtonDefault.setVisible(false);
        radioButtonLocal.setVisible(false);
        radioButtonNewLocal.setVisible(false);
        cancelButton.setVisible(false);
        nextButton.setVisible(false);
        backButton.setVisible(false);
        saveButton.setVisible(false);

        yesButton.setVisible(true);
        noButton.setVisible(true);

        warning1.setVisible(true);
        warning2.setVisible(true);
    }

    /**
     * Shows the Warning screen
     */
    private void showWarningScreen() {
        contentPanel.add(warning1, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
                        0, 0, 0, 0), 0, 0));
        contentPanel.add(warning2, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
                        0, 0, 0, 0), 0, 0));
        yesButton.setText("yes");
        buttonPanel.add(yesButton);
        yesButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                showYesScreen();

            }
        });

        noButton.setText("no");
        buttonPanel.add(noButton);
        noButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                showNoScreen();

            }
        });
    }

    /**
     * Screen if Yes is pressed
     */
    private void showYesScreen() {

        // TODO: Why doesn't it work with the reference only?
        for (int k = 0; k < tmpParams.length; k++) {
            tmpBuildProperties.setParameter(tmpParams[k]);
        }
        tmpBuildProperties.writeLocal();

        saveMessage.setText("Successful saved to: " + rootDir
                + "/local.build.properties");
        contentPanel.add(saveMessage, new GridBagConstraints(2, 2, 1, 1, 0.0,
                0.0, GridBagConstraints.SOUTH, GridBagConstraints.PAGE_END,
                new Insets(0, 0, 0, 0), 0, 0));
        saveMessage.setVisible(true);

        yesButton.setVisible(false);
        noButton.setVisible(false);
        warning1.setVisible(false);
        warning2.setVisible(false);
        exitButton.setPreferredSize(new java.awt.Dimension(74, 22));
        exitButton.setText("Exit");
        contentPanel.add(exitButton, new GridBagConstraints(2, 3, 1, 1, 0.0,
                0.0, GridBagConstraints.SOUTH, GridBagConstraints.PAGE_END,
                new Insets(0, 0, 0, 0), 0, 0));
        exitButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {

                System.exit(0);
            }
        });

        contentPanel.revalidate();
        contentPanel.repaint();
        frame.pack();
    }

    /**
     * Screen if No is pressed
     */
    private void showNoScreen() {

        saveMessage.setVisible(false);
        paraValueLabel.setVisible(true);
        defaultValueLabel.setVisible(true);
        localValueLabel.setVisible(true);
        newLocalValueLabel.setVisible(true);
        defaultValueTextField.setVisible(true);
        localValueTextField.setVisible(true);
        newLocalValueTextField.setVisible(true);

        radioButtonDefault.setVisible(true);
        radioButtonLocal.setVisible(true);
        radioButtonNewLocal.setVisible(true);
        cancelButton.setVisible(true);
        nextButton.setVisible(false);

        backButton.setVisible(true);
        saveButton.setVisible(true);

        yesButton.setVisible(false);
        noButton.setVisible(false);

        saveMessage.setVisible(false);
        frame.pack();
    }

    /**
     * Method to create the Comboboxes
     */
    public void comboBox() {
        String[] availableValues = params[getStep()].getAvailableValues();

        if (availableValues != null && availableValues.length > 0) {

            defaultValueTextField.setVisible(false);
            localValueTextField.setVisible(false);
            newLocalValueTextField.setVisible(false);

            // TODO: not nice solved, we need to exclude subParams without
            // Values like WLS
            String[] labels = new String[availableValues.length - 1];
            for (int i = 0; i < availableValues.length; i++) {
                if (availableValues[i].equals("WLS")) {
                    continue;
                }
                labels[i] = availableValues[i];
            }

            defaultValueCTextField = new JTextField(params[getStep()]
                    .getDefaultValue());
            defaultValueCTextField.setPreferredSize(new java.awt.Dimension(204,
                    22));
            defaultValueCTextField.setEditable(false);

            // MouseListener takes care to place radio button
            defaultValueCTextField.addMouseListener(new MouseListener() {

                public void mouseClicked(MouseEvent event) {
                    radioButtonDefault.setSelected(true);
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
            // ActionListener which looks what is selected in the Dropdown list
            defaultValueCTextField.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent event) {
                    JComboBox cb = (JComboBox) event.getSource();
                    tmpParams[getStep()].setLocalValue(cb.getSelectedItem()
                            .toString());
                    subParams = params[getStep()].getSubsequentParameters(cb
                            .getSelectedItem().toString(), tmpBuildProperties);
                }
            });

            contentPanel.add(defaultValueCTextField, new GridBagConstraints(2,
                    1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

            localValueCTextField = new JTextField(params[getStep()]
                    .getLocalValue());
            localValueCTextField.setPreferredSize(new java.awt.Dimension(204,
                    22));
            localValueCTextField.setEditable(false);

            localValueCTextField.addMouseListener(new MouseListener() {

                public void mouseClicked(MouseEvent event) {
                    radioButtonLocal.setSelected(true);
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
            // ActionListener which looks what is selected in the Dropdown list
            localValueCTextField.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent event) {
                    JComboBox cb = (JComboBox) event.getSource();
                    tmpParams[getStep()].setLocalValue(cb.getSelectedItem()
                            .toString());
                    subParams = params[getStep()].getSubsequentParameters(cb
                            .getSelectedItem().toString(), tmpBuildProperties);
                }
            });
            contentPanel.add(localValueCTextField, new GridBagConstraints(2, 2,
                    1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

            newLocalValueComboBox = new JComboBox(labels);
            newLocalValueComboBox.setSelectedItem(tmpParams[getStep()].getLocalValue());

            newLocalValueComboBox.addMouseListener(new MouseListener() {

                public void mouseClicked(MouseEvent event) {
                    radioButtonNewLocal.setSelected(true);
                }

                public void mousePressed(MouseEvent event) {
                }

                public void mouseEntered(MouseEvent event) {
                }

                public void mouseExited(MouseEvent event) {
                }

                public void mouseReleased(MouseEvent event) {
                }
            });
            // ActionListener which looks what is selected in the Dropdown list
            newLocalValueComboBox.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent event) {
                    JComboBox cb = (JComboBox) event.getSource();
                    tmpParams[getStep()].setLocalValue(cb.getSelectedItem()
                            .toString());
                    subParams = params[getStep()].getSubsequentParameters(cb
                            .getSelectedItem().toString(), tmpBuildProperties);
                }
            });
            newLocalValueComboBox.setMaximumRowCount(availableValues.length);
            newLocalValueComboBox.setPreferredSize(new java.awt.Dimension(204,
                    22));
            contentPanel.add(newLocalValueComboBox, new GridBagConstraints(2,
                    3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

            contentPanel.revalidate();
        } else {
            defaultValueCTextField.setVisible(false);
            localValueCTextField.setVisible(false);
            newLocalValueComboBox.setVisible(false);
            defaultValueTextField.setVisible(true);
            localValueTextField.setVisible(true);
            newLocalValueTextField.setVisible(true);

            warning1.setVisible(false);
            warning2.setVisible(false);
        }
        frame.pack();
    }

    /**
     * Returns the current step
     * 
     * @return steps
     */
    public int getStep() {
        return steps;
    }
}
