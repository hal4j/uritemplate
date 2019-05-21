package com.github.hal4j.uritemplate;

import java.util.Map;

public interface ParamHolder {

    boolean containsKey(String name);

    Object get(String name);

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
        public Object get(String name) {
            return array[index++];
        }
    }

}
