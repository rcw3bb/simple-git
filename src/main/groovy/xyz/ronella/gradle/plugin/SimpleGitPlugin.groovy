package xyz.ronella.gradle.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import xyz.ronella.gradle.plugin.task.GitBranch
import xyz.ronella.gradle.plugin.task.GitCheckout
import xyz.ronella.gradle.plugin.task.GitClone
import xyz.ronella.gradle.plugin.task.GitDeleteBranch
import xyz.ronella.gradle.plugin.task.GitFetchPR
import xyz.ronella.gradle.plugin.task.GitStatus
import xyz.ronella.gradle.plugin.task.GitTask

class SimpleGitPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.extensions.create('simple_git', SimpleGitPluginExtension)
        project.task('gitTask', type: GitTask)
        project.task('gitClone', type: GitClone)
        project.task('gitStatus', type: GitStatus)
        project.task('gitFetchPR', type: GitFetchPR)
        project.task('gitCheckout', type: GitCheckout)
        project.task('gitBranch', type: GitBranch)
        project.task('gitDeleteBranch', type: GitDeleteBranch)
    }
}