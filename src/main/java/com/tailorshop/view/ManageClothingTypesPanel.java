// com.tailorshop.view.ManageClothingTypesPanel.java
package com.tailorshop.view;

import com.tailorshop.controller.ClothingTypeController;
import com.tailorshop.controller.ClothingCategoryController;
import com.tailorshop.controller.MeasurementTemplateController;
import com.tailorshop.controller.MeasurementFieldController;
import com.tailorshop.main.Main;
import com.tailorshop.model.ClothingType;
import com.tailorshop.model.ClothingCategory;
import com.tailorshop.model.MeasurementTemplate;
import com.tailorshop.model.MeasurementField;
import com.tailorshop.util.StyleUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ManageClothingTypesPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private final String currentUserId;
    private final Runnable onBack;
    private JTable table;
    private DefaultTableModel tableModel;

    public ManageClothingTypesPanel(String currentUserId, Runnable onBack) {
        this.currentUserId = currentUserId;
        this.onBack = onBack;
        initializeUI();
        loadClothingTypes();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(StyleUtil.BG_LIGHT);

        JLabel header = new JLabel("PENGURUSAN JENIS PAKAIAN", JLabel.CENTER);
        header.setFont(StyleUtil.TITLE_FONT);
        header.setBorder(BorderFactory.createEmptyBorder(30, 20, 20, 20));
        add(header, BorderLayout.NORTH);

        // Tambah lajur "Jantina"
        String[] columns = {"Nama Jenis", "Jantina", "Kategori", "Penerangan", "Tindakan"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        JButton addButton = new JButton("Tambah Jenis Pakaian");
        JButton backBtn = new JButton("❮ Kembali");

        styleButton(addButton, StyleUtil.BOSS_COLOR);
        styleButton(backBtn, Color.GRAY);

        addButton.addActionListener(e -> showAddTypeDialog());
        backBtn.addActionListener(e -> onBack.run());

        buttonPanel.add(backBtn);
        buttonPanel.add(addButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadClothingTypes() {
        try {
            ClothingTypeController controller = new ClothingTypeController();
            List<ClothingType> types = controller.getAllClothingTypes();
            
            ClothingCategoryController catController = new ClothingCategoryController();
            List<ClothingCategory> categories = catController.getAllCategories();
            
            java.util.Map<Integer, String> categoryMap = new java.util.HashMap<>();
            for (ClothingCategory cat : categories) {
                categoryMap.put(cat.getId(), cat.getName());
            }

            tableModel.setRowCount(0);

            for (ClothingType type : types) {
                String categoryName = categoryMap.getOrDefault(type.getCategoryId(), "Tidak Diketahui");
                String genderDisplay = type.getGender() != null ? type.getGender() : "Unisex";

                Object[] row = {
                    type.getName(),
                    genderDisplay,
                    categoryName,
                    type.getDescription() != null ? type.getDescription() : "",
                    "Urus Ukuran"
                };
                tableModel.addRow(row);
            }

            // Update index tindakan ke column 4 (selepas tambah "Jantina")
            table.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    int row = table.rowAtPoint(evt.getPoint());
                    int col = table.columnAtPoint(evt.getPoint());
                    if (col == 4 && row >= 0) { // Column "Tindakan" sekarang di index 4
                        ClothingType selected = types.get(row);
                        navigateTo(new ManageMeasurementFieldsPanel(selected.getId(), selected.getName(), () -> 
                            navigateTo(new ManageClothingTypesPanel(currentUserId, onBack))
                        ));
                    }
                }
            });

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memuatkan senarai jenis pakaian.", "Ralat", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void showAddTypeDialog() {
        ClothingCategoryController catController = new ClothingCategoryController();
        List<ClothingCategory> categories = catController.getAllCategories();
        
        String[] categoryOptions = categories.stream()
            .map(ClothingCategory::getName)
            .toArray(String[]::new);
        
        String[] optionsWithNew = new String[categoryOptions.length + 1];
        System.arraycopy(categoryOptions, 0, optionsWithNew, 0, categoryOptions.length);
        optionsWithNew[optionsWithNew.length - 1] = "New";

        String selectedCategoryName = (String) JOptionPane.showInputDialog(
            this,
            "Pilih kategori untuk jenis pakaian:",
            "Kategori Jenis Pakaian",
            JOptionPane.QUESTION_MESSAGE,
            null,
            optionsWithNew,
            optionsWithNew[0]
        );
        
        if (selectedCategoryName == null) return;

        if ("New".equals(selectedCategoryName)) {
            handleCreateNewCategory();
        } else {
            handleCreateWithType(selectedCategoryName);
        }
    }

    private void handleCreateNewCategory() {
        String newCategoryName = JOptionPane.showInputDialog(
            this,
            "Masukkan nama kategori baharu:",
            "Kategori Baharu"
        );
        
        if (newCategoryName == null || newCategoryName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama kategori diperlukan!", "Ralat", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            ClothingCategory newCategory = new ClothingCategory();
            newCategory.setName(newCategoryName.trim());
            newCategory.setCreatedBy(currentUserId);
            
            ClothingCategoryController catController = new ClothingCategoryController();
            int categoryId = catController.saveCategory(newCategory);
            
            List<MeasurementTemplate> selectedTemplates = selectMeasurementsFromList();
            if (selectedTemplates == null) return;
            
            // ✅ FORM INPUT DENGAN JANTINA
            JTextField nameField = new JTextField(25);
            JComboBox<String> genderCombo = new JComboBox<>(new String[]{"Lelaki", "Perempuan", "Unisex"});
            JTextArea descArea = new JTextArea(2, 25);
            descArea.setLineWrap(true);
            descArea.setWrapStyleWord(true);

            JPanel panel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(8, 8, 8, 8);
            gbc.anchor = GridBagConstraints.WEST;

            gbc.gridx = 0; gbc.gridy = 0;
            panel.add(new JLabel("Nama Jenis Pakaian:"), gbc);
            gbc.gridx = 1;
            panel.add(nameField, gbc);

            gbc.gridx = 0; gbc.gridy = 1;
            panel.add(new JLabel("Jantina:"), gbc);
            gbc.gridx = 1;
            panel.add(genderCombo, gbc);

            gbc.gridx = 0; gbc.gridy = 2;
            panel.add(new JLabel("Penerangan (Opsional):"), gbc);
            gbc.gridx = 1;
            JScrollPane descScroll = new JScrollPane(descArea);
            descScroll.setPreferredSize(new Dimension(250, 60));
            panel.add(descScroll, gbc);

            int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Tambah Jenis Pakaian (" + newCategoryName + ")",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
            );

            if (result != JOptionPane.OK_OPTION) return;

            String typeName = nameField.getText().trim();
            String gender = (String) genderCombo.getSelectedItem();
            String desc = descArea.getText().trim();

            if (typeName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nama jenis pakaian diperlukan!", "Ralat", JOptionPane.WARNING_MESSAGE);
                return;
            }

            ClothingType type = new ClothingType();
            type.setName(typeName.trim());
            type.setGender(gender);
            type.setCategoryId(categoryId);
            type.setDescription(desc.isEmpty() ? null : desc);
            type.setCreatedBy(currentUserId);
            
            ClothingTypeController typeController = new ClothingTypeController();
            int typeId = typeController.saveClothingType(type);
            
            saveSelectedMeasurementsToFields(typeId, selectedTemplates, currentUserId);
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ralat: " + ex.getMessage(), "Ralat", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void handleCreateWithType(String categoryName) {
        // ✅ FORM INPUT DENGAN JANTINA
        JTextField nameField = new JTextField(25);
        JComboBox<String> genderCombo = new JComboBox<>(new String[]{"Lelaki", "Perempuan", "Unisex"});
        JTextArea descArea = new JTextArea(2, 25);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Nama Jenis Pakaian:"), gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Jantina:"), gbc);
        gbc.gridx = 1;
        panel.add(genderCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Penerangan (Opsional):"), gbc);
        gbc.gridx = 1;
        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setPreferredSize(new Dimension(250, 60));
        panel.add(descScroll, gbc);

        int result = JOptionPane.showConfirmDialog(
            this,
            panel,
            "Tambah Jenis Pakaian (" + categoryName + ")",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String gender = (String) genderCombo.getSelectedItem();
            String desc = descArea.getText().trim();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nama jenis pakaian diperlukan!", "Ralat", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                ClothingCategoryController catController = new ClothingCategoryController();
                int categoryId = catController.getCategoryIdByName(categoryName);
                
                ClothingType type = new ClothingType();
                type.setName(name);
                type.setGender(gender);
                type.setCategoryId(categoryId);
                type.setDescription(desc.isEmpty() ? null : desc);
                type.setCreatedBy(currentUserId);
                
                ClothingTypeController typeController = new ClothingTypeController();
                int typeId = typeController.saveClothingType(type);
                
                typeController.initializeDefaultMeasurements(typeId, categoryId);
                
                navigateTo(new ManageMeasurementFieldsPanel(typeId, name, () -> 
                    navigateTo(new ManageClothingTypesPanel(currentUserId, onBack))
                ));
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Ralat: " + ex.getMessage(), "Ralat", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private List<MeasurementTemplate> selectMeasurementsFromList() {
        try {
            MeasurementTemplateController templateController = new MeasurementTemplateController();
            List<MeasurementTemplate> allTemplates = templateController.getAllTemplates();
            
            if (allTemplates.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tiada jenis ukuran tersedia!", "Ralat", JOptionPane.WARNING_MESSAGE);
                return null;
            }
            
            JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            List<JCheckBox> checkBoxes = new ArrayList<>();
            for (MeasurementTemplate template : allTemplates) {
                JCheckBox cb = new JCheckBox(template.getFieldName() + " (" + template.getUnit() + ")");
                cb.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                checkBoxes.add(cb);
                panel.add(cb);
            }
            
            JScrollPane scrollPane = new JScrollPane(panel);
            scrollPane.setPreferredSize(new Dimension(350, 300));
            
            int result = JOptionPane.showConfirmDialog(
                this,
                scrollPane,
                "Pilih Jenis Ukuran untuk Kategori Baharu",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
            );
            
            if (result == JOptionPane.OK_OPTION) {
                List<MeasurementTemplate> selected = new ArrayList<>();
                for (int i = 0; i < checkBoxes.size(); i++) {
                    if (checkBoxes.get(i).isSelected()) {
                        selected.add(allTemplates.get(i));
                    }
                }
                
                if (selected.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Sila pilih sekurang-kurangnya satu ukuran!", "Amaran", JOptionPane.WARNING_MESSAGE);
                    return selectMeasurementsFromList();
                }
                
                return selected;
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memuatkan senarai ukuran.", "Ralat", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return null;
    }

    private void saveSelectedMeasurementsToFields(int typeId, List<MeasurementTemplate> selected, String currentUserId) {
        try {
            MeasurementFieldController mfController = new MeasurementFieldController();
            for (int i = 0; i < selected.size(); i++) {
                MeasurementTemplate template = selected.get(i);
                
                MeasurementField field = new MeasurementField();
                field.setClothingTypeId(typeId);
                field.setBodyMeasurementId(template.getId());
                field.setFieldName(template.getFieldName());
                field.setUnit(template.getUnit());
                field.setRequired(true);
                field.setDisplayOrder(i + 1);
                field.setCreatedBy(currentUserId);
                
                mfController.saveMeasurementField(field);
            }
            
            navigateTo(new ManageMeasurementFieldsPanel(typeId, "Jenis Baharu", () -> 
                navigateTo(new ManageClothingTypesPanel(currentUserId, onBack))
            ));
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan medan ukuran.", "Ralat", JOptionPane.ERROR_MESSAGE);
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