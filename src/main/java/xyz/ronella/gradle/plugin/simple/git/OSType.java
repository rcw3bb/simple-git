package xyz.ronella.gradle.plugin.simple.git;

import java.util.Locale;

/**
 * The enumerator that identifies the OSType.
 *
 * @author Ron Webb
 * @since 2020-05-05
 */
public enum OSType {
    Windows,
    Linux,
    Mac,
    Unknown;

    public static OSType identify() {
        String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        if (osName.contains("mac") || osName.contains("darwin") || osName.contains("osx")) {
            return OSType.Mac;
        }
        else if (osName.contains("win")) {
            return OSType.Windows;
        }
        else if (osName.contains("nux") || osName.contains("nix") || osName.contains("aix")) {
            return OSType.Linux;
        }
        return OSType.Unknown;
    }
}
