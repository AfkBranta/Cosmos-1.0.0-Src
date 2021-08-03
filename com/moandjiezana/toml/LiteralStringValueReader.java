package com.moandjiezana.toml;

import java.util.concurrent.atomic.AtomicInteger;

class LiteralStringValueReader implements ValueReader {

    static final LiteralStringValueReader LITERAL_STRING_VALUE_READER = new LiteralStringValueReader();

    public boolean canRead(String s) {
        return s.startsWith("\'");
    }

    public Object read(String s, AtomicInteger index, Context context) {
        int startLine = context.line.get();
        boolean terminated = false;
        int startIndex = index.incrementAndGet();

        for (int substring = index.get(); substring < s.length(); substring = index.incrementAndGet()) {
            char c = s.charAt(substring);

            if (c == 39) {
                terminated = true;
                break;
            }
        }

        if (!terminated) {
            Results.Errors substring2 = new Results.Errors();

            substring2.unterminated(context.identifier.getName(), s.substring(startIndex), startLine);
            return substring2;
        } else {
            String substring1 = s.substring(startIndex, index.get());

            return substring1;
        }
    }
}
