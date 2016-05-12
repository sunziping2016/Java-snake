package client.view;

/**
 * Created by sun on 5/12/16.
 *
 * Login dialog.
 */
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class LoginDialog extends JDialog {
    private JTextField tfUsername;
    private JPasswordField pfPassword;
    private JLabel lInfomation;
    private JButton btnLogin;

    public LoginDialog(Frame parent, String username, String password) {
        super(parent, "Login", true);
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "escape");
        getRootPane().getActionMap().put("escape", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LoginDialog.this.close();
            }
        });

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints cs = new GridBagConstraints();
        cs.fill = GridBagConstraints.HORIZONTAL;
        cs.insets = new Insets(5, 5, 5, 5);
        cs.weighty = 0;
        cs.gridx = 0;
        cs.gridy = 0;
        panel.add(new JLabel("Username: "), cs);
        tfUsername = new JTextField(username, 20);
        cs.gridx = 1;
        cs.gridy = 0;
        cs.gridwidth = 2;
        panel.add(tfUsername, cs);
        cs.gridx = 0;
        cs.gridy = 1;
        cs.gridwidth = 1;
        panel.add(new JLabel("Password: "), cs);
        pfPassword = new JPasswordField(password, 20);
        cs.gridx = 1;
        cs.gridy = 1;
        cs.gridwidth = 2;
        panel.add(pfPassword, cs);
        lInfomation = new JLabel("", SwingConstants.CENTER);
        cs.gridx = 0;
        cs.gridy = 2;
        cs.gridwidth = 3;
        panel.add(lInfomation, cs);
        btnLogin = new JButton("Login");
        getRootPane().setDefaultButton(btnLogin);
        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(e -> close());
        JPanel bp = new JPanel();
        bp.add(btnLogin);
        bp.add(btnCancel);
        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(bp, BorderLayout.PAGE_END);
        pack();
        setResizable(false);
        setLocationRelativeTo(parent);
    }

    public String getUsername() {
        return tfUsername.getText().trim();
    }

    public String getPassword() {
        return new String(pfPassword.getPassword());
    }

    public void setInformation(String info) {
        lInfomation.setText(info);
        pack();
    }

    public void setlInfomationColor(Color color) {
        lInfomation.setForeground(color);
    }

    public void addActionListener(ActionListener l) {
        btnLogin.addActionListener(l);
    }

    public void removeActionListener(ActionListener l) {
        btnLogin.removeActionListener(l);
    }

    public void close() {
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }
}