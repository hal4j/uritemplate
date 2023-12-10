package com.github.hal4j.uritemplate.test;

import com.github.hal4j.uritemplate.ParamHolder;
import com.github.hal4j.uritemplate.URITemplate;
import org.junit.jupiter.api.Test;

import java.util.Collections;
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
                }, true).toString();
        assertEquals("http://example.com/1?B=2{#C}", s);
    }

    @Test
    void shouldExpandPreEncodedValueCorrectly() {
        String s = new URITemplate("http://www.example{+tld}")
                .expandPartial("tld", "{.domain}")
                .toString();
        assertEquals("http://www.example%7B.domain%7D", s);
    }

    @Test
    void shouldParsePathTemplateWithMultipleSegmentsCorrectly() {
        String s = new URITemplate("http://www.example.com/{name}/{value}").toString();
        assertEquals("http://www.example.com/{name}/{value}", s);
    }

    @Test
    void shouldExpandUriTemplatePathCorrectly() {
        String s = new URITemplate("http://www.example.com{/path*}")
                .expandPartial("path", asList("1","2"))
                .toString();
        assertEquals("http://www.example.com/1/2", s);
    }

    @Test
    void shouldExpandUriTemplateQueryCorrectly() {
        String s = new URITemplate("http://www.example.com/api{?param*}")
                .expandPartial("param", asList("1","2"))
                .toString();
        assertEquals("http://www.example.com/api?param=1&param=2", s);
    }

    @Test
    void shouldExpandOpaqueURICorrectly() {
        String s = new URITemplate("rel{:param*}")
                .expandPartial("param", asList("1","2"))
                .toString();
        assertEquals("rel:1:2", s);
    }

    @Test
    void shouldExpandPrefixWithURI() {
        String s = new URITemplate("{+prefix}/api/v1{?query}").expandPartial("https://www.example.com:8443").toString();
        assertEquals("https://www.example.com:8443/api/v1{?query}", s);
    }

    @Test
    void shouldDiscardParametersCorrectly() {
        URITemplate template = new URITemplate("https://www.example.com/api{/uuid}{?utm_source}{&utm_campaign}");
        String result = template.discard("uuid").toString();
        assertEquals("https://www.example.com/api{?utm_source}{&utm_campaign}", result);
    }

    @Test
    void shouldExpandOnlyRequestedParameters() {
        URITemplate template = new URITemplate("https://www.example.com/api{/uuid}{?utm_source}{&utm_campaign}");
        String result = template.expand(Collections.singletonMap("uuid", "1")).toString();
        assertEquals("https://www.example.com/api/1", result);
    }

}
