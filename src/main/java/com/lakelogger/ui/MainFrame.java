package com.lakelogger.ui;

import com.lakelogger.ui.panels.LogCatchPanel;
import com.lakelogger.ui.panels.StatsPanel;
import com.lakelogger.ui.panels.ViewCatchesPanel;
import com.lakelogger.ui.theme.DarkTheme;

import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * Main application frame with sidebar navigation.
 */
public class MainFrame extends JFrame {

    private JPanel contentPanel;
    private JPanel sidebarPanel;

    private LogCatchPanel logCatchPanel;
    private ViewCatchesPanel viewCatchesPanel;
    private StatsPanel statsPanel;

    private JPanel activeNavItem = null;
    /** Name of the currently visible content panel — preserved across rebuildUI(). */
    private String currentPanel = "log";

    private BufferedImage bannerImage;
    private BufferedImage iconImage;

    public MainFrame() {
        setTitle("Lake Logger - Bass Fishing Log");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Size window relative to screen resolution so it fits any display
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int w = Math.min(screen.width  - 100, Math.max(DarkTheme.scaled(1280), (int)(screen.width  * 0.78)));
        int h = Math.min(screen.height - 80,  Math.max(DarkTheme.scaled(850),  (int)(screen.height * 0.82)));
        setSize(w, h);
        setMinimumSize(new Dimension(DarkTheme.scaled(900), DarkTheme.scaled(620)));
        setLocationRelativeTo(null);

        loadImages();
        DarkTheme.apply();
        initializeUI();
        addZoomListener();

        // Set window icon
        if (iconImage != null) {
            setIconImage(iconImage);
        }
    }

