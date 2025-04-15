package io.github.belgif.rest.problem.config;

/**
 * Dynamic configuration parameters for the problem module.
 */
public class ProblemConfig {

    public static final String PROPERTY_I18N = "io.github.belgif.rest.problem.i18n";

    public static final String PROPERTY_EXT_ISSUE_TYPES = "io.github.belgif.rest.problem.ext.issue-types";

    public static final String PROPERTY_EXT_INPUTS_ARRAY = "io.github.belgif.rest.problem.ext.inputs-array";

    private static boolean i18nEnabled = true;

    private static boolean extIssueTypesEnabled = false;

    private static boolean extInputsArrayEnabled = false;

    private ProblemConfig() {
    }

    public static boolean isI18nEnabled() {
        return i18nEnabled;
    }

    public static void setI18nEnabled(boolean i18nEnabled) {
        ProblemConfig.i18nEnabled = i18nEnabled;
    }

    public static boolean isExtIssueTypesEnabled() {
        return extIssueTypesEnabled;
    }

    public static void setExtIssueTypesEnabled(boolean extIssueTypesEnabled) {
        ProblemConfig.extIssueTypesEnabled = extIssueTypesEnabled;
    }

    public static boolean isExtInputsArrayEnabled() {
        return extInputsArrayEnabled;
    }

    public static void setExtInputsArrayEnabled(boolean extInputsArrayEnabled) {
        ProblemConfig.extInputsArrayEnabled = extInputsArrayEnabled;
    }

    public static void reset() {
        i18nEnabled = true;
        extIssueTypesEnabled = false;
        extInputsArrayEnabled = false;
    }

}
