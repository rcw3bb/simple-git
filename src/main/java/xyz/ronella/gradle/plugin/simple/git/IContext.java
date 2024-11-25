package xyz.ronella.gradle.plugin.simple.git;

import java.nio.file.Path;
import java.util.List;

/**
 * The context provided in the execute method of the GitExecutor class.
 * This interface defines the methods to access various properties required for executing Git commands.
 *
 * @author Ron Webb
 * @since 2020-05-05
 */
public interface IContext {

    /**
     * Gets the Git command to be executed.
     *
     * @return the Git command as a String.
     */
    String getCommand();

    /**
     * Gets the path to the Git executable.
     *
     * @return the path to the Git executable as a String.
     */
    String getGitExe();

    /**
     * Gets the list of arguments for the Git command.
     *
     * @return a List of arguments as Strings.
     */
    List<String> getArgs();

    /**
     * Gets the list of options for the Git command.
     *
     * @return a List of options as Strings.
     */
    List<String> getOpts();

    /**
     * Gets the path to the script to be executed.
     *
     * @return the path to the script as a Path object.
     */
    Path getScript();

    /**
     * Gets the directory where the Git command should be executed.
     *
     * @return the directory as a Path object.
     */
    Path getDirectory();

    /**
     * Gets the executable to be used.
     *
     * @return the executable as a String.
     */
    String getExecutable();

    /**
     * Gets the list of arguments for the executable.
     *
     * @return a List of arguments as Strings.
     */
    List<String> getExecArgs();
}