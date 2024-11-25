package xyz.ronella.gradle.plugin.simple.git;

import xyz.ronella.gradle.plugin.simple.git.impl.LinuxOS;
import xyz.ronella.gradle.plugin.simple.git.impl.OtherOS;
import xyz.ronella.gradle.plugin.simple.git.impl.WindowsOS;
import xyz.ronella.gradle.plugin.simple.git.impl.MacOS;
import xyz.ronella.trivial.handy.OSType;

/**
 * The template of identifying the actual git executable.
 *
 * @author Ron Webb
 * @since 2020-05-05
 */
public interface IExecutable {

    /**
     * Must return the valid git executable.
     *
     * @return The git executable.
     */
    String getExecutable();

    /**
     * The builder of creating a valid IExecutable implementation.
     *
     * @param osType An instance of OSType
     * @return An implementation of IExecutable.
     */
    static IExecutable of(OSType osType) {
        switch (osType) {
            case WINDOWS:
                return new WindowsOS();
            case LINUX:
                return new LinuxOS();
            case MAC:
                return new MacOS();
            default:
                return new OtherOS();
        }
    }
}
