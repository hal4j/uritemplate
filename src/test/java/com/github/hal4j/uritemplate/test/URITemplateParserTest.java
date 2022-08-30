package com.github.hal4j.uritemplate.test;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static com.github.hal4j.uritemplate.URITemplateParser.parseAndExpand;
import static java.util.Arrays.asList;
import static java.util.Collections.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class URITemplateParserTest {

    private static final Map<String, Object> SINGLE = singletonMap("p1", singletonList("v1"));
    private static final Map<String, Object> LIST = singletonMap("list", asList("red", "green","blue"));

    @Test
    void shouldThrowNPEOnNull() {
        assertThrows(NullPointerException.class,
                () -> parseAndExpand(null, SINGLE));
    }

    @Test
    void shouldReturnEmptyStringAsIs() {
        String uri = "";
        String result = parseAndExpand(uri, SINGLE);
        assertEquals(uri, result);
    }

    @Test
    void shouldReturnNonTemplateValueAsIs() {
        String uri = "http://user:password@www.example.com:8443/path?query=value#fragment";
        String result = parseAndExpand(uri, SINGLE);
        assertEquals(uri, result);
    }

    @Test
    void shouldSubstituteSimpleKeyValuePair() {
        String uri = "http://user:password@www.example.com:8443/{p1}?query=value#fragment";
        String expected = "http://user:password@www.example.com:8443/v1?query=value#fragment";
        String result = parseAndExpand(uri, SINGLE);
        assertEquals(expected, result);
    }

    @Test
    void shouldSubstituteQueryParameter() {
        String uri = "http://www.example.com/?query=value{&p1}&more=test#fragment";
        String expected = "http://www.example.com/?query=value&p1=v1&more=test#fragment";
        String result = parseAndExpand(uri, SINGLE);
        assertEquals(expected, result);
    }

    @Test
    void shouldIgnoreUnusedParametersInArray() {
        String uri = "http://www.example.com{?q*}";
        String expected = "http://www.example.com?q=1&q=2";
        String result = parseAndExpand(uri, asList(1, 2), "another");
        assertEquals(expected, result);
    }

    @Test
    void shouldNotSubstituteQueryParameterIfNameIsDifferent() {
        String uri = "http://www.example.com/?query=value{&p2}";
        String expected = "http://www.example.com/?query=value{&p2}";
        String result = parseAndExpand(uri, SINGLE);
        assertEquals(expected, result);
    }

    @Test
    void shouldSubstituteQueryFirstParameter() {
        String uri = "http://www.example.com{?p1}";
        String expected = "http://www.example.com?p1=v1";
        String result = parseAndExpand(uri, SINGLE);
        assertEquals(expected, result);
    }


    @Test
    void shouldSubstituteAllQueryFirstParameters() {
        String uri = "http://www.example.com{?p1,p2}";
        String expected = "http://www.example.com?p1=v1&p2=v2,v3";
        Map<String, Object> map = new HashMap<>();
        map.put("p1", singletonList("v1"));
        map.put("p2", asList("v2", "v3"));
        String result = parseAndExpand(uri, map);
        assertEquals(expected, result);
    }

    @Test
    void shouldOmitParameterWithEmptyListOfValues() {
        String uri = "http://www.example.com{?p1,p2}";
        String expected = "http://www.example.com?p2=v2,v3";
        Map<String, Object> map = new HashMap<>();
        map.put("p1", emptyList());
        map.put("p2", asList("v2", "v3"));
        String result = parseAndExpand(uri, map);
        assertEquals(expected, result);
    }

    @Test
    void shouldOmitParameterWithEmptyListOfValuesPassedInArray() {
        String uri = "http://www.example.com{?p1,p2}";
        String expected = "http://www.example.com?p2=v2,v3";
        String result = parseAndExpand(uri, emptyList(), asList("v2", "v3"));
        assertEquals(expected, result);
    }

    @Test
    void shouldIgnoreUnusedValuesInParamMap() {
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
    void shouldPreserveTemplateForMissingParameterInQuery() {
        String uri = "http://www.example.com{?p1,p2}";
        String expected = "http://www.example.com?p2=v2,v3{&p1}";
        Map<String, Object> map = new HashMap<>();
        map.put("p2", asList("v2", "v3"));
        String result = parseAndExpand(uri, map);
        assertEquals(expected, result);
    }

    @Test
    void shouldPreserveTemplateExpansionForMissingParameterInQuery() {
        String uri = "http://www.example.com{?p1,p2,p3*}";
        String expected = "http://www.example.com?p2=v2&p2=v3{&p1,p3*}";
        Map<String, Object> map = new HashMap<>();
        map.put("p2", asList("v2", "v3"));
        String result = parseAndExpand(uri, map);
        assertEquals(expected, result);
    }


    @Test
    void shouldPreserveTemplateForMissingParameterInPath() {
        String uri = "http://www.example.com{/p1,p2,p3*}";
        String expected = "http://www.example.com{/p1*}/v2/v3{/p3*}";
        Map<String, Object> map = new HashMap<>();
        map.put("p2", asList("v2", "v3"));
        String result = parseAndExpand(uri, map);
        assertEquals(expected, result);
    }

    @Test
    void shouldNotSubstituteQueryFirstParameterIfNameIsDifferent() {
        String uri = "http://www.example.com{?p2}";
        String expected = "http://www.example.com{?p2}";
        String result = parseAndExpand(uri, SINGLE);
        assertEquals(expected, result);
    }

    @Test
    void shouldSubstitutePathFirstSegment() {
        String uri = "http://www.example.com{/p1}";
        String expected = "http://www.example.com/v1";
        String result = parseAndExpand(uri, SINGLE);
        assertEquals(expected, result);
    }

    @Test
    void shouldSubstitutePathFirstSegmentWithoutModifiers() {
        String uri = "http://www.example.com/{p1}/test";
        String expected = "http://www.example.com/v1/test";
        String result = parseAndExpand(uri, SINGLE);
        assertEquals(expected, result);
    }

    @Test
    void shouldConcatenatePathElementsWithCommaWithoutModifiers() {
        String uri = "http://www.example.com/{p1}/test";
        String expected = "http://www.example.com/v1,v2/test";
        String result = parseAndExpand(uri, singletonMap("p1", asList("v1", "v2")));
        assertEquals(expected, result);
    }

    @Test
    void shouldConcatenatePathElementsWithCommaWithStarModifier() {
        String uri = "http://www.example.com/{p1*}/test";
        String expected = "http://www.example.com/v1,v2/test";
        String result = parseAndExpand(uri, singletonMap("p1", asList("v1", "v2")));
        assertEquals(expected, result);
    }

    @Test
    void shouldPreservePathSeparatorWhenTwoPathSegmentsAreTemplates() {
        String uri = "http://www.example.com/rels/{p1}/{p2}";
        String expected = "http://www.example.com/rels/v1/v2";
        String result = parseAndExpand(uri,
                mapOf("p1", singletonList("v1"), "p2", singletonList("v2")));
        assertEquals(expected, result);
    }

    @Test
    void shouldPreserveDotSeparatorWhenTwoDomainSegmentsAreTemplates() {
        String uri = "http://www.{d1}.{d2}";
        String expected = "http://www.example.com";
        String result = parseAndExpand(uri,
                mapOf("d1", singletonList("example"), "d2", singletonList("com")));
        assertEquals(expected, result);
    }

    @Test
    void shouldNotSubstitutePathFirstSegmentIfNameIsDifferent() {
        String uri = "http://www.example.com{/p2}";
        String expected = "http://www.example.com{/p2}";
        String result = parseAndExpand(uri, SINGLE);
        assertEquals(expected, result);
    }

    @Test
    void shouldSubstituteHostname() {
        String uri = "{service:scheme}://{service:hostname}/some/{path}{?q*}";
        String expected = "https://www.example.com/some/api?q=1&q=2";
        String result = parseAndExpand(uri, "https", "www.example.com","api", asList(1, 2));
        assertEquals(expected, result);
    }

    @Test
    void shouldSubstituteHostnameFromMap() {
        String uri = "{service:scheme}://{service:hostname}/api";
        String expected = "https://www.example.com/api";
        Map<String, Object> map = new HashMap<>();
        map.put("service:scheme", "https");
        map.put("service:hostname", "www.example.com");
        String result = parseAndExpand(uri, map);
        assertEquals(expected, result);
    }

    @Test
    void shouldSubstituteDomainName() {
        String uri = "www{.dom*}";
        String expected = "www.example.com";
        String result = parseAndExpand(uri, singletonMap("dom", asList("example", "com")));
        assertEquals(expected, result);
    }

    @Test
    void shouldExpandFragment() {
        String uri = "{#list}";
        String expected = "#red,green,blue";
        String result = parseAndExpand(uri, LIST);
        assertEquals(expected, result);
    }

    @Test
    void shouldExpandByNameMultipleTimes() {
        String uri = "{.who,who}";
        String expected = ".fred.fred";
        String result = parseAndExpand(uri, singletonMap("who", "fred"));
        assertEquals(expected, result);
    }

    @Test
    void shouldTruncateValue() {
        String uri = "X{.var:3}";
        String expected = "X.val";
        String result = parseAndExpand(uri, singletonMap("var", "value"));
        assertEquals(expected, result);
    }

    @Test
    void shouldCorrectlyTruncateSpecialCharacter() {
        String uri = "{semi:2}";
        String expected = "%3B";
        String result = parseAndExpand(uri, singletonMap("semi", ";"));
        assertEquals(expected, result);
    }

    @Test
    void shouldExpandPreEncodedValueCorrectly() {
        String uri = "{+base}/api/path";
        String expected = "https://www.example.com/api/path";
        String result = parseAndExpand(uri, singletonMap("base", "https://www.example.com"));
        assertEquals(expected, result);
    }

    @Test
    void shouldExpandFragmentExploded() {
        String uri = "{#list*}";
        String expected = "#red,green,blue";
        String result = parseAndExpand(uri, LIST);
        assertEquals(expected, result);
    }

    @Test
    void shouldExpandPathLikeExpression() {
        String uri = "path{;list}";
        String expected = "path;list=red,green,blue";
        String result = parseAndExpand(uri, LIST);
        assertEquals(expected, result);
    }

    @Test
    void shouldExpandPathLikeExpressionExploded() {
        String uri = "path{;list*}";
        String expected = "path;list=red;list=green;list=blue";
        String result = parseAndExpand(uri, LIST);
        assertEquals(expected, result);
    }

    @Test
    void shouldPreserveTemplateInExpandPathLikeExpressionExploded() {
        String uri = "path{;p1,list,p3*}";
        String expected = "path{;p1*};list=red;list=green;list=blue{;p3*}";
        String result = parseAndExpand(uri, LIST);
        assertEquals(expected, result);
    }

    private static <K,V> Map<K,V> mapOf(K key1, V value1, K key2, V value2) {
        Map<K, V> map =new HashMap<>();
        map.put(key1, value1);
        map.put(key2, value2);
        return map;
    }

}
