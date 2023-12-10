package com.github.hal4j.uritemplate.test.rfc6570;

import com.github.hal4j.uritemplate.URITemplate;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @see <a href="https://www.rfc-editor.org/rfc/rfc6570#section-3.2.2">3.2.2.  Simple String Expansion: <strong>{var}</strong></a>
 */
public class SimpleStringExpansionTest {

    private static final Map<String, String> EXAMPLES;

    static {
        Map<String, String> map = new HashMap<>();

        map.put("{var}","value");
        map.put("{hello}","Hello%20World%21");
        map.put("{half}","50%25");
        map.put("O{empty}X","OX");
        map.put("O{undef}X","OX");
        map.put("{x,y}","1024,768");
        map.put("{x,hello,y}","1024,Hello%20World%21,768");
        map.put("?{x,empty}","?1024,");
        map.put("?{x,undef}","?1024");
        map.put("?{undef,y}","?768");
        map.put("{var:3}","val");
        map.put("{var:30}","value");
        map.put("{list}","red,green,blue");
        map.put("{list*}","red,green,blue");
        map.put("{keys}","semi,%3B,dot,.,comma,%2C");
        map.put("{keys*}","semi=%3B,dot=.,comma=%2C");

        EXAMPLES = Collections.unmodifiableMap(map);
    }

    public static Stream<Arguments> testValues() {
        return EXAMPLES.entrySet().stream()
                .map(entry -> Arguments.of(entry.getKey(), entry.getValue()));
    }

    @ParameterizedTest
    @MethodSource("testValues")
    public void shouldPerformSimpleStringExpansionCorrectly(String template, String expectation) {
        String actual = new URITemplate(template).expand(RFC6570Definitions.VALUES).toString();
        assertEquals(expectation, actual, "Incorrect expansion of " + template);
    }
}
