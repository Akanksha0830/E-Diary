/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ediary;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.*;
import java.util.List;

public class NotesManager extends JPanel {

    private JTable table;
    private JTextField txtCategory, txtNote, txtSearch;
    private JComboBox<String> searchFieldDropdown;
    private JButton btnAdd, btnUpdate, btnDelete, btnSearch, btnSync, btnRefresh, btnDownload;
    private DefaultTableModel model;

    public NotesManager() {
        setLayout(new BorderLayout());

        // Top panel for inputs
        JPanel inputPanel = new JPanel(new GridLayout(2, 4, 5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Manage Notes"));

        txtCategory = new JTextField();
        txtNote = new JTextField();

        btnAdd = new JButton("Add");
        btnUpdate = new JButton("Update");
        btnDelete = new JButton("Delete");

        inputPanel.add(new JLabel("Category:"));
        inputPanel.add(txtCategory);
        inputPanel.add(new JLabel("Note Text:"));
        inputPanel.add(txtNote);
        inputPanel.add(btnAdd);
        inputPanel.add(btnUpdate);
        inputPanel.add(btnDelete);

        // Table
        model = new DefaultTableModel(new String[]{"ID", "Category", "Note", "Date", "Time"}, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        // Bottom panel for search and extra buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchFieldDropdown = new JComboBox<>(new String[]{"ID", "Category", "Note Text", "Date", "Time"});
        txtSearch = new JTextField(15);
        btnSearch = new JButton("Search");
        btnSync = new JButton("Sync Notes");
        btnRefresh = new JButton("Refresh");
        btnDownload = new JButton("Download Notes");

        bottomPanel.add(new JLabel("Search Field:"));
        bottomPanel.add(searchFieldDropdown);
        bottomPanel.add(new JLabel("Keyword:"));
        bottomPanel.add(txtSearch);
        bottomPanel.add(btnSearch);
        bottomPanel.add(btnSync);
        bottomPanel.add(btnRefresh);
        bottomPanel.add(btnDownload);

        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Load data initially
        loadNotes();

        // Event listeners
        btnAdd.addActionListener(e -> addNote());
        btnUpdate.addActionListener(e -> updateNote());
        btnDelete.addActionListener(e -> deleteNote());
        btnSearch.addActionListener(e -> searchNotes());
        btnSync.addActionListener(e -> syncOfflineNotes());
        btnRefresh.addActionListener(e -> loadNotes());
        btnDownload.addActionListener(e -> downloadNotesToFile());

        // Fill fields when row clicked
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent me) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    txtCategory.setText(model.getValueAt(row, 1).toString());
                    txtNote.setText(model.getValueAt(row, 2).toString());
                }
            }
        });
    }

    private void loadNotes() {
        model.setRowCount(0); // Clear the table
        try (Connection conn = DBConnection.getConnection()) {
            // Load notes from the database
            String sql = "SELECT * FROM notes";
            PreparedStatement ps = conn.prepareStatement(sql);
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
        } catch (SQLException e) {
            // Load offline notes if the database is unavailable
            JOptionPane.showMessageDialog(this, "Database unavailable. Loading offline notes.");
            List<String[]> offlineNotes = NotesFileManager.loadNotesFromFile();
            for (String[] note : offlineNotes) {
                model.addRow(new Object[]{null, note[0], note[1], note[2], note[3]});
            }
        }
    }

    private void addNote() {
        String category = txtCategory.getText();
        String noteText = txtNote.getText();
        String date = java.time.LocalDate.now().toString();
        String time = java.time.LocalTime.now().toString();

        try (Connection conn = DBConnection.getConnection()) {
            // Save note to the database
            String sql = "INSERT INTO notes (category, note_text, note_date, note_time) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, category);
            ps.setString(2, noteText);
            ps.setString(3, date);
            ps.setString(4, time);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Note added to database.");
            loadNotes();
        } catch (SQLException e) {
            // Save note offline if the database is unavailable
            JOptionPane.showMessageDialog(this, "Database unavailable. Saving note offline.");
            NotesFileManager.saveNoteToFile(category, noteText, date, time);
        }
    }

    private void updateNote() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a note to update.");
            return;
        }

        int id = (int) table.getValueAt(row, 0);
        String category = txtCategory.getText();
        String noteText = txtNote.getText();

        try (Connection conn = DBConnection.getConnection()) {
            // Update note in the database
            String sql = "UPDATE notes SET category=?, note_text=? WHERE id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, category);
            ps.setString(2, noteText);
            ps.setInt(3, id);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Note updated in database.");
            loadNotes();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database unavailable. Update failed.");
        }
    }

    private void deleteNote() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a note to delete.");
            return;
        }

        int id = (int) table.getValueAt(row, 0);
        try (Connection conn = DBConnection.getConnection()) {
            // Delete note from the database
            String sql = "DELETE FROM notes WHERE id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Note deleted from database.");
            loadNotes();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database unavailable. Delete failed.");
        }
    }

    private void searchNotes() {
        model.setRowCount(0); // Clear the table

        String keyword = txtSearch.getText().trim();
        String selectedField = searchFieldDropdown.getSelectedItem().toString();

        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a keyword to search.");
            return;
        }

        // Map the selected field to the actual database column
        String fieldColumn;
        switch (selectedField) {
            case "ID":
                fieldColumn = "id";
                break;
            case "Category":
                fieldColumn = "category";
                break;
            case "Note Text":
                fieldColumn = "note_text";
                break;
            case "Date":
                fieldColumn = "note_date";
                break;
            case "Time":
                fieldColumn = "note_time";
                break;
            default:
                JOptionPane.showMessageDialog(this, "Invalid search field selected.");
                return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            // Dynamic query to search by the selected field
            String sql = "SELECT * FROM notes WHERE " + fieldColumn + " LIKE ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();

            // Populate the table with search results
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
                JOptionPane.showMessageDialog(this, "No results found for your search.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error searching for notes. Please try again.");
            e.printStackTrace();
        }
    }

    private void syncOfflineNotes() {
        List<String[]> offlineNotes = NotesFileManager.loadNotesFromFile();
        try (Connection conn = DBConnection.getConnection()) {
            for (String[] note : offlineNotes) {
                String sql = "INSERT INTO notes (category, note_text, note_date, note_time) VALUES (?, ?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, note[0]);
                ps.setString(2, note[1]);
                ps.setString(3, note[2]);
                ps.setString(4, note[3]);
                ps.executeUpdate();
            }
            JOptionPane.showMessageDialog(this, "Offline notes synchronized"
                    + ".");
            NotesFileManager.clearOfflineNotes(); // Clear offline notes after sync
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to sync offline notes.");
        }
    }

    private void downloadNotesToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("download_notes.txt"))) {
            // Write a header
            writer.write("Category|Note Text|Date|Time");
            writer.newLine();

            // Fetch all notes from the database
            try (Connection conn = DBConnection.getConnection()) {
                String sql = "SELECT category, note_text, note_date, note_time FROM notes";
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    // Write each note in structured format
                    writer.write(rs.getString("category") + " | " +
                            rs.getString("note_text") + " | " +
                            rs.getDate("note_date") + " | " +
                            rs.getTime("note_time"));
                    writer.newLine();
                }
            }

            JOptionPane.showMessageDialog(this, "Notes downloaded to download_notes.txt.");
        } catch (IOException | SQLException e) {
            JOptionPane.showMessageDialog(this, "Error downloading notes.");
            e.printStackTrace();
        }
    }
}