package com.mojang.brigadier;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.SuggestionContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CommandDispatcher {

    public static final String ARGUMENT_SEPARATOR = " ";
    public static final char ARGUMENT_SEPARATOR_CHAR = ' ';
    private static final String USAGE_OPTIONAL_OPEN = "[";
    private static final String USAGE_OPTIONAL_CLOSE = "]";
    private static final String USAGE_REQUIRED_OPEN = "(";
    private static final String USAGE_REQUIRED_CLOSE = ")";
    private static final String USAGE_OR = "|";
    private final RootCommandNode root;
    private final Predicate hasCommand;
    private ResultConsumer consumer;

    public CommandDispatcher(RootCommandNode root) {
        this.hasCommand = new Predicate() {
            public boolean test(CommandNode input) {
                return input != null && (input.getCommand() != null || input.getChildren().stream().anyMatch(CommandDispatcher.this.hasCommand));
            }
        };
        this.consumer = onCommandComplete<invokedynamic>();
        this.root = root;
    }

    public CommandDispatcher() {
        this(new RootCommandNode());
    }

    public LiteralCommandNode register(LiteralArgumentBuilder command) {
        LiteralCommandNode build = command.build();

        this.root.addChild(build);
        return build;
    }

    public void setConsumer(ResultConsumer consumer) {
        this.consumer = consumer;
    }

    public int execute(String input, Object source) throws CommandSyntaxException {
        return this.execute(new StringReader(input), source);
    }

    public int execute(StringReader input, Object source) throws CommandSyntaxException {
        ParseResults parse = this.parse(input, source);

        return this.execute(parse);
    }

    public int execute(ParseResults parse) throws CommandSyntaxException {
        if (parse.getReader().canRead()) {
            if (parse.getExceptions().size() == 1) {
                throw (CommandSyntaxException) parse.getExceptions().values().iterator().next();
            } else if (parse.getContext().getRange().isEmpty()) {
                throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().createWithContext(parse.getReader());
            } else {
                throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().createWithContext(parse.getReader());
            }
        } else {
            int result = 0;
            int successfulForks = 0;
            boolean forked = false;
            boolean foundCommand = false;
            String command = parse.getReader().getString();
            CommandContext original = parse.getContext().build(command);
            Object contexts = Collections.singletonList(original);

            for (ArrayList next = null; contexts != null; next = null) {
                int size = ((List) contexts).size();

                for (int i = 0; i < size; ++i) {
                    CommandContext context = (CommandContext) ((List) contexts).get(i);
                    CommandContext child = context.getChild();

                    if (child != null) {
                        forked |= context.isForked();
                        if (child.hasNodes()) {
                            foundCommand = true;
                            RedirectModifier ex = context.getRedirectModifier();

                            if (ex == null) {
                                if (next == null) {
                                    next = new ArrayList(1);
                                }

                                next.add(child.copyFor(context.getSource()));
                            } else {
                                try {
                                    Collection ex1 = ex.apply(context);

                                    if (!ex1.isEmpty()) {
                                        if (next == null) {
                                            next = new ArrayList(ex1.size());
                                        }

                                        Iterator iterator = ex1.iterator();

                                        while (iterator.hasNext()) {
                                            Object source = iterator.next();

                                            next.add(child.copyFor(source));
                                        }
                                    }
                                } catch (CommandSyntaxException commandsyntaxexception) {
                                    this.consumer.onCommandComplete(context, false, 0);
                                    if (!forked) {
                                        throw commandsyntaxexception;
                                    }
                                }
                            }
                        }
                    } else if (context.getCommand() != null) {
                        foundCommand = true;

                        try {
                            int i = context.getCommand().run(context);

                            result += i;
                            this.consumer.onCommandComplete(context, true, i);
                            ++successfulForks;
                        } catch (CommandSyntaxException commandsyntaxexception1) {
                            this.consumer.onCommandComplete(context, false, 0);
                            if (!forked) {
                                throw commandsyntaxexception1;
                            }
                        }
                    }
                }

                contexts = next;
            }

            if (!foundCommand) {
                this.consumer.onCommandComplete(original, false, 0);
                throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().createWithContext(parse.getReader());
            } else {
                return forked ? successfulForks : result;
            }
        }
    }

    public ParseResults parse(String command, Object source) {
        return this.parse(new StringReader(command), source);
    }

    public ParseResults parse(StringReader command, Object source) {
        CommandContextBuilder context = new CommandContextBuilder(this, source, this.root, command.getCursor());

        return this.parseNodes(this.root, command, context);
    }

    private ParseResults parseNodes(CommandNode node, StringReader originalReader, CommandContextBuilder contextSoFar) {
        Object source = contextSoFar.getSource();
        LinkedHashMap errors = null;
        ArrayList potentials = null;
        int cursor = originalReader.getCursor();
        Iterator iterator = node.getRelevantNodes(originalReader).iterator();

        while (iterator.hasNext()) {
            CommandNode child = (CommandNode) iterator.next();

            if (child.canUse(source)) {
                CommandContextBuilder context = contextSoFar.copy();
                StringReader reader = new StringReader(originalReader);

                try {
                    try {
                        child.parse(reader, context);
                    } catch (RuntimeException runtimeexception) {
                        throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherParseException().createWithContext(reader, runtimeexception.getMessage());
                    }

                    if (reader.canRead() && reader.peek() != 32) {
                        throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherExpectedArgumentSeparator().createWithContext(reader);
                    }
                } catch (CommandSyntaxException commandsyntaxexception) {
                    if (errors == null) {
                        errors = new LinkedHashMap();
                    }

                    errors.put(child, commandsyntaxexception);
                    reader.setCursor(cursor);
                    continue;
                }

                context.withCommand(child.getCommand());
                if (reader.canRead(child.getRedirect() == null ? 2 : 1)) {
                    reader.skip();
                    if (child.getRedirect() != null) {
                        CommandContextBuilder parse2 = new CommandContextBuilder(this, source, child.getRedirect(), reader.getCursor());
                        ParseResults parse1 = this.parseNodes(child.getRedirect(), reader, parse2);

                        context.withChild(parse1.getContext());
                        return new ParseResults(context, parse1.getReader(), parse1.getExceptions());
                    }

                    ParseResults parse = this.parseNodes(child, reader, context);

                    if (potentials == null) {
                        potentials = new ArrayList(1);
                    }

                    potentials.add(parse);
                } else {
                    if (potentials == null) {
                        potentials = new ArrayList(1);
                    }

                    potentials.add(new ParseResults(context, reader, Collections.emptyMap()));
                }
            }
        }

        if (potentials != null) {
            if (potentials.size() > 1) {
                potentials.sort(compare<invokedynamic>());
            }

            return (ParseResults) potentials.get(0);
        } else {
            return new ParseResults(contextSoFar, originalReader, (Map) (errors == null ? Collections.emptyMap() : errors));
        }
    }

    public String[] getAllUsage(CommandNode node, Object source, boolean restricted) {
        ArrayList result = new ArrayList();

        this.getAllUsage(node, source, result, "", restricted);
        return (String[]) result.toArray(new String[result.size()]);
    }

    private void getAllUsage(CommandNode node, Object source, ArrayList result, String prefix, boolean restricted) {
        if (!restricted || node.canUse(source)) {
            if (node.getCommand() != null) {
                result.add(prefix);
            }

            if (node.getRedirect() != null) {
                String redirect = node.getRedirect() == this.root ? "..." : "-> " + node.getRedirect().getUsageText();

                result.add(prefix.isEmpty() ? node.getUsageText() + " " + redirect : prefix + " " + redirect);
            } else if (!node.getChildren().isEmpty()) {
                Iterator redirect1 = node.getChildren().iterator();

                while (redirect1.hasNext()) {
                    CommandNode child = (CommandNode) redirect1.next();

                    this.getAllUsage(child, source, result, prefix.isEmpty() ? child.getUsageText() : prefix + " " + child.getUsageText(), restricted);
                }
            }

        }
    }

    public Map getSmartUsage(CommandNode node, Object source) {
        LinkedHashMap result = new LinkedHashMap();
        boolean optional = node.getCommand() != null;
        Iterator iterator = node.getChildren().iterator();

        while (iterator.hasNext()) {
            CommandNode child = (CommandNode) iterator.next();
            String usage = this.getSmartUsage(child, source, optional, false);

            if (usage != null) {
                result.put(child, usage);
            }
        }

        return result;
    }

    private String getSmartUsage(CommandNode node, Object source, boolean optional, boolean deep) {
        if (!node.canUse(source)) {
            return null;
        } else {
            String self = optional ? "[" + node.getUsageText() + "]" : node.getUsageText();
            boolean childOptional = node.getCommand() != null;
            String open = childOptional ? "[" : "(";
            String close = childOptional ? "]" : ")";

            if (!deep) {
                if (node.getRedirect() != null) {
                    String s = node.getRedirect() == this.root ? "..." : "-> " + node.getRedirect().getUsageText();

                    return self + " " + s;
                }

                Collection children = (Collection) node.getChildren().stream().filter(test<invokedynamic>(source)).collect(Collectors.toList());

                if (children.size() == 1) {
                    String childUsage = this.getSmartUsage((CommandNode) children.iterator().next(), source, childOptional, childOptional);

                    if (childUsage != null) {
                        return self + " " + childUsage;
                    }
                } else if (children.size() > 1) {
                    LinkedHashSet linkedhashset = new LinkedHashSet();
                    Iterator builder = children.iterator();

                    while (builder.hasNext()) {
                        CommandNode count = (CommandNode) builder.next();
                        String usage = this.getSmartUsage(count, source, childOptional, true);

                        if (usage != null) {
                            linkedhashset.add(usage);
                        }
                    }

                    if (linkedhashset.size() == 1) {
                        String s1 = (String) linkedhashset.iterator().next();

                        return self + " " + (childOptional ? "[" + s1 + "]" : s1);
                    }

                    if (linkedhashset.size() > 1) {
                        StringBuilder stringbuilder = new StringBuilder(open);
                        int i = 0;

                        for (Iterator iterator = children.iterator(); iterator.hasNext(); ++i) {
                            CommandNode child = (CommandNode) iterator.next();

                            if (i > 0) {
                                stringbuilder.append("|");
                            }

                            stringbuilder.append(child.getUsageText());
                        }

                        if (i > 0) {
                            stringbuilder.append(close);
                            return self + " " + stringbuilder.toString();
                        }
                    }
                }
            }

            return self;
        }
    }

    public CompletableFuture getCompletionSuggestions(ParseResults parse) {
        return this.getCompletionSuggestions(parse, parse.getReader().getTotalLength());
    }

    public CompletableFuture getCompletionSuggestions(ParseResults parse, int cursor) {
        CommandContextBuilder context = parse.getContext();
        SuggestionContext nodeBeforeCursor = context.findSuggestionContext(cursor);
        CommandNode parent = nodeBeforeCursor.parent;
        int start = Math.min(nodeBeforeCursor.startPos, cursor);
        String fullInput = parse.getReader().getString();
        String truncatedInput = fullInput.substring(0, cursor);
        CompletableFuture[] futures = new CompletableFuture[parent.getChildren().size()];
        int i = 0;

        CompletableFuture future;

        for (Iterator result = parent.getChildren().iterator(); result.hasNext(); futures[i++] = future) {
            CommandNode node = (CommandNode) result.next();

            future = Suggestions.empty();

            try {
                future = node.listSuggestions(context.build(truncatedInput), new SuggestionsBuilder(truncatedInput, start));
            } catch (CommandSyntaxException commandsyntaxexception) {
                ;
            }
        }

        CompletableFuture completablefuture = new CompletableFuture();

        CompletableFuture.allOf(futures).thenRun(run<invokedynamic>(futures, completablefuture, fullInput));
        return completablefuture;
    }

    public RootCommandNode getRoot() {
        return this.root;
    }

    public Collection getPath(CommandNode target) {
        ArrayList nodes = new ArrayList();

        this.addPaths(this.root, nodes, new ArrayList());
        Iterator iterator = nodes.iterator();

        List list;

        do {
            if (!iterator.hasNext()) {
                return Collections.emptyList();
            }

            list = (List) iterator.next();
        } while (list.get(list.size() - 1) != target);

        ArrayList result = new ArrayList(list.size());
        Iterator iterator1 = list.iterator();

        while (iterator1.hasNext()) {
            CommandNode node = (CommandNode) iterator1.next();

            if (node != this.root) {
                result.add(node.getName());
            }
        }

        return result;
    }

    public CommandNode findNode(Collection path) {
        Object node = this.root;
        Iterator iterator = path.iterator();

        do {
            if (!iterator.hasNext()) {
                return (CommandNode) node;
            }

            String name = (String) iterator.next();

            node = ((CommandNode) node).getChild(name);
        } while (node != null);

        return null;
    }

    public void findAmbiguities(AmbiguityConsumer consumer) {
        this.root.findAmbiguities(consumer);
    }

    private void addPaths(CommandNode node, List result, List parents) {
        ArrayList current = new ArrayList(parents);

        current.add(node);
        result.add(current);
        Iterator iterator = node.getChildren().iterator();

        while (iterator.hasNext()) {
            CommandNode child = (CommandNode) iterator.next();

            this.addPaths(child, result, current);
        }

    }

    private static void lambda$getCompletionSuggestions$3(CompletableFuture[] futures, CompletableFuture result, String fullInput) {
        ArrayList suggestions = new ArrayList();
        CompletableFuture[] acompletablefuture = futures;
        int i = futures.length;

        for (int j = 0; j < i; ++j) {
            CompletableFuture future = acompletablefuture[j];

            suggestions.add(future.join());
        }

        result.complete(Suggestions.merge(fullInput, suggestions));
    }

    private static boolean lambda$getSmartUsage$2(Object source, CommandNode c) {
        return c.canUse(source);
    }

    private static int lambda$parseNodes$1(ParseResults a, ParseResults b) {
        return !a.getReader().canRead() && b.getReader().canRead() ? -1 : (a.getReader().canRead() && !b.getReader().canRead() ? 1 : (a.getExceptions().isEmpty() && !b.getExceptions().isEmpty() ? -1 : (!a.getExceptions().isEmpty() && b.getExceptions().isEmpty() ? 1 : 0)));
    }

    private static void lambda$new$0(CommandContext c, boolean s, int r) {}
}
