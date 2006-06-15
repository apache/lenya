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
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.apache.lenya.config.core.ContextEventQueue;
import org.apache.lenya.config.core.Parameter;

public class SubParamsGUI {

    private ConfigureGUI cGui;
    protected JButton nextSubButton;
    protected JFrame subFrame;
    private JPanel contentPanel;
    private JPanel checkBoxPanel;
    private JPanel buttonPanel;
    private JLabel defaultValueLabel;
    private JLabel localValueLabel;
    private JLabel newLocalValueLabel;
    private JLabel stepsLabel;
    private JTextField defaultValueTextField;
    private JTextField localValueTextField;
    private JTextField newLocalValueTextField;
    private JRadioButton radioButtonDefault;
    private JRadioButton radioButtonLocal;
    private JRadioButton radioButtonNewLocal;
    private JButton closeButton;
    private JButton backButton;
    private JButton nextButton;
    private JButton doneButton;
    protected JCheckBox[] checkBoxes;
    private JLabel paraValueLabel;
    private Parameter[] subParams;
    private Parameter[] tmpSubParams;
    private String selectedComboBoxValue;
    int steps = 0;

    /**
     * 
     * @param cGui
     * @param tmpSubParams
     * @param subParams
     */
    public SubParamsGUI(ConfigureGUI cGui, Parameter[] tmpSubParams,
            Parameter[] subParams, String selectedComboBoxValue) {

        this.cGui = cGui;
        this.subParams = subParams;
        this.tmpSubParams = tmpSubParams;
        this.selectedComboBoxValue = selectedComboBoxValue;
        
        // clearing the second temporary array
        for (int i = 0; i < tmpSubParams.length; i++) {
            tmpSubParams[i].setLocalValue("");
        }
        createSubParamsGui();
    }

