package xyz.ronella.gradle.plugin.simple.git.impl;

import xyz.ronella.gradle.plugin.simple.git.IExecutable;
import xyz.ronella.gradle.plugin.simple.git.IScript;

/**
 * An implementation specific to Windows.
 *
 * @author Ron Webb
 * @since 2020-05-05
 */
public class WindowsOS implements IExecutable, IScript {

    /**
     * The constructor.
     */
    public WindowsOS() {}

    @Override
    public String getExecutable() {
        return "git.exe";
    }

    @Override
    public String getScript() {
        return "execute-git.bat";
    }
}
