package Windows;

import javax.swing.*;

public class Home extends JFrame {
    public Home(){
        super("主页");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setLayout(null);
        JLabel label = new JLabel("欢迎使用！");
        label.setBounds(100, 50, 100, 20);
        add(label);
    }
}
