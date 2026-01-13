package com.tailorshop.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.tailorshop.database.DatabaseConnection;
import com.tailorshop.util.StyleUtil;

public class LoginPanel extends JPanel {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LoginPanel() {
        setLayout(new BorderLayout());
        setBackground(StyleUtil.BG_LIGHT);

        // Header
        JLabel header = new JLabel("🔑 LOG MASUK", JLabel.CENTER);
        header.setFont(StyleUtil.TITLE_FONT);
        header.setBorder(BorderFactory.createEmptyBorder(40, 0, 30, 0));
        add(header, BorderLayout.NORTH);

        // Borang
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(StyleUtil.BG_LIGHT);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JTextField emailField = new JTextField(20);
        JPasswordField passField = new JPasswordField(20);

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
        JButton backBtn = new JButton("❮ Kembali ke Menu");

        styleButton(loginBtn, StyleUtil.CUSTOMER_COLOR);
        styleButton(backBtn, Color.GRAY);

        loginBtn.addActionListener(e -> handleLogin(
            emailField.getText().trim(),
            new String(passField.getPassword()).trim()
        ));

        backBtn.addActionListener(e -> navigateToMainMenu());

        buttonPanel.add(backBtn);
        buttonPanel.add(loginBtn);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void styleButton(JButton btn, Color bgColor) {
        btn.setFont(StyleUtil.BUTTON_FONT);
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
    }

    private void handleLogin(String email, String password) {
    if (email.isEmpty() || password.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Sila isi semua medan!", "Ralat", JOptionPane.WARNING_MESSAGE);
        return;
    }

    try {
        String sql = "SELECT role FROM users WHERE email = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            stmt.setString(2, password);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String role = rs.getString("role");
                Runnable onLogout = () -> navigateTo(new MainMenuPanel());

                // 🔁 Guna switch statement biasa (kompatibel Java 8+)
                JPanel dashboard;
                switch (role) {
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
                        throw new IllegalStateException("Peranan tidak dikenali: " + role);
                }

                navigateTo(dashboard);
            } else {
                JOptionPane.showMessageDialog(this, "Emel atau kata laluan salah!", "Ralat", JOptionPane.ERROR_MESSAGE);
            }
        }
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Ralat sistem: " + ex.getMessage(), "Ralat", JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
    }
}

    private void navigateToMainMenu() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (frame != null) {
            frame.setContentPane(new MainMenuPanel()); // ← Kita perlu buat ini!
            frame.revalidate();
            frame.repaint();
        }
    }


    private void navigateTo(JPanel panel) {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (frame != null) {
            frame.setContentPane(panel);
            frame.revalidate();
            frame.repaint();
        }
    }
}