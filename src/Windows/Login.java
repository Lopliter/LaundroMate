package Windows;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class Login extends JFrame {
    JTextField usernameField = new JTextField();
    JPasswordField passwordField = new JPasswordField();
    public Login() {
        super("登录");
        setBounds(500, 200, 300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setResizable(false);
        JLabel usernameLabel = new JLabel("用户名");
        JLabel passwordLabel = new JLabel("密码");
        JButton loginButton = new JButton("登录");
        JButton registerButton = new JButton("注册");
        setLayout(null);
        usernameLabel.setBounds(50, 20, 50, 20);
        usernameField.setBounds(100, 20, 100, 20);
        passwordLabel.setBounds(50, 50, 50, 20);
        passwordField.setBounds(100, 50, 100, 20);
        loginButton.setBounds(50, 80, 60, 30);
        registerButton.setBounds(120, 80, 60, 30);
        add(usernameLabel);
        add(usernameField);
        add(passwordLabel);
        add(passwordField);
        add(loginButton);
        add(registerButton);
        loginButton.addActionListener(new LoginListener());
        registerButton.addActionListener(new RegisterListener());
        LoginListener.login = this;
        revalidate();
        repaint();

    }
}
class LoginListener implements ActionListener {
    static Login login;
    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if(login.usernameField.getText().equals("")){
                JOptionPane.showMessageDialog(null, "用户名不能为空！", "提示", JOptionPane.ERROR_MESSAGE);
                return;
            }
            BufferedReader br = new BufferedReader(new FileReader("./pwd/"+login.usernameField.getText() + ".txt"));
            String pwd = br.readLine();
            br.close();
            if (pwd.equals(login.passwordField.getText())) {
                JOptionPane.showMessageDialog(null, "登录成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
                Home home = new Home();
                login.dispose();
            } else {
                JOptionPane.showMessageDialog(null, "密码错误！", "提示", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "用户不存在，请先注册！", "提示", JOptionPane.INFORMATION_MESSAGE);
            Register register = new Register();
            register.usernameField.setText(LoginListener.login.usernameField.getText());
            LoginListener.login.dispose();
        }
    }
}
class RegisterListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        Register register = new Register();
        register.usernameField.setText(LoginListener.login.usernameField.getText());
        LoginListener.login.dispose();
    }
}
