package io.github.belgif.rest.problem.jaxrs;

import static org.assertj.core.api.Assertions.*;

import javax.ws.rs.CookieParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class JaxRsParameterNameProviderTest {

    private JaxRsParameterNameProvider provider = new JaxRsParameterNameProvider();

    @Test
    void getParameterNameConstructor() throws Exception {
        assertThat(provider.getParameterNames(Tested.class.getDeclaredConstructor(String.class, Long.class)))
                .containsExactly("name", "other");
    }

    @Test
    void getParameterNamesMethod() throws Exception {
        assertThat(provider.getParameterNames(Tested.class.getDeclaredMethod("plainMethod", String.class, Long.class)))
                .containsExactly("name", "other");
    }

    @ParameterizedTest
    @ValueSource(strings = { "queryParam", "pathParam", "headerParam", "cookieParam", "formParam", "matrixParam" })
    void getParameterNamesMethodWithJaxRsAnnotation(String method) throws Exception {
        assertThat(provider.getParameterNames(Tested.class.getDeclaredMethod(method, String.class)))
                .containsExactly("name");
    }

    static class Tested {
        Tested(String name, Long other) {
        }

        void plainMethod(String name, Long other) {
        }

        void queryParam(@QueryParam("name") String x) {
        }

        void pathParam(@PathParam("name") String x) {
        }

        void headerParam(@HeaderParam("name") String x) {
        }

        void cookieParam(@CookieParam("name") String x) {
        }

        void formParam(@FormParam("name") String x) {
        }

        void matrixParam(@MatrixParam("name") String x) {
        }
    }

}
