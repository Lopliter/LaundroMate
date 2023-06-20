package Windows;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class Home extends JFrame {
    DefaultTableModel model;
    JTextField nameField;
    JTextField amountField;
    JComboBox<String> typeBox;
    File data;
    JTable table;
    // 客户管理相关的变量
    DefaultTableModel customerModel;
    JTextField customerNameField;
    JTextField contactField;
    ArrayList<String[]> customers = new ArrayList<>();
    JTable customerTable;
    JComboBox<String> customerBox = new JComboBox<>(getCustomerNames());
    File typesFile = new File("./data/types.txt");
    public Home() throws IOException {
        super("LaundroMate");
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                int response = JOptionPane.showConfirmDialog(null,"请确保你已经保存。", "是否退出？", JOptionPane.YES_NO_OPTION);
                if (response == JOptionPane.YES_OPTION) {
                    System.exit(0);
                } else if (response == JOptionPane.NO_OPTION) {
                    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                }
            }
        });
        File customerFile = new File("./data/customers.txt");
        BufferedReader customerBr = new BufferedReader(new FileReader(customerFile));
        String read;
        while ((read = customerBr.readLine())!=null){
            customers.add(read.split(","));
        }
        data = new File("./data/data.txt");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JTabbedPane tabbedPane = new JTabbedPane();
        setBounds(600, 300, 900, 600);
        setResizable(false);
        JPanel AdminPanel = new JPanel();
        tabbedPane.addTab("洗衣项目信息", AdminPanel);
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
        model.addColumn("客户");
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
        String[] types;
        types = br.readLine().split(",");
        typeBox = new JComboBox<>(types);
        inputPanel.add(new JLabel("项目类型:"));
        inputPanel.add(typeBox);
        inputPanel.add(new JLabel("客户："));
        inputPanel.add(customerBox);
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
        JMenuItem searchMenuItem = new JMenuItem("洗衣项目查询");
        fileMenu.add(searchMenuItem);
        searchMenuItem.addActionListener(new SearchActionListener(table, model));
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);
        customerPanel.setLayout(new BorderLayout());
        //客户管理相关的控件
        customerModel = new DefaultTableModel();
        customerModel.addColumn("客户名");
        customerModel.addColumn("联系方式");
        customerTable = new JTable(customerModel);
        JScrollPane customerScrollPane = new JScrollPane(customerTable);
        customerPanel.add(customerScrollPane, BorderLayout.CENTER);
        JPanel customerInputPanel = new JPanel(new FlowLayout());
        customerNameField = new JTextField(10);
        customerInputPanel.add(new JLabel("客户名:"));
        customerInputPanel.add(customerNameField);
        contactField = new JTextField(10);
        customerInputPanel.add(new JLabel("联系方式:"));
        customerInputPanel.add(contactField);
        JButton addCustomerButton = new JButton("添加客户");
        addCustomerButton.addActionListener(new AddCustomerButtonListener());
        customerInputPanel.add(addCustomerButton);

        JButton deleteCustomerButton = new JButton("删除客户");
        deleteCustomerButton.addActionListener(new DeleteCustomerButtonListener());
        customerInputPanel.add(deleteCustomerButton);
        File customerFile2 = new File("./data/customers.txt");
        try {
            Scanner scanner = new Scanner(customerFile2);
            while (scanner.hasNextLine()) {
                String[] rowData = scanner.nextLine().split(",");
                customerModel.addRow(rowData);
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        customerPanel.add(customerInputPanel, BorderLayout.SOUTH);
        //项目类型管理嗷
        DefaultTableModel typeModel = new DefaultTableModel();
        typeModel.addColumn("类型名称");
        JPanel typeManagementPanel = new JPanel(new BorderLayout());
        tabbedPane.addTab("类型管理", typeManagementPanel);
        JTable typeTable = new JTable(typeModel){
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JScrollPane typeScrollPane = new JScrollPane(typeTable);
        typeManagementPanel.add(typeScrollPane, BorderLayout.CENTER);
        JPanel typeOperationsPanel = new JPanel();
        typeManagementPanel.add(typeOperationsPanel, BorderLayout.SOUTH);
        JTextField typeField = new JTextField(15);
        JButton addTypeButton = new JButton("添加类型");
        JButton removeTypeButton = new JButton("删除选中类型");
        typeOperationsPanel.add(new JLabel("类型名称:"));
        typeOperationsPanel.add(typeField);
        typeOperationsPanel.add(addTypeButton);
        typeOperationsPanel.add(removeTypeButton);
        addTypeButton.addActionListener(new AddTypeButtonListener(typeModel, typeField, typeBox, typesFile));
        removeTypeButton.addActionListener(new RemoveTypeButtonListener(typeTable, typeModel, typeBox, typesFile));
        File typeFile2 = new File("./data/types.txt");
        try {
            BufferedReader br2 = new BufferedReader(new FileReader(typeFile2));
            String[] types2;
            types2 = br2.readLine().split(",");
            for (int i=0;i<types2.length;i++){
                typeModel.addRow(new Object[]{types2[i]});
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
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
            String customer = (String) customerBox.getSelectedItem();
            model.addRow(new Object[]{name, date, amount, type, customer});
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
                            + model.getValueAt(i, 2) + "," + model.getValueAt(i, 3) + "," + model.getValueAt(i, 4));
                    writer.newLine();
                }
                writer.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            try {
                BufferedWriter customerWriter = new BufferedWriter(new FileWriter("./data/customers.txt"));
                for (String[] customer : customers) {
                    customerWriter.write(customer[0] + "," + customer[1]);
                    customerWriter.newLine();
                }
                customerWriter.close();
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
            ButtonGroup group = new ButtonGroup();
            JRadioButton byNameButton = new JRadioButton("按项目名称查询", true);
            JRadioButton byDateButton = new JRadioButton("按日期查询");
            JRadioButton byAmountButton = new JRadioButton("按项目金额查询");
            JRadioButton byTypeButton = new JRadioButton("按项目类型查询");
            group.add(byNameButton);
            group.add(byDateButton);
            group.add(byAmountButton);
            group.add(byTypeButton);
            searchInputPanel.add(byNameButton);
            searchInputPanel.add(byDateButton);
            searchInputPanel.add(byAmountButton);
            searchInputPanel.add(byTypeButton);


            int result = JOptionPane.showConfirmDialog(null, searchInputPanel, "查询", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                String searchText = searchField.getText();
                int searchColumn = byNameButton.isSelected() ? 0 : (byDateButton.isSelected() ? 1 : (byAmountButton.isSelected() ? 2 : 3));
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
    class AddCustomerButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (!customerNameField.getText().isEmpty() && !contactField.getText().isEmpty()) {
                Object[] newCustomer = {customerNameField.getText(), contactField.getText()};
                customerModel.addRow(newCustomer);
                customerBox.addItem((String)newCustomer[0]);
                customers.add(new String[]{customerNameField.getText(), contactField.getText()});
                customerNameField.setText("");
                contactField.setText("");
            } else {
                JOptionPane.showMessageDialog(null, "请确保所有字段都已填写", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    class DeleteCustomerButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int selectedRow = customerTable.getSelectedRow();
            if (selectedRow != -1) {
                String customerName = (String)customerModel.getValueAt(selectedRow, 0);
                customerModel.removeRow(selectedRow);
                customerBox.removeItem(customerName);
                customers.remove(customerName);
            } else {
                JOptionPane.showMessageDialog(null, "请选择一个客户", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    private String[] getCustomerNames() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(new File("./data/customers.txt")));
        List<String> customers = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            customers.add(line.split(",")[0]);
        }
        reader.close();
        return customers.toArray(new String[0]);
    }
    class AddTypeButtonListener implements ActionListener {
        private DefaultTableModel model;
        private JTextField typeField;
        private JComboBox<String> typeBox;
        private File typesFile;

        public AddTypeButtonListener(DefaultTableModel model, JTextField typeField, JComboBox<String> typeBox, File typesFile) {
            this.model = model;
            this.typeField = typeField;
            this.typeBox = typeBox;
            this.typesFile = typesFile;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String newType = typeField.getText();
            if (!newType.isEmpty()) {
                model.addRow(new Object[]{newType});
                typeBox.addItem(newType);
                writeTypesToFile();
                typeField.setText("");
            }
        }

        private void writeTypesToFile() {
            try (PrintWriter writer = new PrintWriter(new FileWriter(typesFile))) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < typeBox.getItemCount(); i++) {
                    sb.append(typeBox.getItemAt(i));
                    if (i != typeBox.getItemCount() - 1) {
                        sb.append(",");
                    }
                }
                writer.println(sb.toString());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    class RemoveTypeButtonListener implements ActionListener {
        private JTable table;
        private DefaultTableModel model;
        private JComboBox<String> typeBox;
        private File typesFile;

        public RemoveTypeButtonListener(JTable table, DefaultTableModel model, JComboBox<String> typeBox, File typesFile) {
            this.table = table;
            this.model = model;
            this.typeBox = typeBox;
            this.typesFile = typesFile;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                String typeName = (String)model.getValueAt(selectedRow, 0);
                model.removeRow(selectedRow);
                typeBox.removeItem(typeName);
                writeTypesToFile();
            } else {
                JOptionPane.showMessageDialog(null, "请选择一个类型", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }

        private void writeTypesToFile() {
            try (PrintWriter writer = new PrintWriter(new FileWriter(typesFile))) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < typeBox.getItemCount(); i++) {
                    sb.append(typeBox.getItemAt(i));
                    if (i != typeBox.getItemCount() - 1) {
                        sb.append(",");
                    }
                }
                writer.println(sb.toString());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    public static void main(String[] args) throws IOException {
        Home home = new Home();
    }
}