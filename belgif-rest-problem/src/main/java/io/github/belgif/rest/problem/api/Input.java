package io.github.belgif.rest.problem.api;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Input model for {@link InputValidationIssue#getInputs()}.
 *
 * <p>
 * The recommended way for clients to instantiate an Input is through these factory methods:
 * </p>
 * <ul>
 * <li>{@link #query(String, Object)}</li>
 * <li>{@link #body(String, Object)}</li>
 * <li>{@link #path(String, Object)}</li>
 * <li>{@link #header(String, Object)}</li>
 * </ul>
 *
 * @param <V> the input value type
 */
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({ "in", "name", "value" })
public class Input<V> {

    private InEnum in;
    private String name;
    private V value;

    public Input() {
    }

    public Input(InEnum in, String name, V value) {
        this.in = in;
        this.name = name;
        this.value = value;
    }

    public InEnum getIn() {
        return in;
    }

    public void setIn(InEnum in) {
        this.in = in;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Input<?> input = (Input<?>) o;
        return in == input.in && Objects.equals(name, input.name) && Objects.equals(value, input.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(in, name, value);
    }

    @Override
    public String toString() {
        return "Input{" +
                "in=" + in +
                ", name='" + name + '\'' +
                ", value=" + value +
                '}';
    }

    public static <V> Input<V> body(String name, V value) {
        return new Input<>(InEnum.BODY, name, value);
    }

    public static <V> Input<V> query(String name, V value) {
        return new Input<>(InEnum.QUERY, name, value);
    }

    public static <V> Input<V> path(String name, V value) {
        return new Input<>(InEnum.PATH, name, value);
    }

    public static <V> Input<V> header(String name, V value) {
        return new Input<>(InEnum.HEADER, name, value);
    }

}
