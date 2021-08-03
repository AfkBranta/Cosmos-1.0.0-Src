package com.moandjiezana.toml;

import java.util.concurrent.atomic.AtomicInteger;

class NumberValueReaderWriter implements ValueReader, ValueWriter {

    static final NumberValueReaderWriter NUMBER_VALUE_READER_WRITER = new NumberValueReaderWriter();

    public boolean canRead(String s) {
        char firstChar = s.charAt(0);

        return firstChar == 43 || firstChar == 45 || Character.isDigit(firstChar);
    }

    public Object read(String s, AtomicInteger index, Context context) {
        boolean signable = true;
        boolean dottable = false;
        boolean exponentable = false;
        boolean terminatable = false;
        boolean underscorable = false;
        String type = "";
        StringBuilder sb = new StringBuilder();

        for (int errors = index.get(); errors < s.length(); errors = index.incrementAndGet()) {
            char c = s.charAt(errors);
            boolean notLastChar = s.length() > errors + 1;

            if (Character.isDigit(c)) {
                sb.append(c);
                signable = false;
                terminatable = true;
                if (type.isEmpty()) {
                    type = "integer";
                    dottable = true;
                }

                underscorable = notLastChar;
                exponentable = !type.equals("exponent");
            } else if ((c == 43 || c == 45) && signable && notLastChar) {
                signable = false;
                terminatable = false;
                if (c == 45) {
                    sb.append('-');
                }
            } else if (c == 46 && dottable && notLastChar) {
                sb.append('.');
                type = "float";
                terminatable = false;
                dottable = false;
                exponentable = false;
                underscorable = false;
            } else if ((c == 69 || c == 101) && exponentable && notLastChar) {
                sb.append('E');
                type = "exponent";
                terminatable = false;
                signable = true;
                dottable = false;
                exponentable = false;
                underscorable = false;
            } else {
                if (c != 95 || !underscorable || !notLastChar || !Character.isDigit(s.charAt(errors + 1))) {
                    if (!terminatable) {
                        type = "";
                    }

                    index.decrementAndGet();
                    break;
                }

                underscorable = false;
            }
        }

        if (type.equals("integer")) {
            return Long.valueOf(sb.toString());
        } else if (type.equals("float")) {
            return Double.valueOf(sb.toString());
        } else if (type.equals("exponent")) {
            String[] errors2 = sb.toString().split("E");

            return Double.valueOf(Double.parseDouble(errors2[0]) * Math.pow(10.0D, Double.parseDouble(errors2[1])));
        } else {
            Results.Errors errors1 = new Results.Errors();

            errors1.invalidValue(context.identifier.getName(), sb.toString(), context.line.get());
            return errors1;
        }
    }

    public boolean canWrite(Object value) {
        return Number.class.isInstance(value);
    }

    public void write(Object value, WriterContext context) {
        context.write(value.toString());
    }

    public boolean isPrimitiveType() {
        return true;
    }

    public String toString() {
        return "number";
    }
}
