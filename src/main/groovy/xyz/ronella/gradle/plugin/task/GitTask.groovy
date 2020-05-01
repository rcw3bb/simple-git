package xyz.ronella.gradle.plugin.task

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import xyz.ronella.gradle.plugin.GitExecutor
import xyz.ronella.gradle.plugin.OSType
import xyz.ronella.gradle.plugin.SimpleGitPluginExtension

import java.nio.file.Path

class GitTask extends DefaultTask {

    @Optional @Input
    protected boolean pushDirectory = true

    @Optional @Input
    String[] options = []

    @Internal
    protected String[] internalArgs = []

    private File directory

    @Input
    String command = ''

    @Input
    String[] args = []

    public GitTask() {
        group = 'Simple Git'
        description = 'Execute git command.'
        directory = project.projectDir
    }

    @Optional @Input
    File getDirectory() {
        return directory
    }

    void setDirectory(File directory) {
        this.directory = directory
    }

    public def initFields() {
        SimpleGitPluginExtension pluginExt = project.extensions.simple_git;

        if (project.hasProperty('directory')) {
            directory = new File((project.directory as String).trim())
            pluginExt.writeln("Found directory: ${directory}")
        }
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
        builder.addOpts(options)

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

        initFields()

        def executor = getExecutor()
        executor.execute { context ->
            if (context.gitExe!=null) {
                String[] fullCommand = [context.command]
                Path scriptFile = context.script
                pluginExt.writeln("Script: " + scriptFile.toString())

                if (pushDirectory && directory && scriptFile) {
/*                    String[] newCommand = ["pushd \"${directory}\" &&"]
                    newCommand += fullCommand
                    newCommand += ["&& popd"]
                    fullCommand = newCommand
*/

                    String[] newCommand = ["\"${scriptFile}\"", "\"${directory}\"", gitExe]
                    String[] newArgs = context.opts.toArray()
                    newArgs += context.args.toArray()

                    if (newArgs) {
                        def allArgs = newArgs.join(" ")
                        newCommand += ["\"${allArgs}\""]
                    }

                    fullCommand = newCommand
                }

                pluginExt.writeln("OS: ${GitExecutor.OS_TYPE}")
                pluginExt.writeln("Command to execute: ${fullCommand.join(' ')}")

                if (!pluginExt.noop) {
                    project.exec {
                        commandLine fullCommand
                    }
                } else {
                    pluginExt.writeln("No-operation is activated.")
                }
            }
            else {
                println("${GitExecutor.GIT_EXE} not found.")
            }
        }
    }
}
