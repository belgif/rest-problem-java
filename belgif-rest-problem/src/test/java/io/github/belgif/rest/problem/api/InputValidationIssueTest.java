package io.github.belgif.rest.problem.api;

import static org.assertj.core.api.Assertions.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;

class InputValidationIssueTest {

    @Test
    void construct() {
        InputValidationIssue issue = new InputValidationIssue();
        assertThat(issue.getType()).isNull();
    }

    @Test
    void constructWithTypeTitle() {
        InputValidationIssue issue =
                new InputValidationIssue(URI.create("urn:problem-type:belgif:input-validation:test"), "Title");
        assertThat(issue.getType()).hasToString("urn:problem-type:belgif:input-validation:test");
        assertThat(issue.getTitle()).isEqualTo("Title");
    }

    @Test
    void constructWithInName() {
        InputValidationIssue issue = new InputValidationIssue(InEnum.QUERY, "name");
        assertThat(issue.getIn()).isEqualTo(InEnum.QUERY);
        assertThat(issue.getName()).isEqualTo("name");
    }

    @Test
    void constructWithInNameValue() {
        InputValidationIssue issue = new InputValidationIssue(InEnum.QUERY, "name", "value");
        assertThat(issue.getIn()).isEqualTo(InEnum.QUERY);
        assertThat(issue.getName()).isEqualTo("name");
        assertThat(issue.getValue()).isEqualTo("value");
    }

    @Test
    void constructWithInput() {
        InputValidationIssue issue = new InputValidationIssue(Input.query("name", "value"));
        assertThat(issue.getIn()).isEqualTo(InEnum.QUERY);
        assertThat(issue.getName()).isEqualTo("name");
        assertThat(issue.getValue()).isEqualTo("value");
    }

    @Test
    void type() {
        InputValidationIssue issue = new InputValidationIssue();
        issue.setType(URI.create("urn:problem-type:belgif:input-validation:test"));
        assertThat(issue.getType()).hasToString("urn:problem-type:belgif:input-validation:test");
        assertThat(new InputValidationIssue().type(URI.create("urn:problem-type:belgif:input-validation:test"))
                .getType()).hasToString("urn:problem-type:belgif:input-validation:test");
        assertThat(new InputValidationIssue().type("urn:problem-type:belgif:input-validation:test")
                .getType()).hasToString("urn:problem-type:belgif:input-validation:test");
    }

    @Test
    void href() {
        InputValidationIssue issue = new InputValidationIssue();
        issue.setHref(URI.create("https://www.belgif.be"));
        assertThat(issue.getHref()).hasToString("https://www.belgif.be");
        assertThat(new InputValidationIssue().href(URI.create("https://www.belgif.be")).getHref())
                .hasToString("https://www.belgif.be");
        assertThat(new InputValidationIssue().href("https://www.belgif.be").getHref())
                .hasToString("https://www.belgif.be");
    }

    @Test
    void title() {
        InputValidationIssue issue = new InputValidationIssue();
        issue.setTitle("Title");
        assertThat(issue.getTitle()).isEqualTo("Title");
        assertThat(new InputValidationIssue().title("Title").getTitle()).isEqualTo("Title");
    }

    @Test
    void detail() {
        InputValidationIssue issue = new InputValidationIssue();
        issue.setDetail("Detail");
        assertThat(issue.getDetail()).isEqualTo("Detail");
        assertThat(new InputValidationIssue().detail("Detail").getDetail()).isEqualTo("Detail");
    }

    @Test
    void in() {
        InputValidationIssue issue = new InputValidationIssue();

        issue.setIn(null);
        assertThat(issue.getIn()).isNull();
        issue.in(null);
        assertThat(issue.getIn()).isNull();

        issue.setIn(InEnum.QUERY);
        assertThat(issue.getIn()).isEqualTo(InEnum.QUERY);
        assertThat(new InputValidationIssue().in(InEnum.QUERY).getIn()).isEqualTo(InEnum.QUERY);
    }

    @Test
    void name() {
        InputValidationIssue issue = new InputValidationIssue();

        issue.setName(null);
        issue.name(null);

        issue.setName("name");
        assertThat(issue.getName()).isEqualTo("name");
        assertThat(new InputValidationIssue().name("name").getName()).isEqualTo("name");
    }

