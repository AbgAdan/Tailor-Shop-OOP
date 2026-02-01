// com.tailorshop.view.CreateOrderPanel.java
package com.tailorshop.view;

import com.tailorshop.controller.FamilyMemberController;
import com.tailorshop.controller.OrderController;
import com.tailorshop.main.Main;
import com.tailorshop.model.FamilyMember;
import com.tailorshop.util.StyleUtil;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class CreateOrderPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private final String customerId;
    private final Runnable onBack;
    private JComboBox<String> familyMemberCombo; // ✅ GUNA STRING
    private JComboBox<String> clothingTypeCombo;
    private JSpinner dueDateSpinner;
    private JTextArea notesArea;
    private List<FamilyMember> familyMembers; // ✅ KEEP REFERENCE

    public CreateOrderPanel(String customerId, Runnable onBack) {
        this.customerId = customerId;
        this.onBack = onBack;
        initializeUI();
        loadFamilyMembers();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(StyleUtil.BG_LIGHT);

        // Header
        JLabel header = new JLabel("BUAT PESANAN BAHARU", JLabel.CENTER);
        header.setFont(StyleUtil.TITLE_FONT);
        header.setBorder(BorderFactory.createEmptyBorder(30, 20, 20, 20));
        add(header, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(StyleUtil.BG_LIGHT);
        formPanel.setBorder(BorderFactory.createTitledBorder("Maklumat Pesanan"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Ahli Keluarga
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Ahli Keluarga:"), gbc);
        gbc.gridx = 1;
        familyMemberCombo = new JComboBox<>(); // ✅ STRING COMBO
        familyMemberCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        familyMemberCombo.setPreferredSize(new Dimension(250, 30));
        familyMemberCombo.addActionListener(e -> onFamilyMemberSelected());
        formPanel.add(familyMemberCombo, gbc);

        // Jenis Pakaian
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Jenis Pakaian:"), gbc);
        gbc.gridx = 1;
        clothingTypeCombo = new JComboBox<>();
        clothingTypeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        clothingTypeCombo.setPreferredSize(new Dimension(250, 30));
        clothingTypeCombo.setEnabled(false);
        formPanel.add(clothingTypeCombo, gbc);

        // Tarikh Siap
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Tarikh Siap:"), gbc);
        gbc.gridx = 1;
        
        SpinnerDateModel dateModel = new SpinnerDateModel();
        dateModel.setCalendarField(java.util.Calendar.DAY_OF_MONTH);
        dueDateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor editor = new JSpinner.DateEditor(dueDateSpinner, "dd/MM/yyyy");
        dueDateSpinner.setEditor(editor);
        dueDateSpinner.setValue(java.util.Date.from(LocalDate.now().plusDays(7).atStartOfDay().atZone(java.time.ZoneId.systemDefault()).toInstant()));
        dueDateSpinner.setPreferredSize(new Dimension(250, 30));
        formPanel.add(dueDateSpinner, gbc);

        // Nota
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        formPanel.add(new JLabel("Nota:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        notesArea = new JTextArea(4, 20);
        notesArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        JScrollPane notesScrollPane = new JScrollPane(notesArea);
        notesScrollPane.setPreferredSize(new Dimension(250, 80));
        formPanel.add(notesScrollPane, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        JButton backBtn = new JButton("❮ Kembali");
        JButton submitBtn = new JButton("Hantar Pesanan");

        styleButton(backBtn, Color.GRAY);
        styleButton(submitBtn, StyleUtil.CUSTOMER_COLOR);

        backBtn.addActionListener(e -> onBack.run());
        submitBtn.addActionListener(e -> handleSubmit());

        buttonPanel.add(backBtn);
        buttonPanel.add(submitBtn);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadFamilyMembers() {
        try {
            FamilyMemberController controller = new FamilyMemberController();
            familyMembers = controller.getFamilyMembers(customerId); // ✅ KEEP LIST
            
            familyMemberCombo.removeAllItems();
            for (FamilyMember member : familyMembers) {
                familyMemberCombo.addItem(member.getName()); // ✅ ADD NAME ONLY
            }
            
            if (!familyMembers.isEmpty()) {
                familyMemberCombo.setSelectedIndex(0);
                onFamilyMemberSelected();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memuatkan senarai ahli keluarga.", "Ralat", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void onFamilyMemberSelected() {
        String selectedName = (String) familyMemberCombo.getSelectedItem(); // ✅ GET NAME
        if (selectedName != null) {
            // Find the corresponding FamilyMember object
            FamilyMember selectedMember = familyMembers.stream()
                .filter(m -> m.getName().equals(selectedName))
                .findFirst()
                .orElse(null);
                
            if (selectedMember != null) {
                try {
                    FamilyMemberController controller = new FamilyMemberController();
                    List<String> clothingTypes = controller.getClothingTypeNamesByGender(selectedMember.getGender());
                    
                    clothingTypeCombo.removeAllItems();
                    if (clothingTypes.isEmpty()) {
                        clothingTypeCombo.addItem("Tiada jenis pakaian tersedia");
                    } else {
                        for (String type : clothingTypes) {
                            clothingTypeCombo.addItem(type);
                        }
                    }
                    clothingTypeCombo.setEnabled(!clothingTypes.isEmpty());
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Gagal memuatkan jenis pakaian.", "Ralat", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
        }
    }

    private void handleSubmit() {
        try {
            String selectedName = (String) familyMemberCombo.getSelectedItem(); // ✅ GET NAME
            if (selectedName == null || selectedName.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Sila pilih ahli keluarga.", "Ralat", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Find the corresponding FamilyMember to get ID
            FamilyMember selectedMember = familyMembers.stream()
                .filter(m -> m.getName().equals(selectedName))
                .findFirst()
                .orElse(null);
                
            if (selectedMember == null) {
                JOptionPane.showMessageDialog(this, "Ahli keluarga tidak ditemui.", "Ralat", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String selectedClothingType = (String) clothingTypeCombo.getSelectedItem();
            if (selectedClothingType == null || "Tiada jenis pakaian tersedia".equals(selectedClothingType)) {
                JOptionPane.showMessageDialog(this, "Tiada jenis pakaian tersedia untuk ahli keluarga ini.", "Ralat", JOptionPane.WARNING_MESSAGE);
                return;
            }

            java.util.Date utilDate = (java.util.Date) dueDateSpinner.getValue();
            LocalDate dueDate = utilDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            
            if (dueDate.isBefore(LocalDate.now())) {
                JOptionPane.showMessageDialog(this, "Tarikh siap mesti hari ini atau masa hadapan.", "Ralat", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String notes = notesArea.getText().trim();

            FamilyMemberController fmController = new FamilyMemberController();
            int clothingTypeId = fmController.getClothingTypeIdByName(selectedClothingType);
            
            if (clothingTypeId == -1) {
                JOptionPane.showMessageDialog(this, "Jenis pakaian tidak ditemui dalam sistem.", "Ralat", JOptionPane.ERROR_MESSAGE);
                return;
            }

            OrderController orderController = new OrderController();
            orderController.createOrder(customerId, selectedMember.getId(), clothingTypeId, dueDate, notes);
            
            JOptionPane.showMessageDialog(this, "Pesanan berjaya dihantar!\nID Pesanan: " + 
                getOrderPreviewId(), "Berjaya", JOptionPane.INFORMATION_MESSAGE);
            
            onBack.run();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal menghantar pesanan: " + e.getMessage(), "Ralat", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private String getOrderPreviewId() {
        String prefix = "ORD";
        String year = String.valueOf(java.time.Year.now().getValue());
        return prefix + "XXX" + year;
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

    protected void navigateTo(JPanel panel) {
        if (Main.mainFrame != null) {
            Main.mainFrame.setContentPane(panel);
            Main.mainFrame.revalidate();
            Main.mainFrame.repaint();
        }
    }
}