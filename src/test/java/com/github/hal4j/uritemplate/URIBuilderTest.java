package com.github.hal4j.uritemplate;

import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.jupiter.api.Test;

import static com.github.hal4j.uritemplate.URITemplateVariable.template;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class URIBuilderTest {

    @Test
    public void shouldAppendSingleParamCorrectly() {
        String s = new URIBuilder("http://www.example.com")
                    .queryParam("name", "Hello world")
                    .toString();
        assertEquals("http://www.example.com?name=Hello%20world", s);
    }

    @Test
    public void shouldEscapeParamValueCorrectly() {
        String s = new URIBuilder("http://www.example.com")
                .queryParam("value", "%")
                .toString();
        assertEquals("http://www.example.com?value=%25", s);
    }

    @Test
    public void shouldBuildSameURIAsOriginalWithEncodedQueryParamValue() {
        String s = new URIBuilder("http://www.example.com?val1=%25").toString();
        assertEquals("http://www.example.com?val1=%25", s);
    }

    @Test
    public void shouldBuildSameURITemplateAsOriginalWithEncodedQueryParamValue() {
        String s = new URIBuilder("http://www.example.com?val1=%25")
                .asTemplate()
                .toString();
        assertEquals("http://www.example.com?val1=%25", s);
    }

    @Test
    public void shouldAppendParamCorrectly() {
        String s = new URIBuilder("http://www.example.com?val1=%25")
                .queryParam("val2", "%")
                .toString();
        assertEquals("http://www.example.com?val1=%25&val2=%25", s);
    }

    @Test
    public void shouldAppendMultiValueParamCorrectly() {
        String s = new URIBuilder("http://www.example.com?val1=%25")
                .queryParam("val2", "%", "$", "#")
                .queryParam("val3", "hello")
                .toString();
        assertEquals("http://www.example.com?val1=%25&val2=%25&val2=$&val2=%23&val3=hello", s);
    }

    @Test
    public void shouldAppendPathCorrectly() {
        String s = new URIBuilder("http://www.example.com?val1=%25")
                .relative("api", "subpath")
                .toString();
        assertEquals("http://www.example.com/api/subpath?val1=%25", s);
    }

    @Test
    public void shouldApplyPartSpecificAppendCorrectly() {
        String s = new URIBuilder("http://www.example.com?val1=%25")
                .path().append("api", "subpath")
                .toString();
        assertEquals("http://www.example.com/api/subpath?val1=%25", s);
    }

    @Test
    public void shouldApplyPartSpecificJoinCorrectly() {
        String s = new URIBuilder("http://www.example.com?val1=%25")
                .path().join("/api", "subpath")
                .toString();
        assertEquals("http://www.example.com/apisubpath?val1=%25", s);
    }

    @Test
    public void shouldApplyPartSpecificJoinCorrectlyInTemplate() {
        String s = new URIBuilder("http://www.example.com?key=value")
                .path().join("/api", "subpath")
                .asTemplate()
                .toString();
        assertEquals("http://www.example.com/apisubpath?key=value", s);
    }

    @Test
    public void shouldEscapeTemplateLikePathCorrectly() {
        String s = new URIBuilder("http://www.example.com")
                .relative("api", "items", "{name}")
                .toString();
        assertEquals("http://www.example.com/api/items/%7Bname%7D", s);
    }

    @Test
    public void shouldAppendPathTemplateCorrectly() {
        String s = new URIBuilder("http://www.example.com")
                .relative("api", "items", template("name"))
                .toString();
        assertEquals("http://www.example.com/api/items/{name}", s);
    }

    @Test
    public void shouldAppendPathTemplateWithMultipleSegmentsCorrectly() {
        String s = new URIBuilder("http://www.example.com")
                .relative("api", "items", template("name"), template("value"))
                .toString();
        assertEquals("http://www.example.com/api/items/{name}/{value}", s);
    }

    @Test
    public void shouldAppendPathVariableCorrectly() {
        String s = new URIBuilder("http://www.example.com")
                .append(URITemplateVariable.pathVariable("name"))
                .toString();
        assertEquals("http://www.example.com{/name}", s);
    }

    @Test
    public void shouldAppendVariableToPathCorrectly() {
        String s = new URIBuilder("http://www.example.com/api/")
                .append(template("name"))
                .toString();
        assertEquals("http://www.example.com/api/{name}", s);
    }

    @Test
    public void shouldAppendVariableToHostCorrectly() {
        String s = new URIBuilder("http://www.example")
                .append(URITemplateVariable.preEncoded("tld"))
                .toString();
        assertEquals("http://www.example{+tld}", s);
    }

    @Test
    public void shouldAppendQueryParamTemplateCorrectly() {
        String s = new URIBuilder("http://www.example.com/api")
                .append(URITemplateVariable.queryStart("name"))
                .toString();
        assertEquals("http://www.example.com/api{?name}", s);
    }

    @Test
    public void shouldExpandPathTemplateCorrectly() {
        String s = new URIBuilder("http://www.example.com")
                .relative("api", "items", template("name"))
                .asTemplate()
                .expand("name", "1")
                .toString();
        assertEquals("http://www.example.com/api/items/1", s);
    }

    @Test
    public void shouldAppendObjectsToPathCorrectly() {
        String s = new URIBuilder("http://www.example.com?val1=%25")
                .relative("api", 1)
                .toString();
        assertEquals("http://www.example.com/api/1?val1=%25", s);
    }

    @Test
    public void shouldParseEmptyRelativePathCorrectly() {
        String s = new URIBuilder("#fragment").toString();
        assertEquals("#fragment", s);
    }

    @Test
    public void shouldParseEmptyAuthorityCorrectlyWithTripleSlashPath() {
        String s = new URIBuilder("file:///foo/bar").toString();
        assertEquals("file:/foo/bar", s);
    }

    @Test
    public void shouldParseEmptyAuthorityCorrectlyWithDoubleSlashPath() {
        String s = new URIBuilder("file://foo/bar").toString();
        assertEquals("file://foo/bar", s);
    }

    @Test
    public void shouldParseEmptyAuthorityCorrectly() {
        String s = new URIBuilder("file:/foo/bar").toString();
        assertEquals("file:/foo/bar", s);
    }

    @Test
    public void shouldBuildFullURICorrectlyAsTemplate() {
        String uri = "http://user:password@www.example.com:8443/path?query=value#fragment";
        String result = new URIBuilder(uri).asTemplate().toString();
        assertEquals(uri, result);
    }

    @Test
    public void shouldAppendUriTemplateCorrectly() {
        String uri = "http://www.example.com";
        String result = new URIBuilder(uri)
                .resolve("/users{/id}{?expand}")
                .expand("id", 1)
                .expand("expand", "all")
                .toString();
        assertEquals("http://www.example.com/users/1?expand=all", result);
    }

}

