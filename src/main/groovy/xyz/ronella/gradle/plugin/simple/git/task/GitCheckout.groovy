package xyz.ronella.gradle.plugin.simple.git.task

class GitCheckout extends GitBranch {

    public GitCheckout() {
        super()
        command = 'checkout'
        forceDirectory = true
    }

}
