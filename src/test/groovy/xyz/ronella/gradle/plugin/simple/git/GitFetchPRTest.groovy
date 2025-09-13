package xyz.ronella.gradle.plugin.simple.git

import xyz.ronella.gradle.plugin.simple.git.exception.MissingPullRequestException

import static org.junit.jupiter.api.Assertions.*

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GitFetchPRTest {

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
        def gitTask = project.tasks.gitFetchPR

        assertThrows(MissingPullRequestException, {
            gitTask.executeCommand()
        })
    }

    @Test
    void testRemote() {
        def gitTask = project.tasks.gitFetchPR

        gitTask.remote = "origin"

        assertThrows(MissingPullRequestException, {
            gitTask.executeCommand()
        })
    }

    @Test
    void testPullRequest() {
        def gitTask = project.tasks.gitFetchPR

        def pullRequest = 1
        gitTask.pullRequest = pullRequest

        gitTask.executeCommand()
        def executor = gitTask.executor
        def gitExe = executor.gitExe
        def cmd = executor.command
        def script = executor.script.toString()
        def directory = executor.directory.toString()

        assertEquals("\"${script}\" \"${directory}\" ${gitExe} fetch \"origin\" pull/${pullRequest}/head:pr-${pullRequest}".toString(), cmd)
    }

    @Test
    void testPullRequestZargs() {
        def gitTask = project.tasks.gitFetchPR

        def pullRequest = 1
        gitTask.remote = "origin"
        gitTask.pullRequest = pullRequest
        gitTask.zargs.add('-zargs')

        gitTask.executeCommand()
        def executor = gitTask.executor
        def gitExe = executor.gitExe
        def cmd = executor.command
        def script = executor.script.toString()
        def directory = executor.directory.toString()

        assertEquals("\"${script}\" \"${directory}\" ${gitExe} fetch \"origin\" pull/${pullRequest}/head:pr-${pullRequest} -zargs".toString(), cmd)
    }

    @Test
    void testBitbucketType() {
        project.extensions.simple_git.repoType = 'bitbucket'
        def gitTask = project.tasks.gitFetchPR

        def pullRequest = 1
        gitTask.remote = "origin"
        gitTask.pullRequest = pullRequest

        gitTask.executeCommand()
        def executor = gitTask.executor
        def gitExe = executor.gitExe
        def cmd = executor.command
        def script = executor.script.toString()
        def directory = executor.directory.toString()

        assertEquals("\"${script}\" \"${directory}\" ${gitExe} fetch \"origin\" pull-requests/${pullRequest}/from:pr-${pullRequest}".toString(), cmd)
    }

    @Test
    void testRepoTypeUnknown() {
        project.extensions.simple_git.repoType = 'unknown'
        def gitTask = project.tasks.gitFetchPR

        def pullRequest = 1
        gitTask.pullRequest = pullRequest

        gitTask.executeCommand()
        def executor = gitTask.executor
        def gitExe = executor.gitExe
        def cmd = executor.command
        def script = executor.script.toString()
        def directory = executor.directory.toString()

        assertEquals("\"${script}\" \"${directory}\" ${gitExe} fetch \"origin\" pull/${pullRequest}/head:pr-${pullRequest}".toString(), cmd)
    }

    @Test
    void testCustomPullRequestPattern() {
        project.extensions.simple_git.pullRequestPattern = '%s/%s'
        def gitTask = project.tasks.gitFetchPR

        def pullRequest = 1
        gitTask.pullRequest = pullRequest

        gitTask.executeCommand()
        def executor = gitTask.executor
        def gitExe = executor.gitExe
        def cmd = executor.command
        def script = executor.script.toString()
        def directory = executor.directory.toString()

        assertEquals("\"${script}\" \"${directory}\" ${gitExe} fetch \"origin\" ${pullRequest}/pr-${pullRequest}".toString(), cmd)
    }

    @Test
    void testDefaultRemoteFromExtension() {
        def gitTask = project.tasks.gitFetchPR
        gitTask.pullRequest = 1
        
        // The extension has a default remote of "origin"
        gitTask.executeCommand()
        def executor = gitTask.executor
        def cmd = executor.command
        
        assertTrue(cmd.contains("origin"))
    }

    @Test
    void testProjectPropertiesRemote() {
        project.ext.sg_remote = "upstream"
        def gitTask = project.tasks.create("testFetchPR", xyz.ronella.gradle.plugin.simple.git.task.GitFetchPR)
        assertEquals("upstream", gitTask.remote.get())
    }

    @Test
    void testProjectPropertiesPullRequest() {
        project.ext.sg_pull_request = "123"
        def gitTask = project.tasks.create("testFetchPR", xyz.ronella.gradle.plugin.simple.git.task.GitFetchPR)
        assertEquals(123, gitTask.pullRequest.get())
    }

    @Test
    void testEnumRepoTypePatternGitHub() {
        def pattern = xyz.ronella.gradle.plugin.simple.git.task.GitFetchPR.EnumRepoTypePattern.GITHUB
        def result = pattern.getPattern(123L, "pr-123")
        assertEquals("pull/123/head:pr-123", result)
    }

    @Test
    void testEnumRepoTypePatternBitbucket() {
        def pattern = xyz.ronella.gradle.plugin.simple.git.task.GitFetchPR.EnumRepoTypePattern.BITBUCKET
        def result = pattern.getPattern(456L, "pr-456")
        assertEquals("pull-requests/456/from:pr-456", result)
    }

    @Test
    void testEnumRepoTypePatternOfGitHub() {
        def pattern = xyz.ronella.gradle.plugin.simple.git.task.GitFetchPR.EnumRepoTypePattern.of("github")
        assertEquals(xyz.ronella.gradle.plugin.simple.git.task.GitFetchPR.EnumRepoTypePattern.GITHUB, pattern)
    }

    @Test
    void testEnumRepoTypePatternOfBitbucket() {
        def pattern = xyz.ronella.gradle.plugin.simple.git.task.GitFetchPR.EnumRepoTypePattern.of("bitbucket")
        assertEquals(xyz.ronella.gradle.plugin.simple.git.task.GitFetchPR.EnumRepoTypePattern.BITBUCKET, pattern)
    }

    @Test
    void testEnumRepoTypePatternOfUnknown() {
        def pattern = xyz.ronella.gradle.plugin.simple.git.task.GitFetchPR.EnumRepoTypePattern.of("unknown")
        assertEquals(xyz.ronella.gradle.plugin.simple.git.task.GitFetchPR.EnumRepoTypePattern.GITHUB, pattern)
    }

    @Test
    void testGetAllArgsWithZargs() {
        def gitTask = project.tasks.gitFetchPR
        gitTask.remote = "origin"
        gitTask.pullRequest = 1
        gitTask.zargs = ["--force"]
        
        def allArgs = gitTask.getAllArgs()
        def argsList = allArgs.get()
        
        // Should contain remote, pull request pattern, and zargs
        assertTrue(argsList.contains("\"origin\""))
        assertTrue(argsList.contains("pull/1/head:pr-1"))
        assertTrue(argsList.contains("--force"))
    }

    @Test
    void testRemoteFromExtension() {
        project.extensions.simple_git.remote = "upstream"
        def gitTask = project.tasks.gitFetchPR
        gitTask.pullRequest = 1
        
        gitTask.executeCommand()
        def executor = gitTask.executor
        def cmd = executor.command
        
        assertTrue(cmd.contains("upstream"))
    }

    @Test
    void testTaskRemoteOverridesExtension() {
        project.extensions.simple_git.remote = "upstream"
        def gitTask = project.tasks.gitFetchPR
        gitTask.remote = "origin"
        gitTask.pullRequest = 1
        
        gitTask.executeCommand()
        def executor = gitTask.executor
        def cmd = executor.command
        
        assertTrue(cmd.contains("origin"))
        assertFalse(cmd.contains("upstream"))
    }
}
