package xyz.ronella.gradle.plugin.simple.git

import xyz.ronella.gradle.plugin.simple.git.task.GitPull

import static org.junit.jupiter.api.Assertions.*

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GitPullTest {

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
        def gitTask = project.tasks.gitPull

        gitTask.executeCommand()

        def executor = gitTask.executor
        def gitExe = executor.gitExe
        def cmd = executor.command
        def script = executor.script.toString()
        def directory = executor.directory.toString()

        assertEquals("\"${script}\" \"${directory}\" ${gitExe} pull".toString(), cmd)
    }

    @Test
    void testDirectory() {
        def gitTask = project.tasks.gitPull

        gitTask.directory=new File('D:\\dev\\tmp\\simple-git')

        gitTask.executeCommand()

        def executor = gitTask.executor
        def gitExe = executor.gitExe
        def cmd = executor.command
        def script = executor.script.toString()

        assertEquals("\"${script}\" \"D:\\dev\\tmp\\simple-git\" ${gitExe} pull".toString(), cmd)
    }

    @Test
    void testPullDefaultOptions() {
        def gitTask = (GitPull) project.tasks.gitPull

        def ext = (SimpleGitPluginExtension) project.extensions.simple_git
        ext.defaultOptions = ['-c', 'dummy']
        ext.defaultPullOptions = ['-c', 'dummy2']

        gitTask.executeCommand()

        def executor = gitTask.executor
        def gitExe = executor.gitExe
        def cmd = executor.command
        def script = executor.script.toString()
        def directory = executor.directory.toString()

        def expected = "\"${script}\" \"${directory}\" ${gitExe} -c dummy -c dummy2 pull".toString()

        assertEquals(expected, cmd)
    }

    @Test
    void testPullDefaultArgs() {
        def gitTask = (GitPull) project.tasks.gitPull

        def ext = (SimpleGitPluginExtension) project.extensions.simple_git
        ext.defaultArgs = ['--additional-arg']
        ext.defaultPullArgs = ['--additional-arg2']

        gitTask.executeCommand()

        def executor = gitTask.executor
        def gitExe = executor.gitExe
        def cmd = executor.command
        def script = executor.script.toString()
        def directory = executor.directory.toString()

        def expected = "\"${script}\" \"${directory}\" ${gitExe} pull --additional-arg --additional-arg2".toString()

        assertEquals(expected, cmd)
    }
}
