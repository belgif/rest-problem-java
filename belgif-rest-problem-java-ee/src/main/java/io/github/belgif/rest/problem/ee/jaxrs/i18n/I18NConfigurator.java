package io.github.belgif.rest.problem.ee.jaxrs.i18n;

import static io.github.belgif.rest.problem.i18n.I18N.*;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import io.github.belgif.rest.problem.i18n.I18N;

/**
 * ServletContextListener that enables/disables I18N based on the "io.github.belgif.rest.problem.i18n"
 * parameter, which is resolved from following configuration locations in order:
 * <ol>
 * <li>System property</li>
 * <li>Environment variable</li>
 * <li>web.xml context param</li>
 * </ol>
 */
@WebListener
public class I18NConfigurator implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        if (System.getProperty(I18N_FLAG) != null) {
            I18N.setEnabled(Boolean.parseBoolean(System.getProperty(I18N_FLAG)));
        } else if (System.getenv(I18N_FLAG) != null) {
            I18N.setEnabled(Boolean.parseBoolean(System.getenv(I18N_FLAG)));
        } else if (sce.getServletContext().getInitParameter(I18N_FLAG) != null) {
            I18N.setEnabled(Boolean.parseBoolean(sce.getServletContext().getInitParameter(I18N_FLAG)));
        }
    }

}
