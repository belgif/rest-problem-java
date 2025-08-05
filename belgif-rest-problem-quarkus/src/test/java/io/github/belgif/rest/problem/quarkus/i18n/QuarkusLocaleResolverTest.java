package io.github.belgif.rest.problem.quarkus.i18n;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Locale;

import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.spi.CDI;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import io.github.belgif.rest.problem.i18n.I18N;
import io.quarkus.arc.Arc;
import io.quarkus.arc.ArcContainer;
import io.quarkus.arc.ManagedContext;

@ExtendWith(MockitoExtension.class)
class QuarkusLocaleResolverTest {

    private final QuarkusLocaleResolver localeResolver = new QuarkusLocaleResolver();

    @Mock
    private CDI cdi;

    @Mock
    private Instance instance;

    @Mock
    private LocaleHolder localeHolder;

    @Mock
    private ArcContainer arcContainer;

    @Mock
    private ManagedContext context;

    @Test
    void getLocale() {
        try (MockedStatic<CDI> cdiStatic = Mockito.mockStatic(CDI.class);
                MockedStatic<Arc> arcStatic = Mockito.mockStatic(Arc.class)) {
            arcStatic.when(Arc::container).thenReturn(arcContainer);
            when(arcContainer.requestContext()).thenReturn(context);
            when(context.isActive()).thenReturn(true);
            cdiStatic.when(CDI::current).thenReturn(this.cdi);
            when(this.cdi.select(LocaleHolder.class)).thenReturn(instance);
            when(instance.get()).thenReturn(localeHolder);
            when(localeHolder.getLocale()).thenReturn(Locale.forLanguageTag("nl-BE"));
            assertThat(localeResolver.getLocale()).isEqualTo(Locale.forLanguageTag("nl-BE"));
        }
    }

    @Test
    void getLocaleRequestScopeNotActive() {
        try (MockedStatic<Arc> arcStatic = Mockito.mockStatic(Arc.class)) {
            arcStatic.when(Arc::container).thenReturn(arcContainer);
            when(arcContainer.requestContext()).thenReturn(context);
            when(context.isActive()).thenReturn(false);
            assertThat(localeResolver.getLocale()).isEqualTo(I18N.DEFAULT_LOCALE);
        }
    }

}
