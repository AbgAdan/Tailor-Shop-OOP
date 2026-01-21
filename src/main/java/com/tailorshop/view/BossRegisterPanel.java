// com.tailorshop.view.BossRegisterPanel.java
package com.tailorshop.view;

import com.tailorshop.controller.UserController;
import com.tailorshop.main.Main;
import com.tailorshop.util.StyleUtil;

import javax.swing.*;
import java.awt.*;

public class BossRegisterPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private JTextField nameField;
    private JTextField emailField;
    private JComboBox<String> roleComboBox;
    private final Runnable onBack;
    private final String bossId; // ID Boss yang sedang log masuk

    /**
     * Constructor
     * @param onBack Callback untuk kembali ke dashboard
     * @param bossId ID Boss yang sedang log masuk (contoh: "B0012026")
     */
    public BossRegisterPanel(Runnable onBack, String bossId) {
        this.onBack = onBack;
        this.bossId = bossId;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(StyleUtil.BG_LIGHT);

        // Header
        JLabel header = new JLabel("➕ DAFTAR PENGGUNA BARU", JLabel.CENTER);
        header.setFont(StyleUtil.TITLE_FONT);
        header.setBorder(BorderFactory.createEmptyBorder(30, 20, 20, 20));
        add(header, BorderLayout.NORTH);

        // Form Panel (tanpa medan kata laluan!)
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(StyleUtil.BG_LIGHT);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.anchor = GridBagConstraints.WEST;

        nameField = new JTextField(20);
        emailField = new JTextField(20);
        roleComboBox = new JComboBox<>(new String[]{"CUSTOMER", "TAILOR", "BOSS"});
        roleComboBox.setSelectedItem("CUSTOMER");
        roleComboBox.setFont(StyleUtil.BUTTON_FONT);

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

        // Peranan
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Peranan:"), gbc);
        gbc.gridx = 1;
        formPanel.add(roleComboBox, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Butang Bawah
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        
        JButton registerBtn = new JButton("Daftar Pengguna");
        JButton backBtn = new JButton("❮ Kembali");

        styleButton(registerBtn, StyleUtil.BOSS_COLOR);
        styleButton(backBtn, Color.GRAY);

        registerBtn.addActionListener(e -> handleRegister());
        backBtn.addActionListener(e -> onBack.run());

        buttonPanel.add(backBtn);
        buttonPanel.add(registerBtn);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void handleRegister() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String role = (String) roleComboBox.getSelectedItem();

        if (name.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Sila isi semua medan!",
                "Ralat",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try {
            UserController userController = new UserController();
            // 🔑 Password auto jadi "123456" dalam controller
            userController.registerByBoss(name, email, role, bossId);

            JOptionPane.showMessageDialog(
                this,
                "Pengguna berjaya didaftarkan!\nKata laluan default: 123456",
                "Berjaya",
                JOptionPane.INFORMATION_MESSAGE
            );

            // Kosongkan borang selepas berjaya
            nameField.setText("");
            emailField.setText("");
            roleComboBox.setSelectedItem("CUSTOMER");

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(
                this,
                ex.getMessage(),
                "Input Tidak Sah",
                JOptionPane.WARNING_MESSAGE
            );
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(
                this,
                ex.getMessage(),
                "Ralat",
                JOptionPane.ERROR_MESSAGE
            );
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this,
                "Ralat sistem: " + ex.getMessage(),
                "Ralat",
                JOptionPane.ERROR_MESSAGE
            );
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
        btn.setPreferredSize(new Dimension(170, 38));
    }

    // Method navigasi selamat
    protected void navigateTo(JPanel panel) {
        if (Main.mainFrame != null) {
            Main.mainFrame.setContentPane(panel);
            Main.mainFrame.revalidate();
            Main.mainFrame.repaint();
        } else {
            JOptionPane.showMessageDialog(
                this,
                "Tetingkap utama tidak dijumpai.",
                "Ralat Navigasi",
                JOptionPane.WARNING_MESSAGE
            );
        }
    }
}