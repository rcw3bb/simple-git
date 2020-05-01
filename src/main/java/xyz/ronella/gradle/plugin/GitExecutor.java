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
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GitExecutor {

    public final static OSType OS_TYPE = OSType.identify();

    public final static String GIT_EXE = IExecutable.getInstance(OS_TYPE).getExecutable();

    private final List<String> args;

    private final List<String> opts;

    private final String knownGitExe;

    private GitExecutor(GitExecutorBuilder builder) {
        this.args = builder.args;
        this.opts = builder.opts;
        this.knownGitExe = builder.knownGitExe;
    }

    private String getProgramFile(Path programFile) {
        if (programFile.toFile().exists())  {
            return programFile.toString();
        }
        return null;
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

        return command==null ? null : String.format("\"%s\"", command);
    }

    private Path getScriptPath(String script) {
        final String DEFAULT_JOIN_DELIMITER = "/";
        final String SCRIPTS_DIR = "scripts";

        String internalScript = String.join(DEFAULT_JOIN_DELIMITER, SCRIPTS_DIR, script);
        Path pathScript = Paths.get(".", SCRIPTS_DIR).toAbsolutePath();
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
                return GitExecutor.this.getScriptPath(IScript.getInstance(OSType.identify()).getScript());
            }
        });
    }

    public List<String> getArgs() {
        return new ArrayList<>(args);
    }

    public List<String> getOpts() {
        return new ArrayList<>(opts);
    }

    public String getCommand() {
        String gitExe = getGitExe();

        if (null==gitExe) {
            return null;
        }

        StringBuilder command = new StringBuilder();
        command.append(getGitExe());

        if (null!=opts && opts.size() > 0) {
            command.append(" ").append(String.join(" ", opts));
        }

        if (null!=args && args.size() > 0) {
            command.append(" ").append(String.join(" ", args));
        }

        return command.toString();
    }

    private static class GitExecutorBuilder {

        private final List<String> args = new ArrayList<>();
        private final List<String> opts = new ArrayList<>();
        private String knownGitExe;

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

        public GitExecutor build() {
            return new GitExecutor(this);
        }
    }

    public static GitExecutorBuilder getBuilder() {
        return new GitExecutorBuilder();
    }
}
