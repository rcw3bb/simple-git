package xyz.ronella.gradle.plugin.simple.git

import static org.junit.jupiter.api.Assertions.*

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GitTaskTest {
    private Project project;

    @BeforeEach
    public void initProject() {
        project = ProjectBuilder.builder().build()
        project.pluginManager.apply 'simple-git'
        project.extensions.simple_git.verbose = true
        project.extensions.simple_git.noop = true
    }

    @Test
    public void testCommand() {
        def gitTask = project.tasks.gitTask
        gitTask.directory = new File("D:/dev/tmp/simple-git")
        gitTask.executeCommand()

        def executor = gitTask.executor
        def gitExe = executor.gitExe
        def cmd = executor.command
        def script = executor.script.toString()
        def directory = executor.directory.toString()

        assertEquals("\"${script}\" \"${directory}\" ${gitExe} --help".toString(), cmd)
    }

    @Test
    public void testVerbose() {
        assertTrue(project.extensions.simple_git.verbose)
    }

    @Test
    public void testNoop() {
        assertTrue(project.extensions.simple_git.noop)
    }

    @Test
    public void testNoGitInstalled() {

        def gitTask = project.tasks.gitTask
        project.extensions.simple_git.noop = false
        def testExt = project.extensions.simple_git_test
        testExt.no_git_installed = true

        gitTask.executeCommand()

        assertEquals("git.exe not found. Please install git application and try again.", testExt.test_message)
    }

}