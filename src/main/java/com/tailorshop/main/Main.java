package com.tailorshop.main;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.tailorshop.view.MainMenuPanel;

public class Main {

    // 🔑 Simpan rujukan global ke JFrame utama
    public static JFrame mainFrame;

    public static void main(String[] args) {
        // Jalankan dalam Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            try {
                // Gunakan rupa sistem (Windows/macOS/Linux)
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // Jika gagal, biar guna default Java Look and Feel
                System.err.println("Gagal set system look and feel: " + e.getMessage());
            }

            // Cipta tetingkap utama dan simpan rujukan
            mainFrame = new JFrame("Sistem Tempahan Jahitan - TailorShop");
            mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            mainFrame.setSize(900, 600);
            mainFrame.setLocationRelativeTo(null); // tengah skrin

            // Mulakan dari menu utama
            mainFrame.setContentPane(new MainMenuPanel());
            mainFrame.setVisible(true);
        });
    }
}