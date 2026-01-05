package io.github.belgif.rest.problem.config;

/**
 * Dynamic configuration parameters for the problem module.
 */
public class ProblemConfig {

    public static final String PROPERTY_SERVER_SIDE_ENABLED =
            "io.github.belgif.rest.problem.server-side-enabled";

    public static final String PROPERTY_CLIENT_SIDE_ENABLED =
            "io.github.belgif.rest.problem.client-side-enabled";

    public static final String PROPERTY_I18N_ENABLED =
            "io.github.belgif.rest.problem.i18n-enabled";

    public static final String PROPERTY_EXT_ISSUE_TYPES_ENABLED =
            "io.github.belgif.rest.problem.ext.issue-types-enabled";

    public static final String PROPERTY_EXT_INPUTS_ARRAY_ENABLED =
            "io.github.belgif.rest.problem.ext.inputs-array-enabled";

    private static final boolean DEFAULT_SERVER_SIDE_ENABLED = true;

    private static final boolean DEFAULT_CLIENT_SIDE_ENABLED = true;

    private static final boolean DEFAULT_I18N_ENABLED = true;

    private static final boolean DEFAULT_EXT_ISSUE_TYPES_ENABLED = false;

    private static final boolean DEFAULT_EXT_INPUTS_ARRAY_ENABLED = false;

    private static boolean serverSideEnabled = DEFAULT_SERVER_SIDE_ENABLED;

    private static boolean clientSideEnabled = DEFAULT_CLIENT_SIDE_ENABLED;

    private static boolean i18nEnabled = DEFAULT_I18N_ENABLED;

    private static boolean extIssueTypesEnabled = DEFAULT_EXT_ISSUE_TYPES_ENABLED;

    private static boolean extInputsArrayEnabled = DEFAULT_EXT_INPUTS_ARRAY_ENABLED;

    private static final ThreadLocal<Boolean> LOCAL_EXT_ISSUE_TYPES_ENABLED = new InheritableThreadLocal<Boolean>() {
        @Override
        protected Boolean initialValue() {
            return null;
        }
    };

    private static final ThreadLocal<Boolean> LOCAL_EXT_INPUTS_ARRAY_ENABLED = new InheritableThreadLocal<Boolean>() {
        @Override
        protected Boolean initialValue() {
            return null;
        }
    };

    private ProblemConfig() {
    }

    public static boolean isServerSideEnabled() {
        return serverSideEnabled;
    }

    public static void setServerSideEnabled(boolean serverSideEnabled) {
        ProblemConfig.serverSideEnabled = serverSideEnabled;
    }

    public static boolean isClientSideEnabled() {
        return clientSideEnabled;
    }

    public static void setClientSideEnabled(boolean clientSideEnabled) {
        ProblemConfig.clientSideEnabled = clientSideEnabled;
    }

    public static boolean isI18nEnabled() {
        return i18nEnabled;
    }

    public static void setI18nEnabled(boolean i18nEnabled) {
        ProblemConfig.i18nEnabled = i18nEnabled;
    }

    public static boolean isExtIssueTypesEnabled() {
        if (LOCAL_EXT_ISSUE_TYPES_ENABLED.get() != null) {
            return LOCAL_EXT_ISSUE_TYPES_ENABLED.get();
        }
        return extIssueTypesEnabled;
    }

    public static void setExtIssueTypesEnabled(boolean extIssueTypesEnabled) {
        ProblemConfig.extIssueTypesEnabled = extIssueTypesEnabled;
    }

    public static void setLocalExtIssueTypesEnabled(boolean extIssueTypesEnabled) {
        LOCAL_EXT_ISSUE_TYPES_ENABLED.set(extIssueTypesEnabled);
    }

    public static boolean isExtInputsArrayEnabled() {
        if (LOCAL_EXT_INPUTS_ARRAY_ENABLED.get() != null) {
            return LOCAL_EXT_INPUTS_ARRAY_ENABLED.get();
        }
        return extInputsArrayEnabled;
    }

    public static void setExtInputsArrayEnabled(boolean extInputsArrayEnabled) {
        ProblemConfig.extInputsArrayEnabled = extInputsArrayEnabled;
    }

    public static void setLocalExtInputsArrayEnabled(boolean extInputsArrayEnabled) {
        LOCAL_EXT_INPUTS_ARRAY_ENABLED.set(extInputsArrayEnabled);
    }

    public static void clearLocal() {
        LOCAL_EXT_ISSUE_TYPES_ENABLED.remove();
        LOCAL_EXT_INPUTS_ARRAY_ENABLED.remove();
    }

    public static void reset() {
        serverSideEnabled = DEFAULT_SERVER_SIDE_ENABLED;
        clientSideEnabled = DEFAULT_CLIENT_SIDE_ENABLED;
        i18nEnabled = DEFAULT_I18N_ENABLED;
        extIssueTypesEnabled = DEFAULT_EXT_ISSUE_TYPES_ENABLED;
        extInputsArrayEnabled = DEFAULT_EXT_INPUTS_ARRAY_ENABLED;
    }

}
