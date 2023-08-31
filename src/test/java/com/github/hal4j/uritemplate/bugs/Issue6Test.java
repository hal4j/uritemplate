package com.github.hal4j.uritemplate.bugs;

import com.github.hal4j.uritemplate.URITemplate;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Collection of unit tests verifying the fix for bug
 * <a href="https://github.com/hal4j/uritemplate/issues/6">6 - The asterisk as a reserved char should not be pсе-encoded</a>.
 *
 * According to RFC 6570, Section 3.2.1:
 * <blockquote>
 *   Variable expansion of a defined, non-empty value results in a
 *   substring of allowed URI characters.  As described in Section 1.6,
 *   the expansion process is defined in terms of Unicode code points in
 *   order to ensure that non-ASCII characters are consistently pct-
 *   encoded in the resulting URI reference.
 * </blockquote>
 * And further:
 * <blockquote>
 *    The allowed set for a given expansion depends on the expression type:
 *    reserved ("+") and fragment ("#") expansions allow the set of
 *    characters in the union of ( unreserved / reserved / pct-encoded ) to
 *    be passed through without pct-encoding, whereas all other expression
 *    types allow only unreserved characters to be passed through without
 *    pct-encoding.
 * </blockquote>
 *
 * Also specification: https://www.rfc-editor.org/rfc/rfc3986#section-2.2
 *
 */
public class Issue6Test {

    @Test
    public void asteriskShouldNotBeEncodedInReservedExpansion() {
        Map<String, String> values = Collections.singletonMap("var", "test*");
        String s = new URITemplate("{+var}")
                .expand(values)
                .toString();
        assertEquals("test*", s);
    }

    @Test
    public void asteriskShouldNotBeEncodedInFragmentExpansion() {
        Map<String, String> values = Collections.singletonMap("var", "test*");
        String s = new URITemplate("www.example.com{#var}")
                .expand(values)
                .toString();
        assertEquals("www.example.com#test*", s);
    }

    @Test
    public void asteriskShouldBeEncodedInSimpleStringExpansion() {
        Map<String, Object> values = Collections.singletonMap("var", Arrays.asList("test","*"));
        String s = new URITemplate("www.example.com/{var}")
                .expand(values)
                .toString();
        assertEquals("www.example.com/test,%2A", s);
    }

    @Test
    public void asteriskShouldBeEncodedInPathExpansion() {
        Map<String, Object> values = Collections.singletonMap("var", Arrays.asList("test","*"));
        String s = new URITemplate("www.example.com{/var}")
                .expand(values)
                .toString();
        assertEquals("www.example.com/test,%2A", s);
    }

    @Test
    public void plusShouldBeEncodedInSimpleStringExpansion() {
        Map<String, Object> values = Collections.singletonMap("var", Arrays.asList("test","+"));
        String s = new URITemplate("www.example.com/{var}")
                .expand(values)
                .toString();
        assertEquals("www.example.com/test,%2B", s);
    }

    @Test
    public void plusShouldBeEncodedInPathExpansion() {
        Map<String, Object> values = Collections.singletonMap("var", Arrays.asList("test","+"));
        String s = new URITemplate("www.example.com{/var}")
                .expand(values)
                .toString();
        assertEquals("www.example.com/test,%2B", s);
    }
}
