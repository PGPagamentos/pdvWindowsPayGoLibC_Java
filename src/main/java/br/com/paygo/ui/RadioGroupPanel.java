package br.com.paygo.ui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

class RadioGroupPanel extends JPanel {

    private ButtonGroup buttonGroup;

    RadioGroupPanel(ArrayList<String> options) {
        this.setLayout(new GridLayout(0, 1));
        buttonGroup = new ButtonGroup();
        int index = 0;

        for (String option : options) {
            JRadioButton radioButton = new JRadioButton(option);
            radioButton.setActionCommand(String.valueOf(index));
            radioButton.setSelected(index == 0);

            index++;

            buttonGroup.add(radioButton);
            this.add(radioButton);
        }
    }

    int getSelectedOption() {
        return Integer.parseInt(buttonGroup.getSelection().getActionCommand());
    }
}
