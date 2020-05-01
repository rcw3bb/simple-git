package xyz.ronella.gradle.plugin;

import xyz.ronella.gradle.plugin.impl.LinuxOS;
import xyz.ronella.gradle.plugin.impl.OtherOS;
import xyz.ronella.gradle.plugin.impl.WindowsOS;

public interface IExecutable {
    String getExecutable();

    public static IExecutable getInstance(OSType osType) {
        switch (osType) {
            case Windows:
                return new WindowsOS();
            case Linux:
                return new LinuxOS();
            default:
                return new OtherOS();
        }
    }
}