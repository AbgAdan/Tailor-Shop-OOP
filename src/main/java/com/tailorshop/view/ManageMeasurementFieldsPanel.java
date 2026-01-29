// com.tailorshop.view.ManageMeasurementFieldsPanel.java
package com.tailorshop.view;

import com.tailorshop.controller.MeasurementFieldController;
import com.tailorshop.controller.MeasurementTemplateController;
import com.tailorshop.main.Main;
import com.tailorshop.model.MeasurementField;
import com.tailorshop.model.MeasurementTemplate;
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
        // ✅ Hanya benarkan pilihan dari senarai standard
        selectFromStandardList();
    }
    
    private void selectFromStandardList() {
        try {
            MeasurementTemplateController templateController = new MeasurementTemplateController();
            List<MeasurementTemplate> templates = templateController.getAllTemplates();
            
            if (templates.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tiada ukuran standard tersedia!", "Ralat", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            JList<MeasurementTemplate> list = new JList<>(templates.toArray(new MeasurementTemplate[0]));
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
                "Pilih Ukuran Standard",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
            );
            
            if (result == JOptionPane.OK_OPTION) {
                List<MeasurementTemplate> selected = list.getSelectedValuesList();
                for (MeasurementTemplate template : selected) {
                    saveStandardField(template);
                }
                loadMeasurementFields();
                JOptionPane.showMessageDialog(this, "Medan ukuran berjaya ditambah!", "Berjaya", JOptionPane.INFORMATION_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memuatkan senarai ukuran.", "Ralat", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void saveStandardField(MeasurementTemplate template) {
        try {
            MeasurementField field = new MeasurementField();
            field.setClothingTypeId(clothingTypeId);
            field.setBodyMeasurementId(template.getId()); // ✅ int sahaja, tiada null
            field.setFieldName(template.getFieldName());
            field.setUnit(template.getUnit());
            field.setRequired(true);
            field.setDisplayOrder(getNextOrder());
            field.setCreatedBy("B0012026"); // Gantikan dengan current user ID sebenar
            
            MeasurementFieldController controller = new MeasurementFieldController();
            controller.saveMeasurementField(field);
        } catch (Exception ex) {
            throw new RuntimeException("Gagal simpan medan standard", ex);
        }
    }
    
    private void createCustomField() {
        // ✅ TIDAK DIGUNAKAN — sistem hanya benarkan ukuran standard
        JOptionPane.showMessageDialog(
            this,
            "Sistem hanya membenarkan pemilihan ukuran daripada senarai standard.\n" +
            "Sila gunakan pilihan \"Pilih dari Senarai Standard\" untuk menambah medan ukuran.",
            "Maklumat",
            JOptionPane.INFORMATION_MESSAGE
        );
        selectFromStandardList();
    }
    
    private int getNextOrder() {
        return fields.isEmpty() ? 1 : fields.stream().mapToInt(MeasurementField::getDisplayOrder).max().orElse(0) + 1;
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
                controller.deleteField(fieldId);
                loadMeasurementFields();
                JOptionPane.showMessageDialog(this, "Medan ukuran berjaya dipadam!", "Berjaya", JOptionPane.INFORMATION_MESSAGE);
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