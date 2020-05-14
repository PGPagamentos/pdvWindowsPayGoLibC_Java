package br.com.paygo.ui;

import br.com.paygo.PGWeb;
import br.com.paygo.enums.PWUserDataMessage;
import br.com.paygo.enums.PWPINPadFunction;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.sun.jna.ptr.LongByReference;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

class PinPadPanel extends JPanel {

    private final JComboBox<PWUserDataMessage> cbMensagem = new JComboBox<>(PWUserDataMessage.values());
    private final Integer[] cbOptions = {1,2,3,4,5,6,7,8,9,10,11,12};
    private final JComboBox cbMinimo = new JComboBox<>(cbOptions);
    private final JComboBox cbMaximo = new JComboBox<>(cbOptions);
    private ButtonGroup buttonGroup;
    private PWPINPadFunction currentFunction = null;

    PinPadPanel(PGWeb pgWeb) {
    	JPanel topPanel = new JPanel();
    	
        this.setLayout(new BorderLayout());
        buttonGroup = new ButtonGroup();
        
        /* Criação das opções para interação com o PIN-pad fora do fluxo transacional */
        for(PWPINPadFunction func : PWPINPadFunction.values()) {
        	JRadioButton rbtnAux = new JRadioButton(func.getFunctionName());
        	rbtnAux.addActionListener(new ActionListener() {
    			@Override
    			public void actionPerformed(ActionEvent e) {
    				currentFunction = func;    			
    			}
    		});
        	buttonGroup.add(rbtnAux);
        	topPanel.add(rbtnAux);
        }
        
        /* Possíveis parâmetros informados nas funções de PIN-pad */
        JPanel centerPanel = new JPanel(new GridLayout(4, 2, 20, 2));
        JTextField txtMensagem = new JTextField("#Mercado PayGo#");        
        
        centerPanel.add(new JLabel("Mensagem fixa"));
        centerPanel.add(new JLabel("Mensagem PIN-pad"));
        centerPanel.add(cbMensagem);
        centerPanel.add(txtMensagem);
        
        centerPanel.add(new JLabel("Tamanho Mínimo"));
        centerPanel.add(new JLabel("Tamanho Máximo"));
        centerPanel.add(cbMinimo);
        centerPanel.add(cbMaximo);

        /* Área de resultado e botão para executar as funções */
        JTextArea txtAreaLog = new JTextArea(5, 20);
        JScrollPane scrollPane = new JScrollPane(txtAreaLog);
        
        txtAreaLog.setEditable(false);
        txtAreaLog.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLACK), BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        txtAreaLog.setLineWrap(true);
        txtAreaLog.setWrapStyleWord(true);

        JButton aplicarButton = new JButton("Executar função");
        aplicarButton.addActionListener(e -> {
        	PWUserDataMessage constMsgPP = null;
        	
        	// Realiza tratamento para o tipo de função a ser executada
        	if(currentFunction == PWPINPadFunction.PW_iPPGetUserData) {
        		constMsgPP = PWUserDataMessage.valueOf(Objects.requireNonNull(cbMensagem.getSelectedItem()).toString());
        	}
        	else if (currentFunction == null) {
        		JOptionPane.showConfirmDialog(this, "Selecione uma função a ser executada.", "Atenção!", JOptionPane.DEFAULT_OPTION);
        		return;
        	}
        	
        	// Limpa área de log
        	txtAreaLog.setText(currentFunction+"");
        	
        	// Executa comandos da biblioteca.
            String response = pgWeb.executeNonTransacionFunction(
            		currentFunction, constMsgPP, (Integer)cbMinimo.getSelectedItem(), (Integer)cbMaximo.getSelectedItem(),
            		txtMensagem.getText(), new LongByReference(15)
            		);
            
            // Exibe retorno
            txtAreaLog.setText(response);
        });

        JPanel bottomPanel = new JPanel(new GridLayout(1, 2, 20, 20));
        bottomPanel.setBorder(new EmptyBorder(new Insets(10, 0, 10, 0)));
        
        bottomPanel.add(scrollPane);
        bottomPanel.add(aplicarButton);        

        this.add(BorderLayout.NORTH, topPanel);
        this.add(BorderLayout.CENTER, centerPanel);
        this.add(BorderLayout.SOUTH, bottomPanel);
    }
}
