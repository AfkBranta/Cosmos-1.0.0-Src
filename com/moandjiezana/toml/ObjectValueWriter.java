package com.moandjiezana.toml;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

class ObjectValueWriter implements ValueWriter {

    static final ValueWriter OBJECT_VALUE_WRITER = new ObjectValueWriter();

    public boolean canWrite(Object value) {
        return true;
    }

    public void write(Object value, WriterContext context) {
        LinkedHashMap to = new LinkedHashMap();
        Set fields = getFields(value.getClass());
        Iterator iterator = fields.iterator();

        while (iterator.hasNext()) {
            Field field = (Field) iterator.next();

            to.put(field.getName(), getFieldValue(field, value));
        }

        MapValueWriter.MAP_VALUE_WRITER.write(to, context);
    }

    public boolean isPrimitiveType() {
        return false;
    }

    private static Set getFields(Class cls) {
        LinkedHashSet fields;

        for (fields = new LinkedHashSet(Arrays.asList(cls.getDeclaredFields())); cls != Object.class; cls = cls.getSuperclass()) {
            fields.addAll(Arrays.asList(cls.getDeclaredFields()));
        }

        removeConstantsAndSyntheticFields(fields);
        return fields;
    }

    private static void removeConstantsAndSyntheticFields(Set fields) {
        Iterator iterator = fields.iterator();

        while (iterator.hasNext()) {
            Field field = (Field) iterator.next();

            if (Modifier.isFinal(field.getModifiers()) && Modifier.isStatic(field.getModifiers()) || field.isSynthetic() || Modifier.isTransient(field.getModifiers())) {
                iterator.remove();
            }
        }

    }

    private static Object getFieldValue(Field field, Object o) {
        boolean isAccessible = field.isAccessible();

        field.setAccessible(true);
        Object value = null;

        try {
            value = field.get(o);
        } catch (IllegalAccessException illegalaccessexception) {
            ;
        }

        field.setAccessible(isAccessible);
        return value;
    }
}
