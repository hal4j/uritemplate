package com.github.hal4j.uritemplate.test.rfc6570;

import java.util.*;

import static java.util.Collections.emptyMap;
import static java.util.Collections.unmodifiableMap;

public final class RFC6570Definitions {

    public final static Map<String, Object> VALUES;

    static {
        Map<String, Object> values = new HashMap<>();
        values.put("count", Arrays.asList("one", "two", "three"));
        values.put("dom", Arrays.asList("example", "com"));
        values.put("dub", "me/too");
        values.put("hello", "Hello World!");
        values.put("half", "50%");
        values.put("var", "value");
        values.put("who", "fred");
        values.put("base", "http://example.com/home/");
        values.put("path", "/foo/bar");
        values.put("list", Arrays.asList("red", "green", "blue"));
        values.put("keys", map("semi",";","dot",".","comma",","));
        values.put("v", "6");
        values.put("x", "1024");
        values.put("y", "768");
        values.put("empty", "");
        values.put("empty_keys", emptyMap());
        values.put("undef", null);
        VALUES = Collections.unmodifiableMap(values);
    }

    private static Map<String, String> map(
            String k1, String v1,
            String k2, String v2,
            String k3, String v3) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put(k1, v1);
        map.put(k2, v2);
        map.put(k3, v3);
        return unmodifiableMap(map);
    }

    private RFC6570Definitions() {}

}
