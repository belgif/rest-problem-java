package io.github.belgif.rest.problem.ee.jaxrs;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junitpioneer.jupiter.SetEnvironmentVariable;
import org.junitpioneer.jupiter.SetSystemProperty;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.github.belgif.rest.problem.config.ProblemConfig;

@ExtendWith(MockitoExtension.class)
class ProblemConfiguratorTest {

    private final ProblemConfigurator configurator = new ProblemConfigurator();

    @Mock
    private ServletContext servletContext;

    @BeforeEach
    @AfterEach
    void cleanup() {
        ProblemConfig.reset();
    }

    @Test
    void defaults() {
        when(servletContext.getInitParameter(ProblemConfig.PROPERTY_I18N_ENABLED)).thenReturn(null);
        when(servletContext.getInitParameter(ProblemConfig.PROPERTY_EXT_ISSUE_TYPES_ENABLED)).thenReturn(null);
        when(servletContext.getInitParameter(ProblemConfig.PROPERTY_EXT_INPUTS_ARRAY_ENABLED)).thenReturn(null);
        configurator.contextInitialized(new ServletContextEvent(servletContext));
        assertThat(ProblemConfig.isI18nEnabled()).isEqualTo(ProblemConfig.DEFAULT_I18N_ENABLED);
        assertThat(ProblemConfig.isExtIssueTypesEnabled()).isEqualTo(ProblemConfig.DEFAULT_EXT_ISSUE_TYPES_ENABLED);
        assertThat(ProblemConfig.isExtInputsArrayEnabled()).isEqualTo(ProblemConfig.DEFAULT_EXT_INPUTS_ARRAY_ENABLED);
    }

    @Test
    void enabledViaInitParam() {
        when(servletContext.getInitParameter(ProblemConfig.PROPERTY_I18N_ENABLED)).thenReturn("true");
        when(servletContext.getInitParameter(ProblemConfig.PROPERTY_EXT_ISSUE_TYPES_ENABLED)).thenReturn("true");
        when(servletContext.getInitParameter(ProblemConfig.PROPERTY_EXT_INPUTS_ARRAY_ENABLED)).thenReturn("true");
        configurator.contextInitialized(new ServletContextEvent(servletContext));
        assertThat(ProblemConfig.isI18nEnabled()).isTrue();
        assertThat(ProblemConfig.isExtIssueTypesEnabled()).isTrue();
        assertThat(ProblemConfig.isExtInputsArrayEnabled()).isTrue();
    }

    @Test
    void disabledViaInitParam() {
        when(servletContext.getInitParameter(ProblemConfig.PROPERTY_I18N_ENABLED)).thenReturn("false");
        when(servletContext.getInitParameter(ProblemConfig.PROPERTY_EXT_ISSUE_TYPES_ENABLED)).thenReturn("false");
        when(servletContext.getInitParameter(ProblemConfig.PROPERTY_EXT_INPUTS_ARRAY_ENABLED)).thenReturn("false");
        configurator.contextInitialized(new ServletContextEvent(servletContext));
        assertThat(ProblemConfig.isI18nEnabled()).isFalse();
        assertThat(ProblemConfig.isExtIssueTypesEnabled()).isFalse();
        assertThat(ProblemConfig.isExtInputsArrayEnabled()).isFalse();
    }

    @Test
    @SetSystemProperty(key = ProblemConfig.PROPERTY_I18N_ENABLED, value = "true")
    @SetSystemProperty(key = ProblemConfig.PROPERTY_EXT_ISSUE_TYPES_ENABLED, value = "true")
    @SetSystemProperty(key = ProblemConfig.PROPERTY_EXT_INPUTS_ARRAY_ENABLED, value = "true")
    void enabledViaSystemProperties() {
        configurator.contextInitialized(new ServletContextEvent(servletContext));
        assertThat(ProblemConfig.isI18nEnabled()).isTrue();
        assertThat(ProblemConfig.isExtIssueTypesEnabled()).isTrue();
        assertThat(ProblemConfig.isExtInputsArrayEnabled()).isTrue();
        verifyNoInteractions(servletContext);
    }

