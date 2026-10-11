package com.peak885.luyten;

import com.peak885.luyten.chooser.FileChooser;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.TextAttribute;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * The <code>JFontChooser</code> class is a swing component for font selection.
 * This class has <code>JFileChooser</code> like APIs.
 */
public class JFontChooser extends JComponent {

    private static final long serialVersionUID = 8856126034081661L;

    public static final int OK_OPTION = 0;
    public static final int CANCEL_OPTION = 1;
    public static final int ERROR_OPTION = -1;

    private static final Font DEFAULT_SELECTED_FONT = StyleContext.getDefaultStyleContext().getFont(
            "Serif", Font.PLAIN, 12);
    private static final Font DEFAULT_FONT = StyleContext.getDefaultStyleContext().getFont("Dialog", Font.PLAIN, 10);
    private static final int[] FONT_STYLE_CODES = {Font.PLAIN, Font.BOLD, Font.ITALIC, Font.BOLD | Font.ITALIC};
    private static final String[] DEFAULT_FONT_SIZE_STRINGS = {"8", "9", "10", "11", "12", "14", "16", "18", "20",
            "22", "24", "26", "28", "36", "48", "72"};
    private static final boolean DEFAULT_ENABLE_KERNING = false;
    private static final boolean DEFAULT_ENABLE_LIGATURES = false;

    protected int dialogResultValue = ERROR_OPTION;
    protected DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();

    private String[] fontStyleNames;
    private String[] fontFamilyNames;
    private final String[] fontSizeStrings;

    private JTextField fontFamilyTextField;
    private JTextField fontStyleTextField;
    private JTextField fontSizeTextField;
    private JList<?> fontNameList;
    private JList<?> fontStyleList;
    private JList<?> fontSizeList;
    private JCheckBox kerningBox;
    private JCheckBox ligaturesBox;

    private JPanel fontNamePanel;
    private JPanel fontStylePanel;
    private JPanel fontSizePanel;
    private JPanel ligaturesPanel;
    private JPanel samplePanel;
    private JTextField sampleText;
    private JButton customFontButton;

    public JFontChooser() {
        this(DEFAULT_FONT_SIZE_STRINGS);
    }

    public JFontChooser(String[] fontSizeStrings) {
        if (fontSizeStrings == null) {
            fontSizeStrings = DEFAULT_FONT_SIZE_STRINGS;
        }
        this.fontSizeStrings = fontSizeStrings;

        setLayout(new MigLayout("insets 5, fill", "[grow]", "[grow]8[]8[]"));

        JPanel selectPanel = new JPanel(new MigLayout("insets 0, fill", "[grow][grow][70!]", "[grow]"));
        selectPanel.add(getFontFamilyPanel(), "grow");
        selectPanel.add(getFontStylePanel(), "grow");
        selectPanel.add(getFontSizePanel(), "grow");

        add(selectPanel, "grow, wrap");
        add(getSamplePanel(), "growx, wrap");
        add(getLigaturesPanel(), "growx");

        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }

    public void loadAndApplyConfig() {
        FontConfig config = FontConfig.load();

        // Register either the managed font or a legacy absolute customFontPath.
        java.nio.file.Path customFontPath = config.getCustomFontFile();
        if (customFontPath != null && java.nio.file.Files.isRegularFile(customFontPath)) {
            try {
                Font customFont = Font.createFont(Font.TRUETYPE_FONT, customFontPath.toFile());
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(customFont);
                fontFamilyNames = null;

                @SuppressWarnings("unchecked")
                JList<Object> familyList = (JList<Object>) getFontFamilyList();
                familyList.setListData(getFontFamilies());
            } catch (Exception e) {
                System.err.println("Could not reload custom font on startup: " + e.getMessage());
            }
        }

        // 2. Apply properties safely using SwingUtilities to ensure UI components are ready
        SwingUtilities.invokeLater(() -> {
            setSelectedFontFamily(config.getFontFamily());
            setSelectedFontStyle(config.getFontStyle());
            setSelectedFontSize(config.getFontSize());
            getKerningBox().setSelected(config.isKerning());
            getLigaturesBox().setSelected(config.isLigatures());
            updateSampleFont();
        });
    }

