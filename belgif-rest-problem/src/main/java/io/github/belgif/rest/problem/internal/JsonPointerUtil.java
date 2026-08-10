package io.github.belgif.rest.problem.internal;

import java.util.regex.Pattern;

import io.github.belgif.rest.problem.api.InEnum;
import io.github.belgif.rest.problem.config.ProblemConfig;

/**
 * Internal utility class for JSON Pointer expressions.
 */
public class JsonPointerUtil {

    private static final Pattern ARRAY_INDEX_PATTERN = Pattern.compile("\\[(\\d++)]");

    private JsonPointerUtil() {
    }

    /**
     * Heuristically check whether the given string is likely to be a JSON Pointer
     * (empty "" or starting with "/" and not containing any square brackets []).
     *
     * @param value the string
     * @return true when it is likely a JSON Pointer, false otherwise
     */
    public static boolean isJsonPointer(String value) {
        return value != null && (value.isEmpty() || value.startsWith("/"))
                && !value.contains("[") && !value.contains("]");
    }

    /**
     * Add the given index to the given name in the correct format (JSON Pointer "/idx" or JsonPath "[idx]").
     *
     * @param in the input location
     * @param name the input name
     * @param index the index
     * @return the name with the index added
     */
    public static String addIndex(InEnum in, String name, int index) {
        if (ProblemConfig.isJsonPointerEnabled() && in == InEnum.BODY) {
            return name + "/" + index;
        } else {
            return name + "[" + index + "]";
        }
    }

    /**
     *
     * @param in
     *        the issue place in the query
     * @param nameJsonPath
     *        the name in JsonPath syntax
     * @return the name converted to JsonPointer syntax
     */
    public static String transformName(InEnum in, String nameJsonPath) {

        if (nameJsonPath == null || nameJsonPath.trim().isEmpty()) {
            return null;
        } else if (!ProblemConfig.isJsonPointerEnabled() || in != InEnum.BODY) {
            return nameJsonPath;
        } else {
            // replace all indexes "[X]" by "/X" and replace all "." by "/"
            String convertedName = replaceSquareBrackets(nameJsonPath).replace(".", "/");
            return convertedName.charAt(0) != '/' ? "/" + convertedName : convertedName;
        }
    }

    private static String replaceSquareBrackets(String propertyName) {
        // replace all indexes "[X]" by "/X"
        return ARRAY_INDEX_PATTERN.matcher(propertyName).replaceAll("/$1");
    }

}
