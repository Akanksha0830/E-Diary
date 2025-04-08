/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.mycompany.ediary;

public class Main {
    public static void main(String[] args) {
        // Optionally set a look-and-feel or do other initial setups
        UtilUI.setApplicationLookAndFeel();  // a custom method in UtilUI
        
        // Show the login form
        LoginForm loginForm = new LoginForm();
        loginForm.setVisible(true);
    }
}
