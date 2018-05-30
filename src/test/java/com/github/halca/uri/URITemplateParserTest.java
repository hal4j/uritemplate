package com.github.halca.uri;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static com.github.halca.uri.URITemplateParser.parseAndExpand;
import static java.util.Arrays.asList;
import static java.util.Collections.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class URITemplateParserTest {

    private final static Map<String, Object> SINGLE = singletonMap("p1", singletonList("v1"));
    private static final Map<String, Object> LIST = singletonMap("list", asList("red", "green","blue"));

    @Test
    public void shouldThrowNPEOnNull() {
        assertThrows(NullPointerException.class, () -> {
            parseAndExpand(null, SINGLE);
        });
    }

    @Test
    public void shouldReturnEmptyStringAsIs() {
        String uri = "";
        String result = parseAndExpand(uri, SINGLE);
        assertEquals(uri, result);
    }

    @Test
    public void shouldReturnNonTemplateValueAsIs() {
        String uri = "http://user:password@www.example.com:8443/path?query=value#fragment";
        String result = parseAndExpand(uri, SINGLE);
        assertEquals(uri, result);
    }

    @Test
    public void shouldSubstituteSimpleKeyValuePair() {
        String uri = "http://user:password@www.example.com:8443/{p1}?query=value#fragment";
        String expected = "http://user:password@www.example.com:8443/v1?query=value#fragment";
        String result = parseAndExpand(uri, SINGLE);
        assertEquals(expected, result);
    }

    @Test
    public void shouldSubstituteQueryParameter() {
        String uri = "http://www.example.com/?query=value{&p1}&more=test#fragment";
        String expected = "http://www.example.com/?query=value&p1=v1&more=test#fragment";
        String result = parseAndExpand(uri, SINGLE);
        assertEquals(expected, result);
    }

    @Test
    public void shouldIgnoreUnusedParametersInArray() {
        String uri = "http://www.example.com{?q*}";
        String expected = "http://www.example.com?q=1&q=2";
        String result = parseAndExpand(uri, asList(1, 2), "another");
        assertEquals(expected, result);
    }

    @Test
    public void shouldNotSubstituteQueryParameterIfNameIsDifferent() {
        String uri = "http://www.example.com/?query=value{&p2}";
        String expected = "http://www.example.com/?query=value{&p2}";
        String result = parseAndExpand(uri, SINGLE);
        assertEquals(expected, result);
    }

    @Test
    public void shouldSubstituteQueryFirstParameter() {
        String uri = "http://www.example.com{?p1}";
        String expected = "http://www.example.com?p1=v1";
        String result = parseAndExpand(uri, SINGLE);
        assertEquals(expected, result);
    }


    @Test
    public void shouldSubstituteAllQueryFirstParameters() {
        String uri = "http://www.example.com{?p1,p2}";
        String expected = "http://www.example.com?p1=v1&p2=v2,v3";
        Map<String, Object> map = new HashMap<>();
        map.put("p1", asList("v1"));
        map.put("p2", asList("v2", "v3"));
        String result = parseAndExpand(uri, map);
        assertEquals(expected, result);
    }

    @Test
    public void shouldOmitParameterWithEmptyListOfValues() {
        String uri = "http://www.example.com{?p1,p2}";
        String expected = "http://www.example.com?p2=v2,v3";
        Map<String, Object> map = new HashMap<>();
        map.put("p1", emptyList());
        map.put("p2", asList("v2", "v3"));
        String result = parseAndExpand(uri, map);
        assertEquals(expected, result);
    }

    @Test
    public void shouldOmitParameterWithEmptyListOfValuesPassedInArray() {
        String uri = "http://www.example.com{?p1,p2}";
        String expected = "http://www.example.com?p2=v2,v3";
        String result = parseAndExpand(uri, emptyList(), asList("v2", "v3"));
        assertEquals(expected, result);
    }

    @Test
    public void shouldIgnoreUnusedValuesInParamMap() {
        String uri = "http://www.example.com{?p1,p2}";
        String expected = "http://www.example.com?p2=v2,v3";
        Map<String, Object> map = new HashMap<>();
        map.put("p1", emptyList());
        map.put("p2", asList("v2", "v3"));
        map.put("p3", "unused");
        String result = parseAndExpand(uri, map);
        assertEquals(expected, result);
    }

    @Test
    public void shouldPreserveTemplateForMissingParameterInQuery() {
        String uri = "http://www.example.com{?p1,p2}";
        String expected = "http://www.example.com?p2=v2,v3{&p1}";
        Map<String, Object> map = new HashMap<>();
        map.put("p2", asList("v2", "v3"));
        String result = parseAndExpand(uri, map);
        assertEquals(expected, result);
    }

    @Test
    public void shouldPreserveTemplateExpansionForMissingParameterInQuery() {
        String uri = "http://www.example.com{?p1,p2,p3*}";
        String expected = "http://www.example.com?p2=v2&p2=v3{&p1,p3*}";
        Map<String, Object> map = new HashMap<>();
        map.put("p2", asList("v2", "v3"));
        String result = parseAndExpand(uri, map);
        assertEquals(expected, result);
    }


    @Test
    public void shouldPreserveTemplateForMissingParameterInPath() {
        String uri = "http://www.example.com{/p1,p2,p3*}";
        String expected = "http://www.example.com{/p1*}/v2/v3{/p3*}";
        Map<String, Object> map = new HashMap<>();
        map.put("p2", asList("v2", "v3"));
        String result = parseAndExpand(uri, map);
        assertEquals(expected, result);
    }

    @Test
    public void shouldNotSubstituteQueryFirstParameterIfNameIsDifferent() {
        String uri = "http://www.example.com{?p2}";
        String expected = "http://www.example.com{?p2}";
        String result = parseAndExpand(uri, SINGLE);
        assertEquals(expected, result);
    }

    @Test
    public void shouldSubstitutePathFirstSegment() {
        String uri = "http://www.example.com{/p1}";
        String expected = "http://www.example.com/v1";
        String result = parseAndExpand(uri, SINGLE);
        assertEquals(expected, result);
    }

    @Test
    public void shouldSubstitutePathFirstSegmentWithoutModifiers() {
        String uri = "http://www.example.com/{p1}/test";
        String expected = "http://www.example.com/v1/test";
        String result = parseAndExpand(uri, SINGLE);
        assertEquals(expected, result);
    }

    @Test
    public void shouldConcatenatePathElementsWithCommaWithoutModifiers() {
        String uri = "http://www.example.com/{p1}/test";
        String expected = "http://www.example.com/v1,v2/test";
        String result = parseAndExpand(uri, singletonMap("p1", asList("v1", "v2")));
        assertEquals(expected, result);
    }

    @Test
    public void shouldConcatenatePathElementsWithCommaWithStarModifier() {
        String uri = "http://www.example.com/{p1*}/test";
        String expected = "http://www.example.com/v1,v2/test";
        String result = parseAndExpand(uri, singletonMap("p1", asList("v1", "v2")));
        assertEquals(expected, result);
    }

    @Test
    public void shouldPreservePathSeparatorWhenTwoPathSegmentsAreTemplates() {
        String uri = "http://www.example.com/rels/{p1}/{p2}";
        String expected = "http://www.example.com/rels/v1/v2";
        String result = parseAndExpand(uri,
                Map.of("p1", singletonList("v1"), "p2", singletonList("v2")));
        assertEquals(expected, result);
    }

    @Test
    public void shouldNotSubstitutePathFirstSegmentIfNameIsDifferent() {
        String uri = "http://www.example.com{/p2}";
        String expected = "http://www.example.com{/p2}";
        String result = parseAndExpand(uri, SINGLE);
        assertEquals(expected, result);
    }

    @Test
    public void shouldSubstituteHostname() {
        String uri = "{service:scheme}://{service:hostname}/some/{path}{?q*}";
        String expected = "https://www.example.com/some/api?q=1&q=2";
        String result = parseAndExpand(uri, "https", "www.example.com","api", asList(1, 2));
        assertEquals(expected, result);
    }

    @Test
    public void shouldSubstituteHostnameFromMap() {
        String uri = "{service:scheme}://{service:hostname}/api";
        String expected = "https://www.example.com/api";
        Map<String, Object> map = new HashMap<>();
        map.put("service:scheme", "https");
        map.put("service:hostname", "www.example.com");
        String result = parseAndExpand(uri, map);
        assertEquals(expected, result);
    }

    @Test
    public void shouldSubstituteDomainName() {
        String uri = "www{.dom*}";
        String expected = "www.example.com";
        String result = parseAndExpand(uri, singletonMap("dom", asList("example", "com")));
        assertEquals(expected, result);
    }

    @Test
    public void shouldExpandFragment() {
        String uri = "{#list}";
        String expected = "#red,green,blue";
        String result = parseAndExpand(uri, LIST);
        assertEquals(expected, result);
    }

    @Test
    public void shouldExpandByNameMultipleTimes() {
        String uri = "{.who,who}";
        String expected = ".fred.fred";
        String result = parseAndExpand(uri, singletonMap("who", "fred"));
        assertEquals(expected, result);
    }

    @Test
    public void shouldTruncateValue() {
        String uri = "X{.var:3}";
        String expected = "X.val";
        String result = parseAndExpand(uri, singletonMap("var", "value"));
        assertEquals(expected, result);
    }

    @Test
    public void shouldCorrectlyTruncateSpecialCharacter() {
        String uri = "{semi:2}";
        String expected = "%3B";
        String result = parseAndExpand(uri, singletonMap("semi", ";"));
        assertEquals(expected, result);
    }

    @Test
    public void shouldExpandPreEncodedValueCorrectly() {
        String uri = "{+base}/api/path";
        String expected = "https://www.example.com/api/path";
        String result = parseAndExpand(uri, singletonMap("base", "https://www.example.com"));
        assertEquals(expected, result);
    }

    @Test
    public void shouldExpandFragmentExploded() {
        String uri = "{#list*}";
        String expected = "#red,green,blue";
        String result = parseAndExpand(uri, LIST);
        assertEquals(expected, result);
    }

    @Test
    public void shouldExpandPathLikeExpression() {
        String uri = "path{;list}";
        String expected = "path;red,green,blue";
        String result = parseAndExpand(uri, LIST);
        assertEquals(expected, result);
    }

    @Test
    public void shouldExpandPathLikeExpressionExploded() {
        String uri = "path{;list*}";
        String expected = "path;list=red;list=green;list=blue";
        String result = parseAndExpand(uri, LIST);
        assertEquals(expected, result);
    }

    @Test
    public void shouldPreserveTemplateInExpandPathLikeExpressionExploded() {
        String uri = "path{;p1,list,p3*}";
        String expected = "path{;p1*};list=red;list=green;list=blue{;p3*}";
        String result = parseAndExpand(uri, LIST);
        assertEquals(expected, result);
    }
}
