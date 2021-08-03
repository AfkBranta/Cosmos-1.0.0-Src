package cope.cosmos.util.combat;

import cope.cosmos.util.Wrapper;
import java.util.Iterator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class EnemyUtil implements Wrapper {

    public static float getHealth(Entity entity) {
        return entity instanceof EntityPlayer ? ((EntityPlayer) entity).getHealth() + ((EntityPlayer) entity).getAbsorptionAmount() : ((EntityLivingBase) entity).getHealth();
    }

    public static float getArmor(Entity target) {
        if (!(target instanceof EntityPlayer)) {
            return 0.0F;
        } else {
            float armorDurability = 0.0F;
            Iterator iterator = target.getArmorInventoryList().iterator();

            while (iterator.hasNext()) {
                ItemStack stack = (ItemStack) iterator.next();

                if (stack != null && stack.getItem() != Items.AIR) {
                    armorDurability += (float) (stack.getMaxDamage() - stack.getItemDamage()) / (float) stack.getMaxDamage() * 100.0F;
                }
            }

            return armorDurability;
        }
    }

    public static boolean getArmor(Entity target, double durability) {
        if (!(target instanceof EntityPlayer)) {
            return false;
        } else {
            Iterator iterator = target.getArmorInventoryList().iterator();

            ItemStack stack;

            do {
                if (!iterator.hasNext()) {
                    return false;
                }

                stack = (ItemStack) iterator.next();
                if (stack == null || stack.getItem() == Items.AIR) {
                    return true;
                }
            } while (durability < (double) ((float) (stack.getMaxDamage() - stack.getItemDamage()) / (float) stack.getMaxDamage() * 100.0F));

            return true;
        }
    }

    public static boolean isDead(Entity entity) {
        return getHealth(entity) <= 0.0F || entity.isDead;
    }
}
