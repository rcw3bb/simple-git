package xyz.ronella.gradle.plugin.simple.git;

import xyz.ronella.gradle.plugin.simple.git.impl.LinuxOS;
import xyz.ronella.gradle.plugin.simple.git.impl.OtherOS;
import xyz.ronella.gradle.plugin.simple.git.impl.WindowsOS;

public interface IScript {

    String getScript();

    public static IScript getInstance(OSType osType) {
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
