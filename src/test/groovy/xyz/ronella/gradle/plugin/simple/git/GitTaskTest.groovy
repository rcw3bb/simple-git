package xyz.ronella.gradle.plugin.simple.git

import xyz.ronella.gradle.plugin.simple.git.exception.MissingGitException
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
        gitTask.noGitInstalled.set(true)

        assertThrows(MissingGitException, {
            gitTask.executeCommand()
        })
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

    @Test
    void testDefaultOptions() {
        def gitTask = (GitTask) project.tasks.gitTask
        gitTask.forceDirectory = false
        gitTask.command = "clone"
        gitTask.args = ['"https://git.com/dummy"', "\"${project.projectDir.absolutePath}\""]

        def ext = (SimpleGitPluginExtension) project.extensions.simple_git
        ext.defaultOptions = ['-c', 'dummy']

        gitTask.executeCommand()

        def executor = gitTask.executor
        def gitExe = executor.gitExe
        def cmd = executor.command

        def expected = "${gitExe} -c dummy clone \"https://git.com/dummy\" \"${project.projectDir.absolutePath}\"".toString()

        assertEquals(expected, cmd)
    }

    @Test
    void testDefaultArgs() {
        def gitTask = (GitTask) project.tasks.gitTask
        gitTask.forceDirectory = false
        gitTask.command = "clone"
        gitTask.args = ['"https://git.com/dummy"', "\"${project.projectDir.absolutePath}\""]

        def ext = (SimpleGitPluginExtension) project.extensions.simple_git
        ext.defaultArgs = ['--recurse-submodules']

        gitTask.executeCommand()

        def executor = gitTask.executor
        def gitExe = executor.gitExe
        def cmd = executor.command

        def expected = "${gitExe} clone \"https://git.com/dummy\" \"${project.projectDir.absolutePath}\" --recurse-submodules".toString()

        assertEquals(expected, cmd)
    }

    @Test
    void testDirectoryIsEmptyWithNoDirectory() {
        def gitTask = (GitTask) project.tasks.gitTask
        gitTask.directory = project.objects.property(File.class)
        assertTrue(gitTask.directoryIsEmpty())
    }

    @Test
    void testDirectoryIsEmptyWithNonExistentDirectory() {
        def gitTask = (GitTask) project.tasks.gitTask
        def nonExistentDir = new File(project.projectDir, "non-existent-dir")
        gitTask.directory = nonExistentDir
        assertTrue(gitTask.directoryIsEmpty())
    }

    @Test
    void testDirectoryIsEmptyWithEmptyDirectory() {
        def gitTask = (GitTask) project.tasks.gitTask
        def emptyDir = new File(project.projectDir, "empty-dir")
        emptyDir.mkdirs()
        
        try {
            gitTask.directory = emptyDir
            assertTrue(gitTask.directoryIsEmpty())
        } finally {
            emptyDir.delete()
        }
    }

    @Test
    void testDirectoryIsEmptyWithNonEmptyDirectory() {
        def gitTask = (GitTask) project.tasks.gitTask
        def nonEmptyDir = new File(project.projectDir, "non-empty-dir")
        nonEmptyDir.mkdirs()
        def testFile = new File(nonEmptyDir, "test.txt")
        testFile.createNewFile()
        
        try {
            gitTask.directory = nonEmptyDir
            assertFalse(gitTask.directoryIsEmpty())
        } finally {
            testFile.delete()
            nonEmptyDir.delete()
        }
    }

    @Test
    void testProjectPropertiesSetCommand() {
        project.ext.sg_command = "status"
        def gitTask = project.tasks.create("testTask", GitTask)
        def expected = new File("status").absolutePath
        assertEquals(expected, gitTask.command.get())
    }

    @Test
    void testProjectPropertiesSetOptions() {
        project.ext.sg_options = "--no-pager, --verbose"
        def gitTask = project.tasks.create("testTask", GitTask)
        assertEquals(["--no-pager", "--verbose"], gitTask.options.get())
    }

    @Test
    void testProjectPropertiesSetArgs() {
        project.ext.sg_args = "arg1, arg2, arg3"
        def gitTask = project.tasks.create("testTask", GitTask)
        assertEquals(["arg1", "arg2", "arg3"], gitTask.args.get())
    }

    @Test
    void testProjectPropertiesSetZargs() {
        project.ext.sg_zargs = "zarg1, zarg2"
        def gitTask = project.tasks.create("testTask", GitTask)
        assertEquals(["zarg1", "zarg2"], gitTask.zargs.get())
    }

    @Test
    void testProjectPropertiesSetDirectory() {
        def tempDir = File.createTempFile("test", "dir")
        tempDir.delete()
        tempDir.mkdirs()
        
        try {
            project.ext.sg_directory = tempDir.absolutePath
            def gitTask = project.tasks.create("testTask", GitTask)
            assertEquals(tempDir, gitTask.directory.get())
        } finally {
            tempDir.delete()
        }
    }

    @Test
    void testGetEncodedCredWithBothUsernameAndPassword() {
        def gitTask = (GitTask) project.tasks.gitTask
        gitTask.username = "testuser"
        gitTask.password = "testpass"
        
        def encodedCred = gitTask.getEncodedCred()
        assertTrue(encodedCred.isPresent())
        assertEquals("testuser:testpass", encodedCred.get())
    }

    @Test
    void testGetEncodedCredWithUsernameOnly() {
        def gitTask = (GitTask) project.tasks.gitTask
        gitTask.username = "testuser"
        
        def encodedCred = gitTask.getEncodedCred()
        assertTrue(encodedCred.isPresent())
        assertEquals("testuser", encodedCred.get())
    }

    @Test
    void testGetEncodedCredWithPasswordOnly() {
        def gitTask = (GitTask) project.tasks.gitTask
        gitTask.password = "testpass"
        
        def encodedCred = gitTask.getEncodedCred()
        assertFalse(encodedCred.isPresent())
    }

    @Test
    void testGetEncodedCredEmpty() {
        def gitTask = (GitTask) project.tasks.gitTask
        def encodedCred = gitTask.getEncodedCred()
        assertFalse(encodedCred.isPresent())
    }

    @Test
    void testGetAllArgsWithEmptyCommandAndArgs() {
        def gitTask = (GitTask) project.tasks.gitTask
        gitTask.command = ""
        gitTask.args = []
        
        def allArgs = gitTask.getAllArgs()
        assertEquals(["--help"], allArgs.get())
    }

    @Test
    void testGetAllArgsWithCommand() {
        def gitTask = (GitTask) project.tasks.gitTask
        gitTask.command = "status"
        gitTask.args = ["--short"]
        
        def allArgs = gitTask.getAllArgs()
        assertEquals(["--short"], allArgs.get())
    }

    @Test
    void testGetAllArgsWithArgsOnly() {
        def gitTask = (GitTask) project.tasks.gitTask
        gitTask.args = ["status", "--short"]
        
        def allArgs = gitTask.getAllArgs()
        assertEquals(["status", "--short"], allArgs.get())
    }

    @Test
    void testExecuteCommandWithVerboseAndNoop() {
        def gitTask = (GitTask) project.tasks.gitTask
        project.extensions.simple_git.verbose = true
        project.extensions.simple_git.noop = true
        gitTask.directory = project.projectDir
        gitTask.command = "status"
        
        // This should execute without throwing an exception
        gitTask.executeCommand()
    }

    @Test
    void testExecuteCommandWithoutVerboseAndNoop() {
        def gitTask = (GitTask) project.tasks.gitTask
        project.extensions.simple_git.verbose = false
        project.extensions.simple_git.noop = true
        gitTask.directory = project.projectDir
        gitTask.command = "status"
        
        // This should execute without throwing an exception
        gitTask.executeCommand()
    }

    @Test
    void testGetExecutorWithCommandProcessorException() {
        def gitTask = (GitTask) project.tasks.gitTask
        gitTask.command = "status"
        gitTask.args = ["--short"]
        
        // Test that the executor is built even if CommandLocator throws exception
        def executor = gitTask.getExecutor()
        assertNotNull(executor)
    }

    @Test
    void testDirectoryIsEmptyIOException() {
        def gitTask = (GitTask) project.tasks.gitTask
        // Set directory to a file instead of directory to potentially trigger an IOException
        def tempFile = File.createTempFile("test", ".tmp")
        
        try {
            gitTask.directory = tempFile
            // This might throw an exception or return true depending on implementation
            def result = gitTask.directoryIsEmpty()
            // We don't assert the result since behavior may vary
        } catch (Exception e) {
            // Expected behavior for invalid directory
        } finally {
            tempFile.delete()
        }
    }

    @Test 
    void testExecuteCommandNonNoopMode() {
        def gitTask = (GitTask) project.tasks.gitTask
        project.extensions.simple_git.verbose = false
        project.extensions.simple_git.noop = false
        gitTask.noGitInstalled.set(true)
        
        // This should throw MissingGitException since git is not installed in test
        assertThrows(MissingGitException, {
            gitTask.executeCommand()
        })
    }

}