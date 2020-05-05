package xyz.ronella.gradle.plugin.simple.git;

import xyz.ronella.gradle.plugin.simple.git.impl.LinuxOS;
import xyz.ronella.gradle.plugin.simple.git.impl.OtherOS;
import xyz.ronella.gradle.plugin.simple.git.impl.WindowsOS;

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
     * The factory of creating a valid IExecutable implementation.
     *
     * @param osType An instance of OSType
     * @return An implementation of IExecutable.
     */
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
