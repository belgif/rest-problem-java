package io.github.belgif.rest.problem;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBuilder;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.SimpleType;
import com.fasterxml.jackson.databind.type.TypeBindings;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.type.TypeModifier;

import io.github.belgif.rest.problem.api.Problem;
import io.github.belgif.rest.problem.registry.ProblemTypeRegistry;

/**
 * Problem module that can be registered on jackson {@link com.fasterxml.jackson.databind.ObjectMapper}.
 *
 * @see com.fasterxml.jackson.databind.ObjectMapper#registerModule(Module)
 */
public class ProblemModule extends SimpleModule {

    private static final TypeModifier PROBLEM_TYPE_MODIFIER = new NonThrowableProblemTypeModifier();

    private static final BeanDeserializerModifier PROBLEM_DESERIALIZER_MODIFIER = new ProblemBeanDeserializerModifier();

    public ProblemModule(ProblemTypeRegistry problemTypeRegistry) {
        NamedType[] problemTypes = problemTypeRegistry.getProblemTypes().entrySet().stream()
                .map(entry -> new NamedType(entry.getValue(), entry.getKey()))
                .toArray(NamedType[]::new);
        registerSubtypes(problemTypes);
    }

    @Override
    public void setupModule(SetupContext context) {
        super.setupModule(context);
        context.addTypeModifier(PROBLEM_TYPE_MODIFIER);
        context.addBeanDeserializerModifier(PROBLEM_DESERIALIZER_MODIFIER);
    }

    /**
     * TypeModifier that bypasses jackson's throwable-specific logic when deserializing problems.
     * First of all, because it is broken in combination with @JsonAnySetter on jackson < 2.16.2
     * (see <a href="https://github.com/FasterXML/jackson-databind/issues/4316">jackson-databind #4316</a>).
     * Additionally, as all throwable-specific fields are declared in @JsonIgnoreProperties anyway,
     * using the default POJO deserialization logic for problems is preferable.
     */
    private static class NonThrowableProblemTypeModifier extends TypeModifier {
        @Override
        public JavaType modifyType(JavaType type, Type jdkType, TypeBindings context, TypeFactory typeFactory) {
            if (type instanceof SimpleType && Problem.class.isAssignableFrom(type.getRawClass()) && type.isConcrete()) {
                return new NonThrowableJavaType((SimpleType) type);
            }
            return type;
        }

        private static class NonThrowableJavaType extends SimpleType {
            private NonThrowableJavaType(SimpleType type) {
                super(type);
            }

            @Override
            public boolean isThrowable() {
                return false;
            }
        }

    }

    /**
     * BeanDeserializerModifier that ignores setCause(Throwable) and setStackTrace(StackTraceElement[]) properties
     * of problems during deserialization, so they can instead be treated as additionalProperties.
     */
    private static class ProblemBeanDeserializerModifier extends BeanDeserializerModifier {

        private static final List<PropertyName> IGNORED_PROPERTIES =
                Arrays.asList(new PropertyName("cause"), new PropertyName("stackTrace"));

        @Override
        public BeanDeserializerBuilder updateBuilder(DeserializationConfig config, BeanDescription beanDesc,
                BeanDeserializerBuilder builder) {
            if (Problem.class.isAssignableFrom(beanDesc.getBeanClass()) && beanDesc.getType().isConcrete()) {
                for (PropertyName property : IGNORED_PROPERTIES) {
                    builder.removeProperty(property);
                }
            }
            return builder;
        }

    }

}
