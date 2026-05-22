package com.lakelogger.ui.theme;

import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.geom.Ellipse2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Dark theme configuration for Lake Logger application.
 */
public class DarkTheme {

    // Main colors - deeper, richer tones
    public static final Color BACKGROUND = new Color(0x0f, 0x12, 0x14);
    public static final Color BACKGROUND_SECONDARY = new Color(0x1a, 0x1f, 0x23);
    public static final Color BACKGROUND_TERTIARY = new Color(0x25, 0x2b, 0x30);
    public static final Color SIDEBAR = new Color(0x0a, 0x0d, 0x0f);
    public static final Color CARD_BACKGROUND = new Color(0x1e, 0x24, 0x28);

    // Text colors
    public static final Color TEXT_PRIMARY = new Color(0xf0, 0xf4, 0xf8);
    public static final Color TEXT_SECONDARY = new Color(0x9c, 0xa3, 0xaf);
    public static final Color TEXT_MUTED = new Color(0x6b, 0x72, 0x80);

    // Accent colors - nature inspired
    public static final Color PRIMARY = new Color(0x1b, 0x4d, 0x3e);       // Deep forest green
    public static final Color PRIMARY_HOVER = new Color(0x2d, 0x6a, 0x4f);
    public static final Color PRIMARY_LIGHT = new Color(0x34, 0x7d, 0x5a);
    public static final Color SUCCESS = new Color(0x10, 0xb9, 0x81);       // Emerald green
    public static final Color SUCCESS_GLOW = new Color(0x10, 0xb9, 0x81, 0x40);
    public static final Color INFO = new Color(0x06, 0xb6, 0xd4);          // Cyan/water blue
    public static final Color INFO_DARK = new Color(0x08, 0x4f, 0x5c);
    public static final Color WARNING = new Color(0xf5, 0x9e, 0x0b);       // Amber/sunset
    public static final Color DANGER = new Color(0xef, 0x44, 0x44);        // Red

    // Gradient colors for header/accents
    public static final Color GRADIENT_START = new Color(0x0f, 0x2d, 0x24);
    public static final Color GRADIENT_END = new Color(0x0a, 0x1a, 0x25);
    public static final Color WATER_BLUE = new Color(0x0e, 0x45, 0x5c);
    public static final Color FOREST_DARK = new Color(0x14, 0x3a, 0x2e);

    // Border colors
    public static final Color BORDER = new Color(0x2d, 0x36, 0x3d);
    public static final Color BORDER_LIGHT = new Color(0x3d, 0x47, 0x50);
    public static final Color BORDER_ACCENT = new Color(0x1b, 0x4d, 0x3e, 0x80);

    // Emoji font name
    public static final String EMOJI_FONT_NAME = "Arial Unicode MS";

    // Base (unscaled) font sizes — never modify these
    private static final int BS_TITLE    = 28;
    private static final int BS_SUBTITLE = 18;
    private static final int BS_HEADING  = 14;
    private static final int BS_REGULAR  = 13;
    private static final int BS_SMALL    = 11;
    private static final int BS_LOGO     = 32;
    private static final int BS_E_LARGE  = 24;
    private static final int BS_E_MEDIUM = 18;
    private static final int BS_E_SMALL  = 14;

    // UI scale factor — adjusted at runtime via Ctrl +/- (1.0 = 100%)
    private static double UI_SCALE = 1.0;
    private static final double MIN_SCALE  = 0.7;
    private static final double MAX_SCALE  = 2.0;
    private static final double SCALE_STEP = 0.1;

    // Mutable scaled font fields — recomputed by recomputeFonts() on every scale change
    public static Font FONT_TITLE        = new Font("Segoe UI", Font.BOLD,  BS_TITLE);
    public static Font FONT_SUBTITLE     = new Font("Segoe UI", Font.BOLD,  BS_SUBTITLE);
    public static Font FONT_HEADING      = new Font("Segoe UI", Font.BOLD,  BS_HEADING);
    public static Font FONT_REGULAR      = new Font("Segoe UI", Font.PLAIN, BS_REGULAR);
    public static Font FONT_SMALL        = new Font("Segoe UI", Font.PLAIN, BS_SMALL);
    public static Font FONT_LOGO         = new Font("Segoe UI", Font.BOLD,  BS_LOGO);
    public static Font FONT_EMOJI_LARGE  = new Font(EMOJI_FONT_NAME, Font.PLAIN, BS_E_LARGE);
    public static Font FONT_EMOJI_MEDIUM = new Font(EMOJI_FONT_NAME, Font.PLAIN, BS_E_MEDIUM);
    public static Font FONT_EMOJI_SMALL  = new Font(EMOJI_FONT_NAME, Font.PLAIN, BS_E_SMALL);

    private DarkTheme() {}

    // ==================== UI Scaling ====================

    /**
     * Scale a pixel dimension by the current UI scale factor.
     * All hardcoded pixel sizes in the UI should pass through this.
     */
    public static int scaled(int px) {
        return (int) Math.round(px * UI_SCALE);
    }

    /** Returns the current UI scale factor (1.0 = 100%). */
    public static double getScale() { return UI_SCALE; }

    /**
     * Increase UI scale one step (Ctrl +).
     * @return true if the scale actually changed
     */
    public static boolean scaleUp() {
        if (UI_SCALE < MAX_SCALE - 0.01) {
            UI_SCALE = Math.round((UI_SCALE + SCALE_STEP) * 10.0) / 10.0;
            recomputeFonts();
            return true;
        }
        return false;
    }

    /**
     * Decrease UI scale one step (Ctrl -).
     * @return true if the scale actually changed
     */
    public static boolean scaleDown() {
        if (UI_SCALE > MIN_SCALE + 0.01) {
            UI_SCALE = Math.round((UI_SCALE - SCALE_STEP) * 10.0) / 10.0;
            recomputeFonts();
            return true;
        }
        return false;
    }

    /** Reset UI scale to 100% (Ctrl 0). */
    public static void resetScale() {
        UI_SCALE = 1.0;
        recomputeFonts();
    }

    /** Recompute all FONT_* fields from base sizes × UI_SCALE. */
    private static void recomputeFonts() {
        FONT_TITLE        = new Font("Segoe UI", Font.BOLD,  scaled(BS_TITLE));
        FONT_SUBTITLE     = new Font("Segoe UI", Font.BOLD,  scaled(BS_SUBTITLE));
        FONT_HEADING      = new Font("Segoe UI", Font.BOLD,  scaled(BS_HEADING));
        FONT_REGULAR      = new Font("Segoe UI", Font.PLAIN, scaled(BS_REGULAR));
        FONT_SMALL        = new Font("Segoe UI", Font.PLAIN, scaled(BS_SMALL));
        FONT_LOGO         = new Font("Segoe UI", Font.BOLD,  scaled(BS_LOGO));
        FONT_EMOJI_LARGE  = new Font(EMOJI_FONT_NAME, Font.PLAIN, scaled(BS_E_LARGE));
        FONT_EMOJI_MEDIUM = new Font(EMOJI_FONT_NAME, Font.PLAIN, scaled(BS_E_MEDIUM));
        FONT_EMOJI_SMALL  = new Font(EMOJI_FONT_NAME, Font.PLAIN, scaled(BS_E_SMALL));
    }

