package xyz.ronella.gradle.plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class GitExecutor {

    public final static OSType OS_TYPE = OSType.identify();

    public final static String GIT_EXE = IExecutable.getInstance(OS_TYPE).getExecutable();

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

    private String quoteString(String text) {
        if (text==null) {
            return null;
        }
        else {
            return String.format("\"%s\"", text);
        }
    }

    private String getKnownGitExe() {
        if (knownGitExe !=null) {
            return getProgramFile(Paths.get(knownGitExe));
        }
        return null;
    }

    private String getGitExeByEnvVar() {
        String gitHome=System.getenv("GIT_HOME");
        Path programFile = Paths.get(gitHome, GIT_EXE);
        return getProgramFile(programFile);
    }

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

        return command==null ? null : quoteString(command);
    }

    private Path getScriptPath(String script) {
        final String DEFAULT_JOIN_DELIMITER = "/";
        final String SCRIPTS_DIR = "scripts";
        final Path SCRIPT_LOCATION = Paths.get("build", "simple", "git");

        String internalScript = String.join(DEFAULT_JOIN_DELIMITER, SCRIPTS_DIR, script);
        Path pathScript = Paths.get(".", SCRIPT_LOCATION.toString(), SCRIPTS_DIR).toAbsolutePath();
        File fileScript = pathScript.toFile();
        Path outputScript = Paths.get(fileScript.toString(), script);

        if (!outputScript.toFile().exists()) {
            fileScript.mkdirs();
            try (InputStream isStream = this.getClass().getClassLoader().getResourceAsStream(internalScript)) {
                Files.copy(isStream, outputScript);
            }
            catch(IOException ioe){
                throw new RuntimeException(ioe);
            }
        }

        return outputScript;
    }

    public Path getScript() {
        return getScriptPath(IScript.getInstance(OSType.identify()).getScript());
    }

    public Path getDirectory() {
        return directory;
    }

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

    public List<String> getArgs() {
        return new ArrayList<>(args);
    }

    public List<String> getOpts() {
        return new ArrayList<>(opts);
    }

    public String getExecutable() {
        String gitExe = getGitExe();

        if (gitExe==null) {
            return null;
        }

        if (forceDirectory && null!=directory && null!=getScript()) {
            return quoteString(getScript().toString());
        }
        else {
            return gitExe;
        }
    }

    public List<String> getExecArgs() {
        List<String> execArgs = new ArrayList<>();
        if (forceDirectory && null!=directory && null!=getScript()) {
            execArgs.add(quoteString(directory.toString()));
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

    public String getCommand() {
        String executable = getExecutable();
        final String DELIM=" ";
        Function<String, String> quoter = ___text -> String.format("\"%s\"", ___text);

        if (null==executable) {
            return null;
        }

        StringBuilder command = new StringBuilder(executable);
        getExecArgs().forEach(___arg -> command.append(DELIM).append(String.join(DELIM, ___arg)));

        return command.toString();
    }

    private static class GitExecutorBuilder {

        private final List<String> args = new ArrayList<>();
        private final List<String> opts = new ArrayList<>();
        private String knownGitExe;
        private boolean forceDirectory;
        private Path directory;

        public GitExecutorBuilder addKnownGitExe(String knownGitExe) {
            this.knownGitExe = knownGitExe;
            return this;
        }

        public GitExecutorBuilder addArg(String arg) {
            if (null!=arg) {
                args.add(arg);
            }
            return this;
        }

        public GitExecutorBuilder addArgs(String ... args) {
            if (null!=args) {
                this.args.addAll(Arrays.asList(args));
            }
            return this;
        }

        public GitExecutorBuilder addOpts(String ... opts) {
            if (null!=opts) {
                this.opts.addAll(Arrays.asList(opts));
            }
            return this;
        }

        public GitExecutorBuilder addForceDirectory(boolean forceDirectory) {
            this.forceDirectory = forceDirectory;
            return this;
        }

        public GitExecutorBuilder addDirectory(File directory) {
            if (null!=directory) {
                this.directory = directory.toPath();
            }
            return this;
        }

        public GitExecutor build() {
            return new GitExecutor(this);
        }
    }

    public static GitExecutorBuilder getBuilder() {
        return new GitExecutorBuilder();
    }
}
