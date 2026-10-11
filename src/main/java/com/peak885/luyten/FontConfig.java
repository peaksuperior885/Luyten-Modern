package com.peak885.luyten;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.JsonPrimitive;

import java.awt.Font;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

public final class FontConfig {

    private static final Jankson JANKSON = Jankson.builder().build();

    private static final Path CONFIG_DIR =
            Paths.get(System.getProperty("user.home"), ".luyten");
    private static final Path CONFIG_FILE = CONFIG_DIR.resolve("config.json5");
    private static final Path FONTS_DIR = CONFIG_DIR.resolve("fonts");

    private String fontFamily = Font.SERIF;
    private int fontStyle = Font.PLAIN;
    private int fontSize = 12;
    private boolean kerning = false;
    private boolean ligatures = false;
    // Retained to read older configurations that stored an absolute path.
    private String customFontPath = "";
    private String managedFontFileName = "";

    private static FontConfig instance;

    public static synchronized FontConfig load() {
        if (instance != null) {
            return instance;
        }

        instance = new FontConfig();
        if (!Files.isRegularFile(CONFIG_FILE)) {
            instance.save();
            return instance;
        }

        try {
            JsonObject json = JANKSON.load(CONFIG_FILE.toFile());
            instance.fontFamily = getString(json, "fontFamily", instance.fontFamily);
            instance.fontStyle = getInt(json, "fontStyle", instance.fontStyle);
            instance.fontSize = getInt(json, "fontSize", instance.fontSize);
            instance.kerning = getBoolean(json, "kerning", instance.kerning);
            instance.ligatures = getBoolean(json, "ligatures", instance.ligatures);
            instance.customFontPath = getString(json, "customFontPath", "");
            instance.managedFontFileName = getString(json, "managedFontFileName", "");

            if (instance.fontSize < 1 || instance.fontSize > 512) {
                instance.fontSize = 12;
            }
            if (instance.fontStyle < Font.PLAIN
                    || instance.fontStyle > (Font.BOLD | Font.ITALIC)) {
                instance.fontStyle = Font.PLAIN;
            }
            if (!isSafeManagedFileName(instance.managedFontFileName)) {
                instance.managedFontFileName = "";
            }
        } catch (Exception e) {
            System.err.println("Could not load Luyten font config: " + e.getMessage());
        }
        return instance;
    }

    public void save() {
        try {
            Files.createDirectories(CONFIG_DIR);
            JsonObject json = new JsonObject();
            json.put("fontFamily", new JsonPrimitive(fontFamily));
            json.put("fontStyle", new JsonPrimitive(fontStyle));
            json.put("fontSize", new JsonPrimitive(fontSize));
            json.put("kerning", new JsonPrimitive(kerning));
            json.put("ligatures", new JsonPrimitive(ligatures));
            json.put("customFontPath", new JsonPrimitive(customFontPath));
            json.put("managedFontFileName", new JsonPrimitive(managedFontFileName));
            String contents = json.toJson(true, true);
            Files.write(CONFIG_FILE,
                    (contents + System.lineSeparator()).getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE);
        } catch (IOException e) {
            System.err.println("Could not save Luyten font config: " + e.getMessage());
        }
    }

    /** Copies a selected font into the user's Luyten font directory and records its filename. */
    public void copyCustomFont(Path source) throws IOException {
        if (source == null || !Files.isRegularFile(source)) {
            throw new IOException("Selected font file does not exist.");
        }
        String fileName = source.getFileName().toString();
        if (!isSafeManagedFileName(fileName)) {
            throw new IOException("Selected font file has an invalid filename.");
        }
        Files.createDirectories(FONTS_DIR);
        Path destination = FONTS_DIR.resolve(fileName).normalize();
        if (!destination.getParent().equals(FONTS_DIR.normalize())) {
            throw new IOException("Selected font path is invalid.");
        }
        Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
        managedFontFileName = fileName;
        customFontPath = "";
    }

    /** Returns the managed font when configured, otherwise the legacy absolute path. */
    public Path getCustomFontFile() {
        if (isSafeManagedFileName(managedFontFileName)) {
            return FONTS_DIR.resolve(managedFontFileName);
        }
        if (customFontPath != null && !customFontPath.trim().isEmpty()) {
            try {
                return Paths.get(customFontPath);
            } catch (RuntimeException ignored) {
                return null;
            }
        }
        return null;
    }

    private static boolean isSafeManagedFileName(String fileName) {
        return fileName != null && !fileName.trim().isEmpty()
                && !".".equals(fileName) && !"..".equals(fileName)
                && fileName.indexOf('/') < 0 && fileName.indexOf('\\') < 0
                && !Paths.get(fileName).isAbsolute();
    }

    private static String getString(JsonObject json, String key, String fallback) {
        try {
            Object val = json.get(key);
            return val instanceof JsonPrimitive ? ((JsonPrimitive) val).asString() : fallback;
        } catch (Exception e) {
            return fallback;
        }
    }

    private static int getInt(JsonObject json, String key, int fallback) {
        try {
            Object val = json.get(key);
            return val instanceof JsonPrimitive ? ((JsonPrimitive) val).asInt(10) : fallback;
        } catch (Exception e) {
            return fallback;
        }
    }

    private static boolean getBoolean(JsonObject json, String key, boolean fallback) {
        try {
            Object val = json.get(key);
            return val instanceof JsonPrimitive ? ((JsonPrimitive) val).asBoolean(fallback) : fallback;
        } catch (Exception e) {
            return fallback;
        }
    }

    public String getFontFamily() { return fontFamily; }
    public void setFontFamily(String fontFamily) {
        if (fontFamily != null && !fontFamily.trim().isEmpty()) this.fontFamily = fontFamily;
    }
    public int getFontStyle() { return fontStyle; }
    public void setFontStyle(int fontStyle) { this.fontStyle = fontStyle; }
    public int getFontSize() { return fontSize; }
    public void setFontSize(int fontSize) { this.fontSize = fontSize; }
    public boolean isKerning() { return kerning; }
    public void setKerning(boolean kerning) { this.kerning = kerning; }
    public boolean isLigatures() { return ligatures; }
    public void setLigatures(boolean ligatures) { this.ligatures = ligatures; }
    public String getCustomFontPath() { return customFontPath; }
    public void setCustomFontPath(String customFontPath) {
        this.customFontPath = customFontPath == null ? "" : customFontPath;
        this.managedFontFileName = "";
    }
    public String getManagedFontFileName() { return managedFontFileName; }
}