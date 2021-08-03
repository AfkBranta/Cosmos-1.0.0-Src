package com.moandjiezana.toml;

interface ValueWriter {

    boolean canWrite(Object object);

    void write(Object object, WriterContext writercontext);

    boolean isPrimitiveType();
}
