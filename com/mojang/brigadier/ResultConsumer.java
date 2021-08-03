package com.mojang.brigadier;

import com.mojang.brigadier.context.CommandContext;

@FunctionalInterface
public interface ResultConsumer {

    void onCommandComplete(CommandContext commandcontext, boolean flag, int i);
}
