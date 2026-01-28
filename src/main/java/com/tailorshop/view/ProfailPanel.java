// com.tailorshop.view.ProfailPanel.java
package com.tailorshop.view;

import com.tailorshop.controller.ProfileController;
import com.tailorshop.main.Main;
import com.tailorshop.model.UserProfile;
import com.tailorshop.util.StyleUtil;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ProfailPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private final String userId;
    private final String role;
    private final String userName;
    private final String userEmail;
    private final Runnable onBack;

    private UserProfile currentProfile;
    private boolean isEditMode = false;

    private JTextField phoneField;
    private JComboBox<String> genderCombo;
    private JTextField addressField;
    private JTextField birthDateField;
    private JPanel contentPanel;

    public ProfailPanel(String userId, String role, String name, String email, Runnable onBack) {
        this.userId = userId;
        this.role = role.toUpperCase();
        this.userName = name;
        this.userEmail = email;
        this.onBack = onBack;
        loadProfileData();
        initializeUI();
    }

    private void loadProfileData() {
        try {
            ProfileController controller = new ProfileController();
            currentProfile = controller.getProfile(userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(StyleUtil.BG_LIGHT);

        JLabel header = new JLabel("PROFIL SAYA", JLabel.CENTER);
        header.setFont(StyleUtil.TITLE_FONT);
        header.setBorder(BorderFactory.createEmptyBorder(30, 20, 20, 20));
        add(header, BorderLayout.NORTH);

        refreshContent();

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        JButton actionBtn = new JButton("Kemaskini Profil");
        JButton backBtn = new JButton("Kembali");

        styleButton(actionBtn, StyleUtil.CUSTOMER_COLOR);
        styleButton(backBtn, Color.GRAY);

        actionBtn.addActionListener(e -> {
            if (!isEditMode) {
                isEditMode = true;
                refreshContent();
                actionBtn.setText("Simpan Perubahan");
            } else {
                handleSave(actionBtn);
            }
        });
        backBtn.addActionListener(e -> onBack.run());

        buttonPanel.add(backBtn);
        buttonPanel.add(actionBtn);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void refreshContent() {
        if (contentPanel != null) {
            remove(contentPanel);
        }

        contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(StyleUtil.BG_LIGHT);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        if (isEditMode) {
            createEditForm(contentPanel, gbc);
        } else {
            createDisplayView(contentPanel, gbc);
        }

        add(contentPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void createDisplayView(JPanel panel, GridBagConstraints gbc) {
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Nama:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(userName), gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Emel:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(userEmail), gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("No. Telefon:"), gbc);
        String phone = (currentProfile != null && currentProfile.getPhone() != null) ? 
                      currentProfile.getPhone() : "Belum ditetapkan";
        gbc.gridx = 1;
        panel.add(new JLabel(phone), gbc);

        if ("CUSTOMER".equals(role)) {
            gbc.gridx = 0; gbc.gridy = 3;
            panel.add(new JLabel("Jantina:"), gbc);
            String gender = (currentProfile != null && currentProfile.getGender() != null) ? 
                           currentProfile.getGender() : "Belum ditetapkan";
            gbc.gridx = 1;
            panel.add(new JLabel(gender), gbc);

            gbc.gridx = 0; gbc.gridy = 4;
            panel.add(new JLabel("Tarikh Lahir:"), gbc);
            String birthDate = (currentProfile != null && currentProfile.getBirthDate() != null) ? 
                              currentProfile.getBirthDate().toString() : "Belum ditetapkan";
            gbc.gridx = 1;
            panel.add(new JLabel(birthDate), gbc);

            gbc.gridx = 0; gbc.gridy = 5;
            panel.add(new JLabel("Alamat:"), gbc);
            String address = (currentProfile != null && currentProfile.getAddress() != null) ? 
                            currentProfile.getAddress() : "Belum ditetapkan";
            gbc.gridx = 1;
            panel.add(new JLabel(address), gbc);
        }
    }

    private void createEditForm(JPanel panel, GridBagConstraints gbc) {
        phoneField = new JTextField(20);
        if (currentProfile != null && currentProfile.getPhone() != null) {
            phoneField.setText(currentProfile.getPhone());
        }

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("No. Telefon:"), gbc);
        gbc.gridx = 1;
        panel.add(phoneField, gbc);

        if ("CUSTOMER".equals(role)) {
            genderCombo = new JComboBox<>(new String[]{"Lelaki", "Perempuan"});
            if (currentProfile != null && currentProfile.getGender() != null) {
                genderCombo.setSelectedItem(currentProfile.getGender());
            }

            birthDateField = new JTextField(20);
            if (currentProfile != null && currentProfile.getBirthDate() != null) {
                birthDateField.setText(currentProfile.getBirthDate().toString());
            } else {
                birthDateField.setText("YYYY-MM-DD");
            }

            addressField = new JTextField(20);
            if (currentProfile != null && currentProfile.getAddress() != null) {
                addressField.setText(currentProfile.getAddress());
            }

            gbc.gridx = 0; gbc.gridy = 3;
            panel.add(new JLabel("Jantina:"), gbc);
            gbc.gridx = 1;
            panel.add(genderCombo, gbc);

            gbc.gridx = 0; gbc.gridy = 4;
            panel.add(new JLabel("Tarikh Lahir (YYYY-MM-DD):"), gbc);
            gbc.gridx = 1;
            panel.add(birthDateField, gbc);

            gbc.gridx = 0; gbc.gridy = 5;
            panel.add(new JLabel("Alamat:"), gbc);
            gbc.gridx = 1;
            panel.add(addressField, gbc);
        }
    }

    private void handleSave(JButton actionBtn) {
        String phone = phoneField.getText().trim();
        if (phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nombor telefon diperlukan!", "Ralat", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if ("CUSTOMER".equals(role)) {
            String gender = (String) genderCombo.getSelectedItem();
            String address = addressField.getText().trim();
            String dateStr = birthDateField.getText().trim();

            if (address.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Alamat diperlukan!", "Ralat", JOptionPane.WARNING_MESSAGE);
                return;
            }

            LocalDate birthDate;
            try {
                birthDate = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Format tarikh salah! Gunakan YYYY-MM-DD", "Ralat", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                ProfileController controller = new ProfileController();
                controller.saveProfileWithBirthDate(userId, role, gender, phone, address, birthDate);
                loadProfileData();
                isEditMode = false;
                refreshContent();
                actionBtn.setText("Kemaskini Profil");
                JOptionPane.showMessageDialog(this, "Profil berjaya dikemaskini!", "Berjaya", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Ralat: " + ex.getMessage(), "Ralat", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        } else {
            // Tailor
            try {
                ProfileController controller = new ProfileController();
                controller.saveProfile(userId, role, null, phone, null);
                loadProfileData();
                isEditMode = false;
                refreshContent();
                actionBtn.setText("Kemaskini Profil");
                JOptionPane.showMessageDialog(this, "Profil berjaya dikemaskini!", "Berjaya", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Ralat: " + ex.getMessage(), "Ralat", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void styleButton(JButton btn, Color bgColor) {
        btn.setFont(StyleUtil.BUTTON_FONT);
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setPreferredSize(new Dimension(180, 38));
    }

    protected void navigateTo(JPanel panel) {
        if (Main.mainFrame != null) {
            Main.mainFrame.setContentPane(panel);
            Main.mainFrame.revalidate();
            Main.mainFrame.repaint();
        }
    }
}