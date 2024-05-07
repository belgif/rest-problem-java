package io.github.belgif.rest.problem;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.Module.SetupContext;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBuilder;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.introspect.BasicBeanDescription;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.type.SimpleType;
import com.fasterxml.jackson.databind.type.TypeModifier;

@ExtendWith(MockitoExtension.class)
class ProblemModuleTest {

    private final ProblemModule module = new ProblemModule(
            () -> new NamedType[] { new NamedType(BadRequestProblem.class, "urn:problem-type:belgif:badRequest") });

    @Mock
    private SetupContext context;

    @Mock
    private BeanDeserializerBuilder beanDeserializerBuilder;

    @Captor
    private ArgumentCaptor<TypeModifier> typeModifierArgumentCaptor;

    @Captor
    private ArgumentCaptor<BeanDeserializerModifier> beanDeserializerModifierArgumentCaptor;

    @Test
    void setupModule() {
        module.setupModule(context);

        verify(context).registerSubtypes(new NamedType(BadRequestProblem.class, "urn:problem-type:belgif:badRequest"));
        verify(context).addTypeModifier(typeModifierArgumentCaptor.capture());
        verify(context).addBeanDeserializerModifier(beanDeserializerModifierArgumentCaptor.capture());

        TypeModifier typeModifier = typeModifierArgumentCaptor.getValue();

        SimpleType problemType = new SimpleType(BadRequestProblem.class) {
        };

        assertThat(problemType.isThrowable()).isTrue();
        JavaType modifiedType = typeModifier.modifyType(problemType, null, null, null);
        assertThat(modifiedType.isThrowable()).isFalse();

        SimpleType regularType = new SimpleType(String.class) {
        };

        assertThat(typeModifier.modifyType(regularType, null, null, null)).isSameAs(regularType);

        BeanDeserializerModifier beanDeserializerModifier = beanDeserializerModifierArgumentCaptor.getValue();

        beanDeserializerModifier.updateBuilder(null,
                new BasicBeanDescription(null, problemType, null, null) {
                }, beanDeserializerBuilder);

        verify(beanDeserializerBuilder).removeProperty(new PropertyName("cause"));
        verify(beanDeserializerBuilder).removeProperty(new PropertyName("stackTrace"));
    }

}
