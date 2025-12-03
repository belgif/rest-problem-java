package io.github.belgif.rest.problem.it;

import org.junit.jupiter.api.BeforeEach;

import com.acme.custom.CustomProblem;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.cfg.PackageVersion;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.ProblemModule;
import io.github.belgif.rest.problem.TooManyRequestsProblem;
import io.github.belgif.rest.problem.api.Problem;

public class AbstractJackson2SerializationTest extends AbstractJacksonSerializationTest {
    @Override
    protected JacksonWrapper createJacksonWrapper() {
        return new JacksonWrapper() {

            ObjectMapper mapper;

            @Override
            public String getVersion() {
                return PackageVersion.VERSION.toString();
            }

            @Override
            public void createMapper(Object module) {
                mapper = new ObjectMapper();
                mapper.enable(SerializationFeature.INDENT_OUTPUT);
                if (module == null) {
                    return;
                }
                if (module instanceof ProblemModule) {
                    ProblemModule problemModule = (ProblemModule) module;
                    mapper.registerModule(problemModule);
                } else {
                    throw new RuntimeException("Unable to cast problem module");
                }
            }

            @Override
            public Problem readProblem(String json) throws JsonProcessingException {
                return mapper.readValue(json, Problem.class);
            }

            @Override
            public String writeValueAsString(Problem problem) throws JsonProcessingException {
                return mapper.writeValueAsString(problem);
            }
        };
    }

    @Override
    @BeforeEach
    protected void setUp() {
        TestProblemTypeRegistry registry = new TestProblemTypeRegistry();
        registry.registerProblemType(BadRequestProblem.class, CustomProblem.class, TooManyRequestsProblem.class);
        jacksonWrapper.createMapper(new ProblemModule(registry));
    }
}
