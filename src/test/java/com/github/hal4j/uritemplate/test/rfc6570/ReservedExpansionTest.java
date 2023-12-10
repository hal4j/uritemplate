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
 * @see <a href="https://www.rfc-editor.org/rfc/rfc6570#section-3.2.3">3.2.3.  Reserved Expansion: <strong>{+var}</strong></a>
 */
public class ReservedExpansionTest {

    private static final Map<String, String> EXAMPLES;

    static {
        Map<String, String> map = new HashMap<>();

        map.put("{+var}","value");
        map.put("{+hello}","Hello%20World!");
        map.put("{+half}","50%25");

        map.put("{base}index","http%3A%2F%2Fexample.com%2Fhome%2Findex");
        map.put("{+base}index","http://example.com/home/index");
        map.put("O{+empty}X","OX");
        map.put("O{+undef}X","OX");

        map.put("{+path}/here","/foo/bar/here");
        map.put("here?ref={+path}","here?ref=/foo/bar");
        map.put("up{+path}{var}/here","up/foo/barvalue/here");
        map.put("{+x,hello,y}","1024,Hello%20World!,768");
        map.put("{+path,x}/here","/foo/bar,1024/here");

        map.put("{+path:6}/here","/foo/b/here");
        map.put("{+list}","red,green,blue");
        map.put("{+list*}","red,green,blue");
        map.put("{+keys}","semi,;,dot,.,comma,,");
        map.put("{+keys*}","semi=;,dot=.,comma=,");
        EXAMPLES = Collections.unmodifiableMap(map);
    }

    public static Stream<Arguments> testValues() {
        return EXAMPLES.entrySet().stream()
                .map(entry -> Arguments.of(entry.getKey(), entry.getValue()));
    }

    @ParameterizedTest
    @MethodSource("testValues")
    public void shouldPerformReservedExpansionCorrectly(String template, String expectation) {
        String actual = new URITemplate(template).expand(RFC6570Definitions.VALUES).toString();
        assertEquals(expectation, actual, "Incorrect expansion of " + template);
    }

}