    /**
     * Register a global key dispatcher for Ctrl +/- (zoom in/out) and Ctrl 0 (reset).
     * Triggers a full UI rebuild so every scaled dimension and font is updated.
     */
    private void addZoomListener() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
            if (e.getID() != KeyEvent.KEY_PRESSED || !e.isControlDown()) return false;
            boolean changed = false;
            int kc = e.getKeyCode();
            if (kc == KeyEvent.VK_PLUS || kc == KeyEvent.VK_EQUALS || kc == KeyEvent.VK_ADD) {
                changed = DarkTheme.scaleUp();
            } else if (kc == KeyEvent.VK_MINUS || kc == KeyEvent.VK_SUBTRACT) {
                changed = DarkTheme.scaleDown();
            } else if (kc == KeyEvent.VK_0 || kc == KeyEvent.VK_NUMPAD0) {
                DarkTheme.resetScale();
                changed = true;
            }
            if (changed) {
                e.consume();
                SwingUtilities.invokeLater(MainFrame.this::rebuildUI);
            }
            return false;
        });
    }

    /**
     * Tear down and recreate all UI panels using the current DarkTheme scale.
     * Called after every Ctrl +/- zoom change.
     */
    private void rebuildUI() {
        getContentPane().removeAll();
        DarkTheme.apply();
        initializeUI();
        showPanel(currentPanel);
        revalidate();
        repaint();
    }

    private void loadImages() {
        try {
            InputStream bannerStream = getClass().getResourceAsStream("/images/bg_banner.jpg");
            if (bannerStream != null) {
                bannerImage = ImageIO.read(bannerStream);
                bannerStream.close();
            }

            InputStream iconStream = getClass().getResourceAsStream("/images/icon.jpg");
            if (iconStream != null) {
                iconImage = ImageIO.read(iconStream);
                iconStream.close();
            }
        } catch (IOException e) {
            System.err.println("Could not load images: " + e.getMessage());
        }
    }

    private void initializeUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(DarkTheme.BACKGROUND);

        // Create sidebar
        sidebarPanel = createSidebar();
        mainPanel.add(sidebarPanel, BorderLayout.WEST);

        // Create content area
        contentPanel = new JPanel(new CardLayout());
        contentPanel.setBackground(DarkTheme.BACKGROUND);

        // Create panels
        logCatchPanel = new LogCatchPanel();
        viewCatchesPanel = new ViewCatchesPanel();
        statsPanel = new StatsPanel();

        // Wire up callback for when catch is saved
        logCatchPanel.setOnCatchSaved(() -> {
            viewCatchesPanel.refresh();
            statsPanel.refresh();
        });

        contentPanel.add(logCatchPanel, "log");
        contentPanel.add(viewCatchesPanel, "view");
        contentPanel.add(statsPanel, "stats");

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);

        // Show log panel by default
        showPanel("log");
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                // Gradient background
                GradientPaint gradient = new GradientPaint(
                        0, 0, DarkTheme.SIDEBAR,
                        0, getHeight(), new Color(0x08, 0x12, 0x10));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Right border glow
                g2d.setColor(new Color(DarkTheme.SUCCESS.getRed(), DarkTheme.SUCCESS.getGreen(), DarkTheme.SUCCESS.getBlue(), 30));
                g2d.fillRect(getWidth() - 2, 0, 2, getHeight());

                g2d.dispose();
            }
        };
        int sw = DarkTheme.scaled(240); // sidebar width for this render pass
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(sw, 0));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, DarkTheme.BORDER));

        // Banner image section
        final BufferedImage banner = bannerImage;
        final BufferedImage appIcon = iconImage;
        JPanel bannerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                if (banner != null) {
                    // Draw banner image scaled to fill
                    double scale = Math.max((double) getWidth() / banner.getWidth(),
                                           (double) getHeight() / banner.getHeight());
                    int scaledW = (int) (banner.getWidth() * scale);
                    int scaledH = (int) (banner.getHeight() * scale);
                    int x = (getWidth() - scaledW) / 2;
                    int y = (getHeight() - scaledH) / 2;
                    g2d.drawImage(banner, x, y, scaledW, scaledH, null);

                    // Dark overlay gradient for readability
                    GradientPaint overlay = new GradientPaint(
                            0, 0, new Color(0, 0, 0, 100),
                            0, getHeight(), new Color(0, 0, 0, 200));
                    g2d.setPaint(overlay);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                } else {
                    // Fallback gradient if no image
                    GradientPaint gradient = new GradientPaint(
                            0, 0, DarkTheme.FOREST_DARK,
                            0, getHeight(), DarkTheme.SIDEBAR);
                    g2d.setPaint(gradient);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }

                g2d.dispose();
            }
        };
        bannerPanel.setLayout(new GridBagLayout());
        bannerPanel.setPreferredSize(new Dimension(sw, DarkTheme.scaled(110)));
        bannerPanel.setMaximumSize(new Dimension(sw, DarkTheme.scaled(110)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, DarkTheme.scaled(15), 0, DarkTheme.scaled(15));
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Title with box frame and underline
        JLabel titleLabel = new JLabel("Lake Logger") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Outer glow effect
                for (int i = 4; i >= 1; i--) {
                    g2d.setColor(new Color(DarkTheme.SUCCESS.getRed(), DarkTheme.SUCCESS.getGreen(), DarkTheme.SUCCESS.getBlue(), 15 * i));
                    g2d.setStroke(new BasicStroke(i * 2f));
                    g2d.drawRoundRect(i, i, getWidth() - 1 - i * 2, getHeight() - 1 - i * 2, 10, 10);
                }

                // Main box frame with gradient stroke
                g2d.setColor(DarkTheme.SUCCESS);
                g2d.setStroke(new BasicStroke(2f));
                g2d.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 8, 8);

                // Inner highlight
                g2d.setColor(new Color(255, 255, 255, 60));
                g2d.setStroke(new BasicStroke(1f));
                g2d.drawRoundRect(4, 4, getWidth() - 9, getHeight() - 9, 6, 6);

                // Corner accents
                g2d.setColor(DarkTheme.SUCCESS);
                g2d.setStroke(new BasicStroke(2.5f));
                int cornerLen = 12;
                // Top-left
                g2d.drawLine(2, 10, 2, 2); g2d.drawLine(2, 2, 10, 2);
                // Top-right
                g2d.drawLine(getWidth() - 12, 2, getWidth() - 4, 2); g2d.drawLine(getWidth() - 4, 2, getWidth() - 4, 10);
                // Bottom-left
                g2d.drawLine(2, getHeight() - 12, 2, getHeight() - 4); g2d.drawLine(2, getHeight() - 4, 10, getHeight() - 4);
                // Bottom-right
                g2d.drawLine(getWidth() - 12, getHeight() - 4, getWidth() - 4, getHeight() - 4); g2d.drawLine(getWidth() - 4, getHeight() - 12, getWidth() - 4, getHeight() - 4);

                // Draw text shadow/glow
                FontMetrics fm = g2d.getFontMetrics(getFont());
                int textWidth = fm.stringWidth(getText());
                int textX = (getWidth() - textWidth) / 2;
                int textY = fm.getAscent() + (getHeight() - fm.getHeight()) / 2;

                // Text glow
                g2d.setFont(getFont());
                for (int i = 3; i >= 1; i--) {
                    g2d.setColor(new Color(DarkTheme.SUCCESS.getRed(), DarkTheme.SUCCESS.getGreen(), DarkTheme.SUCCESS.getBlue(), 30 * i));
                    g2d.drawString(getText(), textX - i, textY);
                    g2d.drawString(getText(), textX + i, textY);
                    g2d.drawString(getText(), textX, textY - i);
                    g2d.drawString(getText(), textX, textY + i);
                }

                // Gradient underline
                int underlineY = getHeight() - 10;
                GradientPaint underlineGradient = new GradientPaint(
                        textX, 0, new Color(DarkTheme.SUCCESS.getRed(), DarkTheme.SUCCESS.getGreen(), DarkTheme.SUCCESS.getBlue(), 50),
                        textX + textWidth / 2, 0, DarkTheme.SUCCESS, true);
                g2d.setPaint(underlineGradient);
                g2d.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2d.drawLine(textX, underlineY, textX + textWidth, underlineY);

                g2d.dispose();
                super.paintComponent(g);
            }
        };
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, DarkTheme.scaled(26)));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(DarkTheme.scaled(12), DarkTheme.scaled(20), DarkTheme.scaled(16), DarkTheme.scaled(20)));
        bannerPanel.add(titleLabel, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(DarkTheme.scaled(8), DarkTheme.scaled(15), 0, DarkTheme.scaled(15));
        JLabel subtitleLabel = new JLabel("Bass Fishing Tracker");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, DarkTheme.scaled(10)));
        subtitleLabel.setForeground(new Color(160, 160, 160));
        bannerPanel.add(subtitleLabel, gbc);

        sidebar.add(bannerPanel);

        // Decorative divider
        JPanel dividerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                GradientPaint gradient = new GradientPaint(
                        20, 0, new Color(0, 0, 0, 0),
                        getWidth() / 2, 0, DarkTheme.SUCCESS,
                        true);
                g2d.setPaint(gradient);
                g2d.fillRect(20, 0, getWidth() - 40, 1);
                g2d.dispose();
            }
        };
        dividerPanel.setOpaque(false);
        dividerPanel.setMaximumSize(new Dimension(sw, 1));
        dividerPanel.setPreferredSize(new Dimension(sw, 1));
        sidebar.add(dividerPanel);

        sidebar.add(Box.createRigidArea(new Dimension(0, DarkTheme.scaled(25))));

        // Section label
        JLabel navLabel = new JLabel("  NAVIGATION");
        navLabel.setFont(new Font("Segoe UI", Font.BOLD, DarkTheme.scaled(10)));
        navLabel.setForeground(DarkTheme.TEXT_MUTED);
        navLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        navLabel.setBorder(BorderFactory.createEmptyBorder(0, DarkTheme.scaled(20), DarkTheme.scaled(10), 0));
        navLabel.setMaximumSize(new Dimension(sw, DarkTheme.scaled(25)));
        sidebar.add(navLabel);

        // Navigation items
        JPanel logNav = createNavItem("Log Catch", "log", "\uD83D\uDCDD", "Record a new catch");
        JPanel viewNav = createNavItem("View Catches", "view", "\uD83D\uDCCB", "Browse your catches");
        JPanel statsNav = createNavItem("Statistics", "stats", "\uD83D\uDCCA", "Analytics & insights");

        sidebar.add(logNav);
        sidebar.add(Box.createRigidArea(new Dimension(0, DarkTheme.scaled(5))));
        sidebar.add(viewNav);
        sidebar.add(Box.createRigidArea(new Dimension(0, DarkTheme.scaled(5))));
        sidebar.add(statsNav);

        sidebar.add(Box.createVerticalGlue());

        // Mountain decoration at bottom
        JPanel mountainDecor = DarkTheme.createMountainDecoration(DarkTheme.scaled(60));
        mountainDecor.setMaximumSize(new Dimension(sw, DarkTheme.scaled(60)));
        sidebar.add(mountainDecor);

        // Footer
        JPanel footerPanel = new JPanel();
        footerPanel.setLayout(new BoxLayout(footerPanel, BoxLayout.Y_AXIS));
        footerPanel.setOpaque(false);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(DarkTheme.scaled(10), DarkTheme.scaled(25), DarkTheme.scaled(20), DarkTheme.scaled(25)));
        footerPanel.setMaximumSize(new Dimension(sw, DarkTheme.scaled(50)));

        JLabel versionLabel = new JLabel("v1.0.0");
        versionLabel.setFont(DarkTheme.FONT_SMALL);
        versionLabel.setForeground(new Color(DarkTheme.TEXT_MUTED.getRed(), DarkTheme.TEXT_MUTED.getGreen(), DarkTheme.TEXT_MUTED.getBlue(), 150));
        versionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        footerPanel.add(versionLabel);

        sidebar.add(footerPanel);

        // Select first nav item by default
        setActiveNavItem(logNav);

        return sidebar;
    }

    private JPanel createNavItem(String text, String panelName, String icon, String description) {
        JPanel navItem = new JPanel() {
            private boolean hover = false;
            private boolean active = false;

            {
                putClientProperty("setActive", (Runnable) () -> { active = true; repaint(); });
                putClientProperty("setInactive", (Runnable) () -> { active = false; repaint(); });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (active) {
                    // Active state - gradient background
                    GradientPaint gradient = new GradientPaint(
                            0, 0, DarkTheme.PRIMARY,
                            getWidth(), 0, new Color(DarkTheme.PRIMARY.getRed(), DarkTheme.PRIMARY.getGreen(), DarkTheme.PRIMARY.getBlue(), 100));
                    g2d.setPaint(gradient);
                    g2d.fillRoundRect(10, 2, getWidth() - 20, getHeight() - 4, 8, 8);

                    // Left accent bar
                    g2d.setColor(DarkTheme.SUCCESS);
                    g2d.fillRoundRect(10, 2, 3, getHeight() - 4, 2, 2);
                } else if (hover) {
                    // Hover state
                    g2d.setColor(new Color(DarkTheme.BACKGROUND_TERTIARY.getRed(), DarkTheme.BACKGROUND_TERTIARY.getGreen(), DarkTheme.BACKGROUND_TERTIARY.getBlue(), 150));
                    g2d.fillRoundRect(10, 2, getWidth() - 20, getHeight() - 4, 8, 8);
                }

                g2d.dispose();
            }

            public void setHover(boolean h) {
                hover = h;
                repaint();
            }
        };

        navItem.setLayout(new BorderLayout());
        navItem.setOpaque(false);
        navItem.setMaximumSize(new Dimension(DarkTheme.scaled(240), DarkTheme.scaled(58)));
        navItem.setPreferredSize(new Dimension(DarkTheme.scaled(240), DarkTheme.scaled(58)));
        navItem.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Left section with icon
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, DarkTheme.scaled(20), DarkTheme.scaled(8)));
        leftPanel.setOpaque(false);

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font(DarkTheme.EMOJI_FONT_NAME, Font.PLAIN, DarkTheme.scaled(18)));
        iconLabel.setForeground(DarkTheme.TEXT_SECONDARY);
        leftPanel.add(iconLabel);

        // Text section
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.setBorder(BorderFactory.createEmptyBorder(DarkTheme.scaled(8), 0, DarkTheme.scaled(8), 0));

        JLabel textLabel = new JLabel(text);
        textLabel.setFont(DarkTheme.FONT_HEADING);
        textLabel.setForeground(DarkTheme.TEXT_PRIMARY);

        JLabel descLabel = new JLabel(description);
        descLabel.setFont(DarkTheme.FONT_SMALL);
        descLabel.setForeground(DarkTheme.TEXT_MUTED);

        textPanel.add(textLabel);
        textPanel.add(descLabel);

        leftPanel.add(textPanel);
        navItem.add(leftPanel, BorderLayout.CENTER);

        // Store references
        navItem.putClientProperty("panelName", panelName);
        navItem.putClientProperty("textLabel", textLabel);
        navItem.putClientProperty("iconLabel", iconLabel);

        navItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showPanel(panelName);
                setActiveNavItem(navItem);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (navItem != activeNavItem) {
                    try {
                        navItem.getClass().getMethod("setHover", boolean.class).invoke(navItem, true);
                    } catch (Exception ex) { /* ignore */ }
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (navItem != activeNavItem) {
                    try {
                        navItem.getClass().getMethod("setHover", boolean.class).invoke(navItem, false);
                    } catch (Exception ex) { /* ignore */ }
                }
            }
        });

        return navItem;
    }

    private void setActiveNavItem(JPanel navItem) {
        // Reset previous active item
        if (activeNavItem != null) {
            Runnable setInactive = (Runnable) activeNavItem.getClientProperty("setInactive");
            if (setInactive != null) setInactive.run();

            JLabel textLabel = (JLabel) activeNavItem.getClientProperty("textLabel");
            JLabel iconLabel = (JLabel) activeNavItem.getClientProperty("iconLabel");
            if (textLabel != null) textLabel.setForeground(DarkTheme.TEXT_PRIMARY);
            if (iconLabel != null) iconLabel.setForeground(DarkTheme.TEXT_SECONDARY);
        }

        // Set new active item
        activeNavItem = navItem;
        if (activeNavItem != null) {
            Runnable setActive = (Runnable) activeNavItem.getClientProperty("setActive");
            if (setActive != null) setActive.run();

            JLabel textLabel = (JLabel) activeNavItem.getClientProperty("textLabel");
            JLabel iconLabel = (JLabel) activeNavItem.getClientProperty("iconLabel");
            if (textLabel != null) textLabel.setForeground(DarkTheme.TEXT_PRIMARY);
            if (iconLabel != null) iconLabel.setForeground(DarkTheme.SUCCESS);
        }
    }

    private void showPanel(String name) {
        currentPanel = name;
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        cl.show(contentPanel, name);

        // Refresh panels when shown
        switch (name) {
            case "log":
                logCatchPanel.refresh();
                break;
            case "view":
                viewCatchesPanel.refresh();
                break;
            case "stats":
                statsPanel.refresh();
                break;
        }
    }
}
