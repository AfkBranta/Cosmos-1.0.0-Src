package com.moandjiezana.toml;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class MapValueWriter implements ValueWriter {

    static final ValueWriter MAP_VALUE_WRITER = new MapValueWriter();
    private static final Pattern REQUIRED_QUOTING_PATTERN = Pattern.compile("^.*[^A-Za-z\\d_-].*$");

    public boolean canWrite(Object value) {
        return value instanceof Map;
    }

    public void write(Object value, WriterContext context) {
        Map from = (Map) value;

        if (hasPrimitiveValues(from, context)) {
            context.writeKey();
        }

        Iterator iterator = from.entrySet().iterator();

        Object fromValue;

        while (iterator.hasNext()) {
            Entry key = (Entry) iterator.next();

            fromValue = key.getKey();
            Object valueWriter = key.getValue();

            if (valueWriter != null) {
                ValueWriter valueWriter1 = ValueWriters.WRITERS.findWriterFor(valueWriter);

                if (valueWriter1.isPrimitiveType()) {
                    context.indent();
                    context.write(quoteKey(fromValue)).write(" = ");
                    valueWriter1.write(valueWriter, context);
                    context.write('\n');
                } else if (valueWriter1 == PrimitiveArrayValueWriter.PRIMITIVE_ARRAY_VALUE_WRITER) {
                    context.setArrayKey(fromValue.toString());
                    context.write(quoteKey(fromValue)).write(" = ");
                    valueWriter1.write(valueWriter, context);
                    context.write('\n');
                }
            }
        }

        iterator = from.keySet().iterator();

        while (iterator.hasNext()) {
            Object key1 = iterator.next();

            fromValue = from.get(key1);
            if (fromValue != null) {
                ValueWriter valueWriter2 = ValueWriters.WRITERS.findWriterFor(fromValue);

                if (valueWriter2 == this || valueWriter2 == ObjectValueWriter.OBJECT_VALUE_WRITER || valueWriter2 == TableArrayValueWriter.TABLE_ARRAY_VALUE_WRITER) {
                    valueWriter2.write(fromValue, context.pushTable(quoteKey(key1)));
                }
            }
        }

    }

    public boolean isPrimitiveType() {
        return false;
    }

    private static String quoteKey(Object key) {
        String stringKey = key.toString();
        Matcher matcher = MapValueWriter.REQUIRED_QUOTING_PATTERN.matcher(stringKey);

        if (matcher.matches()) {
            stringKey = "\"" + stringKey + "\"";
        }

        return stringKey;
    }

    private static boolean hasPrimitiveValues(Map values, WriterContext context) {
        Iterator iterator = values.keySet().iterator();

        ValueWriter valueWriter;

        do {
            Object fromValue;

            do {
                if (!iterator.hasNext()) {
                    return false;
                }

                Object key = iterator.next();

                fromValue = values.get(key);
            } while (fromValue == null);

            valueWriter = ValueWriters.WRITERS.findWriterFor(fromValue);
        } while (!valueWriter.isPrimitiveType() && valueWriter != PrimitiveArrayValueWriter.PRIMITIVE_ARRAY_VALUE_WRITER);

        return true;
    }
}
