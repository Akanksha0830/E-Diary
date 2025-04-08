/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ediary;

/**
 *
 * @author ASUS
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Dashboard extends JFrame {

    private JPanel navBar;
    private JPanel contentPanel;
    private CardLayout cardLayout;

    public Dashboard() {
        setTitle("E-Diary Dashboard");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initComponents();
    }

    private void initComponents() {
        // Navigation bar (top)
        navBar = new JPanel();
        navBar.setBackground(new Color(255, 182, 193)); 
        navBar.setLayout(new BoxLayout(navBar, BoxLayout.X_AXIS));
        navBar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding

        JButton btnNotes = createStyledButton("Notes");
        JButton btnCategories = createStyledButton("Categories");
        //JButton btnPhotos = createStyledButton("Photos");
        JButton btnLogout = createStyledButton("Logout");

        // Attach actions
        btnNotes.addActionListener(e -> switchPanel("notes"));
        btnCategories.addActionListener(e -> switchPanel("categories"));
        btnLogout.addActionListener(e -> logout());
        //btnPhotos.addActionListener(e -> switchPanel("photos"));

        // Add buttons to nav bar with spacing
        navBar.add(Box.createHorizontalStrut(10));
        navBar.add(btnNotes);
        navBar.add(Box.createHorizontalStrut(10));
        navBar.add(btnCategories);
        navBar.add(Box.createHorizontalStrut(10));
        //navBar.add(btnPhotos); // Add Photos button here
        navBar.add(Box.createHorizontalGlue()); // Push Logout to the right
        navBar.add(btnLogout);

        // Main content area
        contentPanel = new JPanel();
        cardLayout = new CardLayout();
        contentPanel.setLayout(cardLayout);

        // Add sub-panels with background colors
        JPanel notesPanel = new NotesManager();
        notesPanel.setBackground(Color.WHITE);
        
        //JPanel photosPanel = new PhotosManager();
        //photosPanel.setBackground(Color.WHITE);

        JPanel categoriesPanel = new CategoryManager();
        categoriesPanel.setBackground(Color.LIGHT_GRAY);

        contentPanel.add(notesPanel, "notes");
        contentPanel.add(categoriesPanel, "categories");
        //contentPanel.add(photosPanel, "photos"); // Add photos panel

        // Layout overall
        setLayout(new BorderLayout());
        add(navBar, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(255, 87, 34)); // Orange background
        button.setForeground(Color.WHITE); // White text
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20)); // Padding for button

        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(244, 67, 54)); // Darker shade on hover
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(255, 87, 34)); // Original color
            }
        });

        return button;
    }

    private void switchPanel(String name) {
        cardLayout.show(contentPanel, name);
    }

    private void logout() {
        // Confirm logout
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure?");
        if (confirm == JOptionPane.YES_OPTION) {
            // Return to login
            new LoginForm().setVisible(true);
            dispose();
        }
    }
}
