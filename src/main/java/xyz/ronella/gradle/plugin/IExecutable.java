package xyz.ronella.gradle.plugin;

import xyz.ronella.gradle.plugin.impl.LinuxExecutable;
import xyz.ronella.gradle.plugin.impl.OtherExecutable;
import xyz.ronella.gradle.plugin.impl.WindowsExecutable;

public interface IExecutable {
    String getExecutable();

    public static IExecutable getInstance(OSType osType) {
        switch (osType) {
            case Windows:
                return new WindowsExecutable();
            case Linux:
                return new LinuxExecutable();
            default:
                return new OtherExecutable();
        }
    }
}
