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
 * @see <a href="https://www.rfc-editor.org/rfc/rfc6570#section-3.2.5">3.2.5.  Label Expansion with Dot-Prefix: <strong>{.var}</strong></a>
 */
public class DotPrefixExpansionTest {

    private static final Map<String, String> EXAMPLES;

    static {
        Map<String, String> map = new HashMap<>();

        map.put("{.who}",".fred");
        map.put("{.who,who}",".fred.fred");
        map.put("{.half,who}",".50%25.fred");
        map.put("www{.dom*}","www.example.com");
        map.put("X{.var}","X.value");
        map.put("X{.empty}","X.");
        map.put("X{.undef}","X");
        map.put("X{.var:3}","X.val");
        map.put("X{.list}","X.red,green,blue");
        map.put("X{.list*}","X.red.green.blue");
        map.put("X{.keys}","X.semi,%3B,dot,.,comma,%2C");
        map.put("X{.keys*}","X.semi=%3B.dot=..comma=%2C");
        map.put("X{.empty_keys}","X");
        map.put("X{.empty_keys*}","X");

        EXAMPLES = Collections.unmodifiableMap(map);
    }

    public static Stream<Arguments> testValues() {
        return EXAMPLES.entrySet().stream()
                .map(entry -> Arguments.of(entry.getKey(), entry.getValue()));
    }

    @ParameterizedTest
    @MethodSource("testValues")
    public void shouldPerformDotPrefixExpansionCorrectly(String template, String expectation) {
        String actual = new URITemplate(template).expand(RFC6570Definitions.VALUES).toString();
        assertEquals(expectation, actual, "Incorrect expansion of " + template);
    }

}
