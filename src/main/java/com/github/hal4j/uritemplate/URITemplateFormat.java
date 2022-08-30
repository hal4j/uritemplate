package com.github.hal4j.uritemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.lang.String.join;
import static java.lang.String.valueOf;
import static java.util.stream.Collectors.*;

public class URITemplateFormat {

    public static final char QUERY_START = '?';
    public static final char QUERY_SEPARATOR = '&';
    public static final char DOMAIN_SEPARATOR = '.';
    public static final char MATRIX_SEPARATOR = ';';
    public static final char FRAGMENT_START = '#';
    public static final char PRE_ENCODED = '+';
    public static final char EXPLODE_FLAG = '*';
    public static final char PATH_SEPARATOR = '/';
    public static final char DEFAULT_DELIMITER = ',';
    public static final String DEFAULT_DELIMITER_STRING = valueOf(DEFAULT_DELIMITER);
    public static final char PREFIX_SEPARATOR = ':';
    public static final String MODIFIERS = "+#./;&?=,!@|:";
    private static final Function<Object, String> NO_TRUNCATE = String::valueOf;

    private boolean explode;

    private boolean named;

    private boolean reorder;

    private boolean encoded;

    private boolean renderPrefix;

    private Character prefix;

    private Character itemSeparator;

    public static URITemplateFormat format(Character modifier, boolean explode) {
        if (modifier == null) {
            return new URITemplateFormat(false, explode, false, false, true, null, null);
        }
        Character prefix = modifier;
        Character itemSeparator = modifier;
        boolean named = false;
        boolean reorder = false;
        boolean encoded = true;
        boolean renderPrefix = true;
        switch (modifier) {
            case QUERY_START:
                prefix = QUERY_START;
                itemSeparator = QUERY_SEPARATOR;
                reorder = true;
            case QUERY_SEPARATOR:
                named = true;
                break;
            case MATRIX_SEPARATOR:
                named = explode;
                break;
            case FRAGMENT_START:
                prefix = FRAGMENT_START;
                itemSeparator = null;
                explode=false;
                break;
            case PRE_ENCODED:
                renderPrefix = false;
                itemSeparator = null;
                encoded = false;
                explode = false;
                named = false;
                break;
            default:
        }
        return new URITemplateFormat(renderPrefix, explode, named, reorder, encoded, prefix, itemSeparator);
    }

    URITemplateFormat(boolean renderPrefix,
                             boolean explode,
                             boolean named,
                             boolean reorder,
                             boolean encoded,
                             Character prefix,
                             Character itemSeparator) {
        this.renderPrefix = renderPrefix;
        this.explode = explode;
        this.named = named;
        this.reorder = reorder;
        this.encoded = encoded;
        this.prefix = prefix;
        this.itemSeparator = itemSeparator;
    }

    String render(List<String> names, ParamHolder parameters) {
        List<String> remaining = new LinkedList<>(names);
        StringBuilder builder = new StringBuilder();
        List<String> missing = new ArrayList<>();
        int counter = 0;
        for (Iterator<String> iterator = remaining.iterator(); iterator.hasNext();) {
            String name = iterator.next();
            Function<Object, String> truncate = NO_TRUNCATE;

            int pos = name.indexOf(PREFIX_SEPARATOR);
            if (pos > 0) {
                String number = name.substring(pos + 1);
                try {
                    Integer prefixLength = Integer.parseInt(number);
                    truncate = val -> truncate(prefixLength, val);
                    name = name.substring(0, pos);
                } catch (NumberFormatException e) {
                    // ignore
                }
            }

            Object substitution = parameters.get(name);

            if (substitution != null) {
                if (!missing.isEmpty()) {
                    appendTemplate(builder, counter == 0, missing);
                    counter += missing.size();
                    missing.clear();
                }
                boolean named = this.named || (explode && substitution instanceof Map); // always named for exploded associative arrays (e.g. 3.2.6)
                List<Pair> values = collect(name, substitution, truncate);
                if (values.size() > 0) {
                    if (builder.length() == 0) {
                        if (renderPrefix) {
                            builder.append(prefix);
                        }
                    } else {
                        builder.append(itemSeparator);
                    }
                    String prefix = named ? name + '=' : "";
                    if (explode && itemSeparator != null) {
                        builder.append(values.stream()
                                .map(entry -> (named ? entry.key() + '=' : "") + entry.value())
                                .collect(joining(itemSeparator + "")));
                    } else {
                        builder.append(prefix);
                        builder.append(values.stream().map(Pair::value).collect(joining(DEFAULT_DELIMITER_STRING)));
                    }
                }
                iterator.remove();
                counter++;
            } else if (!reorder) {
                missing.add(name);
                iterator.remove();
            }
        }
        List<String> vars = reorder ? remaining : missing;
        if (vars.size() > 0) {
            appendTemplate(builder, vars.size() == names.size(), vars);
        }
        return builder.toString();
    }

    private String truncate(Integer prefixLength, Object val) {
        String string = valueOf(val);
        return prefixLength != null && prefixLength < string.length()
                ? string.substring(0, prefixLength)
                : string;
    }

    private void appendTemplate(StringBuilder builder, boolean first, List<String> vars) {
        builder.append('{');
        if (prefix != null && first) {
            builder.append(prefix);
        } else if (itemSeparator != null) {
            builder.append(itemSeparator);
        }
        builder.append(join(DEFAULT_DELIMITER_STRING, vars));
        if (explode) {
            builder.append(EXPLODE_FLAG);
        }
        builder.append('}');
    }

    private List<Pair> collect(String name, Object object, Function<Object, String> truncate) {
        if (object == null) return Collections.emptyList();
        List<Pair> map = new ArrayList<>();
        if (object instanceof Map) {
            if (explode) {
                ((Map<?, ?>) object).forEach((key, value) -> map.add(new Pair(toString(key, NO_TRUNCATE), toString(value, NO_TRUNCATE))));
            } else {
                String value = ((Map<String, Object>) object).entrySet().stream().flatMap(entry -> Stream.of(entry.getKey(), toString(entry.getValue(), NO_TRUNCATE))).collect(joining(","));
                map.add(new Pair(name, value));
            }
        } else if (object instanceof Iterable) {
            ((Iterable<?>) object).forEach(entry -> map.add(new Pair(name, toString(entry, NO_TRUNCATE))));
        } else {
            map.add(new Pair(name, toString(object, truncate)));
        }
        return map;
    }
    @SuppressWarnings("unchecked")
    private String toString(Object object, Function<Object, String> truncate) {
        try {
            if (object instanceof Optional) {
                object = ((Optional) object).orElse(null);
            }
            String value = truncate.apply(object);
            if (encoded) {
                value = URLEncoder.encode(value, "UTF-8");
            }
            return value;
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    public String renderName(String name) {
        StringBuilder builder = new StringBuilder();
        appendTemplate(builder, true, Collections.singletonList(name));
        return builder.toString();
    }

    private final static class Pair {
        private final String key;
        private final String value;
        public Pair(String key, String value) {
            this.key = key;
            this.value = value;
        }
        public String key() {
            return this.key;
        }
        public String value() {
            return this.value;
        }

    }

}
