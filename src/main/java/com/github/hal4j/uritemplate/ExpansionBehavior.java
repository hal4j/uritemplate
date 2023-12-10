package com.github.hal4j.uritemplate;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class ExpansionBehavior {

    private final Character first;

    private final char separator;

    private final boolean named;

    private final Character empty;

    private final boolean allowReserved;

    public static ExpansionBehavior expand() {
        return new ExpansionBehavior(null, ',', false, null, false);
    }

    public static ExpansionBehavior expandAs(Character first) {
        return new ExpansionBehavior(first, first, false, null, false);
    }

    private ExpansionBehavior(Character first, char separator, boolean named, Character empty, boolean allowReserved) {
        this.first = first;
        this.separator = separator;
        this.named = named;
        this.empty = empty;
        this.allowReserved = allowReserved;
    }

    public ExpansionBehavior separator(char separator) {
        return new ExpansionBehavior(first, separator, named, empty, allowReserved);
    }

    public ExpansionBehavior ifEmptyExplodeWith(char empty) {
        return new ExpansionBehavior(first, separator, named, empty, allowReserved);
    }

    public ExpansionBehavior named() {
        return new ExpansionBehavior(first, separator, true, empty, allowReserved);
    }

    public ExpansionBehavior allowReserved() {
        return new ExpansionBehavior(first, separator, named, empty, true);
    }

    /**
     * This algorithm is based on RFC 6570 Appendix A (implementation hints) with some tweaks to handle first elements
     * in collections correctly.
     * @param varname name of the variable
     * @param value value to substitute (can be a Collection or Map)
     * @param explode if <code>true</code>, an explode modifier was present in the template
     * @param prefixLength if not <code>null</code>, a prefix value was specified in the template
     * @param isFirst if <code>true</code>, the varname is the first component of template variable
     * @param result the resulting string to which the substitution must be appended
     * @return <code>true</code> if the result string was modified, <code>false</code> otherwise.
     */
    public boolean expand(String varname,
                       Object value,
                       boolean explode,
                       Integer prefixLength,
                       boolean isFirst,
                       StringBuilder result) {
        if (value instanceof Collection) {
            Collection<Object> list = (Collection<Object>) value;
            if (list.isEmpty()) return false;
            appendPrefix(isFirst, result);
            if (explode) {
                if (named) {
                    appendExplodedNamed(isFirst, result, list, v -> v, v -> varname);
                } else {
                    String delimiter = String.valueOf(separator);
                    appendAll(result, list, delimiter);
                }
            } else {
                appendNonExploded(varname, result, list);
            }
        } else if (value instanceof Map) {
            Set<Map.Entry<Object, Object>> pairs = ((Map<Object, Object>) value).entrySet();
            if (pairs.isEmpty()) return false;
            appendPrefix(isFirst, result);
            if (explode) {
                appendExplodedNamed(isFirst, result, pairs, Map.Entry::getValue, e -> e.getKey().toString());
            } else {
                appendNonExploded(varname, result, pairs.stream()
                        .filter(e -> e.getValue() != null)
                        .flatMap(e -> Stream.of(e.getKey(), e.getValue()))
                        .collect(toList()));
            }
        } else {
            appendPrefix(isFirst, result);
            String s = value.toString();
            if (named) {
                result.append(varname);
                if (s.isEmpty()) {
                    if (empty != null) result.append(empty);
                } else {
                    result.append('=');
                }
            }
            if (prefixLength != null) {
                result.append(encodeValue(substring(s, prefixLength)));
            } else {
                result.append(encodeValue(s));
            }
        }
        return true;
    }

    private void appendPrefix(boolean isFirst, StringBuilder result) {
        if (isFirst) {
            if (first != null) result.append(first);
        } else {
            result.append(separator);
        }
    }

    private <T> void appendExplodedNamed(boolean isFirst,
                                         StringBuilder result,
                                         Iterable<T> iterable,
                                         Function<T, Object> getValue,
                                         Function<T, String> getKey) {
        boolean currentFirst = true;
        for (T entry : iterable) {
            Object entryValue = getValue.apply(entry);
            if (entryValue == null) continue;
            String s = entryValue.toString();
            String name = getKey.apply(entry);
            if (!currentFirst) result.append(separator);
            currentFirst = false;
            result.append(named ? name : encodeValue(name));
            if (s.isEmpty() && named) {
                result.append(empty);
            } else {
                result.append('=').append(encodeValue(s));
            }
        }
    }

    private void appendNonExploded(String varname, StringBuilder result, Collection<? extends Object> list) {
        if (named) {
            result.append(encodeVarname(varname));
            if (list.isEmpty()) {
                if (empty != null) result.append(empty);
                return;
            }
            result.append('=');
        }
        appendAll(result, list, ",");
    }

    private void appendAll(StringBuilder result, Collection<? extends Object> list, String delimiter) {
        result.append(list.stream()
                .filter(Objects::nonNull)
                .map(Object::toString)
                .map(this::encodeValue)
                .collect(Collectors.joining(delimiter)));
    }

    private String encodeValue(String s) {
        return allowReserved ? PercentEncoder.LITERAL.encode(s) : PercentEncoder.DEFAULT.encode(s);
    }

    private String encodeVarname(String s) {
        return PercentEncoder.LITERAL.encode(s);
    }

    private String substring(String s, int prefix) {
        return s.length() <= prefix ? s : s.substring(0, prefix);
    }
}
