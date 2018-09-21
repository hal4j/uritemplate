package com.github.hal4j.uritemplate;

import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

public class OpaqueURIBuilderTest {

    @Test
    public void shouldCreateSimpleOpaqueURIWithoutFragment() {
        URI uri = URIFactory.opaque("rel").ssp("test").toURI();
        assertTrue(uri.isOpaque());
        assertEquals("rel", uri.getScheme());
        assertEquals("test", uri.getSchemeSpecificPart());
        assertNull(uri.getFragment());
    }

    @Test
    public void shouldCreateSimpleOpaqueURIWithFragment() {
        URI uri = URIFactory.opaque("rel").ssp("test").fragment("!/frag").toURI();
        assertTrue(uri.isOpaque());
        assertEquals("rel", uri.getScheme());
        assertEquals("test", uri.getSchemeSpecificPart());
        assertEquals("!/frag", uri.getFragment());
        assertEquals("rel:test#!/frag", uri.toString());
    }

    @Test
    public void shouldConcatenateSSP() {
        URI uri = URIFactory.opaque("rel").ssp(":1", ":2", ":3").toURI();
        assertTrue(uri.isOpaque());
        assertEquals("rel", uri.getScheme());
        assertEquals(":1:2:3", uri.getSchemeSpecificPart());
        assertNull(uri.getFragment());
        assertEquals("rel::1:2:3", uri.toString());
    }

    @Test
    public void shouldConcatenateSSPWithDelimiter() {
        URI uri = URIFactory.opaque("rel").sspJoined(":","1", "2", "3").toURI();
        assertTrue(uri.isOpaque());
        assertEquals("rel", uri.getScheme());
        assertEquals("1:2:3", uri.getSchemeSpecificPart());
        assertNull(uri.getFragment());
        assertEquals("rel:1:2:3", uri.toString());
    }

    @Test
    public void shouldConcatenateFragmentWithDelimiter() {
        URI uri = URIFactory.opaque("rel")
                .ssp("test")
                .fragmentJoined("/","1", "2", "3")
                .toURI();
        assertTrue(uri.isOpaque());
        assertEquals("rel", uri.getScheme());
        assertEquals("test", uri.getSchemeSpecificPart());
        assertEquals("1/2/3", uri.getFragment());
        assertEquals("rel:test#1/2/3", uri.toString());
    }

}
