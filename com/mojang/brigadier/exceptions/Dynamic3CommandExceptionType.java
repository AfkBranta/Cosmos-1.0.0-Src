package com.mojang.brigadier.exceptions;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;

public class Dynamic3CommandExceptionType implements CommandExceptionType {

    private final Dynamic3CommandExceptionType.Function function;

    public Dynamic3CommandExceptionType(Dynamic3CommandExceptionType.Function function) {
        this.function = function;
    }

    public CommandSyntaxException create(Object a, Object b, Object c) {
        return new CommandSyntaxException(this, this.function.apply(a, b, c));
    }

    public CommandSyntaxException createWithContext(ImmutableStringReader reader, Object a, Object b, Object c) {
        return new CommandSyntaxException(this, this.function.apply(a, b, c), reader.getString(), reader.getCursor());
    }

    public interface Function {

        Message apply(Object object, Object object1, Object object2);
    }
}
