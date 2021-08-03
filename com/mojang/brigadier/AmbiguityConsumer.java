package com.mojang.brigadier;

import com.mojang.brigadier.tree.CommandNode;
import java.util.Collection;

@FunctionalInterface
public interface AmbiguityConsumer {

    void ambiguous(CommandNode commandnode, CommandNode commandnode1, CommandNode commandnode2, Collection collection);
}
