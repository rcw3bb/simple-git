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
import java.util.stream.Collectors

class GitTask extends DefaultTask {

    @Optional @Input
    protected boolean forceDirectory = true

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
        SimpleGitPluginExtension pluginExt = project.extensions.simple_git;
        group = 'Simple Git'
        description = 'Execute git command.'
        directory = pluginExt.directory==null ? project.projectDir : pluginExt.directory
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

        if (project.hasProperty('sg_directory')) {
            directory = new File((project.sg_directory as String).trim())
            pluginExt.writeln("Found sg_directory: ${directory}")
        }

        if (project.hasProperty('sg_command')) {
            command = new File((project.sg_command as String).trim())
            pluginExt.writeln("Found sg_command: ${command}")
        }

        if (project.hasProperty('sg_options')) {
            options = (project.sg_options as String).split(",").toList().stream()
                    .map( {___arg -> ___arg.trim()})
                    .collect(Collectors.toList()).toArray()
            pluginExt.writeln("Found sg_options: ${options}")
        }

        if (project.hasProperty('sg_args')) {
            args = (project.sg_args as String).split(",").toList().stream()
                    .map( { ___arg -> ___arg.trim()})
                    .collect(Collectors.toList()).toArray()
            pluginExt.writeln("Found sg_args: ${args}")
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

    public GitExecutor getExecutor() {
        def knownGit = detectGitExec()
        def builder = GitExecutor.getBuilder()

        builder.addKnownGitExe(knownGit)
        if (command) {
            builder.addArg(command)
        }
        builder.addArgs(allArgs)
        builder.addOpts(options)
        builder.addForceDirectory(forceDirectory)
        builder.addDirectory(directory)

        return builder.build()
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
                pluginExt.writeln("OS: ${GitExecutor.OS_TYPE}")
                pluginExt.writeln("Command to execute: ${fullCommand.join(' ')}")

                if (!pluginExt.noop) {
                    project.exec {
                        executable context.executable
                        if (context.execArgs) {
                            args context.execArgs.toArray()
                        }
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
