package br.com.paygo.gui;

import br.com.paygo.PGWeb;
import br.com.paygo.enums.PWInfo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SwingInterface implements UserInterface {

    private final JFrame applicationWindow = new JFrame();
    private final JTextArea logArea = new JTextArea();
    private final JTextArea paramArea = new JTextArea(15, 30);
    private final JPanel paramPanel = new JPanel();
    private final JComboBox selectParam = new JComboBox(PWInfo.values());
    private final JTextField paramValue = new JTextField();

    private PGWeb pgWeb;

    public SwingInterface() {
        pgWeb = new PGWeb(this);

        applicationWindow.setTitle("PGWebLib");
        applicationWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        applicationWindow.setSize(800, 500);
        applicationWindow.setLayout(new GridLayout());
        setupWindow();

        logArea.setEditable(false);
        paramArea.setEditable(false);
        applicationWindow.setVisible(true);
    }

    private void setupWindow() {
        JPanel leftPanel = setupLeftPanel();
        JPanel rightPanel = setupRightPanel();

        setupParamPanel();

        applicationWindow.getContentPane().add(leftPanel);
        applicationWindow.getContentPane().add(rightPanel);
    }

    private void setupParamPanel() {
        paramPanel.setLayout(new BoxLayout(paramPanel, BoxLayout.PAGE_AXIS));
        paramPanel.add(selectParam);
        paramPanel.add(new JLabel("Valor do parâmetro:"));
        paramPanel.add(paramValue);
    }

    private JPanel setupLeftPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("OPERAÇÃO:"));

        JComboBox comboBox = new JComboBox(new String[] {"Instalação", "Venda", "Confirmação"});
        comboBox.setPreferredSize(new Dimension(200, 30));
        topPanel.add(comboBox);

        JPanel centerPanel = new JPanel();
        centerPanel.add(new JLabel("PARÂMETROS:"));

        JButton addParamButton = new JButton("+");

        addParamButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int code = JOptionPane.showConfirmDialog(null, paramPanel, "Selecione um parâmetro:", JOptionPane.OK_CANCEL_OPTION);

                if (code == 0) {
                    addParam(PWInfo.valueOf(selectParam.getSelectedItem().toString()), paramValue.getText());
                }
            }
        });

        centerPanel.add(addParamButton);

        centerPanel.add(paramArea);

        JPanel bottomPanel = new JPanel();

        JButton clearParamsButton = new JButton("Limpar");
        clearParamsButton.addActionListener(e -> paramArea.setText(""));

        centerPanel.add(clearParamsButton);
        centerPanel.add(new JButton("Capturar dados usando PIN-pad"));
        centerPanel.add(new JButton("Executar"));

        panel.add(BorderLayout.NORTH, topPanel);
        panel.add(BorderLayout.CENTER, centerPanel);
        panel.add(BorderLayout.SOUTH, bottomPanel);

        return panel;
    }

    private JPanel setupRightPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.add(BorderLayout.WEST, new JLabel("LOG:"));

        JButton clearLogButton = new JButton("LIMPAR");

        clearLogButton.addActionListener(e -> logArea.setText(""));

        topPanel.add(BorderLayout.EAST, clearLogButton);
        panel.add(topPanel);

        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);

        JScrollPane scroll = new JScrollPane(logArea);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.setPreferredSize(new Dimension(390, 400));

        panel.add(scroll);

        return panel;
    }


    @Override
    public void init() {
        pgWeb.init();
    }

    @Override
    public void install() {
        pgWeb.install();
    }

    @Override
    public void requestParam() {

    }

    @Override
    public void addParam(PWInfo param, String value) {
        pgWeb.addParam(param, value);
    }

    @Override
    public void logInfo(String message) {
        logArea.append(message + "\n");
    }

    @Override
    public void logParam(PWInfo param, String value) {
        this.paramArea.append(param.name() + "(" + param.getValue() + "): " + value + "\n");
    }

    @Override
    public void showException(String message, boolean terminateApplication) {
        int result = JOptionPane.showConfirmDialog(applicationWindow, message, "Close Application", JOptionPane.DEFAULT_OPTION);

        if (result == 0 && terminateApplication) {
            System.exit(-1);
        }
    }
}
