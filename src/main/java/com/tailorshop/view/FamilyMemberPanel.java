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
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class FamilyMemberPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private final String customerId;
    private final String mainUserName;
    private final String userRole;
    private final Runnable onBack;
    private JTable table;
    private DefaultTableModel tableModel;
    private List<FamilyMember> currentMembers;

    public FamilyMemberPanel(String customerId, String mainUserName, String userRole, Runnable onBack) {
        this.customerId = customerId;
        this.mainUserName = mainUserName;
        this.userRole = userRole;
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

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 1) {
                    int row = table.rowAtPoint(evt.getPoint());
                    if (row >= 0 && row < currentMembers.size()) {
                        FamilyMember member = currentMembers.get(row);
                        
                        if ("CUSTOMER".equalsIgnoreCase(userRole)) {
                            showBodyMeasurements(member);
                        } else {
                            showEditableBodyMeasurements(member);
                        }
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

    // ✅ UTAMA: Papar dialog pilih mengikut jantina
    private void showBodyMeasurements(FamilyMember member) {
        try {
            FamilyMemberController controller = new FamilyMemberController();
            
            // ✅ DAPATKAN JENIS PAKAIAN MENGIKUT JANTINA AHLI KELUARGA
            List<String> clothingTypes = controller.getClothingTypeNamesByGender(member.getGender());
            
            String[] options;
            if (clothingTypes.isEmpty()) {
                options = new String[]{"Ukuran Asas"};
            } else {
                options = new String[clothingTypes.size() + 1];
                options[0] = "Ukuran Asas";
                for (int i = 0; i < clothingTypes.size(); i++) {
                    options[i + 1] = clothingTypes.get(i);
                }
            }
            
            String selected = (String) JOptionPane.showInputDialog(
                this,
                "Pilih jenis pakaian untuk lihat ukuran:",
                "Pilih Jenis Pakaian",
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
            );
            
            if (selected == null) return;
            
            Map<String, String> measurements;
            String title;
            
            if ("Ukuran Asas".equals(selected)) {
                measurements = controller.getBasicBodyMeasurements(member.getId());
                title = "Ukuran Asas - " + member.getName();
            } else {
                int clothingTypeId = controller.getClothingTypeIdByName(selected);
                measurements = controller.getMeasurementsByTemplate(member.getId(), clothingTypeId);
                title = "Ukuran Badan - " + member.getName() + " (" + selected + ")";
            }
            
            if (measurements.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tiada ukuran direkodkan.", "Makluman", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            // Susun mengikut urutan standard
            List<String> standardOrder = Arrays.asList(
                "Lebar Bahu", "Leher", "Dada", "Pinggang Badan", "Pinggul Badan",
                "Kepala Lengan", "Hujung Lengan", "Mercu ke Mercu", 
                "Pinggang Badan ke Pinggul Badan", "Dada ke Mercu", 
                "Labuh Lengan", "Labuh Baju", "Labuh Tengah Belakang"
            );
            
            // Buat panel 2 lajur
            JPanel panel = new JPanel(new GridLayout(0, 2, 10, 5));
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            // Papar ukuran standard dulu
            for (String key : standardOrder) {
                if (measurements.containsKey(key)) {
                    JLabel nameLabel = new JLabel(key + ":");
                    JLabel valueLabel = new JLabel(measurements.get(key));
                    nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    valueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                    panel.add(nameLabel);
                    panel.add(valueLabel);
                }
            }
            
            // Papar ukuran tambahan
            for (Map.Entry<String, String> entry : measurements.entrySet()) {
                if (!standardOrder.contains(entry.getKey())) {
                    JLabel nameLabel = new JLabel(entry.getKey() + ":");
                    JLabel valueLabel = new JLabel(entry.getValue());
                    nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    valueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                    panel.add(nameLabel);
                    panel.add(valueLabel);
                }
            }
            
            JOptionPane.showMessageDialog(
                this,
                panel,
                title,
                JOptionPane.INFORMATION_MESSAGE
            );
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memuatkan data ukuran.", "Ralat", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // ✅ UNTUK BOSS/TAILOR: Edit ukuran
    private void showEditableBodyMeasurements(FamilyMember member) {
        try {
            FamilyMemberController controller = new FamilyMemberController();
            List<String> clothingTypes = controller.getAllClothingTypeNames();
            
            if (clothingTypes.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tiada jenis pakaian tersedia.", "Makluman", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            String selected = (String) JOptionPane.showInputDialog(
                this,
                "Pilih jenis pakaian untuk edit ukuran:",
                "Edit Ukuran",
                JOptionPane.QUESTION_MESSAGE,
                null,
                clothingTypes.toArray(),
                clothingTypes.get(0)
            );
            
            if (selected == null) return;
            
            int clothingTypeId = controller.getClothingTypeIdByName(selected);
            showEditableMeasurementsByClothingType(member, clothingTypeId, selected);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memuatkan data edit.", "Ralat", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void showEditableMeasurementsByClothingType(FamilyMember member, int clothingTypeId, String clothingTypeName) {
        try {
            FamilyMemberController controller = new FamilyMemberController();
            Map<String, String> measurements = controller.getMeasurementsByTemplate(member.getId(), clothingTypeId);
            
            if (measurements.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tiada ukuran untuk " + clothingTypeName + ".", "Makluman", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            JPanel panel = new JPanel(new GridLayout(0, 2, 10, 5));
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            Map<String, JTextField> fieldMap = new java.util.HashMap<>();
            
            List<String> standardOrder = Arrays.asList(
                "Lebar Bahu", "Leher", "Dada", "Pinggang Badan", "Pinggul Badan",
                "Kepala Lengan", "Hujung Lengan", "Mercu ke Mercu", 
                "Pinggang Badan ke Pinggul Badan", "Dada ke Mercu", 
                "Labuh Lengan", "Labuh Baju", "Labuh Tengah Belakang"
            );
            
            for (String key : standardOrder) {
                if (measurements.containsKey(key)) {
                    JLabel nameLabel = new JLabel(key + ":");
                    JTextField valueField = new JTextField(measurements.get(key));
                    valueField.setColumns(10);
                    fieldMap.put(key, valueField);
                    
                    nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    panel.add(nameLabel);
                    panel.add(valueField);
                }
            }
            
            for (Map.Entry<String, String> entry : measurements.entrySet()) {
                if (!standardOrder.contains(entry.getKey())) {
                    JLabel nameLabel = new JLabel(entry.getKey() + ":");
                    JTextField valueField = new JTextField(entry.getValue());
                    valueField.setColumns(10);
                    fieldMap.put(entry.getKey(), valueField);
                    
                    nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    panel.add(nameLabel);
                    panel.add(valueField);
                }
            }
            
            JScrollPane scrollPane = new JScrollPane(panel);
            scrollPane.setPreferredSize(new Dimension(400, 500));
            
            int result = JOptionPane.showConfirmDialog(
                this,
                scrollPane,
                "Edit Ukuran - " + member.getName() + " (" + clothingTypeName + ")",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
            );
            
            if (result == JOptionPane.OK_OPTION) {
                Map<String, String> updatedMeasurements = new java.util.HashMap<>();
                for (Map.Entry<String, JTextField> entry : fieldMap.entrySet()) {
                    updatedMeasurements.put(entry.getKey(), entry.getValue().getText().trim());
                }
                
                if (controller.updateMeasurementsByTemplate(member.getId(), clothingTypeId, updatedMeasurements)) {
                    JOptionPane.showMessageDialog(this, "Ukuran berjaya dikemaskini!", "Berjaya", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal mengemaskini ukuran.", "Ralat", JOptionPane.ERROR_MESSAGE);
                }
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memuatkan ukuran edit.", "Ralat", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // ... (method showEditSelection, showEditOrDeleteDialog, showAddDialog, editFamilyMember, confirmDelete tetap sama) ...

    private void showEditSelection() {
        if (!"CUSTOMER".equalsIgnoreCase(userRole)) return;
        
        if (currentMembers.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tiada ahli keluarga untuk diedit.", "Makluman", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        java.util.List<FamilyMember> editableMembers = currentMembers.stream()
            .filter(m -> !m.isMainUser())
            .toList();

        if (editableMembers.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tiada ahli keluarga yang boleh diedit/padam.", "Makluman", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String[] options = editableMembers.stream()
            .map(FamilyMember::getName)
            .toArray(String[]::new);

        String selectedName = (String) JOptionPane.showInputDialog(
            this,
            "Pilih ahli keluarga untuk edit atau padam:",
            "Pilih Ahli Keluarga",
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );

        if (selectedName != null) {
            FamilyMember selected = editableMembers.stream()
                .filter(m -> m.getName().equals(selectedName))
                .findFirst()
                .orElse(null);
            
            if (selected != null) {
                showEditOrDeleteDialog(selected);
            }
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