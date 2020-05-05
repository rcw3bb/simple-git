package xyz.ronella.gradle.plugin.simple.git.task

class GitPull extends GitTask {

    public GitPull() {
        super()
        command = 'pull'
        forceDirectory = true
    }
}
