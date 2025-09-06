package xyz.ronella.gradle.plugin.simple.git

import org.gradle.api.Plugin
import org.gradle.api.Project
import xyz.ronella.gradle.plugin.simple.git.task.*

/**
 * The entry point of the plugin.
 *
 * @author Ron Webb
 * @since 2020-05-05
 */
class SimpleGitPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.extensions.create('simple_git', SimpleGitPluginExtension)
        project.extensions.create('simple_git_test', SimpleGitPluginTestExtension)
        def ext = (SimpleGitPluginExtension) project.extensions.simple_git
        ext.logger = project.logger
        project.tasks.register('gitTask', GitTask)
        project.tasks.register('gitClone', GitClone)
        project.tasks.register('gitStatus', GitStatus)
        project.tasks.register('gitFetchPR', GitFetchPR)
        project.tasks.register('gitCheckout', GitCheckout)
        project.tasks.register('gitBranch', GitBranch)
        project.tasks.register('gitDeleteBranch', GitDeleteBranch)
        project.tasks.register('gitPull', GitPull)
        project.tasks.register('gitVersion', GitVersion)
        project.tasks.register('gitTag', GitTag)
    }
}