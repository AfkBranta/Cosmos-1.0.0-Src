package cope.cosmos.client.manager.managers;

import cope.cosmos.client.Cosmos;
import cope.cosmos.client.features.command.Command;
import cope.cosmos.client.features.command.commands.Drawn;
import cope.cosmos.client.features.command.commands.Friend;
import cope.cosmos.client.features.command.commands.Preset;
import cope.cosmos.client.manager.Manager;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CommandManager extends Manager {

    private static final List commands = Arrays.asList(new Command[] { new Friend(), new Preset(), new Drawn()});

    public CommandManager() {
        super("CommandManager", "Manages client commands", 2);
    }

    public void initialize(Manager manager) {
        registerCommands();
    }

    public static void registerCommands() {
        Iterator iterator = getAllCommands().iterator();

        while (iterator.hasNext()) {
            Command command = (Command) iterator.next();

            Cosmos.INSTANCE.getCommandDispatcher().register(command.getCommand());
            Cosmos.INSTANCE.getCommandDispatcher().register(Command.redirectBuilder(command.getName(), command.getCommand().build()));
        }

    }

    public static List getAllCommands() {
        return CommandManager.commands;
    }

    public static List getCommands(Predicate predicate) {
        return (List) CommandManager.commands.stream().filter(predicate).collect(Collectors.toList());
    }

    public static Command getCommand(Predicate predicate) {
        return (Command) CommandManager.commands.stream().filter(predicate).findFirst().orElse((Object) null);
    }
}
