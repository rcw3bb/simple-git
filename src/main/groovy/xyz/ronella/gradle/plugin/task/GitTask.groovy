package xyz.ronella.gradle.plugin.task

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import xyz.ronella.gradle.plugin.GitExecutor
import xyz.ronella.gradle.plugin.OSType
import xyz.ronella.gradle.plugin.SimpleGitPluginExtension

class GitTask extends DefaultTask {

    @Internal
    protected String[] internalArgs = []

    @Input
    String command = ''

    @Input
    String[] args = []

    public GitTask() {
        group = 'Simple Git'
        description = 'Execute git command.'
    }

    public String[] getAllArgs() {
        String[] newArgs = internalArgs + args

        return (command.length()>0 || newArgs.length > 0) ? newArgs : ['--help']
    }

    private String detectGitExec() {
        def osType = GitExecutor.OS_TYPE
        def gitExec = GitExecutor.GIT_EXE
        String cmd = null
        switch (osType) {
            case OSType.Windows:
                cmd="where"
                break;
            case OSType.Linux:
                cmd="which"
                break;
        }

        if (cmd) {
            def stdOutput = new ByteArrayOutputStream()
            def errOutput = new ByteArrayOutputStream()

            project.exec {
                executable cmd
                args gitExec
                standardOutput = stdOutput
                errorOutput = errOutput
                ignoreExitValue = true
            }

            def error = errOutput.toString()

            if (error.size()>0) {
                println("detectGitExec Error: ${error}")
            }

            def knownGit = stdOutput.toString().trim()

            if (knownGit.size()>0) {
                return knownGit
            }
        }

        return null
    }

    private GitExecutor getExecutor() {
        def knownGit = detectGitExec()
        def builder = GitExecutor.getBuilder()

        builder.addKnownGitExe(knownGit)
        if (command) {
            builder.addArg(command)
        }
        builder.addArgs(allArgs)

        return builder.build()
    }

    public String getCommand() {
        return getExecutor().command
    }

    public String getGitExe() {
        return getExecutor().gitExe
    }

    @TaskAction
    def executeCommand() {
        SimpleGitPluginExtension pluginExt = project.extensions.simple_git;

        def executor = getExecutor()
        executor.execute { ___command, ___args ->
            String[] fullCommand = [___command]
            fullCommand+= ___args.toArray()

            pluginExt.writeln("OS: ${GitExecutor.OS_TYPE}")
            pluginExt.writeln("Command to execute: ${fullCommand.join(' ')}")

            if (!pluginExt.noop) {
                project.exec {
                    commandLine fullCommand
                }
            }
            else {
                pluginExt.writeln("No-operation is activated.")
            }
        }
    }
}
