// com.tailorshop.view.MeasurementPanel.java
package com.tailorshop.view;

import com.tailorshop.controller.MeasurementController;
import com.tailorshop.controller.MeasurementFieldController;
import com.tailorshop.main.Main;
import com.tailorshop.model.MeasurementField;
import com.tailorshop.util.StyleUtil;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MeasurementPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private final int familyMemberId;
    private final int clothingTypeId;
    private final String clothingTypeName;
    private final Runnable onBack;
    private Map<Integer, JTextField> fieldInputs = new HashMap<>();
    private List<MeasurementField> measurementFields;

    public MeasurementPanel(int familyMemberId, int clothingTypeId, String clothingTypeName, Runnable onBack) {
        this.familyMemberId = familyMemberId;
        this.clothingTypeId = clothingTypeId;
        this.clothingTypeName = clothingTypeName;
        this.onBack = onBack;
        initializeUI();
        loadMeasurementFields();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(StyleUtil.BG_LIGHT);

        JLabel header = new JLabel(" 📏 UKURAN UNTUK: " + clothingTypeName, JLabel.CENTER);
        header.setFont(StyleUtil.TITLE_FONT);
        header.setBorder(BorderFactory.createEmptyBorder(30, 20, 20, 20));
        add(header, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(StyleUtil.BG_LIGHT);
        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        JButton saveBtn = new JButton("Simpan Ukuran");
        JButton backBtn = new JButton("❮ Kembali");

        styleButton(saveBtn, StyleUtil.CUSTOMER_COLOR);
        styleButton(backBtn, Color.GRAY);

        saveBtn.addActionListener(e -> handleSave());
        backBtn.addActionListener(e -> onBack.run());

        buttonPanel.add(backBtn);
        buttonPanel.add(saveBtn);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadMeasurementFields() {
        try {
            MeasurementFieldController controller = new MeasurementFieldController();
            measurementFields = controller.getMeasurementFieldsByClothingTypeId(clothingTypeId);
            
            JPanel formPanel = (JPanel) getComponent(1); // BorderLayout.CENTER
            formPanel.removeAll();
            fieldInputs.clear();
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(8, 8, 8, 8);
            gbc.anchor = GridBagConstraints.WEST;
            
            int row = 0;
            for (MeasurementField field : measurementFields) {
                gbc.gridx = 0; gbc.gridy = row;
                formPanel.add(new JLabel(field.getFieldName() + " (" + field.getUnit() + "):"), gbc);
                
                JTextField input = new JTextField(10);
                gbc.gridx = 1;
                formPanel.add(input, gbc);
                
                fieldInputs.put(field.getId(), input);
                row++;
            }
            
            // Muatkan data sedia ada
            MeasurementController mc = new MeasurementController();
            Map<String, Object> existingData = mc.getMeasurement(familyMemberId, clothingTypeId);
            
            for (MeasurementField field : measurementFields) {
                JTextField input = fieldInputs.get(field.getId());
                if (input != null) {
                    String value = (String) existingData.get(field.getFieldName());
                    if (value != null) {
                        input.setText(value);
                    }
                }
            }
            
            formPanel.revalidate();
            formPanel.repaint();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memuatkan medan ukuran.", "Ralat", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void handleSave() {
        Map<String, Object> data = new HashMap<>();
        
        for (MeasurementField field : measurementFields) {
            JTextField input = fieldInputs.get(field.getId());
            if (input != null) {
                String value = input.getText().trim();
                if (value.isEmpty() && field.isRequired()) {
                    JOptionPane.showMessageDialog(this, "Medan '" + field.getFieldName() + "' diperlukan!", "Ralat", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (!value.isEmpty()) {
                    data.put(field.getFieldName(), value);
                }
            }
        }
        
        try {
            MeasurementController controller = new MeasurementController();
            controller.saveMeasurement(familyMemberId, clothingTypeId, data);
            JOptionPane.showMessageDialog(this, "Ukuran berjaya disimpan!", "Berjaya", JOptionPane.INFORMATION_MESSAGE);
            onBack.run();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ralat: " + ex.getMessage(), "Ralat", JOptionPane.ERROR_MESSAGE);
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