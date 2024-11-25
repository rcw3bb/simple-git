package xyz.ronella.gradle.plugin.simple.git;

import xyz.ronella.trivial.handy.OSType;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * The actual assembler of the git command to execute.
 *
 * @author Ron Webb
 * @since 2020-05-05
 */
public class GitExecutor {

    /**
     * Holds the OS Type
     */
    public final static OSType OS_TYPE = OSType.identify();

    /**
     * Holds the git executable.
     */
    public final static String GIT_EXE = IExecutable.of(OS_TYPE).getExecutable();

    private final List<String> args;

    private final List<String> opts;

    private final String knownGitExe;

    private final boolean forceDirectory;

    private final Path directory;

    private GitExecutor(GitExecutorBuilder builder) {
        this.args = builder.args;
        this.opts = builder.opts;
        this.knownGitExe = builder.knownGitExe;
        this.forceDirectory = builder.forceDirectory;
        this.directory = builder.directory;
    }

    private String getProgramFile(Path programFile) {
        if (programFile.toFile().exists())  {
            return programFile.toString();
        }
        return null;
    }

    /**
     * A utility for quoting a string.
     *
     * @param text The text to quote.
     * @return A quoted string.
     */
    public static String quoteString(String text) {
        return quoteString(text, null);
    }

    /**
     * A utility for quoting a string based on os type.
     * Thus, the return value is not necessary a quoted text.
     *
     * @param text The text to quote.
     * @param osType The os type.
     * @return A quoted string.
     */
    public static String quoteString(String text, OSType osType) {
        if (text==null) {
            return null;
        }
        else {
            if (osType==null || OSType.WINDOWS==osType) {
                return String.format("\"%s\"", text);
            }
        }
        return text;
    }

    private String getKnownGitExe() {
        if (knownGitExe !=null) {
            return getProgramFile(Paths.get(knownGitExe));
        }
        return null;
    }

    private String getGitExeByEnvVar() {
        var gitHome= Optional.ofNullable(System.getenv("GIT_HOME"));
        if (gitHome.isPresent()) {
            Path programFile = Paths.get(gitHome.get(), GIT_EXE);
            return getProgramFile(programFile);
        }
        return null;
    }

    /**
     * Provides the git executable.
     *
     * @return The git executable.
     */
    public String getGitExe() {
        List<Supplier<String>> finder = Arrays.asList(
            this::getGitExeByEnvVar,
            this::getKnownGitExe
        );

        String command = null;

        for (Supplier<String> resolver : finder) {
            command = resolver.get();
            if (null!=command) {
                break;
            }
        }

        return command==null ? null : quoteString(command, OS_TYPE);
    }

    private void makeExecutable(String script) {
        ProcessBuilder builder = new ProcessBuilder();
        builder.command("chmod", "775", script);
        try {
            Process process = builder.start();
            int exitCode = process.waitFor();
            assert exitCode ==0;
        } catch (Exception exp) {
            throw new RuntimeException(exp);
        }
    }

    private Path getScriptPath(String script) {
        final String joinDelimiter = "/";
        final String scriptsDir = "scripts";
        final Path scriptLocation = Paths.get("build", "simple", "git");

        String internalScript = String.join(joinDelimiter, scriptsDir, script);
        Path pathScript = Paths.get(".", scriptLocation.toString(), scriptsDir).toAbsolutePath();
        File fileScript = pathScript.toFile();
        Path outputScript = Paths.get(fileScript.toString(), script);

        if (!outputScript.toFile().exists()) {
            fileScript.mkdirs();
            try (InputStream isStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(internalScript)) {
                Files.copy(isStream, outputScript);
                switch (OS_TYPE) {
                    case LINUX:
                    case MAC:
                        makeExecutable(outputScript.toString());
                        break;
                }
            }
            catch(IOException ioe){
                throw new RuntimeException(ioe);
            }
        }

        return outputScript;
    }

    /**
     * Provides the script to use to force in directory execution.
     * @return The path of the script.
     */
    public Path getScript() {
        return getScriptPath(IScript.of(OS_TYPE).getScript());
    }

    /**
     * The directory to run the git command.
     * @return The directory to run the git command.
     */
    public Path getDirectory() {
        return directory;
    }

