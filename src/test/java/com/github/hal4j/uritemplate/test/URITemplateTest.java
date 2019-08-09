package com.github.hal4j.uritemplate.test;

import com.github.hal4j.uritemplate.ParamHolder;
import com.github.hal4j.uritemplate.URITemplate;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

class URITemplateTest {

    @Test
    void shouldExpandWithCustomParamHolder() {
        final Map<String, String> values = new HashMap<>();
        values.put("a", "1");
        values.put("b", "2");
        String s = new URITemplate("http://example.com{/A}{?B}{#C}")
                .expand(new ParamHolder() {
                    @Override
                    public boolean containsKey(String name) {
                        return values.containsKey(name.toLowerCase());
                    }

                    @Override
                    public Object get(String name) {
                        return values.get(name.toLowerCase());
                    }
                }).toString();
        assertEquals("http://example.com/1?B=2{#C}", s);
    }

    @Test
    void shouldExpandPreEncodedValueCorrectly() {
        String s = new URITemplate("http://www.example{+tld}")
                .expand("tld", "{.domain}")
                .toString();
        assertEquals("http://www.example{.domain}", s);
    }

    @Test
    void shouldParsePathTemplateWithMultipleSegmentsCorrectly() {
        String s = new URITemplate("http://www.example.com/{name}/{value}").toString();
        assertEquals("http://www.example.com/{name}/{value}", s);
    }

    @Test
    void shouldExpandUriTemplatePathCorrectly() {
        String s = new URITemplate("http://www.example.com{/path*}")
                .expand("path", asList("1","2"))
                .toString();
        assertEquals("http://www.example.com/1/2", s);
    }

    @Test
    void shouldExpandUriTemplateQueryCorrectly() {
        String s = new URITemplate("http://www.example.com/api{?param*}")
                .expand("param", asList("1","2"))
                .toString();
        assertEquals("http://www.example.com/api?param=1&param=2", s);
    }

    @Test
    void shouldExpandOpaqueURICorrectly() {
        String s = new URITemplate("rel{:param*}")
                .expand("param", asList("1","2"))
                .toString();
        assertEquals("rel:1:2", s);
    }

    @Test
    void shouldExpandPrefixWithURI() {
        String s = new URITemplate("{+prefix}/api/v1{?query}").expand("https://www.example.com:8443").toString();
        assertEquals("https://www.example.com:8443/api/v1{?query}", s);
    }

}
