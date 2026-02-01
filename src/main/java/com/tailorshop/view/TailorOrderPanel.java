// com.tailorshop.view.TailorOrderPanel.java
package com.tailorshop.view;

import com.tailorshop.controller.OrderController;
import com.tailorshop.main.Main;
import com.tailorshop.model.Order;
import com.tailorshop.util.StyleUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;

public class TailorOrderPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private final String tailorId;
    private final Runnable onBack;
    private JTable orderTable;
    private DefaultTableModel tableModel;

    public TailorOrderPanel(String tailorId, Runnable onBack) {
        this.tailorId = tailorId;
        this.onBack = onBack;
        initializeUI();
        loadOrders();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(StyleUtil.BG_LIGHT);

        // Header
        JLabel header = new JLabel("SENARAI PESANAN SAYA", JLabel.CENTER);
        header.setFont(StyleUtil.TITLE_FONT);
        header.setBorder(BorderFactory.createEmptyBorder(30, 20, 20, 20));
        add(header, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID Pesanan", "Pelanggan", "Ahli Keluarga", "Jenis Pakaian", "Tarikh Siap", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        orderTable = new JTable(tableModel);
        orderTable.setFillsViewportHeight(true);
        orderTable.setAutoCreateRowSorter(true);
        orderTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) { // Double click to update status
                    handleUpdateStatus();
                }
            }
        });
        JScrollPane scrollPane = new JScrollPane(orderTable);
        add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        JButton backBtn = new JButton("❮ Kembali");
        styleButton(backBtn, Color.GRAY);
        backBtn.addActionListener(e -> onBack.run());
        buttonPanel.add(backBtn);

        JButton refreshBtn = new JButton("Muat Semula");
        styleButton(refreshBtn, StyleUtil.TAILOR_COLOR);
        refreshBtn.addActionListener(e -> loadOrders());
        buttonPanel.add(refreshBtn);

        JButton updateStatusBtn = new JButton("Kemaskini Status");
        styleButton(updateStatusBtn, StyleUtil.TAILOR_COLOR);
        updateStatusBtn.addActionListener(e -> handleUpdateStatus());
        buttonPanel.add(updateStatusBtn);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadOrders() {
        try {
            OrderController controller = new OrderController();
            java.util.List<Order> orders = controller.getTailorOrders(tailorId);
            
            tableModel.setRowCount(0);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            
            for (Order order : orders) {
                Object[] row = {
                    order.getId(),
                    order.getCustomerName(),
                    order.getFamilyMemberName(),
                    order.getClothingTypeName(),
                    order.getDueDate().format(formatter),
                    order.getStatus()
                };
                tableModel.addRow(row);
            }
            
            if (orders.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tiada pesanan diassign kepada anda.", "Makluman", JOptionPane.INFORMATION_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memuatkan senarai pesanan.", "Ralat", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void handleUpdateStatus() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Sila pilih pesanan untuk dikemaskini.", "Ralat", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get the actual row from model (not view)
        int modelRow = orderTable.convertRowIndexToModel(selectedRow);
        String orderId = (String) tableModel.getValueAt(modelRow, 0);
        String currentStatus = (String) tableModel.getValueAt(modelRow, 5);

        // Determine next status
        String[] nextStatusOptions = getNextStatusOptions(currentStatus);
        if (nextStatusOptions.length == 0) {
            JOptionPane.showMessageDialog(this, "Pesanan ini sudah selesai.", "Makluman", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String newStatus = (String) JOptionPane.showInputDialog(
            this,
            "Pilih status baharu untuk pesanan " + orderId + ":",
            "Kemaskini Status",
            JOptionPane.QUESTION_MESSAGE,
            null,
            nextStatusOptions,
            nextStatusOptions[0]
        );

        if (newStatus != null && !newStatus.equals(currentStatus)) {
            try {
                OrderController controller = new OrderController();
                if (controller.updateOrderStatus(orderId, newStatus)) {
                    JOptionPane.showMessageDialog(this, "Status pesanan berjaya dikemaskini!", "Berjaya", JOptionPane.INFORMATION_MESSAGE);
                    loadOrders(); // Refresh table
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal mengemaskini status.", "Ralat", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Ralat semasa mengemaskini: " + e.getMessage(), "Ralat", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private String[] getNextStatusOptions(String currentStatus) {
        switch (currentStatus) {
            case "Menunggu":
                return new String[]{"Dalam Proses"};
            case "Dalam Proses":
                return new String[]{"Siap"};
            case "Siap":
                return new String[]{"Dihantar"};
            case "Dihantar":
                return new String[]{}; // No further status
            default:
                return new String[]{"Dalam Proses"};
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