    @Test
    @SetSystemProperty(key = ProblemConfig.PROPERTY_I18N_ENABLED, value = "false")
    @SetSystemProperty(key = ProblemConfig.PROPERTY_EXT_ISSUE_TYPES_ENABLED, value = "false")
    @SetSystemProperty(key = ProblemConfig.PROPERTY_EXT_INPUTS_ARRAY_ENABLED, value = "false")
    void disabledViaSystemProperty() {
        configurator.contextInitialized(new ServletContextEvent(servletContext));
        assertThat(ProblemConfig.isI18nEnabled()).isFalse();
        assertThat(ProblemConfig.isExtIssueTypesEnabled()).isFalse();
        assertThat(ProblemConfig.isExtInputsArrayEnabled()).isFalse();
        verifyNoInteractions(servletContext);
    }

    @Test
    @SetEnvironmentVariable(key = ProblemConfig.PROPERTY_I18N_ENABLED, value = "true")
    @SetEnvironmentVariable(key = ProblemConfig.PROPERTY_EXT_ISSUE_TYPES_ENABLED, value = "true")
    @SetEnvironmentVariable(key = ProblemConfig.PROPERTY_EXT_INPUTS_ARRAY_ENABLED, value = "true")
    void enabledViaEnvironmentVariable() {
        configurator.contextInitialized(new ServletContextEvent(servletContext));
        assertThat(ProblemConfig.isI18nEnabled()).isTrue();
        assertThat(ProblemConfig.isExtIssueTypesEnabled()).isTrue();
        assertThat(ProblemConfig.isExtInputsArrayEnabled()).isTrue();
        verifyNoInteractions(servletContext);
    }

    @Test
    @SetEnvironmentVariable(key = ProblemConfig.PROPERTY_I18N_ENABLED, value = "false")
    @SetEnvironmentVariable(key = ProblemConfig.PROPERTY_EXT_ISSUE_TYPES_ENABLED, value = "false")
    @SetEnvironmentVariable(key = ProblemConfig.PROPERTY_EXT_INPUTS_ARRAY_ENABLED, value = "false")
    void disabledViaEnvironmentVariable() {
        configurator.contextInitialized(new ServletContextEvent(servletContext));
        assertThat(ProblemConfig.isI18nEnabled()).isFalse();
        assertThat(ProblemConfig.isExtIssueTypesEnabled()).isFalse();
        assertThat(ProblemConfig.isExtInputsArrayEnabled()).isFalse();
        verifyNoInteractions(servletContext);
    }

    @Test
    @SetEnvironmentVariable(key = ProblemConfig.PROPERTY_I18N_ENABLED, value = "false")
    @SetEnvironmentVariable(key = ProblemConfig.PROPERTY_EXT_ISSUE_TYPES_ENABLED, value = "false")
    @SetEnvironmentVariable(key = ProblemConfig.PROPERTY_EXT_INPUTS_ARRAY_ENABLED, value = "false")
    @SetSystemProperty(key = ProblemConfig.PROPERTY_I18N_ENABLED, value = "true")
    @SetSystemProperty(key = ProblemConfig.PROPERTY_EXT_ISSUE_TYPES_ENABLED, value = "true")
    @SetSystemProperty(key = ProblemConfig.PROPERTY_EXT_INPUTS_ARRAY_ENABLED, value = "true")
    void systemPropertyHasPrecedenceOverEnvironmentVariable() {
        configurator.contextInitialized(new ServletContextEvent(servletContext));
        assertThat(ProblemConfig.isI18nEnabled()).isTrue();
        assertThat(ProblemConfig.isExtIssueTypesEnabled()).isTrue();
        assertThat(ProblemConfig.isExtInputsArrayEnabled()).isTrue();
        verifyNoInteractions(servletContext);
    }

}
