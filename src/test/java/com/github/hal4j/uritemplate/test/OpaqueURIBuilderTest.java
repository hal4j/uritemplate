package com.github.hal4j.uritemplate.test;

import com.github.hal4j.uritemplate.URIFactory;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

class OpaqueURIBuilderTest {

    @Test
    void shouldCreateSimpleOpaqueURIWithoutFragment() {
        URI uri = URIFactory.opaque("rel").ssp("test").toURI();
        assertTrue(uri.isOpaque());
        assertEquals("rel", uri.getScheme());
        assertEquals("test", uri.getSchemeSpecificPart());
        assertNull(uri.getFragment());
    }

    @Test
    void shouldCreateSimpleOpaqueURIWithFragment() {
        URI uri = URIFactory.opaque("rel")
                .ssp("test")
                .fragment("!/frag")
                .toURI();
        assertTrue(uri.isOpaque());
        assertEquals("rel", uri.getScheme());
        assertEquals("test", uri.getSchemeSpecificPart());
        assertEquals("!/frag", uri.getFragment());
        assertEquals("rel:test#!/frag", uri.toString());
    }

    @Test
    void shouldConcatenateSSP() {
        URI uri = URIFactory.opaque("rel")
                .ssp().append(":1", ":2", ":3")
                .toURI();
        assertTrue(uri.isOpaque());
        assertEquals("rel", uri.getScheme());
        assertEquals(":1:2:3", uri.getSchemeSpecificPart());
        assertNull(uri.getFragment());
        assertEquals("rel::1:2:3", uri.toString());
    }

    @Test
    void shouldConcatenateSSPWithDelimiter() {
        URI uri = URIFactory.opaque("rel")
                .ssp().delimiter(':').join("1", "2", "3")
                .toURI();
        assertTrue(uri.isOpaque());
        assertEquals("rel", uri.getScheme());
        assertEquals("1:2:3", uri.getSchemeSpecificPart());
        assertNull(uri.getFragment());
        assertEquals("rel:1:2:3", uri.toString());
    }

    @Test
    void shouldConcatenateSSPWithPrefix() {
        URI uri = URIFactory.opaque("rel")
                .ssp().append(":")
                .ssp().delimiter('-').join("1", "2", "3")
                .toURI();
        assertTrue(uri.isOpaque());
        assertEquals("rel", uri.getScheme());
        assertEquals(":1-2-3", uri.getSchemeSpecificPart());
        assertNull(uri.getFragment());
        assertEquals("rel::1-2-3", uri.toString());
    }

    @Test
    void shouldConcatenateFragmentWithDelimiter() {
        URI uri = URIFactory.opaque("rel")
                .ssp("test")
                .fragment().delimiter('/').join("1", "2", "3")
                .toURI();
        assertTrue(uri.isOpaque());
        assertEquals("rel", uri.getScheme());
        assertEquals("test", uri.getSchemeSpecificPart());
        assertEquals("1/2/3", uri.getFragment());
        assertEquals("rel:test#1/2/3", uri.toString());
    }

}
