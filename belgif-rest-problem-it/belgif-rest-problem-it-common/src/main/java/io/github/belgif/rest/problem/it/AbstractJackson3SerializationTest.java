package io.github.belgif.rest.problem.it;

import io.github.belgif.rest.problem.ProblemModuleJackson3;
import io.github.belgif.rest.problem.api.Problem;
import io.github.belgif.rest.problem.registry.ProblemTypeRegistry;
import tools.jackson.core.json.PackageVersion;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

public class AbstractJackson3SerializationTest extends AbstractJacksonSerializationTest {

    private ObjectMapper mapper;

    @Override
    protected void createMapper(ProblemTypeRegistry registry) {
        this.mapper = JsonMapper.builder()
                .enable(SerializationFeature.INDENT_OUTPUT)
                .addModule(new ProblemModuleJackson3(registry))
                .build();
    }

    @Override
    protected String getVersion() {
        return PackageVersion.VERSION.toString();
    }

    @Override
    protected Problem readProblem(String json) {
        return mapper.readValue(json, Problem.class);
    }

    @Override
    protected String writeProblem(Problem problem) {
        return mapper.writeValueAsString(problem);
    }

}
