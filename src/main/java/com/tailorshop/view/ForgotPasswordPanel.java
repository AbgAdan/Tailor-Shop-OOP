package com.tailorshop.view;

import com.tailorshop.controller.ForgotPasswordController;
import com.tailorshop.util.StyleUtil;

import javax.swing.*;
import java.awt.*;

public class ForgotPasswordPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    public ForgotPasswordPanel() {
        setLayout(new BorderLayout());
        setBackground(StyleUtil.BG_LIGHT);

        JLabel header = new JLabel("LUPA KATA LALUAN", JLabel.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 20));
        header.setBorder(BorderFactory.createEmptyBorder(30, 0, 20, 0));
        add(header, BorderLayout.NORTH);

        showEmailForm();
    }

    private void showEmailForm() {
        removeAll();

        JLabel title = new JLabel("Sila masukkan maklumat anda:", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(StyleUtil.BG_LIGHT);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        JTextField nameField = new JTextField(20);
        JTextField emailField = new JTextField(20);

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Nama Penuh:"), gbc);
        gbc.gridx = 1; formPanel.add(nameField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Emel:"), gbc);
        gbc.gridx = 1; formPanel.add(emailField, gbc);

        JButton nextBtn = new JButton("Seterusnya");
        JButton backBtn = new JButton("Kembali");

        styleButton(nextBtn, StyleUtil.CUSTOMER_COLOR);
        styleButton(backBtn, Color.GRAY);

        nextBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            if (name.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Sila isi semua medan!", "Ralat", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                ForgotPasswordController controller = new ForgotPasswordController();
                if (controller.verifyUser(name, email)) {
                    showPasswordForm(email);
                } else {
                    JOptionPane.showMessageDialog(this, "Maklumat tidak sepadan!\nSila semak nama dan emel.", "Ralat", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Ralat: " + ex.getMessage(), "Ralat", JOptionPane.ERROR_MESSAGE);
            }
        });

        backBtn.addActionListener(e -> navigateTo(new MainMenuPanel()));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.add(backBtn);
        buttonPanel.add(nextBtn);

        add(title, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        revalidate();
        repaint();
    }

    private void showPasswordForm(String email) {
        removeAll();

        JLabel title = new JLabel("Tetapkan kata laluan baharu:", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(StyleUtil.BG_LIGHT);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        JPasswordField pass1Field = new JPasswordField(20);
        JPasswordField pass2Field = new JPasswordField(20);

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Kata Laluan Baharu:"), gbc);
        gbc.gridx = 1; formPanel.add(pass1Field, gbc);
        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Sahkan Semula:"), gbc);
        gbc.gridx = 1; formPanel.add(pass2Field, gbc);

        JButton resetBtn = new JButton("Tetapkan Semula");
        JButton backBtn = new JButton("❮ Kembali");

        styleButton(resetBtn, StyleUtil.CUSTOMER_COLOR);
        styleButton(backBtn, Color.GRAY);

        resetBtn.addActionListener(e -> {
            String pass1 = new String(pass1Field.getPassword()).trim();
            String pass2 = new String(pass2Field.getPassword()).trim();

            if (pass1.isEmpty() || pass2.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Sila isi kedua-dua medan!", "Ralat", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!pass1.equals(pass2)) {
                JOptionPane.showMessageDialog(this, "Kata laluan tidak sepadan!", "Ralat", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (pass1.length() < 6) {
                JOptionPane.showMessageDialog(this, "Kata laluan mesti sekurang-kurangnya 6 aksara.", "Ralat", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                ForgotPasswordController controller = new ForgotPasswordController();
                if (controller.resetPassword(email, pass1)) {
                    JOptionPane.showMessageDialog(this, "Kata laluan berjaya dikemaskini!\nSila log masuk semula.", "Berjaya", JOptionPane.INFORMATION_MESSAGE);
                    navigateTo(new MainMenuPanel());
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal mengemaskini kata laluan.", "Ralat", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Ralat: " + ex.getMessage(), "Ralat", JOptionPane.ERROR_MESSAGE);
            }
        });

        backBtn.addActionListener(e -> showEmailForm());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.add(backBtn);
        buttonPanel.add(resetBtn);

        add(title, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        revalidate();
        repaint();
    }

    private void styleButton(JButton btn, Color bgColor) {
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setPreferredSize(new Dimension(140, 32));
    }

    private void navigateTo(JPanel panel) {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window instanceof JFrame) {
            ((JFrame) window).setContentPane(panel);
            window.revalidate();
            window.repaint();
        }
    }
}