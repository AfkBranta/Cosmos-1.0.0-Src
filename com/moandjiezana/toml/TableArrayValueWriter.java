package com.moandjiezana.toml;

import java.util.Collection;
import java.util.Iterator;

class TableArrayValueWriter extends ArrayValueWriter {

    static final ValueWriter TABLE_ARRAY_VALUE_WRITER = new TableArrayValueWriter();

    public boolean canWrite(Object value) {
        return isArrayish(value) && !isArrayOfPrimitive(value);
    }

    public void write(Object from, WriterContext context) {
        Collection values = this.normalize(from);
        WriterContext subContext = context.pushTableFromArray();
        Iterator iterator = values.iterator();

        while (iterator.hasNext()) {
            Object value = iterator.next();

            ValueWriters.WRITERS.findWriterFor(value).write(value, subContext);
        }

    }

    public String toString() {
        return "table-array";
    }
}
