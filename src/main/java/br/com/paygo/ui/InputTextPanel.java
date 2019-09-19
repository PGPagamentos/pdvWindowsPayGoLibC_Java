package br.com.paygo.ui;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;

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
                MaskFormatter maskFormatter = new MaskFormatter(mask.replaceAll("@", "#"));
                maskFormatter.setOverwriteMode(true);

                formattedTextField = new JFormattedTextField(maskFormatter);
                formattedTextField.setMargin(new Insets(5, 2, 5, 2));
                formattedTextField.setFocusLostBehavior(JFormattedTextField.PERSIST);
                formattedTextField.grabFocus();

                formattedTextField.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                formattedTextField.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                formattedTextField.setCaretPosition(0);

                add(formattedTextField);
            } catch (Exception e) {
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
