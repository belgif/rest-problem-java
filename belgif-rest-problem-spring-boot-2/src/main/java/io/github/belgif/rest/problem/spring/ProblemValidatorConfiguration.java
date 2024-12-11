package io.github.belgif.rest.problem.spring;

import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.validation.MessageInterpolatorFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

/**
 * Registers a LocalValidatorFactoryBean with the AnnotationParameterNameDiscoverer.
 *
 * @see LocalValidatorFactoryBean
 * @see AnnotationParameterNameDiscoverer
 * @see org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration
 */
@Configuration
@AutoConfigureBefore(ValidationAutoConfiguration.class)
public class ProblemValidatorConfiguration {

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @ConditionalOnMissingBean(Validator.class)
    @ConditionalOnClass(ConstraintViolationException.class)
    public LocalValidatorFactoryBean defaultValidator(ApplicationContext applicationContext) {
        LocalValidatorFactoryBean factoryBean = new LocalValidatorFactoryBean();
        MessageInterpolatorFactory interpolatorFactory = new MessageInterpolatorFactory(applicationContext);
        factoryBean.setMessageInterpolator(interpolatorFactory.getObject());
        factoryBean.setParameterNameDiscoverer(new AnnotationParameterNameDiscoverer());
        return factoryBean;
    }

}
