package xyz.ronella.gradle.plugin

import xyz.ronella.gradle.plugin.exception.MissingBranchException

import static org.junit.jupiter.api.Assertions.*

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GitBranchTest {

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
        def gitTask = project.tasks.gitBranch

        assertThrows(MissingBranchException, {
            gitTask.executeCommand()
        })
    }

    @Test
    public void testBranch() {
        def gitTask = project.tasks.gitBranch
        gitTask.branch = 'master'

        gitTask.executeCommand()
        def executor = gitTask.executor
        def gitExe = executor.gitExe
        def cmd = executor.command
        def script = executor.script.toString()
        def directory = executor.directory.toString()

        assertEquals("\"${script}\" \"${directory}\" ${gitExe} branch \"${gitTask.branch}\"".toString(), cmd)

    }
}
