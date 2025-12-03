package io.github.belgif.rest.problem.it;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;

import com.acme.custom.CustomProblem;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.ProblemModuleJackson3;
import io.github.belgif.rest.problem.TooManyRequestsProblem;
import io.github.belgif.rest.problem.api.Problem;
import tools.jackson.core.json.PackageVersion;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

public class AbstractJackson3SerializationTest extends AbstractJacksonSerializationTest {
    @Override
    @BeforeEach
    protected void setUp() {
        TestProblemTypeRegistry registry = new TestProblemTypeRegistry();
        registry.registerProblemType(BadRequestProblem.class, CustomProblem.class, TooManyRequestsProblem.class);
        jacksonWrapper.createMapper(new ProblemModuleJackson3(registry));
    }

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
                JsonMapper.Builder builder = JsonMapper.builder()
                        .enable(SerializationFeature.INDENT_OUTPUT);
                if (module == null) {
                    mapper = builder.build();
                    return;
                }
                if (module instanceof ProblemModuleJackson3) {
                    ProblemModuleJackson3 problemModule = (ProblemModuleJackson3) module;
                    builder.addModule(problemModule);
                    mapper = builder.build();
                    return;
                }
                throw new RuntimeException("Unable to cast problem module");
            }

            @Override
            public Problem readProblem(String json) throws IOException {
                return mapper.readValue(json, Problem.class);
            }

            @Override
            public String writeValueAsString(Problem problem) throws IOException {
                return mapper.writeValueAsString(problem);
            }
        };
    }
}
