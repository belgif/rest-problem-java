package io.github.belgif.rest.problem.i18n;

import static org.assertj.core.api.Assertions.*;

import java.io.IOException;
import java.util.Properties;

import org.junit.jupiter.api.Test;

class ResourceBundleTest {

    private static final String PREFIX = "/io/github/belgif/rest/problem/";

    @Test
    void checkForMissingKeys() throws IOException {
        Properties english = loadProperties("Messages.properties");
        Properties french = loadProperties("Messages_fr.properties");
        Properties dutch = loadProperties("Messages_nl.properties");
        Properties german = loadProperties("Messages_de.properties");

        assertThat(french.keySet()).containsExactlyInAnyOrderElementsOf(english.keySet());
        assertThat(dutch.keySet()).containsExactlyInAnyOrderElementsOf(english.keySet());
        assertThat(german.keySet()).containsExactlyInAnyOrderElementsOf(english.keySet());
    }

    private Properties loadProperties(String name) throws IOException {
        Properties properties = new Properties();
        properties.load(getClass().getResourceAsStream(PREFIX + name));
        return properties;
    }

}
