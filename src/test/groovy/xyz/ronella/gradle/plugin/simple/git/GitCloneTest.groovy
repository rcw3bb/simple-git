package xyz.ronella.gradle.plugin.simple.git

import xyz.ronella.gradle.plugin.simple.git.exception.MissingRepositoryException

import static org.junit.jupiter.api.Assertions.*

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GitCloneTest {

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
        def gitTask = project.tasks.gitClone

        assertThrows(MissingRepositoryException, {
            gitTask.executeCommand()
        })
    }

    @Test
    public void testRepository() {
        def gitTask = project.tasks.gitClone
        gitTask.repository="https://git.com/dummy"

        gitTask.executeCommand()

        def executor = gitTask.executor
        def gitExe = executor.gitExe
        def cmd = executor.command

        assertEquals("${gitExe} clone \"${gitTask.repository}\" \"${project.projectDir.absolutePath}\"".toString(), cmd)
    }

    @Test
    public void testBranchRepository() {
        def gitTask = project.tasks.gitClone
        gitTask.repository="https://git.com/dummy"
        gitTask.branch="master"

        gitTask.executeCommand()

        def executor = gitTask.executor
        def gitExe = executor.gitExe
        def cmd = executor.command

        assertEquals("${gitExe} clone --branch \"${gitTask.branch}\" \"${gitTask.repository}\" \"${project.projectDir.absolutePath}\"".toString(), cmd)
    }

    @Test
    public void testDirectory() {
        def gitTask = project.tasks.gitClone
        gitTask.repository="https://git.com/dummy"
        gitTask.branch="master"
        gitTask.directory=new File('C:\\directory')

        gitTask.executeCommand()

        def executor = gitTask.executor
        def gitExe = executor.gitExe
        def cmd = executor.command

        assertEquals("${gitExe} clone --branch \"${gitTask.branch}\" \"${gitTask.repository}\" \"C:\\directory\"".toString(), cmd)
    }

    @Test
    public void testArgs() {
        def gitTask = project.tasks.gitClone
        gitTask.args+='-c'
        gitTask.args+='dummy'
        gitTask.repository="https://git.com/dummy"
        gitTask.branch="master"

        gitTask.executeCommand()

        def executor = gitTask.executor
        def gitExe = executor.gitExe
        def cmd = executor.command

        assertEquals("${gitExe} clone -c dummy --branch \"${gitTask.branch}\" \"${gitTask.repository}\" \"${project.projectDir.absolutePath}\"".toString(), cmd)
    }

    @Test
    public void testOptions() {
        def gitTask = project.tasks.gitClone
        gitTask.options+='-c'
        gitTask.options+='dummy'
        gitTask.repository="https://git.com/dummy"
        gitTask.branch="master"

        gitTask.executeCommand()

        def executor = gitTask.executor
        def gitExe = executor.gitExe
        def cmd = executor.command

        assertEquals("${gitExe} -c dummy clone --branch \"${gitTask.branch}\" \"${gitTask.repository}\" \"${project.projectDir.absolutePath}\"".toString(), cmd)
    }

}
