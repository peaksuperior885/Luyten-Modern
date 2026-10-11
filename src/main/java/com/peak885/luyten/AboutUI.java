package com.peak885.luyten;

import com.peak885.luyten.image.SvgIconLoader;
import com.strobel.Procyon;
import net.miginfocom.swing.MigLayout;
import picocli.CommandLine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AboutUI {
    private static final Color FALLBACK_LINK = new Color(0x0073e6);
    private static final Color FALLBACK_MUTED = Color.GRAY;

    private static final String ORIGINAL_URL = "https://github.com/deathmarine/Luyten";
    private static final String FORK_URL = "https://github.com/peaksuperior885/Luyten-Modern";

    public AboutUI() {}

    public void Show() {
        final JDialog dialog = new JDialog((Frame) null, "About Luyten", true);

        JPanel root = new JPanel(new MigLayout("insets 0, fill, wrap 1, gapy 0", "[grow, fill, 480:480:]", "[][grow][]"));

        root.add(buildHeader(), "growx");
        root.add(buildBody(), "grow");
        root.add(buildFooter(dialog), "growx");

        dialog.setContentPane(root);
        dialog.setResizable(false);
        dialog.pack();
        dialog.setLocationRelativeTo(null);

        // Esc closes the dialog
        dialog.getRootPane().registerKeyboardAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        }, KeyStroke.getKeyStroke("ESCAPE"), JComponent.WHEN_IN_FOCUSED_WINDOW);

        dialog.setVisible(true);
    }

    // ------------------------------------------------------------------ sections

    private JComponent buildHeader() {
        JPanel header = new JPanel(new MigLayout(
                "insets 22 24 18 24, fillx, wrap 1, gapy 0",
                "[grow, fill]"
        ));
        header.setBackground(headerBackground());

        // Logo + title + version
        JPanel titleRow = new JPanel(new MigLayout(
                "insets 0, gapx 12",
                "[]10[][]",
                "[]"
        ));
        titleRow.setOpaque(false);

        JLabel logo = new JLabel(
                SvgIconLoader.load("/Luyten.svg", 64)
        );
        titleRow.add(logo, "aligny center");

        JLabel title = new JLabel("Luyten");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 26f));
        titleRow.add(title, "aligny center");

        titleRow.add(
                new PillLabel("v" + Luyten.VERSION),
                "aligny center"
        );

        header.add(titleRow);

        JLabel subtitle = new JLabel("A Java Decompiler GUI by Deathmarine");
        subtitle.setForeground(muted());
        header.add(subtitle, "gaptop 6, gapleft 60");

        return header;
    }
    private JComponent buildBody() {
        JPanel body = new JPanel(new MigLayout("insets 4 24 12 24, fillx, wrap 1, gapy 0", "[grow, fill]"));

        // ---- Links ----
        addSectionHeader(body, "Links");

        JPanel links = new JPanel(new MigLayout("insets 0, wrap 2, gapy 4", "[]16[grow, fill]"));
        links.add(mutedLabel("Deathmarine / Luyten"));
        links.add(createLink(ORIGINAL_URL));
        links.add(mutedLabel("Peak885 / Luyten Modern"));
        links.add(createLink(FORK_URL));
        body.add(links, "growx");

        // ---- Contributors ----
        addSectionHeader(body, "Contributions by");

        String contributors = "zerdei, toonetown, dstmath, virustotalop, xtrafrancyz, "
                + "mbax, quitten, mstrobel, FisheyLP, Syquel, ThexXTURBOXx and Peak885";
        body.add(createWrappedText(contributors), "growx, wmin 0");

        // ---- Libraries ----
        addSectionHeader(body, "Powered by");

        JPanel libs = new JPanel(new MigLayout("insets 0, wrap 3, gapy 0", "[]20[]20[grow, fill]"));

        addLibraryRow(libs, "Procyon Decompiler", "https://github.com/nbauma109/procyon",
                Procyon.version(), "\u00a9 2026 Nicolas Baumann");
        addLibraryRow(libs, "RSyntaxTextArea", "https://github.com/bobbylight/RSyntaxTextArea",
                "3.6.3", "\u00a9 2021 Robert Futrell");
        addLibraryRow(libs, "picocli", "https://github.com/remkop/picocli",
                CommandLine.VERSION, "\u00a9 2017 Remko Popma");
        addLibraryRow(libs, "MigLayout", "https://www.miglayout.com",
                "5.3", "\u00a9 MigLayout");
        addLibraryRow(libs, "FlatLaf", "https://www.formdev.com/flatlaf/",
                "3.6.2", "\u00a9 FormDev Software GmbH");

        body.add(libs, "growx");
        return body;
    }

    private JComponent buildFooter(final JDialog dialog) {
        JPanel footer = new JPanel(new MigLayout("insets 0, fillx, wrap 1", "[grow, fill]"));
        footer.add(new JSeparator(), "growx");

        JPanel buttons = new JPanel(new MigLayout("insets 10 24 12 24, fillx", "[grow][]"));
        JButton ok = new JButton("OK");
        ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        buttons.add(new JLabel(), "growx");
        buttons.add(ok, "w 80!");
        footer.add(buttons, "growx");

        dialog.getRootPane().setDefaultButton(ok);
        dialog.setDefaultCloseOperation(
                WindowConstants.DISPOSE_ON_CLOSE
        );
        return footer;
    }

    // ------------------------------------------------------------------ helpers

    private void addSectionHeader(JPanel pane, String text) {
        JPanel header = new JPanel(new MigLayout("insets 0, fillx", "[][grow, fill]", "[]"));
        JLabel label = new JLabel(text.toUpperCase());
        label.setFont(label.getFont().deriveFont(Font.BOLD, label.getFont().getSize2D() - 1f));
        label.setForeground(muted());
        header.add(label);
        header.add(new JSeparator(), "gapleft 10, aligny center");
        pane.add(header, "growx, gaptop 16, gapbottom 8");
    }

    private JTextArea createWrappedText(String text) {
        JTextArea area = new JTextArea(text);
        area.setEditable(false);
        area.setFocusable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setOpaque(false);
        area.setBorder(null);
        area.setFont(UIManager.getFont("Label.font"));
        area.setForeground(UIManager.getColor("Label.foreground"));
        return area;
    }

    private void addLibraryRow(JPanel panel, String name, String url, String version, String copyright) {
        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD));
        panel.add(nameLabel);

        JLabel versionLabel = new JLabel("v" + version);
        versionLabel.setForeground(muted());
        panel.add(versionLabel);

        panel.add(createLink(url));

        JLabel copyLabel = new JLabel(copyright);
        copyLabel.setFont(copyLabel.getFont().deriveFont(copyLabel.getFont().getSize2D() - 1f));
        copyLabel.setForeground(muted());
        panel.add(copyLabel, "span 3, gapbottom 8");
    }

    private JLabel mutedLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(muted());
        return label;
    }

    private JLabel createLink(String label, String url) {
        String hex = String.format("#%06x",
                linkColor().getRGB() & 0xFFFFFF);

        JLabel link = new JLabel(
                "<html><font color=\"" + hex + "\"><u>"
                        + label
                        + "</u></font></html>"
        );

        link.setCursor(new Cursor(Cursor.HAND_CURSOR));
        link.setToolTipText(url);
        link.addMouseListener(new MainMenuBar.LinkListener(url, link));
        return link;
    }

    private JLabel createLink(String url) {
        return createLink(url, url);
    }

    private static Color muted() {
        Color c = UIManager.getColor("Label.disabledForeground");
        return c != null ? c : FALLBACK_MUTED;
    }

    private static Color linkColor() {
        Color c = UIManager.getColor("Component.linkColor");
        return c != null ? c : FALLBACK_LINK;
    }

    /** Slightly tinted version of the panel background, works in light and dark themes. */
    private static Color headerBackground() {
        Color base = UIManager.getColor("Panel.background");
        if (base == null) {
            base = Color.WHITE;
        }
        boolean dark = (base.getRed() + base.getGreen() + base.getBlue()) / 3 < 128;
        int delta = dark ? 12 : -10;
        return new Color(clamp(base.getRed() + delta), clamp(base.getGreen() + delta), clamp(base.getBlue() + delta));
    }

    private static int clamp(int v) {
        return Math.max(0, Math.min(255, v));
    }

    /** Small rounded "badge" label used for the version. */
    private static class PillLabel extends JLabel {
        PillLabel(String text) {
            super(text);
            setFont(getFont().deriveFont(Font.BOLD, getFont().getSize2D() - 1f));
            setForeground(linkColor());
            setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color c = linkColor();
            g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 36));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
            g2.dispose();
            super.paintComponent(g);
        }
    }
}