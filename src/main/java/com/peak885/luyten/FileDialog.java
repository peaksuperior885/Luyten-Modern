package com.peak885.luyten;

import com.peak885.luyten.chooser.FileChooser;
import com.peak885.luyten.chooser.FileChooser.ExtensionFilter;

import java.awt.*;
import java.io.File;

/**
 * File choosers for Open and Save using the custom Luyten FileChooser.
 */
public class FileDialog {

    private final DirPreferences dirPreferences;
    private final Component parent;

    public FileDialog(Component parent) {
        this.parent = parent;
        ConfigSaver configSaver = ConfigSaver.getLoadedInstance();
        LuytenPreferences luytenPrefs = configSaver.getLuytenPreferences();
        dirPreferences = new DirPreferences(luytenPrefs);
    }

    public File doOpenDialog() {
        File initialDir = dirPreferences.getOpenDialogDir();
        File selected = FileChooser.showOpenDialog(
                parent,
                "Open",
                initialDir,
                new ExtensionFilter("Java Archives", "jar", "zip"),
                new ExtensionFilter("Java Class Files", "class"),
                new ExtensionFilter("Java Archives & Classes", "jar", "zip", "class")
        );
        if (selected != null) {
            dirPreferences.saveOpenDialogDir(selected.getParentFile());
        }
        return selected;
    }

    public File doSaveDialog(String recommendedFileName) {
        File initialDir = dirPreferences.getSaveDialogDir();
        File selected = FileChooser.showSaveDialog(
                parent,
                "Save As",
                initialDir,
                recommendedFileName,
                new ExtensionFilter("Java Source", "java"),
                new ExtensionFilter("Text Files", "txt")
        );
        if (selected != null) {
            dirPreferences.saveSaveDialogDir(selected.getParentFile());
        }
        return selected;
    }

    public File doSaveAllDialog(String recommendedFileName) {
        File initialDir = dirPreferences.getSaveDialogDir();
        File selected = FileChooser.showSaveDialog(
                parent,
                "Save All",
                initialDir,
                recommendedFileName,
                new ExtensionFilter("Java Archives", "jar", "zip")
        );
        if (selected != null) {
            dirPreferences.saveSaveDialogDir(selected.getParentFile());
        }
        return selected;
    }

    /** Kept for API compatibility; no longer pre-inits Swing dialogs. */
    public synchronized void initOpenDialog() {
    }

    public synchronized void initSaveDialog() {
    }

    public synchronized void initSaveAllDialog() {
    }
}
