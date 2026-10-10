package com.peak885.luyten;

import com.peak885.luyten.image.SvgIconLoader;

import javax.swing.*;
import java.awt.*;

public final class SplashScreen {

    private static final int ICON_SIZE = 512;

    private final JWindow window;

    public SplashScreen() {
        window = new JWindow();

        JLabel icon = new JLabel(
                SvgIconLoader.load("/Luyten.svg", ICON_SIZE)
        );

        icon.setHorizontalAlignment(SwingConstants.CENTER);
        icon.setVerticalAlignment(SwingConstants.CENTER);

        icon.setOpaque(false);

        window.setBackground(new Color(0, 0, 0, 0));

        window.setContentPane(icon);
        window.setSize(ICON_SIZE, ICON_SIZE);
        window.setLocationRelativeTo(null);
    }

    public void show() {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(this::show);
            return;
        }

        window.setVisible(true);
    }

    public void close() {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(this::close);
            return;
        }

        window.setVisible(false);
        window.dispose();
    }
}