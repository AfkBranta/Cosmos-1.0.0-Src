package cope.cosmos.client.manager.managers;

import com.moandjiezana.toml.Toml;
import cope.cosmos.client.Cosmos;
import cope.cosmos.client.alts.AltEntry;
import cope.cosmos.client.clickgui.cosmos.window.Window;
import cope.cosmos.client.clickgui.cosmos.window.WindowManager;
import cope.cosmos.client.clickgui.cosmos.window.windows.CategoryWindow;
import cope.cosmos.client.features.modules.Category;
import cope.cosmos.client.features.modules.Module;
import cope.cosmos.client.features.setting.Setting;
import cope.cosmos.client.manager.Manager;
import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import net.minecraft.util.math.Vec2f;

public class PresetManager extends Manager {

    private String currentPreset;
    private final List presets = new ArrayList();
    private final File mainDirectory = new File("cosmos");

    public PresetManager() {
        super("PresetManager", "Handles the client\'s configs - saving, loading, etc.", 8);
        this.presets.add("default");
        this.currentPreset = "default";
        this.load();
        this.save();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Cosmos.INSTANCE.getPresetManager().save();
        }));
    }

    public void initialize(Manager manager) {
        new PresetManager();
    }

    public void setPreset(String name) {
        Iterator iterator = this.presets.iterator();

        while (iterator.hasNext()) {
            String preset = (String) iterator.next();

            if (preset.equals(name)) {
                this.currentPreset = preset;
                break;
            }
        }

    }

    public void createPreset(String name) {
        this.presets.add(name);
    }

    public void removePreset(String name) {
        Iterator iterator = this.presets.iterator();

        while (iterator.hasNext()) {
            String preset = (String) iterator.next();

            if (preset.equals(name)) {
                this.presets.remove(preset);
                break;
            }
        }

    }

    public void load() {
        this.writeDirectories();
        this.loadInfo();
        this.loadModules();
        this.loadSocial();
        this.loadGUI();
        this.loadAlts();
    }

    public void save() {
        this.saveInfo();
        this.saveModules();
        this.saveSocial();
        this.saveGUI();
        this.saveAlts();
    }

    public void writeDirectories() {
        if (!this.mainDirectory.exists()) {
            this.mainDirectory.mkdirs();
        }

        Iterator iterator = this.presets.iterator();

        while (iterator.hasNext()) {
            String preset = (String) iterator.next();
            File presetDirectory = new File("cosmos/" + preset);

            if (!presetDirectory.exists()) {
                presetDirectory.mkdirs();
            }
        }

    }

    public void saveModules() {
        try {
            OutputStreamWriter exception = new OutputStreamWriter(new FileOutputStream(this.mainDirectory.getName() + "/" + this.currentPreset + "/modules.toml"), StandardCharsets.UTF_8);
            StringBuilder outputTOML = new StringBuilder();
            Iterator iterator = ModuleManager.getAllModules().iterator();

            while (iterator.hasNext()) {
                Module module = (Module) iterator.next();

                outputTOML.append("[").append(module.getName()).append("]").append("\r\n");
                outputTOML.append("Enabled = ").append(module.isEnabled()).append("\r\n");
                outputTOML.append("Drawn = ").append(module.isDrawn()).append("\r\n");
                outputTOML.append("Bind = ").append((double) module.getKey()).append("\r\n");
                Iterator iterator1 = module.getSettings().iterator();

                while (iterator1.hasNext()) {
                    Setting setting = (Setting) iterator1.next();

                    if (setting != null && !(setting.getValue() instanceof Category)) {
                        if (setting.getValue() instanceof Boolean) {
                            outputTOML.append(setting.hasParent() ? setting.getParentSetting().getName() + "-" + setting.getName() : setting.getName()).append(" = ").append(setting.getValue()).append("\r\n");
                        } else if (setting.getValue() instanceof Integer) {
                            outputTOML.append(setting.hasParent() ? setting.getParentSetting().getName() + "-" + setting.getName() : setting.getName()).append(" = ").append(setting.getValue()).append("\r\n");
                        } else if (setting.getValue() instanceof Double) {
                            outputTOML.append(setting.hasParent() ? setting.getParentSetting().getName() + "-" + setting.getName() : setting.getName()).append(" = ").append(setting.getValue()).append("\r\n");
                        } else if (setting.getValue() instanceof Float) {
                            outputTOML.append(setting.hasParent() ? setting.getParentSetting().getName() + "-" + setting.getName() : setting.getName()).append(" = ").append(setting.getValue()).append("\r\n");
                        } else if (setting.getValue() instanceof Enum) {
                            outputTOML.append(setting.hasParent() ? setting.getParentSetting().getName() + "-" + setting.getName() : setting.getName()).append(" = ").append('\"').append(setting.getValue().toString()).append('\"').append("\r\n");
                        } else if (setting.getValue() instanceof Color) {
                            outputTOML.append(setting.hasParent() ? setting.getParentSetting().getName() + "-" + setting.getName() : setting.getName()).append(" = ").append(((Color) setting.getValue()).getRGB()).append("\r\n");
                        }
                    }
                }

                outputTOML.append("\r\n");
            }

            exception.write(outputTOML.toString());
            exception.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }

    public void loadModules() {
        try {
            InputStream exception = Files.newInputStream(Paths.get(this.mainDirectory.getName() + "/" + this.currentPreset + "/modules.toml", new String[0]), new OpenOption[0]);
            Toml inputTOML = (new Toml()).read(exception);

            if (inputTOML != null) {
                Iterator iterator = ModuleManager.getAllModules().iterator();

                while (iterator.hasNext()) {
                    Module module = (Module) iterator.next();

                    if (inputTOML.getBoolean(module.getName() + ".Enabled") != null) {
                        if (inputTOML.getBoolean(module.getName() + ".Enabled").booleanValue()) {
                            module.enable();
                            module.getAnimation().setState(true);
                        }

                        module.setDrawn(inputTOML.getBoolean(module.getName() + ".Drawn").booleanValue());
                        module.setKey((int) inputTOML.getDouble(module.getName() + ".Bind").doubleValue());
                        Iterator iterator1 = module.getSettings().iterator();

                        while (iterator1.hasNext()) {
                            Setting setting = (Setting) iterator1.next();

                            if (setting != null && !(setting.getValue() instanceof Category)) {
                                if (setting.getValue() instanceof Boolean && inputTOML.getBoolean(module.getName() + "." + (setting.hasParent() ? setting.getParentSetting().getName() + "-" + setting.getName() : setting.getName())) != null) {
                                    setting.setValue(inputTOML.getBoolean(module.getName() + "." + (setting.hasParent() ? setting.getParentSetting().getName() + "-" + setting.getName() : setting.getName())));
                                } else if (setting.getValue() instanceof Integer && inputTOML.getDouble(module.getName() + "." + (setting.hasParent() ? setting.getParentSetting().getName() + "-" + setting.getName() : setting.getName())) != null) {
                                    setting.setValue(Integer.valueOf((int) inputTOML.getDouble(module.getName() + "." + (setting.hasParent() ? setting.getParentSetting().getName() + "-" + setting.getName() : setting.getName())).doubleValue()));
                                } else if (setting.getValue() instanceof Double && inputTOML.getDouble(module.getName() + "." + (setting.hasParent() ? setting.getParentSetting().getName() + "-" + setting.getName() : setting.getName())) != null) {
                                    setting.setValue(inputTOML.getDouble(module.getName() + "." + (setting.hasParent() ? setting.getParentSetting().getName() + "-" + setting.getName() : setting.getName())));
                                } else if (setting.getValue() instanceof Float && inputTOML.getDouble(module.getName() + "." + (setting.hasParent() ? setting.getParentSetting().getName() + "-" + setting.getName() : setting.getName())) != null) {
                                    setting.setValue(Float.valueOf((float) inputTOML.getDouble(module.getName() + "." + (setting.hasParent() ? setting.getParentSetting().getName() + "-" + setting.getName() : setting.getName())).doubleValue()));
                                } else if (setting.getValue() instanceof Enum && Enum.valueOf(((Enum) setting.getValue()).getClass(), inputTOML.getString(module.getName() + "." + (setting.hasParent() ? setting.getParentSetting().getName() + "-" + setting.getName() : setting.getName()))) != null) {
                                    setting.setValue(Enum.valueOf(((Enum) setting.getValue()).getClass(), inputTOML.getString(module.getName() + "." + (setting.hasParent() ? setting.getParentSetting().getName() + "-" + setting.getName() : setting.getName()))));
                                } else if (setting.getValue() instanceof Color && inputTOML.getLong(module.getName() + "." + (setting.hasParent() ? setting.getParentSetting().getName() + "-" + setting.getName() : setting.getName())) != null) {
                                    int colorValue = (int) inputTOML.getLong(module.getName() + "." + (setting.hasParent() ? setting.getParentSetting().getName() + "-" + setting.getName() : setting.getName())).longValue();

                                    setting.setValue(new Color(colorValue, true));
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }

    public void saveSocial() {
        try {
            OutputStreamWriter exception = new OutputStreamWriter(new FileOutputStream(this.mainDirectory.getName() + "/social.toml"), StandardCharsets.UTF_8);
            StringBuilder outputTOML = new StringBuilder();

            outputTOML.append("[Social]").append("\r\n");
            outputTOML.append("Friends").append(" = ").append("[");
            Iterator iterator = Cosmos.INSTANCE.getSocialManager().getSocials().entrySet().iterator();

            while (iterator.hasNext()) {
                Entry social = (Entry) iterator.next();

                outputTOML.append('\"').append((String) social.getKey()).append('\"').append(", ");
            }

            outputTOML.append("]").append("\r\n");
            exception.write(outputTOML.toString());
            exception.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }

    public void loadSocial() {
        try {
            InputStream exception = Files.newInputStream(Paths.get(this.mainDirectory.getName() + "/social.toml", new String[0]), new OpenOption[0]);
            Toml inputTOML = (new Toml()).read(exception);

            if (inputTOML != null) {
                Iterator friendsList = inputTOML.getList("Social.Friends").iterator();

                while (friendsList.hasNext()) {
                    String friend = (String) friendsList.next();

                    Cosmos.INSTANCE.getSocialManager().addSocial(friend, SocialManager.Relationship.FRIEND);
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }

    public void saveInfo() {
        try {
            OutputStreamWriter exception = new OutputStreamWriter(new FileOutputStream(this.mainDirectory.getName() + "/info.toml"), StandardCharsets.UTF_8);
            StringBuilder outputTOML = new StringBuilder();

            outputTOML.append("[Info]").append("\r\n");
            outputTOML.append("Setup = ").append(Cosmos.SETUP).append("\r\n");
            outputTOML.append("Prefix = ").append('\"').append(Cosmos.PREFIX).append('\"').append("\r\n");
            outputTOML.append("Preset = ").append('\"').append(this.currentPreset).append('\"').append("\r\n");
            exception.write(outputTOML.toString());
            exception.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }

    public void loadInfo() {
        try {
            InputStream exception = Files.newInputStream(Paths.get(this.mainDirectory.getName() + "/info.toml", new String[0]), new OpenOption[0]);
            Toml inputTOML = (new Toml()).read(exception);

            Cosmos.SETUP = inputTOML.getBoolean("Info.Setup").booleanValue();
            Cosmos.PREFIX = inputTOML.getString("Info.Prefix");
            this.currentPreset = inputTOML.getString("Info.Preset");
        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }

    public void saveAlts() {
        try {
            OutputStreamWriter exception = new OutputStreamWriter(new FileOutputStream(this.mainDirectory.getName() + "/alts.toml"), StandardCharsets.UTF_8);
            StringBuilder outputTOML = new StringBuilder();
            int altList = 0;

            for (Iterator iterator = AltManager.getAlts().iterator(); iterator.hasNext(); ++altList) {
                AltEntry altEntry = (AltEntry) iterator.next();

                outputTOML.append("[").append(altList).append("]").append("\r\n");
                outputTOML.append("Name = ").append('\"').append(altEntry.getName()).append('\"').append("\r\n");
                outputTOML.append("Email = ").append('\"').append(altEntry.getEmail()).append('\"').append("\r\n");
                outputTOML.append("Password = ").append('\"').append(altEntry.getPassword()).append('\"').append("\r\n");
                outputTOML.append("\r\n");
            }

            exception.write(outputTOML.toString());
            exception.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }

    public void loadAlts() {
        try {
            InputStream exception = Files.newInputStream(Paths.get(this.mainDirectory.getName() + "/alts.toml", new String[0]), new OpenOption[0]);
            Toml inputTOML = (new Toml()).read(exception);

            for (int i = 0; i < 20; ++i) {
                if (inputTOML.getString(i + ".Email") != null) {
                    AltManager.getAlts().add(new AltEntry(inputTOML.getString(i + ".Email"), inputTOML.getString(i + ".Password")));
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }

    public void saveGUI() {
        try {
            OutputStreamWriter exception = new OutputStreamWriter(new FileOutputStream(this.mainDirectory.getName() + "/gui.toml"), StandardCharsets.UTF_8);
            StringBuilder outputTOML = new StringBuilder();
            Iterator iterator = WindowManager.getWindows().iterator();

            while (iterator.hasNext()) {
                Window window = (Window) iterator.next();
                CategoryWindow categoryWindow = (CategoryWindow) ((CategoryWindow) window);

                outputTOML.append("[").append(Setting.formatEnum(categoryWindow.getCategory())).append("]").append("\r\n");
                outputTOML.append("X = ").append(categoryWindow.getPosition().x).append("\r\n");
                outputTOML.append("Y = ").append(categoryWindow.getPosition().y).append("\r\n");
                outputTOML.append("Height = ").append(categoryWindow.getHeight()).append("\r\n");
                outputTOML.append("\r\n");
            }

            exception.write(outputTOML.toString());
            exception.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }

    public void loadGUI() {
        try {
            InputStream exception = Files.newInputStream(Paths.get(this.mainDirectory.getName() + "/gui.toml", new String[0]), new OpenOption[0]);
            Toml inputTOML = (new Toml()).read(exception);
            Iterator iterator = WindowManager.getWindows().iterator();

            while (iterator.hasNext()) {
                Window window = (Window) iterator.next();
                CategoryWindow categoryWindow = (CategoryWindow) ((CategoryWindow) window);

                categoryWindow.setPosition(new Vec2f((float) inputTOML.getDouble(Setting.formatEnum(categoryWindow.getCategory()) + ".X").doubleValue(), (float) inputTOML.getDouble(Setting.formatEnum(categoryWindow.getCategory()) + ".Y").doubleValue()));
                categoryWindow.setHeight((float) inputTOML.getDouble(Setting.formatEnum(categoryWindow.getCategory()) + ".Height").doubleValue());
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }

    public String getCurrentPreset() {
        return this.currentPreset;
    }
}
