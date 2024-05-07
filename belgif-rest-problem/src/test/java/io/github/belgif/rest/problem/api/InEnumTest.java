package io.github.belgif.rest.problem.api;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class InEnumTest {

    @ParameterizedTest
    @EnumSource
    void toString(InEnum in) {
        assertThat(in).hasToString(in.name().toLowerCase());
    }

    @ParameterizedTest
    @EnumSource
    void fromValue(InEnum in) {
        assertThat(InEnum.fromValue(in.toString())).isEqualTo(in);
    }

    @Test
    void fromValueInvalid() {
        assertThatIllegalArgumentException().isThrownBy(() -> InEnum.fromValue("invalid"))
                .withMessage("Unexpected value 'invalid'");
    }

}
