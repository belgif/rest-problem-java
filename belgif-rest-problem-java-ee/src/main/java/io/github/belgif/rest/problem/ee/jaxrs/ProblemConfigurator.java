package io.github.belgif.rest.problem.ee.jaxrs;

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
        ProblemConfig.setI18nEnabled(getBoolean(sce, ProblemConfig.PROPERTY_I18N_ENABLED,
                ProblemConfig.DEFAULT_I18N_ENABLED));
        ProblemConfig.setExtIssueTypesEnabled(getBoolean(sce, ProblemConfig.PROPERTY_EXT_ISSUE_TYPES_ENABLED,
                ProblemConfig.DEFAULT_EXT_ISSUE_TYPES_ENABLED));
        ProblemConfig.setExtInputsArrayEnabled(getBoolean(sce, ProblemConfig.PROPERTY_EXT_INPUTS_ARRAY_ENABLED,
                ProblemConfig.DEFAULT_EXT_INPUTS_ARRAY_ENABLED));
    }

    private boolean getBoolean(ServletContextEvent sce, String key, boolean defaultValue) {
        if (System.getProperty(key) != null) {
            return Boolean.parseBoolean(System.getProperty(key));
        } else if (System.getenv(key) != null) {
            return Boolean.parseBoolean(System.getenv(key));
        } else if (sce.getServletContext().getInitParameter(key) != null) {
            return Boolean.parseBoolean(sce.getServletContext().getInitParameter(key));
        } else {
            return defaultValue;
        }
    }

}
