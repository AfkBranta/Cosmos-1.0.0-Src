package com.moandjiezana.toml;

import java.net.URI;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class StringValueReaderWriter implements ValueReader, ValueWriter {

    static final StringValueReaderWriter STRING_VALUE_READER_WRITER = new StringValueReaderWriter();
    private static final Pattern UNICODE_REGEX = Pattern.compile("\\\\[uU](.{4})");
    private static final String[] specialCharacterEscapes = new String[93];

    public boolean canRead(String s) {
        return s.startsWith("\"");
    }

    public Object read(String s, AtomicInteger index, Context context) {
        int startIndex = index.incrementAndGet();
        int endIndex = -1;

        for (int raw = index.get(); raw < s.length(); raw = index.incrementAndGet()) {
            char errors = s.charAt(raw);

            if (errors == 34 && s.charAt(raw - 1) != 92) {
                endIndex = raw;
                break;
            }
        }

        if (endIndex == -1) {
            Results.Errors raw2 = new Results.Errors();

            raw2.unterminated(context.identifier.getName(), s.substring(startIndex - 1), context.line.get());
            return raw2;
        } else {
            String raw1 = s.substring(startIndex, endIndex);

            s = this.replaceUnicodeCharacters(raw1);
            s = this.replaceSpecialCharacters(s);
            if (s == null) {
                Results.Errors errors1 = new Results.Errors();

                errors1.invalidValue(context.identifier.getName(), raw1, context.line.get());
                return errors1;
            } else {
                return s;
            }
        }
    }

    String replaceUnicodeCharacters(String value) {
        for (Matcher unicodeMatcher = StringValueReaderWriter.UNICODE_REGEX.matcher(value); unicodeMatcher.find(); value = value.replace(unicodeMatcher.group(), new String(Character.toChars(Integer.parseInt(unicodeMatcher.group(1), 16))))) {
            ;
        }

        return value;
    }

    String replaceSpecialCharacters(String s) {
        for (int i = 0; i < s.length() - 1; ++i) {
            char ch = s.charAt(i);
            char next = s.charAt(i + 1);

            if (ch == 92 && next == 92) {
                ++i;
            } else if (ch == 92 && next != 98 && next != 102 && next != 110 && next != 116 && next != 114 && next != 34 && next != 92) {
                return null;
            }
        }

        return s.replace("\\n", "\n").replace("\\\"", "\"").replace("\\t", "\t").replace("\\r", "\r").replace("\\\\", "\\").replace("\\/", "/").replace("\\b", "\b").replace("\\f", "\f");
    }

    public boolean canWrite(Object value) {
        return value instanceof String || value instanceof Character || value instanceof URL || value instanceof URI || value instanceof Enum;
    }

    public void write(Object value, WriterContext context) {
        context.write('\"');
        this.escapeUnicode(value.toString(), context);
        context.write('\"');
    }

    public boolean isPrimitiveType() {
        return true;
    }

    private void escapeUnicode(String in, WriterContext context) {
        for (int i = 0; i < in.length(); ++i) {
            int codePoint = in.codePointAt(i);

            if (codePoint < StringValueReaderWriter.specialCharacterEscapes.length && StringValueReaderWriter.specialCharacterEscapes[codePoint] != null) {
                context.write(StringValueReaderWriter.specialCharacterEscapes[codePoint]);
            } else {
                context.write(in.charAt(i));
            }
        }

    }

    public String toString() {
        return "string";
    }

    static {
        StringValueReaderWriter.specialCharacterEscapes[8] = "\\b";
        StringValueReaderWriter.specialCharacterEscapes[9] = "\\t";
        StringValueReaderWriter.specialCharacterEscapes[10] = "\\n";
        StringValueReaderWriter.specialCharacterEscapes[12] = "\\f";
        StringValueReaderWriter.specialCharacterEscapes[13] = "\\r";
        StringValueReaderWriter.specialCharacterEscapes[34] = "\\\"";
        StringValueReaderWriter.specialCharacterEscapes[92] = "\\\\";
    }
}
