package io.github.belgif.rest.problem.api;

import static org.assertj.core.api.Assertions.*;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;

class InputValidationIssueTest {

    @Test
    void construct() {
        InputValidationIssue issue = new InputValidationIssue();
    }

    @Test
    void constructTypeTitle() {
        InputValidationIssue issue =
                new InputValidationIssue(URI.create("urn:problem-type:belgif:input-validation:test"), "Title");
        assertThat(issue.getType()).hasToString("urn:problem-type:belgif:input-validation:test");
        assertThat(issue.getTitle()).isEqualTo("Title");
    }

    @Test
    void constructInName() {
        InputValidationIssue issue = new InputValidationIssue(InEnum.QUERY, "name");
        assertThat(issue.getIn()).isEqualTo(InEnum.QUERY);
        assertThat(issue.getName()).isEqualTo("name");
    }

    @Test
    void constructInNameValue() {
        InputValidationIssue issue = new InputValidationIssue(InEnum.QUERY, "name", "value");
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
    void status() {
        InputValidationIssue issue = new InputValidationIssue();
        issue.setStatus(499);
        assertThat(issue.getStatus()).isEqualTo(499);
    }

    @Test
    void detail() {
        InputValidationIssue issue = new InputValidationIssue();
        issue.setDetail("Detail");
        assertThat(issue.getDetail()).isEqualTo("Detail");
        assertThat(new InputValidationIssue().detail("Detail").getDetail()).isEqualTo("Detail");
    }

    @Test
    void instance() {
        InputValidationIssue issue = new InputValidationIssue();
        issue.setInstance(URI.create("instance"));
        assertThat(issue.getInstance()).hasToString("instance");
    }

    @Test
    void in() {
        InputValidationIssue issue = new InputValidationIssue();
        issue.setIn(InEnum.QUERY);
        assertThat(issue.getIn()).isEqualTo(InEnum.QUERY);
        assertThat(new InputValidationIssue().in(InEnum.QUERY).getIn()).isEqualTo(InEnum.QUERY);
    }

    @Test
    void name() {
        InputValidationIssue issue = new InputValidationIssue();
        issue.setName("name");
        assertThat(issue.getName()).isEqualTo("name");
        assertThat(new InputValidationIssue().name("name").getName()).isEqualTo("name");
    }

    @Test
    void value() {
        InputValidationIssue issue = new InputValidationIssue();
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
        assertThatIllegalArgumentException().isThrownBy(
                () -> new InputValidationIssue().in(InEnum.QUERY).input(input));
        assertThatIllegalArgumentException().isThrownBy(
                () -> new InputValidationIssue().name("name").input(input));
        assertThatIllegalArgumentException().isThrownBy(
                () -> new InputValidationIssue().value("value").input(input));
        assertThatIllegalArgumentException().isThrownBy(
                () -> new InputValidationIssue().input(input).in(InEnum.QUERY));
        assertThatIllegalArgumentException().isThrownBy(
                () -> new InputValidationIssue().input(input).name("name"));
        assertThatIllegalArgumentException().isThrownBy(
                () -> new InputValidationIssue().input(input).value("value"));
    }

    @Test
    void inputs() {
        List<Input<?>> inputs = Collections.singletonList(Input.query("name", "value"));
        InputValidationIssue issue = new InputValidationIssue();
        issue.setInputs(inputs);
        assertThat(issue.getInputs()).isUnmodifiable().isEqualTo(inputs);
        assertThat(new InputValidationIssue().inputs(inputs).getInputs()).isEqualTo(inputs);
    }

    @Test
    void inputsVarargs() {
        Input<?> input = Input.query("name", "value");
        InputValidationIssue issue = new InputValidationIssue();
        issue.setInputs(input);
        assertThat(issue.getInputs()).isUnmodifiable().containsExactly(input);
        assertThat(new InputValidationIssue().inputs(input).getInputs()).containsExactly(input);
    }

    @Test
    void input() {
        Input<?> input = Input.query("name", "value");
        InputValidationIssue issue = new InputValidationIssue();
        issue.addInput(input);
        assertThat(issue.getInputs()).containsExactly(input);
        assertThat(new InputValidationIssue().input(input).getInputs()).containsExactly(input);
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
