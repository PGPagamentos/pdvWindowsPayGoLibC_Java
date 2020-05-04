package br.com.paygo.ui;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.Arrays;

class InputTextPanel extends JPanel {

    private static final String[] currencyMasks = new String[] {
        "R$@.@@@.@@@,@@",
        "@.@@@.@@@.@@@,@@",
        "@@@.@@@,@@"
    };

    private JTextField textField;
    private JPasswordField passwordField;
    private ABMTextField currencyField;
    private JFormattedTextField formattedTextField;
    private String mask;
    private byte passwordCapture;

    InputTextPanel(String message, String mask, String initialValue, byte passwordCapture) {
        this.mask = mask;
        this.passwordCapture = passwordCapture;

        setLayout(new GridLayout(2, 1, 0,2));

        JLabel label = new JLabel("<html>" + message.replaceAll("\n", "<br/>") + "</html>");

        add(label);

        if(!mask.equals("")) {
            try {
                if (Arrays.asList(currencyMasks).contains(mask)) {
                    DecimalFormat format = new DecimalFormat("R$#,###,##0.00");
                    currencyField = new ABMTextField(format);

                    add(currencyField);
                } else {
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
                }
            } catch (Exception e) {
                addSimpleTextField(initialValue);
            }
        } else {
            if (passwordCapture == 0) {
                addSimpleTextField(initialValue);
            } else {
                addSimplePasswordField(initialValue);
            }
        }
    }

    String getValue() {
        if (!mask.equals("") && textField == null) {

            if (Arrays.asList(currencyMasks).contains(mask)) {
                return currencyField.getText().replaceAll("[^\\d]", "");
            }

            return formattedTextField.getText().replaceAll("[^\\d]", "");
        }
        if (passwordCapture == 0) {
            return textField.getText();
        }
        else {
            return passwordField.getText();
        }

    }

    private void addSimpleTextField(String initialValue) {
        textField = new JTextField();
        textField.setMargin(new Insets(5, 2, 5, 2));
        textField.setText(initialValue);
        add(textField);
    }

    private void addSimplePasswordField(String initialValue) {
        passwordField = new JPasswordField();
        passwordField.setMargin(new Insets(5, 2, 5, 2));
        passwordField.setText(initialValue);
        add(passwordField);
    }
}
