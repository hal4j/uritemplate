package com.github.hal4j.uritemplate;

/**
 * Functional namespace for creation of URIs and URI templates
 */
public final class URIFactory {

    private URIFactory() {
    }

    public static URITemplate templateUri(String baseUriString) {
        return new URITemplate(baseUriString);
    }

    public static URIBuilder uri(String baseUriString) {
        return new URIBuilder(baseUriString);
    }

    public static URIBuilder opaque(String scheme) {
        return new URIBuilder(scheme, true);
    }

    public static URIBuilder opaque() {
        return new URIBuilder(true);
    }

    public static URIBuilder hierarchical() {
        return new URIBuilder(false);
    }

}
