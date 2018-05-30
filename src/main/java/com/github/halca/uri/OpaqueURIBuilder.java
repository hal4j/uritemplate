package com.github.halca.uri;

import java.net.URI;
import java.net.URISyntaxException;

public class OpaqueURIBuilder {

    private final String scheme;
    private String ssp;
    private String fragment;

    public OpaqueURIBuilder(URI uri) {
        this(uri.getScheme());
        this.ssp = uri.getSchemeSpecificPart();
        this.fragment = uri.getFragment();
    }

    public OpaqueURIBuilder(String scheme) {
        if (scheme == null) {
            throw new NullPointerException("scheme");
        }
        this.scheme = scheme;
    }

    public OpaqueURIBuilder ssp(CharSequence... opaqueParts) {
        return this.sspJoined("", opaqueParts);
    }

    public OpaqueURIBuilder sspJoined(CharSequence delimiter, CharSequence... opaqueParts) {
        this.ssp = String.join(delimiter, opaqueParts);
        return this;
    }

    public OpaqueURIBuilder fragment(CharSequence... fragments) {
        return this.fragmentJoined("", fragments);
    }

    public OpaqueURIBuilder fragmentJoined(CharSequence delimiter, CharSequence... fragments) {
        this.fragment = String.join(delimiter, fragments);
        return this;
    }

    public URI toURI() {
        try {
            return new URI(scheme, ssp, fragment);
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Invalid URI component", e);
        }
    }

    @Override
    public String toString() {
        return (scheme != null ? scheme + ":" : "") + ssp + (fragment != null ? "#" + fragment : "");
    }

}
