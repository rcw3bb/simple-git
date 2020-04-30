package xyz.ronella.gradle.plugin;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class GitExecutor {

    public final static OSType OS_TYPE = OSType.identify();

    public final static String GIT_EXE = IExecutable.getInstance(OS_TYPE).getExecutable();

    private final List<String> args;

    private final String knownGitExe;

    private GitExecutor(GitExecutorBuilder builder) {
        this.args = builder.args;
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

        return command;
    }

    public void execute(BiConsumer<String, List<String>> logic) {
        logic.accept(getGitExe(), getArgs());
    }

    public List<String> getArgs() {
        return new ArrayList<>(args);
    }

    public String getCommand() {
        String gitExe = getGitExe();

        if (null==gitExe) {
            return null;
        }

        StringBuilder command = new StringBuilder(gitExe);
        if (null!=args && args.size() > 0) {
            command.append(" ").append(String.join(" ",args));
        }
        return command.toString();
    }

    private static class GitExecutorBuilder {

        private final List<String> args = new ArrayList<>();
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

        public GitExecutor build() {
            return new GitExecutor(this);
        }
    }

    public static GitExecutorBuilder getBuilder() {
        return new GitExecutorBuilder();
    }
}
