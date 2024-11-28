package xyz.ronella.gradle.plugin.simple.git

import xyz.ronella.gradle.plugin.simple.git.task.GitBranch

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
        gitTask.executeCommand()
        def executor = gitTask.executor

        String cmd = executor.command

        assertTrue(cmd.contains(project.extensions.simple_git.branch.get()))
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

    @Test
    void testDefaultOptions() {
        def gitTask = (GitBranch) project.tasks.gitBranch
        gitTask.branch = 'master'

        def ext = (SimpleGitPluginExtension) project.extensions.simple_git
        ext.defaultOptions = ['-c', 'dummy']

        gitTask.executeCommand()

        def executor = gitTask.executor
        def gitExe = executor.gitExe
        def cmd = executor.command
        def script = executor.script.toString()
        def directory = executor.directory.toString()

        def expected = "\"${script}\" \"${directory}\" ${gitExe} -c dummy branch \"master\"".toString()

        assertEquals(expected, cmd)
    }

    @Test
    void testBranchDefaultOptions() {
        def gitTask = (GitBranch) project.tasks.gitBranch
        gitTask.branch = 'master'

        def ext = (SimpleGitPluginExtension) project.extensions.simple_git
        ext.defaultOptions = ['-c', 'dummy']
        ext.defaultBranchOptions = ['-c', 'dummy2']

        gitTask.executeCommand()

        def executor = gitTask.executor
        def gitExe = executor.gitExe
        def cmd = executor.command
        def script = executor.script.toString()
        def directory = executor.directory.toString()

        def expected = "\"${script}\" \"${directory}\" ${gitExe} -c dummy -c dummy2 branch \"master\"".toString()

        assertEquals(expected, cmd)
    }

    @Test
    void testDefaultArgs() {
        def gitTask = (GitBranch) project.tasks.gitBranch
        gitTask.branch = 'master'

        def ext = (SimpleGitPluginExtension) project.extensions.simple_git
        ext.defaultArgs = ['--additional-arg']

        gitTask.executeCommand()

        def executor = gitTask.executor
        def gitExe = executor.gitExe
        def cmd = executor.command
        def script = executor.script.toString()
        def directory = executor.directory.toString()

        def expected = "\"${script}\" \"${directory}\" ${gitExe} branch \"master\" --additional-arg".toString()

        assertEquals(expected, cmd)
    }

    @Test
    void testBranchDefaultArgs() {
        def gitTask = (GitBranch) project.tasks.gitBranch
        gitTask.branch = 'master'

        def ext = (SimpleGitPluginExtension) project.extensions.simple_git
        ext.defaultArgs = ['--additional-arg']
        ext.defaultBranchArgs = ['--additional-arg2']

        gitTask.executeCommand()

        def executor = gitTask.executor
        def gitExe = executor.gitExe
        def cmd = executor.command
        def script = executor.script.toString()
        def directory = executor.directory.toString()

        def expected = "\"${script}\" \"${directory}\" ${gitExe} branch \"master\" --additional-arg --additional-arg2".toString()

        assertEquals(expected, cmd)
    }
}
