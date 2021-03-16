package com.github.hal4j.uritemplate;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

/**
 *
 */
public class URIBuilder {

    public static final int UNSET_PORT = -1;
    private String schemeSpecificPart;
    private String appendedSsp;

    private String path;
    private String appendedPath;

    private String scheme;

    private String host;
    private String appendedHost;

    private String userInfo;
    private int port;
    private String portTemplate;

    private String query;
    private String appendedQuery;

    private String fragment;
    private String appendedFragment;

    private String authority;

    private boolean template;

    private boolean serverTemplate;

    /**
     * Create new URI builder initialized with given scheme, host and port
     * @param scheme valid URI scheme
     * @param host valid URI host
     * @param port valid URI port or -1 if protocol default
     * @return an URI builder initialized with given components
     */
    public static URIBuilder uri(String scheme, String host, int port) {
        try {
            return new URIBuilder(new URI(scheme, null, host, port, null, null, null));
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Create new instance of URI builder initialized with URI defined by string representation of given object
     * @param origin the object that will be converted to string and then parsed as URI
     * @return new instance of URI builder
     */
    public static URIBuilder basedOn(Object origin) {
        return basedOn(origin.toString());
    }

    /**
     * Create  new instance of URI builder initialized with URI provided in given string
     * @param uriString a string containing valid URI
     * @return new instance of URI builder
     */
    public static URIBuilder basedOn(String uriString) {
        return new URIBuilder(URI.create(uriString));
    }

    /**
     * Create new instance of URI builder initialized with given URI
     * @param uri the base URI
     * @return new instance of URI builder
     */
    public static URIBuilder basedOn(URI uri) {
        return new URIBuilder(uri);
    }

    URIBuilder(boolean opaque) {
        this.scheme = null;
        if (opaque) {
            this.schemeSpecificPart = "";
        } else {
            this.host = "";
            this.path = "";
            this.port = -1;
        }
    }

    URIBuilder(String scheme, boolean opaque) {
        this(opaque);
        this.scheme = scheme;
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

    /**
     * Checks if the constructed URI will be opaque, that is, its scheme-specific part will not start with '/'.
     * @return <code>true</code> if constructed URI is opaque, <code>false</code> otherwise.
     */
    private boolean isOpaque() {
        return this.schemeSpecificPart != null;
    }

    private void assertNotOpaque() {
        if (isOpaque()) {
            throw new IllegalStateException("Constructed URI is opaque: only scheme-specific part can be modified");
        }
    }

    public URIBuilder server(URITemplateVariable variable) {
        this.serverTemplate = true;
        this.host = null;
        this.port = UNSET_PORT;
        this.scheme = null;
        this.appendedHost = variable.toString();
        this.template = true;
        return this;
    }

    public URIBuilder clearScheme() {
        this.scheme = null;
        return this;
    }

    /**
     * Set new value of the scheme of the constructed URI
     * @param newScheme new scheme
     * @return this
     */
    public URIBuilder scheme(String newScheme) {
        this.scheme = newScheme;
        return this;
    }

    /**
     * Set new value of the scheme of the constructed URI as a template
     * @param variable scheme template variable
     * @return this
     */
    public URIBuilder scheme(URITemplateVariable variable) {
        this.scheme = variable.toString();
        this.template = true;
        return this;
    }

    /**
     * Set new value of the scheme-specific part of the constructed URI
     * @param ssPart new scheme-specific part
     * @return this
     */
    public URIBuilder ssp(String ssPart) {
        this.schemeSpecificPart = ssPart;
        return this;
    }

    /**
     * Provides DSL for constructing scheme-specific part of the URI
     * @return a builder for scheme-specific part of the URI
     * @see URIPartBuilder
     */
    public URIPartBuilder ssp() {
        if (!isOpaque()) {
            throw new IllegalStateException("Constructed URI is not opaque: cannot modify scheme-specific part as a whole");
        }
        return new URIPartBuilder(() -> this.schemeSpecificPart, (String value) -> this.schemeSpecificPart = value,
                null, null,
                object -> append(this.appendedSsp, object, value -> this.appendedSsp = value),
                () -> this.appendedSsp);
    }

    /**
     * Set user info part of the constructed URI as given value
     * @param info new user info part
     * @return this
     */
    public URIBuilder userInfo(String info) {
        assertNotOpaque();
        this.userInfo = info;
        return this;
    }

    /**
     * Set user info part of this URI as colon-separated user name and password
     * @param username the username part of the user info
     * @param password the password part of the user info
     * @return this
     */
    public URIBuilder userInfo(String username, String password) {
        assertNotOpaque();
        this.userInfo = username + ":" + password;
        return this;
    }
    /**
     * Set new value of the host name of the constructed URI
     * @param newHost new host name
     * @return this
     */
    public URIBuilder host(String newHost) {
        assertNotOpaque();
        this.host = newHost;
        return this;
    }

    /**
     * Set new value of the host of the constructed URI as a template
     * @param variable host template variable
     * @return this
     */
    public URIBuilder host(URITemplateVariable variable) {
        assertNotOpaque();
        this.host = null;
        this.appendedHost = variable.toString();
        this.template = true;
        return this;
    }

    /**
     * Provides DSL for URI host name
     * @return a builder for URI host name
     * @see URIPartBuilder
     */
    public URIPartBuilder host() {
        assertNotOpaque();
        return new URIPartBuilder(() -> this.host, (String value) -> this.host = value,
                '.', null,
                object -> append(this.appendedHost, object, value -> this.appendedHost = value),
                () -> this.appendedHost);
    }


    /**
     * Set new value of the port of the constructed URI as a template
     * @param variable port template variable
     * @return this
     */
    public URIBuilder port(URITemplateVariable variable) {
        assertNotOpaque();
        this.portTemplate = variable.toString();
        this.template = true;
        return this;
    }

    /**
     * Set new value of the port of the constructed URI. Value -1 will define port as scheme default (port will be omitted in the constructed URI).
     * @param portNumber new port number
     * @return this
     */
    public URIBuilder port(int portNumber) {
        assertNotOpaque();
        this.port = portNumber;
        return this;
    }


    /**
     * Sets the new value of the port to default for the specified scheme
     * @return this
     */
    public URIBuilder defaultPort() {
        assertNotOpaque();
        this.port = UNSET_PORT;
        return this;
    }

    /**
     * Provides DSL for constructing path
     * @return builder for URI path
     * @see URIPartBuilder
     */
    public URIPartBuilder path() {
        assertNotOpaque();
        return new URIPartBuilder(() -> this.path, value -> this.path = value,
                '/', this.path == null || this.path.isEmpty() ? '/' : null,
                object -> append(this.appendedPath, object, value -> this.appendedPath = value),
                () -> this.appendedPath);
    }

    /**
     * Set fragment part of the constructed URI
     * @param newFragment new fragment part
     * @return this
     */
    public URIBuilder fragment(String newFragment) {
        this.fragment = newFragment;
        return this;
    }

    /**
     * Provides DSL for constructing fragment
     * @return builder for URI fragment
     * @see URIPartBuilder
     */
    public URIPartBuilder fragment() {
        return new URIPartBuilder(() -> this.fragment, (String value) -> this.fragment = value,
                null, null,
                object -> append(this.appendedFragment, object, value -> this.appendedFragment = value),
                () -> this.appendedFragment);
    }



    /**
     * Provides DSL for constructing query part of URI
     * @return builder for URI query part
     * @see URIPartBuilder
     */
    public URIPartBuilder query() {
        assertNotOpaque();
        return new URIPartBuilder(() -> this.query, (String value) -> this.query = value,
                '&', this.query == null ? null : '&',
                object -> append(this.appendedQuery, object, value -> this.appendedQuery = value),
                () -> this.appendedQuery);
    }

    private URIPartBuilder lastSegment() {
        if (isOpaque()) {
            return ssp();
        } else {
            if (appendedFragment != null || fragment != null) return fragment();
            if (appendedQuery != null || query != null) return query();
            if (appendedPath != null || (path != null && !path.isEmpty())) return path();
            return host();
        }
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

    /**
     * Append template variable to the URI part, corresponding to variable modifier,
     * or to the last part of the URI.
     * @param variable variable to append
     * @return this
     */
    public URIBuilder append(URITemplateVariable variable) {
        if (!variable.modifier().isPresent()) {
            lastSegment().append(variable);
        } else {
            switch (variable.modifier().get()) {
                case DOMAIN:
                    host().append(variable);
                    break;
                case PATH:
                    path().append(variable);
                    break;
                case QUERY_START:
                case QUERY:
                    query().append(variable);
                    break;
                case FRAGMENT:
                    fragment().append(variable);
                    break;
                default:
                    lastSegment().append(variable);
            }
        }
        this.template = true;

        return this;
    }

    /**
     * Joins string representations of given objects with path
     * @param pathSegments objects to be appended to path with forward slash character
     * @return this
     * @see URIPartBuilder#join(Object...)
     */
    public URIBuilder relative(Object... pathSegments) {
        this.path().join(pathSegments);
        return this;
    }

    /**
     * Add parameter with given name and values to the query part of URI
     * @param name name of the parameter
     * @param values one or more values
     * @return this
     */
    public URIBuilder queryParam(String name, Object... values) {
        this.query().join(stream(values).map(String::valueOf).map(s -> name + '=' + s).toArray());
        return this;
    }

    public boolean isTemplate() {
        return template;
    }

    /**
     * Build new URI
     * @return new URI constructed from the components specified in this builder
     * @throws IllegalStateException if at least one of the components contains a template variable
     */
    public URI toURI() {
        if (template) {
            throw new IllegalStateException("This URI is template: " + toDecodedString());
        }
        try {
            String fragment = merge(false, this.fragment, this.appendedFragment);
            if (isOpaque()) {
                String ssp = merge(false, this.schemeSpecificPart, this.appendedSsp);
                return new URI(scheme, ssp, fragment);
            } else {
                String host = merge(false, this.host, this.appendedHost);
                String path = merge(false, this.path, this.appendedPath);
                String query = merge(false, this.query, this.appendedQuery);
                URI uri = new URI(scheme, userInfo, host, port, path, query, fragment);
                return uri.normalize();
            }
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }

    private String merge(boolean encode, String uriPart, String appendedPart) {
        if (uriPart == null || uriPart.isEmpty()) return appendedPart;
        String prefix = encode ? encodePartIgnoreDelimiters(uriPart) : uriPart;
        if (appendedPart == null) return prefix;
        return prefix + appendedPart;
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

    /**
     * Build new URI template from the components defined in this builder
     * @return new URI template
     */
    public URITemplate asTemplate() {
        return new URITemplate(toDecodedString());
    }

    /**
     * Build string representation of an URI or an URI template, constructed from the components of this Builder
     * @return string representation of a constructed object
     */
    @Override
    public String toString() {
        return template ? asTemplate().toString() : toURI().toString();
    }

    private String toDecodedString() {
        String host = merge(true, this.host, this.appendedHost);
        String path = merge(true, this.path, this.appendedPath);
        String query = merge(true, this.query, this.appendedQuery);
        String fragment = merge(true, this.fragment, this.appendedFragment);
        StringBuilder sb = new StringBuilder();
        if (scheme != null) {
            sb.append(scheme);
            sb.append(':');
        }
        if (isOpaque()) {
            sb.append(schemeSpecificPart);
        } else {
            if (host != null) {
                if (!serverTemplate || scheme != null) {
                    sb.append("//");
                }
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
                if (portTemplate != null) {
                    sb.append(':').append(portTemplate);
                } else if (port != -1) {
                    sb.append(':');
                    sb.append(port);
                }
            } else if (authority != null) {
                sb.append("//");
                sb.append(authority);
            }
            append(sb, '/', path);
            append(sb, '?', query);
        }
        append(sb, '#', fragment);
        return sb.toString();
    }

    private void append(StringBuilder buffer, char prefix, String value) {
        if (value == null || value.isEmpty()) return;
        if (!value.startsWith(String.valueOf(prefix)) && !value.startsWith("{" + prefix)) {
            buffer.append(prefix);
        }
        buffer.append(value);
    }

    /**
     * Append relative URI to the decoded string representation of this builder
     * and return result as URI template.
     * @param relativeUri the relative URI to append
     * @return new URI template constructed from this builder decoded string representation
     *         appended by given relative URI
     */
    public URITemplate resolve(String relativeUri) {
        return new URITemplate(this.toDecodedString() + relativeUri);
    }

    /**
     * DSL for constructing components of the URI
     */
    public class URIPartBuilder {
        private final Supplier<String> initialValue;
        private final Consumer<String> replaceInitialValue;
        private final Supplier<String> appendedValue;
        private Character delimiter;
        private Character currentDelimiter;
        private final Consumer<Object> consumer;

        URIPartBuilder(Supplier<String> initialValue,
                       Consumer<String> replaceInitialValue,
                       Character delimiter,
                       Character currentDelimiter,
                       Consumer<Object> consumer,
                       Supplier<String> appendedValue) {
            this.initialValue = initialValue;
            this.replaceInitialValue = replaceInitialValue;
            this.appendedValue = appendedValue;
            this.currentDelimiter = currentDelimiter;
            this.delimiter = delimiter;
            this.consumer = consumer;
        }

        /**
         * Set new delimiter for the {@link #join(Object...)} method (applicable only for opaque URIs)
         * @param newDelimiter delimiter to use in join() method
         * @return this
         */
        public URIPartBuilder delimiter(Character newDelimiter) {
            if (this.delimiter != null) {
                throw new IllegalStateException("Delimiter already set: '" + delimiter + "'");
            }
            this.delimiter = newDelimiter;
            return this;
        }

        /**
         * Append string representations of given values to this fragment without delimiter and prefix.
         * @param values values to append
         * @return URIBuilder.this
         */
        public URIBuilder append(Object... values) {
            for (Object value : values) {
                if (value instanceof URITemplateVariable) {
                    URIBuilder.this.template = true;
                }
                consumer.accept(value);
            }
            return URIBuilder.this;
        }

        /**
         * Append string representations of given values to this fragment, separated by the
         * configured delimiter. The delimiters for path, query and hosts are fixed.
         * Delimiter for the scheme-specific part of opaque URIs can be configured
         * (once per call to URIBuilder{@link #ssp()} method).
         * @param values values to append
         * @return URIBuilder.this
         */
        public URIBuilder join(Object... values) {
            for (Object value : values) {
                boolean includeDelimiter = currentDelimiter != null;
                if (value instanceof URITemplateVariable) {
                    URIBuilder.this.template = true;
                    Optional<URITemplateModifier> modifier = ((URITemplateVariable) value).modifier();
                    includeDelimiter = currentDelimiter != null
                            && (!modifier.isPresent() || !currentDelimiter.equals(modifier.get().modifierChar()));
                }
                if (includeDelimiter) {
                    consumer.accept(currentDelimiter);
                }
                consumer.accept(value);
                currentDelimiter = delimiter;

            }
            return URIBuilder.this;
        }

        /**
         * Replace given substring in this fragment with a new value
         * @param substring the substring to be replaced
         * @param newValue the new value to be substituted
         * @return URIBuilder.this
         */
        public URIBuilder replace(String substring, String newValue) {
            if (initialValue == null || this.replaceInitialValue == null) {
                throw new UnsupportedOperationException("Content of this fragment cannot be replaced");
            }
            String result = initialValue.get().replace(substring, newValue);
            replaceInitialValue.accept(result);
            return URIBuilder.this;
        }

        @Override
        public String toString() {
            return merge(template, initialValue.get(), appendedValue.get());
        }

    }


}
