package com.moandjiezana.toml;

import java.util.concurrent.atomic.AtomicInteger;

class BooleanValueReaderWriter implements ValueReader, ValueWriter {

    static final BooleanValueReaderWriter BOOLEAN_VALUE_READER_WRITER = new BooleanValueReaderWriter();

    public boolean canRead(String s) {
        return s.startsWith("true") || s.startsWith("false");
    }

    public Object read(String s, AtomicInteger index, Context context) {
        s = s.substring(index.get());
        Boolean b = s.startsWith("true") ? Boolean.TRUE : Boolean.FALSE;
        int endIndex = b == Boolean.TRUE ? 4 : 5;

        index.addAndGet(endIndex - 1);
        return b;
    }

    public boolean canWrite(Object value) {
        return Boolean.class.isInstance(value);
    }

    public void write(Object value, WriterContext context) {
        context.write(value.toString());
    }

    public boolean isPrimitiveType() {
        return true;
    }

    public String toString() {
        return "boolean";
    }
}
