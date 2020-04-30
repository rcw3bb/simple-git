package xyz.ronella.gradle.plugin;

/**
 * The enumerator that identifies the OSType.
 *
 * @author Ron Webb
 * @since 2020-04-11
 */
public enum OSType {
    Windows,
    Linux,
    Unknown;

    public static OSType identify() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) {
            return OSType.Windows;
        }
        else if (osName.contains("nux")) {
            return OSType.Linux;
        }
        return OSType.Unknown;
    }
}
