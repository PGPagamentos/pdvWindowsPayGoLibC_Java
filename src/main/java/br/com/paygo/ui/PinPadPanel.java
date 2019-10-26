package br.com.paygo.ui;

import br.com.paygo.PGWeb;
import br.com.paygo.enums.PWData;
import br.com.paygo.enums.PWUserDataMessage;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Objects;

class PinPadPanel extends JPanel {

    private final JComboBox<PWUserDataMessage> cbMensagem = new JComboBox<>(PWUserDataMessage.values());
    private final Integer[] cbOptions = {1,2,3,4,5,6,7,8,9,10,11,12};
    private final JComboBox cbMinimo = new JComboBox<>(cbOptions);
    private final JComboBox cbMaximo = new JComboBox<>(cbOptions);
    private ButtonGroup buttonGroup;

    PinPadPanel(PGWeb pgWeb) {
        this.setLayout(new BorderLayout());

        buttonGroup = new ButtonGroup();

        JRadioButton getUserDataRB = new JRadioButton("PW_iPPGetUserData");
        getUserDataRB.setActionCommand(PWData.PPENTRY.toString());
        getUserDataRB.setSelected(true);
        getUserDataRB.addActionListener(e -> cbMensagem.setEnabled(true));

        JRadioButton getPINBlock = new JRadioButton("PW_iPPGetPINBlock");
        getPINBlock.setActionCommand(PWData.PPENCPIN.toString());
        getPINBlock.addActionListener(e -> cbMensagem.setEnabled(false));

        buttonGroup.add(getUserDataRB);
        buttonGroup.add(getPINBlock);

        JPanel topPanel = new JPanel();
        topPanel.add(getUserDataRB);
        topPanel.add(getPINBlock);


        JPanel centerPanel = new JPanel(new GridLayout(2, 3, 20, 2));
        centerPanel.add(new JLabel("Mensagem"));
        centerPanel.add(new JLabel("Tamanho Mínimo"));
        centerPanel.add(new JLabel("Tamanho Máximo"));
        centerPanel.add(cbMensagem);
        centerPanel.add(cbMinimo);
        centerPanel.add(cbMaximo);

        JLabel labelRetorno = new JLabel("Aguardando...");

        JButton aplicarButton = new JButton("Aplicar");
        aplicarButton.addActionListener(e -> {
            PWData data = PWData.valueOf(buttonGroup.getSelection().getActionCommand());
            PWUserDataMessage message = PWUserDataMessage.valueOf(Objects.requireNonNull(cbMensagem.getSelectedItem()).toString());

            String response = pgWeb.requestDataOnPinPad(data, message, (Integer)cbMinimo.getSelectedItem(), (Integer)cbMaximo.getSelectedItem());
            labelRetorno.setText("RETORNO: " + response);
        });

        JPanel bottomPanel = new JPanel(new GridLayout(1, 3));
        bottomPanel.setBorder(new EmptyBorder(new Insets(20, 0, 10, 0)));
        bottomPanel.add(aplicarButton);
        bottomPanel.add(new JLabel());
        bottomPanel.add(labelRetorno);

        this.add(BorderLayout.NORTH, topPanel);
        this.add(BorderLayout.CENTER, centerPanel);
        this.add(BorderLayout.SOUTH, bottomPanel);
    }
}
