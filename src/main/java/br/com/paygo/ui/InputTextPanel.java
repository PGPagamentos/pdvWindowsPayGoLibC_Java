package br.com.paygo.ui;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.text.ParseException;

class InputTextPanel extends JPanel {

    private JFormattedTextField textField;

    InputTextPanel(String message, String mask) {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        JLabel label = new JLabel("<html>" + message.replaceAll("\n", "<br/>") + "</html>");
        label.setAlignmentX(0);
        add(label);

        try {
            textField = new JFormattedTextField(new MaskFormatter(mask.replaceAll("@", "#")));
        } catch (ParseException e) {
            textField = new JFormattedTextField();
        }

        add(textField);
    }

    String getValue() {
        return textField.getText().replaceAll("[^\\d]", "");
    }
}
