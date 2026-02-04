package com.lakelogger.ui.panels;

import com.lakelogger.service.AnalyticsService;
import com.lakelogger.service.AnalyticsService.*;
import com.lakelogger.ui.theme.DarkTheme;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Map;

/**
 * Panel displaying fishing statistics and analytics.
 */
public class StatsPanel extends JPanel {

    private final AnalyticsService analyticsService;
    private JPanel chartsContainer;

    public StatsPanel() {
        this.analyticsService = new AnalyticsService();
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        // Animated background
        JPanel animatedBg = DarkTheme.createAnimatedBackground();
        animatedBg.setLayout(new BorderLayout(20, 20));
        animatedBg.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        add(animatedBg, BorderLayout.CENTER);

        // Header with icon and subtitle
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JPanel titleSection = new JPanel();
        titleSection.setLayout(new BoxLayout(titleSection, BoxLayout.Y_AXIS));
        titleSection.setOpaque(false);

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titleRow.setOpaque(false);

        JLabel iconLabel = new JLabel("\uD83D\uDCCA ");
        iconLabel.setFont(new java.awt.Font(DarkTheme.EMOJI_FONT_NAME, java.awt.Font.PLAIN, 26));

        JLabel titleLabel = new JLabel("Statistics & Analytics");
        titleLabel.setFont(DarkTheme.FONT_TITLE);
        titleLabel.setForeground(DarkTheme.TEXT_PRIMARY);

        titleRow.add(iconLabel);
        titleRow.add(titleLabel);

        JLabel subtitleLabel = new JLabel("Insights to help you catch more bass");
        subtitleLabel.setFont(DarkTheme.FONT_REGULAR);
        subtitleLabel.setForeground(DarkTheme.TEXT_MUTED);
        subtitleLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        titleSection.add(titleRow);
        titleSection.add(subtitleLabel);
        headerPanel.add(titleSection, BorderLayout.WEST);

        JButton refreshBtn = DarkTheme.createInfoButton("Refresh");
        refreshBtn.setPreferredSize(new Dimension(110, 36));
        refreshBtn.addActionListener(e -> refresh());
        headerPanel.add(refreshBtn, BorderLayout.EAST);

        animatedBg.add(headerPanel, BorderLayout.NORTH);

        // Charts container
        chartsContainer = new JPanel();
        chartsContainer.setLayout(new BoxLayout(chartsContainer, BoxLayout.Y_AXIS));
        chartsContainer.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(chartsContainer);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        DarkTheme.styleScrollPane(scrollPane);

        animatedBg.add(scrollPane, BorderLayout.CENTER);

        loadStats();
    }

