package io.github.belgif.rest.problem.ee.resteasy.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import io.github.belgif.rest.problem.ee.jaxrs.JaxRsUtil;
import io.github.belgif.rest.problem.ee.jaxrs.client.ClientProblemObjectMapperContextResolver;
import io.github.belgif.rest.problem.ee.jaxrs.client.ProblemClientResponseFilter;
import io.github.belgif.rest.problem.ee.jaxrs.client.ProblemSupport;
import io.github.belgif.rest.problem.ee.jaxrs.client.ProblemWrapper;
import io.github.belgif.rest.problem.ee.util.Platform;

/**
 * Utility class for enabling problem support on RESTEasy Clients.
 */
public class ResteasyProblemSupport {

    private ResteasyProblemSupport() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Create a problem-enabled RESTEasy proxy client.
     *
     * @param target the ResteasyWebTarget
     * @param proxyInterface the service interface
     * @return the problem-enabled RESTEasy proxy client
     * @param <T> the service interface type
     */
    @SuppressWarnings("unchecked")
    public static <T> T proxy(ResteasyWebTarget target, Class<T> proxyInterface) {
        JaxRsUtil.register(target, ProblemClientResponseFilter.class);
        if (!Platform.isQuarkus()) {
            JaxRsUtil.register(target, ClientProblemObjectMapperContextResolver.class);
        }
        T client = target.proxy(proxyInterface);
        return (T) Proxy.newProxyInstance(ProblemSupport.class.getClassLoader(),
                client.getClass().getInterfaces(), new ProxyInvocationHandler(client));
    }

    /**
     * JDK Dynamic Proxy InvocationHandler for RESTEasy proxy clients.
     */
    static final class ProxyInvocationHandler implements InvocationHandler {

        private final Object target;

        ProxyInvocationHandler(Object target) {
            this.target = target;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            try {
                return method.invoke(target, args);
            } catch (InvocationTargetException e) {
                if (e.getTargetException() instanceof ProblemWrapper) {
                    throw ((ProblemWrapper) e.getTargetException()).getProblem();
                }
                throw e.getTargetException();
            }
        }

    }

}
