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
import com.tailorshop.util.StyleUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
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

        JLabel header = new JLabel(" 👔 UNTUK PENGURUSAN JENIS PAKAIAN", JLabel.CENTER);
        header.setFont(StyleUtil.TITLE_FONT);
        header.setBorder(BorderFactory.createEmptyBorder(30, 20, 20, 20));
        add(header, BorderLayout.NORTH);

        String[] columns = {"Nama Jenis", "Kategori", "Penerangan", "Tindakan"};
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

                Object[] row = {
                    type.getName(),
                    categoryName,
                    type.getDescription() != null ? type.getDescription() : "",
                    "Urus Ukuran"
                };
                tableModel.addRow(row);
            }

            table.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    int row = table.rowAtPoint(evt.getPoint());
                    int col = table.columnAtPoint(evt.getPoint());
                    if (col == 3 && row >= 0) {
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
            
            String typeName = JOptionPane.showInputDialog(this, "Nama Jenis Pakaian:");
            if (typeName == null || typeName.trim().isEmpty()) return;
            
            String desc = JOptionPane.showInputDialog(this, "Penerangan (Opsional):");
            
            ClothingType type = new ClothingType();
            type.setName(typeName.trim());
            type.setCategoryId(categoryId);
            type.setDescription(desc != null ? desc.trim() : null);
            type.setCreatedBy(currentUserId);
            
            ClothingTypeController typeController = new ClothingTypeController();
            int typeId = typeController.saveClothingType(type);
            
            selectAndLinkMeasurements(typeId);
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ralat: " + ex.getMessage(), "Ralat", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void handleCreateWithType(String categoryName) {
        JTextField nameField = new JTextField(25);
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

    private void selectAndLinkMeasurements(int typeId) {
        try {
            MeasurementTemplateController templateController = new MeasurementTemplateController();
            List<MeasurementTemplate> allTemplates = templateController.getAllTemplates();
            
            if (allTemplates.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tiada jenis ukuran tersedia!", "Ralat", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            JList<MeasurementTemplate> list = new JList<>(allTemplates.toArray(new MeasurementTemplate[0]));
            list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            list.setCellRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof MeasurementTemplate) {
                        MeasurementTemplate t = (MeasurementTemplate) value;
                        setText(t.getFieldName() + " (" + t.getUnit() + ")");
                    }
                    return this;
                }
            });
            
            JScrollPane scrollPane = new JScrollPane(list);
            scrollPane.setPreferredSize(new Dimension(300, 200));
            
            int result = JOptionPane.showConfirmDialog(
                this,
                scrollPane,
                "Pilih Jenis Ukuran untuk Jenis Pakaian Baharu",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
            );
            
            if (result == JOptionPane.OK_OPTION) {
                MeasurementFieldController mfController = new MeasurementFieldController();
                List<MeasurementTemplate> selected = list.getSelectedValuesList();
                for (int i = 0; i < selected.size(); i++) {
                    MeasurementTemplate template = selected.get(i);
                    mfController.linkMeasurementToType(typeId, template.getId(), true, i + 1);
                }
                
                navigateTo(new ManageMeasurementFieldsPanel(typeId, "Jenis Baharu", () -> 
                    navigateTo(new ManageClothingTypesPanel(currentUserId, onBack))
                ));
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memuatkan senarai ukuran.", "Ralat", JOptionPane.ERROR_MESSAGE);
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