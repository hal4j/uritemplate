package com.github.hal4j.uritemplate;

import java.util.Map;

import static com.github.hal4j.uritemplate.URITemplateParser.DISCARDED;

public interface ParamHolder {

    boolean containsKey(String name);

    Object get(String name);

    static ParamHolder discardMissing(ParamHolder holder) {
        return new ParamHolder() {
            @Override
            public boolean containsKey(String name) {
                return true;
            }

            @Override
            public Object get(String name) {
                return holder.containsKey(name) ? holder.get(name) : null;
            }
        };
    }

    static ParamHolder prefixed(String key, Map<String, String> params) {
        return new ParamHolder() {
            String prefix = key + URIVarComponent.NAME_SEPARATOR;
            int length = prefix.length();
            @Override
            public boolean containsKey(String name) {
                return name.startsWith(prefix) && params.containsKey(key(name));
            }

            @Override
            public Object get(String name) {
                return params.get(key(name));
            }

            private String key(String name) {
                return name.substring(length);
            }
        };
    }

    default boolean ignore(String name) {
        return get(name) == DISCARDED;
    }

    class ParamMap implements ParamHolder {
        private final Map<String, ?> params;
        ParamMap(Map<String, ?> params) {
            this.params = params;
        }
        @Override
        public boolean containsKey(String name) {
            return params.containsKey(name);
        }

        @Override
        public Object get(String name) {
            return params.get(name);
        }
    }

    class ParamArray implements ParamHolder {

        ParamArray(Object[] array) {
            this.array = array;
        }

        private final Object[] array;
        private int index = 0;

        @Override
        public boolean containsKey(String name) {
            return index < array.length;
        }

        @Override
        public boolean ignore(String name) {
            return false;
        }

        @Override
        public Object get(String name) {
            if (index >= array.length) return null;
            return array[index++];
        }
    }

    static ParamMap map(Map<String, ?> params) {
        return new ParamMap(params);
    }

}
