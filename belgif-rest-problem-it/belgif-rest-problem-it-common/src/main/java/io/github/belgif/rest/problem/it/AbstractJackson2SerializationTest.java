package io.github.belgif.rest.problem.it;

import java.io.IOException;

import com.fasterxml.jackson.core.json.PackageVersion;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;

import io.github.belgif.rest.problem.ProblemModule;
import io.github.belgif.rest.problem.api.Problem;
import io.github.belgif.rest.problem.registry.ProblemTypeRegistry;

public abstract class AbstractJackson2SerializationTest extends AbstractJacksonSerializationTest {

    private ObjectMapper mapper;

    @Override
    protected void createMapper(ProblemTypeRegistry registry) {
        this.mapper = JsonMapper.builder()
                .enable(SerializationFeature.INDENT_OUTPUT)
                .addModule(new ProblemModule(registry))
                .build();
    }

    @Override
    protected String getVersion() {
        return PackageVersion.VERSION.toString();
    }

    @Override
    protected Problem readProblem(String json) throws IOException {
        return mapper.readValue(json, Problem.class);
    }

    @Override
    protected String writeProblem(Problem problem) throws IOException {
        return mapper.writeValueAsString(problem);
    }

}
