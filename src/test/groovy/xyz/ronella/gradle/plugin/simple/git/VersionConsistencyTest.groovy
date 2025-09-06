package xyz.ronella.gradle.plugin.simple.git

import org.junit.jupiter.api.Test
import static org.junit.jupiter.api.Assertions.assertEquals
import java.nio.file.Files
import java.nio.file.Paths

class VersionConsistencyTest {
    @Test
    void versionInFilesShouldBeEqual() {
        // Read version from gradle.properties
        def gradleProps = Files.readAllLines(Paths.get("gradle.properties"))
        def gradleVersionRaw = gradleProps.find { it.startsWith("version=") }?.split("=")?.last()?.trim()
        // Remove -SNAPSHOT suffix if present for comparison
        def gradleVersion = gradleVersionRaw?.replaceAll("-SNAPSHOT", "")

        // Read version from README.md (line 17)
        def readmeLine = Files.readAllLines(Paths.get("README.md"))[16].trim()
        def matcher = (readmeLine =~ /version \"([\d.]+)\"/)
        def readmeVersion = matcher ? matcher[0][1] : null

    // Read version from CHANGELOG.md (line 3)
    def changelogLine = Files.readAllLines(Paths.get("CHANGELOG.md"))[2].trim()
    def changelogMatcher = (changelogLine =~ /([\d.]+)/)
    def changelogVersion = changelogMatcher ? changelogMatcher[0][1] : null

    assertEquals(gradleVersion, readmeVersion, "gradle.properties and README.md version must match")
    assertEquals(gradleVersion, changelogVersion, "gradle.properties and CHANGELOG.md version must match")
    }
}
