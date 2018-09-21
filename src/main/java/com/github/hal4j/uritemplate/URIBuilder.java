package com.github.hal4j.uritemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.function.Consumer;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

public class URIBuilder {

    private String schemeSpecificPart;

    private String path;
    private String scheme;
    private String host;
    private String userInfo;
    private int port;
    private String query;
    private String fragment;
    private String authority;

    private boolean template;

    URIBuilder(URI uri) {
        this.scheme = uri.getScheme();
        this.fragment = uri.getFragment();
        if (uri.isOpaque()) {
            this.schemeSpecificPart = uri.getSchemeSpecificPart();
        } else {
            this.schemeSpecificPart = null;
            this.userInfo = uri.getUserInfo();
            this.host = uri.getHost();
            this.port = uri.getPort();
            this.path = uri.getPath();
            this.query = uri.getQuery();
            this.authority = uri.getAuthority();
        }
    }

    public static URIBuilder basedOn(String uri) {
        return new URIBuilder(uri);
    }

    URIBuilder(String uriString) {
        this(URI.create(uriString));
    }

    private boolean isOpaque() {
        return this.path == null;
    }

    private Consumer<Object> fragment() {
        return object -> this.fragment = append(this.fragment, object);
    }

    private Consumer<Object> query() {
        return object -> this.query = append(this.query, object);
    }

    private Consumer<Object> path() {
        return object -> this.path = append(this.path, object);
    }

    private Consumer<Object> host() {
        return object -> this.host = append(this.host, object);
    }

    private Consumer<Object> lastSegment() {
        if (fragment != null) return fragment();
        if (query != null) return query();
        if (path != null) return path();
        return host();
    }

    private String append(String string, Object value) {
        String appended = String.valueOf(value);
        if (string == null) {
            return appended;
        }
        return string + appended;
    }

    public URIBuilder append(URITemplateVariable variable) {
        if (!variable.modifier().isPresent()) {
            lastSegment().accept(variable);
        } else {
            switch (variable.modifier().get()) {
                case DOMAIN:
                    host().accept(variable);
                    break;
                case PATH:
                    path().accept(variable);
                    break;
                case QUERY_START:
                case QUERY:
                    query().accept(variable);
                    break;
                case FRAGMENT:
                    fragment().accept(variable);
                    break;
                default:
                    lastSegment().accept(variable);
            }
        }
        this.template = true;

        return this;
    }

    public URIBuilder relative(Object... pathSegments) {
        validate(pathSegments);
        String path = stream(pathSegments)
                .map(String::valueOf)
                .collect(joining("/"));
        this.path = this.path + "/" + path;
        return this;
    }

    public URIBuilder queryParam(String name, Object... values) {
        validate(values);

        StringBuilder builder = new StringBuilder();
        if (query != null) {
            builder.append(query);
            if (query.length() > 0) {
                builder.append('&');
            }
        }
        builder.append(stream(values).map(String::valueOf)
                .map(s -> name + '=' + s)
                .collect(joining("&")));
        this.query = builder.toString();
        return this;
    }

    private void validate(Object[] values) {
        if (this.schemeSpecificPart != null) {
            throw new UnsupportedOperationException("This URI is opaque: " + toString());
        }
        if (stream(values).anyMatch(s -> s instanceof URITemplateVariable)) {
            this.template = true;
        }
    }

    public URI toURI() {
        if (template) {
            throw new IllegalStateException("This URI is template: " + toDecodedString());
        }
        try {
            if (isOpaque()) {
                return new URI(scheme, schemeSpecificPart, fragment);
            }
            URI uri = new URI(scheme, userInfo, host, port, path, query, fragment);
            return uri.normalize();
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }

    public URITemplate asTemplate() {
        return new URITemplate(toDecodedString());
    }

    @Override
    public String toString() {
        return template ? asTemplate().toString() : toURI().toString();
    }

    private String toDecodedString() {
        StringBuilder sb = new StringBuilder();
        if (scheme != null) {
            sb.append(scheme);
            sb.append(':');
        }
        if (isOpaque()) {
            sb.append(schemeSpecificPart);
        } else {
            if (host != null) {
                sb.append("//");
                if (userInfo != null) {
                    sb.append(userInfo);
                    sb.append('@');
                }
                boolean needBrackets = ((host.indexOf(':') >= 0)
                        && !host.startsWith("[")
                        && !host.endsWith("]"));
                if (needBrackets) sb.append('[');
                sb.append(host);
                if (needBrackets) sb.append(']');
                if (port != -1) {
                    sb.append(':');
                    sb.append(port);
                }
            } else if (authority != null) {
                sb.append("//");
                sb.append(authority);
            }
            if (path != null)
                sb.append(path);
            if (query != null) {
                if (!query.startsWith("{?")) {
                    sb.append('?');
                }
                sb.append(query);
            }
        }
        if (fragment != null) {
            if (!fragment.startsWith("{#")) {
                sb.append('#');
            }
            sb.append(fragment);
        }
        return sb.toString();
    }

    public URITemplate resolve(String relativeUri) {
        return new URITemplate(this.toDecodedString() + relativeUri);
    }
}
