package cope.cosmos.client.features.setting;

import cope.cosmos.client.features.Feature;
import cope.cosmos.client.features.modules.Module;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;

public class Setting extends Feature {

    private Object min;
    private Object value;
    private Object max;
    private int scale;
    private int index;
    private Module module;
    private Supplier visible;
    private Setting parentSetting;
    private final List subSettings = new ArrayList();

    public Setting(String name, String description, Object value) {
        super(name, description);
        this.value = value;
    }

    public Setting(Supplier visible, String name, String description, Object value) {
        super(name, description);
        this.visible = visible;
        this.value = value;
    }

    public Setting(String name, String description, Object min, Object value, Object max, int scale) {
        super(name, description);
        this.min = min;
        this.value = value;
        this.max = max;
        this.scale = scale;
    }

    public Setting(Supplier visible, String name, String description, Object min, Object value, Object max, int scale) {
        super(name, description);
        this.visible = visible;
        this.min = min;
        this.value = value;
        this.max = max;
        this.scale = scale;
    }

    public Object getMin() {
        return this.min;
    }

    public Object getValue() {
        return this.value;
    }

    public void setValue(Object in) {
        this.value = in;
    }

    public Object getMax() {
        return this.max;
    }

    public int getRoundingScale() {
        return this.scale;
    }

    public Module getModule() {
        return this.module;
    }

    public Setting setModule(Module in) {
        this.module = in;
        return this;
    }

    public boolean isVisible() {
        return this.visible != null ? ((Boolean) this.visible.get()).booleanValue() : true;
    }

    public boolean hasParent() {
        return this.parentSetting != null;
    }

    public Setting getParentSetting() {
        return this.parentSetting;
    }

    public void setParentSetting(Setting in) {
        this.parentSetting = in;
    }

    public Setting setParent(Setting in) {
        in.getSubSettings().add(this);
        this.parentSetting = in;
        return this;
    }

    public List getSubSettings() {
        return this.subSettings;
    }

    public Object getNextMode() {
        if (this.value instanceof Enum) {
            Enum enumVal = (Enum) this.value;
            String[] values = (String[]) Arrays.stream(enumVal.getClass().getEnumConstants()).map(Enum::name).toArray((x$0) -> {
                return new String[x$0];
            });

            this.index = this.index + 1 > values.length - 1 ? 0 : this.index + 1;
            return Enum.valueOf(enumVal.getClass(), values[this.index]);
        } else {
            return null;
        }
    }

    public Object getPreviousMode() {
        if (this.value instanceof Enum) {
            Enum enumVal = (Enum) this.value;
            String[] values = (String[]) Arrays.stream(enumVal.getClass().getEnumConstants()).map(Enum::name).toArray((x$0) -> {
                return new String[x$0];
            });

            this.index = this.index - 1 < 0 ? values.length - 1 : this.index - 1;
            return Enum.valueOf(enumVal.getClass(), values[this.index]);
        } else {
            return null;
        }
    }

    public static String formatEnum(Enum enumIn) {
        String enumName = enumIn.name();

        if (!enumName.contains("_")) {
            char c0 = enumName.charAt(0);
            String s = enumName.split(String.valueOf(c0), 2)[1];

            return String.valueOf(c0).toUpperCase() + s.toLowerCase();
        } else {
            String[] names = enumName.split("_");
            StringBuilder nameToReturn = new StringBuilder();
            String[] astring = names;
            int i = names.length;

            for (int j = 0; j < i; ++j) {
                String s = astring[j];
                char firstChar = s.charAt(0);
                String suffixChars = s.split(String.valueOf(firstChar), 2)[1];

                nameToReturn.append(String.valueOf(firstChar).toUpperCase()).append(suffixChars.toLowerCase());
            }

            return nameToReturn.toString();
        }
    }
}