    public JTextField getFontFamilyTextField() {
        if (fontFamilyTextField == null) {
            fontFamilyTextField = new JTextField();
            fontFamilyTextField.addFocusListener(new TextFieldFocusHandlerForTextSelection(fontFamilyTextField));
            fontFamilyTextField.addKeyListener(new TextFieldKeyHandlerForListSelectionUpDown(getFontFamilyList()));
            fontFamilyTextField.getDocument()
                    .addDocumentListener(new ListSearchTextFieldDocumentHandler(getFontFamilyList()));
            fontFamilyTextField.setFont(DEFAULT_FONT);
        }
        return fontFamilyTextField;
    }

    public JTextField getFontStyleTextField() {
        if (fontStyleTextField == null) {
            fontStyleTextField = new JTextField();
            fontStyleTextField.addFocusListener(new TextFieldFocusHandlerForTextSelection(fontStyleTextField));
            fontStyleTextField.addKeyListener(new TextFieldKeyHandlerForListSelectionUpDown(getFontStyleList()));
            fontStyleTextField.getDocument()
                    .addDocumentListener(new ListSearchTextFieldDocumentHandler(getFontStyleList()));
            fontStyleTextField.setFont(DEFAULT_FONT);
        }
        return fontStyleTextField;
    }

    public JTextField getFontSizeTextField() {
        if (fontSizeTextField == null) {
            fontSizeTextField = new JTextField();
            fontSizeTextField.addFocusListener(new TextFieldFocusHandlerForTextSelection(fontSizeTextField));
            fontSizeTextField.addKeyListener(new TextFieldKeyHandlerForListSelectionUpDown(getFontSizeList()));
            fontSizeTextField.getDocument()
                    .addDocumentListener(new ListSearchTextFieldDocumentHandler(getFontSizeList()));
            fontSizeTextField.setFont(DEFAULT_FONT);
        }
        return fontSizeTextField;
    }

