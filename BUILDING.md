# Building

## Building with Maven

This project is built with maven.

`mvn package`

## Spotless

The source code formatting is kept clean and consistent using [spotless-maven-plugin](https://github.com/diffplug/spotless/tree/main/plugin-maven).

The build enforces correct source code formatting. To automatically apply the correct formatting, run

`mvn spotless:apply`

## Integration tests

This project also contains integration tests for Java EE and Spring Boot.

`mvn verify`

> [!NOTE]
> The Java EE integration test uses a JBoss EAP docker image pulled from registry.redhat.io, which requires [authentication](https://access.redhat.com/RegistryAuthentication).

## Building on GitHub

Each push to GitHub triggers a new GitHub Actions build using the maven-ci-build workflow.

For a build of the main branch, this also publishes the latest documentation to https://belgif.github.io/rest-problem-java/latest/.

## Releasing

> [!WARNING]
> Be sure to pull the latest version of the main branch first!

To release, simply create a tag on the main branch:

```
git tag -a v1.0.0 -m "release v1.0.0"
git push origin v1.0.0
```

This triggers a new GitHub Actions build (maven-release workflow) using the tag as version number and creates a draft release.
On the releases GitHub page, you can edit the release notes, set the pre-release checkbox if applicable, and publish the release.

The documentation is published to `https://belgif.github.io/rest-problem-java/<tag>/`.

The GitHub Action also publishes the artifacts to the Maven Central repository. If the release build fails for some reason, you can restart it by re-tagging:

```
git tag -d v1.0.0
git push --delete origin v1.0.0
git tag -a v1.0.0 -m "release v1.0.0" 
git push origin v1.0.0
```

On GitHub, complete the changelog and publish the drafted release.

## Versioning

Artifacts are released using semantic versioning.
