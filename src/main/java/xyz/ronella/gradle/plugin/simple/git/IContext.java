package xyz.ronella.gradle.plugin.simple.git;

import java.nio.file.Path;
import java.util.List;

public interface IContext {
    String getCommand();
    String getGitExe();
    List<String> getArgs();
    List<String> getOpts();
    Path getScript();
    Path getDirectory();
    String getExecutable();
    List<String> getExecArgs();
}
