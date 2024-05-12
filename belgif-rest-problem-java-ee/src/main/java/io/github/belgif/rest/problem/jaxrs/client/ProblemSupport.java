package io.github.belgif.rest.problem.jaxrs.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ws.rs.client.AsyncInvoker;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;

/**
 * Utility class for enabling problem support on JAX-RS Clients.
 */
public class ProblemSupport {

    private ProblemSupport() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Enable problem support on the given JAX-RS Client.
     *
     * <p>
     * This causes the JAX-RS Client to throw Problem exceptions instead of ProblemWrapper exceptions.
     * </p>
     *
     * @param client the JAX-RS Client
     * @return the problem-enabled JAX-RS Client
     */
    public static Client enable(Client client) {
        return createProxy(Client.class, new ClientInvocationHandler(client));
    }

    /**
     * Enable problem support on the given client (e.g. RESTEasy proxy client).
     *
     * <p>
     * This causes the client to throw Problem exceptions instead of ProblemWrapper exceptions.
     * </p>
     *
     * @param client the client
     * @param <T> the client type
     * @return the problem-enabled client
     */
    @SuppressWarnings("unchecked")
    public static <T> T enable(T client) {
        return (T) Proxy.newProxyInstance(ProblemSupport.class.getClassLoader(),
                client.getClass().getInterfaces(), new ProxyInvocationHandler(client));
    }

    /**
     * JDK Dynamic Proxy InvocationHandler for JAX-RS Client.
     */
    static final class ClientInvocationHandler implements InvocationHandler {

        private static final Set<Class<?>> PROXIED_RETURN_TYPES = new HashSet<>(Arrays.asList(
                WebTarget.class, Invocation.Builder.class, Invocation.class, AsyncInvoker.class, Future.class));

        private final Object target;

        ClientInvocationHandler(Object target) {
            this.target = target;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (PROXIED_RETURN_TYPES.contains(method.getReturnType())) {
                try {
                    return createProxy(method.getReturnType(),
                            new ClientInvocationHandler(method.invoke(target, args)));
                } catch (InvocationTargetException e) {
                    throw e.getTargetException();
                }
            } else {
                try {
                    return method.invoke(target, args);
                } catch (InvocationTargetException e) {
                    if (e.getTargetException() instanceof ProblemWrapper) {
                        throw ((ProblemWrapper) e.getTargetException()).getProblem();
                    } else if (e.getTargetException() instanceof ExecutionException) {
                        ExecutionException executionException = (ExecutionException) e.getTargetException();
                        if (executionException.getCause() instanceof ProblemWrapper) {
                            throw ((ProblemWrapper) executionException.getCause()).getProblem();
                        }
                    }
                    throw e.getTargetException();
                }
            }
        }

    }

    /**
     * JDK Dynamic Proxy InvocationHandler for proxy clients.
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

    @SuppressWarnings("unchecked")
    private static <T> T createProxy(Class<T> intf, InvocationHandler invocationHandler) {
        return (T) Proxy.newProxyInstance(ProblemSupport.class.getClassLoader(),
                new Class[] { intf }, invocationHandler);
    }

    // TODO: What about methods that receive an javax.ws.rs.client.InvocationCallback?
    // Should we pass a Problem to the .failed() method instead of ProblemWrapper?
    // TODO: What about RxInvoker? javax.ws.rs.client.Invocation.Builder.rx()

}
