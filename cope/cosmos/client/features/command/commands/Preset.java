package cope.cosmos.client.features.command.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.realmsclient.gui.ChatFormatting;
import cope.cosmos.client.Cosmos;
import cope.cosmos.client.features.command.Command;
import cope.cosmos.util.client.ChatUtil;

public class Preset extends Command {

    public Preset() {
        super("Preset", "Creates or Updates a preset", (LiteralArgumentBuilder) ((LiteralArgumentBuilder) LiteralArgumentBuilder.literal("preset").then(((RequiredArgumentBuilder) RequiredArgumentBuilder.argument("action", StringArgumentType.string()).then(RequiredArgumentBuilder.argument("name", StringArgumentType.string()).executes((context) -> {
            String s = StringArgumentType.getString(context, "action");
            byte b0 = -1;

            switch (s.hashCode()) {
            case -934610812:
                if (s.equals("remove")) {
                    b0 = 2;
                }
                break;

            case 3327206:
                if (s.equals("load")) {
                    b0 = 1;
                }
                break;

            case 3522941:
                if (s.equals("save")) {
                    b0 = 0;
                }
            }

            switch (b0) {
            case 0:
                String previousPreset = Cosmos.INSTANCE.getPresetManager().getCurrentPreset();

                Cosmos.INSTANCE.getPresetManager().createPreset(StringArgumentType.getString(context, "name"));
                Cosmos.INSTANCE.getPresetManager().setPreset(StringArgumentType.getString(context, "name"));
                Cosmos.INSTANCE.getPresetManager().writeDirectories();
                Cosmos.INSTANCE.getPresetManager().save();
                Cosmos.INSTANCE.getPresetManager().setPreset(previousPreset);
                ChatUtil.sendHoverableMessage(ChatFormatting.GREEN + "Command dispatched successfully!", "Saved current prefix");

            case 1:
                Cosmos.INSTANCE.getPresetManager().setPreset(StringArgumentType.getString(context, "name"));
                Cosmos.INSTANCE.getPresetManager().load();
                ChatUtil.sendHoverableMessage(ChatFormatting.GREEN + "Command dispatched successfully!", "Loaded current preset");
                break;

            case 2:
                Cosmos.INSTANCE.getPresetManager().removePreset(StringArgumentType.getString(context, "name"));
                ChatUtil.sendHoverableMessage(ChatFormatting.GREEN + "Command dispatched successfully!", "Removed preset with name " + StringArgumentType.getString(context, "name"));
            }

            return 1;
        }))).executes((context) -> {
            ChatUtil.sendHoverableMessage(ChatFormatting.RED + "An error occured!", "Please enter the name of the preset!");
            return 1;
        }))).executes((context) -> {
            ChatUtil.sendHoverableMessage(ChatFormatting.RED + "An error occured!", "Please enter the correct action, was expecting create, save, remove, load, or set!");
            return 1;
        }));
    }
}
