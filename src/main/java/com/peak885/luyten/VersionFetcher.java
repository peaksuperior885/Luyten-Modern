package com.peak885.luyten;

public final class VersionFetcher {

    private VersionFetcher() {
    }

    public static String getVersion() {
        Package pkg = Luyten.class.getPackage();
        String version = pkg.getImplementationVersion();

        return version != null ? version : "DEV";
    }

    public static String getDisplayVersion() {
        return "Luyten " + getVersion();
    }
}