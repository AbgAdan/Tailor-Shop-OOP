package com.tailorshop.view;

import com.tailorshop.controller.AuthController;
import com.tailorshop.main.Main;
import com.tailorshop.model.User;
import com.tailorshop.util.StyleUtil;

import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private JTextField emailField;
    private JPasswordField passField;

    public LoginPanel() {
        setLayout(new BorderLayout());
        setBackground(StyleUtil.BG_LIGHT);

        // Header
        JLabel header = new JLabel("LOG MASUK", JLabel.CENTER);
        header.setFont(StyleUtil.TITLE_FONT);
        header.setBorder(BorderFactory.createEmptyBorder(40, 0, 30, 0));
        add(header, BorderLayout.NORTH);

        // Borang
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(StyleUtil.BG_LIGHT);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        emailField = new JTextField(20);
        passField = new JPasswordField(20);

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Emel:"), gbc);
        gbc.gridx = 1;
        formPanel.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Kata Laluan:"), gbc);
        gbc.gridx = 1;
        formPanel.add(passField, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Butang
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        JButton loginBtn = new JButton("Log Masuk");
        JButton backBtn = new JButton("Kembali ke Menu");

        styleButton(loginBtn, StyleUtil.CUSTOMER_COLOR);
        styleButton(backBtn, Color.GRAY);

        loginBtn.addActionListener(e -> handleLogin());
        backBtn.addActionListener(e -> navigateTo(new MainMenuPanel()));

        buttonPanel.add(backBtn);
        buttonPanel.add(loginBtn);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = new String(passField.getPassword()).trim();

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Sila isi semua medan!", "Ralat", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            AuthController authController = new AuthController();
            User user = authController.login(email, password);

            if (user != null) {
                Runnable onLogout = () -> navigateTo(new MainMenuPanel());

                JPanel dashboard;
                switch (user.getRole().toUpperCase()) {
                    case "CUSTOMER":
                        dashboard = new CustomerDashboard(onLogout);
                        break;
                    case "TAILOR":
                        dashboard = new TailorDashboard(onLogout);
                        break;
                    case "BOSS":
                        dashboard = new BossDashboard(onLogout);
                        break;
                    default:
                        throw new IllegalStateException("Peranan tidak dikenali: " + user.getRole());
                }
                navigateTo(dashboard);
            } else {
                JOptionPane.showMessageDialog(this, "Emel atau kata laluan salah!", "Ralat", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ralat sistem: " + ex.getMessage(), "Ralat", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void styleButton(JButton btn, Color bgColor) {
        btn.setFont(StyleUtil.BUTTON_FONT);
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
    }

    protected void navigateTo(JPanel panel) {
        if (Main.mainFrame != null) {
            Main.mainFrame.setContentPane(panel);
            Main.mainFrame.revalidate();
            Main.mainFrame.repaint();
        } else {
            JOptionPane.showMessageDialog(this, "Ralat: Tetingkap utama tidak tersedia.", "Ralat", JOptionPane.ERROR_MESSAGE);
        }
    }
}