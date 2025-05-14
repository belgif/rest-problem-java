package io.github.belgif.rest.problem.api;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSetter;

import io.github.belgif.rest.problem.config.ProblemConfig;
import io.github.belgif.rest.problem.i18n.I18N;

/**
 * Maps to InputValidationIssue in belgif/problem/v1/problem-v1.yaml.
 *
 * <p>
 * Note that this model class intentionally does not include the "status" and "instance" properties.
 * In problem-v1.yaml, InputValidationIssue technically inherits these properties from Problem,
 * but they are not meant to be used for input validation issues. In the unlikely scenario where these properties
 * would be needed, they can still be added/retrieved via the additionalProperties.
 * </p>
 *
 * @see InputValidationProblem
 */
@JsonInclude(value = Include.NON_EMPTY)
@JsonPropertyOrder(value = { "type", "href", "title", "detail", "in", "name", "value", "inputs" })
public class InputValidationIssue {

    public static final Comparator<InputValidationIssue> BY_NAME = Comparator.comparing(InputValidationIssue::getName);

    private static final String INPUTS_AND_IN_NAME_VALUE_ARE_MUTUALLY_EXCLUSIVE =
            "inputs[] and in/name/value are mutually exclusive";

    private static final String INPUTS_SETTER_ONE_ITEM =
            "inputs[] can not be set with a single item, use in(in, name, value) instead";

    private URI type;
    private URI href;
    private String title;
    private String detail;
    private InEnum in;
    private String name;
    private Object value;
    private final List<Input<?>> inputs = new ArrayList<>();
    private final Map<String, Object> additionalProperties = new HashMap<>();

    public InputValidationIssue() {
    }

    public InputValidationIssue(URI type, String title) {
        this.type = type;
        this.title = title;
    }

    public InputValidationIssue(InEnum in, String name, Object value) {
        this.in = in;
        this.name = name;
        this.value = value;
    }

    public InputValidationIssue(InEnum in, String name) {
        this.in = in;
        this.name = name;
    }

    public URI getType() {
        return type;
    }

    public void setType(URI type) {
        this.type = type;
    }

    public URI getHref() {
        return href;
    }

