package com.lakelogger;

import com.lakelogger.dao.DatabaseManager;
import com.lakelogger.ui.MainFrame;
import com.lakelogger.ui.theme.DarkTheme;

import javax.swing.*;

/**
 * Main entry point for Lake Logger application.
 */
public class LakeLoggerApp {

    public static void main(String[] args) {
        // Set system properties for better rendering
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        // Initialize database
        DatabaseManager.getInstance();

        // Launch GUI on EDT
        SwingUtilities.invokeLater(() -> {
            try {
                // Apply dark theme
                DarkTheme.apply();

                // Create and show main frame
                MainFrame frame = new MainFrame();
                frame.setVisible(true);

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "Error starting application: " + e.getMessage(),
                        "Startup Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
