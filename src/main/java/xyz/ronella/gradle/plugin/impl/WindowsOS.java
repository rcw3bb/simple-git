package xyz.ronella.gradle.plugin.impl;

import xyz.ronella.gradle.plugin.IExecutable;
import xyz.ronella.gradle.plugin.IScript;

public class WindowsOS implements IExecutable, IScript {

    @Override
    public String getExecutable() {
        return "git.exe";
    }

    @Override
    public String getScript() {
        return "execute-git.bat";
    }
}
