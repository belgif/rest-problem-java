package io.github.belgif.rest.problem.ee.util;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import org.junit.jupiter.api.Test;

class PlatformTest {

    @Test
    void detectClass() {
        assertThat(Platform.detectClass("java.lang.String")).isTrue();
        assertThat(Platform.detectClass("not.Found")).isFalse();
    }

}
