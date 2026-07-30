package io.github.belgif.rest.problem.internal;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import io.github.belgif.rest.problem.api.InEnum;
import io.github.belgif.rest.problem.config.ProblemConfig;

/**
 * Internal utility class for JSON Pointer expressions.
 */
public class JsonPointerUtil {

    // e.g: /, field, /field, /field/0, /field/0/nested
    private static final Pattern JSON_POINTER_PATTERN = Pattern.compile("/+[a-zA-Z0-9-]*+(/[a-zA-Z0-9-]++)*+");

    private static final Pattern ARRAY_INDEX_PATTERN = Pattern.compile("\\[(\\d++)]");

    private JsonPointerUtil() {
    }

    /**
     * Check whether the given string is a JSON Pointer.
     *
     * @param value the string
     * @return true when it is a JSON Pointer, false otherwise
     */
    public static boolean isJsonPointer(String value) {
        return JSON_POINTER_PATTERN.matcher(value).matches();
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

    public static String getNameFromProperties(InEnum in, List<String> propertiesName) {

        if (in != InEnum.BODY && propertiesName != null && propertiesName.size() > 1) {
            throw new IllegalArgumentException(
                    "This method should only be used with several properties for issues located in the body");
        }

        if (propertiesName == null || propertiesName.isEmpty()) {
            return null;
        }

        String name = ProblemConfig.isJsonPointerEnabled() && in == InEnum.BODY ? propertiesName.stream()
                .map(JsonPointerUtil::replaceSquareBrackets).collect(Collectors.joining("/"))
                : String.join(".", propertiesName);
        return in == InEnum.BODY && ProblemConfig.isJsonPointerEnabled() ? "/" + name : name;
    }

    private static String replaceSquareBrackets(String propertyName) {
        // replace all indexes "[X]" by "/X"
        return ARRAY_INDEX_PATTERN.matcher(propertyName).replaceAll("/$1");
    }

}
