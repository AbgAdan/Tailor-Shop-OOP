// com.tailorshop.view.FamilyMemberPanel.java
package com.tailorshop.view;

import com.tailorshop.controller.FamilyMemberController;
import com.tailorshop.controller.ClothingTypeController;
import com.tailorshop.main.Main;
import com.tailorshop.model.FamilyMember;
import com.tailorshop.model.ClothingType;
import com.tailorshop.util.StyleUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class FamilyMemberPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private final String customerId;
    private final String mainUserName;
    private final Runnable onBack;
    private JTable table;
    private DefaultTableModel tableModel;
    private List<FamilyMember> currentMembers;

    public FamilyMemberPanel(String customerId, String mainUserName, Runnable onBack) {
        this.customerId = customerId;
        this.mainUserName = mainUserName;
        this.onBack = onBack;
        initializeUI();
        loadFamilyMembers();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(StyleUtil.BG_LIGHT);

        JLabel header = new JLabel("AHLI KELUARGA", JLabel.CENTER);
        header.setFont(StyleUtil.TITLE_FONT);
        header.setBorder(BorderFactory.createEmptyBorder(30, 20, 20, 20));
        add(header, BorderLayout.NORTH);

        // Jadual
        String[] columns = {"Nama", "Umur", "Jantina"};
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
        JButton addButton = new JButton("Tambah Ahli Keluarga");
        JButton backBtn = new JButton("❮ Kembali");

        styleButton(addButton, StyleUtil.CUSTOMER_COLOR);
        styleButton(backBtn, Color.GRAY);

        addButton.addActionListener(e -> showAddDialog());
        backBtn.addActionListener(e -> onBack.run());

        buttonPanel.add(backBtn);
        buttonPanel.add(addButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Aksi klik pada jadual
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = table.rowAtPoint(evt.getPoint());
                if (row >= 0 && row < currentMembers.size()) {
                    FamilyMember member = currentMembers.get(row);
                    showClothingTypeSelection(member);
                }
            }
        });
    }

    private void loadFamilyMembers() {
        try {
            FamilyMemberController controller = new FamilyMemberController();
            currentMembers = controller.getFamilyMembers(customerId);
            tableModel.setRowCount(0);

            for (FamilyMember member : currentMembers) {
                Object[] row = {
                    member.getName(),
                    member.getAge(),
                    member.getGender()
                };
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memuatkan senarai ahli keluarga.", "Ralat", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void showClothingTypeSelection(FamilyMember member) {
        try {
            ClothingTypeController controller = new ClothingTypeController();
            List<ClothingType> types = controller.getAllClothingTypes();
            
            if (types.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tiada jenis pakaian tersedia.\nSila hubungi pentadbir.", "Makluman", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            String[] options = types.stream().map(ClothingType::getName).toArray(String[]::new);
            String selected = (String) JOptionPane.showInputDialog(
                this,
                "Pilih jenis pakaian untuk:\n" + member.getName(),
                "Pilih Jenis Pakaian",
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
            );
            
            if (selected != null) {
                ClothingType type = types.stream()
                    .filter(t -> t.getName().equals(selected))
                    .findFirst().orElse(null);
                if (type != null) {
                    navigateTo(new MeasurementPanel(
                        member.getId(),
                        type.getId(),
                        type.getName(),
                        () -> navigateTo(new FamilyMemberPanel(customerId, mainUserName, onBack))
                    ));
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memuatkan jenis pakaian.", "Ralat", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void showAddDialog() {
        // ... kod tambah ahli keluarga seperti sebelum ini ...
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