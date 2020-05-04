package xyz.ronella.gradle.plugin.simple.git.impl;

import xyz.ronella.gradle.plugin.simple.git.IExecutable;
import xyz.ronella.gradle.plugin.simple.git.IScript;

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
