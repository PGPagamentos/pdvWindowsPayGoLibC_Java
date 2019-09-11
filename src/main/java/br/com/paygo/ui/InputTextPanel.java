package br.com.paygo.ui;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.text.ParseException;

class InputTextPanel extends JPanel {

    private JTextField textField;
    private JFormattedTextField formattedTextField;
    private String mask;

    InputTextPanel(String message, String mask) {
        this.mask = mask;
        setLayout(new GridLayout(2, 1, 0,2));

        JLabel label = new JLabel("<html>" + message.replaceAll("\n", "<br/>") + "</html>");

        add(label);

        if(!mask.equals("")) {
            try {
                formattedTextField = new JFormattedTextField(new MaskFormatter(mask.replaceAll("@", "#")));
                formattedTextField.setMargin(new Insets(5, 2, 5, 2));
                add(formattedTextField);
            } catch (ParseException e) {
                addSimpleTextField();
            }
        } else {
            addSimpleTextField();
        }
    }

    String getValue() {
        if (!mask.equals("") && textField == null) {
            return formattedTextField.getText().replaceAll("[^\\d]", "");
        }
        return textField.getText();
    }

    private void addSimpleTextField() {
        textField = new JTextField();
        textField.setMargin(new Insets(5, 2, 5, 2));
        add(textField);
    }
}
