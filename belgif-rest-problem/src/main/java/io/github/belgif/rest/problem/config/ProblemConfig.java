package io.github.belgif.rest.problem.config;

/**
 * Dynamic configuration parameters for the problem module.
 */
public class ProblemConfig {

    public static final String PROPERTY_I18N = "io.github.belgif.rest.problem.i18n";

    public static final String PROPERTY_EXT_ISSUE_TYPES = "io.github.belgif.rest.problem.ext.issue-types";

    public static final String PROPERTY_EXT_INPUTS_ARRAY = "io.github.belgif.rest.problem.ext.inputs-array";

    public static final boolean DEFAULT_I18N = true;

    public static final boolean DEFAULT_EXT_ISSUE_TYPES = false;

    public static final boolean DEFAULT_EXT_INPUTS_ARRAY = false;

    private static boolean i18nEnabled = DEFAULT_I18N;

    private static boolean extIssueTypesEnabled = DEFAULT_EXT_ISSUE_TYPES;

    private static boolean extInputsArrayEnabled = DEFAULT_EXT_INPUTS_ARRAY;

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
        i18nEnabled = DEFAULT_I18N;
        extIssueTypesEnabled = DEFAULT_EXT_ISSUE_TYPES;
        extInputsArrayEnabled = DEFAULT_EXT_INPUTS_ARRAY;
    }

}
