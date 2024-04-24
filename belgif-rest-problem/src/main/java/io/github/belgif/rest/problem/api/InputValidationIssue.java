package io.github.belgif.rest.problem.api;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Maps to InputValidationIssue in belgif/problem/v1/problem-v1.yaml.
 *
 * @see InputValidationProblem
 */
@JsonInclude(value = Include.NON_DEFAULT)
@JsonPropertyOrder(value = { "type", "href", "title", "status", "detail", "instance", "in", "name", "value", "inputs" })
public class InputValidationIssue {

    private static final String INPUTS_AND_IN_NAME_VALUE_ARE_MUTUALLY_EXCLUSIVE =
            "inputs[] and in/name/value are mutually exclusive";

    private URI type;
    private URI href;
    private String title;
    private int status;
    private String detail;
    private URI instance;
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

    /**
     * @return the status
     * @deprecated this status property is generally not intended to be used, it is only provided because
     *             InputValidationIssue inherits from Problem in belgif-openapi-problem
     */
    @Deprecated
    public int getStatus() {
        return status;
    }

    /**
     * @param status the status
     * @deprecated this status property is generally not intended to be used, it is only provided because
     *             InputValidationIssue inherits from Problem in belgif-openapi-problem
     */
    @Deprecated
    public void setStatus(int status) {
        this.status = status;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    /**
     * @return the instance
     * @deprecated this instance property is generally not intended to be used, it is only provided because
     *             InputValidationIssue inherits from Problem in belgif-openapi-problem
     */
    @Deprecated
    public URI getInstance() {
        return instance;
    }

    /**
     * @param instance the instance
     * @deprecated this instance property is generally not intended to be used, it is only provided because
     *             InputValidationIssue inherits from Problem in belgif-openapi-problem
     */
    @Deprecated
    public void setInstance(URI instance) {
        this.instance = instance;
    }

    public InEnum getIn() {
        return in;
    }

    public void setIn(InEnum in) {
        verifyNoInputs();
        this.in = in;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        verifyNoInputs();
        this.name = name;
    }

    @JsonInclude(value = Include.NON_NULL, content = Include.ALWAYS)
    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        verifyNoInputs();
        this.value = value;
    }

    public List<Input<?>> getInputs() {
        return Collections.unmodifiableList(inputs);
    }

    /**
     * Don't fail on combined use of inputs[] and in/name/value when unmarshalling JSON.
     *
     * @param inputs the inputs[]
     */
    @JsonSetter("inputs")
    private void setInputsFromJson(List<Input<?>> inputs) {
        this.inputs.clear();
        this.inputs.addAll(inputs);
    }

    public void setInputs(List<Input<?>> inputs) {
        verifyNoInNameValue();
        this.inputs.clear();
        this.inputs.addAll(inputs);
    }

    public void setInputs(Input<?>... inputs) {
        verifyNoInNameValue();
        this.inputs.clear();
        this.inputs.addAll(Arrays.asList(inputs));
    }

    public void addInput(Input<?> input) {
        verifyNoInNameValue();
        inputs.add(input);
    }

    private void verifyNoInputs() {
        if (!inputs.isEmpty()) {
            throw new IllegalArgumentException(INPUTS_AND_IN_NAME_VALUE_ARE_MUTUALLY_EXCLUSIVE);
        }
    }

    private void verifyNoInNameValue() {
        if (in != null || name != null || value != null) {
            throw new IllegalArgumentException(INPUTS_AND_IN_NAME_VALUE_ARE_MUTUALLY_EXCLUSIVE);
        }
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        additionalProperties.put(name, value);
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

    public InputValidationIssue in(InEnum in, String name, Object value) {
        return in(in).name(name).value(value);
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
     * @deprecated use {@link InputValidationIssue#input(Input)} to reference multiple input values
     */
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
        ((Map) this.value).put(key, value);
        return this;
    }

    public InputValidationIssue input(Input input) {
        addInput(input);
        return this;
    }

    public InputValidationIssue inputs(List<Input<?>> inputs) {
        setInputs(inputs);
        return this;
    }

    public InputValidationIssue inputs(Input<?>... inputs) {
        setInputs(inputs);
        return this;
    }

    public InputValidationIssue additionalProperty(String key, Object value) {
        setAdditionalProperty(key, value);
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(additionalProperties, detail, href, in, instance, name, status, title, type, value, inputs);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof InputValidationIssue)) {
            return false;
        }
        InputValidationIssue other = (InputValidationIssue) obj;
        return Objects.equals(additionalProperties, other.additionalProperties) && Objects.equals(detail, other.detail)
                && Objects.equals(href, other.href) && in == other.in && Objects.equals(instance, other.instance)
                && Objects.equals(name, other.name) && status == other.status && Objects.equals(title, other.title)
                && Objects.equals(type, other.type) && Objects.equals(value, other.value)
                && Objects.equals(inputs, other.inputs);
    }

    @Override
    public String toString() {
        return "InputValidationIssue{" +
                "type=" + type +
                ", href=" + href +
                ", title='" + title + '\'' +
                ", status=" + status +
                ", detail='" + detail + '\'' +
                ", instance=" + instance +
                ", in=" + in +
                ", name='" + name + '\'' +
                ", value=" + value +
                ", inputs=" + inputs +
                ", additionalProperties=" + additionalProperties +
                '}';
    }
}
