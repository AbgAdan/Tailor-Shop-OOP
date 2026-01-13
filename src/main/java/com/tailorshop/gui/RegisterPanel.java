package com.tailorshop.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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

public class RegisterPanel extends JPanel {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RegisterPanel() {
        setLayout(new BorderLayout());
        setBackground(StyleUtil.BG_LIGHT);

        // Header
        JLabel headerLabel = new JLabel("DAFTAR SEBAGAI PELANGGAN", JLabel.CENTER);
        headerLabel.setFont(StyleUtil.TITLE_FONT);
        headerLabel.setBorder(BorderFactory.createEmptyBorder(30, 20, 20, 20));
        add(headerLabel, BorderLayout.NORTH);

        // Nota kecil
        JLabel note = new JLabel("Hanya akaun pelanggan boleh didaftarkan di sini.", JLabel.CENTER);
        note.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        note.setForeground(Color.GRAY);
        add(note, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(StyleUtil.BG_LIGHT);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.anchor = GridBagConstraints.WEST;

        JTextField nameField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);

        // Nama
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Nama Penuh:"), gbc);
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);

        // Emel
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Emel:"), gbc);
        gbc.gridx = 1;
        formPanel.add(emailField, gbc);

        // Kata Laluan
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Kata Laluan (min 6 aksara):"), gbc);
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Butang Bawah
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        
        JButton backButton = new JButton("❮ Kembali ke Menu");
        JButton registerButton = new JButton("Daftar Akaun");

        styleButton(backButton, Color.GRAY);
        styleButton(registerButton, StyleUtil.CUSTOMER_COLOR);

        backButton.addActionListener(e -> navigateTo(new MainMenuPanel()));
        registerButton.addActionListener(e -> handleRegister(
            nameField.getText().trim(),
            emailField.getText().trim(),
            new String(passwordField.getPassword()).trim()
        ));

        buttonPanel.add(backButton);
        buttonPanel.add(registerButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void styleButton(JButton btn, Color bgColor) {
        btn.setFont(StyleUtil.BUTTON_FONT);
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setPreferredSize(new Dimension(180, 36));
    }

    private void handleRegister(String name, String email, String password) {
    if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Sila isi semua medan!", "Ralat", JOptionPane.WARNING_MESSAGE);
        return;
    }

    try {
        String sql = "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, 'CUSTOMER')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, password); // plain text (untuk demo)
            stmt.executeUpdate();
        }

        JOptionPane.showMessageDialog(this, "Pendaftaran berjaya!", "Berjaya", JOptionPane.INFORMATION_MESSAGE);
        Runnable onLogout = () -> navigateTo(new MainMenuPanel());
        navigateTo(new CustomerDashboard(onLogout));

    } catch (SQLException ex) {
        if (ex.getMessage() != null && ex.getMessage().contains("Duplicate entry")) {
            JOptionPane.showMessageDialog(this, "Emel sudah digunakan!", "Ralat", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Ralat: " + ex.getMessage(), "Ralat", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
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