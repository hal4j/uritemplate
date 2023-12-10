package com.github.hal4j.uritemplate.test.builder;

import com.github.hal4j.uritemplate.URIBuilder;
import org.junit.jupiter.api.Test;

import static com.github.hal4j.uritemplate.URIBuilder.basedOn;
import static com.github.hal4j.uritemplate.URITemplateVariable.queryParam;
import static com.github.hal4j.uritemplate.URITemplateVariable.queryStart;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class QueryComponentsTest {

    @Test
    void shouldAppendSingleParamCorrectly() {
        String s = basedOn("http://www.example.com")
                .queryParam("name", "Hello world")
                .toString();
        assertEquals("http://www.example.com?name=Hello%20world", s);
    }

    @Test
    void shouldEscapeParamValueCorrectly() {
        String s = basedOn("http://www.example.com")
                .queryParam("value", "%")
                .toString();
        assertEquals("http://www.example.com?value=%25", s);
    }

    @Test
    void shouldBuildSameURIAsOriginalWithEncodedQueryParamValue() {
        String s = basedOn("http://www.example.com?val1=%25").toString();
        assertEquals("http://www.example.com?val1=%25", s);
    }


    @Test
    public void shouldAppendMultipleQueryParamsToEmptyStringCorrectly() {
        String actual = URIBuilder.basedOn("")
                .queryParam("a", "b")
                .queryParam("c", "d").toString();
        assertEquals("?a=b&c=d", actual);
    }

    @Test
    public void shouldAppendMultipleQueryParamsToQueryOnlyCorrectly() {
        String actual = URIBuilder.basedOn("?e=f")
                .queryParam("a", "b")
                .queryParam("c", "d").toString();
        assertEquals("?e=f&a=b&c=d", actual);
    }

    @Test
    public void shouldAppendMultipleQueryParamsToExistingURICorrectly() {
        String actual = URIBuilder.basedOn("https://www.example.com")
                .queryParam("a", "b")
                .queryParam("c", "d")
                .toString();
        assertEquals("https://www.example.com?a=b&c=d", actual);
    }

    @Test
    public void shouldAppendMultipleQueryParamsToExistingURIWithQueryCorrectly() {
        String actual = URIBuilder.basedOn("https://www.example.com?e=f")
                .queryParam("a", "b")
                .queryParam("c", "d").toString();
        assertEquals("https://www.example.com?e=f&a=b&c=d", actual);
    }

    @Test
    void shouldAppendMultiValueParamCorrectly() {
        String s = basedOn("http://www.example.com?val1=%25")
                .queryParam("val2", "%", "$", "#")
                .queryParam("val3", "hello")
                .toString();
        assertEquals("http://www.example.com?val1=%25&val2=%25&val2=$&val2=%23&val3=hello", s);
    }

    @Test
    public void shouldAppendMultipleQueryParamsToFragmentStringCorrectly() {
        String actual = URIBuilder.basedOn("#test")
                .queryParam("a", "b")
                .queryParam("c", "d")
                .toString();
        assertEquals("?a=b&c=d#test", actual);
    }

    @Test
    void shouldAppendQueryComponentsCorrectly() {
        String s = basedOn("https://www.example.com")
                .query().append("p1=v1&p2=v2", queryParam("p3"))
                .asTemplate()
                .toString();
        assertEquals("https://www.example.com?p1=v1&p2=v2{&p3}", s);
    }

    @Test
    void shouldAppendQueryStartCorrectly() {
        String s = basedOn("https://www.example.com")
                .query().append(queryStart("p3"), queryParam("p4"))
                .asTemplate()
                .toString();
        assertEquals("https://www.example.com{?p3}{&p4}", s);
    }

}
