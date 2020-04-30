package xyz.ronella.gradle.plugin

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

        gitTask.executeCommand()
        def gitExe = gitTask.getGitExe()
        def cmd = gitTask.getCommand()

        assertEquals("${gitExe} --help".toString(), cmd)
    }

    @Test
    public void testVerbose() {
        assertTrue(project.extensions.simple_git.verbose)
    }

    @Test
    public void testNoop() {
        assertTrue(project.extensions.simple_git.noop)
    }

}