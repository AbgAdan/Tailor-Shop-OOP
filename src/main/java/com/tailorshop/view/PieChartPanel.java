// com.tailorshop.view.PieChartPanel.java
package com.tailorshop.view;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class PieChartPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private final Map<String, Integer> data;
    private final Color[] COLORS = {
        new Color(255, 99, 132),   // Merah jambu
        new Color(54, 162, 235),   // Biru
        new Color(255, 205, 86),   // Kuning
        new Color(75, 192, 192),   // Turquoise
        new Color(153, 102, 255),  // Ungu
        new Color(255, 159, 64),   // Oren
        new Color(199, 199, 199),  // Kelabu
        new Color(83, 102, 255),   // Biru gelap
        new Color(255, 127, 14),   // Oren terang
        new Color(44, 160, 44),    // Hijau
        new Color(214, 39, 40),    // Merah
        new Color(148, 103, 189)   // Ungu gelap
    };

    public PieChartPanel(Map<String, Integer> data) {
        this.data = data;
        setPreferredSize(new Dimension(500, 400));
        setOpaque(true);
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (g instanceof Graphics2D) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            
            drawPieChart(g2d);
        }
    }

    private void drawPieChart(Graphics2D g2d) {
        // Kira jumlah keseluruhan
        int total = 0;
        for (Integer value : data.values()) {
            if (value != null) {
                total += value;
            }
        }
        
        if (total == 0) {
            drawEmptyMessage(g2d, "Tiada data untuk dipaparkan");
            return;
        }

        // Tetapkan saiz dan kedudukan carta
        int chartSize = Math.min(getWidth(), getHeight() - 100);
        if (chartSize <= 0) return;
        
        int centerX = getWidth() / 2;
        int centerY = (getHeight() - 100) / 2;
        int radius = chartSize / 2 - 10;
        
        if (radius <= 0) return;

        // Lukis pie chart
        int startAngle = 0;
        int colorIndex = 0;
        
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            String label = entry.getKey();
            Integer value = entry.getValue();
            
            if (value == null || value <= 0) {
                continue;
            }
            
            int arcAngle = (int) Math.round((value * 360.0) / total);
            
            // Pastikan sudut tidak negatif
            if (arcAngle <= 0) arcAngle = 1;
            
            // Dapatkan warna
            Color sliceColor = COLORS[colorIndex % COLORS.length];
            g2d.setColor(sliceColor);
            
            // Lukis slice
            g2d.fillArc(centerX - radius, centerY - radius, 
                       radius * 2, radius * 2, 
                       startAngle, arcAngle);
            
            // Lukis border
            g2d.setColor(Color.BLACK);
            g2d.drawArc(centerX - radius, centerY - radius, 
                       radius * 2, radius * 2, 
                       startAngle, arcAngle);
            
            startAngle += arcAngle;
            colorIndex++;
        }

        // Lukis legend di bawah carta
        drawLegend(g2d, centerX, centerY + radius + 20);
    }

    private void drawLegend(Graphics2D g2d, int centerX, int startY) {
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        FontMetrics fm = g2d.getFontMetrics();
        
        int y = startY;
        int colorIndex = 0;
        int maxLabelWidth = 0;
        
        // Kira lebar maksimum label untuk alignment
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            if (entry.getValue() != null && entry.getValue() > 0) {
                String label = entry.getKey() + " (" + entry.getValue() + ")";
                int width = fm.stringWidth(label);
                if (width > maxLabelWidth) {
                    maxLabelWidth = width;
                }
            }
        }
        
        // Lukis setiap legend item
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            if (entry.getValue() != null && entry.getValue() > 0) {
                String label = entry.getKey() + " (" + entry.getValue() + ")";
                Color legendColor = COLORS[colorIndex % COLORS.length];
                
                // Kotak warna
                int boxSize = 12;
                g2d.setColor(legendColor);
                g2d.fillRect(centerX - maxLabelWidth/2 - 20, y, boxSize, boxSize);
                
                // Border kotak
                g2d.setColor(Color.BLACK);
                g2d.drawRect(centerX - maxLabelWidth/2 - 20, y, boxSize, boxSize);
                
                // Label teks
                g2d.setColor(Color.BLACK);
                g2d.drawString(label, centerX - maxLabelWidth/2 - 15 + boxSize, y + fm.getAscent());
                
                y += 20;
                colorIndex++;
            }
        }
    }

    private void drawEmptyMessage(Graphics2D g2d, String message) {
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        FontMetrics fm = g2d.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(message)) / 2;
        int y = getHeight() / 2;
        g2d.setColor(Color.GRAY);
        g2d.drawString(message, x, y);
    }
}