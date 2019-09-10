package br.com.paygo.ui;

import br.com.paygo.PGWeb;
import br.com.paygo.enums.PWInfo;
import br.com.paygo.enums.PWOper;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SwingInterface implements UserInterface {

    private final Map<PWOper, String> operations = new HashMap<PWOper, String>() {{
        put(PWOper.INSTALL, "Instalação");
        put(PWOper.SALE, "Venda");
    }};
    private final JFrame applicationWindow = new JFrame();
    private final JTextArea logArea = new JTextArea();
    private final ParamPanel paramPanel = new ParamPanel();

    private JList paramList = new JList();
    private HashMap<PWInfo, String> params = new HashMap<>();
    private DefaultListModel<String> listModel = new DefaultListModel<>();

    private PGWeb pgWeb;

    public SwingInterface() {
        pgWeb = new PGWeb(this);

        applicationWindow.setTitle("PGWebLib");
        applicationWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        applicationWindow.setSize(800, 500);
        applicationWindow.setLayout(new GridLayout());
        setupWindow();

        logArea.setEditable(false);
        applicationWindow.setVisible(true);

        init();
    }

    private void setupWindow() {
        JPanel leftPanel = setupLeftPanel();
        JPanel rightPanel = setupRightPanel();

        applicationWindow.getContentPane().add(leftPanel);
        applicationWindow.getContentPane().add(rightPanel);
    }

    private JPanel setupLeftPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("OPERAÇÃO:"));

        JComboBox comboBox = new JComboBox(operations.values().toArray());
        comboBox.setPreferredSize(new Dimension(200, 30));
        topPanel.add(comboBox);

        JPanel centerPanel = new JPanel();
        centerPanel.add(new JLabel("PARÂMETROS:"));

        JButton addParamButton = new JButton("+");

        addParamButton.addActionListener(e -> {
            int code = JOptionPane.showConfirmDialog(null, paramPanel, "Selecione um parâmetro:", JOptionPane.OK_CANCEL_OPTION);

            if (code == 0) {
                PWInfo param = PWInfo.valueOf(paramPanel.getSelectedItem());
                String value = paramPanel.getText();

                if (value.isEmpty()) {
                    JOptionPane.showMessageDialog(applicationWindow, "Valor do parâmetro é inválido.");
                } else if (params.containsKey(param)) {
                    JOptionPane.showMessageDialog(applicationWindow, "O parâmetro '" + param + "' já foi adicionado.");
                } else {
                    params.put(param, paramPanel.getText());

                    String listElement = param.name() + "(" + param.getValue() + "): " + paramPanel.getText();
                    listModel.addElement(listElement);
                    paramPanel.clear();
                }
            }
        });

        JButton removeParamButton = new JButton("-");

        removeParamButton.addActionListener(e -> {
            int selectedItem = paramList.getSelectedIndex();

            if (selectedItem != -1) {
                listModel.remove(selectedItem);
            }
        });

        centerPanel.add(addParamButton);
        centerPanel.add(removeParamButton);

        paramList.setModel(listModel);
        paramList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        paramList.setLayoutOrientation(JList.VERTICAL);
        paramList.setSelectedIndex(-1);

        JScrollPane scrollParamList = new JScrollPane(paramList);
        scrollParamList.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollParamList.setPreferredSize(new Dimension(380, 300));

        centerPanel.add(scrollParamList);

        JPanel bottomPanel = new JPanel();

        JButton clearParamsButton = new JButton("Limpar");
        clearParamsButton.addActionListener(e -> listModel.removeAllElements());

        JButton executeButton = new JButton("Executar");

        bottomPanel.add(clearParamsButton);
        bottomPanel.add(new JButton("Capturar dados usando PIN-pad"));
        bottomPanel.add(executeButton);

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

        DefaultCaret caret = (DefaultCaret)logArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

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
    public void sale() {
        pgWeb.sale();
    }

    @Override
    public void abort() {
        logInfo("\n\n EXECUÇÃO ABORTADA PELO USUÁRIO! \n\n");
        pgWeb.abort();
    }

    @Override
    public String requestParam(String title, String message, String mask) {
        InputTextPanel inputPanel = new InputTextPanel(message, mask);
        int code = JOptionPane.showConfirmDialog(applicationWindow, inputPanel, title, JOptionPane.OK_CANCEL_OPTION);

        if (code == JOptionPane.OK_OPTION) {
            return inputPanel.getValue();
        } else if (code == JOptionPane.CANCEL_OPTION) {
            abort();
        }

        return null;
    }

    @Override
    public int requestSelection(String title, ArrayList<String> options) {
        RadioGroupPanel radioGroupPanel = new RadioGroupPanel(options);
        int code = JOptionPane.showConfirmDialog(applicationWindow, radioGroupPanel, title, JOptionPane.DEFAULT_OPTION);

        if (code == JOptionPane.OK_OPTION) {
            return radioGroupPanel.getSelectedOption();
        }

        return 0;
    }

    @Override
    public void alert(String message) {
        JOptionPane.showMessageDialog(applicationWindow, message);
    }

    @Override
    public void logInfo(String message) {
        logArea.append(message + "\n");
    }

    @Override
    public void showException(String message, boolean terminateApplication) {
        int result = JOptionPane.showConfirmDialog(applicationWindow, message, "Close Application", JOptionPane.DEFAULT_OPTION);

        if (result == 0 && terminateApplication) {
            System.exit(-1);
        }
    }

    @Override
    public HashMap<PWInfo, String> getParams() {
        return this.params;
    }
}
