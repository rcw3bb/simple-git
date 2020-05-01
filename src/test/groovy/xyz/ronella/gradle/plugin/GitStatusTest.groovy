package xyz.ronella.gradle.plugin

import static org.junit.jupiter.api.Assertions.*

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GitStatusTest {

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
        def gitTask = project.tasks.gitStatus

        gitTask.executeCommand()
        def gitExe = gitTask.getGitExe()
        def cmd = gitTask.getCommand()

        assertEquals("${gitExe} status".toString(), cmd)
    }

    @Test
    public void testDirectory() {
        def gitTask = project.tasks.gitStatus
        gitTask.directory=new File('C:\\directory')

        gitTask.executeCommand()
        def gitExe = gitTask.getGitExe()
        def cmd = gitTask.getCommand()

        assertEquals("${gitExe} status".toString(), cmd)
    }
}
