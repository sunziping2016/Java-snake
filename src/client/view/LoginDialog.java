package client.view;

/**
 * Created by sun on 5/12/16.
 *
 * Login dialog.
 */
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class LoginDialog extends JDialog {
    private JTextField host;
    private JTextField port;
    private JTextField username;
    private JPasswordField password;
    private JLabel confirmLabel;
    private JPasswordField passwordConfirm;
    private JLabel information;
    private JCheckBox register;
    private JButton login;
    private JPanel panel;
    private boolean cancelled = false;

    public LoginDialog(Frame parent) {
        super(parent, "Login", true);
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "escape");
        getRootPane().getActionMap().put("escape", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                close();
            }
        });
        panel = new JPanel(new GridBagLayout());
        GridBagConstraints cs = new GridBagConstraints();
        cs.fill = GridBagConstraints.HORIZONTAL;
        cs.insets = new Insets(5, 5, 5, 5);
        cs.weighty = 0;
        cs.gridx = 0;
        cs.gridy = 0;
        panel.add(new JLabel("Host: "), cs);
        host = new JTextField(20);
        cs.gridx = 1;
        cs.gridy = 0;
        cs.gridwidth = 2;
        panel.add(host, cs);
        cs.gridx = 0;
        cs.gridy = 1;
        cs.gridwidth = 1;
        panel.add(new JLabel("Port: "), cs);
        port = new JTextField(20);
        cs.gridx = 1;
        cs.gridy = 1;
        cs.gridwidth = 2;
        panel.add(port, cs);
        cs.gridx = 0;
        cs.gridy = 2;
        cs.gridwidth = 1;
        panel.add(new JLabel("Username: "), cs);
        username = new JTextField(20);
        cs.gridx = 1;
        cs.gridy = 2;
        cs.gridwidth = 2;
        panel.add(username, cs);
        cs.gridx = 0;
        cs.gridy = 3;
        cs.gridwidth = 1;
        panel.add(new JLabel("Password: "), cs);
        password = new JPasswordField(20);
        cs.gridx = 1;
        cs.gridy = 3;
        cs.gridwidth = 2;
        panel.add(password, cs);
        information = new JLabel("", SwingConstants.CENTER);
        cs.gridx = 0;
        cs.gridy = 5;
        cs.gridwidth = 3;
        panel.add(information, cs);
        login = new JButton("Login");
        getRootPane().setDefaultButton(login);
        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LoginDialog.this.close();
                LoginDialog.this.cancelled = true;
            }
        });
        JPanel bp = new JPanel();
        register = new JCheckBox("Register");
        confirmLabel = new JLabel("Retype password: ");
        passwordConfirm = new JPasswordField(20);
        register.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                GridBagConstraints cs1 = new GridBagConstraints();
                cs1.fill = GridBagConstraints.HORIZONTAL;
                cs1.insets = new Insets(5, 5, 5, 5);
                cs1.weighty = 0;
                cs1.gridx = 0;
                cs1.gridy = 4;
                cs1.gridwidth = 1;
                panel.add(confirmLabel, cs1);
                cs1.gridx = 1;
                cs1.gridy = 4;
                cs1.gridwidth = 2;
                panel.add(passwordConfirm, cs1);
            } else {
                panel.remove(confirmLabel);
                panel.remove(passwordConfirm);
            }
            pack();
        });
        bp.add(register);
        bp.add(login);
        bp.add(btnCancel);
        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(bp, BorderLayout.PAGE_END);
        pack();
        setResizable(false);
        setLocationRelativeTo(parent);
    }

    public String getHost() {
        return host.getText().trim();
    }

    public String getPort() {
        return port.getText().trim();
    }

    public String getUsername() {
        return username.getText();
    }

    public String getPassword() {
        return new String(password.getPassword());
    }
    public String getPasswordConfirm() {
        return new String(passwordConfirm.getPassword());
    }

    public boolean isRegister() {
        return register.isSelected();
    }

    public void setHost(String host) {
        this.host.setText(host);
    }

    public void setPort(String port) {
        this.port.setText(port);
    }

    public void setInformation(String info) {
        information.setText(info);
        pack();
    }

    public void setInformationColor(Color color) {
        information.setForeground(color);
    }

    public void addActionListener(ActionListener l) {
        login.addActionListener(l);
    }

    public void removeActionListener(ActionListener l) {
        login.removeActionListener(l);
    }

    public void close() {
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    public boolean isCancelled() {
        return cancelled;
    }
}