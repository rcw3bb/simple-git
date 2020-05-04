package xyz.ronella.gradle.plugin.simple.git.task

class GitStatus extends GitTask {

    public GitStatus() {
        super()
        command = 'status'
        forceDirectory = true
    }
}
