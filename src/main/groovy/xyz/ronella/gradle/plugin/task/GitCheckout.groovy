package xyz.ronella.gradle.plugin.task

class GitCheckout extends GitBranch {

    public GitCheckout() {
        super()
        command = 'checkout'
        forceDirectory = true
    }

}
