package xyz.ronella.gradle.plugin.simple.git

import xyz.ronella.gradle.plugin.simple.git.task.GitTask

import java.nio.charset.StandardCharsets

import static org.junit.jupiter.api.Assertions.*

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GitTaskTest {
    private Project project

    @BeforeEach
    void initProject() {
        project = ProjectBuilder.builder().build()
        project.pluginManager.apply 'xyz.ronella.simple-git'
        project.extensions.simple_git.verbose = true
        project.extensions.simple_git.noop = true
    }

    @Test
    void testCommand() {
        def gitTask = project.tasks.gitTask
        gitTask.directory = new File("D:/dev/tmp/simple-git")
        gitTask.executeCommand()

        def executor = gitTask.executor
        def gitExe = executor.gitExe
        def cmd = executor.command
        def script = executor.script.toString()

        assertEquals("\"${script}\" \"D:\\dev\\tmp\\simple-git\" ${gitExe} --help".toString(), cmd)
    }

    @Test
    void testVerbose() {
        assertTrue(project.extensions.simple_git.verbose.get())
    }

    @Test
    void testNoop() {
        assertTrue(project.extensions.simple_git.noop.get())
    }

    @Test
    void testNoGitInstalled() {

        def gitTask = project.tasks.gitTask
        project.extensions.simple_git.noop = false
        def testExt = project.extensions.simple_git_test
        testExt.no_git_installed = true

        gitTask.executeCommand()

        assertEquals("git.exe not found. Please install git application and try again.", testExt.test_message)
    }

    @Test
    void testEmptyEncodedUsername() {
        def gitTask = (GitTask) project.tasks.gitTask
        assertFalse(gitTask.encodedUsername.isPresent())
    }

    @Test
    void testEmptyEncodedUsernameFromExt() {
        def gitTask = (GitTask) project.tasks.gitTask
        def ext = project.extensions.simple_git
        ext.username = "ExtUsername"
        def expected = URLEncoder.encode(ext.username.get(), StandardCharsets.UTF_8)
        assertEquals(expected, gitTask.encodedUsername.get())
    }

    @Test
    void testEmptyEncodedUsernameFromExtAndTask() {
        def gitTask = (GitTask) project.tasks.gitTask
        def ext = project.extensions.simple_git
        gitTask.username = "TaskUsername"
        ext.username = "ExtUsername"
        def expected = URLEncoder.encode(gitTask.username.get(), StandardCharsets.UTF_8)
        assertEquals(expected, gitTask.encodedUsername.get())
    }

    @Test
    void testEmptyEncodedUsernameTask() {
        def gitTask = (GitTask) project.tasks.gitTask
        gitTask.username = "TaskUsername"
        def expected = URLEncoder.encode(gitTask.username.get(), StandardCharsets.UTF_8)
        assertEquals(expected, gitTask.encodedUsername.get())
    }

    @Test
    void testEmptyEncodedPassword() {
        def gitTask = (GitTask) project.tasks.gitTask
        assertFalse(gitTask.encodedPassword.isPresent())
    }

    @Test
    void testEmptyEncodedPasswordFromExt() {
        def gitTask = (GitTask) project.tasks.gitTask
        def ext = project.extensions.simple_git
        ext.password = "ExtPassword"
        def expected = URLEncoder.encode(ext.password.get(), StandardCharsets.UTF_8)
        assertEquals(expected, gitTask.encodedPassword.get())
    }

    @Test
    void testEmptyEncodedPasswordFromExtAndTask() {
        def gitTask = (GitTask) project.tasks.gitTask
        def ext = project.extensions.simple_git
        gitTask.password = "TaskPassword"
        ext.password = "ExtPassword"
        def expected = URLEncoder.encode(gitTask.password.get(), StandardCharsets.UTF_8)
        assertEquals(expected, gitTask.encodedPassword.get())
    }

    @Test
    void testEmptyEncodedPasswordTask() {
        def gitTask = (GitTask) project.tasks.gitTask
        gitTask.password = "TaskPassword"
        def expected = URLEncoder.encode(gitTask.password.get(), StandardCharsets.UTF_8)
        assertEquals(expected, gitTask.encodedPassword.get())
    }

    @Test
    void testNoCredUrlInsert() {
        def gitTask = (GitTask) project.tasks.gitTask
        def expected = "https://git.com/dummy"
        assertEquals(expected, gitTask.insertCredToURL(expected))
    }

    @Test
    void testCredUsernameOnlyUrlInsert() {
        def gitTask = (GitTask) project.tasks.gitTask
        gitTask.username = "username"
        def url = "https://git.com/dummy"
        def expected = "https://username@git.com/dummy"
        assertEquals(expected, gitTask.insertCredToURL(url))
    }

    @Test
    void testCredUsernamePasswordUrlInsert() {
        def gitTask = (GitTask) project.tasks.gitTask
        gitTask.username = "username"
        gitTask.password = "password"
        def url = "https://git.com/dummy"
        def expected = "https://username:password@git.com/dummy"
        assertEquals(expected, gitTask.insertCredToURL(url))
    }

    @Test
    void testCredPasswordOnlyUrlInsert() {
        def gitTask = (GitTask) project.tasks.gitTask
        gitTask.password = "password"
        def expected = "https://git.com/dummy"
        assertEquals(expected, gitTask.insertCredToURL(expected))
    }

}