package xyz.ronella.gradle.plugin.simple.git

import xyz.ronella.gradle.plugin.simple.git.exception.MissingBranchException

import static org.junit.jupiter.api.Assertions.*

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GitCheckoutTest {

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
        def gitTask = project.tasks.gitCheckout
        def executor = gitTask.executor

        String cmd = executor.command

        assertTrue(cmd.contains(project.extensions.simple_git.branch.get()))
    }

    @Test
    void testOtherParameters() {
        def gitTask = project.tasks.gitCheckout
        gitTask.args=['test']

        gitTask.executeCommand()
        def executor = gitTask.executor
        def gitExe = executor.gitExe
        def cmd = executor.command
        def script = executor.script.toString()
        def directory = executor.directory.toString()

        assertEquals("\"${script}\" \"${directory}\" ${gitExe} checkout test \"master\"".toString(), cmd)

    }

    @Test
    void testBranch() {
        def gitTask = project.tasks.gitCheckout
        gitTask.branch = 'master'

        gitTask.executeCommand()
        def executor = gitTask.executor
        def gitExe = executor.gitExe
        def cmd = executor.command
        def script = executor.script.toString()
        def directory = executor.directory.toString()

        assertEquals("\"${script}\" \"${directory}\" ${gitExe} checkout \"master\"".toString(), cmd)
    }

    @Test
    void testBranchZargs() {
        def gitTask = project.tasks.gitCheckout
        gitTask.branch = 'master'
        gitTask.zargs = ['-zargs']

        gitTask.executeCommand()
        def executor = gitTask.executor
        def gitExe = executor.gitExe
        def cmd = executor.command
        def script = executor.script.toString()
        def directory = executor.directory.toString()

        assertEquals("\"${script}\" \"${directory}\" ${gitExe} checkout \"master\" -zargs".toString(), cmd)

    }

}
