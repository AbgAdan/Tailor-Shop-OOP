// com.tailorshop.view.FamilyMemberPanel.java
package com.tailorshop.view;

import com.tailorshop.controller.FamilyMemberController;
import com.tailorshop.main.Main;
import com.tailorshop.model.FamilyMember;
import com.tailorshop.util.StyleUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class FamilyMemberPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private final String customerId;
    private final String mainUserName;
    private final Runnable onBack;
    private JTable table;
    private DefaultTableModel tableModel;
    private List<FamilyMember> currentMembers;
    private boolean isEditMode = false;

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

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel(" 👨‍👩‍👧‍👦 AHLI KELUARGA", JLabel.LEFT);
        titleLabel.setFont(StyleUtil.TITLE_FONT);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JButton editBtn = new JButton("Edit");
        editBtn.setFont(StyleUtil.BUTTON_FONT);
        editBtn.setBackground(StyleUtil.CUSTOMER_COLOR);
        editBtn.setForeground(Color.WHITE);
        editBtn.setFocusPainted(false);
        editBtn.setOpaque(true);
        editBtn.addActionListener(e -> toggleEditMode());
        headerPanel.add(editBtn, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

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

        // Butang bawah
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
    }

    private void toggleEditMode() {
        isEditMode = !isEditMode;
        loadFamilyMembers();
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

            // Jika dalam mod edit, tambah lajur "Tindakan" untuk ahli bukan utama
            if (isEditMode) {
                if (tableModel.getColumnCount() == 3) {
                    tableModel.addColumn("Tindakan");
                    // Tambah "Padam" untuk ahli bukan utama
                    for (int i = 0; i < currentMembers.size(); i++) {
                        if (!currentMembers.get(i).isMainUser()) {
                            tableModel.setValueAt("Padam", i, 3);
                        } else {
                            tableModel.setValueAt("", i, 3); // Kosong untuk user utama
                        }
                    }
                }
            } else {
                // Mod biasa: pastikan tiada lajur ke-4
                if (tableModel.getColumnCount() > 3) {
                    // Muat semula model tanpa lajur ke-4
                    String[] cols = {"Nama", "Umur", "Jantina"};
                    tableModel = new DefaultTableModel(cols, 0) {
                        @Override
                        public boolean isCellEditable(int row, int column) {
                            return false;
                        }
                    };
                    table.setModel(tableModel);
                    // Isi semula data
                    for (FamilyMember member : currentMembers) {
                        Object[] row = {
                            member.getName(),
                            member.getAge(),
                            member.getGender()
                        };
                        tableModel.addRow(row);
                    }
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memuatkan senarai ahli keluarga.", "Ralat", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void showAddDialog() {
        JTextField nameField = new JTextField();
        JComboBox<String> genderCombo = new JComboBox<>(new String[]{"Lelaki", "Perempuan"});
        JTextField birthDateField = new JTextField("YYYY-MM-DD");

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.add(new JLabel("Nama:"));
        panel.add(nameField);
        panel.add(new JLabel("Jantina:"));
        panel.add(genderCombo);
        panel.add(new JLabel("Tarikh Lahir (YYYY-MM-DD):"));
        panel.add(birthDateField);

        int result = JOptionPane.showConfirmDialog(
            this,
            panel,
            "Tambah Ahli Keluarga",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String gender = (String) genderCombo.getSelectedItem();
            String dateStr = birthDateField.getText().trim();

            try {
                LocalDate birthDate = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                FamilyMemberController controller = new FamilyMemberController();
                controller.addFamilyMember(customerId, name, gender, birthDate, false);
                loadFamilyMembers();
                JOptionPane.showMessageDialog(this, "Ahli keluarga berjaya ditambah!", "Berjaya", JOptionPane.INFORMATION_MESSAGE);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Format tarikh salah! Gunakan YYYY-MM-DD", "Ralat", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Ralat: " + ex.getMessage(), "Ralat", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void confirmDelete(FamilyMember member) {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Adakah anda pasti mahu padam ahli keluarga ini?\n\nNama: " + member.getName(),
            "Sahkan Padam",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                new FamilyMemberController().deleteFamilyMember(member.getId());
                loadFamilyMembers();
                JOptionPane.showMessageDialog(this, "Ahli keluarga berjaya dipadam!", "Berjaya", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Ralat: " + ex.getMessage(), "Ralat", JOptionPane.ERROR_MESSAGE);
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