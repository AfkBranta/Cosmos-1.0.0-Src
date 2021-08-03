package com.mojang.brigadier.exceptions;

import com.mojang.brigadier.LiteralMessage;
import java.util.function.Function;

public class BuiltInExceptions implements BuiltInExceptionProvider {

    private static final Dynamic2CommandExceptionType DOUBLE_TOO_SMALL = new Dynamic2CommandExceptionType((found, min) -> {
        return new LiteralMessage("Double must not be less than " + min + ", found " + found);
    });
    private static final Dynamic2CommandExceptionType DOUBLE_TOO_BIG = new Dynamic2CommandExceptionType((found, max) -> {
        return new LiteralMessage("Double must not be more than " + max + ", found " + found);
    });
    private static final Dynamic2CommandExceptionType FLOAT_TOO_SMALL = new Dynamic2CommandExceptionType((found, min) -> {
        return new LiteralMessage("Float must not be less than " + min + ", found " + found);
    });
    private static final Dynamic2CommandExceptionType FLOAT_TOO_BIG = new Dynamic2CommandExceptionType((found, max) -> {
        return new LiteralMessage("Float must not be more than " + max + ", found " + found);
    });
    private static final Dynamic2CommandExceptionType INTEGER_TOO_SMALL = new Dynamic2CommandExceptionType((found, min) -> {
        return new LiteralMessage("Integer must not be less than " + min + ", found " + found);
    });
    private static final Dynamic2CommandExceptionType INTEGER_TOO_BIG = new Dynamic2CommandExceptionType((found, max) -> {
        return new LiteralMessage("Integer must not be more than " + max + ", found " + found);
    });
    private static final Dynamic2CommandExceptionType LONG_TOO_SMALL = new Dynamic2CommandExceptionType((found, min) -> {
        return new LiteralMessage("Long must not be less than " + min + ", found " + found);
    });
    private static final Dynamic2CommandExceptionType LONG_TOO_BIG = new Dynamic2CommandExceptionType((found, max) -> {
        return new LiteralMessage("Long must not be more than " + max + ", found " + found);
    });
    private static final DynamicCommandExceptionType LITERAL_INCORRECT = new DynamicCommandExceptionType((expected) -> {
        return new LiteralMessage("Expected literal " + expected);
    });
    private static final SimpleCommandExceptionType READER_EXPECTED_START_OF_QUOTE = new SimpleCommandExceptionType(new LiteralMessage("Expected quote to start a string"));
    private static final SimpleCommandExceptionType READER_EXPECTED_END_OF_QUOTE = new SimpleCommandExceptionType(new LiteralMessage("Unclosed quoted string"));
    private static final DynamicCommandExceptionType READER_INVALID_ESCAPE = new DynamicCommandExceptionType((character) -> {
        return new LiteralMessage("Invalid escape sequence \'" + character + "\' in quoted string");
    });
    private static final DynamicCommandExceptionType READER_INVALID_BOOL = new DynamicCommandExceptionType((value) -> {
        return new LiteralMessage("Invalid bool, expected true or false but found \'" + value + "\'");
    });
    private static final DynamicCommandExceptionType READER_INVALID_INT = new DynamicCommandExceptionType((value) -> {
        return new LiteralMessage("Invalid integer \'" + value + "\'");
    });
    private static final SimpleCommandExceptionType READER_EXPECTED_INT = new SimpleCommandExceptionType(new LiteralMessage("Expected integer"));
    private static final DynamicCommandExceptionType READER_INVALID_LONG = new DynamicCommandExceptionType((value) -> {
        return new LiteralMessage("Invalid long \'" + value + "\'");
    });
    private static final SimpleCommandExceptionType READER_EXPECTED_LONG = new SimpleCommandExceptionType(new LiteralMessage("Expected long"));
    private static final DynamicCommandExceptionType READER_INVALID_DOUBLE = new DynamicCommandExceptionType((value) -> {
        return new LiteralMessage("Invalid double \'" + value + "\'");
    });
    private static final SimpleCommandExceptionType READER_EXPECTED_DOUBLE = new SimpleCommandExceptionType(new LiteralMessage("Expected double"));
    private static final DynamicCommandExceptionType READER_INVALID_FLOAT = new DynamicCommandExceptionType((value) -> {
        return new LiteralMessage("Invalid float \'" + value + "\'");
    });
    private static final SimpleCommandExceptionType READER_EXPECTED_FLOAT = new SimpleCommandExceptionType(new LiteralMessage("Expected float"));
    private static final SimpleCommandExceptionType READER_EXPECTED_BOOL = new SimpleCommandExceptionType(new LiteralMessage("Expected bool"));
    private static final DynamicCommandExceptionType READER_EXPECTED_SYMBOL = new DynamicCommandExceptionType((symbol) -> {
        return new LiteralMessage("Expected \'" + symbol + "\'");
    });
    private static final SimpleCommandExceptionType DISPATCHER_UNKNOWN_COMMAND = new SimpleCommandExceptionType(new LiteralMessage("Unknown command"));
    private static final SimpleCommandExceptionType DISPATCHER_UNKNOWN_ARGUMENT = new SimpleCommandExceptionType(new LiteralMessage("Incorrect argument for command"));
    private static final SimpleCommandExceptionType DISPATCHER_EXPECTED_ARGUMENT_SEPARATOR = new SimpleCommandExceptionType(new LiteralMessage("Expected whitespace to end one argument, but found trailing data"));
    private static final DynamicCommandExceptionType DISPATCHER_PARSE_EXCEPTION = new DynamicCommandExceptionType((message) -> {
        return new LiteralMessage("Could not parse command: " + message);
    });

