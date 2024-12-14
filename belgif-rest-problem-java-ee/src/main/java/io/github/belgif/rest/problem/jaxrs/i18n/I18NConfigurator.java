package io.github.belgif.rest.problem.jaxrs.i18n;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import io.github.belgif.rest.problem.i18n.I18N;

/**
 * ServletContextListener that enables/disables I18N based on
 * "io.github.belgif.rest.problem.i18n" web.xml context param.
 */
@WebListener
public class I18NConfigurator implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        if (sce.getServletContext().getInitParameter(I18N.I18N_FLAG) != null) {
            I18N.setEnabled(Boolean.parseBoolean(sce.getServletContext().getInitParameter(I18N.I18N_FLAG)));
        }
    }

}
