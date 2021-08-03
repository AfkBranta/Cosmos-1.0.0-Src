package com.moandjiezana.toml;

import java.util.ArrayList;

class Keys {

    static Keys.Key[] split(String key) {
        ArrayList splitKey = new ArrayList();
        StringBuilder current = new StringBuilder();
        boolean quoted = false;
        boolean indexable = true;
        boolean inIndex = false;
        int index = -1;

        for (int i = key.length() - 1; i > -1; --i) {
            char c = key.charAt(i);

            if (c == 93 && indexable) {
                inIndex = true;
            } else {
                indexable = false;
                if (c == 91 && inIndex) {
                    inIndex = false;
                    index = Integer.parseInt(current.toString());
                    current = new StringBuilder();
                } else {
                    if (isQuote(c) && (i == 0 || key.charAt(i - 1) != 92)) {
                        quoted = !quoted;
                        indexable = false;
                    }

                    if (c == 46 && !quoted) {
                        splitKey.add(0, new Keys.Key(current.toString(), index, !splitKey.isEmpty() ? (Keys.Key) splitKey.get(0) : null));
                        indexable = true;
                        index = -1;
                        current = new StringBuilder();
                    } else {
                        current.insert(0, c);
                    }
                }
            }
        }

        splitKey.add(0, new Keys.Key(current.toString(), index, !splitKey.isEmpty() ? (Keys.Key) splitKey.get(0) : null));
        return (Keys.Key[]) splitKey.toArray(new Keys.Key[0]);
    }

    static boolean isQuote(char c) {
        return c == 34 || c == 39;
    }

    static class Key {

        final String name;
        final int index;
        final String path;

        Key(String name, int index, Keys.Key next) {
            this.name = name;
            this.index = index;
            if (next != null) {
                this.path = name + "." + next.path;
            } else {
                this.path = name;
            }

        }
    }
}