    public void createSubParamsGui() {

        Toolkit.getDefaultToolkit().getSystemEventQueue().push(
                new ContextEventQueue());

        subFrame = new JFrame("Apache Subparameter \"" + selectedComboBoxValue
                + "\" Configuration");
        subFrame.setLocation(cGui.frame.getX() + 0, cGui.frame.getY() + 0);

        contentPanel = new JPanel();
        checkBoxPanel = new JPanel();
        buttonPanel = new JPanel();

        defaultValueLabel = new JLabel();
        localValueLabel = new JLabel();
        newLocalValueLabel = new JLabel();

        defaultValueTextField = new JTextField(20);
        localValueTextField = new JTextField(20);
        newLocalValueTextField = new JTextField(20);

        radioButtonDefault = new JRadioButton();
        radioButtonLocal = new JRadioButton();
        radioButtonNewLocal = new JRadioButton();
        ButtonGroup g = new ButtonGroup();

        closeButton = new JButton();
        backButton = new JButton();
        nextButton = new JButton();
        doneButton = new JButton();

        Container contentPane = subFrame.getContentPane();
        contentPane.setLayout(new FlowLayout(FlowLayout.LEFT));

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

        checkBoxes = new JCheckBox[subParams.length];

        for (int i = 0; i < subParams.length; ++i) {
            checkBoxes[i] = new JCheckBox();
            checkBoxes[i].setEnabled(false);
            checkBoxes[i].setText(subParams[i].getName());
            checkBoxes[0].setSelected(true);
            checkBoxPanel.add(checkBoxes[i]);
        }

        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 1;
        paraValueLabel = new JLabel(subParams[0].getName());
        paraValueLabel.repaint();
        contentPanel.add(paraValueLabel, c);

        defaultValueLabel.setText("Default Value:");
        contentPanel.add(defaultValueLabel, new GridBagConstraints(1, 1, 1, 1,
                0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        contentPanel.add(defaultValueTextField, new GridBagConstraints(2, 1, 1,
                1, 0.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        defaultValueTextField.setText(subParams[0].getDefaultValue());
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
        localValueTextField.setText(subParams[0].getLocalValue());
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

        closeButton.setText("Close");
        closeButton.setPreferredSize(new java.awt.Dimension(74, 22));
        closeButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                subFrame.setVisible(false);
                cGui.frame.setEnabled(true);
                cGui.frame.setVisible(true);
            }
        });

        backButton.setText("<Back");
        backButton.setPreferredSize(new java.awt.Dimension(74, 22));
        backButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
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

        buttonPanel.add(closeButton);
        buttonPanel.add(backButton);
        buttonPanel.add(nextButton);

        contentPanel.add(buttonPanel, new GridBagConstraints(1, 4, 6, 0, 0.0,
                0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));

        subFrame.addWindowListener(new WindowListener() {

            public void windowOpened(WindowEvent e) {
            }

            public void windowClosing(WindowEvent e) {
                cGui.frame.setEnabled(true);
            }

            public void windowClosed(WindowEvent e) {
                cGui.frame.setEnabled(true);
            }

            public void windowIconified(WindowEvent e) {
            }

            public void windowDeiconified(WindowEvent e) {
            }

            public void windowActivated(WindowEvent e) {
            }

            public void windowDeactivated(WindowEvent e) {
            }
        });

        contentPane.add(contentPanel);
        contentPanel.revalidate();
        subFrame.pack();
        subFrame.setVisible(true);
    }

    /**
     * Controls behavior if back button is pressed
     */
    public void moveBack() {
        steps--;
        subFrame.repaint();
        nextButton.setVisible(true);
        doneButton.setVisible(false);
        checkFirst();
        currentStep("down");
        showNormalOptions();
        checkLast();
        newLocalValueTextField.setText(tmpSubParams[getStep()].getLocalValue());
        setRadioButton();
    }

    /**
     * Controls behavior if next button is pressed
     */
    public void moveNext() {
        setLocalValue();
        steps++;
        subFrame.repaint();
        checkFirst();
        currentStep("up");
        showNormalOptions();
        checkLast();
        newLocalValueTextField.setText(tmpSubParams[getStep()].getLocalValue());
        setRadioButton();
    }

    /**
     * Set radio button
     */
    public void setRadioButton() {
        if (tmpSubParams[getStep()].getLocalValue() != "") {
            radioButtonNewLocal.setSelected(true);
        } else if (subParams[getStep()].getLocalValue() != "") {
            radioButtonLocal.setSelected(true);
        } else {
            radioButtonDefault.setSelected(true);
        }
    }

    /**
     * Set local value depending on chosen value
     */
    public void setLocalValue() {

        if (radioButtonDefault.isSelected()) {
            tmpSubParams[getStep()].setLocalValue(defaultValueTextField
                    .getText());
        } else if (radioButtonLocal.isSelected()) {
            tmpSubParams[getStep()]
                    .setLocalValue(localValueTextField.getText());
        } else if (radioButtonNewLocal.isSelected()) {
            tmpSubParams[getStep()].setLocalValue(newLocalValueTextField
                    .getText());
        } else {
            System.err.println("Fatal Error 0123456789!");
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
        doneButton = new JButton("Done");

        if (getStep() == subParams.length - 1) {
            nextButton.setEnabled(false);
            nextButton.setVisible(false);

            buttonPanel.add(doneButton);
            contentPanel.revalidate();

            doneButton.setPreferredSize(new java.awt.Dimension(74, 22));
            doneButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    setLocalValue();
                    subFrame.setVisible(false);
                    cGui.frame.setEnabled(true);
                    cGui.frame.setVisible(true);
                }
            });

        } else {
            nextButton.setEnabled(true);
        }
    }

    /**
     * Shows the normal options (paramaters)
     */
    public void showNormalOptions() {

        if (getStep() < subParams.length) {

            defaultValueTextField.setText(subParams[getStep()]
                    .getDefaultValue());
            localValueTextField.setText(subParams[getStep()].getLocalValue());
            paraValueLabel.setText(subParams[getStep()].getName());
            subFrame.pack();
        }

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
