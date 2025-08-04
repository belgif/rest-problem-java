package io.github.belgif.rest.problem.quarkus.i18n;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Locale;

import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.context.spi.Context;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.CDI;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import io.github.belgif.rest.problem.i18n.I18N;

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
    private BeanManager beanManager;

    @Mock
    private Context context;

    @Test
    void getLocale() {
        try (MockedStatic<CDI> mock = Mockito.mockStatic(CDI.class)) {
            mock.when(CDI::current).thenReturn(cdi);
            when(cdi.getBeanManager()).thenReturn(beanManager);
            when(beanManager.getContext(RequestScoped.class)).thenReturn(context);
            when(context.isActive()).thenReturn(true);
            when(cdi.select(LocaleHolder.class)).thenReturn(instance);
            when(instance.get()).thenReturn(localeHolder);
            when(localeHolder.getLocale()).thenReturn(Locale.forLanguageTag("nl-BE"));
            assertThat(localeResolver.getLocale()).isEqualTo(Locale.forLanguageTag("nl-BE"));
        }
    }

    @Test
    void getLocaleRequestScopeNotActive() {
        try (MockedStatic<CDI> mock = Mockito.mockStatic(CDI.class)) {
            mock.when(CDI::current).thenReturn(cdi);
            when(cdi.getBeanManager()).thenReturn(beanManager);
            when(beanManager.getContext(RequestScoped.class)).thenReturn(context);
            when(context.isActive()).thenReturn(false);
            assertThat(localeResolver.getLocale()).isEqualTo(I18N.DEFAULT_LOCALE);
        }
    }

}
