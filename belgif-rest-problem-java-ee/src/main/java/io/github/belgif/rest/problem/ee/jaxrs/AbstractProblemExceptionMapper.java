package io.github.belgif.rest.problem.ee.jaxrs;

import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Providers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.belgif.rest.problem.InternalServerErrorProblem;
import io.github.belgif.rest.problem.api.Problem;
import io.github.belgif.rest.problem.config.DisableProblems;
import io.github.belgif.rest.problem.config.EnableProblems;
import io.github.belgif.rest.problem.config.ProblemConfig;

public abstract class AbstractProblemExceptionMapper<E extends Throwable> implements ExceptionMapper<E> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractProblemExceptionMapper.class);

    @Context
    private ResourceInfo resourceInfo;

    @Context
    private Providers providers;

    private Class<E> exceptionClass;

    protected AbstractProblemExceptionMapper(Class<E> exceptionClass) {
        this.exceptionClass = exceptionClass;
    }

    @Override
    public Response toResponse(E exception) {
        if (!isProblemsEnabled()) {
            return fallback(exception);
        } else {
            try {
                Problem problem = toProblem(exception);
                if (problem != null) {
                    return ProblemMediaType.INSTANCE.toResponse(problem);
                } else {
                    return fallback(exception);
                }
            } catch (RuntimeException e) {
                LOGGER.error("Unhandled exception", exception);
                return ProblemMediaType.INSTANCE.toResponse(new InternalServerErrorProblem());
            }
        }
    }

    private boolean isProblemsEnabled() {
        return (ProblemConfig.isServerSideEnabled() &&
                !resourceInfo.getResourceClass().isAnnotationPresent(DisableProblems.class))
                || resourceInfo.getResourceClass().isAnnotationPresent(EnableProblems.class);
    }

    private Response fallback(E exception) {
        ExceptionMapper<? super E> mapper = providers.getExceptionMapper((Class) exceptionClass.getSuperclass());
        if (mapper != null && mapper != this) {
            return mapper.toResponse(exception);
        } else {
            sneakyThrow(exception);
            return null;
        }
    }

    public static <T extends Throwable> void sneakyThrow(Throwable e) throws T {
        throw (T) e;
    }

    protected abstract Problem toProblem(E exception);

}
