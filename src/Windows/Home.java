package Windows;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class Home extends JFrame {
    DefaultTableModel model;
    JTextField nameField;
    JTextField amountField;
    JComboBox<String> typeBox;
    File data;
    JTable table;
    public Home() throws IOException {
        super("LaundroMate");
        data = new File("./data/data.txt");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JTabbedPane tabbedPane = new JTabbedPane();
        setSize(800, 600);
        setResizable(false);
        JPanel AdminPanel = new JPanel();
        tabbedPane.addTab("洗衣项目信息", AdminPanel);
        JPanel businessPanel = new JPanel();
        tabbedPane.addTab("洗衣业务信息", businessPanel);
        JPanel customerPanel = new JPanel();
        tabbedPane.addTab("客户信息", customerPanel);
        getContentPane().add(tabbedPane);
        setLocationRelativeTo(null);
        setVisible(true);
        //洗衣项目信息管理的一堆控件
        JPanel ModifyPanel = new JPanel();
        ModifyPanel.setLayout(new BorderLayout());
        model = new DefaultTableModel();
        model.addColumn("项目名称");
        model.addColumn("添加日期");
        model.addColumn("项目金额");
        model.addColumn("项目类型");
        try {
            Scanner scanner = new Scanner(data);
            while (scanner.hasNextLine()) {
                String[] rowData = scanner.nextLine().split(",");
                model.addRow(rowData);
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        ModifyPanel.add(scrollPane, BorderLayout.CENTER);
        JPanel inputPanel = new JPanel(new FlowLayout());
        nameField = new JTextField(10);
        inputPanel.add(new JLabel("项目名称:"));
        inputPanel.add(nameField);
        amountField = new JTextField(10);
        inputPanel.add(new JLabel("项目金额:"));
        inputPanel.add(amountField);
        File typeFile = new File("./data/types.txt");
        BufferedReader br = new BufferedReader(new FileReader(typeFile));
        String[] types = new String[]{""}; // 更改为你的衣物种类
        types = br.readLine().split(",");
        typeBox = new JComboBox<>(types);
        inputPanel.add(new JLabel("项目类型:"));
        inputPanel.add(typeBox);
        JButton addButton = new JButton("添加");
        addButton.addActionListener(new AddButtonListener());
        inputPanel.add(addButton);
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                    nameField.setText(table.getValueAt(table.getSelectedRow(), 0).toString());
                    amountField.setText(table.getValueAt(table.getSelectedRow(), 2).toString());
                    typeBox.setSelectedItem(table.getValueAt(table.getSelectedRow(), 3));
                }
            }
        });
        JButton deleteButton = new JButton("删除");
        deleteButton.addActionListener(new DeleteButtonListener());
        inputPanel.add(deleteButton);
        ModifyPanel.add(inputPanel, BorderLayout.SOUTH);
        AdminPanel.setLayout(new BorderLayout());
        AdminPanel.add(ModifyPanel, BorderLayout.CENTER);
        // 下面是菜单栏相关的 别弄混了
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("文件");
        JMenuItem saveMenuItem = new JMenuItem("保存");
        saveMenuItem.addActionListener(new SaveMenuItemListener());
        fileMenu.add(saveMenuItem);
        JMenuItem searchMenuItem = new JMenuItem("查询");
        fileMenu.add(searchMenuItem);
        searchMenuItem.addActionListener(new SearchActionListener(table, model));
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);
        revalidate();
        repaint();
    }
    class AddButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String name = nameField.getText();
            String amount = amountField.getText();
            String type = (String) typeBox.getSelectedItem();
            if (name.isEmpty() || amount.isEmpty() || type.isEmpty()) {
                JOptionPane.showMessageDialog(null, "请确保所有字段都已填写", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            model.addRow(new Object[]{name, date, amount, type});
            nameField.setText("");
            amountField.setText("");
        }
    }
    class DeleteButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                model.removeRow(selectedRow);
            }
        }
    }
    class SaveMenuItemListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(data));
                for (int i = 0; i < model.getRowCount(); i++) {
                    if (model.getValueAt(i, 0).toString().isEmpty() || model.getValueAt(i, 1).toString().isEmpty() ||
                            model.getValueAt(i, 2).toString().isEmpty() || model.getValueAt(i, 3).toString().isEmpty()) {
                        JOptionPane.showMessageDialog(null, "请确保所有字段都已填写", "错误", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    writer.write(model.getValueAt(i, 0) + "," + model.getValueAt(i, 1) + ","
                            + model.getValueAt(i, 2) + "," + model.getValueAt(i, 3));
                    writer.newLine();
                }
                writer.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    class SearchActionListener implements ActionListener {
        private final JTable table;
        private final DefaultTableModel model;

        public SearchActionListener(JTable table, DefaultTableModel model) {
            this.table = table;
            this.model = model;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JPanel searchInputPanel = new JPanel(new GridLayout(0, 1));
            JTextField searchField = new JTextField();
            searchInputPanel.add(new JLabel("输入查询条件："));
            searchInputPanel.add(searchField);

            JTextField lowerBoundField = new JTextField();
            JTextField upperBoundField = new JTextField();
            searchInputPanel.add(new JLabel("输入金额下限："));
            searchInputPanel.add(lowerBoundField);
            searchInputPanel.add(new JLabel("输入金额上限："));
            searchInputPanel.add(upperBoundField);

            // 单选按钮组
            ButtonGroup group = new ButtonGroup();
            JRadioButton byNameButton = new JRadioButton("按项目名称查询", true);
            JRadioButton byDateButton = new JRadioButton("按日期查询");
            JRadioButton byAmountButton = new JRadioButton("按项目金额查询");
            group.add(byNameButton);
            group.add(byDateButton);
            group.add(byAmountButton);
            searchInputPanel.add(byNameButton);
            searchInputPanel.add(byDateButton);
            searchInputPanel.add(byAmountButton);

            int result = JOptionPane.showConfirmDialog(null, searchInputPanel, "查询", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                String searchText = searchField.getText();
                int searchColumn = byNameButton.isSelected() ? 0 : (byDateButton.isSelected() ? 1 : 2);
                if (byAmountButton.isSelected()) {
                    double lowerBound = Double.parseDouble(lowerBoundField.getText());
                    double upperBound = Double.parseDouble(upperBoundField.getText());
                    JTable resultTable = new JTable(new DefaultTableModel(new Object[]{"项目名称", "添加日期", "项目金额", "项目类型"}, 0));
                    JScrollPane scrollPane = new JScrollPane(resultTable);
                    JFrame resultFrame = new JFrame("查询结果");
                    resultFrame.add(scrollPane);
                    resultFrame.setSize(400, 300);
                    resultFrame.setLocationRelativeTo(null);
                    for (int i = 0; i < table.getRowCount(); i++) {
                        double amount = Double.parseDouble(model.getValueAt(i, 2).toString());
                        if (amount >= lowerBound && amount <= upperBound) {
                            ((DefaultTableModel) resultTable.getModel()).addRow(new Object[]{
                                    model.getValueAt(i, 0),
                                    model.getValueAt(i, 1),
                                    model.getValueAt(i, 2),
                                    model.getValueAt(i, 3)
                            });
                        }
                    }
                    if (resultTable.getRowCount() > 0) {
                        resultFrame.setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(null, "未找到匹配的项目。");
                    }
                } else {
                    for (int i = 0; i < table.getRowCount(); i++) {
                        if (model.getValueAt(i, searchColumn).equals(searchText)) {
                            table.setRowSelectionInterval(i, i);
                            JOptionPane.showMessageDialog(null, "找到匹配的项目。");
                            return;
                        }
                    }
                    JOptionPane.showMessageDialog(null, "未找到匹配的项目。");
                }
            }
        }
    }
    public static void main(String[] args) throws IOException {
        Home home = new Home();
    }
}