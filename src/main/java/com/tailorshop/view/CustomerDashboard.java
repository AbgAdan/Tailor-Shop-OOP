package com.tailorshop.view;

import com.tailorshop.main.Main; // ← penting untuk akses mainFrame
import com.tailorshop.util.StyleUtil;

import javax.swing.*;
import java.awt.*;

public class CustomerDashboard extends JPanel {

    private static final long serialVersionUID = 1L;
    private final Runnable onLogout;

    public CustomerDashboard(Runnable onLogout) {
        this.onLogout = onLogout;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(StyleUtil.BG_LIGHT);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(StyleUtil.CUSTOMER_COLOR);
        header.setPreferredSize(new Dimension(0, 70));

        JLabel title = new JLabel("MENU PELANGGAN", JLabel.LEFT);
        title.setFont(StyleUtil.TITLE_FONT);
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        header.add(title, BorderLayout.WEST);

        JButton logoutBtn = new JButton("Log Keluar");
        logoutBtn.setFocusPainted(false);
        logoutBtn.setFont(StyleUtil.BUTTON_FONT);
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setBackground(StyleUtil.CUSTOMER_COLOR.darker());
        logoutBtn.addActionListener(e -> handleLogout());
        header.add(logoutBtn, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // Menu Utama
        String[] menuItems = {
            "Urus Ahli Keluarga",
            "Urus Profil Ukuran",
            "Buat Pesanan",
            "Lihat Pesanan Saya"
        };

        JPanel menuPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));
        menuPanel.setBackground(getBackground());

        for (String item : menuItems) {
            JButton btn = new JButton(item);
            btn.setFont(StyleUtil.BUTTON_FONT);
            btn.setBackground(StyleUtil.CUSTOMER_COLOR);
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
            ));
            btn.addActionListener(e -> showFeatureNotReady(item));
            menuPanel.add(btn);
        }

        add(menuPanel, BorderLayout.CENTER);
    }

    private void handleLogout() {
        try {
            if (onLogout != null) {
                onLogout.run(); // ← ini akan kembali ke MainMenuPanel (ditentukan oleh pemanggil)
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

    // Method navigasi selamat — guna Main.mainFrame
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