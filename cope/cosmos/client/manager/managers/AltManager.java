package cope.cosmos.client.manager.managers;

import com.mojang.authlib.Agent;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import cope.cosmos.client.manager.Manager;
import cope.cosmos.loader.asm.mixins.accessor.IMinecraft;
import cope.cosmos.util.Wrapper;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.Session;

public class AltManager extends Manager implements Wrapper {

    private static final List alts = new ArrayList();

    public AltManager() {
        super("AltManager", "Manages alternate accounts for easy login", 0);
    }

    public static YggdrasilUserAuthentication logIn(String email, String password, boolean setSession) {
        YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication) (new YggdrasilAuthenticationService(Proxy.NO_PROXY, "")).createUserAuthentication(Agent.MINECRAFT);

        auth.setUsername(email);
        auth.setPassword(password);
        (new Thread(() -> {
            try {
                auth.logIn();
                if (setSession) {
                    Session exception = new Session(auth.getSelectedProfile().getName(), auth.getSelectedProfile().getId().toString(), auth.getAuthenticatedToken(), "mojang");

                    ((IMinecraft) AltManager.mc).setSession(exception);
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }

        })).start();
        return auth;
    }

    public static List getAlts() {
        return AltManager.alts;
    }
}
