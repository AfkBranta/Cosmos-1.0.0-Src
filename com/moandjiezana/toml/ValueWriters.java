package com.moandjiezana.toml;

class ValueWriters {

    static final ValueWriters WRITERS = new ValueWriters();
    private static final ValueWriter[] VALUE_WRITERS = new ValueWriter[] { StringValueReaderWriter.STRING_VALUE_READER_WRITER, NumberValueReaderWriter.NUMBER_VALUE_READER_WRITER, BooleanValueReaderWriter.BOOLEAN_VALUE_READER_WRITER, getPlatformSpecificDateConverter(), MapValueWriter.MAP_VALUE_WRITER, PrimitiveArrayValueWriter.PRIMITIVE_ARRAY_VALUE_WRITER, TableArrayValueWriter.TABLE_ARRAY_VALUE_WRITER};

    ValueWriter findWriterFor(Object value) {
        ValueWriter[] avaluewriter = ValueWriters.VALUE_WRITERS;
        int i = avaluewriter.length;

        for (int j = 0; j < i; ++j) {
            ValueWriter valueWriter = avaluewriter[j];

            if (valueWriter.canWrite(value)) {
                return valueWriter;
            }
        }

        return ObjectValueWriter.OBJECT_VALUE_WRITER;
    }

    private static DateValueReaderWriter getPlatformSpecificDateConverter() {
        String specificationVersion = Runtime.class.getPackage().getSpecificationVersion();

        return specificationVersion != null && specificationVersion.startsWith("1.6") ? DateValueReaderWriter.DATE_PARSER_JDK_6 : DateValueReaderWriter.DATE_VALUE_READER_WRITER;
    }
}