    public JList<?> getFontFamilyList() {
        if (fontNameList == null) {
            fontNameList = new JList<Object>(getFontFamilies());
            fontNameList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            fontNameList.addListSelectionListener(new ListSelectionHandler(getFontFamilyTextField()));
            fontNameList.setSelectedIndex(0);

            fontNameList.setCellRenderer(new DefaultListCellRenderer() {
                private static final long serialVersionUID = -6753380853569310954L;

                public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                              boolean isSelected, boolean cellHasFocus) {
                    JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent(list, value, index,
                            isSelected, cellHasFocus);

                    if (value instanceof String) {
                        renderer.setText((String) value);
                        try {
                            renderer.setFont(StyleContext.getDefaultStyleContext().getFont(
                                    (String) value, DEFAULT_FONT.getStyle(), DEFAULT_FONT.getSize() + 2));
                        } catch (Exception e) {
                            renderer.setFont(DEFAULT_FONT);
                        }
                    } else {
                        renderer.setText("");
                    }
                    return renderer;
                }
            });
            fontNameList.setFocusable(false);
        }
        return fontNameList;
    }

    public JList<?> getFontStyleList() {
        if (fontStyleList == null) {
            fontStyleList = new JList<Object>(getFontStyleNames());
            fontStyleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            fontStyleList.addListSelectionListener(new ListSelectionHandler(getFontStyleTextField()));
            fontStyleList.setSelectedIndex(0);
            fontStyleList.setFocusable(false);
            fontStyleList.setCellRenderer(new DefaultListCellRenderer() {
                private static final long serialVersionUID = -3904668242514776943L;

                public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                              boolean isSelected, boolean cellHasFocus) {
                    JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent(list, value, index,
                            isSelected, cellHasFocus);

                    if (value instanceof String) {
                        renderer.setText((String) value);
                        renderer.setFont(DEFAULT_FONT.deriveFont(FONT_STYLE_CODES[index], DEFAULT_FONT.getSize() + 2));
                    } else {
                        renderer.setText("");
                    }
                    return renderer;
                }
            });
        }
        return fontStyleList;
    }

    public JList<?> getFontSizeList() {
        if (fontSizeList == null) {
            fontSizeList = new JList<Object>(this.fontSizeStrings);
            fontSizeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            fontSizeList.addListSelectionListener(new ListSelectionHandler(getFontSizeTextField()));
            fontSizeList.setSelectedIndex(0);
            fontSizeList.setFont(DEFAULT_FONT);
            fontSizeList.setFocusable(false);
        }
        return fontSizeList;
    }

    public JCheckBox getKerningBox() {
        if (kerningBox == null) {
            kerningBox = new JCheckBox("Enable Kerning", DEFAULT_ENABLE_KERNING);
            kerningBox.setMnemonic(KeyEvent.VK_K);
            kerningBox.setDisplayedMnemonicIndex(7);
            kerningBox.addChangeListener(l -> updateSampleFont());
        }
        return kerningBox;
    }

    public JCheckBox getLigaturesBox() {
        if (ligaturesBox == null) {
            ligaturesBox = new JCheckBox("Enable Ligatures", DEFAULT_ENABLE_LIGATURES);
            ligaturesBox.setMnemonic(KeyEvent.VK_L);
            ligaturesBox.setDisplayedMnemonicIndex(7);
            ligaturesBox.addChangeListener(l -> updateSampleFont());
        }
        return ligaturesBox;
    }

    public JButton getCustomFontButton() {
        if (customFontButton == null) {
            customFontButton = new JButton("Load Custom Font...");
            customFontButton.setFont(DEFAULT_FONT);
            customFontButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            customFontButton.addActionListener(e -> promptAndRegisterCustomFont());
        }
        return customFontButton;
    }

    private void promptAndRegisterCustomFont() {
        FileChooser.ExtensionFilter fontFilter = new FileChooser.ExtensionFilter("Font Files", "ttf", "otf");
        File selectedFile = FileChooser.showOpenDialog(this, "Select Custom Font File", fontFilter);
        if (selectedFile != null) {
            try {
                Font customFont = Font.createFont(Font.TRUETYPE_FONT, selectedFile);
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(customFont);

                // Keep the selected font available after its original file is moved or deleted.
                FontConfig config = FontConfig.load();
                config.copyCustomFont(selectedFile.toPath());
                config.save();

                // Reset cache to reload families including the newly registered one
                fontFamilyNames = null;

                // Update the JList model dynamically
                JList familyList = getFontFamilyList();
                familyList.setListData(getFontFamilies());

                // Select the newly loaded font family
                setSelectedFontFamily(customFont.getFamily());
                updateSampleFont();
            } catch (FontFormatException | IOException ex) {
                JOptionPane.showMessageDialog(this,
                        "Could not load custom font:\n" + ex.getMessage(),
                        "Error Loading Font", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public String getSelectedFontFamily() {
        return (String) getFontFamilyList().getSelectedValue();
    }

    public int getSelectedFontStyle() {
        int index = getFontStyleList().getSelectedIndex();
        if (index < 0 || index >= FONT_STYLE_CODES.length) return Font.PLAIN;
        return FONT_STYLE_CODES[index];
    }

    public int getSelectedFontSize() {
        int fontSize;
        String fontSizeString = getFontSizeTextField().getText();
        while (true) {
            try {
                fontSize = Integer.parseInt(fontSizeString);
                break;
            } catch (NumberFormatException e) {
                fontSizeString = (String) getFontSizeList().getSelectedValue();
                if (fontSizeString == null) fontSizeString = "12";
                getFontSizeTextField().setText(fontSizeString);
            }
        }
        return fontSize;
    }

    public void addSelectedFontAttrs(Map<TextAttribute, Object> attrs) {
        if (getKerningBox().isSelected())
            attrs.put(TextAttribute.KERNING, TextAttribute.KERNING_ON);
        if (getLigaturesBox().isSelected())
            attrs.put(TextAttribute.LIGATURES, TextAttribute.LIGATURES_ON);
    }

    public Font getSelectedFont() {
        Map<TextAttribute, Object> attrs = new HashMap<>();
        String family = getSelectedFontFamily();
        if (family != null) {
            attrs.put(TextAttribute.FAMILY, family);
        }
        attrs.put(TextAttribute.SIZE, (float) getSelectedFontSize());
        addStyleToAttrs(getSelectedFontStyle(), attrs);
        addSelectedFontAttrs(attrs);
        return Font.getFont(attrs);
    }

    public void setSelectedFontFamily(String name) {
        String[] names = getFontFamilies();
        for (int i = 0; i < names.length; i++) {
            if (names[i].equalsIgnoreCase(name)) {
                getFontFamilyList().setSelectedIndex(i);
                getFontFamilyList().ensureIndexIsVisible(i);
                break;
            }
        }
        updateSampleFont();
    }

    public void setSelectedFontStyle(int style) {
        for (int i = 0; i < FONT_STYLE_CODES.length; i++) {
            if (FONT_STYLE_CODES[i] == style) {
                getFontStyleList().setSelectedIndex(i);
                break;
            }
        }
        updateSampleFont();
    }

    public void setSelectedFontSize(int size) {
        String sizeString = String.valueOf(size);
        for (int i = 0; i < this.fontSizeStrings.length; i++) {
            if (this.fontSizeStrings[i].equals(sizeString)) {
                getFontSizeList().setSelectedIndex(i);
                break;
            }
        }
        getFontSizeTextField().setText(sizeString);
        updateSampleFont();
    }

    @SuppressWarnings("unchecked")
    public void setSelectedFontAttrs(Map<TextAttribute, Object> attrs) {
        if (attrs.containsKey(TextAttribute.KERNING))
            getKerningBox().setSelected(attrs.getOrDefault(TextAttribute.KERNING, 0) == TextAttribute.KERNING_ON);
        if (attrs.containsKey(TextAttribute.LIGATURES))
            getLigaturesBox().setSelected(attrs.getOrDefault(TextAttribute.LIGATURES, 0) == TextAttribute.LIGATURES_ON);
        updateSampleFont();
    }

    @SuppressWarnings("unchecked")
    public void setSelectedFont(Font font) {
        if (font != null) {
            setSelectedFontFamily(font.getFamily());
            setSelectedFontStyle(font.getStyle());
            setSelectedFontSize(font.getSize());
            setSelectedFontAttrs((Map<TextAttribute, Object>) font.getAttributes());
        }
    }

    public String getVersionString() {
        return "Version";
    }

    public int showDialog(Component parent) {
        dialogResultValue = ERROR_OPTION;
        JDialog dialog = createDialog(parent);
        dialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dialogResultValue = CANCEL_OPTION;
            }
        });

        dialog.setVisible(true);
        dialog.dispose();

        return dialogResultValue;
    }

    protected class ListSelectionHandler implements ListSelectionListener {
        private final JTextComponent textComponent;

        ListSelectionHandler(JTextComponent textComponent) {
            this.textComponent = textComponent;
        }

        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                JList<?> list = (JList<?>) e.getSource();
                String selectedValue = (String) list.getSelectedValue();
                if (selectedValue != null) {
                    String oldValue = textComponent.getText();
                    textComponent.setText(selectedValue);
                    if (!oldValue.equalsIgnoreCase(selectedValue)) {
                        textComponent.selectAll();
                        textComponent.requestFocus();
                    }
                }
                updateSampleFont();
            }
        }
    }

    protected class TextFieldFocusHandlerForTextSelection extends FocusAdapter {
        private final JTextComponent textComponent;

        public TextFieldFocusHandlerForTextSelection(JTextComponent textComponent) {
            this.textComponent = textComponent;
        }

        public void focusGained(FocusEvent e) {
            textComponent.selectAll();
        }

        public void focusLost(FocusEvent e) {
            textComponent.select(0, 0);
            updateSampleFont();
        }
    }

    protected static class TextFieldKeyHandlerForListSelectionUpDown extends KeyAdapter {
        private final JList<?> targetList;

        public TextFieldKeyHandlerForListSelectionUpDown(JList<?> list) {
            this.targetList = list;
        }

        public void keyPressed(KeyEvent e) {
            int i;
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP:
                    i = targetList.getSelectedIndex() - 1;
                    if (i < 0) i = 0;
                    targetList.setSelectedIndex(i);
                    break;
                case KeyEvent.VK_DOWN:
                    int listSize = targetList.getModel().getSize();
                    i = targetList.getSelectedIndex() + 1;
                    if (i >= listSize) i = listSize - 1;
                    targetList.setSelectedIndex(i);
                    break;
                default:
                    break;
            }
        }
    }

    protected static class ListSearchTextFieldDocumentHandler implements DocumentListener {
        JList<?> targetList;

        public ListSearchTextFieldDocumentHandler(JList<?> targetList) {
            this.targetList = targetList;
        }

        public void insertUpdate(DocumentEvent e) { update(e); }
        public void removeUpdate(DocumentEvent e) { update(e); }
        public void changedUpdate(DocumentEvent e) { update(e); }

        private void update(DocumentEvent event) {
            String newValue = "";
            try {
                Document doc = event.getDocument();
                newValue = doc.getText(0, doc.getLength());
            } catch (BadLocationException ignored) {}

            if (!newValue.isEmpty()) {
                int index = targetList.getNextMatch(newValue, 0, Position.Bias.Forward);
                if (index < 0) index = 0;
                targetList.ensureIndexIsVisible(index);

                Object element = targetList.getModel().getElementAt(index);
                if (element != null) {
                    String matchedName = element.toString();
                    if (newValue.equalsIgnoreCase(matchedName)) {
                        if (index != targetList.getSelectedIndex()) {
                            SwingUtilities.invokeLater(new ListSelector(index));
                        }
                    }
                }
            }
        }

        public class ListSelector implements Runnable {
            private final int index;

            public ListSelector(int index) {
                this.index = index;
            }

            public void run() {
                targetList.setSelectedIndex(this.index);
            }
        }
    }

    protected class DialogOKAction extends AbstractAction {
        private static final long serialVersionUID = 1618273732543947323L;
        protected static final String ACTION_NAME = "OK";
        private final JDialog dialog;

        protected DialogOKAction(JDialog dialog) {
            this.dialog = dialog;
            putValue(Action.DEFAULT, ACTION_NAME);
            putValue(Action.ACTION_COMMAND_KEY, ACTION_NAME);
            putValue(Action.NAME, ACTION_NAME);
        }

        public void actionPerformed(ActionEvent e) {
            dialogResultValue = OK_OPTION;

            // Load the existing config so we don't wipe out customFontPath
            FontConfig config = FontConfig.load();
            config.setFontFamily(getSelectedFontFamily());
            config.setFontStyle(getSelectedFontStyle());
            config.setFontSize(getSelectedFontSize());
            config.setKerning(getKerningBox().isSelected());
            config.setLigatures(getLigaturesBox().isSelected());
            // customFontPath is already preserved because we loaded the existing config instance!
            config.save();

            dialog.setVisible(false);
        }
    }

    protected class DialogCancelAction extends AbstractAction {
        private static final long serialVersionUID = -4941763616565382601L;
        protected static final String ACTION_NAME = "Cancel";
        private final JDialog dialog;

        protected DialogCancelAction(JDialog dialog) {
            this.dialog = dialog;
            putValue(Action.DEFAULT, ACTION_NAME);
            putValue(Action.ACTION_COMMAND_KEY, ACTION_NAME);
            putValue(Action.NAME, ACTION_NAME);
        }

        public void actionPerformed(ActionEvent e) {
            dialogResultValue = CANCEL_OPTION;
            dialog.setVisible(false);
        }
    }

    protected JDialog createDialog(Component parent) {
        Window owner = parent instanceof Window ? (Window) parent : SwingUtilities.getWindowAncestor(parent);
        JDialog dialog = new JDialog(owner, "Select Font", Dialog.ModalityType.APPLICATION_MODAL);

        dialog.setLayout(new MigLayout("insets 10, fill", "[grow][]", "[grow]"));
        dialog.getContentPane().add(this, "grow");

        Action okAction = new DialogOKAction(dialog);
        Action cancelAction = new DialogCancelAction(dialog);

        JButton okButton = new JButton(okAction);
        okButton.setFont(DEFAULT_FONT);
        JButton cancelButton = new JButton(cancelAction);
        cancelButton.setFont(DEFAULT_FONT);

        JPanel buttonsPanel = new JPanel(new MigLayout("insets 0, wrap 1, fillx", "[100!]", "[]4[]"));
        buttonsPanel.add(okButton, "growx");
        buttonsPanel.add(cancelButton, "growx");

        JPanel dialogEastPanel = new JPanel(new MigLayout("insets 25 0 10 10", "[]", "[]"));
        dialogEastPanel.add(buttonsPanel, "top");

        dialog.getContentPane().add(dialogEastPanel, "top");
        dialog.pack();
        dialog.setLocationRelativeTo(owner);
        return dialog;
    }

    protected void updateSampleFont() {
        Font font = getSelectedFont();
        getSampleTextField().setFont(font);
    }

    protected JPanel getFontFamilyPanel() {
        if (fontNamePanel == null) {
            fontNamePanel = new JPanel(new MigLayout("insets 5, fill", "[grow]", "[]2[grow]2[]"));
            fontNamePanel.setPreferredSize(new Dimension(180, 150));

            JLabel label = new JLabel("Font Name");
            label.setDisplayedMnemonic('F');
            label.setLabelFor(getFontFamilyTextField());

            JScrollPane scrollPane = new JScrollPane(getFontFamilyList());
            scrollPane.getVerticalScrollBar().setFocusable(false);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

            fontNamePanel.add(label, "growx, wrap");
            fontNamePanel.add(getFontFamilyTextField(), "growx, wrap");
            fontNamePanel.add(scrollPane, "grow, wrap");
            fontNamePanel.add(getCustomFontButton(), "growx");
        }
        return fontNamePanel;
    }

    protected JPanel getFontStylePanel() {
        if (fontStylePanel == null) {
            fontStylePanel = new JPanel(new MigLayout("insets 5, fill", "[grow]", "[]2[grow]"));
            fontStylePanel.setPreferredSize(new Dimension(140, 130));

            JLabel label = new JLabel("Font Style");
            label.setDisplayedMnemonic('Y');
            label.setLabelFor(getFontStyleTextField());

            JScrollPane scrollPane = new JScrollPane(getFontStyleList());
            scrollPane.getVerticalScrollBar().setFocusable(false);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

            fontStylePanel.add(label, "growx, wrap");
            fontStylePanel.add(getFontStyleTextField(), "growx, wrap");
            fontStylePanel.add(scrollPane, "grow");
        }
        return fontStylePanel;
    }

    protected JPanel getFontSizePanel() {
        if (fontSizePanel == null) {
            fontSizePanel = new JPanel(new MigLayout("insets 5, fill", "[grow]", "[]2[grow]"));
            fontSizePanel.setPreferredSize(new Dimension(70, 130));

            JLabel label = new JLabel("Font Size");
            label.setDisplayedMnemonic('S');
            label.setLabelFor(getFontSizeTextField());

            JScrollPane scrollPane = new JScrollPane(getFontSizeList());
            scrollPane.getVerticalScrollBar().setFocusable(false);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

            fontSizePanel.add(label, "growx, wrap");
            fontSizePanel.add(getFontSizeTextField(), "growx, wrap");
            fontSizePanel.add(scrollPane, "grow");
        }
        return fontSizePanel;
    }

    protected JPanel getLigaturesPanel() {
        if (ligaturesPanel == null) {
            ligaturesPanel = new JPanel(new MigLayout("insets 5, fillx", "[][grow]", "[]"));
            ligaturesPanel.add(getKerningBox());
            ligaturesPanel.add(getLigaturesBox(), "pushx, alignx right");
        }
        return ligaturesPanel;
    }

    protected JPanel getSamplePanel() {
        if (samplePanel == null) {
            Border titledBorder = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Sample");
            Border empty = BorderFactory.createEmptyBorder(5, 10, 10, 10);
            Border border = BorderFactory.createCompoundBorder(titledBorder, empty);

            samplePanel = new JPanel(new MigLayout("insets 0, fill", "[grow]", "[grow]"));
            samplePanel.setBorder(border);
            samplePanel.add(getSampleTextField(), "grow");
        }
        return samplePanel;
    }

    protected JTextField getSampleTextField() {
        if (sampleText == null) {
            Border lowered = BorderFactory.createLoweredBevelBorder();
            sampleText = new JTextField("AaBbYyZz <=");
            sampleText.setHorizontalAlignment(JTextField.CENTER);
            sampleText.setBorder(lowered);
            sampleText.setPreferredSize(new Dimension(300, 80));
        }
        return sampleText;
    }

    protected String[] getFontFamilies() {
        if (fontFamilyNames == null) {
            GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
            fontFamilyNames = env.getAvailableFontFamilyNames();
        }
        return fontFamilyNames;
    }

    protected String[] getFontStyleNames() {
        if (fontStyleNames == null) {
            fontStyleNames = new String[]{"Plain", "Bold", "Italic", "BoldItalic"};
        }
        return fontStyleNames;
    }

    public static void addStyleToAttrs(int style, Map<TextAttribute, Object> attrs) {
        attrs.put(TextAttribute.WEIGHT, (style & Font.BOLD) != 0 ?
                TextAttribute.WEIGHT_BOLD : TextAttribute.WEIGHT_REGULAR);
        attrs.put(TextAttribute.POSTURE, (style & Font.ITALIC) != 0 ?
                TextAttribute.POSTURE_OBLIQUE : TextAttribute.POSTURE_REGULAR);
    }

    public static int getStyleFromAttrs(Map<TextAttribute, Object> attrs) {
        int style = Font.PLAIN;
        Float currentWeight = (Float) attrs.getOrDefault(TextAttribute.WEIGHT, TextAttribute.WEIGHT_REGULAR);
        if (currentWeight != null && !currentWeight.equals(TextAttribute.WEIGHT_REGULAR))
            style |= Font.BOLD;
        Float currentPosture = (Float) attrs.getOrDefault(TextAttribute.POSTURE, TextAttribute.POSTURE_REGULAR);
        if (currentPosture != null && !currentPosture.equals(TextAttribute.POSTURE_REGULAR))
            style |= Font.ITALIC;
        return style;
    }
}