package xyz.ronella.gradle.plugin.simple.git.exception

import org.gradle.api.GradleException

/**
 * The git executable is missing.
 *
 * @author Ron Webb
 * @since 2.2.0
 */
class MissingGitException extends GradleException {

    /**
     * Constructor.
     *
     * @param message The exception message.
     */
    MissingGitException(final String message) {
        super(message)
    }
}
