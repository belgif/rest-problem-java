package io.github.belgif.rest.problem.api;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class InputTest {

    @Test
    void construct() {
        Input<String> input = new Input<>();
        input.setIn(InEnum.QUERY);
        input.setName("name");
        input.setValue("value");
        assertThat(input.getIn()).isEqualTo(InEnum.QUERY);
        assertThat(input.getName()).isEqualTo("name");
        assertThat(input.getValue()).isEqualTo("value");

        assertThat(input.toString()).contains("query", "name", "value");
    }

    @Test
    void body() {
        Input<String> bodyInput = Input.body("bodyName", "bodyValue");

        assertThat(bodyInput.getIn()).isEqualTo(InEnum.BODY);
        assertThat(bodyInput.getName()).isEqualTo("bodyName");
        assertThat(bodyInput.getValue()).isEqualTo("bodyValue");
    }

    @Test
    void query() {
        Input<String> queryInput = Input.query("queryName", "queryValue");

        assertThat(queryInput.getIn()).isEqualTo(InEnum.QUERY);
        assertThat(queryInput.getName()).isEqualTo("queryName");
        assertThat(queryInput.getValue()).isEqualTo("queryValue");
    }

    @Test
    void path() {
        Input<String> pathInput = Input.path("pathName", "pathValue");

        assertThat(pathInput.getIn()).isEqualTo(InEnum.PATH);
        assertThat(pathInput.getName()).isEqualTo("pathName");
        assertThat(pathInput.getValue()).isEqualTo("pathValue");
    }

    @Test
    void header() {
        Input<String> headerInput = Input.header("headerName", "headerValue");

        assertThat(headerInput.getIn()).isEqualTo(InEnum.HEADER);
        assertThat(headerInput.getName()).isEqualTo("headerName");
        assertThat(headerInput.getValue()).isEqualTo("headerValue");
    }

    @Test
    void equalsHashCodeToString() {
        Input<String> input = new Input<>(InEnum.BODY, "name", "value");
        Input<String> equal = new Input<>(InEnum.BODY, "name", "value");
        Input<String> other = new Input<>(InEnum.BODY, "anotherName", "anotherValue");

        assertThat(input).isEqualTo(input);
        assertThat(input).hasSameHashCodeAs(input);
        assertThat(input).isEqualTo(equal);
        assertThat(input).hasSameHashCodeAs(equal);
        assertThat(input).hasToString(equal.toString());
        assertThat(input).isNotEqualTo(other);
        assertThat(input).doesNotHaveSameHashCodeAs(other);
        assertThat(input).isNotEqualTo("other type");
    }

}