    private void loadStats() {
        chartsContainer.removeAll();

        // Summary Stats Card
        chartsContainer.add(createSummaryCard());
        chartsContainer.add(Box.createRigidArea(new Dimension(0, 20)));

        // Charts row
        JPanel chartsRow = new JPanel(new GridLayout(1, 2, 20, 0));
        chartsRow.setOpaque(false);
        chartsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 350));

        chartsRow.add(createBaitChart());
        chartsRow.add(createTimeOfDayChart());

        chartsContainer.add(chartsRow);
        chartsContainer.add(Box.createRigidArea(new Dimension(0, 20)));

        // Second row
        JPanel chartsRow2 = new JPanel(new GridLayout(1, 2, 20, 0));
        chartsRow2.setOpaque(false);
        chartsRow2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 350));

        chartsRow2.add(createWeatherChart());
        chartsRow2.add(createLocationChart());

        chartsContainer.add(chartsRow2);
        chartsContainer.add(Box.createRigidArea(new Dimension(0, 20)));

        // Detailed stats tables
        chartsContainer.add(createBaitStatsTable());
        chartsContainer.add(Box.createRigidArea(new Dimension(0, 20)));

        chartsContainer.add(createLocationStatsTable());

        chartsContainer.revalidate();
        chartsContainer.repaint();
    }

    private JPanel createSummaryCard() {
        SummaryStats stats = analyticsService.getSummaryStats();

        JPanel card = DarkTheme.createGlowCard(DarkTheme.SUCCESS);
        card.setLayout(new GridLayout(2, 4, 15, 10));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

        card.add(createStatBox("Catches", String.valueOf(stats.totalCatches), DarkTheme.INFO, "\uD83C\uDFA3"));
        card.add(createStatBox("Total Wt", String.format("%.1f lb", stats.totalWeight), DarkTheme.SUCCESS, "\u2696\uFE0F"));
        card.add(createStatBox("Avg Wt", String.format("%.1f lb", stats.avgWeight), DarkTheme.PRIMARY_LIGHT, "\uD83D\uDCCA"));
        card.add(createStatBox("Biggest", String.format("%.1f lb", stats.biggestWeight), DarkTheme.WARNING, "\uD83C\uDFC6"));

        card.add(createStatBox("Avg Len", String.format("%.1f in", stats.avgLength), DarkTheme.PRIMARY_LIGHT, "\uD83D\uDCCF"));
        card.add(createStatBox("Top Bait", stats.topBait != null ? truncate(stats.topBait, 10) : "N/A", DarkTheme.SUCCESS, "\uD83E\uDE9D"));
        card.add(createStatBox("Best Time", stats.bestTime != null ? stats.bestTime : "N/A", DarkTheme.INFO, "\u23F0"));
        card.add(createStatBox("Top Spot", stats.topLocation != null ? truncate(stats.topLocation, 10) : "N/A", DarkTheme.WARNING, "\uD83D\uDCCD"));

        return card;
    }

    private JPanel createStatBox(String label, String value, Color accentColor, String icon) {
        JPanel box = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Background
                g2d.setColor(DarkTheme.BACKGROUND_TERTIARY);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

                // Accent top bar
                g2d.setColor(accentColor);
                g2d.fillRoundRect(0, 0, getWidth(), 3, 8, 8);
                g2d.fillRect(0, 2, getWidth(), 2);

                g2d.dispose();
            }
        };
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setOpaque(false);
        box.setBorder(BorderFactory.createEmptyBorder(10, 8, 8, 8));

        // Icon - smaller
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new java.awt.Font(DarkTheme.EMOJI_FONT_NAME, java.awt.Font.PLAIN, 16));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        box.add(iconLabel);

        box.add(Box.createRigidArea(new Dimension(0, 3)));

        // Value - smaller font
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
        valueLabel.setForeground(DarkTheme.TEXT_PRIMARY);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Label - smaller
        JLabel titleLabel = new JLabel(label);
        titleLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 10));
        titleLabel.setForeground(DarkTheme.TEXT_SECONDARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        box.add(valueLabel);
        box.add(Box.createRigidArea(new Dimension(0, 2)));
        box.add(titleLabel);

        return box;
    }

    private JPanel createBaitChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        Map<String, BaitStats> baitStats = analyticsService.getBaitAnalysis();
        int count = 0;
        for (Map.Entry<String, BaitStats> entry : baitStats.entrySet()) {
            if (count++ >= 8) break;
            dataset.addValue(entry.getValue().count, "Catches", truncate(entry.getKey(), 12));
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Catches by Bait",
                "Bait",
                "Count",
                dataset,
                PlotOrientation.VERTICAL,
                false, true, false);

        styleChart(chart, DarkTheme.SUCCESS);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBackground(DarkTheme.BACKGROUND_SECONDARY);
        chartPanel.setBorder(BorderFactory.createLineBorder(DarkTheme.BORDER));

        return chartPanel;
    }

    private JPanel createTimeOfDayChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        Map<String, TimeStats> timeStats = analyticsService.getTimeOfDayAnalysis();
        for (Map.Entry<String, TimeStats> entry : timeStats.entrySet()) {
            dataset.addValue(entry.getValue().count, "Catches", entry.getKey());
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Catches by Time of Day",
                "Time",
                "Count",
                dataset,
                PlotOrientation.VERTICAL,
                false, true, false);

        styleChart(chart, DarkTheme.INFO);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBackground(DarkTheme.BACKGROUND_SECONDARY);
        chartPanel.setBorder(BorderFactory.createLineBorder(DarkTheme.BORDER));

        return chartPanel;
    }

    private JPanel createWeatherChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        Map<String, WeatherStats> weatherStats = analyticsService.getWeatherAnalysis();
        for (Map.Entry<String, WeatherStats> entry : weatherStats.entrySet()) {
            dataset.addValue(entry.getValue().avgWeight, "Avg Weight (lbs)", truncate(entry.getKey(), 10));
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Average Weight by Weather",
                "Weather",
                "Avg Weight (lbs)",
                dataset,
                PlotOrientation.VERTICAL,
                false, true, false);

        styleChart(chart, DarkTheme.WARNING);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBackground(DarkTheme.BACKGROUND_SECONDARY);
        chartPanel.setBorder(BorderFactory.createLineBorder(DarkTheme.BORDER));

        return chartPanel;
    }

    private JPanel createLocationChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        Map<String, LocationStats> locStats = analyticsService.getLocationAnalysis();
        int count = 0;
        for (Map.Entry<String, LocationStats> entry : locStats.entrySet()) {
            if (count++ >= 6) break;
            dataset.addValue(entry.getValue().count, "Catches", truncate(entry.getKey(), 12));
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Catches by Location",
                "Location",
                "Count",
                dataset,
                PlotOrientation.VERTICAL,
                false, true, false);

        styleChart(chart, DarkTheme.PRIMARY);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBackground(DarkTheme.BACKGROUND_SECONDARY);
        chartPanel.setBorder(BorderFactory.createLineBorder(DarkTheme.BORDER));

        return chartPanel;
    }

    private void styleChart(JFreeChart chart, Color barColor) {
        chart.setBackgroundPaint(DarkTheme.BACKGROUND_SECONDARY);
        chart.getTitle().setPaint(DarkTheme.TEXT_PRIMARY);
        chart.getTitle().setFont(DarkTheme.FONT_HEADING);

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(DarkTheme.BACKGROUND_TERTIARY);
        plot.setDomainGridlinePaint(DarkTheme.BORDER);
        plot.setRangeGridlinePaint(DarkTheme.BORDER);
        plot.setOutlinePaint(DarkTheme.BORDER);

        plot.getDomainAxis().setTickLabelPaint(DarkTheme.TEXT_SECONDARY);
        plot.getDomainAxis().setLabelPaint(DarkTheme.TEXT_SECONDARY);
        plot.getDomainAxis().setTickLabelFont(DarkTheme.FONT_SMALL);

        plot.getRangeAxis().setTickLabelPaint(DarkTheme.TEXT_SECONDARY);
        plot.getRangeAxis().setLabelPaint(DarkTheme.TEXT_SECONDARY);
        plot.getRangeAxis().setTickLabelFont(DarkTheme.FONT_SMALL);

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, barColor);
        renderer.setDrawBarOutline(false);
        renderer.setShadowVisible(false);
    }

    private JPanel createBaitStatsTable() {
        JPanel panel = DarkTheme.createSectionPanel("Bait Performance Details");
        panel.setLayout(new BorderLayout());
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));

        String[] columns = {"Bait", "Catches", "Avg Weight (lbs)", "Avg Length (in)", "Max Weight (lbs)"};
        Object[][] data = analyticsService.getBaitAnalysis().entrySet().stream()
                .map(e -> new Object[]{
                        e.getKey(),
                        e.getValue().count,
                        String.format("%.2f", e.getValue().avgWeight),
                        String.format("%.1f", e.getValue().avgLength),
                        String.format("%.2f", e.getValue().maxWeight)
                })
                .toArray(Object[][]::new);

        JTable table = createStatsTable(columns, data);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(DarkTheme.BACKGROUND_SECONDARY);
        DarkTheme.styleScrollPane(scrollPane);

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createLocationStatsTable() {
        JPanel panel = DarkTheme.createSectionPanel("Location Performance Details");
        panel.setLayout(new BorderLayout());
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));

        String[] columns = {"Location", "Catches", "Avg Weight (lbs)", "Total Weight (lbs)", "Max Weight (lbs)"};
        Object[][] data = analyticsService.getLocationAnalysis().entrySet().stream()
                .map(e -> new Object[]{
                        e.getKey(),
                        e.getValue().count,
                        String.format("%.2f", e.getValue().avgWeight),
                        String.format("%.1f", e.getValue().totalWeight),
                        String.format("%.2f", e.getValue().maxWeight)
                })
                .toArray(Object[][]::new);

        JTable table = createStatsTable(columns, data);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(DarkTheme.BACKGROUND_SECONDARY);
        DarkTheme.styleScrollPane(scrollPane);

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JTable createStatsTable(String[] columns, Object[][] data) {
        JTable table = new JTable(data, columns);
        table.setBackground(DarkTheme.BACKGROUND_SECONDARY);
        table.setForeground(DarkTheme.TEXT_PRIMARY);
        table.setSelectionBackground(DarkTheme.PRIMARY);
        table.setSelectionForeground(DarkTheme.TEXT_PRIMARY);
        table.setGridColor(DarkTheme.BORDER);
        table.setRowHeight(28);
        table.setFont(DarkTheme.FONT_REGULAR);
        table.getTableHeader().setBackground(DarkTheme.BACKGROUND_TERTIARY);
        table.getTableHeader().setForeground(DarkTheme.TEXT_PRIMARY);
        table.getTableHeader().setFont(DarkTheme.FONT_HEADING);
        table.setEnabled(false);
        return table;
    }

    private String truncate(String str, int maxLen) {
        if (str == null) return "";
        return str.length() > maxLen ? str.substring(0, maxLen - 2) + ".." : str;
    }

    public void refresh() {
        loadStats();
    }
}
