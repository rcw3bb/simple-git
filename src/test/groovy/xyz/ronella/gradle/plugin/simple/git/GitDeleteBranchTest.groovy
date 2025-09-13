package xyz.ronella.gradle.plugin.simple.git

import xyz.ronella.gradle.plugin.simple.git.exception.MissingBranchException

import static org.junit.jupiter.api.Assertions.*

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GitDeleteBranchTest {
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
        def gitTask = project.tasks.gitDeleteBranch

        assertThrows(MissingBranchException, {
            gitTask.executeCommand()
        })
    }

    @Test
    void testDeleteBranch() {
        def gitTask = project.tasks.gitDeleteBranch
        gitTask.branch = 'master'

        gitTask.executeCommand()
        def executor = gitTask.executor
        def gitExe = executor.gitExe
        def cmd = executor.command
        def script = executor.script.toString()
        def directory = executor.directory.toString()

        assertEquals("\"${script}\" \"${directory}\" ${gitExe} branch -d \"master\"".toString(), cmd)
    }

    @Test
    void testForceDelete() {
        def gitTask = project.tasks.gitDeleteBranch
        gitTask.branch = 'dummy'
        gitTask.force = true

        gitTask.executeCommand()
        def executor = gitTask.executor
        def gitExe = executor.gitExe
        def cmd = executor.command
        def script = executor.script.toString()
        def directory = executor.directory.toString()

        assertEquals("\"${script}\" \"${directory}\" ${gitExe} branch -D \"dummy\"".toString(), cmd)
    }

    @Test
    void testForceDeleteZargs() {
        def gitTask = project.tasks.gitDeleteBranch
        gitTask.branch = 'dummy'
        gitTask.force = true
        gitTask.zargs.add('-zargs')

        gitTask.executeCommand()
        def executor = gitTask.executor
        def gitExe = executor.gitExe
        def cmd = executor.command
        def script = executor.script.toString()
        def directory = executor.directory.toString()

        assertEquals("\"${script}\" \"${directory}\" ${gitExe} branch -D \"dummy\" -zargs".toString(), cmd)
    }

    @Test
    void testProjectPropertiesForce() {
        project.ext.sg_force = "true"
        def gitTask = project.tasks.create("testDeleteBranch", xyz.ronella.gradle.plugin.simple.git.task.GitDeleteBranch)
        assertTrue(gitTask.force.get())
    }

    @Test
    void testProjectPropertiesForceFalse() {
        project.ext.sg_force = "false"
        def gitTask = project.tasks.create("testDeleteBranch", xyz.ronella.gradle.plugin.simple.git.task.GitDeleteBranch)
        assertFalse(gitTask.force.get())
    }

    @Test
    void testGetAllArgsWithArgsCleanup() {
        def gitTask = project.tasks.gitDeleteBranch
        gitTask.branch = 'test-branch'
        gitTask.args = ["\"test-branch\"", "extra-arg"]
        gitTask.force = false
        
        def allArgs = gitTask.getAllArgs()
        def argsList = allArgs.get()
        
        // The quoted branch should be removed from args but added back with delete flag
        assertTrue(argsList.contains("extra-arg"))
        assertTrue(argsList.contains("-d"))
        assertTrue(argsList.contains("\"test-branch\""))
        // The original quoted branch from args should be removed
        assertEquals(1, argsList.count("\"test-branch\""))
    }

    @Test
    void testGetAllArgsWithZargsCleanup() {
        def gitTask = project.tasks.gitDeleteBranch
        gitTask.branch = 'test-branch'
        gitTask.args = ["zarg-to-remove"]
        gitTask.zargs = ["zarg-to-remove", "zarg-to-keep"]
        gitTask.force = false
        
        def allArgs = gitTask.getAllArgs()
        def argsList = allArgs.get()
        
        // zargs items should be removed from args but added back at the end
        assertTrue(argsList.contains("zarg-to-remove"))
        assertTrue(argsList.contains("zarg-to-keep"))
        assertTrue(argsList.contains("-d"))
        assertTrue(argsList.contains("\"test-branch\""))
    }

    @Test
    void testGetAllArgsForceFlag() {
        def gitTask = project.tasks.gitDeleteBranch
        gitTask.branch = 'test-branch'
        gitTask.force = true
        
        def allArgs = gitTask.getAllArgs()
        def argsList = allArgs.get()
        
        assertTrue(argsList.contains("-D"))
        assertFalse(argsList.contains("-d"))
    }

    @Test
    void testGetAllArgsNormalDeleteFlag() {
        def gitTask = project.tasks.gitDeleteBranch
        gitTask.branch = 'test-branch'
        gitTask.force = false
        
        def allArgs = gitTask.getAllArgs()
        def argsList = allArgs.get()
        
        assertTrue(argsList.contains("-d"))
        assertFalse(argsList.contains("-D"))
    }

    @Test
    void testGetAllArgsWithoutZargs() {
        def gitTask = project.tasks.gitDeleteBranch
        gitTask.branch = 'test-branch'
        gitTask.force = false
        
        def allArgs = gitTask.getAllArgs()
        def argsList = allArgs.get()
        
        assertTrue(argsList.contains("-d"))
        assertTrue(argsList.contains("\"test-branch\""))
        assertEquals(2, argsList.size()) // Should only contain -d and branch name
    }

    @Test
    void testInheritsFromGitBranch() {
        def gitTask = project.tasks.gitDeleteBranch
        
        // Verify inheritance - should have GitBranch properties
        assertTrue(gitTask instanceof xyz.ronella.gradle.plugin.simple.git.task.GitBranch)
        assertNotNull(gitTask.branch)
    }

    @Test
    void testDescriptionSet() {
        def gitTask = project.tasks.gitDeleteBranch
        
        assertEquals("A convenience git branch command for deletion.", gitTask.description)
    }

    @Test
    void testMissingBranchExceptionInGetAllArgs() {
        def gitTask = project.tasks.gitDeleteBranch
        // Don't set branch
        
        assertThrows(xyz.ronella.gradle.plugin.simple.git.exception.MissingBranchException, {
            gitTask.getAllArgs()
        })
    }

}
