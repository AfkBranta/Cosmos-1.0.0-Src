package com.moandjiezana.toml;

import java.util.Collection;
import java.util.Iterator;

class PrimitiveArrayValueWriter extends ArrayValueWriter {

    static final ValueWriter PRIMITIVE_ARRAY_VALUE_WRITER = new PrimitiveArrayValueWriter();

    public boolean canWrite(Object value) {
        return isArrayish(value) && isArrayOfPrimitive(value);
    }

    public void write(Object o, WriterContext context) {
        Collection values = this.normalize(o);

        context.write('[');
        context.writeArrayDelimiterPadding();
        boolean first = true;
        ValueWriter firstWriter = null;

        Object value;

        for (Iterator iterator = values.iterator(); iterator.hasNext(); ValueWriters.WRITERS.findWriterFor(value).write(value, context)) {
            value = iterator.next();
            if (first) {
                firstWriter = ValueWriters.WRITERS.findWriterFor(value);
                first = false;
            } else {
                ValueWriter writer = ValueWriters.WRITERS.findWriterFor(value);

                if (writer != firstWriter) {
                    throw new IllegalStateException(context.getContextPath() + ": cannot write a heterogeneous array; first element was of type " + firstWriter + " but found " + writer);
                }

                context.write(", ");
            }
        }

        context.writeArrayDelimiterPadding();
        context.write(']');
    }

    public String toString() {
        return "primitive-array";
    }
}
