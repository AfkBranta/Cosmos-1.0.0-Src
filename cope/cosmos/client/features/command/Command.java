package cope.cosmos.client.features.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import cope.cosmos.client.features.Feature;
import cope.cosmos.util.Wrapper;
import java.util.Iterator;

public class Command extends Feature implements Wrapper {

    private final LiteralArgumentBuilder command;

    public Command(String name, String description, LiteralArgumentBuilder command) {
        super(name, description);
        this.command = command;
    }

    public LiteralArgumentBuilder getCommand() {
        return this.command;
    }

    public static LiteralArgumentBuilder redirectBuilder(String alias, LiteralCommandNode destination) {
        LiteralArgumentBuilder literalArgumentBuilder = (LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) LiteralArgumentBuilder.literal(alias.toLowerCase()).requires(destination.getRequirement())).forward(destination.getRedirect(), destination.getRedirectModifier(), destination.isFork())).executes(destination.getCommand());
        Iterator iterator = destination.getChildren().iterator();

        while (iterator.hasNext()) {
            CommandNode child = (CommandNode) iterator.next();

            literalArgumentBuilder.then(child);
        }

        return literalArgumentBuilder;
    }
}
