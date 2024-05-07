package io.github.belgif.rest.problem.api;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class InvalidParamTest {

    @Test
    void construct() {
        InvalidParam param = new InvalidParam();
        assertThat(param.getIn()).isNull();
    }

    @Test
    void constructWithInName() {
        InvalidParam param = new InvalidParam(InEnum.QUERY, "name");
        assertThat(param.getIn()).isEqualTo(InEnum.QUERY);
        assertThat(param.getName()).isEqualTo("name");
    }

    @Test
    void constructWithInNameValue() {
        InvalidParam param = new InvalidParam(InEnum.QUERY, "name", "value");
        assertThat(param.getIn()).isEqualTo(InEnum.QUERY);
        assertThat(param.getName()).isEqualTo("name");
        assertThat(param.getValue()).isEqualTo("value");
    }

    @Test
    void constructWithInNameReasonValue() {
        InvalidParam param = new InvalidParam(InEnum.QUERY, "name", "reason", "value");
        assertThat(param.getIn()).isEqualTo(InEnum.QUERY);
        assertThat(param.getName()).isEqualTo("name");
        assertThat(param.getReason()).isEqualTo("reason");
        assertThat(param.getValue()).isEqualTo("value");
    }

    @Test
    void constructWithInNameReasonValueType() {
        InvalidParam param = new InvalidParam(InEnum.QUERY, "name", "reason", "value", "type");
        assertThat(param.getIn()).isEqualTo(InEnum.QUERY);
        assertThat(param.getName()).isEqualTo("name");
        assertThat(param.getReason()).isEqualTo("reason");
        assertThat(param.getValue()).isEqualTo("value");
        assertThat(param.getIssueType()).isEqualTo("type");
    }

    @Test
    void in() {
        InvalidParam param = new InvalidParam();
        param.setIn(InEnum.QUERY);
        assertThat(param.getIn()).isEqualTo(InEnum.QUERY);
    }

    @Test
    void name() {
        InvalidParam param = new InvalidParam();
        param.setName("name");
        assertThat(param.getName()).isEqualTo("name");
    }

    @Test
    void value() {
        InvalidParam param = new InvalidParam();
        param.setValue("value");
        assertThat(param.getValue()).isEqualTo("value");
    }

    @Test
    void reason() {
        InvalidParam param = new InvalidParam();
        param.setReason("reason");
        assertThat(param.getReason()).isEqualTo("reason");
    }

    @Test
    void issueType() {
        InvalidParam param = new InvalidParam();
        param.setIssueType("type");
        assertThat(param.getIssueType()).isEqualTo("type");
    }

    @Test
    void additionalProperties() {
        InvalidParam param = new InvalidParam();
        param.setAdditionalProperty("key", "value");
        assertThat(param.getAdditionalProperties()).containsEntry("key", "value");
    }

    @Test
    void equalsHashCodeToString() {
        InvalidParam param = new InvalidParam(InEnum.QUERY, "name");
        InvalidParam equal = new InvalidParam(InEnum.QUERY, "name");
        InvalidParam other = new InvalidParam(InEnum.QUERY, "other");

        assertThat(param).isEqualTo(param);
        assertThat(param).hasSameHashCodeAs(param);
        assertThat(param).isEqualTo(equal);
        assertThat(param).hasSameHashCodeAs(equal);
        assertThat(param).hasToString(equal.toString());
        assertThat(param).isNotEqualTo(other);
        assertThat(param).doesNotHaveSameHashCodeAs(other);
        assertThat(param).isNotEqualTo("other type");
    }

}
