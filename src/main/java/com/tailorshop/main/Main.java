package com.tailorshop.main;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.tailorshop.gui.MainMenuPanel;

public class Main {
    public static void main(String[] args) {
        // Jalankan dalam Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            try {
                // Gunakan rupa sistem (Windows/macOS/Linux)
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // Jika gagal, biar guna default Java Look and Feel
                System.err.println("⚠️ Gagal set system look and feel: " + e.getMessage());
            }

            // Cipta tetingkap utama
            JFrame frame = new JFrame("Sistem Tempahan Jahitan - TailorShop");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(900, 600);
            frame.setLocationRelativeTo(null); // tengah skrin
            frame.add(new MainMenuPanel());      // mulakan dari login
            frame.setVisible(true);
        });
    }
}