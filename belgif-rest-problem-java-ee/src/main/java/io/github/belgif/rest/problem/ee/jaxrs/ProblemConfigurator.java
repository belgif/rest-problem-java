package io.github.belgif.rest.problem.ee.jaxrs;

import java.util.function.Consumer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import io.github.belgif.rest.problem.config.ProblemConfig;

/**
 * ServletContextListener that configures the problem module properties based on the following locations in order:
 * <ol>
 * <li>System property</li>
 * <li>Environment variable</li>
 * <li>web.xml context param</li>
 * </ol>
 */
@WebListener
public class ProblemConfigurator implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        setBooleanConfig(sce, ProblemConfig.PROPERTY_SERVER_SIDE_ENABLED, ProblemConfig::setServerSideEnabled);
        setBooleanConfig(sce, ProblemConfig.PROPERTY_I18N_ENABLED, ProblemConfig::setI18nEnabled);
        setBooleanConfig(sce, ProblemConfig.PROPERTY_STACK_TRACE_ENABLED, ProblemConfig::setStackTraceEnabled);
        setBooleanConfig(sce, ProblemConfig.PROPERTY_EXT_ISSUE_TYPES_ENABLED, ProblemConfig::setExtIssueTypesEnabled);
        setBooleanConfig(sce, ProblemConfig.PROPERTY_EXT_INPUTS_ARRAY_ENABLED, ProblemConfig::setExtInputsArrayEnabled);
    }

    private void setBooleanConfig(ServletContextEvent sce, String key, Consumer<Boolean> configSetter) {
        if (System.getProperty(key) != null) {
            configSetter.accept(Boolean.parseBoolean(System.getProperty(key)));
        } else if (System.getenv(key) != null) {
            configSetter.accept(Boolean.parseBoolean(System.getenv(key)));
        } else if (sce.getServletContext().getInitParameter(key) != null) {
            configSetter.accept(Boolean.parseBoolean(sce.getServletContext().getInitParameter(key)));
        }
    }

}
