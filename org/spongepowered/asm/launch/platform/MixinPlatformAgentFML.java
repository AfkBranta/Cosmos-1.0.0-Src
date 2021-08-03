package org.spongepowered.asm.launch.platform;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.launch.GlobalProperties;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.extensibility.IRemapper;

public class MixinPlatformAgentFML extends MixinPlatformAgentAbstract {

    private static final String LOAD_CORE_MOD_METHOD = "loadCoreMod";
    private static final String GET_REPARSEABLE_COREMODS_METHOD = "getReparseableCoremods";
    private static final String CORE_MOD_MANAGER_CLASS = "net.minecraftforge.fml.relauncher.CoreModManager";
    private static final String CORE_MOD_MANAGER_CLASS_LEGACY = "cpw.mods.fml.relauncher.CoreModManager";
    private static final String GET_IGNORED_MODS_METHOD = "getIgnoredMods";
    private static final String GET_IGNORED_MODS_METHOD_LEGACY = "getLoadedCoremods";
    private static final String FML_REMAPPER_ADAPTER_CLASS = "org.spongepowered.asm.bridge.RemapperAdapterFML";
    private static final String FML_CMDLINE_COREMODS = "fml.coreMods.load";
    private static final String FML_PLUGIN_WRAPPER_CLASS = "FMLPluginWrapper";
    private static final String FML_CORE_MOD_INSTANCE_FIELD = "coreModInstance";
    private static final String MFATT_FORCELOADASMOD = "ForceLoadAsMod";
    private static final String MFATT_FMLCOREPLUGIN = "FMLCorePlugin";
    private static final String MFATT_COREMODCONTAINSMOD = "FMLCorePluginContainsFMLMod";
    private static final String FML_TWEAKER_DEOBF = "FMLDeobfTweaker";
    private static final String FML_TWEAKER_INJECTION = "FMLInjectionAndSortingTweaker";
    private static final String FML_TWEAKER_TERMINAL = "TerminalTweaker";
    private static final Set loadedCoreMods = new HashSet();
    private final ITweaker coreModWrapper;
    private final String fileName;
    private Class clCoreModManager;
    private boolean initInjectionState;

    public MixinPlatformAgentFML(MixinPlatformManager manager, URI uri) {
        super(manager, uri);
        this.fileName = this.container.getName();
        this.coreModWrapper = this.initFMLCoreMod();
    }

    private ITweaker initFMLCoreMod() {
        try {
            try {
                this.clCoreModManager = getCoreModManagerClass();
            } catch (ClassNotFoundException classnotfoundexception) {
                MixinPlatformAgentAbstract.logger.info("FML platform manager could not load class {}. Proceeding without FML support.", new Object[] { classnotfoundexception.getMessage()});
                return null;
            }

            if ("true".equalsIgnoreCase(this.attributes.get("ForceLoadAsMod"))) {
                MixinPlatformAgentAbstract.logger.debug("ForceLoadAsMod was specified for {}, attempting force-load", new Object[] { this.fileName});
                this.loadAsMod();
            }

            return this.injectCorePlugin();
        } catch (Exception exception) {
            MixinPlatformAgentAbstract.logger.catching(exception);
            return null;
        }
    }

    private void loadAsMod() {
        try {
            getIgnoredMods(this.clCoreModManager).remove(this.fileName);
        } catch (Exception exception) {
            MixinPlatformAgentAbstract.logger.catching(exception);
        }

        if (this.attributes.get("FMLCorePluginContainsFMLMod") != null) {
            if (this.isIgnoredReparseable()) {
                MixinPlatformAgentAbstract.logger.debug("Ignoring request to add {} to reparseable coremod collection - it is a deobfuscated dependency", new Object[] { this.fileName});
                return;
            }

            this.addReparseableJar();
        }

    }

    private boolean isIgnoredReparseable() {
        return this.container.toString().contains("deobfedDeps");
    }

    private void addReparseableJar() {
        try {
            Method ex = this.clCoreModManager.getDeclaredMethod(GlobalProperties.getString("mixin.launch.fml.reparseablecoremodsmethod", "getReparseableCoremods"), new Class[0]);
            List reparsedCoremods = (List) ex.invoke((Object) null, new Object[0]);

            if (!reparsedCoremods.contains(this.fileName)) {
                MixinPlatformAgentAbstract.logger.debug("Adding {} to reparseable coremod collection", new Object[] { this.fileName});
                reparsedCoremods.add(this.fileName);
            }
        } catch (Exception exception) {
            MixinPlatformAgentAbstract.logger.catching(exception);
        }

    }

    private ITweaker injectCorePlugin() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        String coreModName = this.attributes.get("FMLCorePlugin");

