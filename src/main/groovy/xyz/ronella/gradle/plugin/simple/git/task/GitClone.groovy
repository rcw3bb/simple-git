package xyz.ronella.gradle.plugin.simple.git.task

import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import xyz.ronella.gradle.plugin.simple.git.GitExecutor
import xyz.ronella.gradle.plugin.simple.git.SimpleGitPluginExtension
import xyz.ronella.gradle.plugin.simple.git.exception.MissingDirectoryException
import xyz.ronella.gradle.plugin.simple.git.exception.MissingRepositoryException

/**
 * A convenience git task for branch clone.
 *
 * @author Ron Webb
 * @since 2020-05-05
 */
abstract class GitClone extends GitTask {

    @Optional @Input
    abstract Property<String> getBranch()

    @Input
    abstract Property<String> getRepository()

    GitClone() {
        super()
        description = 'A convenience git clone command.'
        command.convention('clone')
        forceDirectory.convention(false)
    }

    @Override
    def initFields() {
        super.initFields()
        SimpleGitPluginExtension pluginExt = project.extensions.simple_git

        if (project.hasProperty('sg_repository')) {
            repository.convention((project.sg_repository as String).trim())
            pluginExt.writeln("Found sg_repository: ${repository}")
        }
        if (project.hasProperty('sg_branch')) {
            branch.convention((project.sg_branch as String).trim())
            pluginExt.writeln("Found sg_branch: ${branch}")
        }
    }

    @Override
    ListProperty<String> getAllArgs() {
        ListProperty<String> newArgs = super.getAllArgs()

        initFields()

        if (branch.isPresent()) {
            newArgs.add('--branch')
            newArgs.add(GitExecutor.quoteString(branch.get(), osType))
        }

        if (repository.isPresent()) {
            newArgs.add(GitExecutor.quoteString(repository.get(), osType))
        }
        else {
            throw new MissingRepositoryException()
        }

        if (!directory.isPresent()) {
            throw new MissingDirectoryException()
        }

        def gitCommand = GitExecutor.quoteString(directory.get().absolutePath, osType)
        newArgs.add(gitCommand)

        if (zargs.isPresent()) {
            newArgs.addAll(zargs.get())
        }

        return newArgs
    }
}