    /**
     * Apply dark theme to the entire application.
     */
    public static void apply() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Panel backgrounds
        UIManager.put("Panel.background", new ColorUIResource(BACKGROUND));
        UIManager.put("Panel.foreground", new ColorUIResource(TEXT_PRIMARY));

        // Text fields - dark theme
        UIManager.put("TextField.background", new ColorUIResource(BACKGROUND_TERTIARY));
        UIManager.put("TextField.foreground", new ColorUIResource(TEXT_PRIMARY));
        UIManager.put("TextField.caretForeground", new ColorUIResource(SUCCESS));
        UIManager.put("TextField.selectionBackground", new ColorUIResource(PRIMARY));
        UIManager.put("TextField.selectionForeground", new ColorUIResource(TEXT_PRIMARY));
        UIManager.put("TextField.inactiveBackground", new ColorUIResource(BACKGROUND_TERTIARY));
        UIManager.put("TextField.inactiveForeground", new ColorUIResource(TEXT_SECONDARY));
        UIManager.put("TextField.border", BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));

        // Text areas - dark theme
        UIManager.put("TextArea.background", new ColorUIResource(BACKGROUND_TERTIARY));
        UIManager.put("TextArea.foreground", new ColorUIResource(TEXT_PRIMARY));
        UIManager.put("TextArea.caretForeground", new ColorUIResource(SUCCESS));
        UIManager.put("TextArea.selectionBackground", new ColorUIResource(PRIMARY));
        UIManager.put("TextArea.selectionForeground", new ColorUIResource(TEXT_PRIMARY));

        // Formatted text fields (used in spinners, date pickers)
        UIManager.put("FormattedTextField.background", new ColorUIResource(BACKGROUND_TERTIARY));
        UIManager.put("FormattedTextField.foreground", new ColorUIResource(TEXT_PRIMARY));
        UIManager.put("FormattedTextField.caretForeground", new ColorUIResource(SUCCESS));
        UIManager.put("FormattedTextField.selectionBackground", new ColorUIResource(PRIMARY));
        UIManager.put("FormattedTextField.selectionForeground", new ColorUIResource(TEXT_PRIMARY));
        UIManager.put("FormattedTextField.inactiveBackground", new ColorUIResource(BACKGROUND_TERTIARY));
        UIManager.put("FormattedTextField.border", BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));

        // Combo boxes - styled with accent colors
        UIManager.put("ComboBox.background", new ColorUIResource(BACKGROUND_TERTIARY));
        UIManager.put("ComboBox.foreground", new ColorUIResource(TEXT_PRIMARY));
        UIManager.put("ComboBox.selectionBackground", new ColorUIResource(PRIMARY_LIGHT));
        UIManager.put("ComboBox.selectionForeground", new ColorUIResource(TEXT_PRIMARY));
        UIManager.put("ComboBox.buttonBackground", new ColorUIResource(PRIMARY));
        UIManager.put("ComboBox.buttonDarkShadow", new ColorUIResource(BORDER));
        UIManager.put("ComboBox.buttonHighlight", new ColorUIResource(SUCCESS));
        UIManager.put("ComboBox.buttonShadow", new ColorUIResource(BORDER));
        UIManager.put("ComboBox.disabledBackground", new ColorUIResource(BACKGROUND_SECONDARY));
        UIManager.put("ComboBox.disabledForeground", new ColorUIResource(TEXT_MUTED));

        // Buttons
        UIManager.put("Button.background", new ColorUIResource(BACKGROUND_SECONDARY));
        UIManager.put("Button.foreground", new ColorUIResource(TEXT_PRIMARY));
        UIManager.put("Button.select", new ColorUIResource(PRIMARY));

        // Labels
        UIManager.put("Label.foreground", new ColorUIResource(TEXT_PRIMARY));

        // Tables
        UIManager.put("Table.background", new ColorUIResource(BACKGROUND_SECONDARY));
        UIManager.put("Table.foreground", new ColorUIResource(TEXT_PRIMARY));
        UIManager.put("Table.selectionBackground", new ColorUIResource(PRIMARY));
        UIManager.put("Table.selectionForeground", new ColorUIResource(TEXT_PRIMARY));
        UIManager.put("Table.gridColor", new ColorUIResource(BORDER));
        UIManager.put("TableHeader.background", new ColorUIResource(BACKGROUND_TERTIARY));
        UIManager.put("TableHeader.foreground", new ColorUIResource(TEXT_PRIMARY));

        // Scroll panes
        UIManager.put("ScrollPane.background", new ColorUIResource(BACKGROUND));
        UIManager.put("ScrollPane.border", BorderFactory.createEmptyBorder());

        // Scrollbar styling - dark theme
        UIManager.put("ScrollBar.background", new ColorUIResource(BACKGROUND_SECONDARY));
        UIManager.put("ScrollBar.thumb", new ColorUIResource(PRIMARY));
        UIManager.put("ScrollBar.thumbHighlight", new ColorUIResource(SUCCESS));
        UIManager.put("ScrollBar.thumbDarkShadow", new ColorUIResource(BORDER));
        UIManager.put("ScrollBar.thumbShadow", new ColorUIResource(BORDER));
        UIManager.put("ScrollBar.track", new ColorUIResource(BACKGROUND_TERTIARY));
        UIManager.put("ScrollBar.trackHighlight", new ColorUIResource(BACKGROUND_TERTIARY));
        UIManager.put("ScrollBar.width", 12);
        UIManager.put("ScrollBar.thumbArc", 10);
        UIManager.put("ScrollBar.trackArc", 10);

        // Scrollbar button colors (arrows)
        UIManager.put("ScrollBar.arrowButtonBackground", new ColorUIResource(BACKGROUND_TERTIARY));
        UIManager.put("ScrollBar.arrowButtonForeground", new ColorUIResource(TEXT_SECONDARY));
        UIManager.put("ScrollBar.darkShadow", new ColorUIResource(BORDER));
        UIManager.put("ScrollBar.highlight", new ColorUIResource(PRIMARY));
        UIManager.put("ScrollBar.shadow", new ColorUIResource(BORDER));

        // Spinners - fully themed
        UIManager.put("Spinner.background", new ColorUIResource(BACKGROUND_TERTIARY));
        UIManager.put("Spinner.foreground", new ColorUIResource(TEXT_PRIMARY));
        UIManager.put("Spinner.border", BorderFactory.createLineBorder(BORDER));
        UIManager.put("Spinner.arrowButtonBackground", new ColorUIResource(PRIMARY));
        UIManager.put("Spinner.arrowButtonBorder", BorderFactory.createLineBorder(BORDER));
        UIManager.put("Spinner.editorBackground", new ColorUIResource(BACKGROUND_TERTIARY));
        UIManager.put("Spinner.editorForeground", new ColorUIResource(TEXT_PRIMARY));
        UIManager.put("Spinner.editorBorderPainted", false);

        // Option panes (dialogs) - full theme support
        UIManager.put("OptionPane.background", new ColorUIResource(BACKGROUND_SECONDARY));
        UIManager.put("OptionPane.messageForeground", new ColorUIResource(TEXT_PRIMARY));
        UIManager.put("OptionPane.messageFont", FONT_REGULAR);
        UIManager.put("OptionPane.buttonFont", FONT_HEADING);
        UIManager.put("OptionPane.minimumSize", new Dimension(300, 100));

        // Dialog/OptionPane buttons
        UIManager.put("Button.background", new ColorUIResource(PRIMARY));
        UIManager.put("Button.foreground", new ColorUIResource(TEXT_PRIMARY));
        UIManager.put("Button.font", FONT_HEADING);
        UIManager.put("Button.select", new ColorUIResource(PRIMARY_HOVER));
        UIManager.put("Button.focus", new ColorUIResource(SUCCESS));
        UIManager.put("Button.border", BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)));

        // Panel backgrounds for dialogs
        UIManager.put("Panel.background", new ColorUIResource(BACKGROUND_SECONDARY));
        UIManager.put("Panel.foreground", new ColorUIResource(TEXT_PRIMARY));

        // Dialog styling
        UIManager.put("Dialog.background", new ColorUIResource(BACKGROUND_SECONDARY));
        UIManager.put("Dialog.foreground", new ColorUIResource(TEXT_PRIMARY));

        // Tooltips
        UIManager.put("ToolTip.background", new ColorUIResource(BACKGROUND_TERTIARY));
        UIManager.put("ToolTip.foreground", new ColorUIResource(TEXT_PRIMARY));

        // Lists
        UIManager.put("List.background", new ColorUIResource(BACKGROUND_SECONDARY));
        UIManager.put("List.foreground", new ColorUIResource(TEXT_PRIMARY));
        UIManager.put("List.selectionBackground", new ColorUIResource(PRIMARY));
        UIManager.put("List.selectionForeground", new ColorUIResource(TEXT_PRIMARY));

        // File chooser
        UIManager.put("FileChooser.background", new ColorUIResource(BACKGROUND_SECONDARY));
        UIManager.put("FileChooser.foreground", new ColorUIResource(TEXT_PRIMARY));
        UIManager.put("FileChooser.listViewBackground", new ColorUIResource(BACKGROUND_TERTIARY));
        UIManager.put("FileChooser.listViewForeground", new ColorUIResource(TEXT_PRIMARY));

        // Input dialogs
        UIManager.put("OptionPane.inputDialogTitle", "Lake Logger");
        UIManager.put("OptionPane.messageDialogTitle", "Lake Logger");

        // Yes/No/Cancel button text colors
        UIManager.put("OptionPane.yesButtonText", "Yes");
        UIManager.put("OptionPane.noButtonText", "No");
        UIManager.put("OptionPane.cancelButtonText", "Cancel");
        UIManager.put("OptionPane.okButtonText", "OK");

        // JCalendar / JDateChooser styling (com.toedter.calendar)
        UIManager.put("JDateChooser.background", new ColorUIResource(BACKGROUND_TERTIARY));
        UIManager.put("JDateChooser.foreground", new ColorUIResource(TEXT_PRIMARY));

        // Calendar panel colors
        UIManager.put("JCalendar.background", new ColorUIResource(BACKGROUND_SECONDARY));
        UIManager.put("JCalendar.foreground", new ColorUIResource(TEXT_PRIMARY));

        // Day chooser (the grid of days)
        UIManager.put("JDayChooser.background", new ColorUIResource(BACKGROUND_SECONDARY));
        UIManager.put("JDayChooser.foreground", new ColorUIResource(TEXT_PRIMARY));
        UIManager.put("JDayChooser.sundayForeground", new ColorUIResource(DANGER));
        UIManager.put("JDayChooser.weekdayForeground", new ColorUIResource(TEXT_PRIMARY));
        UIManager.put("JDayChooser.decorationBackgroundColor", new ColorUIResource(BACKGROUND_TERTIARY));

        // Month chooser
        UIManager.put("JMonthChooser.background", new ColorUIResource(BACKGROUND_TERTIARY));
        UIManager.put("JMonthChooser.foreground", new ColorUIResource(TEXT_PRIMARY));

        // Year chooser
        UIManager.put("JYearChooser.background", new ColorUIResource(BACKGROUND_TERTIARY));
        UIManager.put("JYearChooser.foreground", new ColorUIResource(TEXT_PRIMARY));

        // Toggle button (used in calendar)
        UIManager.put("ToggleButton.background", new ColorUIResource(BACKGROUND_TERTIARY));
        UIManager.put("ToggleButton.foreground", new ColorUIResource(TEXT_PRIMARY));
        UIManager.put("ToggleButton.select", new ColorUIResource(PRIMARY));

        // ComboBox popup list styling (critical for dark theme)
        UIManager.put("ComboBox.listBackground", new ColorUIResource(BACKGROUND_SECONDARY));
        UIManager.put("ComboBox.listForeground", new ColorUIResource(TEXT_PRIMARY));
        UIManager.put("PopupMenu.background", new ColorUIResource(BACKGROUND_SECONDARY));
        UIManager.put("PopupMenu.foreground", new ColorUIResource(TEXT_PRIMARY));
        UIManager.put("PopupMenu.border", BorderFactory.createLineBorder(BORDER));
    }

    /**
     * Show a styled message dialog (replacement for JOptionPane.showMessageDialog).
     */
    public static void showMessage(Component parent, String message, String title, int messageType) {
        JOptionPane pane = new JOptionPane(message, messageType);
        styleOptionPane(pane);
        JDialog dialog = pane.createDialog(parent, title);
        dialog.setBackground(BACKGROUND_SECONDARY);
        dialog.getContentPane().setBackground(BACKGROUND_SECONDARY);
        dialog.setVisible(true);
    }

    /**
     * Show a styled confirmation dialog (replacement for JOptionPane.showConfirmDialog).
     */
    public static int showConfirm(Component parent, String message, String title, int optionType) {
        JOptionPane pane = new JOptionPane(message, JOptionPane.QUESTION_MESSAGE, optionType);
        styleOptionPane(pane);
        JDialog dialog = pane.createDialog(parent, title);
        dialog.setBackground(BACKGROUND_SECONDARY);
        dialog.getContentPane().setBackground(BACKGROUND_SECONDARY);
        dialog.setVisible(true);
        Object value = pane.getValue();
        if (value == null) {
            return JOptionPane.CLOSED_OPTION;
        }
        if (value instanceof Integer) {
            return (Integer) value;
        }
        return JOptionPane.CLOSED_OPTION;
    }

    /**
     * Show a styled input dialog (replacement for JOptionPane.showInputDialog).
     */
    public static String showInput(Component parent, String message, String title, String initialValue) {
        JTextField inputField = createTextField();
        if (initialValue != null) {
            inputField.setText(initialValue);
        }
        inputField.setPreferredSize(new Dimension(scaled(250), scaled(35)));

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND_SECONDARY);
        JLabel label = createLabel(message);
        panel.add(label, BorderLayout.NORTH);
        panel.add(inputField, BorderLayout.CENTER);

        JOptionPane pane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
        styleOptionPane(pane);
        JDialog dialog = pane.createDialog(parent, title);
        dialog.setBackground(BACKGROUND_SECONDARY);
        dialog.getContentPane().setBackground(BACKGROUND_SECONDARY);
        dialog.setVisible(true);

        Object value = pane.getValue();
        if (value != null && value instanceof Integer && (Integer) value == JOptionPane.OK_OPTION) {
            return inputField.getText();
        }
        return null;
    }

    /**
     * Style a JOptionPane and its child components.
     */
    private static void styleOptionPane(JOptionPane pane) {
        pane.setBackground(BACKGROUND_SECONDARY);
        pane.setForeground(TEXT_PRIMARY);
        styleOptionPaneRecursive(pane);
    }

    /**
     * Recursively style all components in an OptionPane.
     */
    private static void styleOptionPaneRecursive(Component comp) {
        if (comp == null) return;

        comp.setBackground(BACKGROUND_SECONDARY);
        if (comp instanceof JComponent) {
            ((JComponent) comp).setOpaque(true);
        }

        if (comp instanceof JButton) {
            JButton btn = (JButton) comp;
            // Replace with styled button appearance
            btn.setBackground(PRIMARY);
            btn.setForeground(TEXT_PRIMARY);
            btn.setFont(FONT_HEADING);
            btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(scaled(6), scaled(14), scaled(6), scaled(14))));
            btn.setFocusPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            // Add hover effect
            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    btn.setBackground(PRIMARY_HOVER);
                }
                public void mouseExited(java.awt.event.MouseEvent e) {
                    btn.setBackground(PRIMARY);
                }
            });
        } else if (comp instanceof JLabel) {
            ((JLabel) comp).setForeground(TEXT_PRIMARY);
        } else if (comp instanceof JPanel) {
            ((JPanel) comp).setBackground(BACKGROUND_SECONDARY);
        }

        if (comp instanceof Container) {
            for (Component child : ((Container) comp).getComponents()) {
                styleOptionPaneRecursive(child);
            }
        }
    }

    /**
     * Create a styled button with the primary color.
     */
    public static JButton createPrimaryButton(String text) {
        return createStyledButton(text, PRIMARY, PRIMARY_HOVER);
    }

    /**
     * Create a styled button with success color.
     */
    public static JButton createSuccessButton(String text) {
        return createStyledButton(text, SUCCESS, SUCCESS.brighter());
    }

    /**
     * Create a styled button with danger color.
     */
    public static JButton createDangerButton(String text) {
        return createStyledButton(text, DANGER, DANGER.brighter());
    }

    /**
     * Create a secondary styled button.
     */
    public static JButton createSecondaryButton(String text) {
        return createStyledButton(text, BACKGROUND_TERTIARY, BORDER_LIGHT);
    }

    /**
     * Create an info/blue styled button.
     */
    public static JButton createInfoButton(String text) {
        return createStyledButton(text, INFO, INFO.brighter());
    }

    /**
     * Create a warning/amber styled button.
     */
    public static JButton createWarningButton(String text) {
        return createStyledButton(text, WARNING, WARNING.brighter());
    }

    /**
     * Create a styled text field.
     */
    public static JTextField createTextField() {
        JTextField field = new JTextField();
        styleTextField(field);
        return field;
    }

    /**
     * Apply theme to any text field.
     */
    public static void styleTextField(JTextField field) {
        field.setBackground(BACKGROUND_TERTIARY);
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(SUCCESS);
        field.setSelectionColor(PRIMARY);
        field.setSelectedTextColor(TEXT_PRIMARY);
        field.setFont(FONT_REGULAR);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
    }

    /**
     * Apply theme to any spinner.
     */
    public static void styleSpinner(JSpinner spinner) {
        spinner.setBackground(BACKGROUND_TERTIARY);
        spinner.setForeground(TEXT_PRIMARY);
        spinner.setBorder(BorderFactory.createLineBorder(BORDER));

        // Style the editor (text field part)
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JTextField tf = ((JSpinner.DefaultEditor) editor).getTextField();
            tf.setBackground(BACKGROUND_TERTIARY);
            tf.setForeground(TEXT_PRIMARY);
            tf.setCaretColor(SUCCESS);
            tf.setSelectionColor(PRIMARY);
            tf.setSelectedTextColor(TEXT_PRIMARY);
            tf.setFont(FONT_REGULAR);
            tf.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
        }

        // Style spinner buttons
        for (Component c : spinner.getComponents()) {
            if (c instanceof JButton) {
                JButton btn = (JButton) c;
                btn.setBackground(PRIMARY);
                btn.setForeground(TEXT_PRIMARY);
                btn.setBorder(BorderFactory.createLineBorder(BORDER));
            }
        }
    }

    /**
     * Apply theme to JDateChooser (from JCalendar library).
     */
    public static void styleDateChooser(Component dateChooser) {
        if (dateChooser == null) return;

        dateChooser.setBackground(BACKGROUND_TERTIARY);
        dateChooser.setForeground(TEXT_PRIMARY);

        // Style all child components recursively
        styleComponentRecursive(dateChooser);

        // If this is a JDateChooser, style using JCalendar's direct API
        if (dateChooser instanceof JDateChooser) {
            JDateChooser chooser = (JDateChooser) dateChooser;

            // Get the JCalendar and style it directly
            com.toedter.calendar.JCalendar calendar = chooser.getJCalendar();
            if (calendar != null) {
                styleJCalendarDirect(calendar);
            }

            // Style the calendar button and add click listener
            for (Component c : chooser.getComponents()) {
                if (c instanceof JButton) {
                    JButton calBtn = (JButton) c;
                    calBtn.setBackground(PRIMARY);
                    calBtn.setBorder(BorderFactory.createLineBorder(BORDER));

                    // Re-style the calendar each time popup is shown
                    calBtn.addActionListener(e -> {
                        SwingUtilities.invokeLater(() -> {
                            if (chooser.getJCalendar() != null) {
                                styleJCalendarDirect(chooser.getJCalendar());
                            }
                            // Also find and style popup windows
                            for (Window window : Window.getWindows()) {
                                if (window.isVisible() && window.getClass().getName().contains("Popup")) {
                                    stylePopupWindowDeep(window);
                                }
                                if (window instanceof JWindow && window.isVisible()) {
                                    stylePopupWindowDeep(window);
                                }
                            }
                        });
                    });
                }
            }
        }
    }

    /**
     * Style JCalendar using its direct API methods.
     */
    private static void styleJCalendarDirect(com.toedter.calendar.JCalendar calendar) {
        // Main calendar background
        calendar.setBackground(BACKGROUND_SECONDARY);
        calendar.setForeground(TEXT_PRIMARY);

        // Style the day chooser
        com.toedter.calendar.JDayChooser dayChooser = calendar.getDayChooser();
        if (dayChooser != null) {
            dayChooser.setBackground(BACKGROUND_SECONDARY);
            dayChooser.setForeground(TEXT_PRIMARY);
            dayChooser.setSundayForeground(DANGER);
            dayChooser.setWeekdayForeground(TEXT_PRIMARY);
            dayChooser.setDecorationBackgroundColor(BACKGROUND_TERTIARY);

            // Style all day buttons
            for (Component comp : dayChooser.getComponents()) {
                styleCalendarComponentDeep(comp);
            }
        }

        // Style the month chooser
        com.toedter.calendar.JMonthChooser monthChooser = calendar.getMonthChooser();
        if (monthChooser != null) {
            monthChooser.setBackground(BACKGROUND_TERTIARY);
            monthChooser.setForeground(TEXT_PRIMARY);

            // Style the combo box inside month chooser
            for (Component comp : monthChooser.getComponents()) {
                if (comp instanceof JComboBox) {
                    // Use full styleComboBox to get proper dark popup styling
                    styleComboBox((JComboBox<?>) comp);
                }
                comp.setBackground(BACKGROUND_TERTIARY);
                comp.setForeground(TEXT_PRIMARY);
            }
        }

        // Style the year chooser
        com.toedter.calendar.JYearChooser yearChooser = calendar.getYearChooser();
        if (yearChooser != null) {
            yearChooser.setBackground(BACKGROUND_TERTIARY);
            yearChooser.setForeground(TEXT_PRIMARY);

            // Style spinner inside year chooser
            for (Component comp : yearChooser.getComponents()) {
                if (comp instanceof JSpinner) {
                    styleSpinner((JSpinner) comp);
                }
                comp.setBackground(BACKGROUND_TERTIARY);
                comp.setForeground(TEXT_PRIMARY);
            }
        }

        // Recursively style everything else
        styleCalendarComponentDeep(calendar);
    }

    /**
     * Create a dark-themed list cell renderer for combo boxes.
     */
    private static DefaultListCellRenderer createDarkListCellRenderer() {
        return new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                list.setBackground(BACKGROUND_SECONDARY);
                if (isSelected) {
                    setBackground(PRIMARY);
                    setForeground(TEXT_PRIMARY);
                } else {
                    setBackground(BACKGROUND_SECONDARY);
                    setForeground(TEXT_PRIMARY);
                }
                return this;
            }
        };
    }

    /**
     * Deep style for calendar components.
     */
    private static void styleCalendarComponentDeep(Component comp) {
        if (comp == null) return;

        comp.setBackground(BACKGROUND_SECONDARY);
        comp.setForeground(TEXT_PRIMARY);

        if (comp instanceof JPanel) {
            ((JPanel) comp).setOpaque(true);
            ((JPanel) comp).setBackground(BACKGROUND_SECONDARY);
        }

        if (comp instanceof JButton) {
            JButton btn = (JButton) comp;
            btn.setBackground(BACKGROUND_TERTIARY);
            btn.setForeground(TEXT_PRIMARY);
            btn.setOpaque(true);
            btn.setBorderPainted(true);
        }

        if (comp instanceof JComboBox) {
            JComboBox<?> combo = (JComboBox<?>) comp;
            combo.setBackground(BACKGROUND_TERTIARY);
            combo.setForeground(TEXT_PRIMARY);
            combo.setRenderer(createDarkListCellRenderer());
        }

        if (comp instanceof JSpinner) {
            styleSpinner((JSpinner) comp);
        }

        if (comp instanceof JTextField) {
            JTextField tf = (JTextField) comp;
            tf.setBackground(BACKGROUND_TERTIARY);
            tf.setForeground(TEXT_PRIMARY);
            tf.setCaretColor(SUCCESS);
        }

        if (comp instanceof JLabel) {
            ((JLabel) comp).setForeground(TEXT_PRIMARY);
            ((JLabel) comp).setBackground(BACKGROUND_SECONDARY);
        }

        if (comp instanceof Container) {
            for (Component child : ((Container) comp).getComponents()) {
                styleCalendarComponentDeep(child);
            }
        }
    }

    /**
     * Style popup window deeply.
     */
    private static void stylePopupWindowDeep(Window window) {
        window.setBackground(BACKGROUND_SECONDARY);
        for (Component comp : window.getComponents()) {
            styleCalendarComponentDeep(comp);
        }
    }

    /**
     * Recursively style all components.
     */
    private static void styleComponentRecursive(Component comp) {
        if (comp == null) return;

        comp.setBackground(BACKGROUND_TERTIARY);
        comp.setForeground(TEXT_PRIMARY);

        if (comp instanceof JTextField) {
            JTextField tf = (JTextField) comp;
            tf.setBackground(BACKGROUND_TERTIARY);
            tf.setForeground(TEXT_PRIMARY);
            tf.setCaretColor(SUCCESS);
            tf.setSelectionColor(PRIMARY);
            tf.setSelectedTextColor(TEXT_PRIMARY);
            tf.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
        } else if (comp instanceof JButton) {
            JButton btn = (JButton) comp;
            btn.setBackground(PRIMARY);
            btn.setForeground(TEXT_PRIMARY);
            btn.setBorder(BorderFactory.createLineBorder(BORDER));
        } else if (comp instanceof JComboBox) {
            styleComboBox((JComboBox<?>) comp);
        } else if (comp instanceof JSpinner) {
            styleSpinner((JSpinner) comp);
        } else if (comp instanceof JToggleButton) {
            JToggleButton tb = (JToggleButton) comp;
            tb.setBackground(BACKGROUND_TERTIARY);
            tb.setForeground(TEXT_PRIMARY);
        } else if (comp instanceof JLabel) {
            comp.setForeground(TEXT_PRIMARY);
        }

        if (comp instanceof Container) {
            for (Component child : ((Container) comp).getComponents()) {
                styleComponentRecursive(child);
            }
        }
    }

    /**
     * Style the JCalendar panel specifically.
     */
    private static void styleCalendarPanel(Component calendar) {
        if (calendar == null) return;

        calendar.setBackground(BACKGROUND_SECONDARY);
        calendar.setForeground(TEXT_PRIMARY);

        // Style recursively
        if (calendar instanceof Container) {
            for (Component child : ((Container) calendar).getComponents()) {
                styleCalendarComponent(child);
            }
        }
    }

    /**
     * Style individual calendar components.
     */
    private static void styleCalendarComponent(Component comp) {
        if (comp == null) return;

        String className = comp.getClass().getName();

        // Day chooser panel
        if (className.contains("JDayChooser")) {
            comp.setBackground(BACKGROUND_SECONDARY);
            comp.setForeground(TEXT_PRIMARY);
            try {
                // Set day colors via reflection
                java.lang.reflect.Method setSunday = comp.getClass().getMethod("setSundayForeground", Color.class);
                java.lang.reflect.Method setWeekday = comp.getClass().getMethod("setWeekdayForeground", Color.class);
                java.lang.reflect.Method setDecoBg = comp.getClass().getMethod("setDecorationBackgroundColor", Color.class);
                setSunday.invoke(comp, DANGER);
                setWeekday.invoke(comp, TEXT_PRIMARY);
                setDecoBg.invoke(comp, BACKGROUND_TERTIARY);
            } catch (Exception ignored) {}
        }
        // Month chooser
        else if (className.contains("JMonthChooser")) {
            comp.setBackground(BACKGROUND_TERTIARY);
            comp.setForeground(TEXT_PRIMARY);
        }
        // Year chooser
        else if (className.contains("JYearChooser")) {
            comp.setBackground(BACKGROUND_TERTIARY);
            comp.setForeground(TEXT_PRIMARY);
        }
        // Generic styling
        else {
            comp.setBackground(BACKGROUND_SECONDARY);
            comp.setForeground(TEXT_PRIMARY);
        }

        // Style combo boxes in calendar (month dropdown)
        if (comp instanceof JComboBox) {
            JComboBox<?> combo = (JComboBox<?>) comp;
            combo.setBackground(BACKGROUND_TERTIARY);
            combo.setForeground(TEXT_PRIMARY);
            combo.setBorder(BorderFactory.createLineBorder(BORDER));
            // Style the dropdown list
            combo.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value,
                        int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (isSelected) {
                        setBackground(PRIMARY);
                        setForeground(TEXT_PRIMARY);
                    } else {
                        setBackground(BACKGROUND_SECONDARY);
                        setForeground(TEXT_PRIMARY);
                    }
                    setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
                    return this;
                }
            });
        }

        // Style spinners in calendar (year spinner)
        if (comp instanceof JSpinner) {
            styleSpinner((JSpinner) comp);
        }

        // Style buttons in calendar (day buttons and navigation buttons)
        if (comp instanceof JButton) {
            JButton btn = (JButton) comp;
            btn.setBackground(BACKGROUND_TERTIARY);
            btn.setForeground(TEXT_PRIMARY);
            btn.setBorder(BorderFactory.createLineBorder(BORDER));
            btn.setFocusPainted(false);
        }

        // Style text fields
        if (comp instanceof JTextField) {
            JTextField tf = (JTextField) comp;
            tf.setBackground(BACKGROUND_TERTIARY);
            tf.setForeground(TEXT_PRIMARY);
            tf.setCaretColor(SUCCESS);
            tf.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
        }

        // Recurse into children
        if (comp instanceof Container) {
            for (Component child : ((Container) comp).getComponents()) {
                styleCalendarComponent(child);
            }
        }
    }

    /**
     * Create a styled text area.
     */
    public static JTextArea createTextArea(int rows, int cols) {
        JTextArea area = new JTextArea(rows, cols);
        area.setBackground(BACKGROUND_TERTIARY);
        area.setForeground(TEXT_PRIMARY);
        area.setCaretColor(TEXT_PRIMARY);
        area.setFont(FONT_REGULAR);
        area.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        return area;
    }

    /**
     * Create a styled combo box with custom colors and themed UI.
     */
    public static <T> JComboBox<T> createComboBox() {
        JComboBox<T> combo = new JComboBox<>();
        styleComboBox(combo);
        return combo;
    }

    /**
     * Apply themed styling to any combo box.
     */
    public static void styleComboBox(JComboBox<?> combo) {
        combo.setBackground(BACKGROUND_TERTIARY);
        combo.setForeground(TEXT_PRIMARY);
        combo.setFont(FONT_REGULAR);
        combo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            BorderFactory.createEmptyBorder(2, 5, 2, 5)));

        // Custom UI for the combo box
        combo.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton button = new JButton() {
                    @Override
                    public void paintComponent(Graphics g) {
                        Graphics2D g2d = (Graphics2D) g.create();
                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                        // Button background
                        g2d.setColor(PRIMARY);
                        g2d.fillRect(0, 0, getWidth(), getHeight());

                        // Draw arrow
                        g2d.setColor(TEXT_PRIMARY);
                        int cx = getWidth() / 2;
                        int cy = getHeight() / 2;
                        int[] xPoints = {cx - 5, cx + 5, cx};
                        int[] yPoints = {cy - 2, cy - 2, cy + 4};
                        g2d.fillPolygon(xPoints, yPoints, 3);

                        g2d.dispose();
                    }
                };
                button.setBorder(BorderFactory.createEmptyBorder());
                button.setBackground(PRIMARY);
                button.setFocusable(false);
                return button;
            }

            @Override
            protected ComboPopup createPopup() {
                BasicComboPopup popup = new BasicComboPopup(comboBox);
                popup.setBorder(BorderFactory.createLineBorder(SUCCESS, 1));
                popup.getList().setBackground(BACKGROUND_SECONDARY);
                popup.getList().setForeground(TEXT_PRIMARY);
                popup.getList().setSelectionBackground(PRIMARY);
                popup.getList().setSelectionForeground(TEXT_PRIMARY);
                return popup;
            }
        });

        // Custom renderer for dropdown items
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                setFont(FONT_REGULAR);

                if (isSelected) {
                    setBackground(PRIMARY);
                    setForeground(TEXT_PRIMARY);
                    setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 3, 0, 0, SUCCESS),
                        BorderFactory.createEmptyBorder(6, 8, 6, 8)));
                } else {
                    setBackground(BACKGROUND_SECONDARY);
                    setForeground(TEXT_PRIMARY);
                    setBorder(BorderFactory.createEmptyBorder(6, 11, 6, 8));
                }

                return this;
            }
        });
    }

    /**
     * Create a styled label with emoji support.
     */
    public static JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT_SECONDARY);
        label.setFont(FONT_REGULAR);
        return label;
    }

    /**
     * Create a colored label.
     */
    public static JLabel createColoredLabel(String text, Color color) {
        JLabel label = new JLabel(text);
        label.setForeground(color);
        label.setFont(FONT_REGULAR);
        return label;
    }

    /**
     * Create a header label.
     */
    public static JLabel createHeaderLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT_PRIMARY);
        label.setFont(FONT_SUBTITLE);
        return label;
    }

    /**
     * Create an emoji label with proper font.
     */
    public static JLabel createEmojiLabel(String emoji, int size) {
        JLabel label = new JLabel(emoji);
        label.setFont(new Font(EMOJI_FONT_NAME, Font.PLAIN, size));
        return label;
    }

    /**
     * Create a section panel with title.
     */
    public static JPanel createSectionPanel(String title) {
        JPanel panel = new JPanel();
        panel.setBackground(BACKGROUND_SECONDARY);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        if (title != null) {
            panel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createTitledBorder(
                            BorderFactory.createLineBorder(BORDER),
                            title,
                            javax.swing.border.TitledBorder.LEFT,
                            javax.swing.border.TitledBorder.TOP,
                            FONT_HEADING,
                            TEXT_SECONDARY),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        }

        return panel;
    }

    /**
     * Create a card-style panel.
     */
    public static JPanel createCard() {
        JPanel panel = new JPanel();
        panel.setBackground(BACKGROUND_SECONDARY);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        return panel;
    }

    /**
     * Get a styled border.
     */
    public static Border createBorder() {
        return BorderFactory.createLineBorder(BORDER);
    }

    /**
     * Style a JScrollPane with themed scrollbars.
     */
    public static void styleScrollPane(JScrollPane scrollPane) {
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        if (scrollPane.getVerticalScrollBar() != null) {
            scrollPane.getVerticalScrollBar().setUI(new ThemedScrollBarUI());
            scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        }
        if (scrollPane.getHorizontalScrollBar() != null) {
            scrollPane.getHorizontalScrollBar().setUI(new ThemedScrollBarUI());
            scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
        }
    }

    /**
     * Custom scrollbar UI matching the dark theme.
     */
    public static class ThemedScrollBarUI extends BasicScrollBarUI {

        @Override
        protected void configureScrollBarColors() {
            this.thumbColor = PRIMARY;
            this.thumbDarkShadowColor = BORDER;
            this.thumbHighlightColor = SUCCESS;
            this.thumbLightShadowColor = PRIMARY_LIGHT;
            this.trackColor = BACKGROUND_TERTIARY;
            this.trackHighlightColor = BACKGROUND_SECONDARY;
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createArrowButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createArrowButton();
        }

        private JButton createArrowButton() {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            button.setMinimumSize(new Dimension(0, 0));
            button.setMaximumSize(new Dimension(0, 0));
            button.setBorder(BorderFactory.createEmptyBorder());
            button.setBackground(BACKGROUND_TERTIARY);
            button.setFocusable(false);
            return button;
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(BACKGROUND_TERTIARY);
            g2d.fillRoundRect(trackBounds.x + 2, trackBounds.y, trackBounds.width - 4, trackBounds.height, 8, 8);
            g2d.dispose();
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) {
                return;
            }

            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Thumb color changes on hover/drag
            Color thumbCol = isDragging ? SUCCESS : (isThumbRollover() ? PRIMARY_HOVER : PRIMARY);
            g2d.setColor(thumbCol);

            // Draw rounded thumb
            int arc = 8;
            g2d.fillRoundRect(thumbBounds.x + 3, thumbBounds.y + 2, thumbBounds.width - 6, thumbBounds.height - 4, arc, arc);

            g2d.dispose();
        }

        @Override
        protected Dimension getMinimumThumbSize() {
            return new Dimension(8, 40);
        }
    }

    /**
     * Create a gradient panel for headers.
     */
    public static JPanel createGradientPanel() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gradient = new GradientPaint(
                        0, 0, GRADIENT_START,
                        getWidth(), getHeight(), GRADIENT_END);
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
    }

    /**
     * Create a card with subtle glow effect.
     */
    public static JPanel createGlowCard(Color glowColor) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw subtle glow
                g2d.setColor(new Color(glowColor.getRed(), glowColor.getGreen(), glowColor.getBlue(), 20));
                g2d.fillRoundRect(-2, -2, getWidth() + 4, getHeight() + 4, 16, 16);

                // Draw card background
                g2d.setColor(CARD_BACKGROUND);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                // Draw border
                g2d.setColor(BORDER);
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);

                g2d.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        return panel;
    }

    /**
     * Create a stat card with accent top border.
     */
    public static JPanel createAccentCard(Color accentColor) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Card background
                g2d.setColor(CARD_BACKGROUND);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                // Accent top bar
                g2d.setColor(accentColor);
                g2d.fillRoundRect(0, 0, getWidth(), 4, 10, 10);
                g2d.fillRect(0, 2, getWidth(), 4);

                // Border
                g2d.setColor(BORDER);
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);

                g2d.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        return panel;
    }

    /**
     * Create a styled button with rounded corners, hover effect, and proper emoji support.
     */
    public static JButton createStyledButton(String text, Color bgColor, Color hoverColor) {
        JButton button = new JButton(text) {
            private boolean hover = false;
            private boolean pressed = false;

            {
                addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseEntered(java.awt.event.MouseEvent e) { hover = true; repaint(); }
                    public void mouseExited(java.awt.event.MouseEvent e) { hover = false; pressed = false; repaint(); }
                    public void mousePressed(java.awt.event.MouseEvent e) { pressed = true; repaint(); }
                    public void mouseReleased(java.awt.event.MouseEvent e) { pressed = false; repaint(); }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                Color bg = pressed ? hoverColor.darker() : (hover ? hoverColor : bgColor);

                // Draw shadow for depth
                if (!pressed) {
                    g2d.setColor(new Color(0, 0, 0, 40));
                    g2d.fillRoundRect(2, 3, getWidth() - 2, getHeight() - 2, 10, 10);
                }

                // Draw button background
                g2d.setColor(bg);
                g2d.fillRoundRect(0, pressed ? 2 : 0, getWidth() - 2, getHeight() - 3, 10, 10);

                // Draw subtle border
                g2d.setColor(new Color(255, 255, 255, 30));
                g2d.drawRoundRect(0, pressed ? 2 : 0, getWidth() - 3, getHeight() - 4, 10, 10);

                // Draw text with emoji font
                g2d.setColor(TEXT_PRIMARY);
                g2d.setFont(FONT_HEADING);
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2 + (pressed ? 1 : -1);
                g2d.drawString(getText(), x, y);

                g2d.dispose();
            }
        };
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setFont(FONT_HEADING);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(scaled(130), scaled(40)));
        return button;
    }

    /**
     * Create ASCII art bass fish for decorative use.
     */
    public static String getBassArt() {
        return """
                       ___
                    .-'   `'.
                   /         \\
                  |  O     ___|>
                   \\       /
                    '-._.-'
            """;
    }

    /**
     * Create a decorative wave pattern component.
     */
    public static JPanel createWaveDecoration(int height) {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                // Draw layered waves
                g2d.setColor(new Color(WATER_BLUE.getRed(), WATER_BLUE.getGreen(), WATER_BLUE.getBlue(), 30));
                drawWave(g2d, w, h, 0.02, 8, 0);

                g2d.setColor(new Color(INFO.getRed(), INFO.getGreen(), INFO.getBlue(), 20));
                drawWave(g2d, w, h, 0.015, 6, 50);

                g2d.dispose();
            }

            private void drawWave(Graphics2D g2d, int w, int h, double freq, int amp, int phase) {
                int[] xPoints = new int[w + 2];
                int[] yPoints = new int[w + 2];

                for (int x = 0; x <= w; x++) {
                    xPoints[x] = x;
                    yPoints[x] = (int) (h/2 + Math.sin((x + phase) * freq) * amp);
                }
                xPoints[w + 1] = w;
                yPoints[w + 1] = h;
                xPoints[0] = 0;

                g2d.fillPolygon(xPoints, yPoints, w + 2);
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(100, height);
            }
        };
    }

    /**
     * Create a panel with mountain silhouette decoration.
     */
    public static JPanel createMountainDecoration(int height) {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                // Back mountains
                g2d.setColor(new Color(0x1a, 0x2e, 0x28));
                int[] xPoints1 = {0, w/4, w/2, 3*w/4, w, w, 0};
                int[] yPoints1 = {h, h/3, h/2, h/4, h/2, h, h};
                g2d.fillPolygon(xPoints1, yPoints1, 7);

                // Front mountains
                g2d.setColor(new Color(0x14, 0x25, 0x20));
                int[] xPoints2 = {0, w/3, w/2, 2*w/3, w, w, 0};
                int[] yPoints2 = {h, h/2, 2*h/3, h/3, h/2, h, h};
                g2d.fillPolygon(xPoints2, yPoints2, 7);

                // Trees silhouette
                g2d.setColor(new Color(0x10, 0x1c, 0x18));
                for (int x = 0; x < w; x += 30) {
                    int treeH = 15 + (x * 7) % 20;
                    drawTree(g2d, x, h - 5, treeH);
                }

                g2d.dispose();
            }

            private void drawTree(Graphics2D g2d, int x, int baseY, int h) {
                int[] xPoints = {x, x + 8, x + 4};
                int[] yPoints = {baseY, baseY, baseY - h};
                g2d.fillPolygon(xPoints, yPoints, 3);
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(100, height);
            }

            @Override
            public boolean isOpaque() {
                return false;
            }
        };
    }

    /**
     * Create an animated background panel with floating particles and waves.
     */
    public static JPanel createAnimatedBackground() {
        return new AnimatedBackgroundPanel();
    }

    /**
     * Animated background panel with floating particles and subtle wave effects.
     */
    public static class AnimatedBackgroundPanel extends JPanel {
        private final List<Particle> particles = new ArrayList<>();
        private final Random random = new Random();
        private double waveOffset = 0;
        private Timer animationTimer;

        public AnimatedBackgroundPanel() {
            setOpaque(false);
            initParticles();
            startAnimation();
        }

        private void initParticles() {
            // Original subtle particles
            for (int i = 0; i < 10; i++) {
                particles.add(new Particle(random, false));
            }
            // Add more visible grey/white floating dots
            for (int i = 0; i < 85; i++) {
                particles.add(new Particle(random, true));
            }
        }

        private void startAnimation() {
            animationTimer = new Timer(50, e -> {
                waveOffset += 0.05;
                for (Particle p : particles) {
                    p.update();
                }
                repaint();
            });
            animationTimer.start();
        }

        @Override
        public void removeNotify() {
            super.removeNotify();
            if (animationTimer != null) {
                animationTimer.stop();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // Background gradient
            GradientPaint bgGradient = new GradientPaint(
                    0, 0, BACKGROUND,
                    w, h, new Color(0x0a, 0x15, 0x12));
            g2d.setPaint(bgGradient);
            g2d.fillRect(0, 0, w, h);

            // Draw subtle waves at bottom
            drawWaves(g2d, w, h);

            // Draw floating particles
            for (Particle p : particles) {
                p.draw(g2d, w, h);
            }

            // Draw subtle grid pattern
            drawGrid(g2d, w, h);

            g2d.dispose();
        }

        private void drawWaves(Graphics2D g2d, int w, int h) {
            // Multiple wave layers
            for (int layer = 0; layer < 3; layer++) {
                int alpha = 8 + layer * 4;
                double freq = 0.008 - layer * 0.002;
                int amp = 20 + layer * 10;
                int baseY = h - 50 - layer * 30;

                g2d.setColor(new Color(INFO.getRed(), INFO.getGreen(), INFO.getBlue(), alpha));

                int[] xPoints = new int[w + 2];
                int[] yPoints = new int[w + 2];

                for (int x = 0; x <= w; x++) {
                    xPoints[x] = x;
                    yPoints[x] = (int) (baseY + Math.sin((x * freq) + waveOffset + layer) * amp);
                }
                xPoints[w + 1] = w;
                yPoints[w + 1] = h;

                int[] fullX = new int[w + 3];
                int[] fullY = new int[w + 3];
                System.arraycopy(xPoints, 0, fullX, 0, w + 2);
                System.arraycopy(yPoints, 0, fullY, 0, w + 2);
                fullX[w + 2] = 0;
                fullY[w + 2] = h;

                g2d.fillPolygon(fullX, fullY, w + 3);
            }
        }

        private void drawGrid(Graphics2D g2d, int w, int h) {
            g2d.setColor(new Color(BORDER.getRed(), BORDER.getGreen(), BORDER.getBlue(), 15));
            g2d.setStroke(new BasicStroke(1));

            // Vertical lines
            for (int x = 0; x < w; x += 60) {
                g2d.drawLine(x, 0, x, h);
            }
            // Horizontal lines
            for (int y = 0; y < h; y += 60) {
                g2d.drawLine(0, y, w, y);
            }
        }

        private class Particle {
            double x, y;
            double speedX, speedY;
            double size;
            int alpha;
            Color color;
            Random rand;
            boolean isFloatingDot;
            double baseY;
            double phase;

            Particle(Random random, boolean floatingDot) {
                this.rand = random;
                this.isFloatingDot = floatingDot;
                this.phase = rand.nextDouble() * Math.PI * 2;
                reset(true);
            }

            void reset(boolean randomY) {
                x = rand.nextDouble() * 2000;
                y = randomY ? rand.nextDouble() * 1200 : -20;
                baseY = y;

                if (isFloatingDot) {
                    // Grey/white floating dots - move horizontally and bob up/down
                    speedX = (rand.nextDouble() - 0.5) * 1.8;
                    speedY = 0;
                    size = rand.nextDouble() * 6 + 2.5;
                    alpha = rand.nextInt(100) + 80;

                    // Grey to white colors - bright and visible
                    int grey = rand.nextInt(80) + 175; // 175-255 range (very bright)
                    color = new Color(grey, grey, grey, alpha);
                } else {
                    // Original falling particles
                    speedX = (rand.nextDouble() - 0.5) * 0.5;
                    speedY = rand.nextDouble() * 0.3 + 0.1;
                    size = rand.nextDouble() * 4 + 1;
                    alpha = rand.nextInt(40) + 10;

                    int colorChoice = rand.nextInt(3);
                    if (colorChoice == 0) {
                        color = new Color(SUCCESS.getRed(), SUCCESS.getGreen(), SUCCESS.getBlue(), alpha);
                    } else if (colorChoice == 1) {
                        color = new Color(INFO.getRed(), INFO.getGreen(), INFO.getBlue(), alpha);
                    } else {
                        color = new Color(200, 200, 200, alpha);
                    }
                }
            }

            void update() {
                if (isFloatingDot) {
                    // Horizontal drift with gentle bobbing
                    x += speedX;
                    phase += 0.03;
                    y = baseY + Math.sin(phase) * 15;

                    // Wrap around horizontally
                    if (x < -20) x = 2000;
                    if (x > 2000) x = -20;

                    // Slowly drift baseY
                    baseY += (rand.nextDouble() - 0.5) * 0.3;
                    if (baseY < 50) baseY = 50;
                    if (baseY > 1100) baseY = 1100;
                } else {
                    x += speedX;
                    y += speedY;
                    x += Math.sin(y * 0.02) * 0.3;

                    if (y > 1200) {
                        reset(false);
                    }
                }
            }

            void draw(Graphics2D g2d, int panelW, int panelH) {
                if (x >= 0 && x <= panelW && y >= 0 && y <= panelH) {
                    g2d.setColor(color);
                    g2d.fill(new Ellipse2D.Double(x, y, size, size));
                }
            }
        }
    }
}
