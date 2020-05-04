package xyz.ronella.gradle.plugin.simple.git

import xyz.ronella.gradle.plugin.simple.git.exception.MissingBranchException

import static org.junit.jupiter.api.Assertions.*

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GitCheckoutTest {

    private Project project;

    @BeforeEach
    public void initProject() {
        project = ProjectBuilder.builder().build()
        project.pluginManager.apply 'simple-git'
        project.extensions.simple_git.verbose = true
        project.extensions.simple_git.noop = true
    }

    @Test
    public void testNoParameters() {
        def gitTask = project.tasks.gitCheckout

        assertThrows(MissingBranchException, {
            gitTask.executeCommand()
        })
    }

    @Test
    public void testOtherParameters() {
        def gitTask = project.tasks.gitCheckout
        gitTask.args += 'test'

        gitTask.executeCommand()
        def executor = gitTask.executor
        def gitExe = executor.gitExe
        def cmd = executor.command
        def script = executor.script.toString()
        def directory = executor.directory.toString()

        assertEquals("\"${script}\" \"${directory}\" ${gitExe} checkout test".toString(), cmd)

    }

    @Test
    public void testBranch() {
        def gitTask = project.tasks.gitCheckout
        gitTask.branch = 'master'

        gitTask.executeCommand()
        def executor = gitTask.executor
        def gitExe = executor.gitExe
        def cmd = executor.command
        def script = executor.script.toString()
        def directory = executor.directory.toString()

        assertEquals("\"${script}\" \"${directory}\" ${gitExe} checkout \"${gitTask.branch}\"".toString(), cmd)

    }

}
