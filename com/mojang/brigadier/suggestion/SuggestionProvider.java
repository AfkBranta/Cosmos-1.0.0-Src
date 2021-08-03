package com.mojang.brigadier.suggestion;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.concurrent.CompletableFuture;

@FunctionalInterface
public interface SuggestionProvider {

    CompletableFuture getSuggestions(CommandContext commandcontext, SuggestionsBuilder suggestionsbuilder) throws CommandSyntaxException;
}
