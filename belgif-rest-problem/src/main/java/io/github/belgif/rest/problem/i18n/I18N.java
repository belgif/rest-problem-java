package io.github.belgif.rest.problem.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

import io.github.belgif.rest.problem.api.Problem;

/**
 * Helper class for I18N (Internationalization).
 */
public class I18N {

    /**
     * The default locale: English.
     */
    private static final Locale DEFAULT_LOCALE = new Locale("en");

    /**
     * The Belgif default resource bundle.
     */
    private static final String DEFAULT_BUNDLE = "io.github.belgif.rest.problem.Messages";

    /**
     * ThreadLocal with the locale of the current request (defaults to English).
     */
    private static final ThreadLocal<Locale> REQUEST_LOCALE = new InheritableThreadLocal<Locale>() {
        @Override
        protected Locale initialValue() {
            return DEFAULT_LOCALE;
        }
    };

    /**
     * Set the locale of the current request.
     *
     * <p>
     * NOTE: The locale is stored in a ThreadLocal that must be cleaned up with {@link #clearRequestLocale()}.
     * </p>
     *
     * @param locale the locale of the current request.
     */
    public static void setRequestLocale(Locale locale) {
        REQUEST_LOCALE.set(locale);
    }

    /**
     * Get the locale of the current request.
     *
     * @return the locale of the current request
     */
    public static Locale getRequestLocale() {
        return REQUEST_LOCALE.get();
    }

    /**
     * Clear the locale of the current request.
     */
    public static void clearRequestLocale() {
        REQUEST_LOCALE.remove();
    }

    /**
     * Get a localized string from the default Belgif resource bundle.
     *
     * @param key the key
     * @param args the optional arguments that will be resolved with {@link String#format(String, Object...)}
     * @return the localized string
     */
    public static String getLocalizedString(String key, Object... args) {
        return getLocalizedString(DEFAULT_BUNDLE, key, args);
    }

    /**
     * Get a localized string from the resource bundle "[context-package].Messages".
     *
     * <p>
     * For example, for context class = com.acme.custom.CustomProblem, the localized string would
     * be loaded from resource bundle "com.acme.custom.Messages".
     * </p>
     *
     * @param context the context class
     * @param key the key
     * @param args the optional arguments that will be resolved with {@link String#format(String, Object...)}
     * @return the localized string
     */
    public static String getLocalizedString(Class<?> context, String key, Object... args) {
        return getLocalizedString(context.getPackage().getName() + ".Messages", key, args);
    }

    private static String getLocalizedString(String bundle, String key, Object... args) {
        return String.format(ResourceBundle.getBundle(bundle, getRequestLocale()).getString(key), args);
    }

    /**
     * Get a localized detail string with key "[problem-class].detail" from the resource bundle
     * "[problem-package].Messages".
     *
     * <p>
     * For example, for problem class = com.acme.custom.CustomProblem, the localized string with
     * key "CustomProblem.detail" would be loaded from resource bundle "com.acme.custom.Messages".
     * </p>
     *
     * @param problemClass the context class
     * @param args the optional arguments that will be resolved with {@link String#format(String, Object...)}
     * @return the localized string
     */
    public static String getLocalizedDetail(Class<? extends Problem> problemClass, Object... args) {
        return getLocalizedString(problemClass, problemClass.getSimpleName() + ".detail", args);
    }

}
