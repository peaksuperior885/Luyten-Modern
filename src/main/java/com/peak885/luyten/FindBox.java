package com.peak885.luyten;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.SearchContext;
import org.fife.ui.rtextarea.SearchEngine;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class FindBox extends JDialog {

    private static final long serialVersionUID = -4125409760166690462L;

    private final JCheckBox mcase;
    private final JCheckBox regex;
    private final JCheckBox wholew;
    private final JCheckBox reverse;
    private final JCheckBox wrap;
    private final JButton findButton;
    private final JTextField textField;
    private final MainWindow mainWindow;

    public void showFindBox() {
        this.setVisible(true);
        this.textField.requestFocus();
        this.textField.selectAll();
    }

    public void hideFindBox() {
        this.setVisible(false);
    }

    public FindBox(final MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        this.setDefaultCloseOperation(HIDE_ON_CLOSE);
        this.setHideOnEscapeButton();

        JLabel label = new JLabel("Find What:");
        textField = new JTextField();

        RSyntaxTextArea pane = mainWindow.getSelectedModel().getCurrentTextArea();
        if (pane != null) {
            textField.setText(pane.getSelectedText());
        }
        mcase = new JCheckBox("Match Case");
        regex = new JCheckBox("Regex");
        wholew = new JCheckBox("Whole Words");
        reverse = new JCheckBox("Search Backwards");
        wrap = new JCheckBox("Wrap", true);

        findButton = new JButton("Find");
        findButton.addActionListener(new FindButton());
        this.getRootPane().setDefaultButton(findButton);

        KeyStroke funcF3 = KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0, false);
        this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(funcF3, "FindNext");
        this.getRootPane().getActionMap().put("FindNext", new FindExploreAction(true));

        KeyStroke sfuncF3 = KeyStroke.getKeyStroke(KeyEvent.VK_F3, InputEvent.SHIFT_DOWN_MASK, false);
        this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(sfuncF3, "FindPrevious");
        this.getRootPane().getActionMap().put("FindPrevious", new FindExploreAction(false));

        mcase.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        regex.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        wholew.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        reverse.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        wrap.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        final Dimension center = new Dimension((int) (screenSize.width * 0.35),
                Math.min((int) (screenSize.height * 0.20), 200));
        final int x = (int) (center.width * 0.2);
        final int y = (int) (center.height * 0.2);
        this.setBounds(x, y, center.width, center.height);
        this.setResizable(false);

        JPanel content = new JPanel(new MigLayout("insets 10, gap 8", "[][grow][]", "[][][][]"));
        content.add(label);
        content.add(textField, "growx");
        content.add(findButton, "wrap");
        content.add(mcase);
        content.add(regex, "span 2, wrap");
        content.add(wholew);
        content.add(reverse, "span 2, wrap");
        content.add(wrap, "span 3");
        setContentPane(content);

        this.setTitle("Find");
        this.setVisible(true);
    }

    private class FindButton extends AbstractAction {
        private static final long serialVersionUID = 75954129199541874L;

        @Override
        public void actionPerformed(ActionEvent event) {
            if (textField.getText().isEmpty())
                return;

            RSyntaxTextArea pane = mainWindow.getSelectedModel().getCurrentTextArea();
            if (pane == null)
                return;

            SearchContext context = new SearchContext();
            context.setSearchFor(textField.getText());
            context.setMatchCase(mcase.isSelected());
            context.setRegularExpression(regex.isSelected());
            context.setSearchForward(!reverse.isSelected());
            context.setWholeWord(wholew.isSelected());

            if (!SearchEngine.find(pane, context).wasFound()) {
                if (wrap.isSelected()) {
                    pane.setSelectionStart(0);
                    pane.setSelectionEnd(0);
                    if (SearchEngine.find(pane, context).wasFound()) {
                        mainWindow.getLabel().setText("Reached end of file, wrapped search.");
                    } else {
                        mainWindow.getLabel().setText("Search Complete");
                    }
                } else {
                    mainWindow.getLabel().setText("Search Complete");
                }
            }
        }

    }

    private void setHideOnEscapeButton() {
        Action escapeAction = new AbstractAction() {
            private static final long serialVersionUID = 5572504000935312338L;

            @Override
            public void actionPerformed(ActionEvent e) {
                FindBox.this.setVisible(false);
            }
        };

        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
        this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKeyStroke, "ESCAPE");
        this.getRootPane().getActionMap().put("ESCAPE", escapeAction);
    }

    private void adjustWindowPositionBySavedState() {
        WindowPosition windowPosition = ConfigSaver.getLoadedInstance().getFindWindowPosition();

        if (windowPosition.isSavedWindowPositionValid()) {
            this.setLocation(windowPosition.getWindowX(), windowPosition.getWindowY());
        }
    }

    private void setSaveWindowPositionOnClosing() {
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowDeactivated(WindowEvent e) {
                WindowPosition windowPosition = ConfigSaver.getLoadedInstance().getFindWindowPosition();
                windowPosition.readPositionFromDialog(FindBox.this);
            }
        });
    }

    public void fireExploreAction(boolean direction) {
        new FindExploreAction(direction).actionPerformed(null);
    }

    class FindExploreAction extends AbstractAction {
        /**
         *
         */
        private static final long serialVersionUID = -4391670062679240573L;
        boolean direction;

        public FindExploreAction(boolean forward) {
            direction = forward;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (textField.getText().isEmpty())
                return;
            RSyntaxTextArea pane = mainWindow.getSelectedModel().getCurrentTextArea();
            if (pane == null)
                return;
            SearchContext context = new SearchContext();
            context.setSearchFor(textField.getText());
            context.setMatchCase(mcase.isSelected());
            context.setRegularExpression(regex.isSelected());
            context.setSearchForward(direction);
            context.setWholeWord(wholew.isSelected());

            if (!SearchEngine.find(pane, context).wasFound()) {
                if (wrap.isSelected()) {
                    pane.setSelectionStart(0);
                    pane.setSelectionEnd(0);
                } else {
                    mainWindow.getLabel().setText("Search Complete");
                }
            }

        }

    }

}
