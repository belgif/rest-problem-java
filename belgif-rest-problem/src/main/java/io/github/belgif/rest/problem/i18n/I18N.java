package io.github.belgif.rest.problem.i18n;

import java.util.Iterator;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.ServiceLoader;

import io.github.belgif.rest.problem.api.Problem;

/**
 * Helper class for I18N (Internationalization).
 */
public class I18N {

    public static final String I18N_FLAG = "io.github.belgif.rest.problem.i18n";

    /**
     * The default locale: English.
     */
    public static final Locale DEFAULT_LOCALE = Locale.ENGLISH;

    /**
     * The Belgif default resource bundle.
     */
    public static final String DEFAULT_BUNDLE = "io.github.belgif.rest.problem.Messages";

    private static final LocaleResolver LOCALE_RESOLVER = loadLocaleResolver();

    private static boolean enabled = true;

    private I18N() {
    }

    /**
     * Get the locale of the current request.
     *
     * @return the locale of the current request
     */
    public static Locale getRequestLocale() {
        return LOCALE_RESOLVER.getLocale();
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

    /**
     * Get a localized string from the specified resource bundle name.
     *
     * @param bundle the resource bundle name
     * @param key the key
     * @param args the optional arguments that will be resolved with {@link String#format(String, Object...)}
     * @return the localized string
     */
    private static String getLocalizedString(String bundle, String key, Object... args) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle(bundle, enabled ? getRequestLocale() : DEFAULT_LOCALE);
        return String.format(resourceBundle.getString(key), args);
    }

    /**
     * Load the LocaleResolver implementation using ServiceLoader SPI, with fallback to DEFAULT_LOCALE.
     *
     * @return the LocaleResolver
     */
    private static LocaleResolver loadLocaleResolver() {
        Iterator<LocaleResolver> localeResolvers = ServiceLoader.load(LocaleResolver.class).iterator();
        if (localeResolvers.hasNext()) {
            return localeResolvers.next();
        } else {
            return () -> DEFAULT_LOCALE;
        }
    }

    /**
     * Enable or disable I18N.
     *
     * @param enabled true to enable, false to disable
     */
    public static void setEnabled(boolean enabled) {
        I18N.enabled = enabled;
    }

    /**
     * Return whether I18N is enabled.
     *
     * @return whether I18N is enabled
     */
    public static boolean isEnabled() {
        return I18N.enabled;
    }

}
