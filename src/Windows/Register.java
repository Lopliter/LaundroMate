package Windows;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import static Windows.LoginListener.login;

public class Register extends JFrame {
    JTextField usernameField = new JTextField();
    JPasswordField passwordField = new JPasswordField();
    JPasswordField passwordConfirmField = new JPasswordField();
    public Register(){
        super("注册");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setResizable(false);
        JLabel usernameLabel = new JLabel("用户名");
        JLabel passwordLabel = new JLabel("密码");
        JLabel passwordConfirmLabel = new JLabel("确认密码");
        JButton registerButton = new JButton("注册");
        usernameLabel.setBounds(50, 20, 50, 20);
        usernameField.setBounds(100, 20, 150, 20);
        passwordLabel.setBounds(50, 50, 50, 20);
        passwordField.setBounds(100, 50, 150, 20);
        passwordConfirmLabel.setBounds(20, 80, 80, 20);
        passwordConfirmField.setBounds(100, 80, 150, 20);
        registerButton.setBounds(50, 110, 100, 20);
        add(usernameLabel);
        add(usernameField);
        add(passwordLabel);
        add(passwordField);
        add(passwordConfirmLabel);
        add(passwordConfirmField);
        add(registerButton);
        setVisible(true);
        registerButton.addActionListener(new RegisterButtonListener(login));
        RegisterButtonListener.register = this;
    }
}
class RegisterButtonListener implements ActionListener {
    static Register register;
    static Login login;
    public RegisterButtonListener(Login l) {
        RegisterButtonListener.login = l;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        File file = new File("./pwd/"+register.usernameField.getText() + ".txt");
        if(register.usernameField.getText().equals("")){
            JOptionPane.showMessageDialog(null, "用户名不能为空！", "提示", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if(register.passwordField.getText().equals("")){
            JOptionPane.showMessageDialog(null, "密码不能为空！", "提示", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if(file.exists()){
            JOptionPane.showMessageDialog(null, "用户已存在！", "提示", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            if(register.passwordField.getText().equals(register.passwordConfirmField.getText())){
                File userdata = new File("./data/data.txt");
                File usertype = new File("./data/types.txt");
                if(userdata.exists()){
                    JOptionPane.showMessageDialog(null, "由于已存在数据，您的账号被设置为负责人账户。", "提示", JOptionPane.ERROR_MESSAGE);
                }
                userdata.createNewFile();
                usertype.createNewFile();
                FileOutputStream typefos = new FileOutputStream(usertype);
                typefos.write("业务一,业务二".getBytes());
                JOptionPane.showMessageDialog(null, "注册成功，请登录！", "提示", JOptionPane.INFORMATION_MESSAGE);
                file.createNewFile();
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(register.passwordField.getText().getBytes());
                fos.close();
                login.usernameField.setText(register.usernameField.getText());
                login.passwordField.setText(register.passwordField.getText());;
                login.setVisible(true);
                register.dispose();

            } else {
                JOptionPane.showMessageDialog(null, "两次密码不一致！", "提示", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "注册失败，请重试！", "提示", JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException(ex);
        }
    }
}
