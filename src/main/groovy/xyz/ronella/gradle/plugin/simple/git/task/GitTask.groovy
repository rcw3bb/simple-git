package xyz.ronella.gradle.plugin.simple.git.task

import org.gradle.api.DefaultTask
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations

import javax.inject.Inject

import xyz.ronella.gradle.plugin.simple.git.SimpleGitPluginExtension
import xyz.ronella.gradle.plugin.simple.git.GitExecutor
import xyz.ronella.gradle.plugin.simple.git.SimpleGitPluginTestExtension
import xyz.ronella.gradle.plugin.simple.git.exception.MissingGitException
import xyz.ronella.trivial.decorator.StringBuilderAppender
import xyz.ronella.trivial.functional.impl.StringBuilderDelim
import xyz.ronella.trivial.handy.CommandProcessorException
import xyz.ronella.trivial.handy.CommandLocator
import xyz.ronella.trivial.handy.OSType

import javax.inject.Inject
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
    
    @Inject
    abstract ExecOperations getExecOperations()
    
    @Inject
    abstract ObjectFactory getObjects()

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

    // Configuration cache compatible properties for extensions
    @Input @Optional
    abstract Property<Boolean> getNoopMode()
    
    @Input @Optional
    abstract Property<Boolean> getVerboseMode()
    
    @Input @Optional
    abstract Property<Boolean> getNoGitInstalled()
    
    @Input @Optional
    abstract ListProperty<String> getDefaultArgs()
    
    @Input @Optional  
    abstract ListProperty<String> getDefaultOptions()
    
    @Input @Optional
    abstract Property<File> getRootProjectDir()

    protected ListProperty<String> internalOptions

    protected ListProperty<String> internalZArgs

    GitTask() {
        group = 'Simple Git'
        description = 'Execute a git command.'
        OS_TYPE = GitExecutor.OS_TYPE
        EXTENSION = project.extensions.simple_git
        forceDirectory.convention(true)
        directory.convention(project.rootProject.rootDir)
        internalZArgs = getObjects().listProperty(String.class)
        internalOptions = getObjects().listProperty(String.class)
        
        SimpleGitPluginTestExtension testExt = project.extensions.simple_git_test
        noopMode.convention(EXTENSION.noop)
        verboseMode.convention(EXTENSION.verbose)
        noGitInstalled.convention(testExt.no_git_installed)
        defaultArgs.convention(EXTENSION.defaultArgs)
        defaultOptions.convention(EXTENSION.defaultOptions)
        directory.convention(EXTENSION.directory.getOrElse(project.rootProject.rootDir))
        rootProjectDir.convention(project.rootProject.rootDir)
        
        if (project.hasProperty('sg_directory')) {
            directory.convention(new File((project.sg_directory as String)))
            logger.lifecycle("Found sg_directory: ${project.sg_directory}")
        }

        if (project.hasProperty('sg_command')) {
            command.convention(new File((project.sg_command as String).trim()).absolutePath)
            logger.lifecycle("Found sg_command: ${project.sg_command}")
        }

        if (project.hasProperty('sg_options')) {
            options.convention((project.sg_options as String).split(",").toList().stream()
                    .map( {___arg -> ___arg.trim()})
                    .collect(Collectors.toList()))
            logger.lifecycle("Found sg_options: ${project.sg_options}")
        }

        if (project.hasProperty('sg_args')) {
            args.convention((project.sg_args as String).split(",").toList().stream()
                    .map( { ___arg -> ___arg.trim()})
                    .collect(Collectors.toList()))
            logger.lifecycle("Found sg_args: ${project.sg_args}")
        }

        if (project.hasProperty('sg_zargs')) {
            zargs.convention((project.sg_zargs as String).split(",").toList().stream()
                    .map( { ___arg -> ___arg.trim()})
                    .collect(Collectors.toList()))
            logger.lifecycle("Found sg_zargs: ${project.sg_zargs}")
        }
    }

    protected String urlEncode(final String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8)
    }

    @Internal
    Provider<String> getEncodedUsername() {
        final def usrName = username.orElse(EXTENSION.username)
        return usrName.map(___username -> urlEncode(___username))
    }

    @Internal
    Provider<String> getEncodedPassword() {
        final def passwd = password.orElse(EXTENSION.password)
        return passwd.map(___password -> urlEncode(___password))
    }

    @Internal
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
     * Assemble all the arguments for the git command.
     *
     * @return An array of arguments for the git command.
     */
    @Internal
    protected ListProperty<String> getAllArgs() {
        def newArgs = args.get()
        def allTheArgs = getObjects().listProperty(String.class)
        if ((command.getOrElse("").length()>0 || newArgs.size() > 0)) {
            allTheArgs.addAll(newArgs)
        }
        else {
            allTheArgs.add('--help')
        }

        return allTheArgs
    }

    /**
     * Build and instance of GitExecutor.
     *
     * @return An instance of GitExecutor.
     */
    @Internal
    GitExecutor getExecutor() {
        def builder = GitExecutor.getBuilder()

        try {
            def knownGit = CommandLocator.locateAsString(GitExecutor.GIT_EXE).orElse(null)
            builder.addKnownGitExe(knownGit)
        } catch (CommandProcessorException cpe) {
            logger.error("detectGitExec Error: ${cpe.getMessage()}")
        }

        if (command.isPresent()) {
            builder.addArg(command.get())
        }
        builder.addArgs(allArgs.getOrElse([]))
        builder.addArgs(defaultArgs.getOrElse([]))
        builder.addArgs(internalZArgs.getOrElse([]))
        builder.addOpts(defaultOptions.getOrElse([]))
        builder.addOpts(internalOptions.getOrElse([]))
        builder.addOpts(options.getOrElse([]))
        builder.addForceDirectory(forceDirectory.get())

        if (directory.isPresent()) {
            builder.addDirectory(directory.get())
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
                        logger.lifecycle("${dirFile.absolutePath} is not empty.")
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
        def verboseMode = this.verboseMode.get()
        def noopMode = this.noopMode.get()
        def taskLogger = this.logger  // Capture logger reference for use in closure
        def isNoGitInstalled = noGitInstalled.isPresent() ? noGitInstalled.get() : false
        
        def executor = getExecutor()
        executor.execute { context ->
            def gitExecutable = isNoGitInstalled ? null : context.gitExe

            if (gitExecutable!=null) {
                String[] fullCommand = [context.command]
                Path scriptFile = context.script
                
                if (verboseMode || noopMode) {
                    taskLogger.lifecycle("Script: " + scriptFile.toString())
                    taskLogger.lifecycle("OS: ${GitExecutor.OS_TYPE}")
                    taskLogger.lifecycle("Command to execute: ${fullCommand.join(' ')}")
                }

                if (!noopMode) {
                    getExecOperations().exec {
                        executable context.executable
                        if (context.execArgs) {
                            args context.execArgs.toArray()
                        }
                    }
                } else {
                    if (verboseMode || noopMode) {
                        taskLogger.lifecycle("No-operation is activated.")
                    }
                }
            }
            else {
                String message = "${GitExecutor.GIT_EXE} not found. Please install git application and try again."
                throw(new MissingGitException(message))
            }
        }
    }
}
