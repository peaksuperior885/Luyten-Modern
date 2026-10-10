package com.peak885.luyten;

import java.io.File;

class DirPreferences {

    private final LuytenPreferences luytenPrefs;

    public DirPreferences(LuytenPreferences luytenPrefs) {
        this.luytenPrefs = luytenPrefs;
    }

    File getOpenDialogDir() {
        try {
            String currentDirStr = luytenPrefs.getFileOpenCurrentDirectory();
            if (currentDirStr != null && !currentDirStr.trim().isEmpty()) {
                File currentDir = new File(currentDirStr);
                if (currentDir.exists() && currentDir.isDirectory()) {
                    return currentDir;
                }
            }
        } catch (Exception e) {
            Luyten.showExceptionDialog("Exception!", e);
        }
        return null;
    }

    void saveOpenDialogDir(File dir) {
        try {
            if (dir != null && dir.exists() && dir.isDirectory()) {
                luytenPrefs.setFileOpenCurrentDirectory(dir.getAbsolutePath());
            }
        } catch (Exception e) {
            Luyten.showExceptionDialog("Exception!", e);
        }
    }

    File getSaveDialogDir() {
        try {
            String currentDirStr = luytenPrefs.getFileSaveCurrentDirectory();
            if (currentDirStr != null && !currentDirStr.trim().isEmpty()) {
                File currentDir = new File(currentDirStr);
                if (currentDir.exists() && currentDir.isDirectory()) {
                    return currentDir;
                }
            }
        } catch (Exception e) {
            Luyten.showExceptionDialog("Exception!", e);
        }
        return null;
    }

    void saveSaveDialogDir(File dir) {
        try {
            if (dir != null && dir.exists() && dir.isDirectory()) {
                luytenPrefs.setFileSaveCurrentDirectory(dir.getAbsolutePath());
            }
        } catch (Exception e) {
            Luyten.showExceptionDialog("Exception!", e);
        }
    }
}
