package cope.cosmos.client.features.modules;

import cope.cosmos.client.Cosmos;
import cope.cosmos.client.events.ModuleToggleEvent;
import cope.cosmos.client.features.Feature;
import cope.cosmos.client.features.setting.Setting;
import cope.cosmos.client.manager.managers.AnimationManager;
import cope.cosmos.util.Wrapper;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraftforge.common.MinecraftForge;

public class Module extends Feature implements Wrapper {

    private boolean enabled;
    private boolean drawn;
    private boolean exempt;
    private int key;
    private Supplier info;
    private final Category category;
    private final List settings = new ArrayList();
    private final AnimationManager animation;

    public Module(String name, Category category, String description) {
        super(name, description);
        this.category = category;
        this.setFields();
        this.drawn = true;
        this.exempt = false;
        this.key = 0;
        this.animation = new AnimationManager(150, this.enabled);
    }

    public Module(String name, Category category, String description, Supplier info) {
        super(name, description);
        this.category = category;
        this.info = info;
        this.setFields();
        this.drawn = true;
        this.exempt = false;
        this.key = 0;
        this.animation = new AnimationManager(150, this.enabled);
    }

    private void setFields() {
        Arrays.stream(this.getClass().getDeclaredFields()).filter((field) -> {
            return Setting.class.isAssignableFrom(field.getType());
        }).forEach((field) -> {
            field.setAccessible(true);

            try {
                this.settings.add(((Setting) field.get(this)).setModule(this));
            } catch (IllegalAccessException | IllegalArgumentException illegalargumentexception) {
                illegalargumentexception.printStackTrace();
            }

        });
    }

    public void toggle() {
        if (this.enabled) {
            this.disable();
        } else {
            this.enable();
        }

    }

    public void enable() {
        if (!this.enabled) {
            this.enabled = true;
            MinecraftForge.EVENT_BUS.register(this);
            if (Module.mc.player != null && Module.mc.world != null || Cosmos.INSTANCE.getNullSafeMods().contains(this)) {
                ModuleToggleEvent.ModuleEnableEvent event = new ModuleToggleEvent.ModuleEnableEvent(this);

                MinecraftForge.EVENT_BUS.post(event);

                try {
                    this.onEnable();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }

    }

    public void disable() {
        if (this.enabled) {
            this.enabled = false;
            if (Module.mc.player != null && Module.mc.world != null || Cosmos.INSTANCE.getNullSafeMods().contains(this)) {
                ModuleToggleEvent.ModuleDisableEvent event = new ModuleToggleEvent.ModuleDisableEvent(this);

                MinecraftForge.EVENT_BUS.post(event);

                try {
                    this.onDisable();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }

            MinecraftForge.EVENT_BUS.unregister(this);
        }

    }

    public void onEnable() {
        this.animation.setState(true);
        Cosmos.INSTANCE.getTickManager().setClientTicks(1.0D);
    }

    public void onDisable() {
        this.animation.setState(false);
        Cosmos.INSTANCE.getTickManager().setClientTicks(1.0D);
    }

    public void onUpdate() {}

    public void onThread() {}

    public void onRender2d() {}

    public void onRender3d() {}

    public String getName() {
        return this.name;
    }

    public Category getCategory() {
        return this.category;
    }

    public String getDescription() {
        return this.description;
    }

    public String getInfo() {
        return this.info != null ? (String) this.info.get() : "";
    }

    public List getSettings() {
        return this.settings;
    }

    public int getKey() {
        return this.key;
    }

    public void setKey(int in) {
        this.key = in;
    }

    public AnimationManager getAnimation() {
        return this.animation;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setDrawn(boolean in) {
        this.drawn = in;
    }

    public boolean isDrawn() {
        return this.drawn;
    }

    public void setExempt(boolean in) {
        this.exempt = in;
    }

    public boolean isExempt() {
        return this.exempt;
    }
}
