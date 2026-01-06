package io.github.belgif.rest.problem.spring.internal;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.atlassian.oai.validator.report.ValidationReport;

import io.github.belgif.rest.problem.api.InEnum;
import io.swagger.v3.oas.models.parameters.Parameter;

/**
 * Internal utility class for parsing validation report message from Atlassian swagger-request-validator library.
 *
 * @see ValidationReport.Message
 */
public class InvalidRequestExceptionUtil {

    private InvalidRequestExceptionUtil() {
    }

    public static InEnum getIn(ValidationReport.Message message) {
        return message.getContext()
                .flatMap(ValidationReport.MessageContext::getParameter)
                .map(parameter -> InEnum.fromValue(parameter.getIn()))
                .orElse(InEnum.BODY);
    }

    public static String getName(ValidationReport.Message message) {
        return message.getContext()
                .flatMap(ValidationReport.MessageContext::getParameter)
                .map(Parameter::getName)
                .orElseGet(() -> message.getContext()
                        .flatMap(ValidationReport.MessageContext::getPointers)
                        .map(ValidationReport.MessageContext.Pointers::getInstance)
                        .orElse(null));
    }

    public static String getPathValue(ValidationReport.Message message, String name) {
        String original = message.getContext().flatMap(ValidationReport.MessageContext::getApiOperation)
                .map(p -> p.getApiPath().original()).orElse(null);
        if (original != null) {
            String actual = message.getContext()
                    .flatMap(ValidationReport.MessageContext::getRequestPath)
                    .orElse("");

            String regex = original.replace("{" + name + "}", "(.*)").replaceAll("/\\{[^}]*}", "/.*");

            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(actual);

            if (matcher.find()) {
                return matcher.group(1);
            }
        }

        return null;
    }

    public static String getDetail(ValidationReport.Message message) {
        if ("validation.request.body.schema.oneOf".equals(message.getKey())
                || "validation.request.body.schema.anyOf".equals(message.getKey())) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(message.getMessage()).append(" : ");
            for (ValidationReport.Message nestedMessage : message.getNestedMessages()) {
                stringBuilder.append(" -- ").append(nestedMessage.getMessage());
            }
            return stringBuilder.toString();
        } else {
            return message.getMessage();
        }
    }

}
