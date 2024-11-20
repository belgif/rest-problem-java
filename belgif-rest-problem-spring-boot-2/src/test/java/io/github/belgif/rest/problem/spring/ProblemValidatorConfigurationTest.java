package io.github.belgif.rest.problem.spring;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

class ProblemValidatorConfigurationTest {

    private final ProblemValidatorConfiguration config = new ProblemValidatorConfiguration();

    @Test
    void defaultValidator() {
        LocalValidatorFactoryBean validator = config.defaultValidator(Mockito.mock(ApplicationContext.class));
        assertThat(validator)
                .extracting("parameterNameDiscoverer")
                .isInstanceOf(AnnotationParameterNameDiscoverer.class);
    }

}
