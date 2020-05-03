package xyz.ronella.gradle.plugin.task

class GitStatus extends GitTask {

    public GitStatus() {
        super()
        command = 'status'
        forceDirectory = true
    }
}
