package io.github.belgif.rest.problem.jaxrs.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
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
    private static final class ClientInvocationHandler implements InvocationHandler {

        private final Client target;

        ClientInvocationHandler(Client target) {
            this.target = target;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            try {
                if (WebTarget.class.equals(method.getReturnType())) {
                    return createProxy(WebTarget.class,
                            new WebTargetInvocationHandler((WebTarget) method.invoke(target, args)));
                } else {
                    return method.invoke(target, args);
                }
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
        }

    }

    /**
     * JDK Dynamic Proxy InvocationHandler for JAX-RS WebTarget.
     */
    private static final class WebTargetInvocationHandler implements InvocationHandler {

        private final WebTarget target;

        WebTargetInvocationHandler(WebTarget target) {
            this.target = target;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            try {
                if (WebTarget.class.equals(method.getReturnType())) {
                    return createProxy(WebTarget.class,
                            new WebTargetInvocationHandler((WebTarget) method.invoke(target, args)));
                } else if (Invocation.Builder.class.equals(method.getReturnType())) {
                    return createProxy(Invocation.Builder.class,
                            new InvocationBuilderInvocationHandler((Invocation.Builder) method.invoke(target, args)));
                } else {
                    return method.invoke(target, args);
                }
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
        }

    }

    /**
     * JDK Dynamic Proxy InvocationHandler for JAX-RS Invocation.Builder.
     */
    private static final class InvocationBuilderInvocationHandler implements InvocationHandler {

        private final Invocation.Builder target;

        InvocationBuilderInvocationHandler(Invocation.Builder target) {
            this.target = target;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            try {
                if (Invocation.Builder.class.equals(method.getReturnType())) {
                    return createProxy(Invocation.Builder.class,
                            new InvocationBuilderInvocationHandler((Invocation.Builder) method.invoke(target, args)));
                } else if (Invocation.class.equals(method.getReturnType())) {
                    return createProxy(Invocation.class,
                            new InvocationInvocationHandler((Invocation) method.invoke(target, args)));
                } else if (AsyncInvoker.class.equals(method.getReturnType())) {
                    return createProxy(AsyncInvoker.class,
                            new AsyncInvokerInvocationHandler((AsyncInvoker) method.invoke(target, args)));
                } else {
                    return method.invoke(target, args);
                }
            } catch (InvocationTargetException e) {
                if (e.getTargetException() instanceof ProblemWrapper) {
                    throw ((ProblemWrapper) e.getTargetException()).getProblem();
                }
                throw e.getTargetException();
            }
        }

    }

    /**
     * JDK Dynamic Proxy InvocationHandler for JAX-RS Invocation.
     */
    private static final class InvocationInvocationHandler implements InvocationHandler {

        private final Invocation target;

        InvocationInvocationHandler(Invocation target) {
            this.target = target;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            try {
                if (Invocation.class.equals(method.getReturnType())) {
                    return createProxy(Invocation.class,
                            new InvocationInvocationHandler((Invocation) method.invoke(target, args)));
                } else if (Future.class.equals(method.getReturnType())) {
                    return createProxy(Future.class,
                            new FutureInvocationHandler((Future<?>) method.invoke(target, args)));
                } else {
                    return method.invoke(target, args);
                }
            } catch (InvocationTargetException e) {
                if (e.getTargetException() instanceof ProblemWrapper) {
                    throw ((ProblemWrapper) e.getTargetException()).getProblem();
                }
                throw e.getTargetException();
            }
        }

    }

    /**
     * JDK Dynamic Proxy InvocationHandler for JAX-RS AsyncInvoker.
     */
    private static final class AsyncInvokerInvocationHandler implements InvocationHandler {

        private final AsyncInvoker target;

        AsyncInvokerInvocationHandler(AsyncInvoker target) {
            this.target = target;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            try {
                if (Future.class.equals(method.getReturnType())) {
                    return createProxy(Future.class,
                            new FutureInvocationHandler((Future<?>) method.invoke(target, args)));
                } else {
                    return method.invoke(target, args);
                }
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
        }

    }

    /**
     * JDK Dynamic Proxy InvocationHandler for Future returned by JAX-RS Client.
     */
    private static final class FutureInvocationHandler implements InvocationHandler {

        private final Future<?> target;

        FutureInvocationHandler(Future<?> target) {
            this.target = target;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            try {
                return method.invoke(target, args);
            } catch (InvocationTargetException e) {
                if (e.getTargetException() instanceof ExecutionException) {
                    ExecutionException executionException = (ExecutionException) e.getTargetException();
                    if (executionException.getCause() instanceof ProblemWrapper) {
                        throw ((ProblemWrapper) executionException.getCause()).getProblem();
                    }
                }
                throw e.getTargetException();
            }
        }

    }

    /**
     * JDK Dynamic Proxy InvocationHandler for proxy clients.
     */
    private static final class ProxyInvocationHandler implements InvocationHandler {

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