    @Test
    void value() {
        InputValidationIssue issue = new InputValidationIssue();

        issue.setValue(null);
        issue.value(null);
        issue.setValue("value");
        assertThat(issue.getValue()).isEqualTo("value");
        assertThat(new InputValidationIssue().value("value").getValue()).isEqualTo("value");
    }

    @Test
    void valueEntry() {
        InputValidationIssue issue = new InputValidationIssue();
        issue.valueEntry("first", "value").valueEntry("second", "value");
        assertThat(issue.getValue()).asInstanceOf(InstanceOfAssertFactories.MAP)
                .containsEntry("first", "value")
                .containsEntry("second", "value");
    }

    @Test
    void valueEntryFailsOnExistingNonMapValue() {
        InputValidationIssue issue = new InputValidationIssue();
        issue.value("not a map");
        assertThatIllegalStateException().isThrownBy(() -> issue.valueEntry("key", "value"));
    }

    @Test
    void inNameValue() {
        InputValidationIssue issue = new InputValidationIssue().in(InEnum.QUERY, "name", "value");
        assertThat(issue.getIn()).isEqualTo(InEnum.QUERY);
        assertThat(issue.getName()).isEqualTo("name");
        assertThat(issue.getValue()).isEqualTo("value");
    }

    @Test
    void inNameValueAndInputsAreMutuallyExclusive() {
        Input<?> input = Input.query("name", "value");
        List<Input<?>> inputs = Arrays.asList(input, input);

        assertThatIllegalArgumentException().isThrownBy(
                () -> new InputValidationIssue().in(InEnum.QUERY).inputs(input));
        assertThatIllegalArgumentException().isThrownBy(
                () -> new InputValidationIssue().name("name").inputs(input));
        assertThatIllegalArgumentException().isThrownBy(
                () -> new InputValidationIssue().value("value").inputs(input));
        assertThatIllegalArgumentException().isThrownBy(
                () -> new InputValidationIssue().inputs(input).in(InEnum.QUERY));
        assertThatIllegalArgumentException().isThrownBy(
                () -> new InputValidationIssue().inputs(input).name("name"));
        assertThatIllegalArgumentException().isThrownBy(
                () -> new InputValidationIssue().inputs(input).value("value"));

        assertThatIllegalArgumentException().isThrownBy(
                () -> new InputValidationIssue().in(InEnum.QUERY).inputs(inputs));
        assertThatIllegalArgumentException().isThrownBy(
                () -> new InputValidationIssue().name("name").inputs(inputs));
        assertThatIllegalArgumentException().isThrownBy(
                () -> new InputValidationIssue().value("value").inputs(inputs));
        assertThatIllegalArgumentException().isThrownBy(
                () -> new InputValidationIssue().inputs(inputs).in(InEnum.QUERY));
        assertThatIllegalArgumentException().isThrownBy(
                () -> new InputValidationIssue().inputs(inputs).name("name"));
        assertThatIllegalArgumentException().isThrownBy(
                () -> new InputValidationIssue().inputs(inputs).value("value"));

        assertThatIllegalArgumentException().isThrownBy(
                () -> new InputValidationIssue().in(InEnum.QUERY).inputs(input, input));
        assertThatIllegalArgumentException().isThrownBy(
                () -> new InputValidationIssue().name("name").inputs(input, input));
        assertThatIllegalArgumentException().isThrownBy(
                () -> new InputValidationIssue().value("value").inputs(input, input));
        assertThatIllegalArgumentException().isThrownBy(
                () -> new InputValidationIssue().inputs(input, input).in(InEnum.QUERY));
        assertThatIllegalArgumentException().isThrownBy(
                () -> new InputValidationIssue().inputs(input, input).name("name"));
        assertThatIllegalArgumentException().isThrownBy(
                () -> new InputValidationIssue().inputs(input, input).value("value"));
    }

    @Test
    void inputs() {
        List<Input<?>> inputs;
        InputValidationIssue issue = new InputValidationIssue();

        assertThatIllegalArgumentException()
                .isThrownBy(() -> issue.setInputs(Collections.singletonList(Input.query("name", "value"))));
        assertThatIllegalArgumentException()
                .isThrownBy(() -> issue.inputs(Collections.singletonList(Input.query("name", "value"))));
        assertThatIllegalArgumentException()
                .isThrownBy(() -> issue.setInputs(Arrays.asList(Input.query("name", "value"), null, null)));
        assertThatIllegalArgumentException()
                .isThrownBy(() -> issue.inputs(Arrays.asList(Input.query("name", "value"), null, null)));

        inputs = null;
        issue.setInputs(inputs);
        assertThat(issue.getInputs()).isEmpty();
        issue.inputs(inputs);
        assertThat(issue.getInputs()).isEmpty();

        inputs = new ArrayList<>(Arrays.asList(Input.query("name", "value"), Input.query("name1", "value1")));
        issue.setInputs(inputs);
        assertThat(issue.getInputs()).isUnmodifiable().isEqualTo(inputs);
        assertThat(new InputValidationIssue().inputs(inputs).getInputs()).isEqualTo(inputs);
    }

