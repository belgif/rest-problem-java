package io.github.belgif.rest.problem.jaxrs.i18n;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junitpioneer.jupiter.SetEnvironmentVariable;
import org.junitpioneer.jupiter.SetSystemProperty;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.github.belgif.rest.problem.i18n.I18N;

@ExtendWith(MockitoExtension.class)
class I18NConfiguratorTest {

    private final I18NConfigurator configurator = new I18NConfigurator();

    @Mock
    private ServletContext servletContext;

    @AfterEach
    void cleanup() {
        I18N.setEnabled(true);
    }

    @Test
    void enabledViaInitParam() {
        I18N.setEnabled(false);
        when(servletContext.getInitParameter(I18N.I18N_FLAG)).thenReturn("true");
        configurator.contextInitialized(new ServletContextEvent(servletContext));
        assertThat(I18N.isEnabled()).isTrue();
    }

    @Test
    void disabledViaInitParam() {
        when(servletContext.getInitParameter(I18N.I18N_FLAG)).thenReturn("false");
        configurator.contextInitialized(new ServletContextEvent(servletContext));
        assertThat(I18N.isEnabled()).isFalse();
    }

    @Test
    void withoutInitParam() {
        I18N.setEnabled(true);
        when(servletContext.getInitParameter(I18N.I18N_FLAG)).thenReturn(null);
        configurator.contextInitialized(new ServletContextEvent(servletContext));
        assertThat(I18N.isEnabled()).isTrue();
    }

    @Test
    @SetSystemProperty(key = I18N.I18N_FLAG, value = "false")
    void disabledViaSystemProperty() {
        configurator.contextInitialized(new ServletContextEvent(servletContext));
        assertThat(I18N.isEnabled()).isFalse();
        verifyNoInteractions(servletContext);
    }

    @Test
    @SetSystemProperty(key = I18N.I18N_FLAG, value = "true")
    void enabledViaSystemProperties() {
        I18N.setEnabled(false);
        configurator.contextInitialized(new ServletContextEvent(servletContext));
        assertThat(I18N.isEnabled()).isTrue();
        verifyNoInteractions(servletContext);
    }

    @Test
    @SetEnvironmentVariable(key = I18N.I18N_FLAG, value = "false")
    void disabledViaEnvironmentVariable() {
        configurator.contextInitialized(new ServletContextEvent(servletContext));
        assertThat(I18N.isEnabled()).isFalse();
        verifyNoInteractions(servletContext);
    }

    @Test
    @SetEnvironmentVariable(key = I18N.I18N_FLAG, value = "true")
    void enabledViaEnvironmentVariable() {
        I18N.setEnabled(false);
        configurator.contextInitialized(new ServletContextEvent(servletContext));
        assertThat(I18N.isEnabled()).isTrue();
        verifyNoInteractions(servletContext);
    }

    @Test
    @SetSystemProperty(key = I18N.I18N_FLAG, value = "true")
    @SetEnvironmentVariable(key = I18N.I18N_FLAG, value = "false")
    void systemPropertyHasPrecedenceOverEnvironmentVariable() {
        I18N.setEnabled(false);
        configurator.contextInitialized(new ServletContextEvent(servletContext));
        assertThat(I18N.isEnabled()).isTrue();
        verifyNoInteractions(servletContext);
    }

}
