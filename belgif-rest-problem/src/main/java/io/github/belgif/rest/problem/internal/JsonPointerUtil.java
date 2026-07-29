package io.github.belgif.rest.problem.internal;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.belgif.rest.problem.api.InEnum;
import io.github.belgif.rest.problem.config.ProblemConfig;

/**
 * Internal utility class for JSON Pointer expressions.
 */
public class JsonPointerUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonPointerUtil.class);

    // e.g: /, field, /field, /field/0, /field/0/nested
    private static final Pattern JSON_POINTER_BASIC_REGEX = Pattern.compile("/+[a-zA-Z0-9-]*+(/[a-zA-Z0-9-]++)*+");

    private JsonPointerUtil() {
    }

    private static boolean nameMatchesJsonPointerFormat(String name) {
        return name == null || (JSON_POINTER_BASIC_REGEX.matcher(name).matches()
                && !name.matches(".*/\\d+/\\d+/*+")
                // not two indexes following each other (e.g: person/1/2)
                && !name.matches("/+\\d++(/[a-zA-Z0-9-.]++)*+")); // not starting with an index (e.g: /1/person)
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

    /**
     *
     * @param in
     *        the issue place in the query
     * @param name
     *        the name in JsonPath syntax
     * @return the name converted (if necessary) to JsonPointer syntax
     */
    public static String convertName(InEnum in, String name) {
        if (in == InEnum.BODY && ProblemConfig.isJsonPointerEnabled() && !nameMatchesJsonPointerFormat(name)) {
            LOGGER.warn(
                    "Your application does not use the JsonPointer syntax for issue in the body although it is "
                            + "enabled [in: %s, name: %s]. Auto-conversion will be applied. ",
                    in, name);
            return transformName(in, name);
        }

        return name;
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
        return propertyName.replaceAll("\\[(\\d++)]", "/$1");
    }

}
