package xyz.ronella.gradle.plugin.simple.git.exception

import org.gradle.api.GradleException

/**
 * An expected pullRequest parameter was not satisfied.
 *
 * @author Ron Webb
 * @since 2020-05-05
 */
class MissingPullRequestException extends GradleException {
}
