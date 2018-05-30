package com.github.halca.uri;

import static com.github.halca.uri.URITemplateFormat.*;

public final class URIFactory {

    private URIFactory() {
    }

    public static URITemplate template(String baseUriString) {
        return new URITemplate(baseUriString);
    }

    public static URIBuilder generic(String baseUriString) {
        return new URIBuilder(baseUriString);
    }

    public static OpaqueURIBuilder opaque(String scheme) {
        return new OpaqueURIBuilder(scheme);
    }

    public static URITemplateVariable var(String name) {
        return var(null, name);
    }

    public static URITemplateVariable var(Character modifier, String name) {
        return new URITemplateVariable(modifier, name, null, false);
    }

    public static URITemplateVariable fragment(String name) {
        return new URITemplateVariable(FRAGMENT_START, name, null, false);
    }

    public static URITemplateVariable queryStart(String name) {
        return new URITemplateVariable(QUERY_START, name, null, false);
    }

    public static URITemplateVariable queryParam(String name) {
        return new URITemplateVariable(QUERY_SEPARATOR, name, null, false);
    }

    public static URITemplateVariable pathVariable(String name) {
        return new URITemplateVariable(PATH_SEPARATOR, name, null, false);
    }

}
