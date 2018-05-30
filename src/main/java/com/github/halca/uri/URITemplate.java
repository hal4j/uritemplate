package com.github.halca.uri;

import java.net.URI;
import java.util.Collections;
import java.util.Map;

public class URITemplate {

    private final String value;

    URITemplate(String string) {
        this.value = string;
    }

    public URITemplate expand(String name, Object value) {
        return expand(Collections.singletonMap(name, value));
    }

    public URITemplate expand(Map<String, ?> substitutions) {
        String expanded = URITemplateParser.parseAndExpand(value, substitutions);
        return new URITemplate(expanded);
    }

    public URITemplate expand(Object... substitutions) {
        String expanded = URITemplateParser.parseAndExpand(value, substitutions);
        return new URITemplate(expanded);
    }

    public boolean isExpanded() {
        return value.indexOf('{') < 0;
    }

    public URI toURI() {
        if (!isExpanded()) {
            throw new IllegalStateException("Template not expanded: " + value);
        }
        return URI.create(value);
    }

    public String toString() {
        return this.value;
    }
}