        if (coreModName == null) {
            return null;
        } else if (this.isAlreadyInjected(coreModName)) {
            MixinPlatformAgentAbstract.logger.debug("{} has core plugin {}. Skipping because it was already injected.", new Object[] { this.fileName, coreModName});
            return null;
        } else {
            MixinPlatformAgentAbstract.logger.debug("{} has core plugin {}. Injecting it into FML for co-initialisation:", new Object[] { this.fileName, coreModName});
            Method mdLoadCoreMod = this.clCoreModManager.getDeclaredMethod(GlobalProperties.getString("mixin.launch.fml.loadcoremodmethod", "loadCoreMod"), new Class[] { LaunchClassLoader.class, String.class, File.class});

            mdLoadCoreMod.setAccessible(true);
            ITweaker wrapper = (ITweaker) mdLoadCoreMod.invoke((Object) null, new Object[] { Launch.classLoader, coreModName, this.container});

            if (wrapper == null) {
                MixinPlatformAgentAbstract.logger.debug("Core plugin {} could not be loaded.", new Object[] { coreModName});
                return null;
            } else {
                this.initInjectionState = isTweakerQueued("FMLInjectionAndSortingTweaker");
                MixinPlatformAgentFML.loadedCoreMods.add(coreModName);
                return wrapper;
            }
        }
    }

    private boolean isAlreadyInjected(String coreModName) {
        if (MixinPlatformAgentFML.loadedCoreMods.contains(coreModName)) {
            return true;
        } else {
            try {
                List tweakers = (List) GlobalProperties.get("Tweaks");

                if (tweakers == null) {
                    return false;
                }

                Iterator iterator = tweakers.iterator();

                while (iterator.hasNext()) {
                    ITweaker tweaker = (ITweaker) iterator.next();
                    Class tweakClass = tweaker.getClass();

                    if ("FMLPluginWrapper".equals(tweakClass.getSimpleName())) {
                        Field fdCoreModInstance = tweakClass.getField("coreModInstance");

                        fdCoreModInstance.setAccessible(true);
                        Object coreMod = fdCoreModInstance.get(tweaker);

                        if (coreModName.equals(coreMod.getClass().getName())) {
                            return true;
                        }
                    }
                }
            } catch (Exception exception) {
                ;
            }

            return false;
        }
    }

    public String getPhaseProvider() {
        return MixinPlatformAgentFML.class.getName() + "$PhaseProvider";
    }

    public void prepare() {
        this.initInjectionState |= isTweakerQueued("FMLInjectionAndSortingTweaker");
    }

    public void initPrimaryContainer() {
        if (this.clCoreModManager != null) {
            this.injectRemapper();
        }

    }

    private void injectRemapper() {
        try {
            MixinPlatformAgentAbstract.logger.debug("Creating FML remapper adapter: {}", new Object[] { "org.spongepowered.asm.bridge.RemapperAdapterFML"});
            Class ex = Class.forName("org.spongepowered.asm.bridge.RemapperAdapterFML", true, Launch.classLoader);
            Method mdCreate = ex.getDeclaredMethod("create", new Class[0]);
            IRemapper remapper = (IRemapper) mdCreate.invoke((Object) null, new Object[0]);

            MixinEnvironment.getDefaultEnvironment().getRemappers().add(remapper);
        } catch (Exception exception) {
            MixinPlatformAgentAbstract.logger.debug("Failed instancing FML remapper adapter, things will probably go horribly for notch-obf\'d mods!");
        }

    }

    public void inject() {
        if (this.coreModWrapper != null && this.checkForCoInitialisation()) {
            MixinPlatformAgentAbstract.logger.debug("FML agent is co-initiralising coremod instance {} for {}", new Object[] { this.coreModWrapper, this.uri});
            this.coreModWrapper.injectIntoClassLoader(Launch.classLoader);
        }

    }

    public String getLaunchTarget() {
        return null;
    }

    protected final boolean checkForCoInitialisation() {
        boolean injectionTweaker = isTweakerQueued("FMLInjectionAndSortingTweaker");
        boolean terminalTweaker = isTweakerQueued("TerminalTweaker");

        if ((!this.initInjectionState || !terminalTweaker) && !injectionTweaker) {
            return !isTweakerQueued("FMLDeobfTweaker");
        } else {
            MixinPlatformAgentAbstract.logger.debug("FML agent is skipping co-init for {} because FML will inject it normally", new Object[] { this.coreModWrapper});
            return false;
        }
    }

    private static boolean isTweakerQueued(String tweakerName) {
        Iterator iterator = ((List) GlobalProperties.get("TweakClasses")).iterator();

        String tweaker;

        do {
            if (!iterator.hasNext()) {
                return false;
            }

            tweaker = (String) iterator.next();
        } while (!tweaker.endsWith(tweakerName));

        return true;
    }

    private static Class getCoreModManagerClass() throws ClassNotFoundException {
        try {
            return Class.forName(GlobalProperties.getString("mixin.launch.fml.coremodmanagerclass", "net.minecraftforge.fml.relauncher.CoreModManager"));
        } catch (ClassNotFoundException classnotfoundexception) {
            return Class.forName("cpw.mods.fml.relauncher.CoreModManager");
        }
    }

    private static List getIgnoredMods(Class clCoreModManager) throws IllegalAccessException, InvocationTargetException {
        Method mdGetIgnoredMods = null;

        try {
            mdGetIgnoredMods = clCoreModManager.getDeclaredMethod(GlobalProperties.getString("mixin.launch.fml.ignoredmodsmethod", "getIgnoredMods"), new Class[0]);
        } catch (NoSuchMethodException nosuchmethodexception) {
            try {
                mdGetIgnoredMods = clCoreModManager.getDeclaredMethod("getLoadedCoremods", new Class[0]);
            } catch (NoSuchMethodException nosuchmethodexception1) {
                MixinPlatformAgentAbstract.logger.catching(Level.DEBUG, nosuchmethodexception1);
                return Collections.emptyList();
            }
        }

        return (List) mdGetIgnoredMods.invoke((Object) null, new Object[0]);
    }

    static {
        String[] astring = System.getProperty("fml.coreMods.load", "").split(",");
        int i = astring.length;

        for (int j = 0; j < i; ++j) {
            String cmdLineCoreMod = astring[j];

            if (!cmdLineCoreMod.isEmpty()) {
                MixinPlatformAgentAbstract.logger.debug("FML platform agent will ignore coremod {} specified on the command line", new Object[] { cmdLineCoreMod});
                MixinPlatformAgentFML.loadedCoreMods.add(cmdLineCoreMod);
            }
        }

    }
}
