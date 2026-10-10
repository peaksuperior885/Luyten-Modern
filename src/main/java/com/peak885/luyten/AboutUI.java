package com.peak885.luyten;

import com.strobel.Procyon;
import net.miginfocom.swing.MigLayout;
import picocli.CommandLine;

import javax.swing.*;
import java.awt.*;

public class AboutUI {
    public AboutUI() {}

    public void Show() {
        // Main panel using MigLayout with better padding and vertical flow
        JPanel pane = new JPanel(new MigLayout("insets 16, fillx, wrap 1", "[grow]", "[]6[]4[]12[]12[]"));

        // Title and Author
        JLabel title = new JLabel("Luyten " + Luyten.VERSION);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        pane.add(title);

        pane.add(new JLabel("A Java Decompiler GUI by Deathmarine"));

        String project = "https://github.com/deathmarine/Luyten";
        pane.add(createLink(project));

        pane.add(new JSeparator(), "growx, gapy 4");

        // Contributors Section
        JLabel contribHeader = new JLabel("Contributions By:");
        contribHeader.setFont(contribHeader.getFont().deriveFont(Font.BOLD));
        pane.add(contribHeader);

        String contributors = "zerdei, toonetown, dstmath, virustotalop, xtrafrancyz, " +
                "mbax, quitten, mstrobel, FisheyLP, Syquel, and ThexXTURBOXx";
        JLabel contribLabel = new JLabel("<html><body style=\"width: 320px\">" + contributors + "</body></html>");
        pane.add(contribLabel);

        pane.add(new JSeparator(), "growx, gapy 4");

        // Powered By Section
        JLabel poweredHeader = new JLabel("Powered By:");
        poweredHeader.setFont(poweredHeader.getFont().deriveFont(Font.BOLD));
        pane.add(poweredHeader);

        // Sub-panel for libraries to keep them cleanly aligned
        JPanel libsPanel = new JPanel(new MigLayout("insets 0, wrap 1", "[grow]", "[]8[]"));

        // Procyon
        addLibraryRow(libsPanel, "Procyon Decompiler", "https://github.com/mstrobel/procyon",
                "Version: " + Procyon.version(), "(c) 2012 Mike Strobel");

        // RSyntaxTextArea
        addLibraryRow(libsPanel, "RSyntaxTextArea", "https://github.com/bobbylight/RSyntaxTextArea",
                "Version: 3.6.3", "(c) 2021 Robert Futrell");

        // picocli
        addLibraryRow(libsPanel, "picocli", "https://github.com/remkop/picocli",
                "Version: " + CommandLine.VERSION, "(c) 2017 Remko Popma");

        // MigLayout (Added as requested)
        addLibraryRow(libsPanel, "MigLayout", "http://www.migcomponents.com",
                "Version: 5.3", "(c) MigLayout");

        // FlatLaf (Added as requested)
        addLibraryRow(libsPanel, "FlatLaf", "https://www.formdev.com/flatlaf/",
                "Version: 3.6.2", "(c) FormDev Software GmbH");

        pane.add(libsPanel, "growx");

        JOptionPane.showMessageDialog(null, pane, "About Luyten", JOptionPane.PLAIN_MESSAGE);
    }

    private JLabel createLink(String url) {
        JLabel link = new JLabel("<HTML><FONT color=\"#0073e6\"><U>" + url + "</U></FONT></HTML>");
        link.setCursor(new Cursor(Cursor.HAND_CURSOR));
        link.addMouseListener(new MainMenuBar.LinkListener(url, link));
        return link;
    }

    private void addLibraryRow(JPanel panel, String name, String url, String version, String copyright) {
        JPanel row = new JPanel(new MigLayout("insets 0, wrap 1", "[grow]"));
        row.add(new JLabel("<html><b>" + name + "</b></html>"));
        row.add(createLink(url));
        row.add(new JLabel(version + "  |  " + copyright));
        panel.add(row, "growx, bottom");
    }
}