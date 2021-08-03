package cope.cosmos.client.features.command.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.realmsclient.gui.ChatFormatting;
import cope.cosmos.client.Cosmos;
import cope.cosmos.client.features.command.Command;
import cope.cosmos.client.manager.managers.SocialManager;
import cope.cosmos.util.client.ChatUtil;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketChatMessage;

public class Friend extends Command {

    public Friend() {
        super("Friend", "Updates your friends list", (LiteralArgumentBuilder) ((LiteralArgumentBuilder) LiteralArgumentBuilder.literal("friend").then(((RequiredArgumentBuilder) RequiredArgumentBuilder.argument("action", StringArgumentType.string()).suggests((context, builder) -> {
            return suggestActions(builder);
        }).then(RequiredArgumentBuilder.argument("name", StringArgumentType.string()).suggests((context, builder) -> {
            return suggestNames(builder);
        }).executes((context) -> {
            if (StringArgumentType.getString(context, "action").equals("add")) {
                Cosmos.INSTANCE.getSocialManager().addSocial(StringArgumentType.getString(context, "name"), SocialManager.Relationship.FRIEND);
                ChatUtil.sendHoverableMessage(ChatFormatting.GREEN + "Command dispatched successfully!", "Added friend with name " + StringArgumentType.getString(context, "name"));
                Friend.mc.player.connection.sendPacket(new CPacketChatMessage("/w " + StringArgumentType.getString(context, "name") + " I just added you as a friend on Cosmos!"));
            } else if (StringArgumentType.getString(context, "action").equals("remove")) {
                Cosmos.INSTANCE.getSocialManager().removeSocial(StringArgumentType.getString(context, "name"));
                ChatUtil.sendHoverableMessage(ChatFormatting.GREEN + "Command dispatched successfully!", "Removed friend with name " + StringArgumentType.getString(context, "name"));
            }

            return 1;
        }))).executes((context) -> {
            ChatUtil.sendHoverableMessage(ChatFormatting.RED + "An error occured!", "Please enter the name of the person to friend!");
            return 1;
        }))).executes((context) -> {
            ChatUtil.sendHoverableMessage(ChatFormatting.RED + "An error occured!", "Please enter the correct action, was expecting add or remove!");
            return 1;
        }));
    }

    private static CompletableFuture suggestNames(SuggestionsBuilder suggestionsBuilder) {
        Iterator iterator = Friend.mc.world.playerEntities.iterator();

        while (iterator.hasNext()) {
            EntityPlayer entityPlayer = (EntityPlayer) iterator.next();

            suggestionsBuilder.suggest(entityPlayer.getName());
        }

        return suggestionsBuilder.buildFuture();
    }

    private static CompletableFuture suggestActions(SuggestionsBuilder suggestionsBuilder) {
        suggestionsBuilder.suggest("add");
        suggestionsBuilder.suggest("remove");
        return suggestionsBuilder.buildFuture();
    }
}
