/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.mycompany.ediary;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/*
 * Enhanced Login Form with a professional UI.
 */
public class LoginForm extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    public LoginForm() {
        setTitle("E-Diary Login");
        setSize(400, 300);
        setLocationRelativeTo(null);  // Center
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initComponents();
    }

    private void initComponents() {
        // Panel with gradient background
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Color color1 = new Color(33, 150, 243); // Blue shade
                Color color2 = new Color(144, 202, 249); // Light blue shade
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, getHeight(), color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        
        panel.setLayout(new GridBagLayout());

        // Form components
        JLabel lblTitle = new JLabel("E-Diary Login");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblUser = new JLabel("Username:");
        lblUser.setFont(new Font("Arial", Font.PLAIN, 16));
        lblUser.setForeground(Color.WHITE);

        txtUsername = new JTextField(15);
        txtUsername.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel lblPass = new JLabel("Password:");
        lblPass.setFont(new Font("Arial", Font.PLAIN, 16));
        lblPass.setForeground(Color.WHITE);

        txtPassword = new JPasswordField(15);
        txtPassword.setFont(new Font("Arial", Font.PLAIN, 14));

        btnLogin = new JButton("Login");
        btnLogin.setFont(new Font("Arial", Font.BOLD, 16));
        btnLogin.setBackground(new Color(255, 87, 34)); // Orange button
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Hover effect for the button
        btnLogin.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnLogin.setBackground(new Color(244, 67, 54)); // Darker orange
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnLogin.setBackground(new Color(255, 87, 34)); // Original color
            }
            
        });

        btnLogin.addActionListener(e -> attemptLogin());

        // Layout adjustments
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(lblTitle, gbc);

        gbc.gridy++;
        gbc.gridwidth = 1;
        panel.add(lblUser, gbc);

        gbc.gridx = 1;
        panel.add(txtUsername, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(lblPass, gbc);

        gbc.gridx = 1;
        panel.add(txtPassword, gbc);

        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        panel.add(btnLogin, gbc);

        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(panel);
    }

    private void attemptLogin() {
        String user = txtUsername.getText();
        String pass = String.valueOf(txtPassword.getPassword());

        // Check admin_user table
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM admin_user WHERE username=? AND password=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, user);
            ps.setString(2, pass);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                // Login success
                JOptionPane.showMessageDialog(this, "Login successful!");
                // Go to Dashboard
                new Dashboard().setVisible(true);
                this.dispose(); // Close login form
            } else {
                // Login failed
                JOptionPane.showMessageDialog(this, "Invalid credentials.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginForm().setVisible(true);
        });
    }
}

