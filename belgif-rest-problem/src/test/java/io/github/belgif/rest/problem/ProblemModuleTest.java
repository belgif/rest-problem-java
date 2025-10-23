package io.github.belgif.rest.problem;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Disabled;
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
class ProblemModuleTest {

    private final ProblemModule module = new ProblemModule(
            () -> new NamedType[] { new NamedType(BadRequestProblem.class, "urn:problem-type:belgif:badRequest") });

    @Mock
    private JacksonModule.SetupContext context;

    @Mock
    private BeanDeserializerBuilder beanDeserializerBuilder;

    @Captor
    private ArgumentCaptor<TypeModifier> typeModifierArgumentCaptor;

    @Captor
    private ArgumentCaptor<ValueDeserializerModifier> beanDeserializerModifierArgumentCaptor;

    @Test
    @Disabled("TODO")
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

        beanDeserializerModifier.updateBuilder(null,
                BasicBeanDescription.forOtherUse(null, problemType, null).supplier()
                // TODO: can't get this to work yet
                , beanDeserializerBuilder);

        verify(beanDeserializerBuilder).removeProperty(new PropertyName("cause"));
        verify(beanDeserializerBuilder).removeProperty(new PropertyName("stackTrace"));
    }

}
