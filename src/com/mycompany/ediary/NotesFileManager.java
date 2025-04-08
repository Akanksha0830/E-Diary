/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ediary;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class NotesFileManager {
    private static final String FILE_PATH = "offline_notes.txt";

    // Save a single note to a text file
    public static void saveNoteToFile(String category, String noteText, String date, String time) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            // Write note in a structured format (e.g., CSV-like structure)
            writer.write(category + "|" + noteText + "|" + date + "|" + time);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load all notes from the text file
    public static List<String[]> loadNotesFromFile() {
        List<String[]> notes = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Parse the line (split by "|")
                String[] noteData = line.split("\\|");
                notes.add(noteData);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return notes;
    }

    // Delete all notes from the file (used after syncing with the database)
    public static void clearOfflineNotes() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            // Overwrite the file with nothing to clear it
            writer.write("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}