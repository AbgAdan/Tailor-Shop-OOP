// com.tailorshop.view.MeasurementPanel.java
package com.tailorshop.view;

import com.tailorshop.controller.FamilyMemberController;
import com.tailorshop.controller.UserController;
import com.tailorshop.main.Main;
import com.tailorshop.model.FamilyMember;
import com.tailorshop.model.User;
import com.tailorshop.util.StyleUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class MeasurementPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private final FamilyMember member;
    private final String currentUserId;
    private final String userRole;
    private final Runnable onBack;
    private JTable measurementTable;
    private DefaultTableModel tableModel;
    private Map<String, String> currentMeasurements;
    private String currentClothingTypeName = "Ukuran Asas";
    private Integer currentClothingTypeId = null; // ✅ NULL untuk ukuran asas

    public MeasurementPanel(FamilyMember member, String currentUserId, String userRole, Runnable onBack) {
        this.member = member;
        this.currentUserId = currentUserId;
        this.userRole = userRole;
        this.onBack = onBack;
        initializeUI();
        loadBasicMeasurements();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(StyleUtil.BG_LIGHT);

        JLabel header = new JLabel("UKURAN BADAN - " + member.getName(), JLabel.CENTER);
        header.setFont(StyleUtil.TITLE_FONT);
        header.setBorder(BorderFactory.createEmptyBorder(30, 20, 20, 20));
        add(header, BorderLayout.NORTH);

        String[] columns = {"Medan Ukuran", "Nilai (inci)"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        measurementTable = new JTable(tableModel);
        measurementTable.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(measurementTable);
        add(scrollPane, BorderLayout.CENTER);

        createViewModeButtons();
    }

    private void createViewModeButtons() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        
        JButton backBtn = new JButton("❮ Kembali");
        styleButton(backBtn, Color.GRAY);
        backBtn.addActionListener(e -> onBack.run());
        buttonPanel.add(backBtn);

        JButton selectTypeBtn = new JButton("Pilih Jenis Pakaian");
        styleButton(selectTypeBtn, StyleUtil.CUSTOMER_COLOR);
        selectTypeBtn.addActionListener(e -> showClothingTypeSelection());
        buttonPanel.add(selectTypeBtn);

        if ("CUSTOMER".equalsIgnoreCase(userRole)) {
            JButton grantAccessBtn = new JButton("Benarkan Tailor Akses");
            styleButton(grantAccessBtn, new Color(255, 165, 0));
            grantAccessBtn.addActionListener(e -> grantTailorAccess());
            buttonPanel.add(grantAccessBtn);
        }

        if (canEditMeasurements()) {
            JButton editBtn = new JButton("Edit Ukuran");
            styleButton(editBtn, StyleUtil.TAILOR_COLOR);
            editBtn.addActionListener(e -> enableEditingMode());
            buttonPanel.add(editBtn);
        }

        // Ganti butang panel
        Component[] components = getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel && ((JPanel) comp).getComponentCount() > 0 && 
                ((JPanel) comp).getComponent(0) instanceof JButton) {
                remove(comp);
                break;
            }
        }
        add(buttonPanel, BorderLayout.SOUTH);
        revalidate();
        repaint();
    }

    private void createEditModeButtons() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        
        JButton backBtn = new JButton("❮ Kembali");
        styleButton(backBtn, Color.GRAY);
        backBtn.addActionListener(e -> onBack.run());
        buttonPanel.add(backBtn);

        JButton selectTypeBtn = new JButton("Pilih Jenis Pakaian");
        styleButton(selectTypeBtn, StyleUtil.CUSTOMER_COLOR);
        selectTypeBtn.addActionListener(e -> showClothingTypeSelectionForEdit());
        buttonPanel.add(selectTypeBtn);
        
        JButton saveBtn = new JButton("Simpan Ukuran");
        styleButton(saveBtn, StyleUtil.TAILOR_COLOR);
        saveBtn.addActionListener(e -> saveMeasurements());
        buttonPanel.add(saveBtn);
        
        // Ganti butang panel
        Component[] components = getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel && ((JPanel) comp).getComponentCount() > 0 && 
                ((JPanel) comp).getComponent(0) instanceof JButton) {
                remove(comp);
                break;
            }
        }
        add(buttonPanel, BorderLayout.SOUTH);
        revalidate();
        repaint();
    }

    private void loadBasicMeasurements() {
        try {
            FamilyMemberController controller = new FamilyMemberController();
            currentMeasurements = controller.getBasicBodyMeasurements(member.getId());
            currentClothingTypeName = "Ukuran Asas";
            currentClothingTypeId = null;
            displayMeasurementsInTable();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memuatkan ukuran asas.", "Ralat", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void showClothingTypeSelection() {
        try {
            FamilyMemberController controller = new FamilyMemberController();
            List<String> clothingTypes = controller.getClothingTypeNamesByGender(member.getGender());
            
            String[] options = clothingTypes.isEmpty() ? 
                new String[]{"Ukuran Asas"} : 
                new String[clothingTypes.size() + 1];
            
            if (!clothingTypes.isEmpty()) {
                options[0] = "Ukuran Asas";
                for (int i = 0; i < clothingTypes.size(); i++) {
                    options[i + 1] = clothingTypes.get(i);
                }
            }

            String selected = (String) JOptionPane.showInputDialog(
                this, "Pilih jenis pakaian:", "Pilih Jenis Pakaian",
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]
            );
            
            if (selected != null) {
                loadMeasurementsForType(selected);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memuatkan jenis pakaian.", "Ralat", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void showClothingTypeSelectionForEdit() {
        try {
            FamilyMemberController controller = new FamilyMemberController();
            List<String> clothingTypes = controller.getClothingTypeNamesByGender(member.getGender());
            
            String[] options = clothingTypes.isEmpty() ? 
                new String[]{"Ukuran Asas"} : 
                new String[clothingTypes.size() + 1];
            
            if (!clothingTypes.isEmpty()) {
                options[0] = "Ukuran Asas";
                for (int i = 0; i < clothingTypes.size(); i++) {
                    options[i + 1] = clothingTypes.get(i);
                }
            }

            String selected = (String) JOptionPane.showInputDialog(
                this, "Pilih jenis pakaian untuk edit:", "Edit Ukuran",
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]
            );
            
            if (selected != null) {
                loadMeasurementsForEdit(selected);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memuatkan jenis pakaian.", "Ralat", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void loadMeasurementsForType(String clothingTypeName) {
        try {
            FamilyMemberController controller = new FamilyMemberController();
            
            if ("Ukuran Asas".equals(clothingTypeName)) {
                currentMeasurements = controller.getBasicBodyMeasurements(member.getId());
                currentClothingTypeName = "Ukuran Asas";
                currentClothingTypeId = null;
            } else {
                int clothingTypeId = controller.getClothingTypeIdByName(clothingTypeName);
                currentMeasurements = controller.getMeasurementsByTemplate(member.getId(), clothingTypeId);
                currentClothingTypeName = clothingTypeName;
                currentClothingTypeId = clothingTypeId;
            }
            
            if (currentMeasurements.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tiada ukuran direkodkan.", "Makluman", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            displayMeasurementsInTable();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memuatkan ukuran.", "Ralat", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void loadMeasurementsForEdit(String clothingTypeName) {
        try {
            FamilyMemberController controller = new FamilyMemberController();
            Map<String, String> measurements;
            
            if ("Ukuran Asas".equals(clothingTypeName)) {
                measurements = controller.getBasicBodyMeasurements(member.getId());
                currentClothingTypeName = "Ukuran Asas";
                currentClothingTypeId = null;
            } else {
                int clothingTypeId = controller.getClothingTypeIdByName(clothingTypeName);
                measurements = controller.getMeasurementsByTemplate(member.getId(), clothingTypeId);
                currentClothingTypeName = clothingTypeName;
                currentClothingTypeId = clothingTypeId;
            }
            
            tableModel.setRowCount(0);
            for (Map.Entry<String, String> entry : measurements.entrySet()) {
                Object[] row = {entry.getKey(), entry.getValue()};
                tableModel.addRow(row);
            }
            
            JLabel header = new JLabel("EDIT UKURAN - " + member.getName() + " (" + currentClothingTypeName + ")", JLabel.CENTER);
            header.setFont(StyleUtil.TITLE_FONT);
            header.setBorder(BorderFactory.createEmptyBorder(30, 20, 20, 20));
            
            Component[] components = getComponents();
            for (Component comp : components) {
                if (comp instanceof JLabel) {
                    remove(comp);
                    break;
                }
            }
            add(header, BorderLayout.NORTH);
            revalidate();
            repaint();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memuatkan ukuran untuk edit.", "Ralat", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void displayMeasurementsInTable() {
        tableModel.setRowCount(0);
        if (currentMeasurements != null) {
            for (Map.Entry<String, String> entry : currentMeasurements.entrySet()) {
                Object[] row = {entry.getKey(), entry.getValue()};
                tableModel.addRow(row);
            }
        }
        
        JLabel header = new JLabel("UKURAN BADAN - " + member.getName() + " (" + currentClothingTypeName + ")", JLabel.CENTER);
        header.setFont(StyleUtil.TITLE_FONT);
        header.setBorder(BorderFactory.createEmptyBorder(30, 20, 20, 20));
        
        Component[] components = getComponents();
        for (Component comp : components) {
            if (comp instanceof JLabel) {
                remove(comp);
                break;
            }
        }
        add(header, BorderLayout.NORTH);
        revalidate();
        repaint();
    }

    private boolean canEditMeasurements() {
        if ("BOSS".equalsIgnoreCase(userRole)) return true;
        if ("TAILOR".equalsIgnoreCase(userRole)) {
            FamilyMemberController controller = new FamilyMemberController();
            return controller.isTailorAuthorized(member.getId(), currentUserId);
        }
        return false;
    }

    private void enableEditingMode() {
        try {
            FamilyMemberController controller = new FamilyMemberController();
            currentMeasurements = controller.getBasicBodyMeasurements(member.getId());
            currentClothingTypeName = "Ukuran Asas";
            currentClothingTypeId = null;
            
            tableModel = new DefaultTableModel(new String[]{"Medan Ukuran", "Nilai (inci)"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return column == 1;
                }
            };
            
            for (Map.Entry<String, String> entry : currentMeasurements.entrySet()) {
                Object[] row = {entry.getKey(), entry.getValue()};
                tableModel.addRow(row);
            }
            
            measurementTable.setModel(tableModel);
            
            JLabel header = new JLabel("EDIT UKURAN - " + member.getName() + " (" + currentClothingTypeName + ")", JLabel.CENTER);
            header.setFont(StyleUtil.TITLE_FONT);
            header.setBorder(BorderFactory.createEmptyBorder(30, 20, 20, 20));
            
            Component[] components = getComponents();
            for (Component comp : components) {
                if (comp instanceof JLabel) {
                    remove(comp);
                    break;
                }
            }
            add(header, BorderLayout.NORTH);
            createEditModeButtons();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memuatkan ukuran untuk edit.", "Ralat", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void saveMeasurements() {
        try {
            Map<String, String> updatedMeasurements = new java.util.HashMap<>();
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                String fieldName = (String) tableModel.getValueAt(i, 0);
                String value = (String) tableModel.getValueAt(i, 1);
                updatedMeasurements.put(fieldName, value != null ? value.trim() : "");
            }
            
            FamilyMemberController controller = new FamilyMemberController();
            // ✅ HANTAR Integer (boleh null) terus - TIDAK GUNA .intValue()
            if (controller.updateMeasurementsByTemplate(member.getId(), currentClothingTypeId, updatedMeasurements)) {
                JOptionPane.showMessageDialog(this, "Ukuran berjaya dikemaskini!", "Berjaya", JOptionPane.INFORMATION_MESSAGE);
                currentMeasurements = updatedMeasurements;
                displayMeasurementsInTable();
                createViewModeButtons();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal mengemaskini ukuran.", "Ralat", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ralat semasa menyimpan: " + e.getMessage(), "Ralat", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void grantTailorAccess() {
        try {
            UserController userController = new UserController();
            List<User> tailors = userController.getAllTailors();
            
            if (tailors.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tiada tailor tersedia.", "Makluman", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            String[] tailorNames = tailors.stream().map(User::getName).toArray(String[]::new);
            String selectedName = (String) JOptionPane.showInputDialog(
                this, "Pilih Tailor:", "Benarkan Akses Tailor",
                JOptionPane.QUESTION_MESSAGE, null, tailorNames, tailorNames[0]
            );
            
            if (selectedName != null) {
                User selectedTailor = tailors.stream()
                    .filter(t -> t.getName().equals(selectedName))
                    .findFirst()
                    .orElse(null);
                
                if (selectedTailor != null) {
                    FamilyMemberController controller = new FamilyMemberController();
                    if (controller.grantTailorAccess(member.getId(), selectedTailor.getId())) {
                        JOptionPane.showMessageDialog(this, "Akses berjaya diberikan kepada " + selectedName + "!", "Berjaya", JOptionPane.INFORMATION_MESSAGE);
                        createViewModeButtons();
                    } else {
                        JOptionPane.showMessageDialog(this, "Gagal memberikan akses.", "Ralat", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memuatkan senarai tailor.", "Ralat", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
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

    protected void navigateTo(JPanel panel) {
        if (Main.mainFrame != null) {
            Main.mainFrame.setContentPane(panel);
            Main.mainFrame.revalidate();
            Main.mainFrame.repaint();
        }
    }
}