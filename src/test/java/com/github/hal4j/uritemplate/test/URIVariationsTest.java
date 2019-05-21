package com.github.hal4j.uritemplate.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.github.hal4j.uritemplate.URIBuilder.basedOn;

class URIVariationsTest {

    @Test
    void shouldHandleCustomRelCorrectly() {
        assertOriginalAndParsedSame("exa-mple:link/action");
    }

    @Test
    void shouldHandleSelfRelCorrectly() {
        assertOriginalAndParsedSame("self");
    }

    @Test
    void shouldAcceptValidServerBasedAuthority() {
        assertOriginalAndParsedSame("http://www.ietf.org/rfc/rfc2396.txt");
    }

    @Test
    void shouldAcceptValidURN() {
        assertOriginalAndParsedSame("urn:oasis:names:specification:docbook:dtd:xml:4.1.2");
    }

    @Test
    void shouldAcceptValidPhone() {
        assertOriginalAndParsedSame("tel:+1-816-555-1212");
    }

    @Test
    void shouldAcceptValidMailto() {
        assertOriginalAndParsedSame("mailto:john.doe+spam@example.com");
    }

    @Test
    void shouldAcceptValidLDAP() {
        assertOriginalAndParsedSame("ldap://[2001:db8::7]/c=GB?objectClass?one");
    }

    private void assertOriginalAndParsedSame(String uri) {
        Assertions.assertEquals(uri, basedOn(uri).toString());
    }

}
