package xyz.ronella.gradle.plugin.simple.git;

import xyz.ronella.gradle.plugin.simple.git.impl.LinuxOS;
import xyz.ronella.gradle.plugin.simple.git.impl.OtherOS;
import xyz.ronella.gradle.plugin.simple.git.impl.WindowsOS;

/**
 * The template of identifying the support script to to run the git command in a particular directory.
 *
 * @author Ron Webb
 * @since 2020-05-05
 */
public interface IScript {

    /**
     * Must give the valid support script.
     *
     * @return The support script.
     */
    String getScript();

    /**
     * The factory of creating a valid IScript implementation.
     *
     * @param osType An instance of OSType
     * @return An implementation of IExecutable.
     */
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
