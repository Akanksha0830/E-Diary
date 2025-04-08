/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
// CategoryManager.java
package com.mycompany.ediary;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class CategoryManager extends JPanel {

    private JTextField txtCategorySearch;
    private JTable table;
    private DefaultTableModel model;
    private JButton btnSearch, btnDeleteAll;

    public CategoryManager() {
        setLayout(new BorderLayout());

        // Top panel for category search
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBorder(BorderFactory.createTitledBorder("Search Notes by Category"));

        txtCategorySearch = new JTextField(20);
        btnSearch = new JButton("Search");
        btnDeleteAll = new JButton("Delete All Notes in Category");

        topPanel.add(new JLabel("Category:"));
        topPanel.add(txtCategorySearch);
        topPanel.add(btnSearch);
        topPanel.add(btnDeleteAll);

        // Table for displaying notes
        model = new DefaultTableModel(new String[]{"ID", "Category", "Note", "Date", "Time"}, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Event listeners
        btnSearch.addActionListener(e -> searchNotesByCategory());
        btnDeleteAll.addActionListener(e -> deleteNotesByCategory());
    }

    private void searchNotesByCategory() {
        model.setRowCount(0); // Clear the table

        String category = txtCategorySearch.getText().trim();
        if (category.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a category to search.");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM notes WHERE category = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, category);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("category"),
                    rs.getString("note_text"),
                    rs.getDate("note_date"),
                    rs.getTime("note_time")
                });
            }

            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No notes found for the category: " + category);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error searching for notes. Please try again.");
            e.printStackTrace();
        }
    }

    private void deleteNotesByCategory() {
        String category = txtCategorySearch.getText().trim();
        if (category.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a category to delete notes.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete all notes in the category: " + category + "?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DBConnection.getConnection()) {
                String sql = "DELETE FROM notes WHERE category = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, category);
                int rowsDeleted = ps.executeUpdate();

                JOptionPane.showMessageDialog(this,
                        rowsDeleted + " notes deleted in category: " + category);
                searchNotesByCategory(); // Refresh table to show no results
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting notes. Please try again.");
                e.printStackTrace();
            }
        }
    }
}