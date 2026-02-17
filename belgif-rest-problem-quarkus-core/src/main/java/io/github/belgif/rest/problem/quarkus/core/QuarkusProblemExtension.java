package io.github.belgif.rest.problem.quarkus.core;

import java.util.ArrayList;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.build.compatible.spi.BuildCompatibleExtension;
import jakarta.enterprise.inject.build.compatible.spi.ClassConfig;
import jakarta.enterprise.inject.build.compatible.spi.Enhancement;
import jakarta.enterprise.inject.build.compatible.spi.Parameters;
import jakarta.enterprise.inject.build.compatible.spi.Synthesis;
import jakarta.enterprise.inject.build.compatible.spi.SyntheticBeanCreator;
import jakarta.enterprise.inject.build.compatible.spi.SyntheticComponents;
import jakarta.enterprise.lang.model.declarations.ClassInfo;

import io.github.belgif.rest.problem.api.Problem;
import io.github.belgif.rest.problem.api.ProblemType;
import io.github.belgif.rest.problem.registry.ProblemTypeRegistry;

/**
 * Build compatible CDI extension for dynamic discovery of {@link Problem} classes annotated with @{@link ProblemType}.
 *
 * @see ProblemType
 */
// NOTE: Can be moved to rest-problem-java-ee if we ever increase its baseline Jakarta EE 10
public class QuarkusProblemExtension implements BuildCompatibleExtension {

    private final List<ClassInfo> problemTypes = new ArrayList<>();

    @Enhancement(types = Problem.class, withSubtypes = true, withAnnotations = ProblemType.class)
    public void detectProblemType(ClassConfig config) {
        problemTypes.add(config.info());
        config.removeAllAnnotations();
    }

    @Synthesis
    public void createProblemTypeRegistry(SyntheticComponents syn) {
        syn.addBean(QuarkusProblemTypeRegistry.class)
                .type(ProblemTypeRegistry.class)
                // name is required to prevent bean from being removed by quarkus
                // (https://quarkus.io/guides/cdi-reference#whats-removed)
                .name("problemTypeRegistry")
                .scope(ApplicationScoped.class)
                .createWith(QuarkusProblemTypeRegistryCreator.class)
                .withParam("problemTypes", problemTypes.toArray(new ClassInfo[0]));
    }

    /**
     * SyntheticBeanCreator for QuarkusProblemTypeRegistry.
     */
    public static class QuarkusProblemTypeRegistryCreator implements SyntheticBeanCreator<QuarkusProblemTypeRegistry> {

        @Override
        public QuarkusProblemTypeRegistry create(Instance<Object> lookup, Parameters params) {
            return new QuarkusProblemTypeRegistry(params.get("problemTypes", Class[].class));
        }

    }

}
