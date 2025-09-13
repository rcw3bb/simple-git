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

    @Test
    void testDefaultBranchFromExtension() {
        def gitTask = project.tasks.gitBranch
        
        // The extension has a default branch of "master"
        gitTask.executeCommand()
        def executor = gitTask.executor
        def cmd = executor.command
        
        assertTrue(cmd.contains("master"))
    }

    @Test
    void testProjectPropertiesBranch() {
        project.ext.sg_branch = "feature-test"
        def gitTask = project.tasks.create("testBranch", xyz.ronella.gradle.plugin.simple.git.task.GitBranch)
        assertEquals("feature-test", gitTask.branch.get())
    }

    @Test
    void testBranchFromExtension() {
        project.extensions.simple_git.branch = "main"
        def gitTask = project.tasks.gitBranch
        
        gitTask.executeCommand()
        def executor = gitTask.executor
        def cmd = executor.command
        
        assertTrue(cmd.contains("main"))
    }

    @Test
    void testTaskBranchOverridesExtension() {
        project.extensions.simple_git.branch = "main"
        def gitTask = project.tasks.gitBranch
        gitTask.branch = "feature"
        
        gitTask.executeCommand()
        def executor = gitTask.executor
        def cmd = executor.command
        
        assertTrue(cmd.contains("feature"))
        assertFalse(cmd.contains("main"))
    }

    @Test
    void testCalcArgsWithCommand() {
        def gitTask = (GitBranch) project.tasks.gitBranch
        gitTask.command = "status"
        gitTask.args = ["--short"]
        
        def calcArgs = gitTask.calcArgs()
        assertEquals(["--short"], calcArgs.get())
    }

    @Test
    void testCalcArgsWithoutCommand() {
        def gitTask = (GitBranch) project.tasks.gitBranch
        gitTask.command = ""
        gitTask.args = []
        
        def calcArgs = gitTask.calcArgs()
        assertEquals([], calcArgs.get())
    }

    @Test
    void testGetAllArgsWithZargs() {
        def gitTask = project.tasks.gitBranch
        gitTask.branch = "master"
        gitTask.zargs = ["--verbose"]
        
        def allArgs = gitTask.getAllArgs()
        def argsList = allArgs.get()
        
        assertTrue(argsList.contains("\"master\""))
        assertTrue(argsList.contains("--verbose"))
    }

    @Test
    void testGetAllArgsWithoutZargs() {
        def gitTask = project.tasks.gitBranch
        gitTask.branch = "master"
        
        def allArgs = gitTask.getAllArgs()
        def argsList = allArgs.get()
        
        assertTrue(argsList.contains("\"master\""))
    }

    @Test
    void testForceDirectoryTrue() {
        def gitTask = project.tasks.gitBranch
        
        // Verify that forceDirectory is true by default for GitBranch
        assertTrue(gitTask.forceDirectory.get())
    }

    @Test
    void testCommandConvention() {
        def gitTask = project.tasks.gitBranch
        
        // Verify that command is set to 'branch' by default
        assertEquals("branch", gitTask.command.get())
    }

    @Test
    void testInternalZArgsFromExtension() {
        project.extensions.simple_git.defaultBranchArgs = ["--list"]
        def gitTask = project.tasks.create("testBranch", xyz.ronella.gradle.plugin.simple.git.task.GitBranch)
        gitTask.branch = "master"
        
        gitTask.executeCommand()
        def executor = gitTask.executor
        def cmd = executor.command
        
        assertTrue(cmd.contains("--list"))
    }

    @Test
    void testInternalOptionsFromExtension() {
        project.extensions.simple_git.defaultBranchOptions = ["-c", "color.ui=false"]
        def gitTask = project.tasks.create("testBranch", xyz.ronella.gradle.plugin.simple.git.task.GitBranch)
        gitTask.branch = "master"
        
        gitTask.executeCommand()
        def executor = gitTask.executor
        def cmd = executor.command
        
        assertTrue(cmd.contains("-c"))
        assertTrue(cmd.contains("color.ui=false"))
    }
}
