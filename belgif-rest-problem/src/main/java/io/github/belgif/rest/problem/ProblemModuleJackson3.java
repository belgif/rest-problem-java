package io.github.belgif.rest.problem;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import io.github.belgif.rest.problem.api.Problem;
import io.github.belgif.rest.problem.registry.ProblemTypeRegistry;
import tools.jackson.databind.*;
import tools.jackson.databind.deser.BeanDeserializerBuilder;
import tools.jackson.databind.deser.ValueDeserializerModifier;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.jsontype.NamedType;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.type.SimpleType;
import tools.jackson.databind.type.TypeBindings;
import tools.jackson.databind.type.TypeFactory;
import tools.jackson.databind.type.TypeModifier;

/**
 * Problem module that can be registered on jackson {@link tools.jackson.databind.ObjectMapper}.
 *
 * @see JsonMapper#builder()
 * @see tools.jackson.databind.cfg.MapperBuilder#addModule(JacksonModule)
 */
public class ProblemModuleJackson3 extends SimpleModule {

    private static final TypeModifier PROBLEM_TYPE_MODIFIER = new NonThrowableProblemTypeModifier();

    private static final ValueDeserializerModifier PROBLEM_DESERIALIZER_MODIFIER =
            new ProblemBeanDeserializerModifier();

    public ProblemModuleJackson3(ProblemTypeRegistry problemTypeRegistry) {
        NamedType[] problemTypes = problemTypeRegistry.getProblemTypes().entrySet()
                .stream()
                .map(entry -> new NamedType(entry.getValue(), entry.getKey()))
                .toArray(NamedType[]::new);
        registerSubtypes(problemTypes);
    }

    @Override
    public void setupModule(SetupContext context) {
        super.setupModule(context);
        context.addTypeModifier(PROBLEM_TYPE_MODIFIER);
        context.addDeserializerModifier(PROBLEM_DESERIALIZER_MODIFIER);
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
    private static class ProblemBeanDeserializerModifier extends ValueDeserializerModifier {

        private static final List<PropertyName> IGNORED_PROPERTIES =
                Arrays.asList(new PropertyName("cause"), new PropertyName("stackTrace"));

        @Override
        public BeanDeserializerBuilder updateBuilder(DeserializationConfig config, BeanDescription.Supplier beanDesc,
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
