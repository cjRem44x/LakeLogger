package com.lakelogger.ui.panels;

import com.lakelogger.dao.CatchDAO;
import com.lakelogger.model.CatchEntry;
import com.lakelogger.ui.theme.DarkTheme;
import com.lakelogger.util.DateTimeUtil;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Panel for viewing and managing logged catches.
 */
public class ViewCatchesPanel extends JPanel {

    private final CatchDAO catchDAO;
    private JTable catchTable;
    private DefaultTableModel tableModel;
    private JDateChooser startDateChooser;
    private JDateChooser endDateChooser;
    private JComboBox<String> locationFilter;
    private JComboBox<String> baitFilter;
    private List<CatchEntry> currentCatches;

    public ViewCatchesPanel() {
        this.catchDAO = new CatchDAO();
        initializeUI();
        loadCatches();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        // Animated background
        JPanel animatedBg = DarkTheme.createAnimatedBackground();
        animatedBg.setLayout(new BorderLayout(20, 20));
        animatedBg.setBorder(BorderFactory.createEmptyBorder(DarkTheme.scaled(30), DarkTheme.scaled(40), DarkTheme.scaled(30), DarkTheme.scaled(40)));
        add(animatedBg, BorderLayout.CENTER);

        // Header with icon and subtitle
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titleRow.setOpaque(false);

        JLabel iconLabel = new JLabel("\uD83D\uDCCB ");
        iconLabel.setFont(new Font(DarkTheme.EMOJI_FONT_NAME, Font.PLAIN, DarkTheme.scaled(26)));

        JLabel titleLabel = new JLabel("View Catches");
        titleLabel.setFont(DarkTheme.FONT_TITLE);
        titleLabel.setForeground(DarkTheme.TEXT_PRIMARY);

        titleRow.add(iconLabel);
        titleRow.add(titleLabel);

        JLabel subtitleLabel = new JLabel("Browse and manage your logged catches");
        subtitleLabel.setFont(DarkTheme.FONT_REGULAR);
        subtitleLabel.setForeground(DarkTheme.TEXT_MUTED);
        subtitleLabel.setBorder(BorderFactory.createEmptyBorder(DarkTheme.scaled(5), 0, DarkTheme.scaled(15), 0));

        headerPanel.add(titleRow);
        headerPanel.add(subtitleLabel);
        animatedBg.add(headerPanel, BorderLayout.NORTH);

        // Filter panel with styled card
        JPanel filterCard = DarkTheme.createGlowCard(DarkTheme.INFO);
        filterCard.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));

        filterCard.add(DarkTheme.createLabel("From:"));
        startDateChooser = createDateChooser();
        filterCard.add(startDateChooser);

        filterCard.add(DarkTheme.createLabel("To:"));
        endDateChooser = createDateChooser();
        filterCard.add(endDateChooser);

        filterCard.add(DarkTheme.createLabel("Location:"));
        locationFilter = new JComboBox<>();
        locationFilter.setPreferredSize(new Dimension(DarkTheme.scaled(150), DarkTheme.scaled(32)));
        DarkTheme.styleComboBox(locationFilter);
        filterCard.add(locationFilter);

        filterCard.add(DarkTheme.createLabel("Bait:"));
        baitFilter = new JComboBox<>();
        baitFilter.setPreferredSize(new Dimension(DarkTheme.scaled(150), DarkTheme.scaled(32)));
        DarkTheme.styleComboBox(baitFilter);
        filterCard.add(baitFilter);

        JButton filterBtn = DarkTheme.createPrimaryButton("Filter");
        filterBtn.addActionListener(e -> applyFilter());
        filterCard.add(filterBtn);

        JButton clearFilterBtn = DarkTheme.createSecondaryButton("Clear");
        clearFilterBtn.addActionListener(e -> clearFilter());
        filterCard.add(clearFilterBtn);

        // Table
        String[] columns = {"ID", "Date", "Time", "Length (in)", "Weight (lbs)", "Location", "Weather", "Temp", "Bait"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int column) {
                switch (column) {
                    case 0: return Integer.class;
                    case 3: case 4: return Double.class;
                    case 7: return Integer.class;
                    default: return String.class;
                }
            }
        };

        catchTable = new JTable(tableModel);
        catchTable.setBackground(DarkTheme.BACKGROUND_SECONDARY);
        catchTable.setForeground(DarkTheme.TEXT_PRIMARY);
        catchTable.setSelectionBackground(DarkTheme.PRIMARY);
        catchTable.setSelectionForeground(DarkTheme.TEXT_PRIMARY);
        catchTable.setGridColor(DarkTheme.BORDER);
        catchTable.setRowHeight(DarkTheme.scaled(30));
        catchTable.setFont(DarkTheme.FONT_REGULAR);
        catchTable.getTableHeader().setBackground(DarkTheme.BACKGROUND_TERTIARY);
        catchTable.getTableHeader().setForeground(DarkTheme.TEXT_PRIMARY);
        catchTable.getTableHeader().setFont(DarkTheme.FONT_HEADING);

        // Hide ID column
        catchTable.getColumnModel().getColumn(0).setMinWidth(0);
        catchTable.getColumnModel().getColumn(0).setMaxWidth(0);
        catchTable.getColumnModel().getColumn(0).setPreferredWidth(0);

        // Enable sorting
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        catchTable.setRowSorter(sorter);

        // Custom cell renderer for alternating rows
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? DarkTheme.BACKGROUND_SECONDARY : DarkTheme.BACKGROUND_TERTIARY);
                }
                c.setForeground(DarkTheme.TEXT_PRIMARY);
                return c;
            }
        };

        for (int i = 0; i < catchTable.getColumnCount(); i++) {
            catchTable.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }

        JScrollPane scrollPane = new JScrollPane(catchTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(DarkTheme.BORDER));
        scrollPane.getViewport().setBackground(DarkTheme.BACKGROUND_SECONDARY);
        DarkTheme.styleScrollPane(scrollPane);

        // Action buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        actionPanel.setOpaque(false);

        JButton editBtn = DarkTheme.createPrimaryButton("Edit");
        editBtn.addActionListener(e -> editSelectedCatch());
        actionPanel.add(editBtn);

        JButton deleteBtn = DarkTheme.createDangerButton("Delete");
        deleteBtn.addActionListener(e -> deleteSelectedCatch());
        actionPanel.add(deleteBtn);

        JButton exportBtn = DarkTheme.createSecondaryButton("Export CSV");
        exportBtn.addActionListener(e -> exportToCsv());
        actionPanel.add(exportBtn);

        JButton refreshBtn = DarkTheme.createSecondaryButton("Refresh");
        refreshBtn.addActionListener(e -> loadCatches());
        actionPanel.add(refreshBtn);

        // Layout
        JPanel centerPanel = new JPanel(new BorderLayout(0, 15));
        centerPanel.setOpaque(false);
        centerPanel.add(filterCard, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(actionPanel, BorderLayout.SOUTH);

        animatedBg.add(centerPanel, BorderLayout.CENTER);

        loadFilters();
    }

    private JDateChooser createDateChooser() {
        JDateChooser chooser = new JDateChooser();
        chooser.setPreferredSize(new Dimension(DarkTheme.scaled(130), DarkTheme.scaled(32)));
        DarkTheme.styleDateChooser(chooser);
        DarkTheme.styleDateChooser(chooser.getDateEditor().getUiComponent());
        return chooser;
    }

    private void loadFilters() {
        locationFilter.removeAllItems();
        locationFilter.addItem("All");
        for (String loc : catchDAO.getAllLocations()) {
            locationFilter.addItem(loc);
        }

        baitFilter.removeAllItems();
        baitFilter.addItem("All");
        for (String bait : catchDAO.getAllBaits()) {
            baitFilter.addItem(bait);
        }
    }

    public void loadCatches() {
        loadFilters();
        currentCatches = catchDAO.getAllCatches();
        populateTable(currentCatches);
    }

    private void applyFilter() {
        LocalDate startDate = DateTimeUtil.toLocalDate(startDateChooser.getDate());
        LocalDate endDate = DateTimeUtil.toLocalDate(endDateChooser.getDate());
        String location = (String) locationFilter.getSelectedItem();
        String bait = (String) baitFilter.getSelectedItem();

        currentCatches = catchDAO.getCatchesWithFilter(startDate, endDate, location, bait);
        populateTable(currentCatches);
    }

    private void clearFilter() {
        startDateChooser.setDate(null);
        endDateChooser.setDate(null);
        locationFilter.setSelectedIndex(0);
        baitFilter.setSelectedIndex(0);
        loadCatches();
    }

    private void populateTable(List<CatchEntry> catches) {
        tableModel.setRowCount(0);

        for (CatchEntry c : catches) {
            tableModel.addRow(new Object[]{
                    c.getId(),
                    c.getCatchDate() != null ? DateTimeUtil.formatDisplayDate(c.getCatchDate()) : "",
                    c.getCatchTime() != null ? DateTimeUtil.formatDisplayTime(c.getCatchTime()) : "",
                    c.getBassLengthInches(),
                    c.getBassWeightLbs(),
                    c.getLocation(),
                    c.getWeather(),
                    c.getTemperatureF(),
                    c.getBait()
            });
        }
    }

    private void editSelectedCatch() {
        int selectedRow = catchTable.getSelectedRow();
        if (selectedRow < 0) {
            DarkTheme.showMessage(this, "Please select a catch to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = catchTable.convertRowIndexToModel(selectedRow);
        int catchId = (Integer) tableModel.getValueAt(modelRow, 0);
        CatchEntry entry = catchDAO.getCatchById(catchId);

        if (entry == null) {
            DarkTheme.showMessage(this, "Could not load catch data.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create edit dialog
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Catch", true);
        dialog.setSize(500, 600);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(DarkTheme.BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Date
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(DarkTheme.createLabel("Date:"), gbc);
        gbc.gridx = 1;
        JDateChooser dateEdit = createDateChooser();
        dateEdit.setDate(DateTimeUtil.toUtilDate(entry.getCatchDate()));
        panel.add(dateEdit, gbc);
        row++;

        // Time
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(DarkTheme.createLabel("Time:"), gbc);
        gbc.gridx = 1;
        JTextField timeEdit = DarkTheme.createTextField();
        timeEdit.setText(entry.getCatchTime() != null ? DateTimeUtil.formatTime(entry.getCatchTime()) : "");
        panel.add(timeEdit, gbc);
        row++;

        // Length
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(DarkTheme.createLabel("Length (in):"), gbc);
        gbc.gridx = 1;
        JSpinner lengthEdit = new JSpinner(new SpinnerNumberModel(entry.getBassLengthInches(), 0.0, 36.0, 0.5));
        DarkTheme.styleSpinner(lengthEdit);
        panel.add(lengthEdit, gbc);
        row++;

        // Weight
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(DarkTheme.createLabel("Weight (lbs):"), gbc);
        gbc.gridx = 1;
        JSpinner weightEdit = new JSpinner(new SpinnerNumberModel(entry.getBassWeightLbs(), 0.0, 20.0, 0.25));
        DarkTheme.styleSpinner(weightEdit);
        panel.add(weightEdit, gbc);
        row++;

        // Location
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(DarkTheme.createLabel("Location:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> locationEdit = new JComboBox<>();
        locationEdit.setEditable(true);
        for (String loc : catchDAO.getAllLocations()) {
            locationEdit.addItem(loc);
        }
        locationEdit.setSelectedItem(entry.getLocation());
        DarkTheme.styleComboBox(locationEdit);
        panel.add(locationEdit, gbc);
        row++;

        // Weather
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(DarkTheme.createLabel("Weather:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> weatherEdit = new JComboBox<>(new String[]{
                "Sunny", "Partly Cloudy", "Cloudy", "Overcast", "Rainy", "Stormy", "Windy", "Foggy"
        });
        weatherEdit.setSelectedItem(entry.getWeather());
        DarkTheme.styleComboBox(weatherEdit);
        panel.add(weatherEdit, gbc);
        row++;

        // Temperature
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(DarkTheme.createLabel("Temp (°F):"), gbc);
        gbc.gridx = 1;
        JSpinner tempEdit = new JSpinner(new SpinnerNumberModel(entry.getTemperatureF(), 0, 120, 1));
        DarkTheme.styleSpinner(tempEdit);
        panel.add(tempEdit, gbc);
        row++;

        // Bait
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(DarkTheme.createLabel("Bait:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> baitEdit = new JComboBox<>();
        baitEdit.setEditable(true);
        for (String bait : catchDAO.getAllBaits()) {
            baitEdit.addItem(bait);
        }
        baitEdit.setSelectedItem(entry.getBait());
        DarkTheme.styleComboBox(baitEdit);
        panel.add(baitEdit, gbc);
        row++;

        // Notes
        gbc.gridx = 0; gbc.gridy = row;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(DarkTheme.createLabel("Notes:"), gbc);
        gbc.gridx = 1;
        JTextArea notesEdit = DarkTheme.createTextArea(3, 20);
        notesEdit.setText(entry.getNotes());
        JScrollPane notesScroll = new JScrollPane(notesEdit);
        notesScroll.setPreferredSize(new Dimension(200, 80));
        notesScroll.setBorder(BorderFactory.createLineBorder(DarkTheme.BORDER));
        DarkTheme.styleScrollPane(notesScroll);
        panel.add(notesScroll, gbc);
        row++;

        // Buttons
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnPanel.setBackground(DarkTheme.BACKGROUND);

        JButton saveBtn = DarkTheme.createSuccessButton("Save");
        saveBtn.addActionListener(e -> {
            entry.setCatchDate(DateTimeUtil.toLocalDate(dateEdit.getDate()));
            LocalTime time = DateTimeUtil.parseTime(timeEdit.getText().trim());
            entry.setCatchTime(time);
            entry.setBassLengthInches((Double) lengthEdit.getValue());
            entry.setBassWeightLbs((Double) weightEdit.getValue());
            entry.setLocation(locationEdit.getSelectedItem() != null ? locationEdit.getSelectedItem().toString() : "");
            entry.setWeather((String) weatherEdit.getSelectedItem());
            entry.setTemperatureF((Integer) tempEdit.getValue());
            entry.setBait(baitEdit.getSelectedItem() != null ? baitEdit.getSelectedItem().toString() : "");
            entry.setNotes(notesEdit.getText());

            if (catchDAO.updateCatch(entry)) {
                DarkTheme.showMessage(dialog, "Catch updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadCatches();
            } else {
                DarkTheme.showMessage(dialog, "Failed to update catch.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        btnPanel.add(saveBtn);

        JButton cancelBtn = DarkTheme.createSecondaryButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());
        btnPanel.add(cancelBtn);

        panel.add(btnPanel, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void deleteSelectedCatch() {
        int selectedRow = catchTable.getSelectedRow();
        if (selectedRow < 0) {
            DarkTheme.showMessage(this, "Please select a catch to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = DarkTheme.showConfirm(this,
                "Are you sure you want to delete this catch?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            int modelRow = catchTable.convertRowIndexToModel(selectedRow);
            int catchId = (Integer) tableModel.getValueAt(modelRow, 0);

            if (catchDAO.deleteCatch(catchId)) {
                DarkTheme.showMessage(this, "Catch deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadCatches();
            } else {
                DarkTheme.showMessage(this, "Failed to delete catch.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exportToCsv() {
        if (currentCatches == null || currentCatches.isEmpty()) {
            DarkTheme.showMessage(this, "No catches to export.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export to CSV");
        fileChooser.setSelectedFile(new File("catches_export.csv"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().endsWith(".csv")) {
                file = new File(file.getAbsolutePath() + ".csv");
            }

            try (FileWriter writer = new FileWriter(file)) {
                writer.write(catchDAO.exportToCsv(currentCatches));
                DarkTheme.showMessage(this,
                        "Exported " + currentCatches.size() + " catches to:\n" + file.getAbsolutePath(),
                        "Export Complete",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                DarkTheme.showMessage(this,
                        "Error exporting: " + ex.getMessage(),
                        "Export Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void refresh() {
        loadCatches();
    }
}
