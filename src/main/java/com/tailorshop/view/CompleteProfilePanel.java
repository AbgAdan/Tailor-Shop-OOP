// com.tailorshop.view.CompleteProfilePanel.java
package com.tailorshop.view;

import com.tailorshop.controller.ProfileController;
import com.tailorshop.main.Main;
import com.tailorshop.util.StyleUtil;

import javax.swing.*;
import java.awt.*;

public class CompleteProfilePanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private final String userId;
    private final String role;
    private final Runnable onProfileComplete;

    private JTextField phoneField;
    private JTextField addressField;
    private JComboBox<String> genderCombo;

    public CompleteProfilePanel(String userId, String role, Runnable onProfileComplete) {
        this.userId = userId;
        this.role = role;
        this.onProfileComplete = onProfileComplete;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(StyleUtil.BG_LIGHT);

        JLabel header = new JLabel(" LENGKAPKAN PROFIL ANDA", JLabel.CENTER);
        header.setFont(StyleUtil.TITLE_FONT);
        header.setBorder(BorderFactory.createEmptyBorder(30, 20, 20, 20));
        add(header, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(StyleUtil.BG_LIGHT);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        phoneField = new JTextField(20);
        
        // Hanya customer perlu gender & address
        if ("CUSTOMER".equalsIgnoreCase(role)) {
            genderCombo = new JComboBox<>(new String[]{"Lelaki", "Perempuan"});
            addressField = new JTextField(20);

            gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Jantina:"), gbc);
            gbc.gridx = 1; formPanel.add(genderCombo, gbc);

            gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("No. Telefon:"), gbc);
            gbc.gridx = 1; formPanel.add(phoneField, gbc);

            gbc.gridx = 0; gbc.gridy = 2; formPanel.add(new JLabel("Alamat:"), gbc);
            gbc.gridx = 1; formPanel.add(addressField, gbc);
        } else {
            // Tailor: hanya no. telefon
            gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("No. Telefon:"), gbc);
            gbc.gridx = 1; formPanel.add(phoneField, gbc);
        }

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        JButton saveBtn = new JButton("Simpan Profil");
        saveBtn.addActionListener(e -> handleSave());
        styleButton(saveBtn, StyleUtil.CUSTOMER_COLOR);
        buttonPanel.add(saveBtn);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void handleSave() {
        String phone = phoneField.getText().trim();
        String gender = genderCombo != null ? (String) genderCombo.getSelectedItem() : null;
        String address = addressField != null ? addressField.getText().trim() : null;

        try {
            ProfileController controller = new ProfileController();
            controller.saveProfile(userId, role, gender, phone, address);
            
            JOptionPane.showMessageDialog(this, "Profil berjaya dikemaskini!", "Berjaya", JOptionPane.INFORMATION_MESSAGE);
            onProfileComplete.run(); // Kembali ke dashboard
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Ralat", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void styleButton(JButton btn, Color bgColor) {
        btn.setFont(StyleUtil.BUTTON_FONT);
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setPreferredSize(new Dimension(160, 36));
    }

    protected void navigateTo(JPanel panel) {
        if (Main.mainFrame != null) {
            Main.mainFrame.setContentPane(panel);
            Main.mainFrame.revalidate();
            Main.mainFrame.repaint();
        }
    }
}