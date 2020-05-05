package xyz.ronella.gradle.plugin.simple.git;

import java.nio.file.Path;
import java.util.List;

/**
 * The context provided in the execute method of the GitExecutor class.
 *
 * @author Ron Webb
 * @since 2020-05-05
 */
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
