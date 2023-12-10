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
 * @see <a href="https://www.rfc-editor.org/rfc/rfc6570#section-3.2.6">3.2.6.  Path Segment Expansion: <strong>{/var}</strong></a>
 */
public class PathSegmentExpansionTest {

    private static final Map<String, String> EXAMPLES;

    static {
        Map<String, String> map = new HashMap<>();

        map.put("{/who}","/fred");
        map.put("{/who,who}","/fred/fred");
        map.put("{/half,who}","/50%25/fred");
        map.put("{/who,dub}","/fred/me%2Ftoo");
        map.put("{/var}","/value");
        map.put("{/var,empty}","/value/");
        map.put("{/var,undef}","/value");
        map.put("{/var,x}/here","/value/1024/here");
        map.put("{/var:1,var}","/v/value");
        map.put("{/list}","/red,green,blue");
        map.put("{/list*}","/red/green/blue");
        map.put("{/list*,path:4}","/red/green/blue/%2Ffoo");
        map.put("{/keys}","/semi,%3B,dot,.,comma,%2C");
        map.put("{/keys*}","/semi=%3B/dot=./comma=%2C");

        EXAMPLES = Collections.unmodifiableMap(map);
    }

    public static Stream<Arguments> testValues() {
        return EXAMPLES.entrySet().stream()
                .map(entry -> Arguments.of(entry.getKey(), entry.getValue()));
    }

    @ParameterizedTest
    @MethodSource("testValues")
    public void shouldPerformPathSegmentExpansionCorrectly(String template, String expectation) {
        String actual = new URITemplate(template).expand(RFC6570Definitions.VALUES).toString();
        assertEquals(expectation, actual, "Incorrect expansion of " + template);
    }

}
