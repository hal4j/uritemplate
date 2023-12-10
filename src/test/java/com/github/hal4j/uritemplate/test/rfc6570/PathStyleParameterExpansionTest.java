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
 * @see <a href="https://www.rfc-editor.org/rfc/rfc6570#section-3.2.7">3.2.7.  Path-Style Parameter Expansion: <strong>{;var}</strong></a>
 */
public class PathStyleParameterExpansionTest {

    private static final Map<String, String> EXAMPLES;

    static {
        Map<String, String> map = new HashMap<>();

        map.put("{;who}",";who=fred");
        map.put("{;half}",";half=50%25");
        map.put("{;empty}",";empty");
        map.put("{;v,empty,who}",";v=6;empty;who=fred");
        map.put("{;v,bar,who}",";v=6;who=fred");
        map.put("{;x,y}",";x=1024;y=768");
        map.put("{;x,y,empty}",";x=1024;y=768;empty");
        map.put("{;x,y,undef}",";x=1024;y=768");
        map.put("{;hello:5}",";hello=Hello");
        map.put("{;list}",";list=red,green,blue");
        map.put("{;list*}",";list=red;list=green;list=blue");
        map.put("{;keys}",";keys=semi,%3B,dot,.,comma,%2C");
        map.put("{;keys*}",";semi=%3B;dot=.;comma=%2C");

        EXAMPLES = Collections.unmodifiableMap(map);
    }

    public static Stream<Arguments> testValues() {
        return EXAMPLES.entrySet().stream()
                .map(entry -> Arguments.of(entry.getKey(), entry.getValue()));
    }

    @ParameterizedTest
    @MethodSource("testValues")
    public void shouldPerformPathStyleExpansionCorrectly(String template, String expectation) {
        String actual = new URITemplate(template).expand(RFC6570Definitions.VALUES).toString();
        assertEquals(expectation, actual, "Incorrect expansion of " + template);
    }

}