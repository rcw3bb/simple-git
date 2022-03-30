package xyz.ronella.gradle.plugin.simple.git

import static org.junit.jupiter.api.Assertions.*

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GitVersionTest {

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
        def gitTask = project.tasks.gitVersion

        gitTask.executeCommand()

        def executor = gitTask.executor
        def gitExe = executor.gitExe
        def cmd = executor.command

        assertEquals("${gitExe} --version".toString(), cmd)
    }

    @Test
    void testDirectory() {
        def gitTask = project.tasks.gitVersion

        gitTask.directory=new File('D:\\dev\\tmp\\simple-git')

        gitTask.executeCommand()

        def executor = gitTask.executor
        def gitExe = executor.gitExe
        def cmd = executor.command

        assertEquals("${gitExe} --version".toString(), cmd)
    }
}
