package xyz.ronella.gradle.plugin.simple.git.impl;

import xyz.ronella.gradle.plugin.simple.git.IExecutable;
import xyz.ronella.gradle.plugin.simple.git.IScript;

/**
 * An implementation specific to Linux.
 *
 * @author Ron Webb
 * @since 2020-05-05
 */
public class LinuxOS implements IExecutable, IScript {

    /**
     * The constructor
     */
    public LinuxOS() {}

    @Override
    public String getExecutable() {
        return "git";
    }

    @Override
    public String getScript() {
        return "execute-git.sh";
    }
}
