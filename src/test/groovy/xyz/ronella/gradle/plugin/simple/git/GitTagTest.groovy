package xyz.ronella.gradle.plugin.simple.git

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

class GitTagTest {

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
        def gitTask = project.tasks.gitTag

        gitTask.executeCommand()

        def executor = gitTask.executor
        def gitExe = executor.gitExe
        def cmd = executor.command
        def script = executor.script.toString()
        def directory = executor.directory.toString()

        assertEquals("\"${script}\" \"${directory}\" ${gitExe} tag".toString(), cmd)
    }

    @Test
    void testDirectory() {
        def gitTask = project.tasks.gitTag

        gitTask.directory=new File('D:\\dev\\tmp\\simple-git')

        gitTask.executeCommand()

        def executor = gitTask.executor
        def gitExe = executor.gitExe
        def cmd = executor.command
        def script = executor.script.toString()

        assertEquals("\"${script}\" \"D:\\dev\\tmp\\simple-git\" ${gitExe} tag".toString(), cmd)
    }

    @Test
    void testDefaultDirectory() {
        project.extensions.simple_git.directory = project.file('D:\\dev\\test\\simple-git')
        def gitTask = project.tasks.gitTag

        gitTask.executeCommand()

        def executor = gitTask.executor
        def gitExe = executor.gitExe
        def cmd = executor.command
        def script = executor.script.toString()

        assertEquals("\"${script}\" \"D:\\dev\\test\\simple-git\" ${gitExe} tag".toString(), cmd)
    }

    @Test
    void testTagZargs() {
        def gitTask = project.tasks.gitTag

        gitTask.zargs.add('-zargs')

        gitTask.executeCommand()
        def executor = gitTask.executor
        def gitExe = executor.gitExe
        def cmd = executor.command
        def script = executor.script.toString()
        def directory = executor.directory.toString()

        assertEquals("\"${script}\" \"${directory}\" ${gitExe} tag -zargs".toString(), cmd)
    }

}
