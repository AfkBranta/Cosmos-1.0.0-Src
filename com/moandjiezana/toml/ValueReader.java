package com.moandjiezana.toml;

import java.util.concurrent.atomic.AtomicInteger;

interface ValueReader {

    boolean canRead(String s);

    Object read(String s, AtomicInteger atomicinteger, Context context);
}
