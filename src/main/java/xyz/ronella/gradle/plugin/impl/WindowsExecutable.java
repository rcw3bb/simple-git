package xyz.ronella.gradle.plugin.impl;

import xyz.ronella.gradle.plugin.IExecutable;

public class WindowsExecutable implements IExecutable {
    @Override
    public String getExecutable() {
        return "git.exe";
    }
}