    public Dynamic2CommandExceptionType doubleTooLow() {
        return BuiltInExceptions.DOUBLE_TOO_SMALL;
    }

    public Dynamic2CommandExceptionType doubleTooHigh() {
        return BuiltInExceptions.DOUBLE_TOO_BIG;
    }

    public Dynamic2CommandExceptionType floatTooLow() {
        return BuiltInExceptions.FLOAT_TOO_SMALL;
    }

    public Dynamic2CommandExceptionType floatTooHigh() {
        return BuiltInExceptions.FLOAT_TOO_BIG;
    }

    public Dynamic2CommandExceptionType integerTooLow() {
        return BuiltInExceptions.INTEGER_TOO_SMALL;
    }

    public Dynamic2CommandExceptionType integerTooHigh() {
        return BuiltInExceptions.INTEGER_TOO_BIG;
    }

    public Dynamic2CommandExceptionType longTooLow() {
        return BuiltInExceptions.LONG_TOO_SMALL;
    }

    public Dynamic2CommandExceptionType longTooHigh() {
        return BuiltInExceptions.LONG_TOO_BIG;
    }

    public DynamicCommandExceptionType literalIncorrect() {
        return BuiltInExceptions.LITERAL_INCORRECT;
    }

    public SimpleCommandExceptionType readerExpectedStartOfQuote() {
        return BuiltInExceptions.READER_EXPECTED_START_OF_QUOTE;
    }

    public SimpleCommandExceptionType readerExpectedEndOfQuote() {
        return BuiltInExceptions.READER_EXPECTED_END_OF_QUOTE;
    }

    public DynamicCommandExceptionType readerInvalidEscape() {
        return BuiltInExceptions.READER_INVALID_ESCAPE;
    }

    public DynamicCommandExceptionType readerInvalidBool() {
        return BuiltInExceptions.READER_INVALID_BOOL;
    }

    public DynamicCommandExceptionType readerInvalidInt() {
        return BuiltInExceptions.READER_INVALID_INT;
    }

    public SimpleCommandExceptionType readerExpectedInt() {
        return BuiltInExceptions.READER_EXPECTED_INT;
    }

    public DynamicCommandExceptionType readerInvalidLong() {
        return BuiltInExceptions.READER_INVALID_LONG;
    }

    public SimpleCommandExceptionType readerExpectedLong() {
        return BuiltInExceptions.READER_EXPECTED_LONG;
    }

    public DynamicCommandExceptionType readerInvalidDouble() {
        return BuiltInExceptions.READER_INVALID_DOUBLE;
    }

    public SimpleCommandExceptionType readerExpectedDouble() {
        return BuiltInExceptions.READER_EXPECTED_DOUBLE;
    }

    public DynamicCommandExceptionType readerInvalidFloat() {
        return BuiltInExceptions.READER_INVALID_FLOAT;
    }

    public SimpleCommandExceptionType readerExpectedFloat() {
        return BuiltInExceptions.READER_EXPECTED_FLOAT;
    }

    public SimpleCommandExceptionType readerExpectedBool() {
        return BuiltInExceptions.READER_EXPECTED_BOOL;
    }

    public DynamicCommandExceptionType readerExpectedSymbol() {
        return BuiltInExceptions.READER_EXPECTED_SYMBOL;
    }

    public SimpleCommandExceptionType dispatcherUnknownCommand() {
        return BuiltInExceptions.DISPATCHER_UNKNOWN_COMMAND;
    }

    public SimpleCommandExceptionType dispatcherUnknownArgument() {
        return BuiltInExceptions.DISPATCHER_UNKNOWN_ARGUMENT;
    }

    public SimpleCommandExceptionType dispatcherExpectedArgumentSeparator() {
        return BuiltInExceptions.DISPATCHER_EXPECTED_ARGUMENT_SEPARATOR;
    }

    public DynamicCommandExceptionType dispatcherParseException() {
        return BuiltInExceptions.DISPATCHER_PARSE_EXCEPTION;
    }
}
