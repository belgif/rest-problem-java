# rest-problem-java

[![maven-ci-build](https://github.com/belgif/rest-problem-java/actions/workflows/maven-ci-build.yml/badge.svg)](https://github.com/belgif/rest-problem-java/actions/workflows/maven-ci-build.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=belgif_rest-problem-java&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=belgif_rest-problem-java)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=belgif_rest-problem-java&metric=coverage)](https://sonarcloud.io/summary/new_code?id=belgif_rest-problem-java)

Java library for [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457) Problems with support for standard problem types of
the [Belgif REST guide](https://www.belgif.be/specification/rest/api-guide/#error-handling).

With this library, RFC 9457 Problems can be treated as standard java exceptions:

* the server side can throw `io.github.belgif.rest.problem.api.Problem` exceptions (or subclasses), which are
  transparently converted to RFC 9457 compliant "application/problem+json" responses
* the client side can catch `io.github.belgif.rest.problem.api.Problem` exceptions (or subclasses), which are
  transparently thrown when an RFC 9457 compliant "application/problem+json" response is received

## build process

The build process is documented [here](https://github.com/belgif/rest-problem-java/blob/master/BUILDING.md).
