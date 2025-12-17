/*
 * Copyright (c) Smals
 */
package io.github.belgif.rest.problem.ee.jaxrs;

import static io.github.belgif.rest.problem.api.InputValidationIssues.schemaViolation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.api.InEnum;
import io.github.belgif.rest.problem.api.InputValidationIssue;

/**
 * {@link javax.ws.rs.ext.ParamConverter} errors are wrapped into a {@link javax.ws.rs.NotFoundException} in JBoss EAP.
 * This ExceptionMapper unwraps them into 400 Bad Request.
 */
@Provider
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotFoundExceptionMapper.class);

    private static final Pattern PARAM_PATTERN =
            Pattern.compile("(javax|jakarta).ws.rs.(Path|Query|Header|Bean|Form|Cookie|Matrix)Param\\(\"(.*)\"\\)");

    @Override
    public Response toResponse(NotFoundException exception) {
        LOGGER.warn(exception.getMessage(), exception);

        if (exception.getCause() instanceof BadRequestProblem) {
            BadRequestProblem problem = (BadRequestProblem) exception.getCause();

            InputValidationIssue issue = problem.getIssues().isEmpty() ? schemaViolation(null, null, null, null)
                    : problem.getIssues().get(0);

            Matcher matcher = PARAM_PATTERN.matcher(exception.getMessage());
            if (matcher.find()) {
                issue.in(mapLocation(matcher.group(2)));
                issue.name(matcher.group(3));
            }

            if (problem.getIssues().isEmpty()) {
                problem.addIssue(issue);
            }

            return Response.status(400).entity(problem).build();
        } else {
            throw exception;
        }
    }

    private InEnum mapLocation(String location) {
        if ("Query".equals(location)) {
            return InEnum.QUERY;
        } else if ("Path".equals(location)) {
            return InEnum.PATH;
        } else if ("Header".equals(location)) {
            return InEnum.HEADER;
        } else if ("Bean".equals(location)) {
            return InEnum.BODY;
        } else if ("Form".equals(location)) {
            return InEnum.FORM;
        } else if ("Matrix".equals(location)) {
            return InEnum.MATRIX;
        } else if ("Cookie".equals(location)) {
            return InEnum.COOKIE;
        } else {
            return null;
        }
    }

}
