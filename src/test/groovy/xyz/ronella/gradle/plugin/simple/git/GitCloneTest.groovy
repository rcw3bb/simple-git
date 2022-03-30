package xyz.ronella.gradle.plugin.simple.git

import xyz.ronella.gradle.plugin.simple.git.exception.MissingRepositoryException

import static org.junit.jupiter.api.Assertions.*

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GitCloneTest {

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
        def gitTask = project.tasks.gitClone

        assertThrows(MissingRepositoryException, {
            gitTask.executeCommand()
        })
    }

    @Test
    void testRepository() {
        def gitTask = project.tasks.gitClone
        gitTask.repository="https://git.com/dummy"

        gitTask.executeCommand()

        def executor = gitTask.executor
        def gitExe = executor.gitExe
        def cmd = executor.command

        assertEquals("${gitExe} clone \"https://git.com/dummy\" \"${project.projectDir.absolutePath}\"".toString(), cmd)
    }

    @Test
    void testBranchRepository() {
        def gitTask = project.tasks.gitClone
        gitTask.repository="https://git.com/dummy"
        gitTask.branch="master"

        gitTask.executeCommand()

        def executor = gitTask.executor
        def gitExe = executor.gitExe
        def cmd = executor.command

        assertEquals("${gitExe} clone --branch \"master\" \"https://git.com/dummy\" \"${project.projectDir.absolutePath}\"".toString(), cmd)
    }

    @Test
    void testDirectory() {
        def gitTask = project.tasks.gitClone
        gitTask.repository="https://git.com/dummy"
        gitTask.branch="master"
        gitTask.directory=new File('C:\\directory')

        gitTask.executeCommand()

        def executor = gitTask.executor
        def gitExe = executor.gitExe
        def cmd = executor.command

        assertEquals("${gitExe} clone --branch \"master\" \"https://git.com/dummy\" \"C:\\directory\"".toString(), cmd)
    }

    @Test
    void testArgs() {
        def gitTask = project.tasks.gitClone
        gitTask.args.add('-c')
        gitTask.args.add('dummy')
        gitTask.repository="https://git.com/dummy"
        gitTask.branch="master"

        gitTask.executeCommand()

        def executor = gitTask.executor
        def gitExe = executor.gitExe
        def cmd = executor.command

        assertEquals("${gitExe} clone -c dummy --branch \"master\" \"https://git.com/dummy\" \"${project.projectDir.absolutePath}\"".toString(), cmd)
    }

    @Test
    void testOptions() {
        def gitTask = project.tasks.gitClone
        gitTask.options.add('-c')
        gitTask.options.add('dummy')
        gitTask.repository="https://git.com/dummy"
        gitTask.branch="master"

        gitTask.executeCommand()

        def executor = gitTask.executor
        def gitExe = executor.gitExe
        def cmd = executor.command

        assertEquals("${gitExe} -c dummy clone --branch \"master\" \"https://git.com/dummy\" \"${project.projectDir.absolutePath}\"".toString(), cmd)
    }

    @Test
    void testZargs() {
        def gitTask = project.tasks.gitClone
        gitTask.options.add('-c')
        gitTask.options.add('dummy')
        gitTask.repository="https://git.com/dummy"
        gitTask.branch="master"
        gitTask.zargs.add('-zargs')

        gitTask.executeCommand()

        def executor = gitTask.executor
        def gitExe = executor.gitExe
        def cmd = executor.command

        assertEquals("${gitExe} -c dummy clone --branch \"master\" \"https://git.com/dummy\" \"${project.projectDir.absolutePath}\" -zargs".toString(), cmd)
    }

}
