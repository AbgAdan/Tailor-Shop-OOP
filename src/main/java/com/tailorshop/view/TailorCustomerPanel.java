// com.tailorshop.view.TailorCustomerPanel.java
package com.tailorshop.view;

import com.tailorshop.controller.UserController;
import com.tailorshop.main.Main;
import com.tailorshop.model.User;
import com.tailorshop.util.StyleUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TailorCustomerPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private final String currentUserId;
    private final Runnable onBack;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private List<User> allCustomers;

    public TailorCustomerPanel(String currentUserId, Runnable onBack) {
        this.currentUserId = currentUserId;
        this.onBack = onBack;
        initializeUI();
        loadCustomers();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(StyleUtil.BG_LIGHT);

        // ✅ HEADER TAJUK
        JLabel header = new JLabel("SENARAI PELANGGAN", JLabel.CENTER);
        header.setFont(StyleUtil.TITLE_FONT);
        header.setBorder(BorderFactory.createEmptyBorder(30, 20, 20, 20));
        add(header, BorderLayout.NORTH);

        // ✅ PANEL UTAMA
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(StyleUtil.BG_LIGHT);

        // ✅ PANEL CARIAN
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setBackground(StyleUtil.BG_LIGHT);
        searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        JButton searchBtn = new JButton("Cari");
        searchBtn.setFont(StyleUtil.BUTTON_FONT);
        searchBtn.setBackground(new Color(70, 130, 180));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFocusPainted(false);

        searchBtn.addActionListener(e -> filterCustomers());
        searchField.addActionListener(e -> filterCustomers());

        searchPanel.add(new JLabel("Cari Pelanggan:"));
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);

        // ✅ SENARAI PELANGGAN
        String[] columns = {"Nama", "Emel"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(table);

        mainPanel.add(searchPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);

        // ✅ BUTTON KEMBALI
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        JButton backBtn = new JButton("❮ Kembali");
        styleButton(backBtn, Color.GRAY);
        backBtn.addActionListener(e -> onBack.run());
        buttonPanel.add(backBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        // ✅ NAVIGASI KE FAMILYMEMBERPANEL DENGAN ROLE = "TAILOR"
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 1) {
                    int row = table.rowAtPoint(evt.getPoint());
                    if (row >= 0 && row < tableModel.getRowCount()) {
                        String selectedName = (String) tableModel.getValueAt(row, 0);
                        User selectedCustomer = allCustomers.stream()
                            .filter(c -> c.getName().equals(selectedName))
                            .findFirst()
                            .orElse(null);
                        
                        if (selectedCustomer != null) {
                            // ✅ GUNA FAMILYMEMBERPANEL SEDIA ADA DENGAN ROLE = "TAILOR"
                            navigateTo(new FamilyMemberPanel(
                                selectedCustomer.getId(),
                                selectedCustomer.getName(),
                                "TAILOR",      // 👈 INI YANG PENTING
                                currentUserId, // 👈 CURRENT USER ID UNTUK SEMAK KEWENANGAN
                                () -> navigateTo(new TailorCustomerPanel(currentUserId, onBack))
                            ));
                        }
                    }
                }
            }
        });
    }

    private void loadCustomers() {
        try {
            UserController controller = new UserController();
            allCustomers = controller.getAllCustomers();
            
            tableModel.setRowCount(0);
            for (User customer : allCustomers) {
                Object[] row = {customer.getName(), customer.getEmail()};
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memuatkan senarai pelanggan.", "Ralat", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void filterCustomers() {
        String searchTerm = searchField.getText().trim().toLowerCase();
        
        tableModel.setRowCount(0);
        
        for (User customer : allCustomers) {
            boolean matches = customer.getName().toLowerCase().contains(searchTerm) ||
                            customer.getEmail().toLowerCase().contains(searchTerm);
            
            if (searchTerm.isEmpty() || matches) {
                Object[] row = {customer.getName(), customer.getEmail()};
                tableModel.addRow(row);
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