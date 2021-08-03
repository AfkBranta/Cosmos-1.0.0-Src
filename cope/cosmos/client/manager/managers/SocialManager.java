package cope.cosmos.client.manager.managers;

import cope.cosmos.client.manager.Manager;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SocialManager extends Manager {

    private final Map socials = new ConcurrentHashMap();

    public SocialManager() {
        super("SocialManager", "Manages the client\'s social system", 13);
    }

    public void initialize(Manager manager) {
        new SocialManager();
    }

    public void addSocial(String socialName, SocialManager.Relationship social) {
        this.socials.put(socialName, social);
    }

    public void removeSocial(String socialName) {
        this.socials.remove(socialName);
    }

    public Map getSocials() {
        return this.socials;
    }

    public SocialManager.Relationship getSocial(String socialName) {
        return (SocialManager.Relationship) this.socials.getOrDefault(socialName, SocialManager.Relationship.NONE);
    }

    public static enum Relationship {

        FRIEND, ENEMY, NONE;
    }
}
