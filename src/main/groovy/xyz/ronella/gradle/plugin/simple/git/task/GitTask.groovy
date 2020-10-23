package xyz.ronella.gradle.plugin.simple.git.task

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import xyz.ronella.gradle.plugin.simple.git.OSType
import xyz.ronella.gradle.plugin.simple.git.SimpleGitPluginExtension
import xyz.ronella.gradle.plugin.simple.git.GitExecutor
import xyz.ronella.gradle.plugin.simple.git.SimpleGitPluginTestExtension

import java.nio.file.Path
import java.util.stream.Collectors

/**
 * The git task that can execute any git command that you can do in console.
 *
 * @author Ron Webb
 * @since 2020-05-05
 */
class GitTask extends DefaultTask {

    /**
     * Force the execute the git command inside a directory.
     */
    @Optional @Input
    boolean forceDirectory = true

    /**
     * The options added before the git command.
     */
    @Optional @Input
    String[] options = []

    @Internal
    protected OSType osType

    private File directory

    /**
     * The git command to execute.
     */
    @Input
    String command = ''

    /**
     * The arguments for the git command.
     */
    @Input
    String[] args = []

    @Optional @Input
    String[] zargs = []

    public GitTask() {
        group = 'Simple Git'
        description = 'Execute a git command.'
        osType = GitExecutor.OS_TYPE
    }

    /**
     * The directory where the git command must be executed.
     *
     * @return The instance of File pointing to where to execute the git command.
     */
    @Optional @Input
    File getDirectory() {
        return this.directory
    }

    void setDirectory(File directory) {
        this.directory = directory
    }

    /**
     * Initialized fields based on command line parameters.
     */
    public def initFields() {
        SimpleGitPluginExtension pluginExt = project.extensions.simple_git

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

        if (project.hasProperty('sg_zargs')) {
            zargs = (project.sg_zargs as String).split(",").toList().stream()
                    .map( { ___arg -> ___arg.trim()})
                    .collect(Collectors.toList()).toArray()
            pluginExt.writeln("Found sg_zargs: ${zargs}")
        }
    }

    /**
     * Assemble all the arguments for the git command.
     *
     * @return An array of arguments for the git command.
     */
    public String[] getAllArgs() {
        String[] newArgs = args

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

    /**
     * Build and instance of GitExecutor.
     *
     * @return An instance of GitExecutor.
     */
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

    private def initCommand() {
        SimpleGitPluginExtension pluginExt = project.extensions.simple_git;

        if (directory==null) {
            directory = pluginExt.directory==null ? project.rootProject.rootDir : pluginExt.directory
        }

        initFields()
    }

    /**
     * The main action logic of the task.
     */
    @TaskAction
    def executeCommand() {
        SimpleGitPluginExtension pluginExt = project.extensions.simple_git;
        SimpleGitPluginTestExtension pluginTestExt = project.extensions.simple_git_test;

        initCommand()

        def executor = getExecutor()
        executor.execute { context ->
            def gitExecutable = pluginTestExt.no_git_installed ? null : context.gitExe

            if (gitExecutable!=null) {
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
                String message = "${GitExecutor.GIT_EXE} not found. Please install git application and try again."
                pluginTestExt.test_message = message
                println(message)
            }
        }
    }
}
