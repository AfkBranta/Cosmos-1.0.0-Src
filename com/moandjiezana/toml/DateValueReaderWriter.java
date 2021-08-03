package com.moandjiezana.toml;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class DateValueReaderWriter implements ValueReader, ValueWriter {

    static final DateValueReaderWriter DATE_VALUE_READER_WRITER = new DateValueReaderWriter();
    static final DateValueReaderWriter DATE_PARSER_JDK_6 = new DateValueReaderWriter.DateConverterJdk6(null);
    private static final Pattern DATE_REGEX = Pattern.compile("(\\d{4}-[0-1][0-9]-[0-3][0-9]T[0-2][0-9]:[0-5][0-9]:[0-5][0-9])(\\.\\d*)?(Z|(?:[+\\-]\\d{2}:\\d{2}))(.*)");

    public boolean canRead(String s) {
        if (s.length() < 5) {
            return false;
        } else {
            for (int i = 0; i < 5; ++i) {
                char c = s.charAt(i);

                if (i < 4) {
                    if (!Character.isDigit(c)) {
                        return false;
                    }
                } else if (c != 45) {
                    return false;
                }
            }

            return true;
        }
    }

    public Object read(String original, AtomicInteger index, Context context) {
        StringBuilder sb = new StringBuilder();

        for (int s = index.get(); s < original.length(); s = index.incrementAndGet()) {
            char matcher = original.charAt(s);

            if (!Character.isDigit(matcher) && matcher != 45 && matcher != 43 && matcher != 58 && matcher != 46 && matcher != 84 && matcher != 90) {
                index.decrementAndGet();
                break;
            }

            sb.append(matcher);
        }

        String s1 = sb.toString();
        Matcher matcher1 = DateValueReaderWriter.DATE_REGEX.matcher(s1);

        if (!matcher1.matches()) {
            Results.Errors dateString1 = new Results.Errors();

            dateString1.invalidValue(context.identifier.getName(), s1, context.line.get());
            return dateString1;
        } else {
            String dateString = matcher1.group(1);
            String zone = matcher1.group(3);
            String fractionalSeconds = matcher1.group(2);
            String format = "yyyy-MM-dd\'T\'HH:mm:ss";

            if (fractionalSeconds != null && !fractionalSeconds.isEmpty()) {
                format = format + ".SSS";
                dateString = dateString + fractionalSeconds;
            }

            format = format + "Z";
            if ("Z".equals(zone)) {
                dateString = dateString + "+0000";
            } else if (zone.contains(":")) {
                dateString = dateString + zone.replace(":", "");
            }

            try {
                SimpleDateFormat e = new SimpleDateFormat(format);

                e.setLenient(false);
                return e.parse(dateString);
            } catch (Exception exception) {
                Results.Errors errors = new Results.Errors();

                errors.invalidValue(context.identifier.getName(), s1, context.line.get());
                return errors;
            }
        }
    }

    public boolean canWrite(Object value) {
        return value instanceof Date;
    }

    public void write(Object value, WriterContext context) {
        DateFormat formatter = this.getFormatter(context.getDatePolicy());

        context.write(formatter.format(value));
    }

    public boolean isPrimitiveType() {
        return true;
    }

    private DateFormat getFormatter(DatePolicy datePolicy) {
        boolean utc = "UTC".equals(datePolicy.getTimeZone().getID());
        String format;

        if (utc && datePolicy.isShowFractionalSeconds()) {
            format = "yyyy-MM-dd\'T\'HH:mm:ss.SSS\'Z\'";
        } else if (utc) {
            format = "yyyy-MM-dd\'T\'HH:mm:ss\'Z\'";
        } else if (datePolicy.isShowFractionalSeconds()) {
            format = this.getTimeZoneAndFractionalSecondsFormat();
        } else {
            format = this.getTimeZoneFormat();
        }

        SimpleDateFormat formatter = new SimpleDateFormat(format);

        formatter.setTimeZone(datePolicy.getTimeZone());
        return formatter;
    }

    String getTimeZoneFormat() {
        return "yyyy-MM-dd\'T\'HH:mm:ssXXX";
    }

    String getTimeZoneAndFractionalSecondsFormat() {
        return "yyyy-MM-dd\'T\'HH:mm:ss.SSSXXX";
    }

    private DateValueReaderWriter() {}

    public String toString() {
        return "datetime";
    }

    DateValueReaderWriter(Object x0) {
        this();
    }

    private static class DateConverterJdk6 extends DateValueReaderWriter {

        private DateConverterJdk6() {
            super(null);
        }

        public void write(Object value, WriterContext context) {
            DateFormat formatter = super.getFormatter(context.getDatePolicy());
            String date = formatter.format(value);

            if ("UTC".equals(context.getDatePolicy().getTimeZone().getID())) {
                context.write(date);
            } else {
                int insertionIndex = date.length() - 2;

                context.write(date.substring(0, insertionIndex)).write(':').write(date.substring(insertionIndex));
            }

        }

        String getTimeZoneAndFractionalSecondsFormat() {
            return "yyyy-MM-dd\'T\'HH:mm:ss.SSSZ";
        }

        String getTimeZoneFormat() {
            return "yyyy-MM-dd\'T\'HH:mm:ssZ";
        }

        DateConverterJdk6(Object x0) {
            this();
        }
    }
}