    /**
     * Must hold the execute logic based on the context provided.
     *
     * @param logic The executed logic with context.
     */
    public void execute(Consumer<IContext> logic) {
        logic.accept(new IContext() {
            @Override
            public String getCommand() {
                return GitExecutor.this.getCommand();
            }

            @Override
            public String getGitExe() {
                return GitExecutor.this.getGitExe();
            }

            @Override
            public List<String> getArgs() {
                return GitExecutor.this.getArgs();
            }

            @Override
            public List<String> getOpts() {
                return GitExecutor.this.getOpts();
            }

            @Override
            public Path getScript() {
                return GitExecutor.this.getScript();
            }

            @Override
            public Path getDirectory() {
                return GitExecutor.this.getDirectory();
            }

            @Override
            public String getExecutable() {
                return GitExecutor.this.getExecutable();
            }

            @Override
            public List<String> getExecArgs() {
                return GitExecutor.this.getExecArgs();
            }
        });
    }

    /**
     * The git command arguments.
     *
     * @return An array of arguments.
     */
    public List<String> getArgs() {
        return new ArrayList<>(args);
    }

    /**
     * The options before the git command.
     * @return An array of options.
     */
    public List<String> getOpts() {
        return new ArrayList<>(opts);
    }

    /**
     * The executable to be used in task exec.
     * @return The executable name.
     */
    public String getExecutable() {
        String gitExe = getGitExe();

        if (gitExe==null) {
            return null;
        }

        if (forceDirectory && null!=directory && null!=getScript()) {
            return quoteString(getScript().toString(), OS_TYPE);
        }
        else {
            return gitExe;
        }
    }

    /**
     * The arguments for the executable.
     *
     * @return An array of executable args.
     */
    public List<String> getExecArgs() {
        List<String> execArgs = new ArrayList<>();
        if (forceDirectory && null!=directory && null!=getScript()) {
            execArgs.add(quoteString(directory.toString(), OS_TYPE));
            execArgs.add(getGitExe());
        }

        if (null!=opts && opts.size() > 0) {
            execArgs.addAll(opts);
        }

        if (null!=args && args.size() > 0) {
            execArgs.addAll(args);
        }

        return execArgs;
    }

    /**
     * The full command that will be executed.
     *
     * @return The full command.
     */
    public String getCommand() {
        String executable = getExecutable();
        final String DELIM=" ";

        if (null==executable) {
            return null;
        }

        StringBuilder command = new StringBuilder(executable);
        getExecArgs().forEach(___arg -> command.append(DELIM).append(String.join(DELIM, ___arg)));

        return command.toString();
    }

    /**
     * The builder of the GitExecutor
     */
    private static class GitExecutorBuilder {

        private final List<String> args = new ArrayList<>();
        private final List<String> opts = new ArrayList<>();
        private String knownGitExe;
        private boolean forceDirectory;
        private Path directory;

        /**
         * Adds the known git executable.
         * @param knownGitExe The known git executable.
         * @return The builder.
         */
        public GitExecutorBuilder addKnownGitExe(String knownGitExe) {
            this.knownGitExe = knownGitExe;
            return this;
        }

        /**
         * Adds the git command argument.
         * @param arg The git command argument.
         * @return The builder.
         */
        public GitExecutorBuilder addArg(String arg) {
            if (null!=arg) {
                args.add(arg);
            }
            return this;
        }

        /**
         * Adds the git command arguments.
         * @param args The git command arguments.
         * @return The builder.
         */
        public GitExecutorBuilder addArgs(List<String> args) {
            if (null!=args) {
                this.args.addAll(args);
            }
            return this;
        }

        /**
         * Adds the git command options.
         * @param opts The git command options.
         * @return The builder.
         */
        public GitExecutorBuilder addOpts(List<String> opts) {
            if (null!=opts) {
                this.opts.addAll(opts);
            }
            return this;
        }

        /**
         * Add indication to force in directory execution.
         * @param forceDirectory The indication to force in directory execution.
         * @return The builder.
         */
        public GitExecutorBuilder addForceDirectory(boolean forceDirectory) {
            this.forceDirectory = forceDirectory;
            return this;
        }

        /**
         * Adds the directory to run the git command.
         * @param directory The directory to run the git command.
         * @return The builder.
         */
        public GitExecutorBuilder addDirectory(File directory) {
            if (null!=directory) {
                this.directory = directory.toPath();
            }
            return this;
        }

        /**
         * Build an instance of GitExecutor.
         * @return The instance of GitExecutor.
         */
        public GitExecutor build() {
            return new GitExecutor(this);
        }
    }

    /**
     * The builder of the GitExecutor
     *
     * @return An instance of GitExecutorBuilder.
     */
    public static GitExecutorBuilder getBuilder() {
        return new GitExecutorBuilder();
    }
}
