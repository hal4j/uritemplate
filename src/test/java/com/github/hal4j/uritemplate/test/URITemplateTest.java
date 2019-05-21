package com.github.hal4j.uritemplate.test;

import com.github.hal4j.uritemplate.URITemplate;
import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

class URITemplateTest {

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

}
