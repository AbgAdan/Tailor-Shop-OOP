package com.tailorshop.view;

import com.tailorshop.controller.UserController;
import com.tailorshop.util.StyleUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class RegisterPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private JTextField nameField;
    private JTextField emailField;
    private JPasswordField passwordField;

    public RegisterPanel() {
        setLayout(new BorderLayout());
        setBackground(StyleUtil.BG_LIGHT);

        JLabel headerLabel = new JLabel("DAFTAR SEBAGAI PELANGGAN", JLabel.CENTER);
        headerLabel.setFont(StyleUtil.TITLE_FONT);
        headerLabel.setBorder(BorderFactory.createEmptyBorder(30, 20, 10, 20));
        add(headerLabel, BorderLayout.NORTH);

        JLabel note = new JLabel("Sila isi butiran berikut", JLabel.CENTER);
        note.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        note.setForeground(Color.GRAY);
        add(note, BorderLayout.PAGE_START);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(StyleUtil.BG_LIGHT);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.anchor = GridBagConstraints.WEST;

        nameField = new JTextField(20);
        emailField = new JTextField(20);
        passwordField = new JPasswordField(20);

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Nama Penuh:"), gbc);
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Emel:"), gbc);
        gbc.gridx = 1;
        formPanel.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Kata Laluan (min 6 aksara):"), gbc);
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        JButton backButton = new JButton("Kembali ke Menu");
        JButton registerButton = new JButton("Daftar Akaun");

        styleButton(backButton, Color.GRAY);
        styleButton(registerButton, StyleUtil.CUSTOMER_COLOR);

        backButton.addActionListener(e -> navigateTo(new MainMenuPanel()));
        registerButton.addActionListener(this::onRegisterClick);

        buttonPanel.add(backButton);
        buttonPanel.add(registerButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void onRegisterClick(ActionEvent e) {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        try {
            UserController userController = new UserController();
            userController.registerCustomer(name, email, password);

            JOptionPane.showMessageDialog(
                this,
                "Pendaftaran berjaya!\nSila log masuk untuk meneruskan.",
                "Berjaya",
                JOptionPane.INFORMATION_MESSAGE
            );

            // ✅ PERUBAHAN DI SINI: Terus ke MainMenuPanel
            navigateTo(new MainMenuPanel());

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Tidak Sah", JOptionPane.WARNING_MESSAGE);
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Ralat", JOptionPane.ERROR_MESSAGE);
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
        btn.setPreferredSize(new Dimension(180, 36));
    }

    private void navigateTo(JPanel panel) {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window instanceof JFrame) {
            JFrame frame = (JFrame) window;
            frame.setContentPane(panel);
            frame.revalidate();
            frame.repaint();
        } else {
            JOptionPane.showMessageDialog(this, "Ralat navigasi", "Ralat", JOptionPane.ERROR_MESSAGE);
        }
    }
}