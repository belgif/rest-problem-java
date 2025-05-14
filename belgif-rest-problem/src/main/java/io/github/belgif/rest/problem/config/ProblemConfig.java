package io.github.belgif.rest.problem.config;

import java.util.logging.Logger;

/**
 * Dynamic configuration parameters for the problem module.
 */
public class ProblemConfig {

    public static final Logger LOG = Logger.getLogger(ProblemConfig.class.getName());

    public static final String PROPERTY_I18N_ENABLED =
            "io.github.belgif.rest.problem.i18n-enabled";

    public static final String PROPERTY_EXT_ISSUE_TYPES_ENABLED =
            "io.github.belgif.rest.problem.ext.issue-types-enabled";

    public static final String PROPERTY_EXT_INPUTS_ARRAY_ENABLED =
            "io.github.belgif.rest.problem.ext.inputs-array-enabled";

    private static final boolean DEFAULT_I18N_ENABLED = true;

    private static final boolean DEFAULT_EXT_ISSUE_TYPES_ENABLED = false;

    private static final boolean DEFAULT_EXT_INPUTS_ARRAY_ENABLED = false;

    private static boolean i18nEnabled = DEFAULT_I18N_ENABLED;

    private static boolean extIssueTypesEnabled = DEFAULT_EXT_ISSUE_TYPES_ENABLED;

    private static boolean extInputsArrayEnabled = DEFAULT_EXT_INPUTS_ARRAY_ENABLED;

    private ProblemConfig() {
    }

    public static boolean isI18nEnabled() {
        return i18nEnabled;
    }

    public static void setI18nEnabled(boolean i18nEnabled) {
        LOG.info("setI18nEnabled: " + i18nEnabled);
        ProblemConfig.i18nEnabled = i18nEnabled;
    }

    public static boolean isExtIssueTypesEnabled() {
        return extIssueTypesEnabled;
    }

    public static void setExtIssueTypesEnabled(boolean extIssueTypesEnabled) {
        LOG.info("setExtIssueTypesEnabled: " + extIssueTypesEnabled);
        ProblemConfig.extIssueTypesEnabled = extIssueTypesEnabled;
    }

    public static boolean isExtInputsArrayEnabled() {
        return extInputsArrayEnabled;
    }

    public static void setExtInputsArrayEnabled(boolean extInputsArrayEnabled) {
        LOG.info("setExtInputsArrayEnabled: " + extInputsArrayEnabled);
        ProblemConfig.extInputsArrayEnabled = extInputsArrayEnabled;
    }

    public static void reset() {
        LOG.info("Problem config reset");
        i18nEnabled = DEFAULT_I18N_ENABLED;
        extIssueTypesEnabled = DEFAULT_EXT_ISSUE_TYPES_ENABLED;
        extInputsArrayEnabled = DEFAULT_EXT_INPUTS_ARRAY_ENABLED;
    }

}
