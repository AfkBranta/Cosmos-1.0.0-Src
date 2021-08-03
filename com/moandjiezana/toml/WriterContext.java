package com.moandjiezana.toml;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;

class WriterContext {

    private String arrayKey;
    private boolean isArrayOfTable;
    private boolean empty;
    private final String key;
    private final String currentTableIndent;
    private final String currentFieldIndent;
    private final Writer output;
    private final IndentationPolicy indentationPolicy;
    private final DatePolicy datePolicy;

    WriterContext(IndentationPolicy indentationPolicy, DatePolicy datePolicy, Writer output) {
        this("", "", output, indentationPolicy, datePolicy);
    }

    WriterContext pushTable(String newKey) {
        String newIndent = "";

        if (!this.key.isEmpty()) {
            newIndent = this.growIndent(this.indentationPolicy);
        }

        String fullKey = this.key.isEmpty() ? newKey : this.key + "." + newKey;
        WriterContext subContext = new WriterContext(fullKey, newIndent, this.output, this.indentationPolicy, this.datePolicy);

        if (!this.empty) {
            subContext.empty = false;
        }

        return subContext;
    }

    WriterContext pushTableFromArray() {
        WriterContext subContext = new WriterContext(this.key, this.currentTableIndent, this.output, this.indentationPolicy, this.datePolicy);

        if (!this.empty) {
            subContext.empty = false;
        }

        subContext.setIsArrayOfTable(true);
        return subContext;
    }

    WriterContext write(String s) {
        try {
            this.output.write(s);
            if (this.empty && !s.isEmpty()) {
                this.empty = false;
            }

            return this;
        } catch (IOException ioexception) {
            throw new RuntimeException(ioexception);
        }
    }

    void write(char[] chars) {
        char[] achar = chars;
        int i = chars.length;

        for (int j = 0; j < i; ++j) {
            char c = achar[j];

            this.write(c);
        }

    }

    WriterContext write(char c) {
        try {
            this.output.write(c);
            this.empty = false;
            return this;
        } catch (IOException ioexception) {
            throw new RuntimeException(ioexception);
        }
    }

    void writeKey() {
        if (!this.key.isEmpty()) {
            if (!this.empty) {
                this.write('\n');
            }

            this.write(this.currentTableIndent);
            if (this.isArrayOfTable) {
                this.write("[[").write(this.key).write("]]\n");
            } else {
                this.write('[').write(this.key).write("]\n");
            }

        }
    }

    void writeArrayDelimiterPadding() {
        for (int i = 0; i < this.indentationPolicy.getArrayDelimiterPadding(); ++i) {
            this.write(' ');
        }

    }

    void indent() {
        if (!this.key.isEmpty()) {
            this.write(this.currentFieldIndent);
        }

    }

    DatePolicy getDatePolicy() {
        return this.datePolicy;
    }

    WriterContext setIsArrayOfTable(boolean isArrayOfTable) {
        this.isArrayOfTable = isArrayOfTable;
        return this;
    }

    WriterContext setArrayKey(String arrayKey) {
        this.arrayKey = arrayKey;
        return this;
    }

    String getContextPath() {
        return this.key.isEmpty() ? this.arrayKey : this.key + "." + this.arrayKey;
    }

    private String growIndent(IndentationPolicy indentationPolicy) {
        return this.currentTableIndent + this.fillStringWithSpaces(indentationPolicy.getTableIndent());
    }

    private String fillStringWithSpaces(int count) {
        char[] chars = new char[count];

        Arrays.fill(chars, ' ');
        return new String(chars);
    }

    private WriterContext(String key, String tableIndent, Writer output, IndentationPolicy indentationPolicy, DatePolicy datePolicy) {
        this.arrayKey = null;
        this.isArrayOfTable = false;
        this.empty = true;
        this.key = key;
        this.output = output;
        this.indentationPolicy = indentationPolicy;
        this.currentTableIndent = tableIndent;
        this.datePolicy = datePolicy;
        this.currentFieldIndent = tableIndent + this.fillStringWithSpaces(this.indentationPolicy.getKeyValueIndent());
    }
}
