# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Test Commands
- Build plugin: `./gradlew build`
- Run plugin in IDE: `./gradlew runIde`
- Run tests: `./gradlew test`
- Run single test: `./gradlew test --tests "TestClassName.testMethodName"`
- Verify plugin: `./gradlew verifyPlugin`
- Build plugin distribution: `./gradlew buildPlugin`

## Code Style Guidelines
- **Kotlin Standards**: Follow Kotlin coding conventions
- **Imports**: Organize imports, avoid wildcard imports
- **Naming**: Use camelCase for variables/methods, PascalCase for classes
- **Error Handling**: Use try-catch with specific error messages, as seen in TestMethodLineMarkerProvider
- **Nullability**: Use safe navigation (?.) and null checks for VirtualFile operations
- **Line Length**: Keep lines under 100 characters
- **Documentation**: Add KDoc comments for public methods and classes
- **Architecture**: Follow IntelliJ Plugin architecture patterns