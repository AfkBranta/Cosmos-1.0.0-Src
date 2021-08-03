package com.mojang.brigadier.tree;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.RedirectModifier;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

public class RootCommandNode extends CommandNode {

    public RootCommandNode() {
        super((Command) null, (c) -> {
            return true;
        }, (CommandNode) null, (s) -> {
            return Collections.singleton(s.getSource());
        }, false);
    }

    public String getName() {
        return "";
    }

    public String getUsageText() {
        return "";
    }

    public void parse(StringReader reader, CommandContextBuilder contextBuilder) throws CommandSyntaxException {}

    public CompletableFuture listSuggestions(CommandContext context, SuggestionsBuilder builder) {
        return Suggestions.empty();
    }

    public boolean isValidInput(String input) {
        return false;
    }

    public boolean equals(Object o) {
        return this == o ? true : (!(o instanceof RootCommandNode) ? false : super.equals(o));
    }

    public ArgumentBuilder createBuilder() {
        throw new IllegalStateException("Cannot convert root into a builder");
    }

    protected String getSortedKey() {
        return "";
    }

    public Collection getExamples() {
        return Collections.emptyList();
    }

    public String toString() {
        return "<root>";
    }
}
