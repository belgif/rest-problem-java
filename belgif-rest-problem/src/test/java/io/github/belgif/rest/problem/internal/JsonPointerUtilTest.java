package io.github.belgif.rest.problem.internal;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import io.github.belgif.rest.problem.api.InEnum;
import io.github.belgif.rest.problem.config.ProblemConfig;

class JsonPointerUtilTest {

    @BeforeEach
    @AfterEach
    void resetProblemConfig() {
        ProblemConfig.reset();
    }

    @Test
    void isJsonPointer() {
        assertThat(JsonPointerUtil.isJsonPointer("")).isTrue();
        assertThat(JsonPointerUtil.isJsonPointer("/test")).isTrue();
        assertThat(JsonPointerUtil.isJsonPointer("/foo/bar")).isTrue();
        assertThat(JsonPointerUtil.isJsonPointer("/foo/bar/1")).isTrue();
        assertThat(JsonPointerUtil.isJsonPointer(null)).isFalse();
        assertThat(JsonPointerUtil.isJsonPointer("test")).isFalse();
        assertThat(JsonPointerUtil.isJsonPointer("foo.bar")).isFalse();
        assertThat(JsonPointerUtil.isJsonPointer("foo.bar[1]")).isFalse();
        assertThat(JsonPointerUtil.isJsonPointer("/foo/bar[1]")).isFalse();
        assertThat(JsonPointerUtil.isJsonPointer("    ")).isFalse();
    }

    @ParameterizedTest
    @EnumSource(InEnum.class)
    void transformNameInBody(InEnum in) {
        assertThat(JsonPointerUtil.transformName(in, null)).isNull();
        assertThat(JsonPointerUtil.transformName(in, "")).isNull();
        assertThat(JsonPointerUtil.transformName(in, "    ")).isNull();
        assertThat(JsonPointerUtil.transformName(in, "field")).isEqualTo(in == InEnum.BODY ? "/field" : "field");
        assertThat(JsonPointerUtil.transformName(in, "field[0]"))
                .isEqualTo(in == InEnum.BODY ? "/field/0" : "field[0]");
        assertThat(JsonPointerUtil.transformName(in, "field[0].nested"))
                .isEqualTo(in == InEnum.BODY ? "/field/0/nested" : "field[0].nested");
        assertThat(JsonPointerUtil.transformName(in, "field/0"))
                .isEqualTo(in == InEnum.BODY ? "/field/0" : "field/0");
        assertThat(JsonPointerUtil.transformName(in, "field/0/nested"))
                .isEqualTo(in == InEnum.BODY ? "/field/0/nested" : "field/0/nested");
        assertThat(JsonPointerUtil.transformName(in, "/field/0")).isEqualTo("/field/0");
        assertThat(JsonPointerUtil.transformName(in, "/field/0/nested")).isEqualTo("/field/0/nested");
        assertThat(JsonPointerUtil.transformName(in, "/field")).isEqualTo("/field");
    }

    @ParameterizedTest
    @EnumSource(InEnum.class)
    void transformNameInBodyJsonPointerDisabled(InEnum in) {
        ProblemConfig.setJsonPointerEnabled(false);

        assertThat(JsonPointerUtil.transformName(in, null)).isNull();
        assertThat(JsonPointerUtil.transformName(in, "")).isNull();
        assertThat(JsonPointerUtil.transformName(in, "    ")).isNull();
        assertThat(JsonPointerUtil.transformName(in, "field")).isEqualTo("field");
        assertThat(JsonPointerUtil.transformName(in, "field[0]")).isEqualTo("field[0]");
        assertThat(JsonPointerUtil.transformName(in, "field[0].nested")).isEqualTo("field[0].nested");
        assertThat(JsonPointerUtil.transformName(in, "field/0")).isEqualTo("field/0");
        assertThat(JsonPointerUtil.transformName(in, "field/0/nested")).isEqualTo("field/0/nested");
        assertThat(JsonPointerUtil.transformName(in, "/field/0")).isEqualTo("/field/0");
        assertThat(JsonPointerUtil.transformName(in, "/field/0/nested")).isEqualTo("/field/0/nested");
        assertThat(JsonPointerUtil.transformName(in, "/field")).isEqualTo("/field");
    }

}
