package com.github.halca.uri;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class URITemplateMatchingTest {

    private void assertMatches(String template, String uri) {
        assertTrue(new URITemplate(template).matches(uri));
    }

    private void assertNotMatches(String template, String uri) {
        assertFalse(new URITemplate(template).matches(uri));
    }

    @Test
    public void shouldMatchExactPath() {
        assertMatches("/a/b-c/d", "http://www.example.com:8080/a/b-c/d");
    }

    @Test
    public void shouldMatchPathTemplateVars() {
        assertMatches("/a{/any}{/id}", "http://www.example.com:8080/a/b-c/d");
    }

    @Test
    public void shouldMatchTemplateBetweenPathSegments() {
        assertMatches("/a{/any}/d", "http://www.example.com:8080/a/b-c/d");
    }

    @Test
    public void shouldMatchPathTemplateWhenQueryIsPresent() {
        assertMatches("/a/b-c/d", "http://www.example.com:8080/a/b-c/d?var=1&val=2");
    }

    @Test
    public void shouldMatchPathTemplateWhenFragmentIsPresent() {
        assertMatches("/a/b-c/d", "http://www.example.com:8080/a/b-c/d#!ui/path");
    }

    @Test
    public void shouldNotMatchPathTemplateWhenFragmentContainsMatchingPath() {
        assertNotMatches("/a/b-c/d", "http://www.example.com:8080#!/a/b-c/d");
    }

    @Test
    public void shouldNotMatchPathTemplateWhenQueryContainsMatchingPath() {
        assertNotMatches("/a/b-c/d", "http://www.example.com:8080?path=/a/b-c/d");
    }

    @Test
    public void shouldMatchQueryParameterTemplate() {
        assertMatches("/path/sub{?query,id}", "http://www.example.com:8080/path/sub?query=hello&id=1");
    }

    @Test
    public void shouldMatchQueryParameterTemplateWhenURIContainsMultipleValues() {
        assertMatches("/path/sub{?id}", "http://www.example.com:8080/path/sub?id=1&id=2");
    }

    @Test
    public void shouldMatchQueryParameterTemplateWithValue() {
        assertMatches("/path/sub?id=1{&query}", "http://www.example.com:8080/path/sub?id=1&query=hello");
    }

    @Test
    public void shouldNotMatchQueryParameterTemplateWhenParameterMissing() {
        assertNotMatches("/path/sub{?id}", "http://www.example.com:8080/path/sub?query=hello");
    }

    @Test
    public void shouldMatchQueryParameterTemplateWithValueWithAlternativeOrder() {
        assertMatches("/path/sub?id=1{&query}", "http://www.example.com:8080/path/sub?query=hello&id=1");
    }

    @Test
    public void shouldMatchQueryParameterTemplateWhenQueryContainsMoreParameters() {
        assertMatches("/path/sub{?id}", "http://www.example.com:8080/path/sub?query=hello&id=1");
    }

}
