package xyz.ronella.gradle.plugin.simple.git

import xyz.ronella.gradle.plugin.simple.git.exception.MissingBranchException

import static org.junit.jupiter.api.Assertions.*

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GitDeleteBranchTest {
    private Project project

    @BeforeEach
    void initProject() {
        project = ProjectBuilder.builder().build()
        project.pluginManager.apply 'xyz.ronella.simple-git'
        project.extensions.simple_git.verbose = true
        project.extensions.simple_git.noop = true
    }

    @Test
    void testNoParameters() {
        def gitTask = project.tasks.gitDeleteBranch

        assertThrows(MissingBranchException, {
            gitTask.executeCommand()
        })
    }

    @Test
    void testDeleteBranch() {
        def gitTask = project.tasks.gitDeleteBranch
        gitTask.branch = 'master'

        gitTask.executeCommand()
        def executor = gitTask.executor
        def gitExe = executor.gitExe
        def cmd = executor.command
        def script = executor.script.toString()
        def directory = executor.directory.toString()

        assertEquals("\"${script}\" \"${directory}\" ${gitExe} branch -d \"master\"".toString(), cmd)
    }

    @Test
    void testForceDelete() {
        def gitTask = project.tasks.gitDeleteBranch
        gitTask.branch = 'dummy'
        gitTask.force = true

        gitTask.executeCommand()
        def executor = gitTask.executor
        def gitExe = executor.gitExe
        def cmd = executor.command
        def script = executor.script.toString()
        def directory = executor.directory.toString()

        assertEquals("\"${script}\" \"${directory}\" ${gitExe} branch -D \"dummy\"".toString(), cmd)
    }

    @Test
    void testForceDeleteZargs() {
        def gitTask = project.tasks.gitDeleteBranch
        gitTask.branch = 'dummy'
        gitTask.force = true
        gitTask.zargs.add('-zargs')

        gitTask.executeCommand()
        def executor = gitTask.executor
        def gitExe = executor.gitExe
        def cmd = executor.command
        def script = executor.script.toString()
        def directory = executor.directory.toString()

        assertEquals("\"${script}\" \"${directory}\" ${gitExe} branch -D \"dummy\" -zargs".toString(), cmd)
    }

}
