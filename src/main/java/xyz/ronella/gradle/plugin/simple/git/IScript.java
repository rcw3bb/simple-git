package xyz.ronella.gradle.plugin.simple.git;

import xyz.ronella.gradle.plugin.simple.git.impl.LinuxOS;
import xyz.ronella.gradle.plugin.simple.git.impl.MacOS;
import xyz.ronella.gradle.plugin.simple.git.impl.OtherOS;
import xyz.ronella.gradle.plugin.simple.git.impl.WindowsOS;
import xyz.ronella.trivial.handy.OSType;

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
     * The builder of creating a valid IScript implementation.
     *
     * @param osType An instance of OSType
     * @return An implementation of IExecutable.
     */
    public static IScript of(OSType osType) {
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
