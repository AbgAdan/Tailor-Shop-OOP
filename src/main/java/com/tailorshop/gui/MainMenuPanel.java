package com.tailorshop.gui;

import com.tailorshop.util.StyleUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainMenuPanel extends JPanel {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MainMenuPanel() {
        setLayout(new BorderLayout());
        setBackground(StyleUtil.BG_LIGHT);

        // Header - Title sahaja
        JLabel header = new JLabel("SISTEM TEMPANAH JAHITAN", JLabel.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 22));
        header.setForeground(StyleUtil.CUSTOMER_COLOR);
        header.setBorder(BorderFactory.createEmptyBorder(40, 20, 30, 20));
        add(header, BorderLayout.NORTH);

        // Main Content: Kiri (Logo) | Tengah (Garisan) | Kanan (Butang)
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerSize(2);
        splitPane.setResizeWeight(0.5);
        splitPane.setEnabled(false); // Kunci divider

        // Panel Kiri - Logo
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        leftPanel.add(loadLogo(), BorderLayout.CENTER);

        // Panel Kanan - Butang & Teks
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(StyleUtil.BG_LIGHT);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 50, 30));

        // Teks arahan
        JLabel subtitle = new JLabel("Sila pilih tindakan anda", JLabel.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(Color.GRAY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Butang Daftar
        JButton registerBtn = createButton("Daftar (Pelanggan)");
        addHoverEffect(registerBtn, StyleUtil.CUSTOMER_COLOR);
        registerBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerBtn.addActionListener(e -> navigateTo(new RegisterPanel()));

        // Butang Log Masuk
        JButton loginBtn = createButton("Log Masuk");
        addHoverEffect(loginBtn, StyleUtil.CUSTOMER_COLOR);
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.addActionListener(e -> navigateTo(new LoginPanel()));

        // Lupa kata laluan
        JLabel forgotPassLabel = new JLabel("<html><u>Lupa Kata Laluan?</u></html>", JLabel.CENTER);
        forgotPassLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        forgotPassLabel.setForeground(Color.BLUE);
        forgotPassLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        forgotPassLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        forgotPassLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                navigateTo(new ForgotPasswordPanel());
            }
        });

        // Susun dalam panel kanan
        rightPanel.add(Box.createVerticalGlue());
        rightPanel.add(subtitle);
        rightPanel.add(registerBtn);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        rightPanel.add(loginBtn);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        rightPanel.add(forgotPassLabel);
        rightPanel.add(Box.createVerticalGlue());

        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);
        add(splitPane, BorderLayout.CENTER);
    }

    private JLabel loadLogo() {
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/logo tailor shop.png"));
            if (icon.getIconWidth() == -1) {
                throw new Exception("Logo tidak ditemui");
            }

            // Skalakan ke 250x250 dengan kualiti tinggi
            Image scaledImage = icon.getImage().getScaledInstance(250, 250, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(scaledImage);
            return new JLabel(scaledIcon, JLabel.CENTER);

        } catch (Exception e) {
            JLabel placeholder = new JLabel("🖼️ LOGO TIDAK DIJUMPAI", JLabel.CENTER);
            placeholder.setFont(new Font("Segoe UI", Font.BOLD, 14));
            placeholder.setForeground(Color.RED);
            return placeholder;
        }
    }

    private JButton createButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(StyleUtil.CUSTOMER_COLOR);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true); // Penting untuk warna kelihatan
        btn.setMaximumSize(new Dimension(220, 45));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        return btn;
    }

    private void addHoverEffect(JButton button, Color normalColor) {
        Color hoverColor = new Color(
            Math.max(0, normalColor.getRed() - 30),
            Math.max(0, normalColor.getGreen() - 30),
            Math.max(0, normalColor.getBlue() - 30)
        );

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(normalColor);
            }
        });
    }

    private void navigateTo(JPanel panel) {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (frame != null) {
            frame.setContentPane(panel);
            frame.revalidate();
            frame.repaint();
        }
    }
}