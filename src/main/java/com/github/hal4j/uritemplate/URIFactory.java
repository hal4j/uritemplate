package com.github.hal4j.uritemplate;

public final class URIFactory {

    private URIFactory() {
    }

    public static URITemplate templateUri(String baseUriString) {
        return new URITemplate(baseUriString);
    }

    public static URIBuilder uri(String baseUriString) {
        return new URIBuilder(baseUriString);
    }

    public static OpaqueURIBuilder opaque(String scheme) {
        return new OpaqueURIBuilder(scheme);
    }

}
