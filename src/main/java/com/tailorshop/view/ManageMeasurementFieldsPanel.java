// com.tailorshop.view.ManageMeasurementFieldsPanel.java
package com.tailorshop.view;

import com.tailorshop.controller.MeasurementFieldController;
import com.tailorshop.main.Main;
import com.tailorshop.model.MeasurementField;
import com.tailorshop.util.StyleUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ManageMeasurementFieldsPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private final int clothingTypeId;
    private final String clothingTypeName;
    private final Runnable onBack;
    private JTable table;
    private DefaultTableModel tableModel;
    private List<MeasurementField> fields = new ArrayList<>();

    public ManageMeasurementFieldsPanel(int clothingTypeId, String clothingTypeName, Runnable onBack) {
        this.clothingTypeId = clothingTypeId;
        this.clothingTypeName = clothingTypeName;
        this.onBack = onBack;
        initializeUI();
        loadMeasurementFields();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(StyleUtil.BG_LIGHT);

        JLabel header = new JLabel("URUS UKURAN UNTUK: " + clothingTypeName, JLabel.CENTER);
        header.setFont(StyleUtil.TITLE_FONT);
        header.setBorder(BorderFactory.createEmptyBorder(30, 20, 20, 20));
        add(header, BorderLayout.NORTH);

        // Jadual
        String[] columns = {"Nama Medan", "Unit", "Wajib", "Susunan", "Tindakan"};
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

        // Butang
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        JButton addButton = new JButton("Tambah Medan Ukuran");
        JButton backBtn = new JButton("Kembali");

        styleButton(addButton, StyleUtil.BOSS_COLOR);
        styleButton(backBtn, Color.GRAY);

        addButton.addActionListener(e -> showAddFieldDialog());
        backBtn.addActionListener(e -> onBack.run());

        buttonPanel.add(backBtn);
        buttonPanel.add(addButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadMeasurementFields() {
        try {
            MeasurementFieldController controller = new MeasurementFieldController();
            fields = controller.getMeasurementFieldsByClothingTypeId(clothingTypeId);
            tableModel.setRowCount(0);

            for (MeasurementField field : fields) {
                Object[] row = {
                    field.getFieldName(),
                    field.getUnit(),
                    field.isRequired() ? "Ya" : "Tidak",
                    field.getDisplayOrder(),
                    "Padam"
                };
                tableModel.addRow(row);
            }

            // Tambah aksi padam
            table.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    int row = table.rowAtPoint(evt.getPoint());
                    int col = table.columnAtPoint(evt.getPoint());
                    if (col == 4 && row >= 0) {
                        confirmDelete(fields.get(row).getId());
                    }
                }
            });

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memuatkan medan ukuran.", "Ralat", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void showAddFieldDialog() {
        JTextField nameField = new JTextField();
        JTextField unitField = new JTextField("inci");
        JCheckBox requiredBox = new JCheckBox("Wajib", true);
        JTextField orderField = new JTextField("0");

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Nama Medan Ukuran:"), gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Unit (contoh: inci, cm):"), gbc);
        gbc.gridx = 1;
        panel.add(unitField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Medan Wajib:"), gbc);
        gbc.gridx = 1;
        panel.add(requiredBox, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Susunan Paparan:"), gbc);
        gbc.gridx = 1;
        panel.add(orderField, gbc);

        int result = JOptionPane.showConfirmDialog(
            this,
            panel,
            "Tambah Medan Ukuran",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String unit = unitField.getText().trim();
            boolean required = requiredBox.isSelected();
            int order = 0;

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nama medan ukuran diperlukan!", "Ralat", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                order = Integer.parseInt(orderField.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Susunan mesti nombor!", "Ralat", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                MeasurementField field = new MeasurementField();
                field.setClothingTypeId(clothingTypeId);
                field.setFieldName(name);
                field.setUnit(unit);
                field.setRequired(required);
                field.setDisplayOrder(order);

                MeasurementFieldController controller = new MeasurementFieldController();
                controller.saveMeasurementField(field);
                loadMeasurementFields();
                JOptionPane.showMessageDialog(this, "Medan ukuran berjaya ditambah!", "Berjaya", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Ralat: " + ex.getMessage(), "Ralat", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void confirmDelete(int fieldId) {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Adakah anda pasti mahu padam medan ukuran ini?",
            "Sahkan Padam",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                MeasurementFieldController controller = new MeasurementFieldController();
                // Anda perlu tambah method deleteById dalam controller
                controller.deleteField(fieldId);
                loadMeasurementFields();
                JOptionPane.showMessageDialog(this, "Medan ukuran berjaya dipadam!", "Berjaya", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Ralat: " + ex.getMessage(), "Ralat", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    // Tambah method deleteField dalam MeasurementFieldController
    // public void deleteField(int id) { dao.delete(id); }

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