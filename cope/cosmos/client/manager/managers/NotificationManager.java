package cope.cosmos.client.manager.managers;

import cope.cosmos.client.manager.Manager;
import cope.cosmos.util.system.Timer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.util.ResourceLocation;

public class NotificationManager extends Manager {

    private final List notifications = new ArrayList();

    public NotificationManager() {
        super("NotificationManager", "Handles sending client notifications", 6);
    }

    public void pushNotification(NotificationManager.Notification notification) {
        boolean unique = true;
        Iterator iterator = this.notifications.iterator();

        while (iterator.hasNext()) {
            NotificationManager.Notification uniqueNotification = (NotificationManager.Notification) iterator.next();

            if (uniqueNotification.getMessage().equals(notification.getMessage())) {
                unique = false;
                break;
            }
        }

        if (unique) {
            this.notifications.add(notification);
            notification.getAnimation().setState(true);
            notification.getTimer().reset();
        }

    }

    public List getNotifications() {
        return this.notifications;
    }

    public static class Notification {

        private final String message;
        private final NotificationManager.Type type;
        private final AnimationManager animation;
        private final Timer timer = new Timer();

        public Notification(String message, NotificationManager.Type type) {
            this.message = message;
            this.type = type;
            this.animation = new AnimationManager(200, false);
        }

        public String getMessage() {
            return this.message;
        }

        public NotificationManager.Type getType() {
            return this.type;
        }

        public AnimationManager getAnimation() {
            return this.animation;
        }

        public Timer getTimer() {
            return this.timer;
        }
    }

    public static enum Type {

        INFO(new ResourceLocation("cosmos", "textures/icons/info.png")), SAFETY(new ResourceLocation("cosmos", "textures/icons/warning.png")), WARNING(new ResourceLocation("cosmos", "textures/icons/warning.png"));

        private final ResourceLocation resourceLocation;

        private Type(ResourceLocation resourceLocation) {
            this.resourceLocation = resourceLocation;
        }

        public ResourceLocation getResourceLocation() {
            return this.resourceLocation;
        }
    }
}
