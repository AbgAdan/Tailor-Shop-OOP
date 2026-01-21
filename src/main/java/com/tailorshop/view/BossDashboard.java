package com.tailorshop.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.tailorshop.util.StyleUtil;

public class BossDashboard extends JPanel {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BossDashboard(Runnable onLogout) {
        setLayout(new BorderLayout());
        setBackground(new Color(250, 240, 250)); // Light purple background

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
        logoutBtn.addActionListener(e -> onLogout.run());
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
            // 🔜 Tambah fungsi nanti
            menuPanel.add(btn);
        }

        add(menuPanel, BorderLayout.CENTER);
    }
}