package io.github.belgif.rest.problem.spring;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ReflectionUtils;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

class ProblemValidatorConfigurationTest {

    private ProblemValidatorConfiguration config = new ProblemValidatorConfiguration();

    @Test
    void defaultValidator() throws Exception {
        LocalValidatorFactoryBean validator = config.defaultValidator(Mockito.mock(ApplicationContext.class));
        Object parameterNameDiscoverer = ReflectionUtils
                .tryToReadFieldValue(LocalValidatorFactoryBean.class, "parameterNameDiscoverer", validator).get();
        assertThat(parameterNameDiscoverer).isInstanceOf(AnnotationParameterNameDiscoverer.class);
    }

}
