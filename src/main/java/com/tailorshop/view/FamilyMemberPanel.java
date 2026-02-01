// com.tailorshop.view.FamilyMemberPanel.java
package com.tailorshop.view;

import com.tailorshop.controller.FamilyMemberController;
import com.tailorshop.main.Main;
import com.tailorshop.model.FamilyMember;
import com.tailorshop.util.StyleUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class FamilyMemberPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private final String customerId;
    private final String mainUserName;
    private final String userRole;
    private final String currentUserId;
    private final Runnable onBack;
    private JTable table;
    private DefaultTableModel tableModel;
    private List<FamilyMember> currentMembers;

    public FamilyMemberPanel(String customerId, String mainUserName, String userRole, String currentUserId, Runnable onBack) {
        this.customerId = customerId;
        this.mainUserName = mainUserName;
        this.userRole = userRole;
        this.currentUserId = currentUserId;
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

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        JButton backBtn = new JButton("❮ Kembali");
        styleButton(backBtn, Color.GRAY);
        backBtn.addActionListener(e -> onBack.run());
        buttonPanel.add(backBtn);

        // ✅ HANYA CUSTOMER BOLEH TAMBAH/EDIT AHLI KELUARGA
        if ("CUSTOMER".equalsIgnoreCase(userRole)) {
            JButton addButton = new JButton("Tambah Ahli Keluarga");
            styleButton(addButton, StyleUtil.CUSTOMER_COLOR);
            addButton.addActionListener(e -> showAddDialog());
            buttonPanel.add(addButton);
            
            JButton editButton = new JButton("Edit Ahli Keluarga");
            styleButton(editButton, new Color(70, 130, 180));
            editButton.addActionListener(e -> showEditSelection());
            buttonPanel.add(editButton);
        }

        add(buttonPanel, BorderLayout.SOUTH);

        // ✅ KLIK AHLI KELUARGA → NAVIGASI KE MEASUREMENT PANEL
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 1) {
                    int row = table.rowAtPoint(evt.getPoint());
                    if (row >= 0 && row < currentMembers.size()) {
                        FamilyMember member = currentMembers.get(row);
                        
                        navigateTo(new MeasurementPanel(
                            member,
                            currentUserId,
                            userRole,
                            () -> navigateTo(new FamilyMemberPanel(customerId, mainUserName, userRole, currentUserId, onBack))
                        ));
                    }
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
                    member.getAge() > 0 ? String.valueOf(member.getAge()) : "N/A",
                    member.getGender()
                };
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memuatkan senarai ahli keluarga.", "Ralat", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // ✅ PAPAR DIALOG EDIT DENGAN JADUAL
    private void showEditSelection() {
        if (!"CUSTOMER".equalsIgnoreCase(userRole)) return;
        
        if (currentMembers.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tiada ahli keluarga untuk diedit.", "Makluman", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Filter ahli keluarga yang boleh diedit (bukan main user)
        java.util.List<FamilyMember> editableMembers = currentMembers.stream()
            .filter(m -> !m.isMainUser())
            .toList();

        if (editableMembers.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tiada ahli keluarga yang boleh diedit/padam.", "Makluman", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // ✅ PAPAR DIALOG DENGAN JADUAL EDIT
        showEditTableDialog(editableMembers);
    }

    // ✅ METHOD BARU: PAPAR JADUAL EDIT DALAM DIALOG
    private void showEditTableDialog(java.util.List<FamilyMember> editableMembers) {
        JDialog dialog = new JDialog((Frame) null, "Edit Ahli Keluarga", true);
        dialog.setLayout(new BorderLayout());
        dialog.setBackground(StyleUtil.BG_LIGHT);
        dialog.setPreferredSize(new Dimension(600, 400));

        // Header
        JLabel header = new JLabel("SENARAI AHLI KELUARGA", JLabel.CENTER);
        header.setFont(StyleUtil.TITLE_FONT);
        header.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        dialog.add(header, BorderLayout.NORTH);

        // Jadual dengan lajur Tindakan
        String[] columns = {"Nama", "Umur", "Jantina", "Tindakan"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3; // Hanya lajur tindakan boleh edit
            }
        };

        JTable table = new JTable(tableModel);
        table.setFillsViewportHeight(true);

        // Isi data
        for (FamilyMember member : editableMembers) {
            Object[] row = {
                member.getName(),
                member.getAge() > 0 ? String.valueOf(member.getAge()) : "N/A",
                member.getGender(),
                "Edit"
            };
            tableModel.addRow(row);
        }

        // Custom renderer dan editor untuk butang
        table.getColumn("Tindakan").setCellRenderer(new ButtonRenderer());
        table.getColumn("Tindakan").setCellEditor(new ButtonEditor(new JCheckBox(), editableMembers, this));

        JScrollPane scrollPane = new JScrollPane(table);
        dialog.add(scrollPane, BorderLayout.CENTER);

        // Butang tutup
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton closeBtn = new JButton("Tutup");
        closeBtn.addActionListener(e -> dialog.dispose());
        buttonPanel.add(closeBtn);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // ✅ CUSTOM CELL RENDERER
    private class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setBackground(StyleUtil.CUSTOMER_COLOR);
            setForeground(Color.WHITE);
            setFont(StyleUtil.BUTTON_FONT);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    // ✅ CUSTOM CELL EDITOR
    private class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;
        private FamilyMemberPanel panel;
        private java.util.List<FamilyMember> members;

        public ButtonEditor(JCheckBox checkBox, java.util.List<FamilyMember> members, FamilyMemberPanel panel) {
            super(checkBox);
            this.members = members;
            this.panel = panel;
            button = new JButton();
            button.setOpaque(true);
            button.setBackground(StyleUtil.CUSTOMER_COLOR);
            button.setForeground(Color.WHITE);
            button.setFont(StyleUtil.BUTTON_FONT);
            button.addActionListener(e -> {
                if (isPushed) {
                    // Dapatkan ahli keluarga untuk baris ini
                    int viewRow = table.getEditingRow();
                    int modelRow = table.convertRowIndexToModel(viewRow);
                    FamilyMember selectedMember = members.get(modelRow);
                    panel.showEditOrDeleteDialog(selectedMember);
                }
                fireEditingStopped();
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            isPushed = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }

        @Override
        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }

    private void showEditOrDeleteDialog(FamilyMember member) {
        if (!"CUSTOMER".equalsIgnoreCase(userRole)) return;
        
        String[] options = {"Edit", "Padam", "Batal"};
        int choice = JOptionPane.showOptionDialog(
            this,
            "Tindakan untuk: " + member.getName(),
            "Edit atau Padam",
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );

        if (choice == 0) {
            editFamilyMember(member);
        } else if (choice == 1) {
            confirmDelete(member);
        }
    }

    private void showAddDialog() {
        if (!"CUSTOMER".equalsIgnoreCase(userRole)) return;
        
        JTextField nameField = new JTextField(20);
        JComboBox<String> genderCombo = new JComboBox<>(new String[]{"Lelaki", "Perempuan"});
        
        JComboBox<Integer> dayCombo = new JComboBox<>();
        JComboBox<String> monthCombo = new JComboBox<>(new String[]{
            "Januari", "Februari", "Mac", "April", "Mei", "Jun",
            "Julai", "Ogos", "September", "Oktober", "November", "Disember"
        });
        JComboBox<Integer> yearCombo = new JComboBox<>();

        for (int i = 1; i <= 31; i++) dayCombo.addItem(i);
        int currentYear = LocalDate.now().getYear();
        for (int year = currentYear; year >= 1900; year--) yearCombo.addItem(year);

        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        datePanel.add(dayCombo);
        datePanel.add(monthCombo);
        datePanel.add(yearCombo);

        JPanel form = new JPanel(new GridLayout(3, 2, 5, 10));
        form.add(new JLabel("Nama:"));
        form.add(nameField);
        form.add(new JLabel("Jantina:"));
        form.add(genderCombo);
        form.add(new JLabel("Tarikh Lahir:"));
        form.add(datePanel);

        int result = JOptionPane.showConfirmDialog(
            this,
            form,
            "Tambah Ahli Keluarga",
            JOptionPane.OK_CANCEL_OPTION
        );

        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String gender = (String) genderCombo.getSelectedItem();
            int day = (Integer) dayCombo.getSelectedItem();
            int month = monthCombo.getSelectedIndex() + 1;
            int year = (Integer) yearCombo.getSelectedItem();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Sila isi nama!", "Ralat", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                LocalDate birthDate = LocalDate.of(year, month, day);
                FamilyMemberController controller = new FamilyMemberController();
                FamilyMember newMember = new FamilyMember(customerId, name, gender, birthDate.toString(), false);
                
                if (controller.addFamilyMember(newMember)) {
                    loadFamilyMembers();
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal menyimpan data.", "Ralat", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Tarikh tidak sah! (Contoh: 31 Februari)", "Ralat", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void editFamilyMember(FamilyMember member) {
        if (!"CUSTOMER".equalsIgnoreCase(userRole)) return;
        
        JTextField nameField = new JTextField(member.getName(), 20);
        JComboBox<String> genderCombo = new JComboBox<>(new String[]{"Lelaki", "Perempuan"});
        genderCombo.setSelectedItem(member.getGender());
        
        LocalDate birthDate = LocalDate.parse(member.getBirthDate());
        int currentDay = birthDate.getDayOfMonth();
        int currentMonth = birthDate.getMonthValue();
        int currentYear = birthDate.getYear();

        JComboBox<Integer> dayCombo = new JComboBox<>();
        JComboBox<String> monthCombo = new JComboBox<>(new String[]{
            "Januari", "Februari", "Mac", "April", "Mei", "Jun",
            "Julai", "Ogos", "September", "Oktober", "November", "Disember"
        });
        JComboBox<Integer> yearCombo = new JComboBox<>();

        for (int i = 1; i <= 31; i++) dayCombo.addItem(i);
        dayCombo.setSelectedItem(currentDay);
        monthCombo.setSelectedIndex(currentMonth - 1);
        
        int nowYear = LocalDate.now().getYear();
        for (int year = nowYear; year >= 1900; year--) yearCombo.addItem(year);
        yearCombo.setSelectedItem(currentYear);

        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        datePanel.add(dayCombo);
        datePanel.add(monthCombo);
        datePanel.add(yearCombo);

        JPanel form = new JPanel(new GridLayout(3, 2, 5, 10));
        form.add(new JLabel("Nama:"));
        form.add(nameField);
        form.add(new JLabel("Jantina:"));
        form.add(genderCombo);
        form.add(new JLabel("Tarikh Lahir:"));
        form.add(datePanel);

        int result = JOptionPane.showConfirmDialog(
            this,
            form,
            "Edit Ahli Keluarga",
            JOptionPane.OK_CANCEL_OPTION
        );

        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String gender = (String) genderCombo.getSelectedItem();
            int day = (Integer) dayCombo.getSelectedItem();
            int month = monthCombo.getSelectedIndex() + 1;
            int year = (Integer) yearCombo.getSelectedItem();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Sila isi nama!", "Ralat", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                LocalDate newBirthDate = LocalDate.of(year, month, day);
                member.setName(name);
                member.setGender(gender);
                member.setBirthDate(newBirthDate.toString());
                
                FamilyMemberController controller = new FamilyMemberController();
                if (controller.updateFamilyMember(member)) {
                    loadFamilyMembers();
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal mengemaskini data.", "Ralat", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Tarikh tidak sah!", "Ralat", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void confirmDelete(FamilyMember member) {
        if (!"CUSTOMER".equalsIgnoreCase(userRole)) return;
        
        int result = JOptionPane.showConfirmDialog(
            this,
            "Adakah anda pasti mahu padam ahli keluarga ini?\n" + member.getName(),
            "Sahkan Padam",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        if (result == JOptionPane.YES_OPTION) {
            FamilyMemberController controller = new FamilyMemberController();
            if (controller.removeFamilyMember(member.getId())) {
                loadFamilyMembers();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal memadam ahli keluarga.", "Ralat", JOptionPane.ERROR_MESSAGE);
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