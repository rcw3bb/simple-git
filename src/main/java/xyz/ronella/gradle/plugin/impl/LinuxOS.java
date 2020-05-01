package xyz.ronella.gradle.plugin.impl;

import xyz.ronella.gradle.plugin.IExecutable;
import xyz.ronella.gradle.plugin.IScript;

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
