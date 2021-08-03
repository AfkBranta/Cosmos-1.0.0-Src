package cope.cosmos.util.combat;

import cope.cosmos.client.Cosmos;
import cope.cosmos.client.features.modules.client.Social;
import cope.cosmos.client.manager.managers.SocialManager;
import cope.cosmos.util.Wrapper;
import cope.cosmos.util.world.EntityUtil;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class TargetUtil implements Wrapper {

    public static EntityPlayer getClosestPlayer(double range) {
        return (new ArrayList(TargetUtil.mc.world.playerEntities)).stream().noneMatch(test<invokedynamic>()) ? null : (EntityPlayer) (new ArrayList(TargetUtil.mc.world.playerEntities)).stream().filter(test<invokedynamic>()).filter(test<invokedynamic>()).filter(test<invokedynamic>(range)).filter(test<invokedynamic>()).min(Comparator.comparing(apply<invokedynamic>())).orElse((Object) null);
    }

    public static EntityPlayer getTargetPlayer(double range, TargetUtil.Target target) {
        EntityPlayer targetPlayer = null;

        if ((new ArrayList(TargetUtil.mc.world.playerEntities)).stream().filter(test<invokedynamic>()).filter(test<invokedynamic>()).filter(test<invokedynamic>(range)).allMatch(test<invokedynamic>())) {
            return null;
        } else {
            switch (target) {
            case CLOSEST:
                targetPlayer = (EntityPlayer) (new ArrayList(TargetUtil.mc.world.playerEntities)).stream().filter(test<invokedynamic>()).filter(test<invokedynamic>()).filter(test<invokedynamic>(range)).filter(test<invokedynamic>()).findFirst().orElse((Object) null);
                break;

            case LOWESTHEALTH:
                targetPlayer = (EntityPlayer) (new ArrayList(TargetUtil.mc.world.playerEntities)).stream().filter(test<invokedynamic>()).filter(test<invokedynamic>()).filter(test<invokedynamic>(range)).filter(test<invokedynamic>()).min(Comparator.comparing(apply<invokedynamic>())).orElse((Object) null);
                break;

            case LOWESTARMOR:
                targetPlayer = (EntityPlayer) (new ArrayList(TargetUtil.mc.world.playerEntities)).stream().filter(test<invokedynamic>()).filter(test<invokedynamic>()).filter(test<invokedynamic>(range)).filter(test<invokedynamic>()).min(Comparator.comparing(apply<invokedynamic>())).orElse((Object) null);
            }

            return targetPlayer;
        }
    }

    public static Entity getTargetEntity(double range, TargetUtil.Target target, boolean players, boolean passive, boolean neutral, boolean hostile) {
        EntityPlayer targetPlayer = null;

        if ((new ArrayList(TargetUtil.mc.world.loadedEntityList)).stream().noneMatch(test<invokedynamic>())) {
            return null;
        } else {
            switch (target) {
            case CLOSEST:
                targetPlayer = (EntityPlayer) (new ArrayList(TargetUtil.mc.world.playerEntities)).stream().filter(test<invokedynamic>()).filter(test<invokedynamic>()).filter(test<invokedynamic>(range)).filter(test<invokedynamic>()).min(Comparator.comparing(apply<invokedynamic>())).orElse((Object) null);
                break;

            case LOWESTHEALTH:
                targetPlayer = (EntityPlayer) (new ArrayList(TargetUtil.mc.world.playerEntities)).stream().filter(test<invokedynamic>()).filter(test<invokedynamic>()).filter(test<invokedynamic>(range)).filter(test<invokedynamic>()).min(Comparator.comparing(apply<invokedynamic>())).orElse((Object) null);
                break;

            case LOWESTARMOR:
                targetPlayer = (EntityPlayer) (new ArrayList(TargetUtil.mc.world.playerEntities)).stream().filter(test<invokedynamic>()).filter(test<invokedynamic>()).filter(test<invokedynamic>(range)).filter(test<invokedynamic>()).min(Comparator.comparing(apply<invokedynamic>())).orElse((Object) null);
            }

            if (players && targetPlayer != null) {
                return targetPlayer;
            } else {
                ArrayList verifiedEntities = new ArrayList();
                Iterator iterator = ((List) (new ArrayList(TargetUtil.mc.world.loadedEntityList)).stream().filter(test<invokedynamic>()).collect(Collectors.toList())).iterator();

                while (iterator.hasNext()) {
                    Entity entity = (Entity) iterator.next();

                    if (hostile && EntityUtil.isHostileMob(entity)) {
                        verifiedEntities.add(entity);
                    } else if (neutral && EntityUtil.isNeutralMob(entity)) {
                        verifiedEntities.add(entity);
                    } else if (passive && EntityUtil.isPassiveMob(entity)) {
                        verifiedEntities.add(entity);
                    }
                }

                return (Entity) verifiedEntities.stream().min(Comparator.comparing(apply<invokedynamic>())).orElse((Object) null);
            }
        }
    }

    private static Float lambda$getTargetEntity$36(Entity entity) {
        return Float.valueOf(TargetUtil.mc.player.getDistance(entity));
    }

    private static boolean lambda$getTargetEntity$35(Entity entity) {
        return entity != TargetUtil.mc.player;
    }

    private static boolean lambda$getTargetEntity$34(EntityPlayer entityPlayer) {
        return !EnemyUtil.isDead(entityPlayer);
    }

    private static boolean lambda$getTargetEntity$33(double range, EntityPlayer entityPlayer) {
        return (double) TargetUtil.mc.player.getDistance(entityPlayer) <= range;
    }

    private static boolean lambda$getTargetEntity$32(EntityPlayer entityPlayer) {
        return TargetUtil.mc.player != entityPlayer;
    }

    private static boolean lambda$getTargetEntity$31(EntityPlayer entityPlayer) {
        return !Cosmos.INSTANCE.getSocialManager().getSocial(entityPlayer.getName()).equals(SocialManager.Relationship.FRIEND) && ((Boolean) Social.friends.getValue()).booleanValue();
    }

    private static boolean lambda$getTargetEntity$30(EntityPlayer entityPlayer) {
        return !EnemyUtil.isDead(entityPlayer);
    }

    private static boolean lambda$getTargetEntity$29(double range, EntityPlayer entityPlayer) {
        return (double) TargetUtil.mc.player.getDistance(entityPlayer) <= range;
    }

    private static boolean lambda$getTargetEntity$28(EntityPlayer entityPlayer) {
        return TargetUtil.mc.player != entityPlayer;
    }

    private static boolean lambda$getTargetEntity$27(EntityPlayer entityPlayer) {
        return !Cosmos.INSTANCE.getSocialManager().getSocial(entityPlayer.getName()).equals(SocialManager.Relationship.FRIEND) && ((Boolean) Social.friends.getValue()).booleanValue();
    }

    private static Float lambda$getTargetEntity$26(EntityPlayer entityPlayer) {
        return Float.valueOf(TargetUtil.mc.player.getDistance(entityPlayer));
    }

    private static boolean lambda$getTargetEntity$25(EntityPlayer entityPlayer) {
        return !EnemyUtil.isDead(entityPlayer);
    }

    private static boolean lambda$getTargetEntity$24(double range, EntityPlayer entityPlayer) {
        return (double) TargetUtil.mc.player.getDistance(entityPlayer) <= range;
    }

    private static boolean lambda$getTargetEntity$23(EntityPlayer entityPlayer) {
        return TargetUtil.mc.player != entityPlayer;
    }

    private static boolean lambda$getTargetEntity$22(EntityPlayer entityPlayer) {
        return !Cosmos.INSTANCE.getSocialManager().getSocial(entityPlayer.getName()).equals(SocialManager.Relationship.FRIEND) && ((Boolean) Social.friends.getValue()).booleanValue();
    }

    private static boolean lambda$getTargetEntity$21(Entity entityPlayer) {
        return entityPlayer != TargetUtil.mc.player;
    }

    private static boolean lambda$getTargetPlayer$20(EntityPlayer entityPlayer) {
        return !EnemyUtil.isDead(entityPlayer);
    }

    private static boolean lambda$getTargetPlayer$19(double range, EntityPlayer entityPlayer) {
        return (double) TargetUtil.mc.player.getDistance(entityPlayer) <= range;
    }

    private static boolean lambda$getTargetPlayer$18(EntityPlayer entityPlayer) {
        return TargetUtil.mc.player != entityPlayer;
    }

    private static boolean lambda$getTargetPlayer$17(EntityPlayer entityPlayer) {
        return !Cosmos.INSTANCE.getSocialManager().getSocial(entityPlayer.getName()).equals(SocialManager.Relationship.FRIEND) && ((Boolean) Social.friends.getValue()).booleanValue();
    }

    private static boolean lambda$getTargetPlayer$16(EntityPlayer entityPlayer) {
        return !EnemyUtil.isDead(entityPlayer);
    }

    private static boolean lambda$getTargetPlayer$15(double range, EntityPlayer entityPlayer) {
        return (double) TargetUtil.mc.player.getDistance(entityPlayer) <= range;
    }

    private static boolean lambda$getTargetPlayer$14(EntityPlayer entityPlayer) {
        return TargetUtil.mc.player != entityPlayer;
    }

    private static boolean lambda$getTargetPlayer$13(EntityPlayer entityPlayer) {
        return !Cosmos.INSTANCE.getSocialManager().getSocial(entityPlayer.getName()).equals(SocialManager.Relationship.FRIEND) && ((Boolean) Social.friends.getValue()).booleanValue();
    }

    private static boolean lambda$getTargetPlayer$12(EntityPlayer entityPlayer) {
        return !EnemyUtil.isDead(entityPlayer);
    }

    private static boolean lambda$getTargetPlayer$11(double range, EntityPlayer entityPlayer) {
        return (double) TargetUtil.mc.player.getDistance(entityPlayer) <= range;
    }

    private static boolean lambda$getTargetPlayer$10(EntityPlayer entityPlayer) {
        return TargetUtil.mc.player != entityPlayer;
    }

    private static boolean lambda$getTargetPlayer$9(EntityPlayer entityPlayer) {
        return !Cosmos.INSTANCE.getSocialManager().getSocial(entityPlayer.getName()).equals(SocialManager.Relationship.FRIEND) && ((Boolean) Social.friends.getValue()).booleanValue();
    }

    private static boolean lambda$getTargetPlayer$8(double range, EntityPlayer entityPlayer) {
        return (double) TargetUtil.mc.player.getDistance(entityPlayer) <= range;
    }

    private static boolean lambda$getTargetPlayer$7(EntityPlayer entityPlayer) {
        return TargetUtil.mc.player != entityPlayer;
    }

    private static boolean lambda$getTargetPlayer$6(EntityPlayer entityPlayer) {
        return !Cosmos.INSTANCE.getSocialManager().getSocial(entityPlayer.getName()).equals(SocialManager.Relationship.FRIEND) && ((Boolean) Social.friends.getValue()).booleanValue();
    }

    private static Float lambda$getClosestPlayer$5(EntityPlayer entityPlayer) {
        return Float.valueOf(TargetUtil.mc.player.getDistance(entityPlayer));
    }

    private static boolean lambda$getClosestPlayer$4(EntityPlayer entityPlayer) {
        return !entityPlayer.isDead;
    }

    private static boolean lambda$getClosestPlayer$3(double range, EntityPlayer entityPlayer) {
        return (double) TargetUtil.mc.player.getDistance(entityPlayer) <= range;
    }

    private static boolean lambda$getClosestPlayer$2(EntityPlayer entityPlayer) {
        return TargetUtil.mc.player != entityPlayer;
    }

    private static boolean lambda$getClosestPlayer$1(EntityPlayer entityPlayer) {
        return !Cosmos.INSTANCE.getSocialManager().getSocial(entityPlayer.getName()).equals(SocialManager.Relationship.FRIEND) && ((Boolean) Social.friends.getValue()).booleanValue();
    }

    private static boolean lambda$getClosestPlayer$0(EntityPlayer entityPlayer) {
        return entityPlayer != TargetUtil.mc.player;
    }

    public static enum Target {

        CLOSEST, LOWESTHEALTH, LOWESTARMOR;
    }
}
