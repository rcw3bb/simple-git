# Simple Git Gradle Plugin - AI Coding Guide

This is a Gradle plugin that wraps Git commands as Gradle tasks, providing convenient access to Git operations within Gradle builds.

## Architecture Overview

The plugin follows a layered architecture with clear separation of concerns:

- **Entry Point**: `SimpleGitPlugin.groovy` - Registers all Git tasks and extensions
- **Core Engine**: `GitExecutor.java` - Handles Git command assembly and execution with cross-platform support
- **Task Hierarchy**: `GitTask.groovy` (base) → specialized tasks (`GitClone`, `GitBranch`, etc.)
- **Extensions**: `SimpleGitPluginExtension.groovy` - Plugin configuration properties

## Key Components

### Task Architecture
All Git tasks extend `GitTask` which provides:
- Common properties: `options`, `args`, `command`, `directory`, `forceDirectory`
- OS-aware command execution via `GitExecutor`
- Built-in support for terminal arguments (`zargs`) on specialized tasks

**Task Inheritance Pattern**:
```
GitTask (base)
├── GitClone, GitStatus, GitPull, GitVersion, GitTag
└── GitBranch
    ├── GitCheckout  
    └── GitDeleteBranch
```

### Command Execution Pattern
Git commands follow this structure:
```
<GIT_EXECUTABLE> <GIT_OPTIONS> <GIT_COMMAND> <GIT_ARGUMENTS>
```

Example: `git -c core.quotepath=false branch -D pr-2`

## Development Workflows

### Building and Testing
- **Build**: `.\gradlew jar` (creates shadowed JAR with relocated dependencies)
- **Tests**: `.\gradlew test` (includes PMD checks and JaCoCo coverage)
- **Quality**: PMD analysis with custom rules in `quality/pmd/java/custom.xml`

### Key Build Features
- **Shadow JAR**: Relocates `xyz.ronella.trivial` to avoid conflicts
- **Java 21**: Required for build and runtime
- **Dependency Locking**: All configurations locked for reproducible builds
- **Cross-platform**: Supports Windows/Linux/MacOS via `OSType` detection

## Project-Specific Patterns

### Extension Configuration
Plugin uses `simple_git` extension block for global settings:
```groovy
simple_git {
    username = System.getenv('GIT_USERNAME')
    directory = new File('path/to/repo')
    verbose = true
}
```

### Task Creation Pattern
Tasks can be created three ways:
1. **Built-in tasks**: `gitClone`, `gitStatus`, etc.
2. **Custom GitTask**: `task myTask(type: GitTask) { command = 'status' }`
3. **Specialized types**: `task myClone(type: GitClone) { repository = 'url' }`

### Command Line Integration
All tasks support command-line parameters with `sg_` prefix:
- `.\gradlew gitClone -Psg_repository=https://github.com/user/repo -Psg_directory=C:\tmp`

### Cross-Platform Considerations
- Uses `CommandLocator` for Git executable discovery
- Supports `GIT_HOME` environment variable override
- OS-specific quoting handled by `GitExecutor.quoteString()`

## Testing Patterns

Tests use Gradle TestKit with Spock framework:
- Mock Git execution for unit tests
- Project fixture setup in test resources
- Separate test for each task type with inheritance testing

## Key Files for Understanding
- `src/main/groovy/xyz/ronella/gradle/plugin/simple/git/SimpleGitPlugin.groovy` - Plugin registration
- `src/main/groovy/xyz/ronella/gradle/plugin/simple/git/task/GitTask.groovy` - Base task implementation
- `src/main/java/xyz/ronella/gradle/plugin/simple/git/GitExecutor.java` - Command execution engine
- `build.gradle` - Shadow JAR configuration and dependency management
