package com.github.hal4j.uritemplate;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class URIVariationsTest {

    @Test
    public void shouldHandleCustomRelCorrectly() {
        assertOriginalAndParsedSame("exa-mple:link/action");
    }

    @Test
    public void shouldHandleSelfRelCorrectly() {
        assertOriginalAndParsedSame("self");
    }

    @Test
    public void shouldAcceptValidServerBasedAuthority() {
        assertOriginalAndParsedSame("http://www.ietf.org/rfc/rfc2396.txt");
    }

    @Test
    public void shouldAcceptValidURN() {
        assertOriginalAndParsedSame("urn:oasis:names:specification:docbook:dtd:xml:4.1.2");
    }

    @Test
    public void shouldAcceptValidPhone() {
        assertOriginalAndParsedSame("tel:+1-816-555-1212");
    }

    @Test
    public void shouldAcceptValidMailto() {
        assertOriginalAndParsedSame("mailto:john.doe+spam@example.com");
    }

    @Test
    public void shouldAcceptValidLDAP() {
        assertOriginalAndParsedSame("ldap://[2001:db8::7]/c=GB?objectClass?one");
    }


    private void assertOriginalAndParsedSame(String uri) {
        assertEquals(uri, new URIBuilder(uri).toString());
    }

}
