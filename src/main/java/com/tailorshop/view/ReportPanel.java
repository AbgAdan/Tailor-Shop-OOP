// com.tailorshop.view.ReportPanel.java
package com.tailorshop.view;

import com.tailorshop.controller.ReportController;
import com.tailorshop.main.Main;
import com.tailorshop.util.StyleUtil;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class ReportPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private final Runnable onBack;

    public ReportPanel(Runnable onBack) {
        this.onBack = onBack;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(StyleUtil.BG_LIGHT);

        JLabel header = new JLabel("📊 LAPORAN PRESTASI", JLabel.CENTER);
        header.setFont(StyleUtil.TITLE_FONT);
        header.setBorder(BorderFactory.createEmptyBorder(30, 20, 20, 20));
        add(header, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(StyleUtil.BUTTON_FONT);

        // Tab 1: Mengikut Tailor
        tabbedPane.addTab("Mengikut Tailor", createChartPanel("Tailor", "getOrdersByTailor"));
        
        // Tab 2: Mengikut Jenis Pakaian  
        tabbedPane.addTab("Jenis Pakaian", createChartPanel("Jenis Pakaian", "getOrdersByClothingType"));
        
        // Tab 3: Mengikut Status
        tabbedPane.addTab("Status Pesanan", createChartPanel("Status", "getOrdersByStatus"));

        add(tabbedPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton backBtn = new JButton("❮ Kembali");
        styleButton(backBtn, Color.GRAY);
        backBtn.addActionListener(e -> onBack.run());
        buttonPanel.add(backBtn);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createChartPanel(String title, String reportMethod) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(title));

        try {
            ReportController controller = new ReportController();
            Map<String, Integer> data;
            
            // ✅ KOMPATIBEL DENGAN SEMUA VERSI JAVA
            if ("getOrdersByTailor".equals(reportMethod)) {
                data = controller.getOrdersByTailor();
            } else if ("getOrdersByClothingType".equals(reportMethod)) {
                data = controller.getOrdersByClothingType();
            } else if ("getOrdersByStatus".equals(reportMethod)) {
                data = controller.getOrdersByStatus();
            } else {
                data = new java.util.HashMap<>();
            }

            if (data.isEmpty()) {
                JLabel emptyLabel = new JLabel("Tiada data untuk paparan", JLabel.CENTER);
                emptyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                panel.add(emptyLabel, BorderLayout.CENTER);
            } else {
                PieChartPanel chartPanel = new PieChartPanel(data);
                panel.add(chartPanel, BorderLayout.CENTER);
            }
        } catch (Exception e) {
            JLabel errorLabel = new JLabel("Ralat memuatkan data: " + e.getMessage(), JLabel.CENTER);
            errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            errorLabel.setForeground(Color.RED);
            panel.add(errorLabel, BorderLayout.CENTER);
        }

        return panel;
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