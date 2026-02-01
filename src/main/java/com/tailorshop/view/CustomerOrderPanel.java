// com.tailorshop.view.CustomerOrderPanel.java
package com.tailorshop.view;

import com.tailorshop.controller.OrderController;
import com.tailorshop.main.Main;
import com.tailorshop.model.Order;
import com.tailorshop.util.StyleUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;

public class CustomerOrderPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private final String customerId;
    private final Runnable onBack;
    private JTable orderTable;
    private DefaultTableModel tableModel;

    public CustomerOrderPanel(String customerId, Runnable onBack) {
        this.customerId = customerId;
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
        String[] columns = {"ID Pesanan", "Ahli Keluarga", "Jenis Pakaian", "Tarikh Siap", "Status", "Tailor"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        orderTable = new JTable(tableModel);
        orderTable.setFillsViewportHeight(true);
        orderTable.setAutoCreateRowSorter(true);
        JScrollPane scrollPane = new JScrollPane(orderTable);
        add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        JButton backBtn = new JButton("❮ Kembali");
        styleButton(backBtn, Color.GRAY);
        backBtn.addActionListener(e -> onBack.run());
        buttonPanel.add(backBtn);

        JButton refreshBtn = new JButton("Muat Semula");
        styleButton(refreshBtn, StyleUtil.CUSTOMER_COLOR);
        refreshBtn.addActionListener(e -> loadOrders());
        buttonPanel.add(refreshBtn);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadOrders() {
        try {
            OrderController controller = new OrderController();
            java.util.List<Order> orders = controller.getCustomerOrders(customerId);
            
            tableModel.setRowCount(0);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            
            for (Order order : orders) {
                Object[] row = {
                    order.getId(),
                    order.getFamilyMemberName(),
                    order.getClothingTypeName(),
                    order.getDueDate().format(formatter),
                    order.getStatus(),
                    order.getTailorName() != null ? order.getTailorName() : "Belum Diassign"
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