    public void setHref(URI href) {
        this.href = href;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public InEnum getIn() {
        return in;
    }

    public void setIn(InEnum in) {
        verifyNoInputs(in);
        this.in = in;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        verifyNoInputs(name);
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        verifyNoInputs(value);
        this.value = value;
    }

    public List<Input<?>> getInputs() {
        return Collections.unmodifiableList(inputs);
    }

    private void clearInNameValue() {
        this.in = null;
        this.name = null;
        this.value = null;
    }

    public void addInput(Input<?> input) {
        if (input == null) {
            return;
        }

        if (!ProblemConfig.isExtInputsArrayEnabled() && hasInNameValue()) {
            throw new IllegalStateException(
                    "inputs[] array extension is not enabled: " + ProblemConfig.PROPERTY_EXT_INPUTS_ARRAY_ENABLED);
        }

        if (hasInNameValue()) {
            this.inputs.add(new Input<>(in, name, value));
            this.inputs.add(input);
            clearInNameValue();
        } else if (!inputs.isEmpty()) {
            this.inputs.add(input);
        } else {
            in(input.getIn(), input.getName(), input.getValue());
        }
    }

    private void verifyNoInputs(Object valueToUpdate) {
        if (valueToUpdate == null) {
            return;
        }

        if (!inputs.isEmpty()) {
            throw new IllegalArgumentException(INPUTS_AND_IN_NAME_VALUE_ARE_MUTUALLY_EXCLUSIVE);
        }
    }

    private boolean hasInNameValue() {
        return in != null || name != null || value != null;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        additionalProperties.put(name, value);
    }

    /**
     * Don't fail on combined use of inputs[] and in/name/value when unmarshalling JSON.
     *
     * @param inputs the inputs[]
     */
    @JsonSetter("inputs")
    @SuppressWarnings("unused")
    private void setInputsFromJson(List<Input<?>> inputs) {
        this.inputs.clear();
        this.inputs.addAll(inputs);
    }

    /**
     * Don't fail on combined use of inputs[] and in/name/value when unmarshalling JSON.
     *
     * @param in the InEnum
     */
    @JsonSetter("in")
    @SuppressWarnings("unused")
    private void setInFromJson(InEnum in) {
        this.in = in;
    }

    /**
     * Don't fail on combined use of inputs[] and in/name/value when unmarshalling JSON.
     *
     * @param name the name
     */
    @JsonSetter("name")
    @SuppressWarnings("unused")
    private void setNameFromJson(String name) {
        this.name = name;
    }

    /**
     * Don't fail on combined use of inputs[] and in/name/value when unmarshalling JSON.
     *
     * @param value the value
     */
    @JsonSetter("value")
    @SuppressWarnings("unused")
    private void setValueFromJson(Object value) {
        this.value = value;
    }

    // builder methods

    public InputValidationIssue type(URI type) {
        setType(type);
        return this;
    }

    public InputValidationIssue type(String type) {
        return type(URI.create(type));
    }

    public InputValidationIssue href(URI href) {
        setHref(href);
        return this;
    }

    public InputValidationIssue href(String href) {
        return href(URI.create(href));
    }

    public InputValidationIssue title(String title) {
        setTitle(title);
        return this;
    }

    public InputValidationIssue detail(String detail) {
        setDetail(detail);
        return this;
    }

    /**
     * Set a localized detail message.
     *
     * @param key the resource bundle key (without ".detail" suffix)
     * @param args the optional arguments
     * @return this InputValidationIssue
     * @see I18N#getLocalizedString(String, Object...)
     */
    public InputValidationIssue localizedDetail(String key, Object... args) {
        setDetail(I18N.getLocalizedString(key + ".detail", args));
        return this;
    }

    /**
     * Set a localized detail message.
     *
     * @param context the context class where the resource bundle is loaded from
     * @param key the resource bundle key (without ".detail" suffix)
     * @param args the optional arguments
     * @return this InputValidationIssue
     * @see I18N#getLocalizedString(Class, String, Object...)
     */
    public InputValidationIssue localizedDetail(Class<?> context, String key, Object... args) {
        setDetail(I18N.getLocalizedString(context, key + ".detail", args));
        return this;
    }

    public InputValidationIssue in(InEnum in, String name, Object value) {
        return in(in).name(name).value(value);
    }

    public InputValidationIssue in(Input<?> input) {
        if (input != null) {
            return in(input.getIn(), input.getName(), input.getValue());
        } else {
            return this;
        }
    }

    public InputValidationIssue in(InEnum in) {
        setIn(in);
        return this;
    }

    public InputValidationIssue name(String name) {
        setName(name);
        return this;
    }

    public InputValidationIssue value(Object value) {
        setValue(value);
        return this;
    }

    /**
     * Embed a key-value pair inside the value property.
     *
     * @param key the key
     * @param value the value
     * @return this InputValidationIssue
     * @deprecated use {@link InputValidationIssue#inputs(Collection)} or
     *             {@link InputValidationIssue#inputs(Input, Input, Input[])} to
     *             reference multiple input values
     */
    @SuppressWarnings("unchecked")
    @Deprecated
    public InputValidationIssue valueEntry(String key, Object value) {
        if (this.value != null && !(this.value instanceof Map)) {
            throw new IllegalStateException(
                    "Cannot add value map entry because value is already set to an instance of "
                            + this.value.getClass());
        }
        if (this.value == null) {
            this.value = new LinkedHashMap<String, Object>();
        }
        ((Map<String, Object>) this.value).put(key, value);
        return this;
    }

    /**
     * Set the inputs[] to the given collection.
     *
     * @param inputs collection with input items to initialize the inputs[] array. Any previous inputs are removed.
     * @return this InputValidationIssue
     *
     * @throws IllegalArgumentException if in/name/value properties are not null (mutually exclusive with inputs[]),
     *         or when the collection only contains one non-null item.
     * @throws IllegalStateException if inputs[] array extension is not enabled
     */
    public InputValidationIssue inputs(Collection<Input<?>> inputs) {
        if (inputs == null) {
            return this;
        }

        if (!ProblemConfig.isExtInputsArrayEnabled()) {
            throw new IllegalStateException(
                    "inputs[] array extension is not enabled: " + ProblemConfig.PROPERTY_EXT_INPUTS_ARRAY_ENABLED);
        }

        if (hasInNameValue()) {
            throw new IllegalArgumentException(INPUTS_AND_IN_NAME_VALUE_ARE_MUTUALLY_EXCLUSIVE);
        }

        List<Input<?>> filteredInputs = inputs.stream().filter(Objects::nonNull).collect(Collectors.toList());

        if (filteredInputs.size() == 1) {
            throw new IllegalArgumentException(INPUTS_SETTER_ONE_ITEM);
        }

        this.inputs.clear();
        this.inputs.addAll(filteredInputs);
        return this;
    }

    /**
     * Set the inputs[] to the given inputs.
     *
     * @param firstInput the first input
     * @param secondInput the second input
     * @param otherInputs the varargs array with other inputs
     * @return this InputValidationIssue
     *
     * @throws IllegalArgumentException if in/name/value properties are not null (mutually exclusive with inputs[]),
     *         or when the parameters only contain one non-null item.
     */
    public InputValidationIssue inputs(Input<?> firstInput, Input<?> secondInput, Input<?>... otherInputs) {
        if (firstInput == null && secondInput == null && otherInputs == null) {
            return this;
        }

        List<Input<?>> inputsToAdd = new ArrayList<>(Arrays.asList(firstInput, secondInput));
        Collections.addAll(inputsToAdd, otherInputs);

        inputs(inputsToAdd);

        return this;
    }

    public InputValidationIssue additionalProperty(String key, Object value) {
        setAdditionalProperty(key, value);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        InputValidationIssue that = (InputValidationIssue) o;
        return Objects.equals(type, that.type) && Objects.equals(href, that.href)
                && Objects.equals(title, that.title) && Objects.equals(detail, that.detail)
                && in == that.in && Objects.equals(name, that.name) && Objects.equals(value, that.value)
                && Objects.equals(inputs, that.inputs)
                && Objects.equals(additionalProperties, that.additionalProperties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, href, title, detail, in, name, value, inputs, additionalProperties);
    }

    @Override
    public String toString() {
        return "InputValidationIssue{" +
                "type=" + type +
                ", href=" + href +
                ", title='" + title + '\'' +
                ", detail='" + detail + '\'' +
                ", in=" + in +
                ", name='" + name + '\'' +
                ", value=" + value +
                ", inputs=" + inputs +
                ", additionalProperties=" + additionalProperties +
                '}';
    }

}
