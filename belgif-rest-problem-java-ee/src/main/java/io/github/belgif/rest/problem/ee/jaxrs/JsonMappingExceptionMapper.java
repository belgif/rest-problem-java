/*
 * Copyright (c) Smals
 */
package io.github.belgif.rest.problem.ee.jaxrs;

import static io.github.belgif.rest.problem.api.InputValidationIssues.schemaViolation;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.ValueInstantiationException;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.api.InEnum;

@Provider
public class JsonMappingExceptionMapper implements ExceptionMapper<JsonMappingException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonMappingExceptionMapper.class);

    @Override
    public Response toResponse(JsonMappingException exception) {
        LOGGER.warn(exception.getMessage(), exception);
        return Response.status(400).entity(new BadRequestProblem(schemaViolation(InEnum.BODY,
                getPath(exception.getPath()), getValue(exception), getReason(exception.getClass())))).build();
    }

    private String getReason(Class<? extends JsonMappingException> clazz) {
        switch (clazz.getSimpleName()) {
            case "InvalidFormatException":
                return "value has invalid format";
            case "InvalidNullException":
                return "property should not be null";
            case "InvalidTypeIdException":
                return "invalid discriminator property";
            case "IgnoredPropertyException":
                return "unexpected property";
            case "UnrecognizedPropertyException":
                return "unrecognized property";
            case "ValueInstantiationException":
                return "invalid value for property";
            default:
                return "invalid property value";
        }
    }

    private String getPath(List<JsonMappingException.Reference> path) {
        StringBuilder builder = new StringBuilder("$");
        for (JsonMappingException.Reference ref : path) {
            builder.append(".").append(ref.getFieldName());
            if (ref.getIndex() != -1) {
                builder.append("[").append(ref.getIndex()).append("]");
            }
        }
        return builder.toString();
    }

    private static final Pattern UNEXPECTED_VALUE = Pattern.compile("Unexpected value '(.*)'");
    private static final Pattern FOR_INPUT_STRING = Pattern.compile("For input string: \"(.*)\"");

    private String getValue(JsonMappingException exception) {
        String value = null;
        if (exception instanceof InvalidFormatException) {
            value = String.valueOf(((InvalidFormatException) exception).getValue());
        } else if (exception instanceof ValueInstantiationException && exception.getCause() != null
                && exception.getCause() instanceof IllegalArgumentException) {
            IllegalArgumentException e = (IllegalArgumentException) exception.getCause();
            Matcher matcher = UNEXPECTED_VALUE.matcher(e.getMessage());
            if (matcher.matches()) {
                return matcher.group(1);
            }
            matcher = FOR_INPUT_STRING.matcher(e.getMessage());
            if (matcher.matches()) {
                return matcher.group(1);
            }
        }
        return value;
    }

}
