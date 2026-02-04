package com.lakelogger.ui.panels;

import com.lakelogger.dao.CatchDAO;
import com.lakelogger.model.Bait;
import com.lakelogger.model.CatchEntry;
import com.lakelogger.model.Location;
import com.lakelogger.ui.theme.DarkTheme;
import com.lakelogger.util.DateTimeUtil;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;
import java.util.List;

/**
 * Panel for logging a new bass catch.
 */
public class LogCatchPanel extends JPanel {

    private final CatchDAO catchDAO;
    private JDateChooser dateChooser;
    private JSpinner hourSpinner;
    private JSpinner minuteSpinner;
    private JComboBox<String> amPmCombo;
    private JSpinner lengthSpinner;
    private JSpinner weightSpinner;
    private JComboBox<String> locationCombo;
    private JComboBox<String> weatherCombo;
    private JSpinner tempSpinner;
    private JComboBox<String> baitCombo;
    private JTextArea notesArea;

    private Runnable onCatchSaved;

    public LogCatchPanel() {
        this.catchDAO = new CatchDAO();
        initializeUI();
    }

    public void setOnCatchSaved(Runnable callback) {
        this.onCatchSaved = callback;
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        // Animated background as base layer
        JPanel animatedBg = DarkTheme.createAnimatedBackground();
        animatedBg.setLayout(new BorderLayout(20, 20));
        animatedBg.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        add(animatedBg, BorderLayout.CENTER);

        // Header with icon and subtitle
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titleRow.setOpaque(false);

        JLabel iconLabel = new JLabel("\uD83C\uDFA3 ");
        iconLabel.setFont(new Font(DarkTheme.EMOJI_FONT_NAME, Font.PLAIN, 26));

        JLabel titleLabel = new JLabel("Log New Catch");
        titleLabel.setFont(DarkTheme.FONT_TITLE);
        titleLabel.setForeground(DarkTheme.TEXT_PRIMARY);

        titleRow.add(iconLabel);
        titleRow.add(titleLabel);

        JLabel subtitleLabel = new JLabel("Record the details of your bass catch");
        subtitleLabel.setFont(DarkTheme.FONT_REGULAR);
        subtitleLabel.setForeground(DarkTheme.TEXT_MUTED);
        subtitleLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 15, 0));

        headerPanel.add(titleRow);
        headerPanel.add(subtitleLabel);
        animatedBg.add(headerPanel, BorderLayout.NORTH);

        // Form panel with glow effect
        JPanel formCard = DarkTheme.createGlowCard(DarkTheme.SUCCESS);
        formCard.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Date
        gbc.gridx = 0; gbc.gridy = row;
        formCard.add(DarkTheme.createLabel("Date:"), gbc);
        gbc.gridx = 1;
        dateChooser = new JDateChooser();
        dateChooser.setDate(new java.util.Date());
        dateChooser.setPreferredSize(new Dimension(200, 35));
        DarkTheme.styleDateChooser(dateChooser);
        DarkTheme.styleDateChooser(dateChooser.getDateEditor().getUiComponent());
        formCard.add(dateChooser, gbc);
        row++;

        // Time
        gbc.gridx = 0; gbc.gridy = row;
        formCard.add(DarkTheme.createLabel("Time:"), gbc);
        gbc.gridx = 1;
        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        timePanel.setBackground(DarkTheme.BACKGROUND_SECONDARY);

        LocalTime now = LocalTime.now();
        int hour12 = now.getHour() % 12;
        if (hour12 == 0) hour12 = 12;

        hourSpinner = new JSpinner(new SpinnerNumberModel(hour12, 1, 12, 1));
        hourSpinner.setPreferredSize(new Dimension(60, 35));
        DarkTheme.styleSpinner(hourSpinner);
        timePanel.add(hourSpinner);

        timePanel.add(DarkTheme.createLabel(":"));

        minuteSpinner = new JSpinner(new SpinnerNumberModel(now.getMinute(), 0, 59, 1));
        minuteSpinner.setPreferredSize(new Dimension(60, 35));
        JSpinner.NumberEditor minuteEditor = new JSpinner.NumberEditor(minuteSpinner, "00");
        minuteSpinner.setEditor(minuteEditor);
        DarkTheme.styleSpinner(minuteSpinner);
        timePanel.add(minuteSpinner);

        amPmCombo = new JComboBox<>(new String[]{"AM", "PM"});
        amPmCombo.setSelectedItem(now.getHour() >= 12 ? "PM" : "AM");
        amPmCombo.setPreferredSize(new Dimension(70, 35));
        DarkTheme.styleComboBox(amPmCombo);
        timePanel.add(amPmCombo);

        formCard.add(timePanel, gbc);
        row++;

        // Length
        gbc.gridx = 0; gbc.gridy = row;
        formCard.add(DarkTheme.createLabel("Length (inches):"), gbc);
        gbc.gridx = 1;
        lengthSpinner = new JSpinner(new SpinnerNumberModel(12.0, 0.0, 36.0, 0.5));
        lengthSpinner.setPreferredSize(new Dimension(100, 35));
        DarkTheme.styleSpinner(lengthSpinner);
        formCard.add(lengthSpinner, gbc);
        row++;

        // Weight
        gbc.gridx = 0; gbc.gridy = row;
        formCard.add(DarkTheme.createLabel("Weight (lbs):"), gbc);
        gbc.gridx = 1;
        weightSpinner = new JSpinner(new SpinnerNumberModel(1.0, 0.0, 20.0, 0.25));
        weightSpinner.setPreferredSize(new Dimension(100, 35));
        DarkTheme.styleSpinner(weightSpinner);
        formCard.add(weightSpinner, gbc);
        row++;

        // Location
        gbc.gridx = 0; gbc.gridy = row;
        formCard.add(DarkTheme.createLabel("Location:"), gbc);
        gbc.gridx = 1;
        JPanel locationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        locationPanel.setBackground(DarkTheme.BACKGROUND_SECONDARY);
        locationCombo = new JComboBox<>();
        locationCombo.setEditable(true);
        locationCombo.setPreferredSize(new Dimension(200, 35));
        DarkTheme.styleComboBox(locationCombo);
        loadLocations();
        locationPanel.add(locationCombo);

        JButton addLocationBtn = DarkTheme.createSecondaryButton("+");
        addLocationBtn.setPreferredSize(new Dimension(40, 35));
        addLocationBtn.addActionListener(e -> addNewLocation());
        locationPanel.add(addLocationBtn);
        formCard.add(locationPanel, gbc);
        row++;

        // Weather
        gbc.gridx = 0; gbc.gridy = row;
        formCard.add(DarkTheme.createLabel("Weather:"), gbc);
        gbc.gridx = 1;
        weatherCombo = new JComboBox<>(new String[]{
                "Sunny", "Partly Cloudy", "Cloudy", "Overcast", "Rainy", "Stormy", "Windy", "Foggy"
        });
        weatherCombo.setPreferredSize(new Dimension(200, 35));
        DarkTheme.styleComboBox(weatherCombo);
        formCard.add(weatherCombo, gbc);
        row++;

        // Temperature
        gbc.gridx = 0; gbc.gridy = row;
        formCard.add(DarkTheme.createLabel("Temperature (°F):"), gbc);
        gbc.gridx = 1;
        tempSpinner = new JSpinner(new SpinnerNumberModel(70, 0, 120, 1));
        tempSpinner.setPreferredSize(new Dimension(100, 35));
        DarkTheme.styleSpinner(tempSpinner);
        formCard.add(tempSpinner, gbc);
        row++;

        // Bait
        gbc.gridx = 0; gbc.gridy = row;
        formCard.add(DarkTheme.createLabel("Bait:"), gbc);
        gbc.gridx = 1;
        JPanel baitPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        baitPanel.setBackground(DarkTheme.BACKGROUND_SECONDARY);
        baitCombo = new JComboBox<>();
        baitCombo.setEditable(true);
        baitCombo.setPreferredSize(new Dimension(200, 35));
        DarkTheme.styleComboBox(baitCombo);
        loadBaits();
        baitPanel.add(baitCombo);

        JButton addBaitBtn = DarkTheme.createSecondaryButton("+");
        addBaitBtn.setPreferredSize(new Dimension(40, 35));
        addBaitBtn.addActionListener(e -> addNewBait());
        baitPanel.add(addBaitBtn);
        formCard.add(baitPanel, gbc);
        row++;

        // Notes
        gbc.gridx = 0; gbc.gridy = row;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formCard.add(DarkTheme.createLabel("Notes:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        notesArea = DarkTheme.createTextArea(4, 30);
        JScrollPane notesScroll = new JScrollPane(notesArea);
        notesScroll.setPreferredSize(new Dimension(300, 100));
        notesScroll.setBorder(BorderFactory.createLineBorder(DarkTheme.BORDER));
        DarkTheme.styleScrollPane(notesScroll);
        formCard.add(notesScroll, gbc);
        row++;

        // Buttons
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weighty = 0;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 25));
        buttonPanel.setOpaque(false);

        JButton saveButton = DarkTheme.createSuccessButton("Save Catch");
        saveButton.setPreferredSize(new Dimension(140, 42));
        saveButton.addActionListener(e -> saveCatch());
        buttonPanel.add(saveButton);

        JButton clearButton = DarkTheme.createSecondaryButton("Clear Form");
        clearButton.setPreferredSize(new Dimension(130, 42));
        clearButton.addActionListener(e -> clearForm());
        buttonPanel.add(clearButton);

        formCard.add(buttonPanel, gbc);

        // Center the form
        JPanel centerWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerWrapper.setOpaque(false);
        centerWrapper.add(formCard);

        JScrollPane scrollPane = new JScrollPane(centerWrapper);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        DarkTheme.styleScrollPane(scrollPane);
        animatedBg.add(scrollPane, BorderLayout.CENTER);
    }


    private void loadLocations() {
        locationCombo.removeAllItems();
        List<String> locations = catchDAO.getAllLocations();
        for (String loc : locations) {
            locationCombo.addItem(loc);
        }
        if (locationCombo.getItemCount() == 0) {
            locationCombo.addItem("");
        }
    }

    private void loadBaits() {
        baitCombo.removeAllItems();
        List<String> baits = catchDAO.getAllBaits();
        for (String bait : baits) {
            baitCombo.addItem(bait);
        }
    }

    private void addNewLocation() {
        String newLocation = JOptionPane.showInputDialog(this,
                "Enter new location name:",
                "Add Location",
                JOptionPane.PLAIN_MESSAGE);

        if (newLocation != null && !newLocation.trim().isEmpty()) {
            catchDAO.saveLocation(new Location(newLocation.trim()));
            loadLocations();
            locationCombo.setSelectedItem(newLocation.trim());
        }
    }

    private void addNewBait() {
        String newBait = JOptionPane.showInputDialog(this,
                "Enter new bait name:",
                "Add Bait",
                JOptionPane.PLAIN_MESSAGE);

        if (newBait != null && !newBait.trim().isEmpty()) {
            catchDAO.saveBait(new Bait(newBait.trim()));
            loadBaits();
            baitCombo.setSelectedItem(newBait.trim());
        }
    }

    private void saveCatch() {
        try {
            CatchEntry entry = new CatchEntry();

            // Date
            if (dateChooser.getDate() != null) {
                entry.setCatchDate(DateTimeUtil.toLocalDate(dateChooser.getDate()));
            }

            // Time
            int hour = (Integer) hourSpinner.getValue();
            int minute = (Integer) minuteSpinner.getValue();
            String amPm = (String) amPmCombo.getSelectedItem();

            if ("PM".equals(amPm) && hour != 12) {
                hour += 12;
            } else if ("AM".equals(amPm) && hour == 12) {
                hour = 0;
            }
            entry.setCatchTime(LocalTime.of(hour, minute));

            // Measurements
            entry.setBassLengthInches((Double) lengthSpinner.getValue());
            entry.setBassWeightLbs((Double) weightSpinner.getValue());

            // Location
            Object locItem = locationCombo.getSelectedItem();
            if (locItem != null) {
                entry.setLocation(locItem.toString().trim());
            }

            // Weather
            entry.setWeather((String) weatherCombo.getSelectedItem());

            // Temperature
            entry.setTemperatureF((Integer) tempSpinner.getValue());

            // Bait
            Object baitItem = baitCombo.getSelectedItem();
            if (baitItem != null) {
                entry.setBait(baitItem.toString().trim());
            }

            // Notes
            entry.setNotes(notesArea.getText().trim());

            // Save
            int id = catchDAO.saveCatch(entry);

            if (id > 0) {
                JOptionPane.showMessageDialog(this,
                        String.format("Catch logged successfully!\n\n%.1f\" / %.2f lb %s on %s",
                                entry.getBassLengthInches(),
                                entry.getBassWeightLbs(),
                                entry.getBait(),
                                entry.getLocation()),
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                clearForm();

                if (onCatchSaved != null) {
                    onCatchSaved.run();
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to save catch. Please try again.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error saving catch: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void clearForm() {
        dateChooser.setDate(new java.util.Date());
        LocalTime now = LocalTime.now();
        int hour12 = now.getHour() % 12;
        if (hour12 == 0) hour12 = 12;
        hourSpinner.setValue(hour12);
        minuteSpinner.setValue(now.getMinute());
        amPmCombo.setSelectedItem(now.getHour() >= 12 ? "PM" : "AM");
        lengthSpinner.setValue(12.0);
        weightSpinner.setValue(1.0);
        if (locationCombo.getItemCount() > 0) {
            locationCombo.setSelectedIndex(0);
        }
        weatherCombo.setSelectedIndex(0);
        tempSpinner.setValue(70);
        if (baitCombo.getItemCount() > 0) {
            baitCombo.setSelectedIndex(0);
        }
        notesArea.setText("");
    }

    /**
     * Refresh dropdowns when panel is shown.
     */
    public void refresh() {
        loadLocations();
        loadBaits();
    }
}
