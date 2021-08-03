package com.moandjiezana.toml;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Toml {

    private static final Gson DEFAULT_GSON = new Gson();
    private Map values;
    private final Toml defaults;

    public Toml() {
        this((Toml) null);
    }

    public Toml(Toml defaults) {
        this(defaults, new HashMap());
    }

    public Toml read(File file) {
        try {
            return this.read((Reader) (new InputStreamReader(new FileInputStream(file), "UTF8")));
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    public Toml read(InputStream inputStream) {
        return this.read((Reader) (new InputStreamReader(inputStream)));
    }

    public Toml read(Reader reader) {
        BufferedReader bufferedReader = null;

        try {
            bufferedReader = new BufferedReader(reader);
            StringBuilder e = new StringBuilder();

            for (String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
                e.append(line).append('\n');
            }

            this.read(e.toString());
            return this;
        } catch (IOException ioexception) {
            throw new RuntimeException(ioexception);
        } finally {
            try {
                bufferedReader.close();
            } catch (IOException ioexception1) {
                ;
            }

        }
    }

    public Toml read(Toml otherToml) {
        this.values = otherToml.values;
        return this;
    }

    public Toml read(String tomlString) throws IllegalStateException {
        Results results = TomlParser.run(tomlString);

        if (results.errors.hasErrors()) {
            throw new IllegalStateException(results.errors.toString());
        } else {
            this.values = results.consume();
            return this;
        }
    }

    public String getString(String key) {
        return (String) this.get(key);
    }

    public String getString(String key, String defaultValue) {
        String val = this.getString(key);

        return val == null ? defaultValue : val;
    }

    public Long getLong(String key) {
        return (Long) this.get(key);
    }

    public Long getLong(String key, Long defaultValue) {
        Long val = this.getLong(key);

        return val == null ? defaultValue : val;
    }

    public List getList(String key) {
        List list = (List) this.get(key);

        return list;
    }

    public List getList(String key, List defaultValue) {
        List list = this.getList(key);

        return list != null ? list : defaultValue;
    }

    public Boolean getBoolean(String key) {
        return (Boolean) this.get(key);
    }

    public Boolean getBoolean(String key, Boolean defaultValue) {
        Boolean val = this.getBoolean(key);

        return val == null ? defaultValue : val;
    }

    public Date getDate(String key) {
        return (Date) this.get(key);
    }

    public Date getDate(String key, Date defaultValue) {
        Date val = this.getDate(key);

        return val == null ? defaultValue : val;
    }

    public Double getDouble(String key) {
        return (Double) this.get(key);
    }

    public Double getDouble(String key, Double defaultValue) {
        Double val = this.getDouble(key);

        return val == null ? defaultValue : val;
    }

    public Toml getTable(String key) {
        Map map = (Map) this.get(key);

        return map != null ? new Toml((Toml) null, map) : null;
    }

    public List getTables(String key) {
        List tableArray = (List) this.get(key);

        if (tableArray == null) {
            return null;
        } else {
            ArrayList tables = new ArrayList();
            Iterator iterator = tableArray.iterator();

            while (iterator.hasNext()) {
                Map table = (Map) iterator.next();

                tables.add(new Toml((Toml) null, table));
            }

            return tables;
        }
    }

    public boolean contains(String key) {
        return this.get(key) != null;
    }

    public boolean containsPrimitive(String key) {
        Object object = this.get(key);

        return object != null && !(object instanceof Map) && !(object instanceof List);
    }

    public boolean containsTable(String key) {
        Object object = this.get(key);

        return object != null && object instanceof Map;
    }

    public boolean containsTableArray(String key) {
        Object object = this.get(key);

        return object != null && object instanceof List;
    }

    public boolean isEmpty() {
        return this.values.isEmpty();
    }

    public Object to(Class targetClass) {
        JsonElement json = Toml.DEFAULT_GSON.toJsonTree(this.toMap());

        return targetClass == JsonElement.class ? targetClass.cast(json) : Toml.DEFAULT_GSON.fromJson(json, targetClass);
    }

    public Map toMap() {
        HashMap valuesCopy = new HashMap(this.values);

        if (this.defaults != null) {
            Iterator iterator = this.defaults.values.entrySet().iterator();

            while (iterator.hasNext()) {
                java.util.Map.Entry entry = (java.util.Map.Entry) iterator.next();

                if (!valuesCopy.containsKey(entry.getKey())) {
                    valuesCopy.put(entry.getKey(), entry.getValue());
                }
            }
        }

        return valuesCopy;
    }

    public Set entrySet() {
        LinkedHashSet entries = new LinkedHashSet();
        Iterator iterator = this.values.entrySet().iterator();

        while (iterator.hasNext()) {
            java.util.Map.Entry entry = (java.util.Map.Entry) iterator.next();
            Class entryClass = entry.getValue().getClass();

            if (Map.class.isAssignableFrom(entryClass)) {
                entries.add(new Toml.Entry((String) entry.getKey(), this.getTable((String) entry.getKey()), null));
            } else if (List.class.isAssignableFrom(entryClass)) {
                List value = (List) entry.getValue();

                if (!value.isEmpty() && value.get(0) instanceof Map) {
                    entries.add(new Toml.Entry((String) entry.getKey(), this.getTables((String) entry.getKey()), null));
                } else {
                    entries.add(new Toml.Entry((String) entry.getKey(), value, null));
                }
            } else {
                entries.add(new Toml.Entry((String) entry.getKey(), entry.getValue(), null));
            }
        }

        return entries;
    }

    private Object get(String key) {
        if (this.values.containsKey(key)) {
            return this.values.get(key);
        } else {
            Object current = new HashMap(this.values);
            Keys.Key[] keys = Keys.split(key);
            Keys.Key[] akeys_key = keys;
            int i = keys.length;

            for (int j = 0; j < i; ++j) {
                Keys.Key k = akeys_key[j];

                if (k.index == -1 && current instanceof Map && ((Map) current).containsKey(k.path)) {
                    return ((Map) current).get(k.path);
                }

                current = ((Map) current).get(k.name);
                if (k.index > -1 && current != null) {
                    if (k.index >= ((List) current).size()) {
                        return null;
                    }

                    current = ((List) current).get(k.index);
                }

                if (current == null) {
                    return this.defaults != null ? this.defaults.get(key) : null;
                }
            }

            return current;
        }
    }

    private Toml(Toml defaults, Map values) {
        this.values = new HashMap();
        this.values = values;
        this.defaults = defaults;
    }

    private class Entry implements java.util.Map.Entry {

        private final String key;
        private final Object value;

        public String getKey() {
            return this.key;
        }

        public Object getValue() {
            return this.value;
        }

        public Object setValue(Object value) {
            throw new UnsupportedOperationException("TOML entry values cannot be changed.");
        }

        private Entry(String key, Object value) {
            this.key = key;
            this.value = value;
        }

        Entry(String x1, Object x2, Object x3) {
            this(x1, x2);
        }
    }
}
