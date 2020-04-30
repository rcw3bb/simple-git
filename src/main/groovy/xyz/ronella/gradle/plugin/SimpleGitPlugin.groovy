package xyz.ronella.gradle.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import xyz.ronella.gradle.plugin.task.GitTask

class SimpleGitPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.extensions.create('simple_git', SimpleGitPluginExtension)
        project.task('gitTask', type: GitTask)
    }
}