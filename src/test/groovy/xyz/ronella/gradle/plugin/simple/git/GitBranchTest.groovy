package xyz.ronella.gradle.plugin.simple.git

import xyz.ronella.gradle.plugin.simple.git.exception.MissingBranchException

import static org.junit.jupiter.api.Assertions.*

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GitBranchTest {

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
        def gitTask = project.tasks.gitBranch

        assertThrows(MissingBranchException, {
            gitTask.executeCommand()
        })
    }

    @Test
    void testBranch() {
        def gitTask = project.tasks.gitBranch
        gitTask.branch = 'master'

        gitTask.executeCommand()
        def executor = gitTask.executor
        def gitExe = executor.gitExe
        def cmd = executor.command
        def script = executor.script.toString()
        def directory = executor.directory.toString()

        assertEquals("\"${script}\" \"${directory}\" ${gitExe} branch \"master\"".toString(), cmd)

    }

    @Test
    void testBranchZArgs() {
        def gitTask = project.tasks.gitBranch
        gitTask.branch = 'master'
        gitTask.zargs = ['-zargs']

        gitTask.executeCommand()
        def executor = gitTask.executor
        def gitExe = executor.gitExe
        def cmd = executor.command
        def script = executor.script.toString()
        def directory = executor.directory.toString()

        assertEquals("\"${script}\" \"${directory}\" ${gitExe} branch \"master\" -zargs".toString(), cmd)

    }
}
