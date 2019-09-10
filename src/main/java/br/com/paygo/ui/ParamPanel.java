package br.com.paygo.ui;

import br.com.paygo.enums.PWInfo;

import javax.swing.*;

class ParamPanel extends JPanel {

    private final JComboBox<PWInfo> comboBox = new JComboBox<>(PWInfo.values());
    private final JTextField textField = new JTextField();

    ParamPanel() {
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        this.add(comboBox);

        JLabel label = new JLabel("Valor do Parâmetro:");
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
