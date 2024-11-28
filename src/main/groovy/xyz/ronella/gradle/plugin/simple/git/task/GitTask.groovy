package xyz.ronella.gradle.plugin.simple.git.task

import org.gradle.api.DefaultTask
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

import xyz.ronella.gradle.plugin.simple.git.SimpleGitPluginExtension
import xyz.ronella.gradle.plugin.simple.git.GitExecutor
import xyz.ronella.gradle.plugin.simple.git.SimpleGitPluginTestExtension
import xyz.ronella.trivial.decorator.StringBuilderAppender
import xyz.ronella.trivial.functional.impl.StringBuilderDelim
import xyz.ronella.trivial.handy.OSType

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Collectors
import java.util.stream.Stream

/**
 * The git task that can execute any git command that you can do in console.
 *
 * @author Ron Webb
 * @since 2020-05-05
 */
abstract class GitTask extends DefaultTask {

    /**
     * Force the execute the git command inside a directory.
     */
    @Optional @Input
    abstract Property<Boolean> getForceDirectory()

    /**
     * The options added before the git command.
     */
    @Optional @Input
    abstract ListProperty<String> getOptions()

    protected final OSType OS_TYPE
    protected final SimpleGitPluginExtension EXTENSION

    @Input @Optional
    abstract Property<File> getDirectory()

    /**
     * The git command to execute.
     */
    @Input @Optional
    abstract Property<String> getCommand()

    /**
     * The username for the git command.
     * @return
     */
    @Input @Optional
    abstract Property<String> getUsername()

    /**
     * The password for the git command.
     * @return
     */
    @Input @Optional
    abstract Property<String> getPassword()

    /**
     * The arguments for the git command.
     */
    @Input
    abstract ListProperty<String> getArgs()

    @Optional @Input
    abstract ListProperty<String> getZargs()

    GitTask() {
        group = 'Simple Git'
        description = 'Execute a git command.'
        OS_TYPE = GitExecutor.OS_TYPE
        EXTENSION = project.extensions.simple_git
        forceDirectory.convention(true)
        directory.convention(project.rootProject.rootDir)
        initialization()
    }

