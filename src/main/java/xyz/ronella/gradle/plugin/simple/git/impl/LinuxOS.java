package xyz.ronella.gradle.plugin.simple.git.impl;

import xyz.ronella.gradle.plugin.simple.git.IExecutable;
import xyz.ronella.gradle.plugin.simple.git.IScript;

public class LinuxOS implements IExecutable, IScript {
    @Override
    public String getExecutable() {
        return "git";
    }

    @Override
    public String getScript() {
        return null;
    }
}
