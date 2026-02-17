# rest-problem-java

[![maven-ci-build](https://github.com/belgif/rest-problem-java/actions/workflows/maven-ci-build.yml/badge.svg)](https://github.com/belgif/rest-problem-java/actions/workflows/maven-ci-build.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=belgif_rest-problem-java&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=belgif_rest-problem-java)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=belgif_rest-problem-java&metric=coverage)](https://sonarcloud.io/summary/new_code?id=belgif_rest-problem-java)
[![Maven Central Version](https://img.shields.io/maven-central/v/io.github.belgif.rest.problem/belgif-rest-problem?color=green)](https://central.sonatype.com/namespace/io.github.belgif.rest.problem)

Java library for [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457) Problems with support for standard problem types of
the [Belgif REST guide](https://www.belgif.be/specification/rest/api-guide/#error-handling).

With this library, RFC 9457 Problems can be treated as standard java exceptions:

* the server side can throw `io.github.belgif.rest.problem.api.Problem` exceptions (or subclasses), which are
  transparently converted to RFC 9457 compliant "application/problem+json" responses
* the client side can catch `io.github.belgif.rest.problem.api.Problem` exceptions (or subclasses), which are
  transparently thrown when an RFC 9457 compliant "application/problem+json" response is received

## Documentation

Documentation on how to use this library is available on https://belgif.github.io/rest-problem-java.

## Build Process

The build process is documented [here](https://github.com/belgif/rest-problem-java/blob/master/BUILDING.md).

## Maven Central

* Release artifacts are available on Maven Central (https://central.sonatype.com/namespace/io.github.belgif.rest.problem)
* Snapshot artifacts are also available (`latest-SNAPSHOT` for the main branch, `pr<number>-SNAPSHOT` for PR branches):

```xml
<repositories>
  <repository>
    <name>Central Portal Snapshots</name>
    <id>central-portal-snapshots</id>
    <url>https://central.sonatype.com/repository/maven-snapshots/</url>
    <releases>
      <enabled>false</enabled>
    </releases>
    <snapshots>
      <enabled>true</enabled>
    </snapshots>
  </repository>
</repositories>
```
