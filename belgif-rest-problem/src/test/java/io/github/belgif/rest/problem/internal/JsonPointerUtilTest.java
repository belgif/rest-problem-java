package io.github.belgif.rest.problem.internal;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    @ParameterizedTest
    @EnumSource(InEnum.class)
    void getNameFromProperties(InEnum in) {
        List<String> properties = null;
        assertThat(JsonPointerUtil.getNameFromProperties(in, properties)).isNull();

        properties = new ArrayList<>();
        assertThat(JsonPointerUtil.getNameFromProperties(in, properties)).isNull();

        properties.add("field");
        assertThat(JsonPointerUtil.getNameFromProperties(in, properties))
                .isEqualTo(in == InEnum.BODY ? "/field" : "field");

        properties.set(0, "field[0]");
        assertThat(JsonPointerUtil.getNameFromProperties(in, properties))
                .isEqualTo(in == InEnum.BODY ? "/field/0" : "field[0]");

        if (in == InEnum.BODY) {
            properties.add("nested");
            assertThat(JsonPointerUtil.getNameFromProperties(in, properties)).isEqualTo("/field/0/nested");

            properties.add("nestedAgain[1]");
            assertThat(JsonPointerUtil.getNameFromProperties(in, properties))
                    .isEqualTo("/field/0/nested/nestedAgain/1");
        }
    }

    @ParameterizedTest
    @EnumSource(InEnum.class)
    void getNameFromPropertiesWithJsonPointerDisabled(InEnum in) {

        ProblemConfig.setJsonPointerEnabled(false);

        List<String> properties = null;
        assertThat(JsonPointerUtil.getNameFromProperties(in, properties)).isNull();

        properties = new ArrayList<>();
        assertThat(JsonPointerUtil.getNameFromProperties(in, properties)).isNull();

        properties.add("field");
        assertThat(JsonPointerUtil.getNameFromProperties(in, properties)).isEqualTo("field");

        properties.set(0, "field[0]");
        assertThat(JsonPointerUtil.getNameFromProperties(in, properties)).isEqualTo("field[0]");

        if (in == InEnum.BODY) {
            properties.add("nested");
            assertThat(JsonPointerUtil.getNameFromProperties(in, properties)).isEqualTo("field[0].nested");
            assertThat(JsonPointerUtil.getNameFromProperties(in, properties)).isEqualTo("field[0].nested");

            properties.add("nestedAgain[1]");
            assertThat(JsonPointerUtil.getNameFromProperties(in, properties))
                    .isEqualTo("field[0].nested.nestedAgain[1]");
        }
    }

    @ParameterizedTest
    @EnumSource(value = InEnum.class, names = { "BODY" }, mode = EnumSource.Mode.EXCLUDE)
    void getNameFromPropertiesIllegalArgument() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> JsonPointerUtil.getNameFromProperties(InEnum.QUERY,
                        Arrays.asList("field", "nested")))
                .withMessageContaining("located in the body");
        assertThatIllegalArgumentException()
                .isThrownBy(
                        () -> JsonPointerUtil.getNameFromProperties(InEnum.PATH, Arrays.asList("field", "nested")))
                .withMessageContaining("located in the body");
        assertThatIllegalArgumentException()
                .isThrownBy(() -> JsonPointerUtil.getNameFromProperties(InEnum.HEADER,
                        Arrays.asList("field", "nested")))
                .withMessageContaining("located in the body");
    }

}
