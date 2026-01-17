package io.github.belgif.rest.problem.spring.internal;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;

import io.github.belgif.rest.problem.config.DisableProblems;
import io.github.belgif.rest.problem.config.EnableProblems;
import io.github.belgif.rest.problem.config.ProblemConfig;

class ProblemRestControllerSupportTest {

    @BeforeEach
    @AfterEach
    void cleanup() {
        ProblemConfig.reset();
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void isServerSideEnabled() throws NoSuchMethodException {
        // no handler method
        assertThat(ProblemRestControllerSupport.isServerSideEnabled()).isTrue();
        assertThat(ProblemRestControllerSupport.isServerSideDisabled()).isFalse();
        // default controller handler method
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE,
                new HandlerMethod(new DefaultController(), DefaultController.class.getMethod("handle")));
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        assertThat(ProblemRestControllerSupport.isServerSideEnabled()).isTrue();
        assertThat(ProblemRestControllerSupport.isServerSideDisabled()).isFalse();
        // enabled controller handler method
        request.setAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE,
                new HandlerMethod(new EnabledController(), EnabledController.class.getMethod("handle")));
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        assertThat(ProblemRestControllerSupport.isServerSideEnabled()).isTrue();
        assertThat(ProblemRestControllerSupport.isServerSideDisabled()).isFalse();
        // disabled controller handler method
        request.setAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE,
                new HandlerMethod(new DisabledController(), DisabledController.class.getMethod("handle")));
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        assertThat(ProblemRestControllerSupport.isServerSideEnabled()).isFalse();
        assertThat(ProblemRestControllerSupport.isServerSideDisabled()).isTrue();
    }

    @Test
    void isServerSideDisabled() throws NoSuchMethodException {
        ProblemConfig.setServerSideEnabled(false);
        // no handler method
        assertThat(ProblemRestControllerSupport.isServerSideEnabled()).isFalse();
        assertThat(ProblemRestControllerSupport.isServerSideDisabled()).isTrue();
        // default controller handler method
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE,
                new HandlerMethod(new DefaultController(), DefaultController.class.getMethod("handle")));
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        assertThat(ProblemRestControllerSupport.isServerSideEnabled()).isFalse();
        assertThat(ProblemRestControllerSupport.isServerSideDisabled()).isTrue();
        // enabled controller handler method
        request.setAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE,
                new HandlerMethod(new EnabledController(), EnabledController.class.getMethod("handle")));
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        assertThat(ProblemRestControllerSupport.isServerSideEnabled()).isTrue();
        assertThat(ProblemRestControllerSupport.isServerSideDisabled()).isFalse();
        // disabled controller handler method
        request.setAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE,
                new HandlerMethod(new DisabledController(), DisabledController.class.getMethod("handle")));
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        assertThat(ProblemRestControllerSupport.isServerSideEnabled()).isFalse();
        assertThat(ProblemRestControllerSupport.isServerSideDisabled()).isTrue();
    }

    @RestController
    static class DefaultController {
        public void handle() {
            return;
        }
    }

    @RestController
    @EnableProblems
    static class EnabledController {
        public void handle() {
            return;
        }
    }

    @RestController
    @DisableProblems
    static class DisabledController {
        public void handle() {
            return;
        }
    }

}