    @Test
    void inputsVarargs() {
        Input<?> input;
        InputValidationIssue issue = new InputValidationIssue();

        issue.setInputs();
        assertThat(issue.getInputs()).isEmpty();
        issue.inputs();
        assertThat(issue.getInputs()).isEmpty();

        assertThatIllegalArgumentException().isThrownBy(() -> issue.setInputs(Input.query("name", "value")));
        assertThatIllegalArgumentException().isThrownBy(() -> issue.inputs(Input.query("name", "value")));

        input = Input.query("name", "value");
        issue.setInputs(input, input);
        assertThat(issue.getInputs()).isUnmodifiable().containsExactly(input, input);
        assertThat(new InputValidationIssue().inputs(input, input).getInputs()).containsExactly(input, input);
    }

    @Test
    void input() {
        Input<?> input = null;
        InputValidationIssue issue = new InputValidationIssue();

        issue.addInput(input);
        assertThat(issue.getInputs()).isEmpty();
        issue.addInput(input, input);
        assertThat(issue.getInputs()).isEmpty();
        issue.addInput(Arrays.asList(null, null));
        assertThat(issue.getInputs()).isEmpty();
        issue.addInput(new ArrayList<>());
        assertThat(issue.getInputs()).isEmpty();

        input = Input.query("name", "value");
        issue.addInput(input);
        assertThat(issue.getInputs()).isEmpty();
        assertThat(issue.getName()).isEqualTo(input.getName());
        assertThat(issue.getIn()).isEqualTo(input.getIn());
        assertThat(issue.getValue()).isEqualTo(input.getValue());

        issue.addInput(input);
        assertThat(issue.getName()).isNull();
        assertThat(issue.getIn()).isNull();
        assertThat(issue.getValue()).isNull();
        assertThat(issue.getInputs()).containsExactly(input, input);

        issue = new InputValidationIssue();
        issue.addInput(Collections.singletonList((input)));
        assertThat(issue.getInputs()).isEmpty();
        assertThat(issue.getName()).isEqualTo(input.getName());
        assertThat(issue.getIn()).isEqualTo(input.getIn());
        assertThat(issue.getValue()).isEqualTo(input.getValue());

        issue.addInput(Arrays.asList(input, input));
        assertThat(issue.getName()).isNull();
        assertThat(issue.getIn()).isNull();
        assertThat(issue.getValue()).isNull();
        assertThat(issue.getInputs()).containsExactly(input, input, input);

        issue = new InputValidationIssue();
        issue.addInput(input, input);
        assertThat(issue.getName()).isNull();
        assertThat(issue.getIn()).isNull();
        assertThat(issue.getValue()).isNull();
        assertThat(issue.getInputs()).containsExactly(input, input);

    }

    @Test
    void additionalProperties() {
        InputValidationIssue issue = new InputValidationIssue();
        issue.setAdditionalProperty("key", "value");
        assertThat(issue.getAdditionalProperties()).containsEntry("key", "value");
        assertThat(new InputValidationIssue().additionalProperty("key", "value")
                .getAdditionalProperties()).containsEntry("key", "value");
    }

    @Test
    void equalsHashCodeToString() {
        InputValidationIssue issue = new InputValidationIssue().detail("ok");
        InputValidationIssue equal = new InputValidationIssue().detail("ok");
        InputValidationIssue other = new InputValidationIssue().detail("other");

        assertThat(issue).isEqualTo(issue);
        assertThat(issue).hasSameHashCodeAs(issue);
        assertThat(issue).isEqualTo(equal);
        assertThat(issue).hasSameHashCodeAs(equal);
        assertThat(issue).hasToString(equal.toString());
        assertThat(issue).isNotEqualTo(other);
        assertThat(issue).doesNotHaveSameHashCodeAs(other);
        assertThat(issue).isNotEqualTo("other type");
    }

}
