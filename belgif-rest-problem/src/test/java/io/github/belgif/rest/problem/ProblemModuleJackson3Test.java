package io.github.belgif.rest.problem;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import tools.jackson.databind.JacksonModule;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.PropertyName;
import tools.jackson.databind.deser.BeanDeserializerBuilder;
import tools.jackson.databind.deser.ValueDeserializerModifier;
import tools.jackson.databind.introspect.BasicBeanDescription;
import tools.jackson.databind.jsontype.NamedType;
import tools.jackson.databind.type.SimpleType;
import tools.jackson.databind.type.TypeModifier;

@ExtendWith(MockitoExtension.class)
class ProblemModuleJackson3Test {

    private final ProblemModuleJackson3 module = new ProblemModuleJackson3(
            () -> {
                Map<String, Class<?>> problemTypes = new HashMap<>();
                problemTypes.put("urn:problem-type:belgif:badRequest", BadRequestProblem.class);
                return problemTypes;
            });

    @Mock
    private JacksonModule.SetupContext context;

    @Mock
    private BeanDeserializerBuilder beanDeserializerBuilder;

    @Captor
    private ArgumentCaptor<TypeModifier> typeModifierArgumentCaptor;

    @Captor
    private ArgumentCaptor<ValueDeserializerModifier> beanDeserializerModifierArgumentCaptor;

    @Test
    void setupModule() {
        module.setupModule(context);

        verify(context).registerSubtypes(new NamedType(BadRequestProblem.class, "urn:problem-type:belgif:badRequest"));
        verify(context).addTypeModifier(typeModifierArgumentCaptor.capture());
        verify(context).addDeserializerModifier(beanDeserializerModifierArgumentCaptor.capture());

        TypeModifier typeModifier = typeModifierArgumentCaptor.getValue();

        SimpleType problemType = new SimpleType(BadRequestProblem.class) {
        };

        assertThat(problemType.isThrowable()).isTrue();
        JavaType modifiedType = typeModifier.modifyType(problemType, null, null, null);
        assertThat(modifiedType.isThrowable()).isFalse();

        SimpleType regularType = new SimpleType(String.class) {
        };

        assertThat(typeModifier.modifyType(regularType, null, null, null)).isSameAs(regularType);

        ValueDeserializerModifier beanDeserializerModifier = beanDeserializerModifierArgumentCaptor.getValue();

        BasicBeanDescription beanDescription = mock(BasicBeanDescription.class);
        when(beanDescription.getType()).thenReturn(problemType);
        when(beanDescription.supplier()).thenCallRealMethod();

        beanDeserializerModifier.updateBuilder(null,
                beanDescription.supplier(), beanDeserializerBuilder);

        verify(beanDeserializerBuilder).removeProperty(new PropertyName("cause"));
        verify(beanDeserializerBuilder).removeProperty(new PropertyName("stackTrace"));
    }

}