    protected String urlEncode(final String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8)
    }

    @Input @Internal
    Provider<String> getEncodedUsername() {
        final def usrName = username.orElse(EXTENSION.username)
        return usrName.map(___username -> urlEncode(___username))
    }

    @Input @Internal
    Provider<String> getEncodedPassword() {
        final def passwd = password.orElse(EXTENSION.password)
        return passwd.map(___password -> urlEncode(___password))
    }

    @Input @Internal
    java.util.Optional<String> getEncodedCred() {
        final var sbCred = new StringBuilderAppender(new StringBuilderDelim(":"))
                .appendWhen(sb -> sb.append(encodedUsername.get())).when(sb -> encodedUsername.isPresent())
                .appendWhen(sb -> sb.append(encodedPassword.get())).when(sb -> encodedUsername.isPresent() && encodedPassword.isPresent())
        return java.util.Optional.ofNullable(sbCred.getStringBuilder().isBlank() ? null : sbCred.toString())
    }

    String insertCredToURL(String url) {
        final var encodedCred = getEncodedCred()
        return encodedCred.isPresent() ? url.replaceFirst("://", "://" + encodedCred.get() + "@") : url
    }

    /**
     * Initialized fields based on command line parameters.
     */
    def initialization() {

        if (project.hasProperty('sg_directory')) {
            directory.convention(new File((project.sg_directory as String)))
            EXTENSION.writeln("Found sg_directory: ${directory}")
        }

        if (project.hasProperty('sg_command')) {
            command.convention(new File((project.sg_command as String).trim()).absolutePath)
            EXTENSION.writeln("Found sg_command: ${command}")
        }

        if (project.hasProperty('sg_options')) {
            options.convention((project.sg_options as String).split(",").toList().stream()
                    .map( {___arg -> ___arg.trim()})
                    .collect(Collectors.toList()))
            EXTENSION.writeln("Found sg_options: ${options}")
        }

        if (project.hasProperty('sg_args')) {
            args.convention((project.sg_args as String).split(",").toList().stream()
                    .map( { ___arg -> ___arg.trim()})
                    .collect(Collectors.toList()))
            EXTENSION.writeln("Found sg_args: ${args}")
        }

        if (project.hasProperty('sg_zargs')) {
            zargs.convention((project.sg_zargs as String).split(",").toList().stream()
                    .map( { ___arg -> ___arg.trim()})
                    .collect(Collectors.toList()))
            EXTENSION.writeln("Found sg_zargs: ${zargs}")
        }
    }

    /**
     * Assemble all the arguments for the git command.
     *
     * @return An array of arguments for the git command.
     */
    @Internal
    protected ListProperty<String> getAllArgs() {
        def newArgs = args.get()
        def allTheArgs = project.getObjects().listProperty(String.class)
        if ((command.getOrElse("").length()>0 || newArgs.size() > 0)) {
            allTheArgs.addAll(newArgs)
        }
        else {
            allTheArgs.add('--help')
        }

        return allTheArgs
    }

    private String detectGitExec() {
        def osType = GitExecutor.OS_TYPE
        def gitExec = GitExecutor.GIT_EXE
        String cmd = null
        switch (osType) {
            case OSType.WINDOWS:
                cmd="where"
                break
            case OSType.LINUX:
                cmd="which"
                break
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
                switch (osType) {
                    case OSType.WINDOWS:
                        String[] execs = knownGit.split("\r\n");
                        knownGit = execs.first();
                        break;
                }

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
    @Internal
    GitExecutor getExecutor() {
        def knownGit = detectGitExec()
        def builder = GitExecutor.getBuilder()

        builder.addKnownGitExe(knownGit)

        if (command.isPresent()) {
            builder.addArg(command.get())
        }
        builder.addArgs(allArgs.getOrElse([]))
        builder.addOpts(options.getOrElse([]))
        builder.addForceDirectory(forceDirectory.get())

        def  targetDir = java.util.Optional.ofNullable(directory.get())

        if (directory.get()==project.rootProject.rootDir) {
            targetDir = java.util.Optional.ofNullable(EXTENSION.directory.getOrElse(directory.get()))
        }

        targetDir.ifPresent { ___dir->
            builder.addDirectory(___dir)
        }

        return builder.build()
    }

    boolean directoryIsEmpty() {
        if (directory.isPresent()) {
            def dirFile = directory.get()
            if (dirFile.exists()) {
                try (Stream<Path> entries = Files.list(dirFile.toPath())) {
                    def hasEntry = entries.findFirst().isPresent()
                    if (hasEntry) {
                        println("${dirFile.absolutePath} is not empty.")
                    }
                    return !hasEntry
                }
            }
        }

        return true
    }

    /**
     * The main action logic of the task.
     */
    @TaskAction
    def executeCommand() {
        SimpleGitPluginTestExtension pluginTestExt = project.extensions.simple_git_test;

        def executor = getExecutor()
        executor.execute { context ->
            def gitExecutable = pluginTestExt.no_git_installed ? null : context.gitExe

            if (gitExecutable!=null) {
                String[] fullCommand = [context.command]
                Path scriptFile = context.script
                EXTENSION.writeln("Script: " + scriptFile.toString())
                EXTENSION.writeln("OS: ${GitExecutor.OS_TYPE}")
                EXTENSION.writeln("Command to execute: ${fullCommand.join(' ')}")

                if (!EXTENSION.noop.get()) {
                    project.exec {
                        executable context.executable
                        if (context.execArgs) {
                            args context.execArgs.toArray()
                        }
                    }
                } else {
                    EXTENSION.writeln("No-operation is activated.")
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
