package io.github.belgif.rest.problem.ee.jaxrs;

import io.github.belgif.rest.problem.config.DisableProblems;
import io.github.belgif.rest.problem.config.EnableProblems;

public class AbstractProblemExceptionMapperTest {

    @EnableProblems
    public static class ProblemsEnabledResource {
    }

    @DisableProblems
    public static class ProblemsDisabledResource {
    }

    public static class DefaultResource {
    }

}
