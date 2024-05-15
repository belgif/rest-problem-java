package io.github.belgif.rest.problem.internal;

import static org.assertj.core.api.Assertions.*;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class AnnotationUtilTest {

    @Test
    void notFound() {
        assertThat(AnnotationUtil.findParamAnnotation(getMethod("notFound"), 0, Foo.class)).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = { "ownMethod", "interfaceMethod", "parentInterfaceMethod", "parentMethod",
            "grandParentMethod", "overriddenParentMethod", "overriddenGrandParentMethod" })
    void found(String method) {
        Optional<? extends Annotation> annotation = AnnotationUtil.findParamAnnotation(getMethod(method), 0, Foo.class);
        assertThat(annotation).isPresent();
        assertThat(annotation.get()).isInstanceOf(Foo.class);
        Foo foo = (Foo) annotation.get();
        assertThat(foo.value()).isEqualTo(method);
    }

    @Test
    void byName() {
        Optional<? extends Annotation> annotation =
                AnnotationUtil.findParamAnnotation(getMethod("ownMethod"), "i", Foo.class);
        assertThat(annotation).isPresent();
    }

    @Test
    void byNameFallback() {
        Optional<? extends Annotation> annotation =
                AnnotationUtil.findParamAnnotation(getMethod("ownMethod"), "fallback", Foo.class);
        assertThat(annotation).isPresent();
    }

    @Test
    void byNameNotFound() {
        assertThatIllegalStateException().isThrownBy(
                () -> AnnotationUtil.findParamAnnotation(getMethod("notFound"), "notFound", Foo.class));
    }

    private static Method getMethod(String name) {
        return Arrays.stream(MyClass.class.getMethods()).filter(m -> m.getName().equals(name)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Method not found: " + name));
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    public @interface Foo {
        String value() default "";
    }

    static class MyClass extends ParentClass implements MyInterface {

        public void notFound(int i, String test) {
        }

        public void ownMethod(@Foo("ownMethod") int i) {
        }

        @Override
        public void interfaceMethod(int i) {
        }

        @Override
        public void parentInterfaceMethod(int i) {
        }

        @Override
        public void overriddenParentMethod(int i) {
            super.overriddenParentMethod(i);
        }

        @Override
        public void overriddenGrandParentMethod(int i) {
            super.overriddenGrandParentMethod(i);
        }

    }

    static class ParentClass extends GrandParentClass {

        public void parentMethod(@Foo("parentMethod") int i) {
        }

        public void overriddenParentMethod(@Foo("overriddenParentMethod") int i) {
        }

    }

    static class GrandParentClass {

        public void grandParentMethod(@Foo("grandParentMethod") int i) {
        }

        public void overriddenGrandParentMethod(@Foo("overriddenGrandParentMethod") int i) {
        }

    }

    interface MyInterface extends ParentInterface {

        void interfaceMethod(@Foo("interfaceMethod") int i);

    }

    interface ParentInterface {

        void parentInterfaceMethod(@Foo("parentInterfaceMethod") int i);

    }

}
