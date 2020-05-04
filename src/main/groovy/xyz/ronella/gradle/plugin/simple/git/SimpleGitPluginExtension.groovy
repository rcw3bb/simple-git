package xyz.ronella.gradle.plugin.simple.git

class SimpleGitPluginExtension {

    public boolean verbose

    public boolean noop

    public File directory

    public void writeln(String text) {
        if (verbose || noop) {
            println(text)
        }
    }

}
