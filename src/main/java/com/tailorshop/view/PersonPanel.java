package com.tailorshop.view;

import com.tailorshop.util.DatabaseConnection;
import com.tailorshop.util.StyleUtil;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PersonPanel extends JPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String currentCustomerEmail;
    private final Runnable onBack;

    public PersonPanel(String currentCustomerEmail, Runnable onBack) {
        this.currentCustomerEmail = currentCustomerEmail;
        this.onBack = onBack;
        setLayout(new BorderLayout());
        setBackground(StyleUtil.BG_LIGHT);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(StyleUtil.CUSTOMER_COLOR);
        header.setPreferredSize(new Dimension(0, 60));

        JLabel title = new JLabel("👥 DAFTAR ORANG LAIN", JLabel.LEFT);
        title.setFont(StyleUtil.TITLE_FONT);
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        header.add(title, BorderLayout.WEST);

        JButton backBtn = new JButton("❮ Kembali");
        backBtn.setFocusPainted(false);
        backBtn.addActionListener(e -> onBack.run());
        header.add(backBtn, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // Isi kandungan
        loadContent();
    }

    private void loadContent() {
        removeAll();

        // Panel utama
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(StyleUtil.BG_LIGHT);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Pemilik Akaun (Auto-muncul)
        try {
            String ownerName = getCustomerName(currentCustomerEmail);
            if (ownerName != null) {
                JPanel ownerPanel = createPersonPanel("👤 Pemilik Akaun", ownerName, "-", "-");
                ownerPanel.setBorder(BorderFactory.createTitledBorder("Pemilik Akaun"));
                mainPanel.add(ownerPanel);
                mainPanel.add(Box.createVerticalStrut(20));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // Orang Lain
        List<Person> others = loadAssociatedPersons();
        if (others.isEmpty()) {
            JLabel emptyLabel = new JLabel("Tiada orang lain didaftarkan.", JLabel.CENTER);
            emptyLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            emptyLabel.setForeground(Color.GRAY);
            mainPanel.add(emptyLabel);
            mainPanel.add(Box.createVerticalStrut(20));
        } else {
            for (Person p : others) {
                JPanel personPanel = createPersonPanel("👥", p.name, p.gender, p.phone);
                mainPanel.add(personPanel);
                mainPanel.add(Box.createVerticalStrut(15));
            }
        }

        // Butang Tambah
        JButton addButton = new JButton("➕ Tambah Orang Baru");
        addButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        addButton.setBackground(StyleUtil.CUSTOMER_COLOR);
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setOpaque(true);
        addButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        addButton.addActionListener(e -> showAddForm());

        mainPanel.add(addButton);
        mainPanel.add(Box.createVerticalGlue());

        add(new JScrollPane(mainPanel), BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private JPanel createPersonPanel(String icon, String name, String gender, String phone) {
        JPanel panel = new JPanel(new GridLayout(1, 3, 10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        JLabel nameLabel = new JLabel(icon + " " + name);
        JLabel genderLabel = new JLabel(gender);
        JLabel phoneLabel = new JLabel(phone);

        panel.add(nameLabel);
        panel.add(genderLabel);
        panel.add(phoneLabel);

        return panel;
    }

    private void showAddForm() {
        JTextField nameField = new JTextField(20);
        JComboBox<String> genderCombo = new JComboBox<>(new String[]{"Lelaki", "Perempuan"});
        JTextField phoneField = new JTextField(20);

        JPanel form = new JPanel(new GridLayout(3, 2, 5, 10));
        form.add(new JLabel("Nama:"));
        form.add(nameField);
        form.add(new JLabel("Jantina:"));
        form.add(genderCombo);
        form.add(new JLabel("No. Telefon:"));
        form.add(phoneField);

        int result = JOptionPane.showConfirmDialog(this, form, "Tambah Orang Baru", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String gender = (String) genderCombo.getSelectedItem();
            String phone = phoneField.getText().trim();

            if (name.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Sila isi semua medan!", "Ralat", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!savePerson(name, gender, phone)) {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan data.", "Ralat", JOptionPane.ERROR_MESSAGE);
                return;
            }

            loadContent(); // Refresh
        }
    }

    private boolean savePerson(String name, String gender, String phone) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO associated_persons (customer_email, name, gender, phone) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, currentCustomerEmail);
                stmt.setString(2, name);
                stmt.setString(3, gender);
                stmt.setString(4, phone);
                return stmt.executeUpdate() > 0;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private List<Person> loadAssociatedPersons() {
        List<Person> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT name, gender, phone FROM associated_persons WHERE customer_email = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, currentCustomerEmail);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    list.add(new Person(
                        rs.getString("name"),
                        rs.getString("gender"),
                        rs.getString("phone")
                    ));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    private String getCustomerName(String email) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT name FROM users WHERE email = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, email);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getString("name");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private static class Person {
        String name, gender, phone;
        Person(String name, String gender, String phone) {
            this.name = name;
            this.gender = gender;
            this.phone = phone;
        }
    }
}