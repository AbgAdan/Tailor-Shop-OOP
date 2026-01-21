// com.tailorshop.view.BossDashboard.java
package com.tailorshop.view;

import com.tailorshop.main.Main;
import com.tailorshop.util.StyleUtil;

import javax.swing.*;
import java.awt.*;

public class BossDashboard extends JPanel {

    private static final long serialVersionUID = 1L;
    private final Runnable onLogout;
    private final String currentBossId; // ID Boss yang sedang log masuk

    /**
     * Constructor
     * @param onLogout Callback untuk logout
     * @param bossId ID Boss semasa (contoh: "B0012026")
     */
    public BossDashboard(Runnable onLogout, String bossId) {
        this.onLogout = onLogout;
        this.currentBossId = bossId;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(StyleUtil.BG_LIGHT);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(StyleUtil.BOSS_COLOR);
        header.setPreferredSize(new Dimension(0, 70));

        JLabel title = new JLabel("👑 MENU PENGURUS", JLabel.LEFT);
        title.setFont(StyleUtil.TITLE_FONT);
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        header.add(title, BorderLayout.WEST);

        JButton logoutBtn = new JButton("Log Keluar");
        logoutBtn.setFocusPainted(false);
        logoutBtn.setFont(StyleUtil.BUTTON_FONT);
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setBackground(StyleUtil.BOSS_COLOR.darker());
        logoutBtn.addActionListener(e -> handleLogout());
        header.add(logoutBtn, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // Menu items
        String[] menuItems = {
            "Pengurusan Staff",
            "Pantau Semua Pesanan",
            "Laporan Bulanan",
            "Analisis Pekerja"
        };

        JPanel menuPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));
        menuPanel.setBackground(getBackground());

        for (String item : menuItems) {
            JButton btn = new JButton(item);
            btn.setFont(StyleUtil.BUTTON_FONT);
            btn.setBackground(StyleUtil.BOSS_COLOR);
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
            ));

            if ("Pengurusan Staff".equals(item)) {
                btn.addActionListener(e -> navigateTo(
                    new BossRegisterPanel(
                        () -> navigateTo(new BossDashboard(onLogout, currentBossId)),
                        currentBossId
                    )
                ));
            } else {
                btn.addActionListener(e -> showFeatureNotReady(item));
            }

            menuPanel.add(btn);
        }

        add(menuPanel, BorderLayout.CENTER);
    }

    private void handleLogout() {
        try {
            if (onLogout != null) {
                onLogout.run();
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "Ralat: Callback logout tidak disediakan.",
                    "Ralat",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this,
                "Ralat semasa logout: " + ex.getMessage(),
                "Ralat",
                JOptionPane.ERROR_MESSAGE
            );
            ex.printStackTrace();
        }
    }

    private void showFeatureNotReady(String featureName) {
        JOptionPane.showMessageDialog(
            this,
            "Fungsi \"" + featureName + "\" sedang dalam pembangunan.\n\n" +
            "Akan tersedia dalam versi akan datang.",
            "Makluman",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    protected void navigateTo(JPanel panel) {
        if (Main.mainFrame != null) {
            Main.mainFrame.setContentPane(panel);
            Main.mainFrame.revalidate();
            Main.mainFrame.repaint();
        } else {
            JOptionPane.showMessageDialog(
                this,
                "Tetingkap utama tidak dijumpai.",
                "Ralat Navigasi",
                JOptionPane.WARNING_MESSAGE
            );
        }
    }
}