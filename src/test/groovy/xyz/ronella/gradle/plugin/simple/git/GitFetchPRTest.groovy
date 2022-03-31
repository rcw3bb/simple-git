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
}
