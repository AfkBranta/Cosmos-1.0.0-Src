package com.moandjiezana.toml;

import java.util.concurrent.atomic.AtomicInteger;

class MultilineLiteralStringValueReader implements ValueReader {

    static final MultilineLiteralStringValueReader MULTILINE_LITERAL_STRING_VALUE_READER = new MultilineLiteralStringValueReader();

    public boolean canRead(String s) {
        return s.startsWith("\'\'\'");
    }

    public Object read(String s, AtomicInteger index, Context context) {
        AtomicInteger line = context.line;
        int startLine = line.get();
        int originalStartIndex = index.get();
        int startIndex = index.addAndGet(3);
        int endIndex = -1;

        if (s.charAt(startIndex) == 10) {
            startIndex = index.incrementAndGet();
            line.incrementAndGet();
        }

        for (int errors = startIndex; errors < s.length(); errors = index.incrementAndGet()) {
            char c = s.charAt(errors);

            if (c == 10) {
                line.incrementAndGet();
            }

            if (c == 39 && s.length() > errors + 2 && s.charAt(errors + 1) == 39 && s.charAt(errors + 2) == 39) {
                endIndex = errors;
                index.addAndGet(2);
                break;
            }
        }

        if (endIndex == -1) {
            Results.Errors errors1 = new Results.Errors();

            errors1.unterminated(context.identifier.getName(), s.substring(originalStartIndex), startLine);
            return errors1;
        } else {
            return s.substring(startIndex, endIndex);
        }
    }
}
