package com.mojang.brigadier.suggestion;

import com.mojang.brigadier.context.StringRange;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class Suggestions {

    private static final Suggestions EMPTY = new Suggestions(StringRange.at(0), new ArrayList());
    private final StringRange range;
    private final List suggestions;

    public Suggestions(StringRange range, List suggestions) {
        this.range = range;
        this.suggestions = suggestions;
    }

    public StringRange getRange() {
        return this.range;
    }

    public List getList() {
        return this.suggestions;
    }

    public boolean isEmpty() {
        return this.suggestions.isEmpty();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof Suggestions)) {
            return false;
        } else {
            Suggestions that = (Suggestions) o;

            return Objects.equals(this.range, that.range) && Objects.equals(this.suggestions, that.suggestions);
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[] { this.range, this.suggestions});
    }

    public String toString() {
        return "Suggestions{range=" + this.range + ", suggestions=" + this.suggestions + '}';
    }

    public static CompletableFuture empty() {
        return CompletableFuture.completedFuture(Suggestions.EMPTY);
    }

    public static Suggestions merge(String command, Collection input) {
        if (input.isEmpty()) {
            return Suggestions.EMPTY;
        } else if (input.size() == 1) {
            return (Suggestions) input.iterator().next();
        } else {
            HashSet texts = new HashSet();
            Iterator iterator = input.iterator();

            while (iterator.hasNext()) {
                Suggestions suggestions = (Suggestions) iterator.next();

                texts.addAll(suggestions.getList());
            }

            return create(command, texts);
        }
    }

    public static Suggestions create(String command, Collection suggestions) {
        if (suggestions.isEmpty()) {
            return Suggestions.EMPTY;
        } else {
            int start = Integer.MAX_VALUE;
            int end = Integer.MIN_VALUE;

            Suggestion texts;

            for (Iterator range = suggestions.iterator(); range.hasNext(); end = Math.max(texts.getRange().getEnd(), end)) {
                texts = (Suggestion) range.next();
                start = Math.min(texts.getRange().getStart(), start);
            }

            StringRange range1 = new StringRange(start, end);
            HashSet texts1 = new HashSet();
            Iterator sorted = suggestions.iterator();

            while (sorted.hasNext()) {
                Suggestion suggestion = (Suggestion) sorted.next();

                texts1.add(suggestion.expand(command, range1));
            }

            ArrayList sorted1 = new ArrayList(texts1);

            sorted1.sort((a, b) -> {
                return a.compareToIgnoreCase(b);
            });
            return new Suggestions(range1, sorted1);
        }
    }
}
