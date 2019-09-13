package br.com.paygo.ui;

import br.com.paygo.PGWeb;
import br.com.paygo.enums.PWInfo;
import br.com.paygo.enums.PWOper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class SwingInterface implements UserInterface {

    private final Map<PWOper, String> operations = new LinkedHashMap<PWOper, String>() {{
        put(PWOper.VERSION, "Versão da DLL");
        put(PWOper.INSTALL, "Instalação");
        put(PWOper.SALE, "Venda");
        put(PWOper.REPRINT, "Reimpressão");
        put(PWOper.SALEVOID, "Cancelamento de Venda");
        put(PWOper.PNDCNF, "Verifica Confirmação Pendente");
        put(PWOper.RPTTRUNC, "Relatório Sintético");
        put(PWOper.RPTDETAIL, "Relatório Detalhado");
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
        logArea.setEditable(false);
        setupWindow();
        init();
    }

    private void setupWindow() {
        JPanel leftPanel = setupLeftPanel();
        JPanel rightPanel = setupRightPanel();

        applicationWindow.setTitle("PGWebLib");
        applicationWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        applicationWindow.setLayout(new GridLayout());
        applicationWindow.getContentPane().add(leftPanel);
        applicationWindow.getContentPane().add(rightPanel);
        applicationWindow.pack();
        applicationWindow.setVisible(true);
    }

    private JPanel setupLeftPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // TOP PANEL
        JComboBox operationsSelector = new JComboBox(operations.values().toArray());
        ((JLabel) operationsSelector.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(1, 2));
        topPanel.setBorder(new EmptyBorder(10, 10, 5, 10));
        topPanel.add(new JLabel("OPERAÇÃO:"));
        topPanel.add(operationsSelector);

        // CENTER PANEL
        JButton addParamButton = new JButton("Adicionar");
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

        JButton removeParamButton = new JButton("Remover");
        removeParamButton.addActionListener(e -> {
            int selectedIndex = paramList.getSelectedIndex();
            String operValue = ((String)paramList.getSelectedValue()).replaceAll("\\(.*$", "");

            if (selectedIndex != -1) {
                listModel.remove(selectedIndex);
                PWInfo oper = PWInfo.valueOf(operValue);
                params.keySet().remove(oper);
            }
        });

        paramList.setModel(listModel);
        paramList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        paramList.setLayoutOrientation(JList.VERTICAL);
        paramList.setSelectedIndex(-1);

        JScrollPane scrollParamList = new JScrollPane(paramList);
        scrollParamList.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollParamList.setPreferredSize(new Dimension(380, 300));

        JPanel centerTopPanel = new JPanel();
        centerTopPanel.setLayout(new GridLayout(1, 4, 5, 0));
        centerTopPanel.setBorder(new EmptyBorder(0, 0, 5, 0));
        centerTopPanel.add(new JLabel("PARÂMETROS:"));
        centerTopPanel.add(new JLabel());
        centerTopPanel.add(addParamButton);
        centerTopPanel.add(removeParamButton);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());
        centerPanel.setBorder(new EmptyBorder(5, 10, 5, 10));
        centerPanel.add(BorderLayout.NORTH, centerTopPanel);
        centerPanel.add(BorderLayout.CENTER, scrollParamList);

        // BOTTOM PANEL
        JButton clearParamsButton = new JButton("Limpar");
        clearParamsButton.addActionListener(e -> {
            listModel.removeAllElements();
            params.clear();
        });

        JButton executeButton = new JButton("Executar");
        executeButton.addActionListener(e -> {
            String selectedOperation = operationsSelector.getSelectedItem().toString();

            PWOper operation = operations.entrySet().stream().filter(entry -> entry.getValue().equals(selectedOperation)).map(Map.Entry::getKey).findFirst().orElse(PWOper.INSTALL);

            switch (operation) {
                case INSTALL:
                    install();
                    break;
                case VERSION:
                    version();
                    break;
                case SALE:
                    sale();
                    break;
                case REPRINT:
                    reprint();
                    break;
                case SALEVOID:
                    saleVoid();
                    break;
                case PNDCNF:
                    checkPendingConfirmation();
                    break;
                case RPTTRUNC:
                    reportTrunc();
                    break;
                case RPTDETAIL:
                    reportDetail();
                    break;
                default:
                    showException("Operação não encontrada!", false);
            }
        });

        JButton pinPadButton = new JButton("Capturar dados usando PIN-pad");

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBorder(new EmptyBorder(5, 0, 10, 0));
        bottomPanel.add(clearParamsButton);
        bottomPanel.add(pinPadButton);
        bottomPanel.add(executeButton);

        panel.add(BorderLayout.NORTH, topPanel);
        panel.add(BorderLayout.CENTER, centerPanel);
        panel.add(BorderLayout.SOUTH, bottomPanel);

        return panel;
    }

    private JPanel setupRightPanel() {

        JButton clearLogButton = new JButton("Limpar Log");
        clearLogButton.addActionListener(e -> logArea.setText(""));

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(1, 2));
        topPanel.setBorder(new EmptyBorder(10, 10, 5, 10));

        topPanel.add(new JLabel("LOG:"));
        topPanel.add(clearLogButton);

        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);

        JScrollPane scroll = new JScrollPane(logArea);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.setPreferredSize(new Dimension(385, 375));

        DefaultCaret caret = (DefaultCaret)logArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        JPanel scrollWrapper = new JPanel();
        scrollWrapper.add(scroll);
        scrollWrapper.setBorder(new EmptyBorder(0, 5, 10, 5));

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(BorderLayout.NORTH, topPanel);
        panel.add(BorderLayout.CENTER, scrollWrapper);

        return panel;
    }

    @Override
    public void init() {
        pgWeb.init();
    }

    @Override
    public void version() {
        pgWeb.version();
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
    public void reprint() {
        pgWeb.reprint();
    }

    @Override
    public void saleVoid() {
        pgWeb.saleVoid();
    }

    @Override
    public void checkPendingConfirmation() {
        pgWeb.checkPendingConfirmation();
    }

    @Override
    public void reportTrunc() {
        pgWeb.reportTrunc();
    }

    @Override
    public void reportDetail() {
        pgWeb.reportDetail();
    }

    @Override
    public void abort() {
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
        } else if(code == JOptionPane.CANCEL_OPTION) {
            abort();
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
