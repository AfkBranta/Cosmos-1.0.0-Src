package com.moandjiezana.toml;

import java.util.concurrent.atomic.AtomicInteger;

class IdentifierConverter {

    static final IdentifierConverter IDENTIFIER_CONVERTER = new IdentifierConverter();

    Identifier convert(String s, AtomicInteger index, Context context) {
        boolean quoted = false;
        StringBuilder name = new StringBuilder();
        boolean terminated = false;
        boolean isKey = s.charAt(index.get()) != 91;
        boolean isTableArray = !isKey && s.length() > index.get() + 1 && s.charAt(index.get() + 1) == 91;
        boolean inComment = false;

        for (int i = index.get(); i < s.length(); i = index.incrementAndGet()) {
            char c = s.charAt(i);

            if (Keys.isQuote(c) && (i == 0 || s.charAt(i - 1) != 92)) {
                quoted = !quoted;
                name.append(c);
            } else {
                if (c == 10) {
                    index.decrementAndGet();
                    break;
                }

                if (quoted) {
                    name.append(c);
                } else {
                    if (c == 61 && isKey) {
                        terminated = true;
                        break;
                    }

                    if (c == 93 && !isKey) {
                        if (!isTableArray || s.length() > index.get() + 1 && s.charAt(index.get() + 1) == 93) {
                            terminated = true;
                            name.append(']');
                            if (isTableArray) {
                                name.append(']');
                            }
                        }
                    } else if (terminated && c == 35) {
                        inComment = true;
                    } else {
                        if (terminated && !Character.isWhitespace(c) && !inComment) {
                            terminated = false;
                            break;
                        }

                        if (!terminated) {
                            name.append(c);
                        }
                    }
                }
            }
        }

        if (!terminated) {
            if (isKey) {
                context.errors.unterminatedKey(name.toString(), context.line.get());
            } else {
                context.errors.invalidKey(name.toString(), context.line.get());
            }

            return Identifier.INVALID;
        } else {
            return Identifier.from(name.toString(), context);
        }
    }
}
