package xyz.ronella.gradle.plugin

import static org.junit.jupiter.api.Assertions.*

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SimpleGitPluginTest {
    private Project project;

    @BeforeEach
    public void initProject() {
        project = ProjectBuilder.builder().build()
        project.pluginManager.apply 'simple-git'
        project.extensions.simple_git.verbose = true
        project.extensions.simple_git.noop = true
    }

    @Test
    public void testVerbose() {
        project.tasks.gitTask.executeCommand()
        assertTrue(project.extensions.simple_git.verbose)
    }

    @Test
    public void testNoop() {
        project.tasks.gitTask.executeCommand()
        assertTrue(project.extensions.simple_git.noop)
    }

}