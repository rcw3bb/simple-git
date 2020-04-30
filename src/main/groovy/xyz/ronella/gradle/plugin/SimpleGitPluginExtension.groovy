package xyz.ronella.gradle.plugin

class SimpleGitPluginExtension {

    public boolean verbose

    public boolean noop

    public void writeln(String text) {
        if (verbose || noop) {
            println(text)
        }
    }

}
