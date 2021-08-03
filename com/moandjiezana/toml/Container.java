package com.moandjiezana.toml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

abstract class Container {

    abstract boolean accepts(String s);

    abstract void put(String s, Object object);

    abstract Object get(String s);

    abstract boolean isImplicit();

    private Container() {}

    Container(Object x0) {
        this();
    }

    static class TableArray extends Container {

        private final List values = new ArrayList();

        TableArray() {
            super(null);
            this.values.add(new Container.Table());
        }

        boolean accepts(String key) {
            return this.getCurrent().accepts(key);
        }

        void put(String key, Object value) {
            this.values.add((Container.Table) value);
        }

        Object get(String key) {
            throw new UnsupportedOperationException();
        }

        boolean isImplicit() {
            return false;
        }

        List getValues() {
            ArrayList unwrappedValues = new ArrayList();
            Iterator iterator = this.values.iterator();

            while (iterator.hasNext()) {
                Container.Table table = (Container.Table) iterator.next();

                unwrappedValues.add(table.consume());
            }

            return unwrappedValues;
        }

        Container.Table getCurrent() {
            return (Container.Table) this.values.get(this.values.size() - 1);
        }

        public String toString() {
            return this.values.toString();
        }
    }

    static class Table extends Container {

        private final Map values;
        final String name;
        final boolean implicit;

        Table() {
            this((String) null, false);
        }

        public Table(String name) {
            this(name, false);
        }

        public Table(String tableName, boolean implicit) {
            super(null);
            this.values = new HashMap();
            this.name = tableName;
            this.implicit = implicit;
        }

        boolean accepts(String key) {
            return !this.values.containsKey(key) || this.values.get(key) instanceof Container.TableArray;
        }

        void put(String key, Object value) {
            this.values.put(key, value);
        }

        Object get(String key) {
            return this.values.get(key);
        }

        boolean isImplicit() {
            return this.implicit;
        }

        Map consume() {
            Iterator iterator = this.values.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();

                if (entry.getValue() instanceof Container.Table) {
                    entry.setValue(((Container.Table) entry.getValue()).consume());
                } else if (entry.getValue() instanceof Container.TableArray) {
                    entry.setValue(((Container.TableArray) entry.getValue()).getValues());
                }
            }

            return this.values;
        }

        public String toString() {
            return this.values.toString();
        }
    }
}
