package com.peak885.luyten;

import javax.swing.*;
import java.awt.*;

public class CircleIcon implements Icon {

    private int size;

    public CircleIcon() {
        this(12);
    }

    public CircleIcon(int size) {
        this.size = size;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        g2.setColor(Color.LIGHT_GRAY);
        g2.fillOval(x, y, size, size);
        g2.setColor(Color.BLACK);
        g2.drawOval(x, y, size, size);

        g2.dispose();
    }

    @Override
    public int getIconWidth() {
        return size;
    }

    @Override
    public int getIconHeight() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }

}
