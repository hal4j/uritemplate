package com.github.hal4j.uritemplate;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.function.Consumer;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

public class URIBuilder {

    private String schemeSpecificPart;

    private String path;
    private String appendedPath;

    private String scheme;

    private String host;
    private String appendedHost;

    private String userInfo;
    private int port;

    private String query;
    private String appendedQuery;

    private String fragment;
    private String appendedFragment;

    private String authority;

    private boolean template;

    public static URIBuilder uri(String scheme, String host, int port) {
        try {
            return new URIBuilder(new URI(scheme, null, host, port, null, null, null));
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static URIBuilder basedOn(Object uriBuilder) {
        return basedOn(uriBuilder.toString());
    }

    public static URIBuilder basedOn(String uriString) {
        return new URIBuilder(URI.create(uriString));
    }

    public static URIBuilder basedOn(URI uri) {
        return new URIBuilder(uri);
    }


    URIBuilder(String uriString) {
        this(URI.create(uriString));
    }

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

    private boolean isOpaque() {
        return this.path == null;
    }

    public Fragment path() {
        return new Fragment("/", "/", object -> append(this.appendedPath, object, value -> this.appendedPath = value));
    }

    public Fragment fragment() {
        return new Fragment(null, "/", object -> append(this.appendedFragment, object, value -> this.appendedFragment = value));
    }

    public Fragment host() {
        return new Fragment(null, ".", object -> append(this.appendedHost, object, value -> this.appendedHost = value));
    }

    public Fragment query() {
        return new Fragment(null, "&", object -> append(this.appendedQuery, object, value -> this.appendedQuery = value));
    }

    private Fragment lastSegment() {
        if (appendedFragment != null || fragment != null) return fragment();
        if (appendedQuery != null || query != null) return query();
        if (appendedPath != null || (path  != null && !path.isEmpty())) return path();
        return host();
    }

    private void append(String string, Object value, Consumer<String> newValue) {
        String appendedString;
        if (value instanceof URIVarComponent) {
            value = URITemplateVariable.template((URIVarComponent) value);
        }
        if (value instanceof URITemplateVariable) {
            appendedString = value.toString();
        } else {
            appendedString = String.valueOf(value);
        }
        newValue.accept(string == null ? appendedString : string + appendedString);
    }

    public URIBuilder append(URITemplateVariable variable) {
        if (!variable.modifier().isPresent()) {
            lastSegment().join(variable);
        } else {
            switch (variable.modifier().get()) {
                case DOMAIN:
                    host().join(variable);
                    break;
                case PATH:
                    path().join(variable);
                    break;
                case QUERY_START:
                case QUERY:
                    query().join(variable);
                    break;
                case FRAGMENT:
                    fragment().join(variable);
                    break;
                default:
                    lastSegment().join(variable);
            }
        }
        this.template = true;

        return this;
    }

    public URIBuilder relative(Object... pathSegments) {
        validate(pathSegments);
        path().append(pathSegments);
        return this;
    }

    public URIBuilder queryParam(String name, Object... values) {
        validate(values);
        StringBuilder builder = new StringBuilder();
        if (appendedQuery != null) {
            builder.append(appendedQuery);
            if (appendedQuery.length() > 0) {
                builder.append('&');
            }
        }
        builder.append(stream(values).map(String::valueOf)
                .map(s -> name + '=' + s)
                .collect(joining("&")));
        this.appendedQuery = builder.toString();
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
        String host = merge(false, this.host, this.appendedHost, "");
        String path = merge(false, this.path, this.appendedPath, "");
        String query = merge(false, this.query, this.appendedQuery, "&");
        String fragment = merge(false, this.fragment, this.appendedFragment, "");
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

    private String merge(boolean encode, String uriPart, String appendedPart, String delimiter) {
        if (uriPart == null || uriPart.isEmpty()) return appendedPart;
        String prefix = encode ? encodePartIgnoreDelimiters(uriPart) : uriPart;
        if (appendedPart == null) return prefix;
        return prefix + delimiter + appendedPart;
    }

    private String encodePartIgnoreDelimiters(String uriPart) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < uriPart.length(); i++) {
            char c = uriPart.charAt(i);
            if (c == '&' || c == '=' || c == '/') {
                result.append(c);
            } else {
                try {
                    result.append(URLEncoder.encode(String.valueOf(c), "utf-8"));
                } catch (UnsupportedEncodingException e) {
                    throw new IllegalStateException(e);
                }
            }

        }
        return result.toString();
    }

    public URITemplate asTemplate() {
        return new URITemplate(toDecodedString());
    }

    @Override
    public String toString() {
        return template ? asTemplate().toString() : toURI().toString();
    }

    private String toDecodedString() {
        String host = merge(true, this.host, this.appendedHost, "");
        String path = merge(true, this.path, this.appendedPath, "");
        String query = merge(true, this.query, this.appendedQuery, "&");
        String fragment = merge(true, this.fragment, this.appendedFragment, "");
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

    public class Fragment {
        private final String prefix;
        private final String delimiter;
        private final Consumer<Object> consumer;

        public Fragment(String prefix, String delimiter, Consumer<Object> consumer) {
            this.prefix = prefix;
            this.delimiter = delimiter;
            this.consumer = consumer;
        }

        public URIBuilder join(Object... values) {
            for (Object value : values) {
                consumer.accept(value);
            }
            return URIBuilder.this;
        }

        public URIBuilder append(Object... values) {
            String currentDelimiter = prefix;
            for (Object value : values) {
                if (currentDelimiter != null) {
                    consumer.accept(currentDelimiter);
                }
                consumer.accept(value);
                currentDelimiter = delimiter;

            }
            return URIBuilder.this;
        }
    }


}
