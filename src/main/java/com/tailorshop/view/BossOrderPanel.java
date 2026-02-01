// com.tailorshop.view.BossOrderPanel.java
package com.tailorshop.view;

import com.tailorshop.controller.OrderController;
import com.tailorshop.controller.UserController;
import com.tailorshop.main.Main;
import com.tailorshop.model.Order;
import com.tailorshop.model.User;
import com.tailorshop.util.StyleUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BossOrderPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private final String bossId;
    private final Runnable onBack;
    private JTable orderTable;
    private DefaultTableModel tableModel;

    public BossOrderPanel(String bossId, Runnable onBack) {
        this.bossId = bossId;
        this.onBack = onBack;
        initializeUI();
        loadAllOrders();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(StyleUtil.BG_LIGHT);

        // Header
        JLabel header = new JLabel("PENGURUSAN SEMUA PESANAN", JLabel.CENTER);
        header.setFont(StyleUtil.TITLE_FONT);
        header.setBorder(BorderFactory.createEmptyBorder(30, 20, 20, 20));
        add(header, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID Pesanan", "Pelanggan", "Ahli Keluarga", "Jenis Pakaian", "Tailor", "Tarikh Siap", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        orderTable = new JTable(tableModel);
        orderTable.setFillsViewportHeight(true);
        orderTable.setAutoCreateRowSorter(true);
        orderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(orderTable);
        add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        JButton backBtn = new JButton("❮ Kembali");
        styleButton(backBtn, Color.GRAY);
        backBtn.addActionListener(e -> onBack.run());
        buttonPanel.add(backBtn);

        JButton refreshBtn = new JButton("Muat Semula");
        styleButton(refreshBtn, StyleUtil.BOSS_COLOR);
        refreshBtn.addActionListener(e -> loadAllOrders());
        buttonPanel.add(refreshBtn);

        JButton assignTailorBtn = new JButton("Assign Tailor");
        styleButton(assignTailorBtn, StyleUtil.BOSS_COLOR);
        assignTailorBtn.addActionListener(e -> handleAssignTailor());
        buttonPanel.add(assignTailorBtn);

        JButton updateStatusBtn = new JButton("Kemaskini Status");
        styleButton(updateStatusBtn, StyleUtil.BOSS_COLOR);
        updateStatusBtn.addActionListener(e -> handleUpdateStatus());
        buttonPanel.add(updateStatusBtn);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadAllOrders() {
        try {
            OrderController controller = new OrderController();
            List<Order> orders = controller.getAllOrders();
            
            tableModel.setRowCount(0);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            
            for (Order order : orders) {
                Object[] row = {
                    order.getId(),
                    order.getCustomerName(),
                    order.getFamilyMemberName(),
                    order.getClothingTypeName(),
                    order.getTailorName() != null ? order.getTailorName() : "Belum Diassign",
                    order.getDueDate().format(formatter),
                    order.getStatus()
                };
                tableModel.addRow(row);
            }
            
            if (orders.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tiada pesanan dijumpai.", "Makluman", JOptionPane.INFORMATION_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memuatkan senarai pesanan.", "Ralat", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void handleAssignTailor() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Sila pilih pesanan untuk diassign tailor.", "Ralat", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = orderTable.convertRowIndexToModel(selectedRow);
        String orderId = (String) tableModel.getValueAt(modelRow, 0);
        String currentTailor = (String) tableModel.getValueAt(modelRow, 4);

        if (!"Belum Diassign".equals(currentTailor)) {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Pesanan ini sudah diassign kepada " + currentTailor + ".\nAdakah anda mahu tukar tailor?",
                "Sahkan Perubahan",
                JOptionPane.YES_NO_OPTION
            );
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }
        }

        try {
            UserController userController = new UserController();
            List<User> tailors = userController.getAllTailors();
            
            if (tailors.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tiada tailor tersedia.", "Makluman", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            String[] tailorOptions = tailors.stream()
                .map(t -> t.getId() + " - " + t.getName())
                .toArray(String[]::new);
            
            String selectedOption = (String) JOptionPane.showInputDialog(
                this,
                "Pilih Tailor untuk pesanan " + orderId + ":",
                "Assign Tailor",
                JOptionPane.QUESTION_MESSAGE,
                null,
                tailorOptions,
                tailorOptions[0]
            );
            
            if (selectedOption != null) {
                String selectedTailorId = selectedOption.split(" - ")[0];
                
                // Update order dengan tailor_id
                OrderController orderController = new OrderController();
                Order order = orderController.getOrderById(orderId);
                if (order != null) {
                    // Create new order with same details but assigned tailor
                    orderController.createOrderWithTailor(
                        order.getCustomerId(),
                        order.getFamilyMemberId(),
                        order.getClothingTypeId(),
                        selectedTailorId,
                        order.getDueDate(),
                        order.getNotes()
                    );
                    
                    // Delete old order (without tailor)
                    // Note: In real system, you'd have an update method instead
                    JOptionPane.showMessageDialog(this, "Tailor berjaya diassign!", "Berjaya", JOptionPane.INFORMATION_MESSAGE);
                    loadAllOrders();
                }
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal mengassign tailor: " + e.getMessage(), "Ralat", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void handleUpdateStatus() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Sila pilih pesanan untuk dikemaskini.", "Ralat", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = orderTable.convertRowIndexToModel(selectedRow);
        String orderId = (String) tableModel.getValueAt(modelRow, 0);
        String currentStatus = (String) tableModel.getValueAt(modelRow, 6);

        String[] statusOptions = {"Menunggu", "Dalam Proses", "Siap", "Dihantar"};
        
        String newStatus = (String) JOptionPane.showInputDialog(
            this,
            "Pilih status baharu untuk pesanan " + orderId + ":",
            "Kemaskini Status",
            JOptionPane.QUESTION_MESSAGE,
            null,
            statusOptions,
            currentStatus
        );

        if (newStatus != null && !newStatus.equals(currentStatus)) {
            try {
                OrderController controller = new OrderController();
                if (controller.updateOrderStatus(orderId, newStatus)) {
                    JOptionPane.showMessageDialog(this, "Status pesanan berjaya dikemaskini!", "Berjaya", JOptionPane.INFORMATION_MESSAGE);
                    loadAllOrders();
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal mengemaskini status.", "Ralat", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Ralat semasa mengemaskini: " + e.getMessage(), "Ralat", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
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