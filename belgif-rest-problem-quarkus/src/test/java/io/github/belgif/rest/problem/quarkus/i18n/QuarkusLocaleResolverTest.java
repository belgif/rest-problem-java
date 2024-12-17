package io.github.belgif.rest.problem.quarkus.i18n;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
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

@ExtendWith(MockitoExtension.class)
class QuarkusLocaleResolverTest {

    private final QuarkusLocaleResolver localeResolver = new QuarkusLocaleResolver();

    @Mock
    private CDI cdi;

    @Mock
    private Instance instance;

    @Mock
    private LocaleHolder localeHolder;

    @Test
    void getLocale() {
        try (MockedStatic<CDI> mock = Mockito.mockStatic(CDI.class)) {
            mock.when(CDI::current).thenReturn(cdi);
            when(cdi.select(LocaleHolder.class)).thenReturn(instance);
            when(instance.get()).thenReturn(localeHolder);
            when(localeHolder.getLocale()).thenReturn(Locale.forLanguageTag("nl-BE"));
            assertThat(localeResolver.getLocale()).isEqualTo(Locale.forLanguageTag("nl-BE"));
        }
    }

}
