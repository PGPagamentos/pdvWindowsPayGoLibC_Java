package br.com.paygo.ui;

import br.com.paygo.enums.PWInfo;

import javax.swing.*;
import java.awt.*;

class ParamPanel extends JPanel {

    private final JComboBox<PWInfo> comboBox = new JComboBox<>(PWInfo.values());
    private final JTextField textField = new JTextField();

    ParamPanel() {
        this.setLayout(new GridLayout(3, 1, 0, 1));

        JLabel label = new JLabel("Valor do Parâmetro:");

        textField.setMargin(new Insets(5, 2, 5, 2));

        this.add(comboBox);
        this.add(label);
        this.add(textField);
    }

    String getSelectedItem() {
        return comboBox.getSelectedItem().toString();
    }

    String getText() {
        return textField.getText();
    }

    void clear() {
        comboBox.setSelectedIndex(0);
        textField.setText("");
    }

}
