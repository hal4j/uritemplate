package com.github.hal4j.uritemplate.test;

import com.github.hal4j.uritemplate.URITemplate;
import com.github.hal4j.uritemplate.URITemplateVariable;
import org.junit.jupiter.api.Test;

import static com.github.hal4j.uritemplate.URIBuilder.basedOn;
import static com.github.hal4j.uritemplate.URIFactory.hierarchical;
import static com.github.hal4j.uritemplate.URITemplateVariable.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class URIBuilderTest {

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
    void shouldBuildSameURITemplateAsOriginalWithEncodedQueryParamValue() {
        String s = basedOn("http://www.example.com?val1=%25")
                .asTemplate()
                .toString();
        assertEquals("http://www.example.com?val1=%25", s);
    }

    @Test
    void shouldAppendParamCorrectly() {
        String s = basedOn("http://www.example.com?val1=%25")
                .queryParam("val2", "%")
                .toString();
        assertEquals("http://www.example.com?val1=%25&val2=%25", s);
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
    void shouldReplaceHost() {
        String s = basedOn("https://example.com")
                .host("example2.com")
                .asTemplate()
                .toString();
        assertEquals("https://example2.com", s);
    }

    @Test
    void shouldJoinHostComponentsCorrectly() {
        String s = hierarchical()
                .scheme("https")
                .host().join(template("env"), "example.com")
                .asTemplate()
                .toString();
        assertEquals("https://{env}.example.com", s);
    }

    @Test
    void shouldAppendHostComponentsCorrectly() {
        String s = hierarchical()
                .scheme("https")
                .host().append(template("env"), ".example.com")
                .asTemplate()
                .toString();
        assertEquals("https://{env}.example.com", s);
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

    @Test
    void shouldAppendPathCorrectly() {
        String s = basedOn("http://www.example.com?val1=%25")
                .relative("api", "subpath")
                .toString();
        assertEquals("http://www.example.com/api/subpath?val1=%25", s);
    }

    @Test
    void shouldApplyPartSpecificJoinCorrectly() {
        String s = basedOn("http://www.example.com?val1=%25")
                .path().join("api", "subpath")
                .toString();
        assertEquals("http://www.example.com/api/subpath?val1=%25", s);
    }

    @Test
    void shouldApplyPartSpecificAppendCorrectly() {
        String s = basedOn("http://www.example.com?val1=%25")
                .path().append("/api", "subpath")
                .toString();
        assertEquals("http://www.example.com/apisubpath?val1=%25", s);
    }

    @Test
    void shouldApplyPartSpecificAppendCorrectlyInTemplate() {
        String s = basedOn("http://www.example.com?key=value")
                .path().append("/api", "subpath")
                .asTemplate()
                .toString();
        assertEquals("http://www.example.com/apisubpath?key=value", s);
    }

    @Test
    void shouldEscapeTemplateLikePathCorrectly() {
        String s = basedOn("http://www.example.com")
                .relative("api", "items", "{name}")
                .toString();
        assertEquals("http://www.example.com/api/items/%7Bname%7D", s);
    }

    @Test
    void shouldAppendPathTemplateCorrectly() {
        String s = basedOn("http://www.example.com")
                .relative("api", "items", template("name"))
                .toString();
        assertEquals("http://www.example.com/api/items/{name}", s);
    }

    @Test
    void shouldAppendPathTemplateWithMultipleSegmentsCorrectly() {
        String s = basedOn("http://www.example.com")
                .relative("api", "items", template("name"), template("value"))
                .toString();
        assertEquals("http://www.example.com/api/items/{name}/{value}", s);
    }

    @Test
    void shouldAppendPathVariableCorrectly() {
        String s = basedOn("http://www.example.com")
                .append(URITemplateVariable.pathVariable("name"))
                .toString();
        assertEquals("http://www.example.com{/name}", s);
    }

    @Test
    void shouldAppendVariableToPathCorrectly() {
        String s = basedOn("http://www.example.com/api/")
                .append(template("name"))
                .toString();
        assertEquals("http://www.example.com/api/{name}", s);
    }

    @Test
    void shouldAppendVariableToHostCorrectly() {
        String s = basedOn("http://www.example")
                .append(preEncoded("tld"))
                .toString();
        assertEquals("http://www.example{+tld}", s);
    }

    @Test
    void shouldAppendQueryParamTemplateCorrectly() {
        String s = basedOn("http://www.example.com/api")
                .append(queryStart("name"))
                .toString();
        assertEquals("http://www.example.com/api{?name}", s);
    }

    @Test
    void shouldExpandPathTemplateCorrectly() {
        String s = basedOn("http://www.example.com")
                .relative("api", "items", template("name"))
                .asTemplate()
                .expand("name", "1")
                .toString();
        assertEquals("http://www.example.com/api/items/1", s);
    }

    @Test
    void shouldAppendObjectsToPathCorrectly() {
        String s = basedOn("http://www.example.com?val1=%25")
                .relative("api", 1)
                .toString();
        assertEquals("http://www.example.com/api/1?val1=%25", s);
    }

    @Test
    void shouldParseEmptyRelativePathCorrectly() {
        String s = basedOn("#fragment").toString();
        assertEquals("#fragment", s);
    }

    @Test
    void shouldParseEmptyAuthorityCorrectlyWithTripleSlashPath() {
        String s = basedOn("file:///foo/bar").toString();
        assertEquals("file:/foo/bar", s);
    }

    @Test
    void shouldParseEmptyAuthorityCorrectlyWithDoubleSlashPath() {
        String s = basedOn("file://foo/bar").toString();
        assertEquals("file://foo/bar", s);
    }

    @Test
    void shouldParseEmptyAuthorityCorrectly() {
        String s = basedOn("file:/foo/bar").toString();
        assertEquals("file:/foo/bar", s);
    }

    @Test
    void shouldBuildFullURICorrectlyAsTemplate() {
        String uri = "http://user:password@www.example.com:8443/path?query=value#fragment";
        String result = basedOn(uri).asTemplate().toString();
        assertEquals(uri, result);
    }

    @Test
    void shouldAppendUriTemplateCorrectly() {
        String uri = "http://www.example.com";
        String result = basedOn(uri)
                .resolve("/users{/id}{?expand}")
                .expand("id", 1)
                .expand("expand", "all")
                .toString();
        assertEquals("http://www.example.com/users/1?expand=all", result);
    }

    @Test
    void shouldBuildSchemeAndAuthorityTemplate() {
        URITemplate template = hierarchical()
                .scheme(template("service.scheme"))
                .host(template("service.hostname"))
                .port(template("service.port"))
                .resolve("/api/v1/search{?query}");
        assertEquals("{service.scheme}://{service.hostname}:{service.port}/api/v1/search{?query}", template.toString());
        String uri = template
                .expand("http", "www.example.com", 80, "test")
                .toString();
        assertEquals("http://www.example.com:80/api/v1/search?query=test", uri);

    }

    @Test
    void shouldAppendBasicAuthCredentials() {
        String s = basedOn("https://example.com")
                .userInfo("user", "password")
                .toString();
        assertEquals("https://user:password@example.com", s);
    }

    @Test
    void shouldAppendUserInfo() {
        String s = basedOn("https://example.com")
                .userInfo("abc")
                .toString();
        assertEquals("https://abc@example.com", s);
    }

    @Test
    void shouldSetPort() {
        String s = basedOn("https://example.com")
                .port(8080)
                .toString();
        assertEquals("https://example.com:8080", s);
    }

}

