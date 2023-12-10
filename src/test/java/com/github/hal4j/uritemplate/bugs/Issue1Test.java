package com.github.hal4j.uritemplate.bugs;

import com.github.hal4j.uritemplate.URITemplate;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * https://github.com/hal4j/uritemplate/issues/1
 * <p>
 * As defined in RFC 6570 section 3.2.1:
 * For a variable that is an associative array, expansion depends on
 * both the expression type and the presence of an explode modifier.  If
 * there is no explode modifier, expansion consists of appending a
 * comma-separated concatenation of each (name, value) pair that has a
 * defined value.  If there is an explode modifier, expansion consists
 * of appending each pair that has a defined value as either
 * "name=value" or, if the value is the empty string and the expression
 * type does not indicate form-style parameters (i.e., not a "?" or "&"
 * type), simply "name".  Both name and value strings are encoded in the
 * same way as simple string values.
 */
public class Issue1Test {

    @Test
    public void shouldSupportFormStyleQueryExpansionExplode() {
        Map<String, String> keys = new LinkedHashMap<>();
        keys.put("semi", ";");
        keys.put("dot", ".");
        keys.put("comma", ",");
        Map<String, Object> vars = new HashMap<>();
        vars.put("keys", keys);
        String s = new URITemplate("https://www.example.com{?keys*}")
                .expandPartial(vars)
                .toString();
        assertEquals("https://www.example.com?semi=%3B&dot=.&comma=%2C", s);
    }

    @Test
    public void shouldSupportQueryExpansionNoExplode() {
        Map<String, String> keys = new LinkedHashMap<>();
        keys.put("semi", ";");
        keys.put("dot", ".");
        keys.put("comma", ",");
        Map<String, Object> vars = new HashMap<>();
        vars.put("keys", keys);
        String s = new URITemplate("https://www.example.com{?keys}")
                .expandPartial(vars)
                .toString();
        assertEquals("https://www.example.com?keys=semi,%3B,dot,.,comma,%2C", s);
    }

    @Test
    public void shouldExpandAssociativeArrayWithPathExplode() {
        Map<String, String> keys = new LinkedHashMap<>();
        keys.put("semi", ";");
        keys.put("dot", ".");
        keys.put("comma", ",");
        Map<String, Object> vars = new HashMap<>();
        vars.put("keys", keys);
        String s = new URITemplate("https://www.example.com{/keys*}")
                .expandPartial(vars)
                .toString();
        assertEquals("https://www.example.com/semi=%3B/dot=./comma=%2C", s);
    }

    @Test
    public void shouldExpandAssociativeArrayWithPathNoExplode() {
        Map<String, String> keys = new LinkedHashMap<>();
        keys.put("semi", ";");
        keys.put("dot", ".");
        keys.put("comma", ",");
        Map<String, Object> vars = new HashMap<>();
        vars.put("keys", keys);
        String s = new URITemplate("https://www.example.com{/keys}")
                .expandPartial(vars)
                .toString();
        assertEquals("https://www.example.com/semi,%3B,dot,.,comma,%2C", s);
    }

    @Test
    public void shouldProduceResultAsExpectedInBugReport() {
        List<String> p2 = asList("One", "Two", "Three");
        Map<String, String> p3 = new LinkedHashMap<>();
        p3.put("property1", "zx");
        p3.put("property2", "qx");

        Map<String, Object> vars = new LinkedHashMap<>();
        vars.put("p1", "Hello");
        vars.put("qp1", "Hello");
        vars.put("p2", p2);
        vars.put("qp2", p2);
        vars.put("p3", p3);
        vars.put("qp3", p3);

        String s = new URITemplate("/path1SF/{p1}/{p2}/{p3}{?qp2*}{&qp1*}{&qp3*}")
                .expandPartial(vars)
                .toString();

        assertEquals("/path1SF/Hello/One,Two,Three/property1,zx,property2,qx?qp2=One&qp2=Two&qp2=Three&qp1=Hello&property1=zx&property2=qx", s);
    }

    @Test
    public void shouldExpandPathLikeNoExplodeAsInBugReport() {
        List<String> p2 = asList("One", "Two", "Three");
        Map<String, String> p3 = new LinkedHashMap<>();
        p3.put("zx", "80");
        p3.put("dragon", "32k");

        Map<String, Object> vars = new LinkedHashMap<>();
        vars.put("p1", "Hello");
        vars.put("p2", p2);
        vars.put("p3", p3);

        String s = new URITemplate("/path1/{;p1}/{;p2}/{;p3}")
                .expandPartial(vars)
                .toString();

        assertEquals("/path1/;p1=Hello/;p2=One,Two,Three/;p3=zx,80,dragon,32k", s);
    }


}
